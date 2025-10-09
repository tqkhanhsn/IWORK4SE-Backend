package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iwork4se.controller.request.SavedApplicantListCreationRequest;
import vn.iwork4se.controller.response.SavedApplicantListResponse;
import vn.iwork4se.exception.ResourceNotFoundException;
import vn.iwork4se.model.Employer;
import vn.iwork4se.model.SavedApplicantList;
import vn.iwork4se.repository.EmployerRepository;
import vn.iwork4se.repository.SavedApplicantListRepository;
import vn.iwork4se.repository.SavedApplicantRepository;
import vn.iwork4se.service.SavedApplicantListService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SavedApplicantListServiceImpl implements SavedApplicantListService {

    private final SavedApplicantListRepository savedApplicantListRepository;
    private final SavedApplicantRepository savedApplicantRepository;
    private final EmployerRepository employerRepository;

    private static final String DEFAULT_LIST_NAME = "Ứng cử viên đã lưu";

    @Override
    @Transactional
    public SavedApplicantListResponse createList(SavedApplicantListCreationRequest request) {
        log.info("Creating saved applicant list for employer: {}", request.getEmployerId());

        // Check if list name already exists for this employer
        if (savedApplicantListRepository.existsByEmployerIdAndListName(request.getEmployerId(), request.getListName())) {
            throw new RuntimeException("List with name '" + request.getListName() + "' already exists");
        }

        Employer employer = employerRepository.findById(request.getEmployerId())
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

        SavedApplicantList list = SavedApplicantList.builder()
                .id("SAL" + UUID.randomUUID().toString())
                .employer(employer)
                .listName(request.getListName())
                .isDefault(false)
                .build();

        SavedApplicantList savedList = savedApplicantListRepository.save(list);
        log.info("Created saved applicant list: {}", savedList.getId());

        return convertToResponse(savedList);
    }

    @Override
    @Transactional
    public SavedApplicantListResponse createDefaultList(String employerId) {
        log.info("Creating default saved applicant list for employer: {}", employerId);

        // Check if default list already exists
        if (savedApplicantListRepository.findByEmployerIdAndIsDefaultTrue(employerId).isPresent()) {
            throw new RuntimeException("Default list already exists for this employer");
        }

        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

        SavedApplicantList defaultList = SavedApplicantList.builder()
                .id("SAL" + UUID.randomUUID().toString())
                .employer(employer)
                .listName(DEFAULT_LIST_NAME)
                .isDefault(true)
                .build();

        SavedApplicantList savedList = savedApplicantListRepository.save(defaultList);
        log.info("Created default saved applicant list: {}", savedList.getId());

        return convertToResponse(savedList);
    }

    @Override
    public List<SavedApplicantListResponse> getAllListsByEmployer(String employerId) {
        log.info("Getting all saved applicant lists for employer: {}", employerId);

        List<SavedApplicantList> lists = savedApplicantListRepository.findByEmployerIdOrderByCreatedDateDesc(employerId);

        return lists.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SavedApplicantListResponse getListById(String listId) {
        log.info("Getting saved applicant list by id: {}", listId);

        SavedApplicantList list = getListEntityById(listId);
        return convertToResponse(list);
    }

    @Override
    @Transactional
    public void deleteList(String listId) {
        log.info("Deleting saved applicant list: {}", listId);

        SavedApplicantList list = getListEntityById(listId);

        // Prevent deletion of default list
        if (list.getIsDefault()) {
            throw new RuntimeException("Cannot delete default list");
        }

        savedApplicantListRepository.delete(list);
        log.info("Deleted saved applicant list: {}", listId);
    }

    @Override
    public SavedApplicantList getListEntityById(String listId) {
        return savedApplicantListRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("Saved applicant list not found with id: " + listId));
    }

    private SavedApplicantListResponse convertToResponse(SavedApplicantList list) {
        long totalCount = savedApplicantRepository.countBySavedApplicantListId(list.getId());
        long contactedCount = savedApplicantRepository.countBySavedApplicantListIdAndIsContacted(list.getId(), true);
        long notContactedCount = savedApplicantRepository.countBySavedApplicantListIdAndIsContacted(list.getId(), false);

        return SavedApplicantListResponse.builder()
                .id(list.getId())
                .employerId(list.getEmployer().getId())
                .listName(list.getListName())
                .isDefault(list.getIsDefault())
                .createdDate(list.getCreatedDate())
                .applicantCount(totalCount)
                .contactedCount(contactedCount)
                .notContactedCount(notContactedCount)
                .build();
    }
}
