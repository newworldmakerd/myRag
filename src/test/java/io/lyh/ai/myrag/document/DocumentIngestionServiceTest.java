package io.lyh.ai.myrag.document;

import io.lyh.ai.myrag.service.DocumentIngestionService;
import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class DocumentIngestionServiceTest {

    @Autowired
    private DocumentIngestionService ingestionService;

    @Test
    void testIngest() throws TikaException, IOException {
        ingestionService.ingest(3L);
    }
}
