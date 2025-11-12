package io.studi.backend.security;

import io.studi.backend.constants.LoggerHelper;
import io.studi.backend.constants.Validator;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider(CustomUserDetailsService _userDetailsService, PasswordEncoder _passwordEncoder) {
        this.userDetailsService = _userDetailsService;
        this.passwordEncoder = _passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            String identifier = authentication.getName();
            String password = authentication.getCredentials().toString();
            UserDetails userDetails;
            if (Validator.isEmail(identifier)) {
                userDetails = userDetailsService.loadUserByEmail(identifier);
            } else {
                userDetails = userDetailsService.loadUserByUsername(identifier);
            }
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("Invalid email or Password");
            }
            return new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
        } catch (Exception e) {
            LoggerHelper.error(this, "Error while creating user: " + e.getMessage(), e);
            throw new InternalAuthenticationServiceException("Internal Server Error!");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
