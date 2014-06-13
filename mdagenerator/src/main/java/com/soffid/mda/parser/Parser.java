package com.soffid.mda.parser;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Parser {
	public HashMap<Object, ModelElement> elements = new HashMap<Object, ModelElement>();
	File root = null;
	LinkedList<URL> urls = new LinkedList<URL>();
	
	public URL[] getURLs()
	{
		return urls.toArray(new URL[0]);
	}
	
	public Collection<ModelElement> getModelElements()
	{
		return new LinkedList (elements.values());
	}
	
	public void parse (File dir) throws ClassNotFoundException, MalformedURLException
	{
		if (root == null)
			root = dir;
		
		urls.add(dir.toURI().toURL());
		
		for (File file: dir.listFiles())
		{
			if (file.isDirectory())
				parse (file);
			else
			{
				if (file.getName().endsWith(".class"))
				{
					register (file);
				}
			}
		}
		
		for (ModelElement element: getModelElements())
		{
			if (element instanceof ModelClass)
			{
				((ModelClass) element).fixup();
			}
		}
	}


	private void register(File file) throws ClassNotFoundException {
		String f1 = root.getPath();
		String f2 = file.getPath();
		String diff = f2.substring(f1.length());
		if (diff.charAt(0) == File.separatorChar)
			diff = diff.substring(1);
		diff = diff.substring(0, diff.length()-6); // Remove trailing .class
		diff = diff.replace(File.separatorChar, '.'); // Change slash by dot
		Class classFile;
		classFile = Thread.currentThread().getContextClassLoader().
				loadClass(diff);
		
		ModelClass mc = (ModelClass) getElement (classFile);
		mc.setGenerated(true);
	}

	public ModelElement getElement (Object obj) {
		if (elements.containsKey(obj))
			return elements.get(obj);
		else
		{
			ModelElement model;
			if (obj instanceof Type)
				model = new ModelClass(this, (Type) obj);
			else if (obj instanceof Method)
				model = new ModelOperation(this, (Method) obj);
			else if (obj instanceof Field)
				model = new ModelAttribute(this, (Field) obj);
			else
				return null;
			elements.put(obj, model);
			return model;
		}
		
	}
	
	List<ModelClass> services = null;
	public List<ModelClass> getServices ()
	{
		if (services == null)
		{
			services = new LinkedList<ModelClass>();
			for (ModelElement element: elements.values())
			{
				if (element instanceof ModelClass && ((ModelClass) element).isService() && ((ModelClass) element).isGenerated())
					services.add((ModelClass) element);
			}
		}
		return services;
	}

	List<ModelClass> entities = null;
	public List<ModelClass> getEntities ()
	{
		if (entities == null)
		{
			entities = new LinkedList<ModelClass>();
			for (ModelElement element: elements.values())
			{
				if (element instanceof ModelClass && ((ModelClass) element).isEntity() && ((ModelClass) element).isGenerated())
					entities.add((ModelClass) element);
			}
		}
		return entities;
	}

	List<ModelClass> indexes = null;
	public List<ModelClass> getIndexes ()
	{
		if (indexes == null)
		{
			indexes = new LinkedList<ModelClass>();
			for (ModelElement element: elements.values())
			{
				if (element instanceof ModelClass && ((ModelClass) element).isIndex() && ((ModelClass) element).isGenerated())
					indexes.add((ModelClass) element);
			}
		}
		return indexes;
	}


	List<ModelClass> valueObjects = null;
	public List<ModelClass> getValueObjects ()
	{
		if (valueObjects == null)
		{
			valueObjects = new LinkedList<ModelClass>();
			for (ModelElement element: elements.values())
			{
				if (element instanceof ModelClass && ((ModelClass) element).isValueObject() && ((ModelClass) element).isGenerated())
					valueObjects.add((ModelClass) element);
			}
		}
		return valueObjects;
	}

	List<ModelClass> actors = null;
	private LinkedList<ModelClass> criterias;
	private LinkedList<ModelClass> enumerations;
	private LinkedList<ModelClass> exceptions;
	private boolean translateOnly;
	public boolean isTranslateOnly() {
		return translateOnly;
	}

	public List<ModelClass> getActors ()
	{
		if (actors == null)
		{
			actors = new LinkedList<ModelClass>();
			for (ModelElement element: elements.values())
			{
				if (element instanceof ModelClass && ((ModelClass) element).isRole() && ((ModelClass) element).isGenerated())
					actors.add((ModelClass) element);
			}
		}
		return actors;
	}

	public List<ModelClass> getCriterias() {
		if (criterias == null)
		{
			criterias = new LinkedList<ModelClass>();
			for (ModelElement element: elements.values())
			{
				if (element instanceof ModelClass && ((ModelClass) element).isCriteria() && ((ModelClass) element).isGenerated())
					criterias.add((ModelClass) element);
			}
		}
		return criterias;
	}

	public List<ModelClass> getEnumerations() {
		if (enumerations == null)
		{
			enumerations = new LinkedList<ModelClass>();
			for (ModelElement element: elements.values())
			{
				if (element instanceof ModelClass && ((ModelClass) element).isEnumeration() && ((ModelClass) element).isGenerated())
					enumerations.add((ModelClass) element);
			}
		}
		return enumerations;
	}

	public List<ModelClass> getExceptions() {
		if (exceptions == null)
		{
			exceptions = new LinkedList<ModelClass>();
			for (ModelElement element: elements.values())
			{
				if (element instanceof ModelClass && ((ModelClass) element).isException() && ((ModelClass) element).isGenerated())
					exceptions.add((ModelClass) element);
			}
		}
		return exceptions;
	}

	public void setTranslateOnly(boolean translatedOnly) {
		this.translateOnly = translateOnly;
		
	}
}
