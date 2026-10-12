package io.lyh.ai.myrag.service;

import org.apache.tika.exception.TikaException;

import java.io.IOException;

public interface DocumentIngestionService {

    void ingest(Long documentId) throws TikaException, IOException;

}
