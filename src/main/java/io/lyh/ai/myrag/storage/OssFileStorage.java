package io.lyh.ai.myrag.storage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class OssFileStorage implements FileStorage {

    private final OSS ossClient;
    private final String bucketName;

    public OssFileStorage(
            @Value("${myrag.storage.oss.endpoint}") String endpoint,
            @Value("${myrag.storage.oss.bucket-name}") String bucketName,
            @Value("${myrag.storage.oss.access-key-id}") String accessKeyId,
            @Value("${myrag.storage.oss.access-key-secret}") String accessKeySecret
    ) {
        this.bucketName = bucketName;
        this.ossClient = new OSSClientBuilder()
                .build(endpoint, accessKeyId, accessKeySecret);
    }

    @Override
    public void upload(String objectKey, InputStream inputStream, long contentLength) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);

        ossClient.putObject(bucketName, objectKey, inputStream, metadata);
    }

    @Override
    public InputStream download(String objectKey) {
        return ossClient.getObject(bucketName, objectKey)
                .getObjectContent();
    }

    @Override
    public void delete(String objectKey) {
        ossClient.deleteObject(bucketName, objectKey);
    }

    @PreDestroy
    public void shutdown() {
        ossClient.shutdown();
    }
}
