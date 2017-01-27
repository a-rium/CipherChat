package utils;

import java.io.ObjectOutputStream;

/** Interfaccia che un gestore di oggetti deve implementare per essere riconosciuta dalla TCPSocketWrapper e TCPServerSocket.
**/ 
public interface ObjectHandler
{
	/** Gestisce un oggetto.
	 *  @param obj oggetto da gestire.
	 *  @param out stream da utilizzare per comunicare con l'esterno.
	**/ 
	public void handle(Object obj, ObjectOutputStream out);
}