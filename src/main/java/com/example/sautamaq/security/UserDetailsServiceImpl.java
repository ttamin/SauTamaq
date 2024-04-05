package com.example.sautamaq.security;


import com.example.sautamaq.model.User;
import com.example.sautamaq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("There is no user with such username"));
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole()));

        logger.info("User details: {}", user);
        logger.info("Authorities: {}", authorities);
        return new org.springframework.security.core.userdetails
                .User(user.getEmail(),
                user.getPassword(),
                user.isActive(),
                user.isActive(),
                user.isActive(),
                user.isActive(),
                authorities);
    }
}
