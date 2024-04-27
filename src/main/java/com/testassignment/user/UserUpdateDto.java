package com.testassignment.user;

import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Validated
public record UserUpdateDto(@Pattern(regexp = "^([^ ]+@[^ ]+\\.[a-z]{2,6}|)$")
                            String email,
                            String firstName,
                            String lastName,
                            LocalDate birthDate,
                            String address,
                            String phoneNumber) {
}
