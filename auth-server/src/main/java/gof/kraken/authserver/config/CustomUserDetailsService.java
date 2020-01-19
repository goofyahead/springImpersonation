package gof.kraken.authserver.config;

import gof.kraken.authserver.model.CustomUserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CustomUserDetailsService implements UserDetailsService {

    private Map<String, CustomUserDetails> users = new HashMap<>();

    public CustomUserDetailsService(){
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        CustomUserDetails user = CustomUserDetails.builder()
                .customerId(12345L)
                .location("Brazil")
                .username("alex")
                .password(encoder.encode("user_secret"))
                .authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("READ_COMPANY")))
                .isAdmin(false)
                .build();

        CustomUserDetails userAdmin = CustomUserDetails.builder()
                .username("admin")
                .customerId(99999L)
                .location("Nicaragua")
                .password(encoder.encode("user_secret"))
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .isAdmin(true)
                .build();

        users.put(user.getUsername(), user);
        users.put(userAdmin.getUsername(), userAdmin);
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (users.get(username) != null) {
            return users.get(username);
        } else {
            throw new UsernameNotFoundException("Cant find you");
        }
    }
}
