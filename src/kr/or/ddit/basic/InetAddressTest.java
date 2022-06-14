package kr.or.ddit.basic;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressTest {
	public static void main(String[] args) throws UnknownHostException {
//		InetAddress 클래스 => IP주소를 다루기 위한 클래스
//				
//		getByName()은 www.naver.com 또는 SEM-PC와 같은 머신이름이나
//		IP주소를 파라미터로 이용하여 유효한 InetAddress 객체를 제공한다.
//		IP 주소 자체를 넣으면 구성 자체의 유효성 정도만 체크가 이루어 진다.
//		
//		네이버 사이트의 IP 정보 가져오기
		InetAddress naverIP = InetAddress.getByName("www.naver.com");
		System.out.println("Host Name => " + naverIP.getHostName());
		System.out.println("Host Address => " + naverIP.getHostAddress());
		System.out.println();

		// 자기 자신의 컴퓨터 주소 가져오기
		InetAddress localIp = InetAddress.getLocalHost();
		System.out.println("내 컴퓨터의 Host Name => " + localIp.getHostName());
		System.out.println("내 컴퓨터의 Host Address => " + localIp.getHostAddress());
		System.out.println();

		// 쌤컴의 컴퓨터 주소 가져오기
		InetAddress samIP = InetAddress.getByAddress(getBytesbyInetAddress("192.168.141.3"));
		System.out.println("쌤컴의 Name => " + samIP.getHostName());
		System.out.println("쌤컴의 Address => " + samIP.getHostAddress());
		System.out.println();

		// IP 주소가 여러개인 호스트의 정보 가져오기
		InetAddress[] naverIps = InetAddress.getAllByName("www.naver.com");
		for (InetAddress iAddr : naverIps) {
			System.out.println(iAddr.toString());
		}
	}

	public static byte[] getBytesbyInetAddress(String ip) {
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			return new byte[0];
		}
		return addr.getAddress();

	}
}
