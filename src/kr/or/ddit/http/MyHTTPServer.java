package kr.or.ddit.http;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.util.StringTokenizer;


/**
 * 간단한 웹서버 예제
 * 
 * @author JayAl
 *
 */
public class MyHTTPServer {
	private final int port = 80;
	private final String encoding = "UTF-8";

	public void start() {
		System.out.println("HTTP 서버가 시작되었습니다.");

		try (ServerSocket server = new ServerSocket(this.port)) {

			while (true) {
				try {
					Socket socket = server.accept();

					HttpHandler handler = new HttpHandler(socket);

					new Thread(handler).start();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * HTTP 요청 처리를 위한 Runnable 클래스
	 */
	private class HttpHandler implements Runnable {
		private final Socket socket;

		public HttpHandler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			OutputStream out = null;
			BufferedReader br = null;

			try {
				out = new BufferedOutputStream(socket.getOutputStream());
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				// Request Line 가져우기
				String reqLine = br.readLine();
				printMsg("Request Line", reqLine);

				// 요청헤더 정보 파싱하기
				StringBuilder sb = new StringBuilder();
				while (true) {
					String str = br.readLine();

					if (str.equals(""))
						break;

					sb.append(str + "\n");

				}

				// 헤더 정보 (Request Line + Header)
				String reqHeaderStr = sb.toString();
				printMsg("요청 헤더", reqHeaderStr);

				String reqPath = ""; // 요청 경로

				StringTokenizer st = new StringTokenizer(reqLine);
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					if (token.startsWith("/")) {
						reqPath = token;
						break;
					}
				}

				String filePath = "./Webapp" + reqPath;

				// 해당 파일 이름을 이용하여 Content-type 정보 추출하기
				String contentType = URLConnection.getFileNameMap().getContentTypeFor(filePath);

				// CSS파일인 경우 인식이 안되서 추가함.
				if (contentType == null && filePath.endsWith(".css")) {
					contentType = "text/css";
				}

				File file = new File(filePath);
				if (!file.exists()) {
					makeErrorPage(out, 040, "Not Found");
					return;
				}

				byte[] body = makeResponseBody(filePath);

				byte[] header = makeResponseHeader(body.length, contentType);

				// 요청헤더가 Http/1.0이나 그 이후의 버전을 지원할 경우
				// 헤더 정보를 전송한다.

				if (reqLine.indexOf("HTTP/") != -1) {
					out.write(header); // 응답 헤더 보내기
				}

				// 응답내용 보내기 전 반드시 Empty Line을 보내야 한다.
				out.write("\r\n\r\n".getBytes()); // Empty Line 보내기

				out.write(body); // 응답 내용 보내기

				out.flush(); //

				System.out.println("요청 헤더 :\n" + reqHeaderStr);
				System.out.println("---------------------------------");

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close(); // 소켓 닫기(연결 끊기)
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}

		/**
		 * 에러 페이지 생성하기
		 * 
		 * @param out
		 * @param statusCode
		 * @param errMsg
		 */
		private void makeErrorPage(OutputStream out, int statusCode, String errMsg) {
			String statusLine = "HTTP/1.1" + " " + statusCode + " " + errMsg;

			try {
				out.write(statusLine.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void printMsg(String title, String content) {
			System.out.println("===========================================");
			System.out.println(title);
			System.out.println("===========================================");
			System.out.println(content);
			System.out.println("-------------------------------------------");
		}
	}

	/**
	 * 응답헤더 생성하기
	 * 
	 * @param length      응답내용 크기
	 * @param contentType MIME 타입
	 * @return 바이트 배열
	 */
	private byte[] makeResponseHeader(int length, String contentType) {
		String header = "HTTP/1.1 200 OK\r\n" + "Server: MyHTTPServer 1.0\r\n" + "Content-length: " + length + "\r\n"
				+ "Content-type: " + contentType + "; charset=" + this.encoding;

		return header.getBytes();
	}

	/**
	 * 응답 내용 생성하기
	 * 
	 * @param filePath 응답으로 사용할 파일 경로
	 * @return 바이트 배열
	 */
	private byte[] makeResponseBody(String filePath) {
		FileInputStream fis = null;
		byte[] data = null;

		try {
			File file = new File(filePath);
			data = new byte[(int) file.length()];

			fis = new FileInputStream(file);
			fis.read(data);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
		return data;
	}

	public static void main(String[] args) {
		new MyHTTPServer().start();
	}
}
