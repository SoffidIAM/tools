package com.soffid.mda.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

import com.soffid.mda.parser.*;

public class ValueObjectGenerator {
	
	final static String endl = "\n";

	private Generator generator;
	private Parser parser;

	private boolean translated;

	public void generate(Generator generator, Parser parser) throws FileNotFoundException {
		this.generator = generator;
		this.parser = parser;
		this.translated = generator.isTranslatedOnly();
		
		for (AbstractModelClass vo: parser.getValueObjects()) {
			if (!generator.isTranslatedOnly())
				generateValueObject (vo, false);
			if (vo.isTranslated() || generator.isTranslatedOnly())
				generateValueObject (vo, true);
			else
				System.out.println( "Object is not translated ["+ vo.getName(translated) + "]" );
			
			if (vo.isJsonObject())
			{
				generateJsonTransformer (vo);
			}
		}


		for (AbstractModelClass vo: parser.getCriterias() ){
			generateValueObject (vo, translated);
			if ( ! translated && vo.isTranslated())
				generateValueObject (vo, true);
		}

		for (AbstractModelClass exception: parser.getExceptions ())
		{
			generateException (exception, translated);
			if ( ! translated && exception.isTranslated())
				generateException (exception, true);
		}

		for (AbstractModelClass enumeration: parser.getEnumerations ()) {
			if (!enumeration.getAttributes().isEmpty()) {
				generateEnumeration (enumeration, generator.isTranslatedOnly());
				generateHibernateEnumeration (enumeration, generator.isTranslatedOnly());
				if ( ! generator.isTranslatedOnly() && enumeration.isTranslated())
				{
					generateEnumeration (enumeration, true);
					generateHibernateEnumeration (enumeration, true);
				}
			}
		}

	}

