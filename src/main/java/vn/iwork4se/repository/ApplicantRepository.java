package vn.iwork4se.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iwork4se.model.Applicant;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, String> {
}
