package com.testassignment.user;

import com.testassignment.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(userService, "minAge", 18);
    }

    @Test
    void createUser() {
        UserCreateDto userCreateDto = new UserCreateDto("test@test.com", "John", "Doe",
                LocalDate.of(2000, 1, 1), "123 Street", "1234567890");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User user = userService.createUser(userCreateDto);

        verify(userRepository, times(1)).save(any(User.class));

        assertEquals("test@test.com", user.getEmail());
    }

    @Test
    void createUserBadEmail() {
        UserCreateDto userCreateDto = new UserCreateDto("test.com", "John", "Doe",
                LocalDate.of(2000, 1, 1), "123 Street", "1234567890");
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicatedEmailException.class, () -> userService.createUser(userCreateDto));
    }

    @Test
    void createUserUnderageDate() {
        UserCreateDto userCreateDto = new UserCreateDto("test@test.com", "John", "Doe",
                LocalDate.of(2022, 1, 1), "123 Street", "123456789");

        assertThrows(UserUnderageException.class, () -> userService.createUser(userCreateDto));
    }

    @Test
    void createUserDuplicateEmail() {
        UserCreateDto userCreateDto = new UserCreateDto("test@test.com", "John", "Doe",
                LocalDate.of(2000, 1, 1), "123 Street", "1234567890");
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicatedEmailException.class, () -> userService.createUser(userCreateDto));
    }

    @Test
    void createUserFutureDate() {
        UserCreateDto userCreateDto = new UserCreateDto("tes@test.com", "John", "Doe",
                LocalDate.of(2022, 1, 1), "123 Street", "1234567890");

        assertThrows(UserUnderageException.class, () -> userService.createUser(userCreateDto));
    }

    @Test
    void updateAll() {
        UserCreateDto userCreateDto = new UserCreateDto("test@test.com", "John", "Doe",
                LocalDate.of(2000, 1, 1), "123 Street", "1234567890");
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new User()));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User user = userService.updateAll(1L, userCreateDto);

        verify(userRepository, times(1)).save(any(User.class));
        assertEquals("test@test.com", user.getEmail());
    }

    @Test
    void updateAllNotFound() {
        UserCreateDto userCreateDto = new UserCreateDto("test@test.com", "John", "Doe",
                LocalDate.of(2000, 1, 1), "123 Street", "1234567890");
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateAll(1L, userCreateDto));
    }

    @Test
    void updateAllBadEmail() {
        UserCreateDto userCreateDto = new UserCreateDto("test.com", "John", "Doe",
                LocalDate.of(2000, 1, 1), "123 Street", "1234567890");
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicatedEmailException.class, () -> userService.updateAll(1L, userCreateDto));
    }

    @Test
    void updateAllUnderageDate() {
        UserCreateDto userCreateDto = new UserCreateDto("test@test.com", "John", "Doe",
                LocalDate.of(2018, 1, 1), "123 Street", "1234567890");

        assertThrows(UserUnderageException.class, () -> userService.updateAll(1L, userCreateDto));
    }

    @Test
    void updateAllDuplicateEmail() {
        UserCreateDto userCreateDto = new UserCreateDto("test@test.com", "John", "Doe",
                LocalDate.of(2000, 1, 1), "123 Street", "1234567890");
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicatedEmailException.class, () -> userService.updateAll(1L, userCreateDto));
    }

    @Test
    void updateAllFutureDate() {
        UserCreateDto userCreateDto = new UserCreateDto("test@test.com", "John", "Doe",
                LocalDate.of(2030, 1, 1), "123 Street", "1234567890");

        assertThrows(FutureBirthDateException.class, () -> userService.updateAll(1L, userCreateDto));
    }

    @Test
    void updateUserFields() {
        UserUpdateDto userUpdateDto = new UserUpdateDto("test@test.com", "John", "Doe",
                LocalDate.of(2000, 1, 1), "123 Street", "1234567890");
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new User()));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User user = userService.updateUserFields(1L, userUpdateDto);

        verify(userRepository, times(1)).save(any(User.class));
        assertEquals("test@test.com", user.getEmail());
    }

    @Test
    void updateUserFieldsNotFound() {
        UserUpdateDto userUpdateDto = new UserUpdateDto("test@test.com", null, null,
                null, null, null);
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUserFields(1L, userUpdateDto));
    }

    @Test
    void updateUserFieldsBadEmail() {
        UserUpdateDto userUpdateDto = new UserUpdateDto("test.com", null, null,
                null, null, null);
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new User()));
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicatedEmailException.class, () -> userService.updateUserFields(1L, userUpdateDto));
    }

    @Test
    void updateUserFieldsUnderageDate() {
        UserUpdateDto userUpdateDto = new UserUpdateDto(null, null, null,
                LocalDate.of(2020, 1, 1), null, null);
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new User()));

        assertThrows(UserUnderageException.class, () -> userService.updateUserFields(1L, userUpdateDto));
    }

    @Test
    void updateUserFieldsDuplicateEmail() {
        UserUpdateDto userUpdateDto = new UserUpdateDto("test@test.com", null, null,
                null, null, null);
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new User()));
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicatedEmailException.class, () -> userService.updateUserFields(1L, userUpdateDto));
    }

    @Test
    void updateUserFieldsFutureDate() {
        UserUpdateDto userUpdateDto = new UserUpdateDto(null, null, null,
                LocalDate.of(2030, 1, 1), null, null);
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new User()));

        assertThrows(FutureBirthDateException.class, () -> userService.updateUserFields(1L, userUpdateDto));
    }

    @Test
    void updateUserFieldsFirstName() {
        UserUpdateDto userUpdateDto = new UserUpdateDto(null, "John", null,
                null, null, null);
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new User()));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.updateUserFields(1L, userUpdateDto);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserFieldsLastName() {
        UserUpdateDto userUpdateDto = new UserUpdateDto(null, null, "Doe",
                null, null, null);
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new User()));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.updateUserFields(1L, userUpdateDto);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserFieldsBirthDate() {
        UserUpdateDto userUpdateDto = new UserUpdateDto(null, null, null,
                LocalDate.of(2000, 1, 1), null, null);
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new User()));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.updateUserFields(1L, userUpdateDto);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserFieldsAddress() {
        UserUpdateDto userUpdateDto = new UserUpdateDto(null, null, null,
                null, "123 Street", null);
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new User()));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.updateUserFields(1L, userUpdateDto);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserFieldsPhoneNumber() {
        UserUpdateDto userUpdateDto = new UserUpdateDto(null, null, null,
                null, null, "1234567890");
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new User()));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.updateUserFields(1L, userUpdateDto);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteUser() {
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new User()));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(any(User.class));
    }

    @Test
    void deleteUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void searchUsersByBirthDateRange() {
        LocalDate startDate = LocalDate.of(2000, 1, 1);
        LocalDate endDate = LocalDate.of(2000, 1, 2);
        when(userRepository.findByBirthDateBetween(startDate, endDate)).thenReturn(java.util.List.of(new User()));

        userService.searchUsersByBirthDateRange(startDate, endDate);

        verify(userRepository, times(1)).findByBirthDateBetween(startDate, endDate);
    }

    @Test
    void searchUsersByBirthDateRangeInvalidDateRange() {
        LocalDate startDate = LocalDate.of(2000, 1, 2);
        LocalDate endDate = LocalDate.of(2000, 1, 1);

        assertThrows(InvalidDateRangeException.class, () -> userService.searchUsersByBirthDateRange(startDate, endDate));
    }
}
