package io.github.elizabethlfransen.languageclient.packet;

import org.jline.utils.AttributedString;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketReader {
    private final DataInputStream inputStream;

    public PacketReader(DataInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public LanguagePacket nextPacket() throws IOException {
        String packetId = inputStream.readUTF();
        if(packetId.equals("highlight")) return readHighlightPacket();
        if(packetId.equals("output")) return readOutputPacket();
        if(packetId.equals("error")) return readErrorPacket();
        throw new RuntimeException("Unknown packet id");
    }

    private HighlightingResultPacket readHighlightPacket() throws IOException {
        return new HighlightingResultPacket(AttributedString.fromAnsi(inputStream.readUTF()));
    }
    private OutputPacket readOutputPacket() throws IOException {
        return new OutputPacket(inputStream.readUTF());
    }
    private ErrorPacket readErrorPacket() throws IOException {
        return new ErrorPacket(inputStream.readUTF());
    }
}
