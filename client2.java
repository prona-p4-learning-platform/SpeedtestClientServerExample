import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class client2 {
	public static void main(String argv[]) throws Exception {
		String serverIP;
		int serverPort = 7000;
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		Long clientNumberOfMegabytes = 1000L;
		long totalBytesReceived = 0;


		if(argv.length < 2) {
			System.out.println("Bitte IP und Anzahl der zu uebertragenden Megabytes angeben");
			System.out.println("Bsp: 'java client2 172.17.0.120 1000'");
		} else {
			//ServerIP und zu uebertragende Megabyte festlegen
			serverIP = argv[0];
			clientNumberOfMegabytes = Long.parseLong(argv[1]);

			//Socket erstellen
			DatagramSocket clientSocket = new DatagramSocket();
			clientSocket.setSoTimeout(5000);

			//Startzeit der Uebertragung
			long timestampStart = System.currentTimeMillis();
			System.out.println("Zeit vor Versand = " + df.format(new Date(timestampStart)));

			//Server mitteilen wie viel Megabytes uebertragen werden sollen
			byte[] outbuffer = (new String(clientNumberOfMegabytes + "\n")).getBytes();
			DatagramPacket sendPacket = new DatagramPacket(outbuffer, outbuffer.length, InetAddress.getByName(serverIP), serverPort);
			clientSocket.send(sendPacket);

			//Daten empfangen
			while (true) {
				byte[] receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				try {
					clientSocket.receive(receivePacket);
				} catch (SocketTimeoutException ste) {
					System.out.println("Timeout");
					break;
				}
					String receivedString = new String(receiveData);
				if (receivedString.contains("done")) {
					break;
				} else {
					totalBytesReceived += receivePacket.getLength();
				}
			}

			//Auswertung
			long timestampEnd = System.currentTimeMillis();
			System.out.println("Zeit nach Empfang = " + df.format(new Date(timestampEnd)));
			System.out.printf("Insgesamt %d Bytes von %d (%d MB) empfangen.\n", totalBytesReceived - 4, (1024 * 1024) * clientNumberOfMegabytes, clientNumberOfMegabytes);
			clientSocket.close();
		}
	}
}
