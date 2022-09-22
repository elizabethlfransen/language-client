package io.github.elizabethlfransen.languageclient.packet;

import org.jline.utils.AttributedString;

public class HighlightingResultPacket implements LanguagePacket {
    public final AttributedString result;

    public HighlightingResultPacket(AttributedString result) {
        this.result = result;
    }
}
