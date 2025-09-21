package vn.iwork4se.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import vn.iwork4se.repository.UserRepository;

@Service
public record UserServiceDetail(UserRepository userRepository) {
    public UserDetailsService UserServiceDetail() {
        return userRepository::findByUserName;
    }
}
