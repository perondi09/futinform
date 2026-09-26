package perondi.futinform.security;

import perondi.futinform.entities.UserEntity;
import perondi.futinform.repositories.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) {
        UserEntity user = userRepository
        
                log.debug("Loading user by identifier={}", usernameOrEmail);
            
                .findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> {
                    log.warn("User not found for identifier={}", usernameOrEmail);
                    return new UsernameNotFoundException("Usuário não encontrado");
                });
                

        return new User(user.getId().toString(), user.getPasswordHash(), Collections.emptyList());
    }
}