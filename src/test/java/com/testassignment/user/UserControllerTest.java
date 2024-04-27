package com.testassignment.user;

import com.testassignment.TestAssignmentApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = TestAssignmentApplication.class, properties = "user.min-age=18")
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    final String userJson = """
            {
              "email": "test@test.com",
              "firstName": "test",
              "lastName": "test",
              "birthDate": "2005-04-27",
              "address": "test",
              "phoneNumber": "test"
            }""";

    final String updatedUserJson = """
            {
              "email": "updated@test.com",
              "firstName": "test",
              "lastName": "test",
              "birthDate": "2005-04-27",
              "address": "test",
              "phoneNumber": "test"
            }""";

    @AfterEach
    public void resetDb() {
        userRepository.deleteAll();
    }

    @Test
    public void testCreateUser() throws Exception {
        mvc.perform(post("/api/v1/users")
                        .contentType("application/json")
                        .content(userJson))
                .andExpect(status().isOk()).andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    public void testCreateUserBadEmail() throws Exception {
        mvc.perform(post("/api/v1/users")
                        .contentType("application/json")
                        .content(userJson.replace("@", "")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateUserDuplicatedEmail() throws Exception {
        createTestUser();
        mvc.perform(post("/api/v1/users")
                        .contentType("application/json")
                        .content(userJson))
                .andExpect(status().isConflict());
    }

    @Test
    public void testCreateUnderagedUserAge() throws Exception {
        mvc.perform(post("/api/v1/users")
                        .contentType("application/json")
                        .content(userJson.replace("2005", "2023")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateUserFutureBirthDate() throws Exception {
        mvc.perform(post("/api/v1/users")
                        .contentType("application/json")
                        .content(userJson.replace("2005", "2026")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUser() throws Exception {
        createTestUser();
        mvc.perform(put("/api/v1/users/" + userRepository.findAll().getFirst().getId())
                        .contentType("application/json")
                        .content(updatedUserJson))
                .andExpect(status().isOk()).andExpect(jsonPath("$.email").value("updated@test.com"));
    }

    @Test
    public void testUpdateUserNotFound() throws Exception {
        mvc.perform(put("/api/v1/users/1")
                        .contentType("application/json")
                        .content(userJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateUserBadEmail() throws Exception {
        createTestUser();
        mvc.perform(put("/api/v1/users/" + userRepository.findAll().getFirst().getId())
                        .contentType("application/json")
                        .content(userJson.replace("@", "")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUserDuplicatedEmail() throws Exception {
        createTestUser();
        mvc.perform(put("/api/v1/users/" + userRepository.findAll().getFirst().getId())
                        .contentType("application/json")
                        .content(userJson))
                .andExpect(status().isConflict());
    }

    @Test
    public void testUpdateUnderagedUserAge() throws Exception {
        createTestUser();
        mvc.perform(put("/api/v1/users/" + userRepository.findAll().getFirst().getId())
                        .contentType("application/json")
                        .content(userJson.replace("2005", "2023")
                                .replace("test@test.com", "update@test.com")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUserFutureBirthDate() throws Exception {
        createTestUser();
        mvc.perform(put("/api/v1/users/" + userRepository.findAll().getFirst().getId())
                        .contentType("application/json")
                        .content(userJson.replace("2005", "2030")
                                .replace("test@test.com", "update@test.com")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUserBirthDate() throws Exception {
        createTestUser();
        mvc.perform(put("/api/v1/users/" + userRepository.findAll().getFirst().getId())
                        .contentType("application/json")
                        .content(userJson.replace("2005", "2006")
                                .replace("test@test.com", "update@test.com")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.birthDate").value("2006-04-27"));
    }

    @Test
    public void testUpdateUserFields() throws Exception {
        createTestUser();
        mvc.perform(patch("/api/v1/users/" + userRepository.findAll().getFirst().getId())
                        .contentType("application/json")
                        .content("{\"email\": \"updated@test.com\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.email").value("updated@test.com"));
    }

    @Test
    public void testUpdateUserFieldsNotFound() throws Exception {
        mvc.perform(patch("/api/v1/users/1")
                        .contentType("application/json")
                        .content("{\"email\": \"test@test.com\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateUserFieldsBadEmail() throws Exception {
        createTestUser();
        mvc.perform(patch("/api/v1/users/" + userRepository.findAll().getFirst().getId())
                        .contentType("application/json")
                        .content("{\"email\": \"testtest.com\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUserFieldsDuplicatedEmail() throws Exception {
        createTestUser();
        mvc.perform(patch("/api/v1/users/" + userRepository.findAll().getFirst().getId())
                        .contentType("application/json")
                        .content("{\"email\": \"test@test.com\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    public void testUpdateUserFieldsUnderagedUserAge() throws Exception {
        createTestUser();
        mvc.perform(patch("/api/v1/users/" + userRepository.findAll().getFirst().getId())
                        .contentType("application/json")
                        .content("{\"birthDate\": \"2024-01-27\"} "))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUserFieldsFutureBirthDate() throws Exception {
        createTestUser();
        mvc.perform(patch("/api/v1/users/" + userRepository.findAll().getFirst().getId())
                        .contentType("application/json")
                        .content("{\"birthDate\": \"2026-01-27\"} "))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUserFieldsFirstName() throws Exception {
        createTestUser();
        mvc.perform(patch("/api/v1/users/" + userRepository.findAll().getFirst().getId())
                        .contentType("application/json")
                        .content("{\"firstName\": \"updated\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.firstName").value("updated"));
    }

    @Test
    public void testUpdateUserFieldsLastName() throws Exception {
        createTestUser();
        mvc.perform(patch("/api/v1/users/" + userRepository.findAll().getFirst().getId())
                        .contentType("application/json")
                        .content("{\"lastName\": \"updated\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.lastName").value("updated"));
    }

    @Test
    public void testUpdateUserFieldsPhoneNumber() throws Exception {
        createTestUser();
        mvc.perform(patch("/api/v1/users/" + userRepository.findAll().getFirst().getId())
                        .contentType("application/json")
                        .content("{\"phoneNumber\": \"updated\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.phoneNumber").value("updated"));
    }

    @Test
    public void testUpdateUserFieldsAddress() throws Exception {
        createTestUser();
        mvc.perform(patch("/api/v1/users/" + userRepository.findAll().getFirst().getId())
                        .contentType("application/json")
                        .content("{\"address\": \"updated\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.address").value("updated"));
    }

    @Test
    public void testUpdateUserFieldsBirthDate() throws Exception {
        createTestUser();
        mvc.perform(patch("/api/v1/users/" + userRepository.findAll().getFirst().getId())
                        .contentType("application/json")
                        .content("{\"birthDate\": \"2006-04-27\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.birthDate").value("2006-04-27"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        createTestUser();
        mvc.perform(delete("/api/v1/users/" + userRepository.findAll().getFirst().getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteUserNotFound() throws Exception {
        mvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSearchUsersByBirthDateRange() throws Exception {
        createTestUser();
        mvc.perform(get("/api/v1/users/search")
                        .param("startDate", "1400-04-27")
                        .param("endDate", "1700-04-27"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].email").value("test@test.com"));
    }

    @Test
    public void testSearchUsersByBirthDateRangeBadDate() throws Exception {
        createTestUser();
        mvc.perform(get("/api/v1/users/search")
                        .param("startDate", "1700-04-27")
                        .param("endDate", "1400-04-27"))
                .andExpect(status().isBadRequest());
    }

    private void createTestUser() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setFirstName("test");
        user.setLastName("test");
        user.setBirthDate(LocalDate.of(1500, 4, 27));
        user.setAddress("test");
        user.setPhoneNumber("test");
        userRepository.save(user);
    }
}
