package com.soffid.mda.parser;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
		LinkedList<ModelElement> l = new LinkedList<ModelElement> (elements.values());
		Collections.sort(l, new Comparator<ModelElement>(){

			public int compare(ModelElement o1, ModelElement o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});
		return l;
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
			if (element instanceof AbstractModelClass)
			{
				((AbstractModelClass) element).fixup();
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
		
		AbstractModelClass mc = (AbstractModelClass) getElement (classFile);
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
	
	public void register (Object id, ModelElement element) {
		if (!elements.containsKey(id))
			elements.put(id, element);
		
	}

	List<ModelClass> services = null;
	public List<ModelClass> getServices ()
	{
		if (services == null)
		{
			services = new LinkedList<ModelClass>();
			for (ModelElement element: elements.values())
			{
				if (element instanceof AbstractModelClass && ((AbstractModelClass) element).isService() && ((AbstractModelClass) element).isGenerated())
					services.add((ModelClass) element);
			}
			Collections.sort(services, new Comparator<ModelClass>(){
				public int compare(ModelClass o1, ModelClass o2) {
					return o1.getFullName().compareTo(o2.getFullName());
				}
				
			});
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
				if (element instanceof AbstractModelClass && ((AbstractModelClass) element).isEntity() && ((AbstractModelClass) element).isGenerated())
					entities.add((ModelClass) element);
			}
			Collections.sort(entities, new Comparator<ModelClass>(){
				public int compare(ModelClass o1, ModelClass o2) {
					return o1.getFullName().compareTo(o2.getFullName());
				}
				
			});
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
				if (element instanceof AbstractModelClass && ((AbstractModelClass) element).isIndex() && ((AbstractModelClass) element).isGenerated())
					indexes.add((ModelClass) element);
			}
			Collections.sort(indexes, new Comparator<ModelClass>(){
				public int compare(ModelClass o1, ModelClass o2) {
					return o1.getFullName().compareTo(o2.getFullName());
				}
				
			});
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
				if (element instanceof AbstractModelClass && ((AbstractModelClass) element).isValueObject() && ((AbstractModelClass) element).isGenerated())
					valueObjects.add((ModelClass) element);
			}
			Collections.sort(valueObjects, new Comparator<ModelClass>(){
				public int compare(ModelClass o1, ModelClass o2) {
					return o1.getFullName().compareTo(o2.getFullName());
				}
				
			});
		}
		return valueObjects;
	}

	List<ModelClass> actors = null;
	private LinkedList<ModelClass> criterias;
	private LinkedList<ModelClass> enumerations;
	private LinkedList<ModelClass> exceptions;
	private boolean translate;
	private boolean translateOnly;

	public boolean isTranslateOnly() {
		return translateOnly;
	}

	public void setTranslateOnly(boolean translateOnly) {
		this.translateOnly = translateOnly;
	}

	private boolean translateEntities;
	private String defaultException;
	public boolean isTranslateEntities() {
		return translateEntities;
	}

	public void setTranslateEntities(boolean translateEntities) {
		this.translateEntities = translateEntities;
	}

	public boolean isTranslate() {
		return translate;
	}

	public List<ModelClass> getActors ()
	{
		if (actors == null)
		{
			actors = new LinkedList<ModelClass>();
			for (ModelElement element: elements.values())
			{
				if (element instanceof AbstractModelClass && ((AbstractModelClass) element).isRole() && ((AbstractModelClass) element).isGenerated())
					actors.add((ModelClass) element);
			}
			Collections.sort(actors, new Comparator<ModelClass>(){
				public int compare(ModelClass o1, ModelClass o2) {
					return o1.getFullName().compareTo(o2.getFullName());
				}
				
			});
		}
		return actors;
	}

	public List<ModelClass> getCriterias() {
		if (criterias == null)
		{
			criterias = new LinkedList<ModelClass>();
			for (ModelElement element: elements.values())
			{
				if (element instanceof AbstractModelClass && ((AbstractModelClass) element).isCriteria() && ((AbstractModelClass) element).isGenerated())
					criterias.add((ModelClass) element);
			}
			Collections.sort(criterias, new Comparator<ModelClass>(){
				public int compare(ModelClass o1, ModelClass o2) {
					return o1.getFullName().compareTo(o2.getFullName());
				}
				
			});
		}
		return criterias;
	}

	public List<ModelClass> getEnumerations() {
		if (enumerations == null)
		{
			enumerations = new LinkedList<ModelClass>();
			for (ModelElement element: elements.values())
			{
				if (element instanceof AbstractModelClass && ((AbstractModelClass) element).isEnumeration() && ((AbstractModelClass) element).isGenerated())
					enumerations.add((ModelClass) element);
			}
			Collections.sort(enumerations, new Comparator<ModelClass>(){
				public int compare(ModelClass o1, ModelClass o2) {
					return o1.getFullName().compareTo(o2.getFullName());
				}
				
			});
		}
		return enumerations;
	}

	public List<ModelClass> getExceptions() {
		if (exceptions == null)
		{
			exceptions = new LinkedList<ModelClass>();
			for (ModelElement element: elements.values())
			{
				if (element instanceof AbstractModelClass && ((AbstractModelClass) element).isException() && ((AbstractModelClass) element).isGenerated())
					exceptions.add((ModelClass) element);
			}
			Collections.sort(exceptions, new Comparator<ModelClass>(){
				public int compare(ModelClass o1, ModelClass o2) {
					return o1.getFullName().compareTo(o2.getFullName());
				}
				
			});
		}
		return exceptions;
	}

	public void setTranslate(boolean translatedOnly) {
		this.translate = translatedOnly;
		
	}

	public String getDefaultException() {
		return defaultException;
	}

	public void setDefaultException(String defaultException) {
		this.defaultException = defaultException;
	}
}
