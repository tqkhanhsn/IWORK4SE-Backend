package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iwork4se.common.UserStatus;
import vn.iwork4se.model.User;
import vn.iwork4se.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserStatusScheduler {

    private final UserRepository userRepository;

    /**
     * Chạy mỗi ngày lúc 02:00 để tự động chuyển trạng thái các user đã hết hạn ban sang INACTIVE.
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void updateExpiredBannedUsers() {
        LocalDate today = LocalDate.now();
        List<User> bannedUsers = userRepository.findBannedUsersWithExpiredBan(today, vn.iwork4se.common.UserStatus.BANNED);

        if (bannedUsers.isEmpty()) {
            log.info("No banned users to unban today");
            return;
        }

        log.info("Found {} banned users to unban", bannedUsers.size());

        for (User user : bannedUsers) {
            user.setUserStatus(UserStatus.INACTIVE);
            user.setBannedDate(null);
            user.setUnbannedDate(null);
            log.info("Unbanned user: {} (ID: {})", user.getUsername(), user.getId());
        }

        userRepository.saveAll(bannedUsers);
        log.info("Updated {} users from BANNED to INACTIVE status", bannedUsers.size());
    }
}

