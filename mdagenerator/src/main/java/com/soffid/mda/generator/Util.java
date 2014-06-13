package com.soffid.mda.generator;

import java.io.File;

import com.soffid.mda.parser.ModelClass;

public class Util {

	public static String formatComments(String comments) {
		return formatComments (comments, "");
	}

	public static String formatComments(String comments, String prefix) {
		if (comments == null)
			return "";
		
		StringBuffer b = new StringBuffer ();
		int i = 0;
		while ( i < comments.length())
		{
			b.append (prefix);
			b.append (" * ");
			int newline = comments.indexOf('\n', i);
			if (newline < 0)
			{
				b.append (comments.substring(i));
				break;
			}
			b.append (comments.substring(i, newline));
			b.append ('\n');
			i = newline + 1;
		}
		b.append ('\n');
		return b.toString();
	}

	public static String firstUpper (String s)
	{
		return s.substring(0, 1).toUpperCase()+ s.substring(1);
	}

	public static String firstLower (String s)
	{
		return s.substring(0, 1).toLowerCase()+ s.substring(1);
	}
	
	public static String formatXmlComments (String comments, String indent)
	{
		if (comments == null)
			return "";
		
		StringBuffer r = new StringBuffer();
		for (char ch: comments.toCharArray())
		{
			switch (ch)
			{
			case '&': r.append ("&amp;"); break;
			case '<': r.append ("&lt;"); break;
			case '>': r.append ("&gt;"); break;
			case '\n': r.append (ch). append(indent); break;
			default: r.append (ch);
			}
		}
		return r.toString();
	}
	
	public static String hardTrim (String text)
	{
		int first = 0;
		while (first < text.length() && (text.charAt(first) == ' ' || text.charAt(first) =='\n' || text.charAt(first) == '\r'))
			first ++;
		int end = text.length();
		while (end > 0 && (text.charAt(end-1) == ' '|| text.charAt(end-1) =='\n' || text.charAt(end-1) == '\r'))
			end --;
		return text.substring(first, end);
	}
	
	public static boolean isPrimitive (String type)
	{
		return type.equals("int") || type.equals("long") ||
				type.equals("boolean") ||
				type.equals("double") || type.equals("float") || 
				type.equals("char") || type.equals("byte") ;
	}
	
	public static String toUpper (String s)
	{
		int i = 0;
		StringBuffer r = new StringBuffer();
		boolean wasLower = false;
		while ( i < s.length()) {
			char ch = s.charAt(i);;
			char ch2 = Character.toUpperCase(ch);
			if (ch == ch2)
			{
				if (wasLower && ch != '_') 
					r.append ("_");
				wasLower = false;
			}
			else
				wasLower = true;
			r.append ( ch2 );
			i++;
		}
		return r.toString();
	}
	
	public void mkFileDir (String file)
	{
		File f = new File (file);
		File dir = f.getParentFile();
		if (! dir.isDirectory())
			dir.mkdirs();
	}

	public static String formatString(String string) {
		if (string == null)
			return "";
		
		StringBuffer r = new StringBuffer();
		for (char ch: string.toCharArray())
		{
			switch (ch)
			{
			case '\n': r.append ("\\n"); break;
			case '\r': r.append ("\\r"); break;
			case '"': r.append ("\\\""); break;
			default: r.append (ch);
			}
		}
		return r.toString();
	}

	public static String packageToDir(String packageName) {
		String s = packageName.replace('.', '/') ;
		if (! s.endsWith ("/")) s = s + "/";
		return s;
	}
	
	public static boolean isModifiedClass (ModelClass clazz, File f)
	{
		if (!f.canRead())
			return true;
		if (clazz.lastModified() > f.lastModified())
			return true;
		else
			return false;
	}

	public static String formatHtml(String comments) {
		return formatXmlComments(comments, "<br>");
	}
}
