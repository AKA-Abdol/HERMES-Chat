package com.codestar.HAMI.service;

import com.codestar.HAMI.entity.User;
import com.codestar.HAMI.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public void addUser(User user) {
        userRepository.saveAndFlush(user);
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(
            String id
    ) throws UsernameNotFoundException {
        return userRepository
                .findById(Long.parseLong(id))
                .orElseThrow(() -> new UsernameNotFoundException("User not Found!"));
    }
}
