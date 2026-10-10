package io.lyh.ai.myrag.parser;

import org.apache.tika.exception.TikaException;

import java.io.IOException;
import java.io.InputStream;

public interface DocumentParser {
    String detect(InputStream inputStream, String fileName) throws IOException;

    ParsedDocument parse(InputStream inputStream, String fileName) throws IOException, TikaException;
}
