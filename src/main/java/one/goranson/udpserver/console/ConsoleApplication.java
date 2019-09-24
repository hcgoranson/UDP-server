package one.goranson.udpserver.console;

import one.goranson.udpserver.common.UdpServer;

public class ConsoleApplication {

    public static void main(String[] args) {
        new UdpServer(System.out::println).start();
    }
}
