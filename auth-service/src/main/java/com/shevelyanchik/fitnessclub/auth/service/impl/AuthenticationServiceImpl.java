package com.shevelyanchik.fitnessclub.auth.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shevelyanchik.fitnessclub.auth.client.UserServiceClient;
import com.shevelyanchik.fitnessclub.auth.constant.AuthenticationErrorMessageKey;
import com.shevelyanchik.fitnessclub.auth.constant.EmailEventPayload;
import com.shevelyanchik.fitnessclub.auth.dto.AuthenticationRequest;
import com.shevelyanchik.fitnessclub.auth.dto.AuthenticationResponse;
import com.shevelyanchik.fitnessclub.auth.dto.user.Role;
import com.shevelyanchik.fitnessclub.auth.dto.user.Status;
import com.shevelyanchik.fitnessclub.auth.dto.user.UserDto;
import com.shevelyanchik.fitnessclub.auth.service.AuthenticationProducerService;
import com.shevelyanchik.fitnessclub.auth.service.AuthenticationService;
import com.shevelyanchik.fitnessclub.auth.service.security.jwt.JwtTokenProvider;
import com.shevelyanchik.fitnessclub.auth.util.AuthenticationEventUtils;
import com.shevelyanchik.fitnessclub.kafkaconfig.dto.EmailEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserServiceClient userServiceClient;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final AuthenticationProducerService authenticationProducerService;

    @Override
    public UserDto signup(UserDto userDto) {
        validateUser(userDto);
        userDto.setRole(Role.USER);
        userDto.setStatus(Status.ACTIVE);
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        UserDto savedUserDto = userServiceClient.createUser(userDto);

        EmailEvent emailEvent = buildEmailEvent(savedUserDto);
        authenticationProducerService.sendMessage(emailEvent);
        return savedUserDto;
    }

    @Override
    public Map<Object, Object> login(AuthenticationRequest authenticationRequest) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
                );

        authenticationManager.authenticate(authentication);

        UserDto userDto = userServiceClient
                .findUserByEmail(authenticationRequest.getEmail());

        String token = jwtTokenProvider
                .createToken(authenticationRequest.getEmail(), userDto.getRole());
        AuthenticationResponse response = new AuthenticationResponse(authenticationRequest.getEmail(), token);
        return objectMapper.convertValue(response, new TypeReference<>() {
        });
    }

    private void validateUser(UserDto userDto) {
        if (Objects.isNull(userDto)) {
            throw new com.shevelyanchik.fitnessclub.auth.service.exception.AuthenticationException(
                    AuthenticationErrorMessageKey.USER_VALIDATE_ERROR);
        }
        if (userServiceClient.existsUserByEmail(userDto.getEmail())) {
            throw new com.shevelyanchik.fitnessclub.auth.service.exception.AuthenticationException(
                    AuthenticationErrorMessageKey.RESOURCE_ALREADY_EXIST);
        }
    }

    private EmailEvent buildEmailEvent(UserDto userDto) {
        EmailEventPayload userCreatedEventPayload = EmailEventPayload.USER_HAS_BEEN_CREATED;
        String message = AuthenticationEventUtils.getEmailEventMessage(
                userCreatedEventPayload,
                userDto.getId(),
                userDto.getEmail()
        );

        return EmailEvent.builder()
                .subject(userCreatedEventPayload.getSubject())
                .message(message)
                .build();
    }
}
