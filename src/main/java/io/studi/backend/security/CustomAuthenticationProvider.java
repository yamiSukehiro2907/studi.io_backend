package io.studi.backend.security;

import io.studi.backend.helpers.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        String identifier = authentication.getName();
        String password = authentication.getCredentials().toString();

        log.info("Authentication attempt for identifier: {}", identifier);

        try {
            UserDetails userDetails = Validator.isEmail(identifier)
                    ? userDetailsService.loadUserByEmail(identifier)
                    : userDetailsService.loadUserByUsername(identifier);

            if (!userDetails.isAccountNonLocked()) {
                log.warn("Account locked for user: {}", identifier);
                throw new LockedException("Account is locked. Please contact support.");
            }

            if (!userDetails.isEnabled()) {
                log.warn("Disabled account for user: {}", identifier);
                throw new DisabledException("Your account is disabled. Please verify your email or contact support.");
            }

            if (!userDetails.isAccountNonExpired()) {
                log.warn("Expired account for user: {}", identifier);
                throw new AccountExpiredException("Your account has expired.");
            }

            if (!userDetails.isCredentialsNonExpired()) {
                log.warn("Credentials expired for user: {}", identifier);
                throw new CredentialsExpiredException("Your credentials have expired. Please reset your password.");
            }

            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                log.warn("Invalid password for user: {}", identifier);
                throw new BadCredentialsException("Invalid password.");
            }

            log.info("Authentication successful for user: {}", identifier);
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        } catch (UsernameNotFoundException e) {
            log.warn("No user found with identifier: {}", identifier);
            throw new BadCredentialsException("No user found with this email or username.");
        } catch (LockedException | DisabledException | AccountExpiredException | CredentialsExpiredException | BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during authentication for {}: {}", identifier, e.getMessage(), e);
            throw new InternalAuthenticationServiceException("Unexpected authentication error occurred.", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
