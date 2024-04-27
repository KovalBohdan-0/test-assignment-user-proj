package com.testassignment.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Validated
public record UserCreateDto(@Pattern(regexp = "^([^ ]+@[^ ]+\\.[a-z]{2,6}|)$")
                            @NotNull(message = "The 'email' cannot be null")
                            @NotBlank(message = "The 'email' cannot be empty")
                            String email,
                            @NotNull(message = "The 'firstName' cannot be null")
                            @NotBlank(message = "The 'firstName' cannot be empty")
                            String firstName,
                            @NotNull(message = "The 'lastName' cannot be null")
                            @NotBlank(message = "The 'lastName' cannot be empty")
                            String lastName,
                            @NotNull(message = "The 'birthDate' cannot be null")
                            LocalDate birthDate,
                            String address,
                            String phoneNumber) {
}
