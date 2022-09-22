package io.github.elizabethlfransen.languageclient;

import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.utils.AttributedString;

import java.io.IOException;
import java.util.regex.Pattern;

public class LanguageClientHighlighter implements Highlighter {
    private final LanguageClient client;

    public LanguageClientHighlighter(LanguageClient client) {
        this.client = client;
    }

    @Override
    public AttributedString highlight(LineReader reader, String buffer) {
        try {
            return client.highlight(buffer);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setErrorPattern(Pattern errorPattern) {
    }

    @Override
    public void setErrorIndex(int errorIndex) {
    }
}
