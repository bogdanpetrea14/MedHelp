package com.mobylab.springbackend.config.security;

import com.mobylab.springbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<com.mobylab.springbackend.entity.User> optionalUser = userRepository.findUserByEmail(email);

        if (optionalUser.isPresent()) {
            com.mobylab.springbackend.entity.User user = optionalUser.get();
            // Extragem Enum-ul nostru și îl mapam la ce așteaptă Spring Security
            return new User(user.getEmail(), user.getPassword(), mapRolesToAuthorities(user));
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    // Am modificat metoda sa primeasca user-ul si sa foloseasca noul camp getRole()
    private Collection<GrantedAuthority> mapRolesToAuthorities(com.mobylab.springbackend.entity.User user){
        // Spring Security asteapta de obicei formatul "ROLE_ADMIN", "ROLE_DOCTOR" etc.
        // Daca in controlerele tale vechi foloseai hasAuthority('ADMIN'), lasa fara 'ROLE_'
        String authorityName = user.getRole().name();
        return Collections.singletonList(new SimpleGrantedAuthority(authorityName));
    }
}