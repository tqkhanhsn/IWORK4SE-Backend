package vn.iwork4se.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iwork4se.model.SavedApplicantList;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedApplicantListRepository extends JpaRepository<SavedApplicantList, String> {

    // Find all lists by employer
    List<SavedApplicantList> findByEmployerIdOrderByCreatedDateDesc(String employerId);

    // Find default list by employer
    Optional<SavedApplicantList> findByEmployerIdAndIsDefaultTrue(String employerId);

    // Find list by employer and name
    Optional<SavedApplicantList> findByEmployerIdAndListName(String employerId, String listName);

    // Check if list name exists for employer
    boolean existsByEmployerIdAndListName(String employerId, String listName);

    // Count lists by employer
    long countByEmployerId(String employerId);
}
