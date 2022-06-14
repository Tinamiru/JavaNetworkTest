package kr.or.ddit.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MultiChatServer {
	// 대화명, 클라이트의 Socket을 저장하기 위한 Map변수 선언
	Map<String, Socket> clients;

	public MultiChatServer() {
		// 동기화 처리가 가능하도록 Map객체 생성하기
		clients = Collections.synchronizedMap(new HashMap<String, Socket>());
	}

	// 서버 시작 메서드
	public void serverStart() {
		ServerSocket server = null;
		Socket socket = null;

		try {
			System.out.println("서버가 시작되었습니다.");

			server = new ServerSocket(7777);

			while (true) {

				// 클라이언트의 접속을 대기한다.
				socket = server.accept();

				System.out.println("[" + socket.getInetAddress() + " : " + socket.getPort() + "에서 접속하였습니다.");

				// 메세지 전송 처리를 하는 스레드 객체 생성 및 실행

				ServerReceiver receiver = new ServerReceiver(socket);
				receiver.start();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 서버소켓 닫기
			if (server != null) {
				try {
					server.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
	}

	/**
	 * 대화방 즉, Map에 저장된 전체 유저에게 안내메시지를 전송하는 메서드
	 * 
	 * @param msg 안내 메시지
	 */
	public void sendMessage(String msg) {
		Iterator<String> it = clients.keySet().iterator();
		while (it.hasNext()) {
			try {
				String name = it.next(); // 대화명(Key) 구하기

				// 대화명에 해당하는 Socket 객체에서 OutputStream 객체 구하기
				DataOutputStream dos = new DataOutputStream(clients.get(name).getOutputStream());
				dos.writeUTF(msg);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * 대화방 즉, Map에 저장된 전체 유저에게 안내메시지를 전송하는 메서드
	 * 
	 * @param msg  안내 메시지
	 * @param from
	 */
	public void sendMessage(String msg, String from) {
		sendMessage("[" + from + "]" + msg);
	}

	/**
	 * 서버에서 클라이언트로 메세지를 전송할 Thread 클래스를 Inner클래스로 정의한다. (Inner클래스에서는 부모 클래스의 멤버들을 직접
	 * 사용할 수 있다.)
	 */

	class ServerReceiver extends Thread {

		private Socket socket;
		private DataInputStream dis;
		private String name;

		public ServerReceiver(Socket socket) {
			this.socket = socket;

			try {
				dis = new DataInputStream(socket.getInputStream());

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void run() {
			try {
				// 서버에서는 클라이언트가 보내는 최초의 메시지 즉, 대화명을 수신해야한다.
				name = dis.readUTF();

				// 대화명을 받아서 다른 모든 클라이언트에게 대화방 참여
				// 메세지를 보낸다.
				sendMessage("#" + name + "님이 입장했습니다.");

				// 대화명과 소켓정보를 Map에 저장한다.
				clients.put(name, socket);

				System.out.println("현재 서버 접속자 수는 " + clients.size() + "명 입니다");

				// 이 후의 메세지 처리는 반복문으로 처리한다.
				// 한 클라이언트가 보낸 메세지를 다른 모든 클라이언트에게 보내준다.

				while (dis != null) {
					sendMessage(dis.readUTF(), name);
				}
			} catch (IOException e) {
//				e.printStackTrace();
			} finally {
				// 이 finally 영역이 실행된다는 의미는 클라이언트의 접속이 종료되었다는 의미이다.

				sendMessage(name + "님이 나가셨습니다.");

				// Map에서 해당 대화명을 삭제한다.
				clients.remove(name);

				System.out.println("[" + socket.getInetAddress() + " : " + socket.getPort() + "] 에서 접속을 종료했습니다.");
				System.out.println("현재 접속자 수는 " + clients.size() + "명 입니다.");
			}
		}
	}
	public static void main(String[] args) {
		new MultiChatServer().serverStart();
		
	}
}