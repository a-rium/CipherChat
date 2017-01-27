package utils;

import java.net.Socket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import java.util.ArrayList;

/** Classe involucro che contiene una socket e gli stream necessari per lo stabilimento di una connessione basata sullo scambio di oggetti.
**/ 
public class TCPSocketWrapper
{
	protected Socket socket;
	protected ObjectInputStream in;
	protected ObjectOutputStream out;
	protected ObjectListener listener;
	protected ArrayList<ObjectHandler> objectHandlers;
	
	/** Costruttore che inizializza la socket e gli stream ad essa associate.
	 *  @param ipAddr kindizzo ip del server.
	 *	@param port numero di porta della socket del server.
	**/ 
	public TCPSocketWrapper(String ipAddr, int port) throws IOException
	{
		socket = new Socket(ipAddr, port);
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(socket.getInputStream());
		objectHandlers = new ArrayList<ObjectHandler>();
	}
	
	/** Costruttore che estrapola gli stream da una socket precedentemente aperta.
	 *  @param socket socket in input.
	**/ 
	public TCPSocketWrapper(Socket socket) throws IOException
	{
		this.socket = socket;
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(socket.getInputStream());
		objectHandlers = new ArrayList<ObjectHandler>();
	}

	/** Legge un Object dalla stream di input e lo ritorna.
	 *  @return ritorna l'oggetto letto.
	**/ 
	public Object readObject() throws IOException, ClassNotFoundException
	{
		return in.readObject();
	}

	/** Invia un oggetto tramite la stream associata alla socket.
	 *  @param obj Object da inviare.
	**/
	public void writeObject(Object obj) throws IOException, ClassNotFoundException
	{
		out.writeObject(obj);
	}

	/** Chiude la socket e le stream associate.
	**/
	public void close() throws IOException
	{
		out.close();
		in.close();
		socket.close();
	}

	/** Attiva o disattiva la funzionalità di auto-receiver, ovvero lettura e gestione automatica di oggetti ottenuti dallo stream.
	 *  @param on stato.
	**/
	public void setAutoReceive(boolean on)
	{
		if(listener == null || !listener.isRunning())
		{
			if(on)
			{
				listener = new ObjectListener();
				listener.start();
			}
		}
		else
		{
			if(!on)
			{
				listener.halt();
			}
		}
	}

	/** Aggiunge un gestore alla quale verranno passati gli oggetti letti in modalità auto-receiver
	 *  @param handler Oggetto istanzianto da una classe che implementa ObjectHandler.
	**/
	public void addObjectHandler(ObjectHandler handler)
	{
		objectHandlers.add(handler);
	}

	/** Thread che si occupa di rimanere in ascolto di oggetti, i quali passerà agli appositi gestori.
	 * 	E' abilitato solo in modalità auto-receiver.
	**/
	private class ObjectListener extends Thread
	{
		private boolean listening = true;

		/** Metodo che implementa le funzionalità della classe.
		**/
		public void run()
		{
			while(listening)
			{
				try
				{
					Object obj = in.readObject();
					for(ObjectHandler objectHandler : objectHandlers)
						objectHandler.handle(obj, out);
				}
				catch(IOException | ClassNotFoundException e) {}
			}
		}

		/** Ferma il thread chiudendo la connessione.
		**/
		public void halt()
		{
			if(listening)
			{
				try
				{
					socket.setSoTimeout(1);
					listening = false;
					wait(1);
					socket.setSoTimeout(0);
				}
				catch(InterruptedException | IOException e) {}
			}
		}

		/** Ritorna un valore booleano corrispondente  allo stato del thread.
		 * 	@return stato del thread.
		**/
		public boolean isRunning()
		{
			return listening;
		}
	}
}
