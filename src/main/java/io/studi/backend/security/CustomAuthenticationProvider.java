package io.studi.backend.security;

import io.studi.backend.helpers.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            String identifier = authentication.getName();
            String password = authentication.getCredentials().toString();

            log.info("Attempting authentication for identifier: {}", identifier);

            UserDetails userDetails = Validator.isEmail(identifier)
                    ? userDetailsService.loadUserByEmail(identifier)
                    : userDetailsService.loadUserByUsername(identifier);

            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                log.warn("Invalid credentials for user: {}", identifier);
                throw new BadCredentialsException("Invalid email or password");
            }

            log.info("Authentication successful for user: {}", identifier);

            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Authentication error for user {}: {}", authentication.getName(), e.getMessage(), e);
            throw new InternalAuthenticationServiceException("Internal server error during authentication", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
