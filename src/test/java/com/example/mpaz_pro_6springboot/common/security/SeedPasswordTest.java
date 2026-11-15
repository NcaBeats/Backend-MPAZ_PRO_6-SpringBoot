package com.example.mpaz_pro_6springboot.common.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SeedPasswordTest {

    private static final String HASH =
            "$2y$10$G9eiaV79wF9balaZIMR6z.SQEgip0iNPGlKi/4oPMB18GmqLvGBL6";

    @Test
    void elHashDelSeedCorrespondeAMpaz123() {
        assertTrue(new BCryptPasswordEncoder().matches("mpaz123", HASH));
    }
}
