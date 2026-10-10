package io.lyh.ai.myrag.storage;

import java.io.InputStream;

public interface FileStorage {

    void upload(String objectKey, InputStream inputStream,
                long contentLength);

    InputStream download(String objectKey);

    void delete(String objectKey);
}
