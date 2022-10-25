package org.mirea.pm.notes_backend.security;

import org.mirea.pm.notes_backend.db.User;
import org.mirea.pm.notes_backend.db.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByName(username).
                orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        return new UserDetailsImpl(user);
    }
}
