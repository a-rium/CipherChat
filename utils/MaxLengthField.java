package utils;

import javax.swing.JTextField;

import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.AttributeSet;

// TextField modificata che permette di stabilire un numero massimo di caratteri
// disponibili.

public class MaxLengthField extends JTextField 
{
	private int maximumLength;

	public MaxLengthField(int maximumLength) 
	{
		this.maximumLength = maximumLength;
	}

	public void setMaximumLength(int maximumLength)
	{
		this.maximumLength = maximumLength;
	}

	protected Document createDefaultModel() 
	{
		return new MaxLengthDocument(this);
	}

	private class MaxLengthDocument extends PlainDocument 
	{
		private JTextField reference;

		public MaxLengthDocument(JTextField reference)
		{
			this.reference = reference;
		}

		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException 
		{
			if(reference.getText().length() + str.length() <= maximumLength || maximumLength < 0)
				super.insertString(offs, str, a);
		}
	}
}