package kr.or.ddit.tcp;

import java.io.IOException;
import java.net.Socket;

public class TcpChatClient {
    public static void main(String[] args) {
        Socket socket;
        try {
            socket = new Socket("localhost", 7777);

            System.out.println("서버에 연결되었습니다.");

            Sender sender = new Sender(socket);
            Receiver receiver = new Receiver(socket);

            sender.start();
            receiver.start();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
