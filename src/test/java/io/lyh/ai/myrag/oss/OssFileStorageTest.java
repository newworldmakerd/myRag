package io.lyh.ai.myrag.oss;

import io.lyh.ai.myrag.storage.FileStorage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class OssFileStorageTest {

    @Autowired
    private FileStorage fileStorage;

    @Test
    void testUploadAndDownload() throws Exception{
        String objectKey =
                "myrag/test/" + UUID.randomUUID() + ".txt";

        byte[] content =
                "Hello myRag OSS".getBytes(StandardCharsets.UTF_8);

        try {
            fileStorage.upload(
                    objectKey,
                    new ByteArrayInputStream(content),
                    content.length
            );

            try (InputStream inputStream =
                         fileStorage.download(objectKey)) {

                String result = new String(
                        inputStream.readAllBytes(),
                        StandardCharsets.UTF_8
                );

                assertEquals("Hello myRag OSS", result);
            }
        } finally {
            fileStorage.delete(objectKey);
        }
    }
}
