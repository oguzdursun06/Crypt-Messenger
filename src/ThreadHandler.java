import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKey;

public class ThreadHandler extends Thread {
	Socket socket=null;
	ServerT Server=null;
	DataInputStream dis;
	private boolean KeySend=false;
	DataOutputStream dos;
	SecretKey KeyAES;
	SecretKey KeyDES;
	byte[] IVDES;
	byte[] IVAES;
	ArrayList<ThreadHandler> clientlist=null;
	public ThreadHandler(Socket socket, ServerT serverT,SecretKey KeyAES,SecretKey KeyDES,byte[] IVDES , byte[] IVAES) {
		// TODO Auto-generated constructor stub
		this.KeyAES=KeyAES;
		this.KeyDES=KeyDES;
		this.IVAES=IVAES;
		this.IVDES=IVDES;
		this.socket=socket;
		this.Server=serverT;
	}
	@Override
	public void run() {

		while(true) {
			try {
				dis= new DataInputStream(socket.getInputStream());
				dos=new DataOutputStream(socket.getOutputStream());
				if(KeySend==false) {

					dos.write(KeyAES.getEncoded());
					dos.write(IVAES);
					dos.write(KeyDES.getEncoded());
					dos.write(IVDES);
					KeySend=true;
				}
				String Clientmsg=dis.readUTF();
				String[]Clientdatas=Clientmsg.split("~");
				System.out.println(Clientdatas[2]+"> "+Clientdatas[3]);
				Writer fileWriter = new FileWriter("log.txt", true);
				fileWriter.write(Clientdatas[2]+"> "+Clientdatas[3]+"\n");
				fileWriter.close();
				clientlist=Server.getClients();
				for(ThreadHandler client : clientlist){
					client.dos.writeUTF(Clientmsg);
				}
			} catch (IOException e) {
				try {
					clientlist.remove(this);
					Server.setClients(clientlist);
					socket.close();

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}


		}

	}

}
