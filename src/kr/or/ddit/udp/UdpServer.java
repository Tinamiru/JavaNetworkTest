package kr.or.ddit.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UdpServer {
	private DatagramSocket ds;
	private DatagramPacket dp;
	private byte[] msg;

	public UdpServer() {
		try {
			ds = new DatagramSocket(8888);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 시작 메소드
	public void start() throws IOException {
		while (true) {
			// 데이터를 수신하기 위한 패킷을 생성한다.
			msg = new byte[1];
			dp = new DatagramPacket(msg, msg.length);

			System.out.println("패킷 수신 대기중...");

			// 패킷을 통해 데이터를 수신(receive)한다.
			ds.receive(dp);

			System.out.println("패킷 수신 완료...");

			// 수신한 패킷으로부터 client의 IP주소와 Port번호를 얻는다.
			InetAddress addr = dp.getAddress();
			int port = dp.getPort();

			// 서버와 현재 시간을 시분초 형태[hh:mm:ss]로 반환한다.
			SimpleDateFormat sdf = new SimpleDateFormat("[hh:mm:ss");
			String time = sdf.format(new Date());
			msg = time.getBytes(); // 시간문자열을 바이트 배열로 변환
			
			// 패킷을 생성해서 client에게 전송(send)한다.
			dp = new DatagramPacket(msg, msg.length, addr, port);
			ds.send(dp); // 전송하기

		}
	}
}
