package com.atguigu.tingshu.search.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author admin
 * @version 1.0
 */

@SpringBootTest
public class SearchServiceTest {

    @Autowired
    private SearchService searchService;

    @Test
    public void search() {
        for (long i = 1; i <= 1607; i++) {
            searchService.upperAlbum(i);
        }
    }
}
