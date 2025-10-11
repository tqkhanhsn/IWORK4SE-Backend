//package vn.iwork4se.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.stereotype.Service;
//import vn.iwork4se.repository.UserRepository;
//
//@Service
//public record UserServiceDetail(UserRepository userRepository) {
//    public UserDetailsService UserServiceDetail() {
//        return userRepository::findByUserName;
//    }
//}
package vn.iwork4se.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.iwork4se.repository.UserRepository;

import java.util.Optional;

@Service
public record UserServiceDetail(UserRepository userRepository) {
    public UserDetailsService userDetailsService() {
        return (String username) -> Optional
                .ofNullable(userRepository.findByUserName(username))
                .map(UserDetails.class::cast)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
