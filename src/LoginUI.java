package src;

import java.io.IOException;

import java.awt.GridLayout;
import java.awt.Dimension;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import utils.MaxLengthField;

public class LoginUI extends JFrame
{
	public LoginUI()
	{
		super("SecureChat[Client] - Login");

		Border etchedBorder = BorderFactory.createEtchedBorder();

		JPanel inputPanel = new JPanel(new GridLayout(3, 2));
		inputPanel.setBorder(new TitledBorder(etchedBorder, "Inserimento dati server"));

		JTextField nameField = new MaxLengthField(10);
		JTextField	 ipField = new MaxLengthField(9);
		JTextField portField = new MaxLengthField(5);

		nameField.setText("Debug");
		ipField.setText("localhost");
		portField.setText("50000");

		inputPanel.add(new JLabel("Nome:", SwingConstants.RIGHT));
		inputPanel.add(nameField);
		inputPanel.add(new JLabel("Indirizzo IP server:", SwingConstants.RIGHT));
		inputPanel.add(ipField);
		inputPanel.add(new JLabel("Porta del server:", SwingConstants.RIGHT));
		inputPanel.add(portField);

		JPanel controlPanel = new JPanel(new GridLayout(1, 3));
		
		JButton nextButton = new JButton("Avanti >>");
		nextButton.addActionListener((ActionEvent e) ->
		{
			String name = nameField.getText();
			if(name.length() == 0)
			{
				JOptionPane.showMessageDialog(null, "Devi inserire un nome.", "Criptatore di messaggi[Client] - Errore", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String ip = ipField.getText();
			if(ip.length() == 0)
			{
				JOptionPane.showMessageDialog(null, "Devi inserire un indirizzo IP.", "Criptatore di messaggi[Client] - Errore", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String textPort = portField.getText();
			if(textPort.length() == 0)
			{
				JOptionPane.showMessageDialog(null, "Devi inserire una porta.", "Criptatore di messaggi[Client] - Errore", JOptionPane.ERROR_MESSAGE);
				return;
			}
			try
			{
				int port = Integer.parseInt(textPort);
				if(port < 0 || port > 65535)
				{
					throw new NumberFormatException("Invalid socket port");
				}
				new ClientUI(ip, port, name);
				dispose();
			}
			catch(IOException i)
			{
				JOptionPane.showMessageDialog(null, "Impossibile collegarsi al server locato alla porta e all'ip dato",
						"Criptatore di messaggi[Client] - Errore", JOptionPane.ERROR_MESSAGE);
				i.printStackTrace();
				return;	
			}
			catch(NumberFormatException nfe)
			{
				JOptionPane.showMessageDialog(null, "Il numero di porta non e' valido, deve essere un valore compreso tra 0 e 65535 inclusi",
						"Criptatore di messaggi[Client] - Errore", JOptionPane.ERROR_MESSAGE);
				return;	
			}
		});

		controlPanel.add(new JLabel());		
		controlPanel.add(new JLabel());		
		controlPanel.add(nextButton);		

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		
		mainPanel.add(inputPanel);
		mainPanel.add(controlPanel);
	
		this.add(mainPanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(600, 180));
		setVisible(true);
	}
}