	void generateEnumeration(AbstractModelClass vo, boolean translated) throws FileNotFoundException {
		String file = generator.getCommonsDir() + "/" + vo.getPackageDir(translated) + vo.getName(translated) + ".java";
		System.out.println( "Generating " + file );
		File f = new File(file);
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream (f);
		

		out.println( "//" + endl
				+ "// (C) 2013 Soffid" + endl
				+ "//" + endl
				+ "//" + endl
				+"// Attention: Generated code! Do not modify by hand!" + endl
				+ "//" );
		if (vo.getPackage() != null)
			out.println( "package " + vo.getPackage(translated) + ";" );

		out.println( "/**" + endl
				+ " * Enumeration " + vo.getName(translated) + endl
				+ Util.formatComments(vo.getComments())
				+ " */" );
		out.println( "public class " + vo.getName(translated));
		if (vo.getSuperClass() != null) {
			out.print( " extends " );
			out.println( vo.getSuperClass().getFullName(translated));
		}
		out.println("\t\timplements java.io.Serializable" );
		out.println( " {" );
		out.println( );
		out.println(   "\t/**" + endl
				+ "\t * The serial version UID of this class. Needed for serialization." + endl
				+ "\t */" + endl
				+ "\tprivate static final long serialVersionUID = "
				+ vo.getSerialVersion()
				+ ";" );

		if (!vo.getAttributes().isEmpty())
		{
			String dataType;
			dataType = vo.getAttributes().get(0).getJavaType(translated, this.translated);
			int values = 0;
			for (AbstractModelAttribute att: vo.getAttributes())
			{
				if (! att.getName(translated).isEmpty()) {
					values ++;
					dataType = att.getJavaType(translated, this.translated);
					out.println( "\t/**" + endl
							+ Util.formatComments(att.getComments())
							+ "\t */" );
					out.println( "\tpublic static final " + vo.getName(translated) + " " + Util.toUpper(att.getName(translated)) + "= new "
							+ vo.getName(translated) + "( new "+dataType+  "("
							+ att.getConstantValue() + "));" + endl );

				}
			}

			// empty constructor method
			out.println( "\t/**" + endl
				+ "\t * The default constructor, allowing super classes to access it" + endl
				+ "\t */" + endl
				+ "\tprivate " + dataType + " value;" + endl
				+ endl
				+ "\tprivate " + vo.getName(translated) + "(" + dataType + " value)" + endl
				+ "\t{" + endl
				+ "\t\tthis.value=value;" + endl
				+ "\t}" + endl + endl
				+ "\tprotected " + vo.getName(translated) + "()" + endl
				+ "\t{" + endl
				+ "\t}" + endl + endl
				+ "\t/**" + endl
				+ "\t *  @see java.lang.object#toString()" + endl
				+ "\t */" + endl
				+ "\tpublic String toString()" + endl
				+ "\t{" + endl
				+ "\t\treturn java.lang.String.valueOf(value);" + endl
				+ "\t}" + endl
				+ "\t/**" + endl
				+ "\t * Creates an instance of " + vo.getName(translated) + " from <code>value</code>." + endl
				+ "\t *" + endl
				+ "\t * @param value the value to create the " + vo.getName(translated) + " from." + endl
				+ "\t */" );
			String getterType ;
			if (dataType.equals ( "java.lang.String"  )|| dataType.equals ( "String" )) {
				getterType = "String";
			} else if (dataType.equals("java.lang.Long") || dataType.equals ("Long")) {
				getterType = "Long";
			} else if (dataType.equals("java.lang.Integer") || dataType.equals ( "Integer" )) {
				getterType = "Integer";
			} else {
				getterType = "Object";
			}
			out.println( "\tpublic static " + vo.getName(translated) + " from" + getterType + "(" + dataType+ " value)" );
			out.println( "\t{" + endl
				+ "\t\tfinal "+ vo.getName(translated) + " typeValue = (" + vo.getName(translated) + ") values.get(value);" + endl
				+ "\t\tif (typeValue == null)" + endl
				+ "\t\t\tthrow new IllegalArgumentException(\"invalue value '\" + value + \"', possible vaues are: \" + literals); " + endl
				+ "\t\treturn typeValue;" + endl
				+ "\t}" + endl
				+ endl
				+ "\t/**" + endl
				+ "\t * Gets the underlying value of this type safe enumeration." + endl
				+ "\t *" + endl
				+ "\t * @return the underlying value." + endl
				+ "\t */" + endl
				+ "\tpublic " + dataType + " getValue()" + endl
				+ "\t{" + endl
				+ "\t\treturn this.value;" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * @see java.lang.Comparable#compareTo(java.lang.Object)" + endl
				+ "\t */" + endl
				+ "\tpublic int compareTo(Object that)" + endl
				+ "\t{" + endl
				+ "\t\treturn (this == that) ? 0 : this.getValue().compareTo(((" + vo.getName(translated) + ")that).getValue());" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * Returns an unmodifiable list containing the literals that are known by this enumeration." + endl
				+ "\t *" + endl
				+ "\t * @return A List containing the actual literals defined by this enumeration, this list" + endl
				+ "\t *         can not be modified." + endl
				+ "\t */" + endl
				+ "\tpublic static java.util.List literals()" + endl
				+ "\t{" + endl
				+ "\t\treturn literals;" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * Returns an unmodifiable list containing the names of the literals that are known" + endl
				+ "\t * by this enumeration." + endl
				+ "\t *" + endl
				+ "\t * @return A List containing the actual names of the literals defined by this" + endl
				+ "\t *         enumeration, this list can not be modified." + endl
				+ "\t */" + endl
				+ "\tpublic static java.util.List names()" + endl
				+ "\t{" + endl
				+ "\t\treturn names;" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * @see java.lang.Object#equals(java.lang.Object)" + endl
				+ "\t */" + endl
				+ "\tpublic boolean equals(Object object)" + endl
				+ "\t{" + endl
				+ "\t\treturn (this == object)" + endl
				+ "\t\t\t|| (object instanceof " + vo.getName(translated) + endl
				+ "\t\t\t    && ((" + vo.getName(translated) + ")object).getValue().equals(this.getValue()));" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * @see java.lang.Object#hashCode()" + endl
				+ "\t */" + endl
				+ "\tpublic int hashCode()" + endl
				+ "\t{" + endl
				+ "\t\treturn this.getValue().hashCode();" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * This method allows the deserialization of an instance of this enumeration type to return the actual instance" + endl
				+ "\t * that will be the singleton for the JVM in which the current thread is running." + endl
				+ "\t * <p/>" + endl
				+ "\t * Doing this will allow users to safely use the equality operator <code>==</code> for enumerations because" + endl
				+ "\t * a regular deserialized object is always a newly constructed instance and will therefore never be" + endl
				+ "\t * an existing reference; it is this <code>readResolve()</code> method which will intercept the deserialization" + endl
				+ "\t * process in order to return the proper singleton reference." + endl
				+ "\t * <p/>" + endl
				+ "\t * This method is documented here:" + endl
				+ "\t * <a href=\"http://java.sun.com/j2se/1.3/docs/guide/serialization/spec/input.doc6.html\">Java" + endl
				+ "\t * Object Serialization Specification</a>" + endl
				+ "\t */" + endl
				+ "\tprivate java.lang.Object readResolve() throws java.io.ObjectStreamException" + endl
				+ "\t{" + endl
				+ "\t\treturn " + vo.getName(translated)+ ".from" + getterType + "(this.value);" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\tprivate static final java.util.Map values = new java.util.HashMap(" + values + ", 1);" + endl
				+ "\tprivate static java.util.List literals = new java.util.ArrayList(" + values + ");" + endl
				+ "\tprivate static java.util.List names = new java.util.ArrayList(" + values + ");" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * Initializes the values." + endl
				+ "\t */" + endl
				+ "\tstatic" + endl
				+ "\t{" );

			for (AbstractModelAttribute att: vo.getAttributes())
			{
				if (! att.getName(translated).isEmpty()) {
					out.println( "\t\tvalues.put(" + Util.toUpper(att.getName(translated)) + ".value, "
							+ Util.toUpper(att.getName(translated)) + ");" + endl
							+ "\t\tliterals.add(" + Util.toUpper(att.getName(translated)) + ".value);" + endl
							+ "\t\tnames.add(\"" + Util.toUpper(att.getName(translated)) + "\");" );
				}
			}
			out.println( "\t\tliterals = java.util.Collections.unmodifiableList(literals);" + endl
				+ "\t\tnames = java.util.Collections.unmodifiableList(names);" + endl
				+ "\t}" );
		}
		out.println( "}" );

		out.close();

	}

