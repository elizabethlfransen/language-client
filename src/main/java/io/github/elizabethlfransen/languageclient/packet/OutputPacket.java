package io.github.elizabethlfransen.languageclient.packet;

public class OutputPacket implements LanguagePacket {
    public final String line;

    public OutputPacket(String line) {
        this.line = line;
    }
}
