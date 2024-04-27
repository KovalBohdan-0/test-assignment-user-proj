package com.testassignment.user;

import com.testassignment.exceptions.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    @Value("${user.min-age}")
    private int minAge;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User createUser(UserCreateDto userCreateDto) {
        validatedUserDto(userCreateDto);

        User user = new User();
        setUserData(user, userCreateDto);
        return userRepository.save(user);
    }

    @Transactional
    public User updateAll(Long id, UserCreateDto userCreateDto) {
        validatedUserDto(userCreateDto);
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(String.format("User with id %d not found", id)));
        setUserData(user, userCreateDto);
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserFields(Long id, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(String.format("User with id %d not found", id)));

        if (userUpdateDto.email() != null) {
            validateEmail(userUpdateDto.email());
            user.setEmail(userUpdateDto.email());
        }
        if (userUpdateDto.firstName() != null) {
            user.setFirstName(userUpdateDto.firstName());
        }
        if (userUpdateDto.lastName() != null) {
            user.setLastName(userUpdateDto.lastName());
        }
        if (userUpdateDto.birthDate() != null) {
            validateBirthDate(userUpdateDto.birthDate());
            user.setBirthDate(userUpdateDto.birthDate());
        }
        if (userUpdateDto.address() != null) {
            user.setAddress(userUpdateDto.address());
        }
        if (userUpdateDto.phoneNumber() != null) {
            user.setPhoneNumber(userUpdateDto.phoneNumber());
        }

        return userRepository.save(user);
    }


    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(String.format("User with id %d not found", id)));
        userRepository.delete(user);
    }

    public List<User> searchUsersByBirthDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("Start date cannot be after end date");
        }

        return userRepository.findByBirthDateBetween(startDate, endDate);
    }

    private void setUserData(User user, UserCreateDto userCreateDto) {
        user.setEmail(userCreateDto.email());
        user.setFirstName(userCreateDto.firstName());
        user.setLastName(userCreateDto.lastName());
        user.setBirthDate(userCreateDto.birthDate());
        user.setAddress(userCreateDto.address());
        user.setPhoneNumber(userCreateDto.phoneNumber());
    }

    private void validatedUserDto(UserCreateDto userCreateDto) {
        validateEmail(userCreateDto.email());
        validateBirthDate(userCreateDto.birthDate());
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicatedEmailException("Email already exists");
        }
    }

    private void validateBirthDate(LocalDate birthDate) {
        if (birthDate.isAfter(java.time.LocalDate.now())) {
            throw new FutureBirthDateException("Birth date cannot be in the future");
        } else if (birthDate.plusYears(minAge).isAfter(java.time.LocalDate.now())) {
            throw new UserUnderageException("User must be at least " + minAge + " years old");
        }
    }
}
