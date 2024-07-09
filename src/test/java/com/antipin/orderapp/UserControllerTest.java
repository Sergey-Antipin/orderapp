package com.antipin.orderapp;

import com.antipin.orderapp.model.User;
import com.antipin.orderapp.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.config.location=classpath:application-test.properties")
@AutoConfigureMockMvc
class UserControllerTest {

    private static final String URL = "/api/v1/users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository repository;

    private ObjectMapper mapper = new ObjectMapper();

    private static final User user =
            new User(null, "Username", "email@mail.ru", new ArrayList<>());

    @Test
    @Order(2)
    void whenGetSingleUserThenReturnUserWithDetails() throws Exception {
        mockMvc.perform(get(URL + "/100"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.name").value("Username"))
                .andExpect(jsonPath("$.email").value("email@mail.ru"))
                .andExpect(jsonPath("$.orders").exists());
    }

    @Test
    @Order(3)
    void whenGetAllUsersThenReturnUsersWithoutDetails() throws Exception {
        mockMvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$..orders").doesNotExist());
    }

    @Test
    @Order(1)
    void whenCreateNewUserThenReturnNewUriAndStatus201() throws Exception {
        MvcResult result = mockMvc.perform(post(URL)
                        .content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(header().exists("Location"))
                .andReturn();
        String createdUri = result.getResponse().getHeader("Location");
        Assertions.assertThat(createdUri).endsWith(URL + "/100");
    }

    @Test
    @Order(5)
    void whenDeleteThenNoContent() throws Exception {
        mockMvc.perform(delete(URL + "/100"))
                .andExpect(status().isNoContent());
        Assertions.assertThat(repository.findById(100L)).isEmpty();
    }

    @Test
    @Order(4)
    @Transactional
    void whenPutThenUpdateCorrectly() throws Exception {
        String newEmail = "newmail@yandex.ru";
        User userToUpdate = repository.findById(100L).orElseThrow();
        userToUpdate.setEmail(newEmail);
        MvcResult result = mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userToUpdate)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andReturn();
        User updatedUser = mapper.readValue(result.getResponse().getContentAsString(), User.class);
        Assertions.assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void whenUserNotFoundThen404() throws Exception {
        mockMvc.perform(get(URL + "/100500"))
                .andExpect(status().isNotFound());
    }
}