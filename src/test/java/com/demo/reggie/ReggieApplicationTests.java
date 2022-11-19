package com.demo.reggie;

import com.demo.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class ReggieApplicationTests {
    @Autowired
    DishService dishService;

    @Test
    void contextLoads() {

    }

}
