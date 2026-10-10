package io.lyh.ai.myrag.document;

import io.lyh.ai.myrag.parser.ParsedDocument;
import io.lyh.ai.myrag.parser.TikaDocumentParser;
import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TikaDocumentParserTest {
    private final TikaDocumentParser parser = new TikaDocumentParser();

    @Test
    void testTxt() throws Exception {
        try (InputStream inputStream = getClass()
                .getResourceAsStream("/documents/test.txt")) {

            assertNotNull(inputStream);

            ParsedDocument result =
                    parser.parse(inputStream, "test.txt");

            assertTrue(result.content().contains("Redis"));
        }
    }

    @Test
    void testMd() throws Exception {
        try (InputStream inputStream = getClass()
                .getResourceAsStream("/documents/test.md")) {

            assertNotNull(inputStream);

            ParsedDocument result =
                    parser.parse(inputStream, "test.md");

            assertTrue(result.content().contains("Redis"));
        }
    }

    @Test
    void testDocx() throws Exception {
        try (InputStream inputStream = getClass()
                .getResourceAsStream("/documents/test.docx")) {

            assertNotNull(inputStream);

            ParsedDocument result =
                    parser.parse(inputStream, "test.docx");

            assertTrue(result.content().contains("Redis"));
        }
    }

    @Test
    void testPdf() throws Exception {

        try (InputStream inputStream = getClass()
                .getResourceAsStream("/documents/《GPU高性能编程CUDA实战》.pdf")) {

            assertNotNull(inputStream);

            ParsedDocument result =
                    parser.parse(inputStream, "《GPU高性能编程CUDA实战》.pdf");

            assertFalse(result.content().isBlank());
        }
    }

    @Test
    void testDetectPdf() throws Exception {

        try (InputStream inputStream = getClass()
                .getResourceAsStream("/documents/《GPU高性能编程CUDA实战》.pdf")) {

            assertNotNull(inputStream);

            String mimeType =
                    parser.detect(inputStream, "《GPU高性能编程CUDA实战》.pdf");

            assertEquals("application/pdf", mimeType);
        }
    }
}
