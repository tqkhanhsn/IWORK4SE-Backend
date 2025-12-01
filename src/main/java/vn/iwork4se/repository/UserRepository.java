package vn.iwork4se.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iwork4se.common.UserStatus;
import vn.iwork4se.common.UserType;
import vn.iwork4se.model.User;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
    User findByUserName(String userName);
    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);

    @Query("SELECT u FROM User u WHERE u.userType = :userType")
    List<User> findByUserType(@Param("userType") UserType userType);

    @Query("SELECT u FROM User u WHERE u.userType IN :userTypes")
    List<User> findByUserTypes(@Param("userTypes") List<UserType> userTypes);

    @Query("SELECT u FROM User u WHERE (u.userType = :userType1 OR u.userType = :userType2) AND (u.firstName LIKE %:keyword% OR u.lastName LIKE %:keyword% OR u.email LIKE %:keyword%)")
    List<User> findByUserTypesAndKeyword(@Param("userType1") UserType userType1, @Param("userType2") UserType userType2, @Param("keyword") String keyword);

    // Find banned users whose ban has expired (unbannedDate <= today)
    @Query("SELECT u FROM User u WHERE u.userStatus = :bannedStatus AND u.unbannedDate IS NOT NULL AND u.unbannedDate <= :today")
    List<User> findBannedUsersWithExpiredBan(@Param("today") LocalDate today, @Param("bannedStatus") UserStatus bannedStatus);
}