	void generateHibernateEnumeration(AbstractModelClass vo, boolean translated) throws FileNotFoundException {
		String file = generator.getCoreDir() + "/" + vo.getPackageDir(translated) + vo.getName(translated) + "Enum.java";
		System.out.println( "Generating " + file );
		File f = new File(file);
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream (f);

		String dataType = vo.getAttributes().get(0).getJavaType(translated, this.translated);
		String getterType;
		if (dataType.equals( "java.lang.String" ) || dataType.equals ( "String" )) {
			getterType = "String";
		} else if (dataType.equals ( "java.lang.Long" ) || dataType.equals("Long")) {
			getterType = "Long";
		} else if (dataType.equals("java.lang.Integer") || dataType.equals( "Integer") ) {
			getterType = "Integer";
		} else {
			getterType = "Object";
		}
		out.println( "//" + endl
				+ "// (C) 2013 Soffid" + endl
				+ "//" + endl
				+ "//" + endl
				+"// Attention: Generated code! Do not modify by hand!" + endl
				+ "//" + endl
				+ "package " + vo.getPackage(translated) + ";" + endl
				+ "" + endl
				+ "import org.hibernate.HibernateException;" + endl
				+ "" + endl
				+ "import java.sql.Types;" + endl
				+ "import java.sql.ResultSet;" + endl
				+ "import java.sql.PreparedStatement;" + endl
				+ "import java.sql.SQLException;" + endl
				+ "" + endl
				+ "/**" + endl
				+ " * " + endl
				+ " */" + endl
				+ "public final class " + vo.getName(translated)+ "Enum" + endl
				+ "\textends " + vo.getName(translated) + endl
				+ "\timplements java.io.Serializable," + endl
				+ "\t\t\t   java.lang.Comparable," + endl
				+ "\t\t\t   org.hibernate.usertype.EnhancedUserType" + endl
				+ "{" + endl
				+ "" + endl
				+ "\tprivate static final int[] SQL_TYPES = {Types.VARCHAR};" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * Default constructor.  Hibernate needs the default constructor" + endl
				+ "\t * to retrieve an instance of the enum from a JDBC resultset." + endl
				+ "\t * The instance will be converted to the correct enum instance" + endl
				+ "\t * in {@link #nullSafeGet(java.sql.ResultSet, java.lang.String[], java.lang.Object)}." + endl
				+ "\t */" + endl
				+ "\tpublic " + vo.getName(translated)+ "Enum()" + endl
				+ "\t{" + endl
				+ "\t\tsuper();" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t *  @see org.hibernate.usertype.UserType#sqlTypes()" + endl
				+ "\t */" + endl
				+ "\tpublic int[] sqlTypes()" + endl
				+ "\t{" + endl
				+ "\t\treturn SQL_TYPES;" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t *  @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)" + endl
				+ "\t */" + endl
				+ "\tpublic Object deepCopy(Object value) throws HibernateException" + endl
				+ "\t{" + endl
				+ "\t\t// Enums are immutable - nothing to be done to deeply clone it" + endl
				+ "\t\treturn value;" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t *  @see org.hibernate.usertype.UserType#isMutable()" + endl
				+ "\t */" + endl
				+ "\tpublic boolean isMutable()" + endl
				+ "\t{" + endl
				+ "\t\t// Enums are immutable" + endl
				+ "\t\treturn false;" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t *  @see org.hibernate.usertype.UserType#equals(java.lang.Object, java.lang.Object)" + endl
				+ "\t */" + endl
				+ "\tpublic boolean equals(Object x, Object y) throws HibernateException" + endl
				+ "\t{" + endl
				+ "\t\treturn (x == y) || (x != null && y != null && y.equals(x));" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * @see org.hibernate.usertype.UserType#returnedClass()" + endl
				+ "\t */" + endl
				+ "\tpublic Class returnedClass()" + endl
				+ "\t{" + endl
				+ "\t\treturn " + vo.getName(translated)+ ".class;" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t *  @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], java.lang.Object)" + endl
				+ "\t */" + endl
				+ "\tpublic Object nullSafeGet(ResultSet resultSet, String[] values, Object owner) throws HibernateException, SQLException" + endl
				+ "\t{" + endl
				+ "\t\tfinal " + dataType + " value = (" + dataType + ")resultSet.getObject(values[0]);" + endl
				+ "\t\treturn resultSet.wasNull() ? null : from" + getterType + "(value);" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)" + endl
				+ "\t */" + endl
				+ "\tpublic void nullSafeSet(PreparedStatement statement, Object value, int index) throws HibernateException, SQLException" + endl
				+ "\t{" + endl
				+ "\t\tif (value == null)" + endl
				+ "\t\t{" + endl
				+ "\t\t\tstatement.setNull(index, Types.VARCHAR);" + endl
				+ "\t\t}" + endl
				+ "\t\telse" + endl
				+ "\t\t{" + endl
				+ "\t\t\tstatement.setObject(index, " + dataType + ".valueOf(java.lang.String.valueOf(value)));" + endl
				+ "\t\t}" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * @see org.hibernate.usertype.UserType#replace(Object original, Object target, Object owner)" + endl
				+ "\t */" + endl
				+ "\tpublic Object replace(Object original, Object target, Object owner)" + endl
				+ "\t{" + endl
				+ "\t\treturn original;" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable cached, Object owner)" + endl
				+ "\t */" + endl
				+ "\tpublic Object assemble(java.io.Serializable cached, Object owner)" + endl
				+ "\t{" + endl
				+ "\t\treturn cached;" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * @see org.hibernate.usertype.UserType#disassemble(Object value)" + endl
				+ "\t */" + endl
				+ "\tpublic java.io.Serializable disassemble(Object value)" + endl
				+ "\t{" + endl
				+ "\t\treturn (java.io.Serializable)value;" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * @see org.hibernate.usertype.UserType#hashCode(Object value)" + endl
				+ "\t */" + endl
				+ "\tpublic int hashCode(Object value)" + endl
				+ "\t{" + endl
				+ "\t\treturn value.hashCode();" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * @see org.hibernate.usertype.EnhancedUserType#objectToSQLString(Object object)" + endl
				+ "\t */" + endl
				+ "\tpublic String objectToSQLString(Object object)" + endl
				+ "\t{" + endl
				+ "\t\treturn java.lang.String.valueOf(((" + vo.getName(translated) + ")object).getValue());" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * @see org.hibernate.usertype.EnhancedUserType#toXMLString(Object object)" + endl
				+ "\t */" + endl
				+ "\tpublic String toXMLString(Object object)" + endl
				+ "\t{" + endl
				+ "\t\treturn java.lang.String.valueOf(((" + vo.getName(translated) + ")object).getValue());" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * @see org.hibernate.usertype.EnhancedUserType#fromXMLString(String string)" + endl
				+ "\t */" + endl
				+ "\tpublic Object fromXMLString(String string)" + endl
				+ "\t{" + endl
				+ "\t\treturn "+vo.getName(translated)+".from" + getterType
				+ "(" + dataType + ".valueOf(string));" + endl
				+ "\t}" + endl
				+ "}" );

		out.close();

	}

	void generateException(AbstractModelClass vo, boolean translated) throws FileNotFoundException {
		String file = generator.getCommonsDir() + "/" + vo.getPackageDir(translated) + vo.getName(translated) + ".java";
		System.out.println( "Generating " + file );
		File f = new File(file);
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream (f);

		out.println( "//" + endl
				+ "// (C) 2013 Soffid" + endl
				+ "//" + endl
				+ "//" + endl
				);
		if (vo.getPackage()!= null)
			out.println( "package " + vo.getPackage(translated) + ";" );

		out.println ( "import org.apache.commons.beanutils.PropertyUtils;" + endl
				+ "/**" + endl
				+ " * Exception " + vo.getName(translated) + endl
				+ Util.formatComments(vo.getComments())
				+ " */" );
		out.println( "public class " + vo.getName(translated));
		if (vo.getSuperClass() != null) {
			out.println( " extends " + vo.getSuperClass().getFullName(translated) );
		} else {
			out.println( " extends java.lang.Exception" );
		}
		out.println( " {" );
		out.println( );
		out.println(   "\t/**" + endl
				+ "\t * The serial version UID of this class. Needed for serialization." + endl
				+ "\t */" + endl
				+ "\tprivate static final long serialVersionUID = "
				+ vo.getSerialVersion()
				+ ";" );

		// empty constructor method
		out.println(  "\t/**" + endl
				+ "\t * The default constructor." + endl
				+ "\t */" + endl
				+ "\tpublic " + vo.getName(translated) + "()" + endl
				+ "\t{}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * Constructs a new instance of " + vo.getName(translated) + "" + endl
				+ "\t *" + endl
				+ "\t * @param throwable the parent Throwable" + endl
				+ "\t */" + endl
				+ "\tpublic " + vo.getName(translated) + "(Throwable throwable)" + endl
				+ "\t{" + endl
				+ "\t\tsuper(findRootCause(throwable));" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * Constructs a new instance of " + vo.getName(translated) + "" + endl
				+ "\t *" + endl
				+ "\t * @param message the throwable message." + endl
				+ "\t */" + endl
				+ "\tpublic " + vo.getName(translated) + "(String message)" + endl
				+ "\t{" + endl
				+ "\t\tsuper(message);" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * Constructs a new instance of " + vo.getName(translated) + "" + endl
				+ "\t *" + endl
				+ "\t * @param message the throwable message." + endl
				+ "\t * @param throwable the parent of this Throwable." + endl
				+ "\t */" + endl
				+ "\tpublic " + vo.getName(translated) + "(String message, Throwable throwable)" + endl
				+ "\t{" + endl
				+ "\t\tsuper(message, findRootCause(throwable));" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * Finds the root cause of the parent exception" + endl
				+ "\t * by traveling up the exception tree" + endl
				+ "\t */" + endl
				+ "\tprivate static Throwable findRootCause(Throwable th)" + endl
				+ "\t{" + endl
				+ "\t\tif (th != null)" + endl
				+ "\t\t{" + endl
				+ "\t\t\t// Lets reflectively get any JMX or EJB exception causes." + endl
				+ "\t\t\ttry" + endl
				+ "\t\t\t{" + endl
				+ "\t\t\t\tThrowable targetException = null;" + endl
				+ "\t\t\t\t// java.lang.reflect.InvocationTargetException" + endl
				+ "\t\t\t\t// or javax.management.ReflectionException" + endl
				+ "\t\t\t\tString exceptionProperty = \"targetException\";" + endl
				+ "\t\t\t\tif (PropertyUtils.isReadable(th, exceptionProperty))" + endl
				+ "\t\t\t\t{" + endl
				+ "\t\t\t\t\ttargetException = (Throwable)PropertyUtils.getProperty(th, exceptionProperty);" + endl
				+ "\t\t\t\t}" + endl
				+ "\t\t\t\telse" + endl
				+ "\t\t\t\t{" + endl
				+ "\t\t\t\t\texceptionProperty = \"causedByException\";" + endl
				+ "\t\t\t\t\t//javax.ejb.EJBException" + endl
				+ "\t\t\t\t\tif (PropertyUtils.isReadable(th, exceptionProperty))" + endl
				+ "\t\t\t\t\t{" + endl
				+ "\t\t\t\t\t\ttargetException = (Throwable)PropertyUtils.getProperty(th, exceptionProperty);" + endl
				+ "\t\t\t\t\t}" + endl
				+ "\t\t\t\t}" + endl
				+ "\t\t\t\tif (targetException != null)" + endl
				+ "\t\t\t\t{" + endl
				+ "\t\t\t\t\tth = targetException;" + endl
				+ "\t\t\t\t}" + endl
				+ "\t\t\t}" + endl
				+ "\t\t\tcatch (Exception ex)" + endl
				+ "\t\t\t{" + endl
				+ "\t\t\t\t// just print the exception and continue" + endl
				+ "\t\t\t\tex.printStackTrace();" + endl
				+ "\t\t\t}" + endl
				+ "" + endl
				+ "\t\t\tif (th.getCause() != null)" + endl
				+ "\t\t\t{" + endl
				+ "\t\t\t\tth = th.getCause();" + endl
				+ "\t\t\t\tth = findRootCause(th);" + endl
				+ "\t\t\t}" + endl
				+ "\t\t}" + endl
				+ "\t\treturn th;" + endl
				+ "\t}" + endl
				+ "}" );
		out.close();

	}

	void generateJsonObject (AbstractModelClass vo) throws FileNotFoundException
	{
		boolean translated = false;
		String file = generator.getCoreDir() + "/" + vo.getPackageDir(translated) + "json/"+vo.getName(translated) + ".java";
		System.out.println( "Generating " + file );
		File f = new File(file);
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream (f);

		out.close();
	}
	
	void generateValueObject(AbstractModelClass vo, boolean translated) throws FileNotFoundException {
		String file = generator.getCommonsDir() + "/" + vo.getPackageDir(translated) + vo.getName(translated) + ".java";
		System.out.println( "Generating " + file );
		File f = new File(file);
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream (f);

		out.println( "//" + endl
				+ "// (C) 2013 Soffid" + endl
				+ "//" + endl
				+ "//" + endl
				);
		if (vo.getPackage(translated) != null)
			out.println( "package " + vo.getPackage(translated) + ";" );

		out.println( "/**" + endl
				+ " * ValueObject " + vo.getName(translated) + endl
				+ Util.formatComments(vo.getComments())
				+ " **/" );
		out.println( "public class " + vo.getName(translated));
		if (vo.getSuperClass() != null) {
			out.print( " extends " );
			out.println( vo.getSuperClass().getFullName(translated));
		}
		out.println( endl + "\t\timplements java.io.Serializable" );
		out.println( " {" );
		out.println( );
		out.println(   "\t/**" + endl
				+ "\t + The serial version UID of this class. Needed for serialization." + endl
				+ "\t */" + endl
				+ "\tprivate static final long serialVersionUID = "
				+ vo.getSerialVersion()
				+ ";" );

		//
		// Attributes
		//
		for (AbstractModelAttribute att: vo.getAttributes())
		{
			if (! att.getName(translated).isEmpty()) {
				String dataType = att.getDataType().getJavaType(translated, this.translated);
				
				out.println( "\t/**" + endl
						+ "\t * Attribute " + att.getName(translated) + endl
						+ Util.formatComments(att.getComments()) + endl
						+ "\t */" );
				out.print( "\tprivate " + dataType
						+ " " + att.getName(translated)) ;
				if (att.getDefaultValue() != null && (!translated || ! att.getDataType().isTranslated()))
					out.print( " = " + att.getDefaultValue());
				out.println( ";" + endl );
			}
		}

		// empty constructor method
		out.println( "\tpublic " + vo.getName(translated) + "()" + endl
			+ "\t{" + endl
	        + "\t}" + endl );

		// Required attributes constructor
		List<AbstractModelAttribute> allAttributes = vo.getAllAttributes();
		boolean allRequired = true;
		boolean anyRequired = false;
		out.print( "\tpublic " + vo.getName(translated) + "(" );
		boolean first = true;
		for (AbstractModelAttribute att: allAttributes)
		{
			if (! att.getName(translated).isEmpty()) {
				if (att.isRequired())
					anyRequired = true;
				else
					allRequired = false;

				if (first)
					first = false;
				else
					out.print( ", " ) ;
				out.print( att.getDataType().getJavaType(translated, this.translated) + " " + att.getName(translated) );
			}
		}
		out.print( ")" + endl
				+ "\t{" + endl
				+ "\t\tsuper(") ;
		boolean superAttributes = true;
		boolean firstSuperAttribute = true;
		for (AbstractModelAttribute att: allAttributes)
		{
			if (! att.getName(translated).isEmpty()) {
				if (superAttributes) {
					if (att.getModelClass() == vo)
					{
						out.println( ");" );
						superAttributes = false;
					}
					else
					{
						if (!firstSuperAttribute)
							out.print( ", ");
						out.print( att.getName(translated) );
						firstSuperAttribute = false;
					}
				}
				if ( !superAttributes)
					out.println( "\t\tthis." + att.getName(translated) + " = " + att.getName(translated) + ";" );
			}
		}
		if (superAttributes)
			out.println( ");" );
		out.println( "\t}" + endl );

		// All attributes constructor
		if (! allRequired && anyRequired)
		{
			superAttributes = true;
			firstSuperAttribute = true;
			out.print( "\tpublic " + vo.getName(translated) + "(" );
			first = true;
			for (AbstractModelAttribute att: allAttributes)
			{
				if (! att.getName(translated).isEmpty() && att.isRequired()) {
					if (first)
						first = false;
					else
						out.print( ", " );
					out.print( att.getDataType().getJavaType(translated, this.translated) + " " + att.getName(translated) );
				}
			}
			out.print( ")" + endl
					+ "\t{" + endl
					+ "\t\tsuper(" ) ;
			for (AbstractModelAttribute att: allAttributes)
			{
				if (! att.getName(translated).isEmpty() && att.isRequired()) {
					if (superAttributes) {
						if (att.getModelClass() == vo)
						{
							out.println( ");" );
							superAttributes = false;
						}
						else
						{
							if (!firstSuperAttribute)
								out.print( ", ");
							out.print( att.getName(translated));
							firstSuperAttribute = false;
						}
					}
					if ( !superAttributes)
						out.println( "\t\tthis." + att.getName(translated) + " = " + att.getName(translated) + ";" );
				}
			}
			if (superAttributes)
				out.println( ");" );
			out.println( "\t}" + endl );
		}

		// Other  bean constructor
		out.print( "\tpublic " + vo.getName(translated) + "(" + vo.getName(translated) + " otherBean)" + endl
				+ "\t{" + endl
				+ "\t\tthis(");
		first = true;
		for (AbstractModelAttribute att: allAttributes)
		{
			if (! att.getName(translated).isEmpty()) {
				if (first)
					first = false;
				else
					out.print( ", " ) ;
				if (att.getModelClass() == vo)
					out.print( "otherBean." + att.getName(translated) );
				else
					out.print( "otherBean." + att.getterName(translated) + "()" );
			}
		}
		out.println( ");" + endl
				+ "\t}" + endl );
		//
		// Attributes getter & setter
		//
		for (AbstractModelAttribute att: vo.getAttributes())
		{
			if (! att.getName(translated).isEmpty()) {
				out.println( "\t/**" + endl
						+ "\t * Gets value for attribute " + att.getName(translated) + endl
						+ "\t */" );
				out.println( "\tpublic " + att.getDataType().getJavaType(translated, this.translated)
						+ " " + att.getterName(translated) + "() {" + endl
						+ "\t\treturn this." + att.getName(translated) + ";" + endl
						+ "\t}" + endl + endl
						+ "\t/**" + endl
						+ "\t * Sets value for attribute " + att.getName(translated) + endl
						+ "\t */" + endl
						+ "\tpublic void " + att.setterName(translated) + "("
						+ att.getDataType().getJavaType(translated, this.translated) + " "
						+ att.getName(translated) + ") {" + endl
						+ "\t\tthis." + att.getName(translated)
						+ " = " + att.getName(translated) + ";" + endl
						+ "\t}" + endl );
			}
		}

		if (vo.isCriteria())
		{
			out.println( "\t/**" + endl
				+ "\t * The first result to retrieve." + endl
				+ "\t */" + endl
				+ "\tprivate java.lang.Integer firstResult;" + endl + endl
				+ "\t/**" + endl
				+ "\t * Gets the first result to retrieve." + endl
				+ "\t *" + endl
				+ "\t * @return the first result to retrieve" + endl
				+ "\t */" + endl
				+ "\tpublic java.lang.Integer getFirstResult()" + endl
				+ "\t{" + endl
				+ "\t\treturn this.firstResult;" + endl
				+ "\t}" + endl + endl
				+ "\t/**" + endl
				+ "\t * Sets the first result to retrieve." + endl
				+ "\t *" + endl
				+ "\t * @param firstResult the first result to retrieve" + endl
				+ "\t */" + endl
				+ "\tpublic void setFirstResult(java.lang.Integer firstResult)" + endl
				+ "\t{" + endl
				+ "\t\tthis.firstResult = firstResult;" + endl
				+ "\t}" + endl + endl

				+ "\t/**" + endl
				+ "\t * The fetch size." + endl
				+ "\t */" + endl
				+ "\tprivate java.lang.Integer fetchSize;" + endl + endl
				+ "\t/**" + endl
				+ "\t * Gets the fetch size." + endl
				+ "\t *" + endl
				+ "\t * @return the fetch size" + endl
				+ "\t */" + endl
				+ "\tpublic java.lang.Integer getFetchSize()" + endl
				+ "\t{" + endl
				+ "\t\treturn this.fetchSize;" + endl
				+ "\t}" + endl + endl
				+ "\t/**" + endl
				+ "\t * Sets the fetch size." + endl
				+ "\t *" + endl
				+ "\t * @param fetchSize the fetch size." + endl
				+ "\t */" + endl
				+ "\tpublic void setFetchSize(java.lang.Integer fetchSize)" + endl
				+ "\t{" + endl
				+ "\t\tthis.fetchSize = fetchSize;" + endl
				+ "\t}" + endl + endl
				+ "\t/**" + endl
				+ "\t * The maximum size of the result set." + endl
				+ "\t */" + endl
				+ "\tprivate java.lang.Integer maximumResultSize;" + endl + endl
				+ "\t/**" + endl
				+ "\t * Gets the maximum size of the result set." + endl
				+ "\t *" + endl
				+ "\t * @return the maximum size of the result set" + endl
				+ "\t */" + endl
				+ "\tpublic java.lang.Integer getMaximumResultSize()" + endl
				+ "\t{" + endl
				+ "\t\treturn this.maximumResultSize;" + endl
				+ "\t}" + endl + endl
				+ "\t/**" + endl
				+ "\t * Sets the maximum size of the result set." + endl
				+ "\t *" + endl
				+ "\t * @param maximumResultSize the maximum size of the result set." + endl
				+ "\t */" + endl
				+ "\tpublic void setMaximumResultSize(java.lang.Integer maximumResultSize)" + endl
				+ "\t{" + endl
				+ "\t\tthis.maximumResultSize = maximumResultSize;" + endl
				+ "\t}" + endl );
		}




		// toString method

		out.println( "\t/**" + endl
			+ "\t * Returns a string representation of the value object." + endl
			+ "\t */" + endl
			+ "\tpublic String toString()" + endl
			+ "\t{" + endl
	        + "\t\tStringBuffer b = new StringBuffer();" + endl
	        + "\t\tb.append (getClass().getName());" );
		first = true;
		for (AbstractModelAttribute att: vo.getAttributes())
		{
			if (! att.getName(translated).isEmpty()) {
			    out.print ( "\t\tb.append (\"" );
				if (first)
					out.print ( "[" );
				else
					out.print ( ", " ) ;
			    out.println( att.getName(translated) + ": \");" );
				out.println( "\t\tb.append (this." + att.getName(translated) + ");" );
				first = false;
			}
		}

	    out.println( "\t\tb.append (\"]\");" + endl
	        + "\t\treturn b.toString();" + endl
			+ "\t}" + endl );



	    // Translation transfomers
	    if (vo.isTranslated())
	    {

	    	out.println( "\t/**" + endl
				+ "\t * Creates a " + vo.getName(translated) + " value object based on a " + vo.getName(!translated) + " object." + endl
				+ "\t */" );
			out.println( "\tpublic static " + vo.getName(translated) + " to" + vo.getName(translated) + "(" + vo.getPackagePrefix(!translated) + vo.getName(!translated) + " vo)" );
			out.println ( "\t{" + endl
				+ "\t\tif (vo == null)" + endl
				+ "\t\t\treturn null;" + endl
				+ "\t\t" + vo.getName(translated) + " target = new " + vo.getName(translated) + "();" );
			first = true;
			for (AbstractModelAttribute att: vo.getAttributes())
			{
				if (! att.getName(translated).isEmpty()) {
					if (att.getDataType().isCollection() && att.getDataType().getChildClass() != null && 
						att.getDataType().getChildClass().isTranslated() && att.getDataType().getChildClass().isValueObject())
					{
						AbstractModelClass child = att.getDataType().getChildClass();
						out.println( "\t\ttarget." + att.getName(translated) + " = "
							+ child.getFullName(translated) + ".to"
							+ child.getName(translated) + "List (vo."
							+ att.getterName(!translated) + "());" );
					} 
					else if (att.getDataType().isTranslated() && att.getDataType().isValueObject())
					{
						out.println( "\t\ttarget." + att.getName(translated) + " = "
													+ att.getDataType().getFullName(translated) + ".to"
													+ att.getDataType().getName(translated) + "(vo."
													+ att.getterName(!translated) + "());" );
					}
					else
						out.println( "\t\ttarget." + att.getName(translated) + " = vo." + att.getterName(!translated) + "();" );
				}
			}

			out.println( "\t\treturn target;" + endl
				+ "\t}" + endl );

			// List transformer
	    	out.println( "\t/**" + endl
				+ "\t * Creates a " + vo.getName(translated) + " list on a " + vo.getName(!translated) + " collection." + endl
				+ "\t */" );

			out.println( "\tpublic static java.util.List<" + vo.getName(translated) + "> to" + vo.getName(translated) + "List (java.util.Collection<" + vo.getFullName(!translated) + "> source)" );

			out.println ( "\t{" + endl
				+ "\t\tif (source == null) return null;" + endl + endl
				+ "\t\tjava.util.List<" + vo.getName(translated) + "> target = new java.util.LinkedList<" + vo.getName(translated) + "> ();" + endl
				+ "\t\tfor (" + vo.getFullName(!translated) + " obj: source) " + endl
				+ "\t\t\t{" + endl
				+ "\t\t\t\ttarget.add ( to"+ vo.getName(translated) + "(obj));" + endl
				+ "\t\t\t}" + endl
				+ "\t\treturn target;" + endl
				+ "\t}" + endl );

	    }
		out.println ( "}" );

		out.close();

	}
	
}
