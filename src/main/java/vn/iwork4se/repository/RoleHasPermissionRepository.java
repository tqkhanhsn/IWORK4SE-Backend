package vn.iwork4se.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iwork4se.model.RoleHasPermission;

@Repository
public interface RoleHasPermissionRepository extends JpaRepository<RoleHasPermission, Long> {
}