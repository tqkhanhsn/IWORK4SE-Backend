package vn.iwork4se.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iwork4se.model.Employer;

import java.util.List;
import java.util.Map;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, String> {
    @Query("SELECT e FROM Employer e WHERE e.userStatus = 'ACTIVE' AND (" +
            "lower(e.firstName) LIKE lower(CONCAT('%', :keyword, '%')) OR " +
            "lower(e.lastName) LIKE lower(CONCAT('%', :keyword, '%')) OR " +
            "lower(e.userName) LIKE lower(CONCAT('%', :keyword, '%')) OR " +
            "lower(e.email) LIKE lower(CONCAT('%', :keyword, '%')) OR " +
            "lower(e.phone) LIKE lower(CONCAT('%', :keyword, '%')) OR " +
            "lower(e.address) LIKE lower(CONCAT('%', :keyword, '%')) OR " +
            "lower(e.companyName) LIKE lower(CONCAT('%', :keyword, '%')) OR " +
            "lower(e.location) LIKE lower(CONCAT('%', :keyword, '%')) OR " +
            "lower(e.industry) LIKE lower(CONCAT('%', :keyword, '%')) OR " +
            "lower(e.description) LIKE lower(CONCAT('%', :keyword, '%')))")
    Page<Employer> searchByKeywords(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT e FROM Employer e WHERE e.userStatus = 'ACTIVE' AND lower(e.companyName) LIKE lower(CONCAT('%', :companyName, '%'))")
    Page<Employer> findByCompanyName(@Param("companyName") String companyName, Pageable pageable);

    @Query("SELECT e FROM Employer e WHERE e.userStatus = 'ACTIVE' AND lower(e.location) LIKE lower(CONCAT('%', :location, '%'))")
    Page<Employer> findByLocation(@Param("location") String location, Pageable pageable);

    @Query("SELECT e FROM Employer e WHERE e.userStatus = 'ACTIVE' AND lower(e.industry) LIKE lower(CONCAT('%', :industry, '%'))")
    Page<Employer> findByIndustry(@Param("industry") String industry, Pageable pageable);

    @Query("SELECT e FROM Employer e WHERE e.userStatus = 'ACTIVE' AND " +
            "lower(e.location) LIKE lower(CONCAT('%', :location, '%')) AND " +
            "lower(e.industry) LIKE lower(CONCAT('%', :industry, '%'))")
    Page<Employer> findByLocationAndIndustry(@Param("location") String location,
                                             @Param("industry") String industry,
                                             Pageable pageable);

    @Query("SELECT DISTINCT new map(" +
            "e.companyName as companyName, " +
            "e.industry as industry, " +
            "e.location as location, " +
            "e.logoUrl as logoUrl," +
            "e.description as description) " +
            "FROM Employer e " +
            "WHERE e.userStatus = 'ACTIVE' AND e.companyName IS NOT NULL " +
            "ORDER BY e.companyName ASC")
    List<Map<String, Object>> findDistinctCompanies();
}
