import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class server2 {
	public static void main(String argv[]) throws Exception {
		int serverPort = 7000;
		Long clientNumberOfMegabytes;
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];

		//Socket erstellen
		DatagramSocket datagramSocket = new DatagramSocket(serverPort);
		System.out.println("Server2 gestartet. Warte auf eingehende Requests...");

		while (true) {
			//Datenpakete empfangen
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			datagramSocket.receive(receivePacket);

			//Client IP und Port ermitteln
			InetAddress clientAddress = receivePacket.getAddress();
			int clientSrcPort = receivePacket.getPort();

			//Anzahl der zu uebertragenden Megabytes ermitteln
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(receivePacket.getData())));
			clientNumberOfMegabytes = Long.parseLong(inFromClient.readLine());

			if (clientNumberOfMegabytes == 0) {
				break;
			}

			//Daten erzeugen und an Client senden
			for (long mb = 0; mb < clientNumberOfMegabytes; mb++) {
				long megabyteStartTimeMilli = System.currentTimeMillis();
				for (long segment = 1; segment < 1024; segment++) {
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientSrcPort);
					datagramSocket.send(sendPacket);
				}
				double transferDurationMB = System.currentTimeMillis() - megabyteStartTimeMilli;
				System.out.printf("1 MB (genauer MiB) gesendet in: %.0f ms, Rate: %.0f Bytes/s (MB: %d)\n", transferDurationMB, ((1024 * 1024) / (transferDurationMB / 1000)), mb);
			}
			System.out.print("\n");
			byte[] finishLabel = (new String("done")).getBytes();
			DatagramPacket sendPacket = new DatagramPacket(finishLabel, finishLabel.length, clientAddress, clientSrcPort);
			datagramSocket.send(sendPacket);
		}
		datagramSocket.close();
	}
}
