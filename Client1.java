import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


class Client1 {
	public static void main(String argv[]) throws Exception {
		int serverPort = 6000;
		String serverIP = "localhost";
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		Long clientNumberOfMegabytes = 1000L;
		char[] buffer = new char[1024 * 1024];
		long totalBytesReceived = 0;


		if (argv.length < 2) {
			System.out.println("Bitte IP und Anzahl der zu uebertragenden Megabytes angeben");
			System.out.println("Beispiel: 'java client1 172.17.0.120 1000'");
		} else {
			//ServerIP und zu uebertragende Megabyte festlegen
			serverIP = argv[0];
			clientNumberOfMegabytes = Long.parseLong(argv[1]);


			//Socket erstellen + input/output Stream
			Socket clientSocket = new Socket(serverIP, serverPort);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			//Startzeit der Uebertragung
			long timestampStart = System.currentTimeMillis();
			System.out.println("Zeit vor Versand = " + df.format(new Date(timestampStart)));

			//Server mitteilen wie viel Megabytes uebertragen werden sollen
			outToServer.writeBytes(clientNumberOfMegabytes.toString() + "\n");

			//Daten empfangen
			int charsRead = inFromServer.read(buffer);
			while (charsRead != -1) {
				totalBytesReceived += charsRead;
				charsRead = inFromServer.read(buffer);
			}

			//Auswertung
			long timestampEnd = System.currentTimeMillis();
			System.out.println("Zeit nach Empfang = " + df.format(new Date(timestampEnd)));
			long duration = timestampEnd - timestampStart;
			System.out.println("Delay = " + duration + " ms");
			double seconds = (duration / 1000.0);
			double throughput = (totalBytesReceived / seconds);
			System.out.println("Rate = " + (long) throughput + " B/s");
			clientSocket.close();
		}
	}
}
