package src;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.Arrays;

import java.net.Socket;

import java.awt.Dimension;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JList;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.BadPaddingException;

import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;

import utils.TCPSocketWrapper;

/**	Finestra per la chat. 	
 * 	Permette l'invio di messaggi criptati secondo l'algoritmo indicato(DES, AES, Triple DES) e una chiave immessa dall'utente e l'operazione inverse, cioè la decriptazione di un messaggio ricevuto. 
**/
public class ClientUI extends JFrame
{
	protected TCPSocketWrapper client;
	protected byte[] cryptedMessage = null;
	protected BoundPanel inboundPanel, outboundPanel;

	public static final String[] cipherAlgorithm 	= {"Nessuno", "DES", "AES", "DESede"};
	public static final int[] cipherKeySize 		= {0, 8, 32, 24};

	/**	Costruttore che inizializza la finestra (JFrame) e la socket addetta alle comunicazioni.
	 *	@param ipAddr  indirizzo ip della macchina server.
	 *	@param port    numero porta della socket della macchina server.
	 *  @param username nome dell'utente.
	**/
	public ClientUI(String ipAddr, int port, String username) throws IOException
	{
		super("SecureChat[Client] - Loggato come " + username);

		client = new TCPSocketWrapper(ipAddr, port);

		initComponent();

		client.addObjectHandler((Object obj, ObjectOutputStream out) ->
		{
			cryptedMessage = (byte[]) obj;
			inboundPanel.textArea.setText(new String(cryptedMessage));
		});
		client.setAutoReceive(true);

		setSize(new Dimension(800, 600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	
	/**	Costruttore utilizzato dalla macchina server per comunicare con il client.
	 *  @param socket tramite la quale avviene la comunicazione.
	**/
	public ClientUI(Socket socket) throws IOException
	{
		super("SecureChat[Server] - Chattando con il client");

		client = new TCPSocketWrapper(socket);

		initComponent();

		client.addObjectHandler((Object obj, ObjectOutputStream out) ->
		{
			cryptedMessage = (byte[]) obj;
			inboundPanel.textArea.setText(new String(cryptedMessage));
		});
		client.setAutoReceive(true);

		setSize(new Dimension(800, 600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	
	/**	Inizializza il pannello aggiungendo tutti i componenti e specifica la modalità con cui gestire gli eventi swing.
	**/	
	private void initComponent()
	{
		Border etchedBorder = BorderFactory.createEtchedBorder();
		outboundPanel = new BoundPanel();
		outboundPanel.setBorder(new TitledBorder(etchedBorder, "Invio messaggio"));
		outboundPanel.textArea.setBorder(new TitledBorder(etchedBorder, "Messaggio"));
		outboundPanel.button.setText("Cripta ed invia");
		outboundPanel.button.addActionListener((ActionEvent e) ->
		{
			String message = outboundPanel.textArea.getText();
			String key = outboundPanel.keyField.getText();
			if(message.length() != 0)
			{
				try
				{
					int algorithm = outboundPanel.algorithmList.getSelectedIndex();
					if(algorithm > 0)
					{
						MessageDigest md5 = MessageDigest.getInstance("MD5");
						byte[] digestedKey = md5.digest(key.getBytes());
						Cipher cipher = Cipher.getInstance(cipherAlgorithm[algorithm]);
						SecretKeySpec secretKey = new SecretKeySpec(Arrays.copyOf(digestedKey, cipherKeySize[algorithm]), cipherAlgorithm[algorithm]);
						cipher.init(Cipher.ENCRYPT_MODE, secretKey);
						client.writeObject(cipher.doFinal(message.getBytes()));
					}
					else
						client.writeObject(message.getBytes());	
				}
				catch(InvalidKeyException ike)
				{
					JOptionPane.showMessageDialog(null, "Chiave non valida.", "SecureChat[Server] - Errore", JOptionPane.ERROR_MESSAGE);
				}
				catch(Exception ee)
				{
					ee.printStackTrace();
				}
				// catch(IOException ie)
				// {

				// }
				// catch(IllegalBlockSizeException ibe)
				// {

				// }
				// catch(NoSuchAlgorithmException ibe)
				// {

				// }
				// catch(NoSuchPaddingException ibe)
				// {

				// }
				// catch(BadPaddingException ike)
				// {

				// }
				// catch(ClassNotFoundException ibe)
				// {

				// }
				
			}
		});

		inboundPanel = new BoundPanel();
		inboundPanel.setBorder(new TitledBorder(etchedBorder, "Messaggio ricevuto"));
		inboundPanel.textArea.setBorder(new TitledBorder(etchedBorder, "Messaggio criptato"));
		inboundPanel.textArea.setEditable(false);
		inboundPanel.button.setText("Decripta");
		inboundPanel.button.addActionListener((ActionEvent e) ->
		{
			try
			{		
				String key = inboundPanel.keyField.getText();
				int algorithm = inboundPanel.algorithmList.getSelectedIndex();
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				byte[] digestedKey = md5.digest(key.getBytes());
				Cipher cipher = Cipher.getInstance(cipherAlgorithm[algorithm]);
				SecretKeySpec secretKey = new SecretKeySpec(Arrays.copyOf(digestedKey, cipherKeySize[algorithm]), cipherAlgorithm[algorithm]);
				cipher.init(Cipher.DECRYPT_MODE, secretKey);
				String decryptedMessage = new String(cipher.doFinal(cryptedMessage));
				inboundPanel.textArea.setText(decryptedMessage);
			}
			catch(InvalidKeyException ike)
			{
				JOptionPane.showMessageDialog(null, "Chiave non valida.", "SecureChat[Server] - Errore", JOptionPane.ERROR_MESSAGE);
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
			// catch(IOException ie)
			// {

			// }
			// catch(IllegalBlockSizeException ibe)
			// {

			// }
			// catch(NoSuchAlgorithmException ibe)
			// {

			// }
			// catch(NoSuchPaddingException ibe)
			// {

			// }
			// catch(BadPaddingException ike)
			// {

			// }
		});



		inboundPanel.setLayout(new BoxLayout(inboundPanel, BoxLayout.PAGE_AXIS));

		JPanel mainPanel = new JPanel();
		mainPanel.add(outboundPanel);
		mainPanel.add(new JSeparator(SwingConstants.VERTICAL));
		mainPanel.add(inboundPanel);

		this.add(mainPanel);
	}

	
	private class BoundPanel extends JPanel
	{
		public JTextArea textArea; 
		public JList<String> algorithmList; 
		public JTextField keyField;
		public JButton button;

		/**	Costruttore che inizializza il pannello.
		**/
		public BoundPanel()
		{
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

			textArea = new JTextArea();

			Border etchedBorder = BorderFactory.createEtchedBorder();

			JPanel algoritmPanel = new JPanel(new GridLayout(2, 1));
			algoritmPanel.setBorder(new TitledBorder(etchedBorder, "Algoritmo"));

			algorithmList = new JList<String>(cipherAlgorithm);
			algorithmList.setBorder(etchedBorder);
			algorithmList.setPreferredSize(new Dimension(getWidth(), (int) algorithmList.getPreferredSize().getHeight()));
			algorithmList.setSelectedIndex(0);

			JPanel keyPanel = new JPanel(new GridLayout(1, 2));

			keyField = new JTextField();
			Dimension keyFieldDimension = keyField.getPreferredSize();
			keyField.getInsets().set( (int)(keyFieldDimension.getHeight() / 8),
									 0,
									 (int)(keyFieldDimension.getHeight() / 8),
									 0);
			// keyField.setBorder(new TitledBorder(etchedBorder, "Chiave"));

			keyPanel.add(new JLabel("Chiave", SwingConstants.RIGHT));
			keyPanel.add(keyField);

			algoritmPanel.add(algorithmList);
			algoritmPanel.add(keyPanel);

			button = new JButton("blank");
		
			add(textArea);
			add(algoritmPanel);
			add(button);

			setPreferredSize(new Dimension(380, 300));
		}
	}
}