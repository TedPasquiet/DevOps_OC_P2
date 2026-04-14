package com.openclassrooms.etudiant.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
public class JwtServiceTest {

    private static final String SECRET = "mysecretkeymysecretkeymysecretkeymysecretkey12345678";
    private static final long EXPIRATION = 3_600_000L;
    private static final String USERNAME = "testuser";

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", EXPIRATION);
    }

    private UserDetails buildUserDetails() {
        return User.builder()
                .username(USERNAME)
                .password("password")
                .roles("USER")
                .build();
    }

    @Test
    public void generateToken_returnsNonNullToken() {
        // WHEN
        String token = jwtService.generateToken(buildUserDetails());

        // THEN
        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    public void extractUsername_returnsCorrectUsername() {
        // GIVEN
        String token = jwtService.generateToken(buildUserDetails());

        // WHEN
        String username = jwtService.extractUsername(token);

        // THEN
        assertThat(username).isEqualTo(USERNAME);
    }

    @Test
    public void isTokenValid_validToken_returnsTrue() {
        // GIVEN
        String token = jwtService.generateToken(buildUserDetails());

        // WHEN / THEN
        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    public void isTokenValid_malformedToken_returnsFalse() {
        // WHEN / THEN
        assertThat(jwtService.isTokenValid("this.is.not.a.valid.jwt")).isFalse();
    }

    @Test
    public void isTokenValid_expiredToken_returnsFalse() {
        // GIVEN - set a negative expiration so the token is immediately expired
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L);
        String token = jwtService.generateToken(buildUserDetails());

        // WHEN / THEN
        assertThat(jwtService.isTokenValid(token)).isFalse();
    }
}
