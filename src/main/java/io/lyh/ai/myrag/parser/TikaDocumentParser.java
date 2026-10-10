package io.lyh.ai.myrag.parser;

import com.aliyuncs.utils.StringUtils;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class TikaDocumentParser implements DocumentParser {

    private static final int MAX_TEXT_LENGTH = 2_000_000;

    private final Tika tika  = new Tika();

    public TikaDocumentParser() {
        tika.setMaxStringLength(MAX_TEXT_LENGTH);
    }

    @Override
    public String detect(InputStream inputStream, String fileName) throws IOException {
        return tika.detect(inputStream, fileName);
    }

    @Override
    public ParsedDocument parse(InputStream inputStream, String fileName) throws IOException, TikaException {
        String content = tika.parseToString(inputStream);

        if (StringUtils.isEmpty(content)) {
            throw new IllegalArgumentException("File content is empty" + fileName);
        }

        return new ParsedDocument(content,fileName,null);
    }
}
