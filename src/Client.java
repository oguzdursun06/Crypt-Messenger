import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import java.awt.Font;
import javax.swing.JPanel;
import java.awt.SystemColor;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

public class Client {

	JLabel lblNewLabel ;
	private JFrame frame;
	private JTextArea textField;
	private String encryptedmsg;
	private static JTextArea textField_1;
	private static JTextArea textArea;
	private boolean Connected=false;
	static Scanner scn;
	static DataInputStream dis;
	static DataOutputStream dos ;
	private String nickname;
	private Socket s;
	JButton Disconnect = null;
	JRadioButton Aesbtn;
	JRadioButton Desbtn;
	JRadioButton ofbtn;
	JRadioButton cbcbtn;
	SecretKey AESKey;
	SecretKey DESKey;
	byte[]  IVectorAES;
	byte[] 	IVectorDES;
	boolean Keysend=false;
	boolean canSend=false;
	String Mode;
	String algorithm;
	static Client client;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					client = new Client();

					client.frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Client() {
		initialize();
	}
	public void Write(String message) throws IOException {
		if(message.isEmpty()) {
			return;
		}
		dos.writeUTF(message);
	}
	public void Connection() throws IOException {

		try {
			s = new Socket("localhost", 8008);
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(frame,"Can not Connect to Server\nPlease Try Again Later");
			return;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame,"Can not Connect to Server\nPlease Try Again Later");
			return;
		}
		dis = new DataInputStream(s.getInputStream());
		dos = new DataOutputStream(s.getOutputStream());
		Connected=true;
		lblNewLabel.setText("Connected "+nickname);
	}

	@SuppressWarnings("static-access")
	public void Disconnection() {
		try {
			s.close();
			dis.close();
			dos.flush();
			dos.close();
			nickname="";
			lblNewLabel.setText("Not Connected");
			client.frame.dispose();
			client = new Client();
			client.main(null);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void start() {
		try {

			new Thread() {
				@Override
				public void run() {
					try {

						while(true) {
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if(Connected) {
								if(Keysend==false) {
									byte[] byteAESKey = new byte[16];
									IVectorAES = new byte[16];
									byte[] byteDESKey = new byte[8];
									IVectorDES= new byte[8];
									dis.read(byteAESKey);
									dis.read(IVectorAES );
									dis.read(byteDESKey);
									dis.read(IVectorDES);
									AESKey = new SecretKeySpec(byteAESKey, 0, byteAESKey.length, "AES");
									DESKey = new SecretKeySpec(byteDESKey,0,byteDESKey.length,"DES");
									Keysend=true;
								}
								else {
									if(dis.available()>0) {
										String[] datas=dis.readUTF().split("~");
										try {
											String plaintext=Decryption(datas[0],datas[1],datas[3]);
											textArea.append(datas[3]+"\n"+datas[2]+"> "+plaintext+"\n");
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

									}
								}
							}
						}
					} catch (IOException e) {
						return;
					}
				}
			}.start();
		} catch (Exception err) {
			return;
		}
	}

	public String Decryption(String mode, String algorithm,String CipherText) throws Exception {
		if(mode.equals("AES")) {
			if(algorithm.equals("cbc")) {
				CbcOfAes cbc_aes= new CbcOfAes();
				return cbc_aes.decrypt(CipherText,IVectorAES,AESKey);
			}
			else if(algorithm.equals("ofb")) {
				OfbOfAes ofb_aes=new OfbOfAes();
				return ofb_aes.decrypt(CipherText,IVectorAES,AESKey);
			}
		}
		else if(mode.equals("DES")) {
			if(algorithm.equals("cbc")) {
				CbcOfDes cbc_des= new CbcOfDes();
				return cbc_des.doDecryption(CipherText,DESKey,IVectorDES);
			}
			else if(algorithm.equals("ofb")) {
				OfbOfDes ofb_des=new OfbOfDes(DESKey,IVectorDES);
				return ofb_des.doDecryption(CipherText,DESKey,IVectorDES);
			}
		}

		return CipherText;
	}

	public void Encryption() throws Exception {
		if(!Aesbtn.isSelected()&&!Desbtn.isSelected()&&!ofbtn.isSelected()&&!cbcbtn.isSelected()) {
			JOptionPane.showMessageDialog(frame,"Please select encryption algorithm and mode");
		}
		else if(textField.getText().isEmpty()) {
			JOptionPane.showMessageDialog(frame,"Please Enter a Message to Encrypt");
			return;
		}

		if(Aesbtn.isSelected()) {
			if(cbcbtn.isSelected()) {
				CbcOfAes aes_cbc=new CbcOfAes();
				Mode="AES";
				algorithm="cbc";
				textField_1.setText(aes_cbc.encrypt(textField.getText(), IVectorAES, AESKey));
				canSend=true;
			}
			else if(ofbtn.isSelected()) {
				OfbOfAes aes_ofb=new OfbOfAes();
				Mode="AES";
				algorithm="ofb";
				textField_1.setText(aes_ofb.encrypt(textField.getText(), IVectorAES, AESKey));
				canSend=true;
			}

		}
		else if(Desbtn.isSelected()) {

			if(cbcbtn.isSelected()) {
				CbcOfDes des_cbc=new CbcOfDes();
				try {
					Mode="DES";
					algorithm="cbc";
					textField_1.setText(des_cbc.doEncryption(textField.getText(),DESKey,IVectorDES));
					canSend=true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(ofbtn.isSelected()) {
				Mode="DES";
				algorithm="ofb";
				OfbOfDes des_ofb = new OfbOfDes(DESKey,IVectorDES);
				try {
					textField_1.setText(des_ofb.doEncryption(textField.getText()));
					canSend=true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	private void initialize() {

		frame = new JFrame("Crypto Messenger");
		frame.getContentPane().setForeground(new Color(245, 222, 179));
		frame.setBounds(100, 100, 771, 668);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setBackground(new Color(51, 167, 255));

		JButton SendButton = new JButton();
		SendButton.setFont(new Font("Calibra", Font.BOLD,15));
		SendButton.setText("Send");
		SendButton.setEnabled(false);
		SendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(textField.getText().isEmpty()) {
						return;
					}
					Write(Mode+"~"+algorithm+"~"+nickname+"~"+textField_1.getText());
					textField.setText("");
					textField_1.setText("");
					canSend=false;
					SendButton.setEnabled(false);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		SendButton.setBounds(618, 520, 113, 64);
		frame.getContentPane().add(SendButton);

		textArea = new JTextArea();
		textArea.setForeground(Color.BLACK);
		textArea.setEditable(false);
		textArea.setBounds(21, 41, 594, 444);
		JScrollPane scroll=new JScrollPane();
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setBounds(16,105,715,358);
		scroll.getViewport().setBackground(Color.WHITE);
		scroll.setViewportView(textArea);
		frame.getContentPane().add(scroll);


		textField = new JTextArea();
		textField.setBounds(9, 506, 235, 84);
		JScrollPane scroll_2=new JScrollPane();
		scroll_2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll_2.setBounds(9, 506, 235, 84);
		scroll_2.getViewport().setBackground(Color.WHITE);
		scroll_2.setViewportView(textField);
		frame.getContentPane().add(scroll_2);

		textField_1 = new JTextArea();
		textField_1.setEditable(false);
		textField_1.setForeground(Color.black);
		textField_1.setBounds(267, 506,200, 84);
		JScrollPane scroll_3=new JScrollPane();
		scroll_3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll_3.setBounds(267, 506,200, 84);
		scroll_3.getViewport().setBackground(Color.WHITE);
		scroll_3.setViewportView(textField_1);
		frame.getContentPane().add(scroll_3);

		JSeparator separator = new JSeparator();
		separator.setBounds(0, 603, 755, 2);
		frame.getContentPane().add(separator);

		lblNewLabel= new JLabel("Not Connected");
		lblNewLabel.setBounds(0, 608, 375, 23);
		frame.getContentPane().add(lblNewLabel);
		JButton EncryptButton = new JButton();
		EncryptButton.setFont(new Font("Calibra", Font.BOLD,15));
		EncryptButton.setText("Encrypt");
		EncryptButton.setBounds(496, 520, 112, 64);
		EncryptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(Connected==false) {
						JOptionPane.showMessageDialog(frame,"You are not Connected to a Server");
						return;
					}
					Encryption();
					if(canSend==true) {
						SendButton.setEnabled(true);
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		frame.getContentPane().add(EncryptButton);

		ButtonGroup Alg = new ButtonGroup();
		ButtonGroup Mode = new ButtonGroup();

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(71, 506, -41, -10);
		frame.getContentPane().add(separator_1);

		JPanel panel = new JPanel();
		panel.setBackground(SystemColor.controlHighlight);
		panel.setBounds(9, 487, 235, 21);
		frame.getContentPane().add(panel);

		JLabel lblNewLabel_1 = new JLabel("Text");
		panel.add(lblNewLabel_1);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(SystemColor.controlHighlight);
		panel_1.setBounds(267, 487, 200, 21);
		frame.getContentPane().add(panel_1);

		JLabel lblNewLabel_2 = new JLabel("Crypted Text");
		panel_1.add(lblNewLabel_2);
		JButton ConnectButton = new JButton("\\\u25B6".substring(1)+" Connect");
		ConnectButton.setBounds(548, 10, 183, 29);
		frame.getContentPane().add(ConnectButton);

		Disconnect = new JButton("\u274C Disconnect");
		Disconnect.setBounds(548, 53, 183, 29);
		frame.getContentPane().add(Disconnect);
		Disconnect.setEnabled(false);

		JPanel panel_2 = new JPanel();
		panel_2.setForeground(new Color(245, 222, 179));
		panel_2.setBorder(new LineBorder(UIManager.getColor("InternalFrame.inactiveTitleGradient"), 2));
		panel_2.setBackground(new Color(250, 250, 150));
		panel_2.setBounds(31, 10, 213, 72);
		frame.getContentPane().add(panel_2);
		panel_2.setLayout(null);
		Aesbtn=new JRadioButton("AES");
		Aesbtn.setBounds(6, 45, 78, 21);
		panel_2.add(Aesbtn);
		Aesbtn.setBackground(new Color(250, 250, 150));
		Alg.add(Aesbtn);

		Desbtn= new JRadioButton("DES");
		Desbtn.setBounds(106, 45, 78, 21);
		panel_2.add(Desbtn);
		Desbtn.setBackground(new Color(250, 250, 150));
		Alg.add(Desbtn);

		JLabel lblNewLabel_3 = new JLabel("Method");
		lblNewLabel_3.setBounds(6, 10, 43, 13);
		panel_2.add(lblNewLabel_3);

		JSeparator separator_2 = new JSeparator();
		separator_2.setForeground(new Color(205, 133, 63));
		separator_2.setBounds(59, 20, 144, 21);
		panel_2.add(separator_2);

		JPanel panel_3 = new JPanel();
		panel_3.setLayout(null);
		panel_3.setForeground(new Color(245, 222, 179));
		panel_3.setBorder(new LineBorder(UIManager.getColor("InternalFrame.inactiveTitleGradient"), 2));
		panel_3.setBackground(new Color(250, 250, 150));
		panel_3.setBounds(254, 10, 213, 72);
		frame.getContentPane().add(panel_3);

		JLabel lblMode = new JLabel("Mode");
		lblMode.setBounds(6, 10, 43, 13);
		panel_3.add(lblMode);

		JSeparator separator_3 = new JSeparator();
		separator_3.setForeground(new Color(205, 133, 63));
		separator_3.setBounds(41, 20, 162, 21);
		panel_3.add(separator_3);

		ofbtn = new JRadioButton("OFB");
		ofbtn.setBounds(6, 45, 55, 21);
		panel_3.add(ofbtn);
		ofbtn.setBackground(new Color(250, 250, 150));

		Mode.add(ofbtn);

		cbcbtn = new JRadioButton("CBC");
		cbcbtn.setBounds(107, 45, 55, 21);
		panel_3.add(cbcbtn);
		cbcbtn.setBackground(new Color(250, 250, 150));
		Mode.add(cbcbtn);
		Disconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Disconnection();
				ConnectButton.setEnabled(true);
				Disconnect.setEnabled(false);
			}
		});

		ConnectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nickname=JOptionPane.showInputDialog("Enter user name :");
				try {
					Connection();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(Connected=true) {
					ConnectButton.setEnabled(false);
					Disconnect.setEnabled(true);
				}
			}
		});

		start();
	}
}
