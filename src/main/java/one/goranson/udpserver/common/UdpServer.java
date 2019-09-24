package one.goranson.udpserver.common;

import lombok.Setter;

import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * A class for running a UDP server and print incoming messages to a give consumer <br/>
 * This is a convenient class to be used during development to check how the sent statsD messages <br/>
 * actually looks.
 */
public class UdpServer {
    private static final List<String> INCLUDE_LIST = new ArrayList<>();

    private ExecutorService executor;
    private final Consumer<String> consumer;
    private final AtomicInteger COUNTER = new AtomicInteger();
    private final static int PORT = 8125;
    @Setter
    private boolean addTimestamp = true;

    public UdpServer(Consumer<String> consumer) {
        this.consumer = consumer;
        executor = Executors.newSingleThreadExecutor();
    }

    public void start() {
        executor.submit(this::execute);
    }

    public void updateFilter(String filter) {
        INCLUDE_LIST.clear();
        if (filter != null && !"".equals(filter))  {
            INCLUDE_LIST.add(filter);
        }
    }

    public void execute() {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            while (executor != null) {
                byte[] packetBuffer = new byte[2024];
                final DatagramPacket packet = new DatagramPacket(packetBuffer, packetBuffer.length);
                // Blocks until a packet is received
                socket.receive(packet);
                final String receivedPacket = new String(packet.getData()).trim();

                if (!receivedPacket.isEmpty() && isIncluded(receivedPacket)) {
                    COUNTER.incrementAndGet();
                    String message = receivedPacket;
                    if (addTimestamp) {
                        final String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
                        message = String.format("[%s][%s] %s", timestamp, COUNTER.get(), message);
                    }
                   appendText(message);
                }
            }
        } catch (BindException bindException) {
            appendText("Failed to start the UDP Server due to '" + bindException.getMessage() + "'\n");
            appendText("Please close all running application using port 8125 and restart this application");
        } catch (Exception e) {
            appendText("Something wrong happen: Error message: '" + e.getMessage() + "'");
        }
    }

    private void appendText(String message) {
        consumer.accept(message);
    }

    private boolean isIncluded(String received) {

        if (INCLUDE_LIST.isEmpty()) {
            return true;
        }

        return INCLUDE_LIST.stream()
                .filter(included -> received.contains(included) || "*".equals(included))
                .findAny()
                .isPresent();
    }
}
