package com.nnk.springboot.config.userdetails;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustumDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(usernameOrEmail);

        if (user != null) {
            return new org.springframework.security.core.userdetails.User(user.getUsername()
                    , user.getPassword(),
                    user.getRoles().stream()
                            .map((role) -> new SimpleGrantedAuthority(role.getRoleName()))
                            .collect(Collectors.toList()));
        }else {
            throw new UsernameNotFoundException("Invalid email or password");
        }
    }
}
