package utils;

import java.io.ObjectOutputStream;

public interface ObjectHandler
{
	public void handle(Object obj, ObjectOutputStream out);
}