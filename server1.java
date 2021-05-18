import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

class server1 {
	public static void main(String argv[]) throws Exception {
		int serverPort = 6000;

		//Socket erstellen
		ServerSocket serverSocket = new ServerSocket(serverPort);
		System.out.println("Server1 gestartet. Warte auf eingehende Requests...");

		while (true) {
			//Eingehende Verbindungen akzeptieren und ConnectionSocket erstellen + input/output Stream
			Socket connectionSocket = serverSocket.accept();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			BufferedWriter outToClient = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));

			char[] buffer = new char[1024]; //Puffer anlegen
			Long clientNumberOfMegabytes = Long.parseLong(inFromClient.readLine()); //Die Anzahl der zu uebertragenden Megabytes

			if (clientNumberOfMegabytes == 0) {
				connectionSocket.close();
				break;
			}

			System.out.println("Request von TCPPerfClient " + connectionSocket.getInetAddress() + ":" + connectionSocket.getPort() + " empfangen. Erzeuge und sende " + clientNumberOfMegabytes + "MB ...");

			//Daten erzeugen und an Client senden
			for (long mb = 0; mb < clientNumberOfMegabytes; mb++) {
				long megabyteStartTimeMilli = System.currentTimeMillis();
				for (long segment = 1; segment < 1024; segment++) {
					long segmentStartTimeNano = System.nanoTime();
					outToClient.write(buffer);

					if (clientNumberOfMegabytes == 1) {
						double transferDurationSegment = System.nanoTime() - segmentStartTimeNano;
						System.out.println(mb + "," + segment + "," + transferDurationSegment);
					}

				}
				double transferDurationMB = System.currentTimeMillis() - megabyteStartTimeMilli;
				System.out.printf("1 MB (genauer MiB) gesendet in: %.0f ms, Rate: %.0f Bytes/s (MB: %d)\n", transferDurationMB, ((1024 * 1024) / (transferDurationMB / 1000)), mb);
			}
			System.out.print("\n");

			//Nach der Uebertragung ConnectionSocket schliessen
			connectionSocket.close();
		}
		serverSocket.close();
	}
}
