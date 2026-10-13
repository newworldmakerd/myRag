package io.lyh.ai.myrag.vector;

import io.lyh.ai.myrag.service.VectorIndexService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class VectorIndexServiceTest {

    @Autowired
    private VectorIndexService vectorIndexService;

    @Test
    public void testIndexDocument() {
        vectorIndexService.indexDocument(3L);
    }
}
