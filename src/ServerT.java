import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class ServerT extends Thread {
	ServerSocket MainServer=null;
	Socket socket =null;
	private final int  port=8008;
	SecretKey KeyAES;
	SecretKey KeyDES;
	byte[] IVDES;
	byte[] IVAES;
	private ArrayList<ThreadHandler> clients = new ArrayList<>();
	public ServerT() throws NoSuchAlgorithmException {
		try {
			KeyAES=getAESKey();
			KeyDES=getDESKey();
			IVDES=getDESVector();
			IVAES=getAESVector();
			MainServer = new ServerSocket(8008);
			Writer fileWriter = new FileWriter("log.txt", true);
			fileWriter.write("Aes Key: "+ Base64.getEncoder().encodeToString(KeyAES.getEncoded())+"\n");
			fileWriter.write("Aes Vector: "+Base64.getEncoder().encodeToString(IVAES)+"\n");
			fileWriter.write("Des Key: "+Base64.getEncoder().encodeToString(KeyDES.getEncoded())+"\n");
			fileWriter.write("Des Vector: "+Base64.getEncoder().encodeToString(IVDES)+"\n");
			System.out.print("Aes Key: "+Base64.getEncoder().encodeToString(KeyAES.getEncoded())+"\n");
			System.out.print("Aes Vector: "+Base64.getEncoder().encodeToString(IVAES)+"\n");
			System.out.print("Des Key: "+Base64.getEncoder().encodeToString(KeyDES.getEncoded())+"\n");
			System.out.print("Des Vector: "+Base64.getEncoder().encodeToString(IVDES)+"\n");
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Can not Create ServerSocket");
		}


	}

	@Override
	public void run() {
		while(true) {
			try {
				socket=MainServer.accept();
				ThreadHandler clientthread = new ThreadHandler(socket,this,KeyAES,KeyDES,IVDES,IVAES);
				clients.add(clientthread);
				clientthread.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}

		}
	}

	public static SecretKey getAESKey() throws NoSuchAlgorithmException {
		SecureRandom rand = new SecureRandom();
		KeyGenerator generator = KeyGenerator.getInstance("AES");
		generator.init(128, rand);
		return generator.generateKey();
	}
	public static byte[] getAESVector() {
		byte[] aesKey = new byte[128 / 8];
		SecureRandom rand = new SecureRandom();
		rand.nextBytes(aesKey);
		return aesKey;
	}
	public static byte[] getDESVector() {
		byte[] aesKey = new byte[8];
		SecureRandom rand = new SecureRandom();
		rand.nextBytes(aesKey);
		return aesKey;
	}
	public static SecretKey getDESKey() throws NoSuchAlgorithmException {
		KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
		keygenerator.init(56,new SecureRandom());
		return keygenerator.generateKey();
	}
	public ArrayList<ThreadHandler> getClients() {
		return clients;
	}

	public void setClients(ArrayList<ThreadHandler> clients) {
		this.clients = clients;
	}

}
