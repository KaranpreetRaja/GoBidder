package com.gobidder.auth;

import com.gobidder.auth.dtos.LoginDto;
import com.gobidder.auth.dtos.RegisterDto;
import com.gobidder.auth.model.User;
import com.gobidder.auth.repository.UserRepository;
import com.gobidder.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthApplicationTests {

	@Mock
	private UserRepository userRepository;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AuthService authService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testSignup() {
		RegisterDto registerDto = new RegisterDto();
		registerDto.setFullName("John Doe");
		registerDto.setEmail("john.doe@example.com");
		registerDto.setPassword("password123");
		registerDto.setCardNumber("4111111111111111");
		registerDto.setCsv("123");
		registerDto.setExpirationDate("12/25");
		registerDto.setBillingAddress("123 Main St");

		when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		User result = authService.signup(registerDto);

		assertNotNull(result);
		assertEquals("John Doe", result.getFullName());
		assertEquals("john.doe@example.com", result.getEmail());
		assertEquals("encodedPassword", result.getPassword());
	}

	@Test
	public void testAuthenticate() {
		LoginDto loginDto = new LoginDto();
		loginDto.setEmail("john.doe@example.com");
		loginDto.setPassword("password123");

		User user = new User();
		user.setEmail("john.doe@example.com");
		user.setPassword("encodedPassword");

		when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

		User result = authService.authenticate(loginDto);

		assertNotNull(result);
		assertEquals("john.doe@example.com", result.getEmail());
	}
}