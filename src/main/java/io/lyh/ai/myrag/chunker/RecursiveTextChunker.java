package io.lyh.ai.myrag.chunker;

import com.aliyun.core.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecursiveTextChunker implements TextChunker {

    private static final List<String> SEPARATORS = List.of(
            "\n\n",
            "\n",
            "。",
            "！",
            "？",
            ".",
            "!",
            "?",
            " ",
            ""
    );

    private final int chunkSize;
    private final int overlap;

    public RecursiveTextChunker(int chunkSize, int overlap) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize 必须大于 0");
        }
        if (overlap < 0 || overlap >= chunkSize) {
            throw new IllegalArgumentException(
                    "overlap 必须大于等于 0 且小于 chunkSize"
            );
        }
        this.chunkSize = chunkSize;
        this.overlap = overlap;
    }

    @Override
    public List<String> split(String text) {
        if (StringUtils.isEmpty(text)) {
            return Collections.emptyList();
        }

        List<String> units = splitRecursively(text, 0);
        return mergeWithOverlap(units);
    }

    private List<String> splitRecursively(String text, int level) {
        if (text.length() <= chunkSize) {
            return List.of(text);
        }

        if (level >= SEPARATORS.size()) {
            return splitByLength(text);
        }

        String separator = SEPARATORS.get(level);

        if (separator.isEmpty()) {
            return splitByLength(text);
        }

        List<String> parts = splitKeepingSeparator(text, separator);

        if (parts.size() <= 1) {
            return splitRecursively(text, level + 1);
        }

        List<String> result = new ArrayList<>();

        for (String part : parts) {
            if (part.length() <= chunkSize) {
                result.add(part);
            } else {
                result.addAll(splitRecursively(part, level + 1));
            }
        }
        return result;
    }

    private List<String> splitKeepingSeparator(String text, String separator) {
        List<String> result = new ArrayList<>();

        int start = 0;
        int index;

        while ((index = text.indexOf(separator, start)) > 0) {
            int end = index + separator.length();

            result.add(text.substring(start, end));
            start = end;
        }

        if (start < text.length()) {
            result.add(text.substring(start));
        }
        return result;
    }

    private List<String> splitByLength(String text) {
        List<String> result = new ArrayList<>();

        for (int i = 0; i < text.length(); i += chunkSize) {
            result.add(
                    text.substring(i,
                            Math.min(i + chunkSize, text.length()))
            );
        }
        return result;
    }

    private List<String> mergeWithOverlap(List<String> units) {
        List<String> result = new ArrayList<>();

        StringBuilder current = new StringBuilder();

        for (String unit : units) {

            if (current.length() + unit.length() <= chunkSize) {
                current.append(unit);
                continue;
            }

            if (!current.isEmpty()) {
                String chunk = current.toString().trim();

                if (!chunk.isEmpty()) {
                    result.add(chunk);
                }

                String previous = current.toString();

                int overlapStart = Math.max(0, previous.length() - overlap);

                String overlapText = previous.substring(overlapStart);

                current = new StringBuilder(overlapText);
            }

            if (current.length() + unit.length() > chunkSize) {
                int available = Math.max(0, chunkSize - unit.length());
                String retained = current.toString();

                current = new StringBuilder(
                        retained.substring(
                                Math.max(0, retained.length() - available)
                        )
                );

                current.append(unit);
            }
        }
        if (!current.isEmpty()) {
            String chunk = current.toString().trim();

            if (!chunk.isEmpty()) {
                result.add(chunk);
            }
        }
        return result;
    }
}
