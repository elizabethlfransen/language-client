package io.github.elizabethlfransen.languageclient;

import io.github.elizabethlfransen.languageclient.packet.*;
import io.github.elizabethlfransen.languageclient.util.LambdaUtil;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.utils.AttributedString;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class LanguageClient implements Closeable {
    private Socket socket;
    private PacketReader packetReader;

    private DataOutputStream output;

    private Vector<Consumer<String>> outputHandlers = new Vector<>();
    private Vector<Consumer<String>> errorHandlers = new Vector<>();
    private BlockingQueue<LanguagePacket> packetQueue = new LinkedBlockingQueue<>();


    public LanguageClient() throws IOException {
        this(3420);
    }
    public LanguageClient(int port) throws IOException {
        socket = new Socket(InetAddress.getLocalHost(), port);
        output = new DataOutputStream(socket.getOutputStream());
        packetReader = new PacketReader(new DataInputStream(socket.getInputStream()));
        Thread packetReadThread = new Thread(LambdaUtil.runIgnoringExceptions(this::readPackets));
        packetReadThread.start();
    }

    private void readPackets() throws IOException {
        try {
            while (!socket.isClosed()) {
                LanguagePacket packet = packetReader.nextPacket();
                handlePacket(packet);
            }
        } catch (SocketException e) {
            if(!e.getMessage().equals("Socket closed"))
                throw e;
        } catch (EOFException ignored) {
        }
    }

    private void handlePacket(LanguagePacket packet) {
        if(packet instanceof ErrorPacket) {
            handleError((ErrorPacket) packet);
            return;
        }
        if(packet instanceof OutputPacket) {
            handleOutput((OutputPacket) packet);
            return;
        }
        packetQueue.add(packet);
    }

    private void handleError(ErrorPacket errorPacket) {
        errorHandlers.forEach(handler -> handler.accept(errorPacket.message));
    }
    private void handleOutput(OutputPacket outputPacket) {
        errorHandlers.forEach(handler -> handler.accept(outputPacket.line));
    }

    public void onError(Consumer<String> handler) {
        errorHandlers.add(handler);
    }

    public void onOutput(Consumer<String> handler) {
        outputHandlers.add(handler);
    }

    private <T extends LanguagePacket> T nextPacket() throws InterruptedException {
        LanguagePacket nextPacket = packetQueue.take();
        try {
            //noinspection unchecked
            return (T)nextPacket;
        } catch (ClassCastException e) {
            throw new IllegalStateException("Invalid packet received");
        }
    }

    public AttributedString highlight(String line) throws IOException, InterruptedException {
        output.writeUTF("highlight");
        output.writeUTF(line);
        return this.<HighlightingResultPacket>nextPacket().result;
    }

    public void interpret(String line) throws IOException {
        output.writeUTF("interpret");
        output.writeUTF(line);
    }

    public static void main(String[] args) throws IOException {
        LanguageClient client = new LanguageClient();
        client.onError(System.err::print);
        client.onOutput(System.out::println);
        LineReader reader = LineReaderBuilder.builder()
                .highlighter(new LanguageClientHighlighter(client))
                .build();
        while(true) {
            try {
                String line = reader.readLine("$ ");
                client.interpret(line);
            } catch (UserInterruptException ignored) {
                client.close();
                break;
            }
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
