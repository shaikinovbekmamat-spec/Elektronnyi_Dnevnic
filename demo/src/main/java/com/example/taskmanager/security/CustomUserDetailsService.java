package com.example.taskmanager.security;

import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            System.out.println("DEBUG: Attempting login for user: " + username);
            com.example.taskmanager.model.User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        System.out.println("DEBUG: User NOT found in database: " + username);
                        return new UsernameNotFoundException("User not found: " + username);
                    });

            System.out.println("DEBUG: User found: " + user.getUsername());
            String roleName = (user.getRole() != null) ? user.getRole().name() : "STUDENT";
            
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities("ROLE_" + roleName)
                    .build();
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR during authentication: " + e.getMessage());
            e.printStackTrace();
            throw new UsernameNotFoundException("Internal error during auth", e);
        }
    }
}
