package com.example.demo.netty;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TextServer {

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(7777);

        System.out.println("channel: " + server.getChannel());
        System.out.println("inetAddress: " + server.getInetAddress());
        System.out.println("localPort: " + server.getLocalPort());
        System.out.println("localSocketAddress: " + server.getLocalSocketAddress());

        while(true) {

            Socket socket = server.accept();

            new Thread(() -> {
                BufferedReader input = null;
                PrintStream out = null;
                try {
                    // 读取客户端数据
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

//                    String clientInputStr = input.readLine();
//
//                    System.out.println("message from client: " + clientInputStr);

                    out = new PrintStream(socket.getOutputStream());

                    out.println("test message from server to client");

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        input.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

}
