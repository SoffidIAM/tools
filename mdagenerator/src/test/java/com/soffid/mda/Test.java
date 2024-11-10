package com.soffid.mda;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.soffid.mda.generator.Generator;
import com.soffid.mda.generator.XmiGenerator;
import com.soffid.mda.parser.Parser;


public class Test extends TestCase {
	public List<String> param;
	public byte[] param2;
	
	/**
	 * @param args
	 * @throws NoSuchFieldException 
	 * @throws SecurityException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public void testGeneration() throws SecurityException, NoSuchFieldException, ClassNotFoundException, IOException {
		Parser parser = new Parser ();
		parser.parse (new File("target/test-classes"));
		Generator gen = new Generator ();
		gen.setTranslated(false);
		gen.setTranslateEntities(true);
		gen.configure (new File("target"));
		gen.setGenerateUml(false);
		gen.generate(parser);
	}
	
	public Map<String,int[]>method () {
		return null;
	}
	
	public void test2 () throws NoSuchMethodException, SecurityException {
		Method m = getClass().getMethod("method");
		Type result = m.getGenericReturnType();
		System.out.println("RESULT:"+toString(result));
	}

	private String toString ( Type t )
	{
		if (t instanceof ParameterizedType)
		{
			ParameterizedType pt = (ParameterizedType) t;
			String s = toString(((ParameterizedType) t).getRawType()) + "<";
			Type[] params = pt.getActualTypeArguments() ;
			for (int i = 0; i < params.length; i++)
			{
				if (i > 0) s = s + ",";
				s = s + toString(params[i]);
			}
			s = s +">";
						
			return s;
		}
		else if  (t instanceof Class){
			Class cl = (Class) t;
			return cl.getCanonicalName();
		} else {
			return t.toString();
		}
	}
}
