package com.demo.reggie;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

@SpringBootTest
@Slf4j
class ReggieApplicationTests {
    @Autowired
    private CacheManager cacheManager;

    @Test
    void contextLoads() {

    }

}
