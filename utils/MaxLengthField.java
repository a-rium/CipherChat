package utils;

import javax.swing.JTextField;

import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.AttributeSet;

/** TextField modificata che permette di stabilire un numero massimo di caratteri
 *  disponibili.
**/ 
public class MaxLengthField extends JTextField 
{
	private int maximumLength;

	/** Costruttore che stabilisce la lunghezza massima dell' area di testo.
	 *  @param maximumLength lunghezza massima
	**/
	public MaxLengthField(int maximumLength) 
	{
		this.maximumLength = maximumLength;
	}

	/** Modifica la lunghezza massima dell' area di testo.
	 *  @param maximumLength nuova lunghezza massima
	**/
	public void setMaximumLength(int maximumLength)
	{
		this.maximumLength = maximumLength;
	}

	/** Ritorna un modello di default del documento.
	 *  @return modello di default.
	**/
	protected Document createDefaultModel() 
	{
		return new MaxLengthDocument(this);
	}

	/** Modello di documento che stabilisce una lunghezza massima all'input. **/
	private class MaxLengthDocument extends PlainDocument 
	{
		private JTextField reference;

		/** Costruttore del modello del documento.
		 *  @param reference area di testo alla quale si fa riferimento.
		**/
		public MaxLengthDocument(JTextField reference)
		{
			this.reference = reference;
		}

		/** Funzione richiamata all'inserimento, rimozione o alterazione di un carattere o una stringa all'interno della JTextField alla quale si fa riferimento.
		 *  @param offs posizione alla quale è avvenuta la modifica.
		 *  @param str carattere/stringa inserito/a.
		**/
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException 
		{
			if(reference.getText().length() + str.length() <= maximumLength || maximumLength < 0)
				super.insertString(offs, str, a);
		}
	}
}