package com.udacity.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        properties = "spring.jpa.defer-datasource-initialization=true"
)
@ActiveProfiles("test")
class ContextLoadsIT {
    @Test void contextLoads() { }
}