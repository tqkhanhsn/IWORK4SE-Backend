package vn.iwork4se.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iwork4se.model.Permission;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    @Query("SELECT p FROM Permission p " +
            "JOIN p.permissions rp " +
            "JOIN rp.role r " +
            "WHERE r.id = :roleId AND p.path = :path AND p.method = :method")
    List<Permission> findByRoleIdAndPathAndMethod(@Param("roleId") Long roleId,
                                                  @Param("path") String path,
                                                  @Param("method") String method);

    @Query("SELECT p FROM Permission p " +
            "JOIN p.permissions rp " +
            "JOIN rp.role r " +
            "WHERE r.id = :roleId")
    List<Permission> findByRoleId(@Param("roleId") Long roleId);
}
