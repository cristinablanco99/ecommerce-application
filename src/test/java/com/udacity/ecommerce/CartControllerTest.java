package com.udacity.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CartControllerTest extends TestSupport {

    @Test
    void add_then_remove_ok_and_404_for_unknown_user() throws Exception {
        String auth = createUserAndLogin("bob", "password1");

        var add = Map.of("username", "bob", "itemId", 1, "quantity", 2);
        mockMvc.perform(post("/api/cart/addToCart")
                        .header(HttpHeaders.AUTHORIZATION, auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(add)))
                .andExpect(status().isOk());

        var remove = Map.of("username", "bob", "itemId", 1, "quantity", 1);
        mockMvc.perform(post("/api/cart/removeFromCart")
                        .header(HttpHeaders.AUTHORIZATION, auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(remove)))
                .andExpect(status().isOk());

        var bad = Map.of("username", "nobody", "itemId", 1, "quantity", 1);
        mockMvc.perform(post("/api/cart/addToCart")
                        .header(HttpHeaders.AUTHORIZATION, auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isNotFound());
    }
}