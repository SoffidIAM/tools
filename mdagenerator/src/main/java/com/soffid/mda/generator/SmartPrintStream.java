package com.soffid.mda.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class SmartPrintStream extends PrintStream {

	private File originalFile;
	private File tempFile;
	boolean closed = false;
	
	public SmartPrintStream(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
		super(new File(file+".candidate"), csn);
		tempFile = new File(file+".candidate");
		this.originalFile = file;
	}

	public SmartPrintStream(File file) throws FileNotFoundException, UnsupportedEncodingException {
		super(new File(file+".candidate"), "UTF-8");
		tempFile = new File(file+".candidate");
		this.originalFile = file;
	}

	@Override
	public void close() {
		super.close();
		if (!closed)
		{
			closed = true;
			if (! originalFile.exists() ||
				differs (originalFile, tempFile))
			{
				copy (tempFile, originalFile);
			}
			else
			{
				tempFile.delete();
			}
		}
	}

	private boolean differs(File originalFile2, File tempFile2) {
		try {
			long l1 = originalFile2.length();
			long l2 = tempFile2.length();
			if (l1 != l2)
			{
				return true;
			}
			InputStream in1 = new FileInputStream(originalFile2);
			InputStream in2 = new FileInputStream(tempFile2);
			int read1 = 0;
			int read2 = 0;
			int offset = 0;
			do
			{
				if (read1 < 0)
				{
					in2.close();
					in1.close();
					return false;
				}
				offset ++;
				read1 = in1.read();
				read2 = in2.read();
			} while (read1 == read2);
			in2.close();
			in1.close();
			return true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	private void copy(File tempFile2, File originalFile2) {
		if (originalFile2.exists())
			originalFile2.delete();
		System.out.println("Generating "+originalFile2.toString());
		tempFile2.renameTo(originalFile2);
	}
	

}
