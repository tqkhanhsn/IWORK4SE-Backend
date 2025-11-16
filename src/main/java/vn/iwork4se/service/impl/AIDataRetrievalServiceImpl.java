package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iwork4se.common.JobStatus;
import vn.iwork4se.common.JobType;
import vn.iwork4se.model.Employer;
import vn.iwork4se.model.JobPost;
import vn.iwork4se.repository.EmployerRepository;
import vn.iwork4se.repository.JobPostRepository;
import vn.iwork4se.service.AIDataRetrievalService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AIDataRetrievalServiceImpl implements AIDataRetrievalService {
    
    private final JobPostRepository jobPostRepository;
    private final EmployerRepository employerRepository;
    
    // Keywords to detect job-related queries
    private static final Pattern JOB_KEYWORDS = Pattern.compile(
        "(?i)(việc làm|job|công việc|tuyển dụng|ứng tuyển|tìm việc|việc|position|opening|vacancy)"
    );
    
    // Keywords to detect company/employer queries
    private static final Pattern COMPANY_KEYWORDS = Pattern.compile(
        "(?i)(công ty|company|employer|nhà tuyển dụng|doanh nghiệp|tổ chức|firm)"
    );
    
    // Keywords for skills/technologies
    private static final Pattern SKILL_KEYWORDS = Pattern.compile(
        "(?i)(java|python|javascript|react|angular|vue|spring|node|sql|mysql|postgresql|mongodb|docker|kubernetes|aws|azure|git)"
    );
    
    // Keywords for job types
    private static final Pattern JOB_TYPE_KEYWORDS = Pattern.compile(
        "(?i)(internship|fresher|junior|senior|manager|thực tập|mới ra trường|sinh viên)"
    );
    
    @Override
    public String retrieveRelevantData(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "";
        }
        List<String> dataSections = new ArrayList<>();
        
        // Check if query is about jobs
        boolean isJobQuery = JOB_KEYWORDS.matcher(userMessage).find() || 
                            SKILL_KEYWORDS.matcher(userMessage).find() ||
                            JOB_TYPE_KEYWORDS.matcher(userMessage).find();
        
        // Check if query is about companies
        boolean isCompanyQuery = COMPANY_KEYWORDS.matcher(userMessage).find();
        
        // Extract keywords from message
        String keyword = extractKeyword(userMessage);
        String jobType = extractJobType(userMessage);
        String location = extractLocation(userMessage);
        
        // Retrieve job posts if relevant
        if (isJobQuery) {
            String jobsData = retrieveJobPosts(keyword, jobType, location);
            if (!jobsData.isEmpty()) {
                dataSections.add("=== THÔNG TIN VIỆC LÀM TRONG HỆ THỐNG ===\n" + jobsData);
            }
        }
        
        // Retrieve employers if relevant
        if (isCompanyQuery || isJobQuery) {
            String employersData = retrieveEmployers(keyword);
            if (!employersData.isEmpty()) {
                dataSections.add("=== THÔNG TIN CÔNG TY TRONG HỆ THỐNG ===\n" + employersData);
            }
        }
        
        if (dataSections.isEmpty()) {
            return "";
        }
        
        return "\n\n" + String.join("\n\n", dataSections) + "\n\n";
    }
    
    private String retrieveJobPosts(String keyword, String jobType, String location) {
        try {
            Pageable pageable = PageRequest.of(0, 10); // Get top 10 most relevant jobs
            List<JobPost> jobs = new ArrayList<>();
            
            // Try to find active jobs matching criteria
            if (keyword != null && !keyword.isEmpty()) {
                // Search by keyword
                jobs = jobPostRepository.findByKeyword(keyword, pageable).getContent();
            } else if (jobType != null || location != null) {
                // Search by job type and/or location
                JobType type = parseJobType(jobType);
                JobStatus status = JobStatus.ACCEPTED;
                
                if (type != null && location != null) {
                    jobs = jobPostRepository.findByMultipleCriteria(
                        null, status, type, location, null, null, pageable
                    ).getContent();
                } else if (type != null) {
                    jobs = jobPostRepository.findByJobType(type, pageable).getContent();
                } else if (location != null) {
                    jobs = jobPostRepository.findByLocationContainingIgnoreCase(location, pageable).getContent();
                }
            } else {
                // Get active jobs
                jobs = jobPostRepository.findActiveJobPosts(JobStatus.ACCEPTED, LocalDate.now(), pageable).getContent();
            }
            
            if (jobs.isEmpty()) {
                return "";
            }
            
            // Format jobs data
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < Math.min(jobs.size(), 10); i++) {
                JobPost job = jobs.get(i);
                sb.append(String.format(
                    "%d. **%s**\n" +
                    "   - Vị trí: %s\n" +
                    "   - Công ty: %s\n" +
                    "   - Địa điểm: %s\n" +
                    "   - Loại: %s\n" +
                    "   - Kinh nghiệm: %s\n" +
                    "   - Lương: %s - %s triệu VNĐ\n" +
                    "   - Mô tả: %s\n" +
                    "   - ID: %s\n\n",
                    i + 1,
                    job.getTitle() != null ? job.getTitle() : "N/A",
                    job.getJobPosition() != null ? job.getJobPosition() : "N/A",
                    job.getEmployer() != null && job.getEmployer().getCompanyName() != null 
                        ? job.getEmployer().getCompanyName() : "N/A",
                    job.getLocation() != null ? job.getLocation() : "N/A",
                    job.getJobType() != null ? job.getJobType().name() : "N/A",
                    job.getExperience() != null ? job.getExperience() : "N/A",
                    job.getMinSalary() != null ? String.format("%.0f", job.getMinSalary()) : "0",
                    job.getMaxSalary() != null ? String.format("%.0f", job.getMaxSalary()) : "0",
                    job.getDescription() != null && job.getDescription().length() > 200 
                        ? job.getDescription().substring(0, 200) + "..." : 
                        (job.getDescription() != null ? job.getDescription() : "N/A"),
                    job.getId()
                ));
            }
            
            return sb.toString();
        } catch (Exception e) {
            log.error("[AI-DATA] Error retrieving job posts", e);
            return "";
        }
    }
    
    private String retrieveEmployers(String keyword) {
        try {
            Pageable pageable = PageRequest.of(0, 5); // Get top 5 employers
            List<Employer> employers = new ArrayList<>();
            
            if (keyword != null && !keyword.isEmpty()) {
                employers = employerRepository.searchByKeywords(keyword, pageable).getContent();
            } else {
                // Get some active employers
                employers = employerRepository.findAll(pageable).getContent();
            }
            
            if (employers.isEmpty()) {
                return "";
            }
            
            // Format employers data
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < Math.min(employers.size(), 5); i++) {
                Employer emp = employers.get(i);
                sb.append(String.format(
                    "%d. **%s**\n" +
                    "   - Địa điểm: %s\n" +
                    "   - Ngành: %s\n" +
                    "   - Mô tả: %s\n" +
                    "   - ID: %s\n\n",
                    i + 1,
                    emp.getCompanyName() != null ? emp.getCompanyName() : "N/A",
                    emp.getLocation() != null ? emp.getLocation() : "N/A",
                    emp.getIndustry() != null ? emp.getIndustry() : "N/A",
                    emp.getDescription() != null && emp.getDescription().length() > 150 
                        ? emp.getDescription().substring(0, 150) + "..." : 
                        (emp.getDescription() != null ? emp.getDescription() : "N/A"),
                    emp.getId()
                ));
            }
            
            return sb.toString();
        } catch (Exception e) {
            log.error("[AI-DATA] Error retrieving employers", e);
            return "";
        }
    }
    
    private String extractKeyword(String message) {
        // Extract main keywords (skills, technologies, job titles)
        String lower = message.toLowerCase();
        
        // Try to extract skill keywords
        for (String skill : new String[]{"java", "python", "javascript", "react", "angular", "vue", 
                                         "spring", "node", "sql", "mysql", "postgresql", "mongodb"}) {
            if (lower.contains(skill)) {
                return skill;
            }
        }
        
        // Extract job position keywords
        for (String pos : new String[]{"developer", "engineer", "programmer", "lập trình viên", 
                                       "kỹ sư", "nhân viên", "chuyên viên"}) {
            if (lower.contains(pos)) {
                return pos;
            }
        }
        
        // If no specific keyword, return null to search all
        return null;
    }
    
    private String extractJobType(String message) {
        String lower = message.toLowerCase();
        if (lower.contains("internship") || lower.contains("thực tập")) {
            return "INTERNSHIP";
        } else if (lower.contains("fresher") || lower.contains("mới ra trường") || lower.contains("sinh viên")) {
            return "FRESHER";
        } else if (lower.contains("junior")) {
            return "JUNIOR";
        } else if (lower.contains("senior")) {
            return "SENIOR";
        } else if (lower.contains("manager")) {
            return "MANAGER";
        }
        return null;
    }
    
    private String extractLocation(String message) {
        String lower = message.toLowerCase();
        if (lower.contains("hà nội") || lower.contains("hanoi")) {
            return "Hà Nội";
        } else if (lower.contains("hồ chí minh") || lower.contains("ho chi minh") || lower.contains("hcm")) {
            return "Hồ Chí Minh";
        } else if (lower.contains("đà nẵng") || lower.contains("da nang")) {
            return "Đà Nẵng";
        } else if (lower.contains("remote") || lower.contains("từ xa")) {
            return "Remote";
        }
        return null;
    }
    
    private JobType parseJobType(String jobTypeStr) {
        if (jobTypeStr == null) return null;
        try {
            return JobType.valueOf(jobTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

