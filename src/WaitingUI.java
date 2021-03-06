package src;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import java.awt.Dimension;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JButton;

/**	Finestra vuota che compare in attesa di richiesta di connessione.
**/
public class WaitingUI extends JFrame
{
	/**	Costruttore utilizzato dalla macchina server per comunicare con il client.
	 *  @param port numero porta della socket del server addetto all'accettazione di richieste da parte del client.
	**/
	public WaitingUI(int port) throws IOException
	{
		super("SecureChat[Server] - Aspettando il client...");
		ServerSocket server = new ServerSocket(port);
		JButton cancelButton = new JButton("Annulla");
		cancelButton.addActionListener((ActionEvent e) -> System.exit(0));
		Thread acceptConnection = new Thread(() ->
		{
			try
			{
				Socket client = server.accept();
				System.out.println("Hello");
				new ClientUI(client);
				setVisible(false);
			}
			catch(IOException ie)
			{

			}
		});
		acceptConnection.start();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(600, 120));
		setVisible(true);
	} 
}