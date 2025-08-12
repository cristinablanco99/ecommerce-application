package com.udacity.ecommerce;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udacity.ecommerce.controllers.UserController;
import com.udacity.ecommerce.model.dto.CreateUserRequest;
import com.udacity.ecommerce.model.persistence.Cart;
import com.udacity.ecommerce.model.persistence.User;
import com.udacity.ecommerce.model.persistence.repositories.CartRepository;
import com.udacity.ecommerce.model.persistence.repositories.UserRepository;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
class UserControllerUnitTest {

    @Resource MockMvc mockMvc;
    @Resource ObjectMapper objectMapper;

    @MockBean UserRepository userRepository;
    @MockBean CartRepository cartRepository;
    @MockBean PasswordEncoder passwordEncoder;

    @Test
    void createUser_OK_201_and_noPassword() throws Exception {
        when(userRepository.findByUsername("alice")).thenReturn(null);
        when(passwordEncoder.encode("password1")).thenReturn("hashed");

        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> {
            Cart c = i.getArgument(0);
            c.setId(1L);
            return c;
        });

        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(10L);
            return u;
        });

        var body = new CreateUserRequest("alice", "password1", "password1");

        mockMvc.perform(post("/api/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.username", is("alice")))
                .andExpect(jsonPath("$.password").doesNotExist());

        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(cap.capture());
        assertEquals("hashed", cap.getValue().getPassword());  // ✅
    }

    @Test
    void createUser_duplicate_409() throws Exception {
        when(userRepository.findByUsername("dave")).thenReturn(new User());
        var body = new CreateUserRequest("dave", "password1", "password1");

        mockMvc.perform(post("/api/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict());
    }

    @Test
    void createUser_shortPassword_400() throws Exception {
        var body = new CreateUserRequest("bob", "short", "short");
        mockMvc.perform(post("/api/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_mismatch_400() throws Exception {
        var body = new CreateUserRequest("carol", "password1", "password2");
        mockMvc.perform(post("/api/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }
}
