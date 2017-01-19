package utils;

import java.net.Socket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import java.util.ArrayList;

public class TCPSocketWrapper
{
	protected Socket socket;
	protected ObjectInputStream in;
	protected ObjectOutputStream out;
	protected ObjectListener listener;
	protected ArrayList<ObjectHandler> objectHandlers;

	public TCPSocketWrapper(String ipAddr, int port) throws IOException
	{
		socket = new Socket(ipAddr, port);
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(socket.getInputStream());
		objectHandlers = new ArrayList<ObjectHandler>();
	}

	public TCPSocketWrapper(Socket socket) throws IOException
	{
		this.socket = socket;
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(socket.getInputStream());
		objectHandlers = new ArrayList<ObjectHandler>();
	}

	public Object readObject() throws IOException, ClassNotFoundException
	{
		return in.readObject();
	}

	public void writeObject(Object obj) throws IOException, ClassNotFoundException
	{
		out.writeObject(obj);
	}

	public void close() throws IOException
	{
		out.close();
		in.close();
		socket.close();
	}

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

	public void addObjectHandler(ObjectHandler handler)
	{
		objectHandlers.add(handler);
	}

	private class ObjectListener extends Thread
	{
		private boolean listening = true;

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

		
		public boolean isRunning()
		{
			return listening;
		}
	}
}
