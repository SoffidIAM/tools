package com.soffid.mda;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;

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
		gen.setTranslatedOnly(false);
		gen.setTranslateEntities(true);
		gen.configure (new File("target"));
		gen.setGenerateUml(false);
		gen.generate(parser);
	}

}
