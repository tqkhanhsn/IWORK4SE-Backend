package vn.iwork4se.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iwork4se.model.SavedApplicant;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedApplicantRepository extends JpaRepository<SavedApplicant, String> {

    // Find saved applicants by list
    Page<SavedApplicant> findBySavedApplicantListId(String listId, Pageable pageable);

    // Find saved applicants by list and contact status
    Page<SavedApplicant> findBySavedApplicantListIdAndIsContacted(String listId, Boolean isContacted, Pageable pageable);

    // Check if applicant is already saved in a list
    Optional<SavedApplicant> findBySavedApplicantListIdAndApplicantId(String listId, String applicantId);

    // Find all saved applicants by employer (across all lists)
    @Query("SELECT sa FROM SavedApplicant sa WHERE sa.savedApplicantList.employer.id = :employerId")
    Page<SavedApplicant> findByEmployerId(@Param("employerId") String employerId, Pageable pageable);

    // Count saved applicants in a list
    long countBySavedApplicantListId(String listId);

    // Count contacted applicants in a list
    long countBySavedApplicantListIdAndIsContacted(String listId, Boolean isContacted);

    // Find all lists containing a specific applicant for an employer
    @Query("SELECT sa FROM SavedApplicant sa WHERE sa.applicant.id = :applicantId AND sa.savedApplicantList.employer.id = :employerId")
    List<SavedApplicant> findByApplicantIdAndEmployerId(@Param("applicantId") String applicantId, @Param("employerId") String employerId);

    // Delete all saved applicants in a list
    void deleteBySavedApplicantListId(String listId);
}
