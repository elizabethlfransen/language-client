package io.github.elizabethlfransen.languageclient.packet;

public class ErrorPacket implements LanguagePacket{
    public final String message;

    public ErrorPacket(String message) {
        this.message = message;
    }
}
