package com.utp.trabajo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.utp.trabajo.util.HeadlessSpringBootContextLoader;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(loader = HeadlessSpringBootContextLoader.class)
class SistemaLibreriaApplicationTests {

    @Test
    void contextLoads() {
    }

}
