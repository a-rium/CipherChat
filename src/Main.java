package src;

import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

public class Main
{
	public static void main(String[] args)
	{
		JRadioButton clientRadio = new JRadioButton("Client");
		JRadioButton serverRadio = new JRadioButton("Server");
		ButtonGroup group = new ButtonGroup();
		group.add(clientRadio);
		group.add(serverRadio);

		clientRadio.setSelected(true);

		JPanel panel = new JPanel();
		panel.add(clientRadio);
		panel.add(serverRadio);

		int chosenOption = JOptionPane.showOptionDialog(null, panel, "SecureChat", JOptionPane.OK_CANCEL_OPTION, 
			JOptionPane.PLAIN_MESSAGE, null, null, null);
		if(chosenOption == 0)
		{
			if(clientRadio.isSelected())
				new LoginUI();
			else
			{
				while(true)
				{
					String portText = JOptionPane.showInputDialog(null, "Inserisci la porta sulla quale ascoltare per la connessione", 
						"SecureChat[Server] - Seleziona porta", JOptionPane.QUESTION_MESSAGE);
					if(portText == null)
						return;
					try
					{
						int port = Integer.parseInt(portText);
						if(port < 0 || port > 65535)
							throw new NumberFormatException();
						new WaitingUI(port);
						break;
					}
					catch(IOException ie)
					{

					}
					catch(NumberFormatException nfe)
					{
						JOptionPane.showInputDialog(null, "Il valore della porta deve essere un numero compreso tra 0 e 65535", "Chat sicura[Server] - Errore");
					}
				}
			}
			
		}
	}
}