package com.soffid.mda.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.soffid.mda.annotation.JsonObject;
import com.soffid.mda.parser.*;

public class ValueObjectGenerator {
	
	final static String endl = "\n";

	private Generator generator;
	private Parser parser;

	private int scope;

	public void generate(Generator generator, Parser parser) throws FileNotFoundException, UnsupportedEncodingException {
		this.generator = generator;
		this.parser = parser;
		this.scope = Translate.SERVICE_SCOPE; 
		
		for (AbstractModelClass vo: parser.getValueObjects()) {
			generateValueObject (vo, Translate.SERVICE_SCOPE);
			if (vo.isTranslated())
				generateValueObject (vo, Translate.ALTSERVICE_SCOPE);
			else
				System.out.println( "Object is not translated ["+ vo.getName(scope) + "]" );
			generateValueObjectQueryDescriptor(vo, Translate.SERVICE_SCOPE);
			generateValueObjectQueryDescriptor(vo, Translate.ALTSERVICE_SCOPE);
		}


		for (AbstractModelClass vo: parser.getCriterias() ){
			generateValueObject (vo, Translate.SERVICE_SCOPE);
			if (vo.isTranslated())
				generateValueObject (vo, Translate.ALTSERVICE_SCOPE);
		}

		for (AbstractModelClass exception: parser.getExceptions ())
		{
			generateException (exception, Translate.SERVICE_SCOPE);
			if ( exception.isTranslated())
				generateException (exception, Translate.ALTSERVICE_SCOPE);
		}

		for (AbstractModelClass enumeration: parser.getEnumerations ()) {
			if (!enumeration.getAttributes().isEmpty() ) {
				generateEnumeration (enumeration, Translate.SERVICE_SCOPE);
				generateHibernateEnumeration (enumeration, Translate.SERVICE_SCOPE);
				if ( enumeration.isTranslated())
				{
					generateEnumeration (enumeration, Translate.ALTSERVICE_SCOPE);
					generateHibernateEnumeration (enumeration, Translate.ENTITY_SCOPE);
				}
			}
		}

	}

	void generateEnumeration(AbstractModelClass vo, int entityScope) throws FileNotFoundException, UnsupportedEncodingException {
		String file = generator.getCommonsDir() + "/" + vo.getPackageDir(entityScope) + vo.getName(entityScope) + ".java";
//		System.out.println( "Generating " + file );
		File f = new File(file);
		f.getParentFile().mkdirs();
		SmartPrintStream out = new SmartPrintStream (f);
		

		out.println( "//" + endl
				+ "// (C) 2013 Soffid" + endl
				+ "//" + endl
				+ "//" + endl
				+"// Attention: Generated code! Do not modify by hand!" + endl
				+ "//" );
		if (vo.getPackage() != null)
			out.println( "package " + vo.getPackage(entityScope) + ";" );

		out.println( "/**" + endl
				+ " * Enumeration " + vo.getName(entityScope) + endl
				+ Util.formatComments(vo.getComments())
				+ " */" );
		out.println( "public class " + vo.getName(entityScope));
		if (vo.getSuperClass() != null) {
			out.print( " extends " );
			out.println( vo.getSuperClass().getFullName(entityScope));
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
			dataType = vo.getAttributes().get(0).getJavaType(entityScope);
			int values = 0;
			for (AbstractModelAttribute att: vo.getAttributes())
			{
				if (! att.getName(entityScope).isEmpty()) {
					values ++;
					dataType = att.getJavaType(entityScope);
					out.println( "\t/**" + endl
							+ Util.formatComments(att.getComments())
							+ "\t */" );
					out.println( "\tpublic static final " + vo.getName(entityScope) + " " + Util.toUpper(att.getName(entityScope)) + "= new "
							+ vo.getName(entityScope) + "( new "+dataType+  "("
							+ att.getConstantValue() + "));" + endl );

				}
			}

			// empty constructor method
			out.println( "\t/**" + endl
				+ "\t * The default constructor, allowing super classes to access it" + endl
				+ "\t */" + endl
				+ "\tprivate " + dataType + " value;" + endl
				+ endl
				+ "\tprivate " + vo.getName(entityScope) + "(" + dataType + " value)" + endl
				+ "\t{" + endl
				+ "\t\tthis.value=value;" + endl
				+ "\t}" + endl + endl
				+ "\tprotected " + vo.getName(entityScope) + "()" + endl
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
				+ "\t * Creates an instance of " + vo.getName(entityScope) + " from <code>value</code>." + endl
				+ "\t *" + endl
				+ "\t * @param value the value to create the " + vo.getName(entityScope) + " from." + endl
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
			out.println( "\tpublic static " + vo.getName(entityScope) + " from" + getterType + "(" + dataType+ " value)" );
			out.println( "\t{" + endl
				+ "\t\tfinal "+ vo.getName(entityScope) + " typeValue = (" + vo.getName(entityScope) + ") values.get(value);" + endl
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
				+ "\t\treturn (this == that) ? 0 : this.getValue().compareTo(((" + vo.getName(entityScope) + ")that).getValue());" + endl
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
				+ "\t\t\t|| (object instanceof " + vo.getName(entityScope) + endl
				+ "\t\t\t    && ((" + vo.getName(entityScope) + ")object).getValue().equals(this.getValue()));" + endl
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
				+ "\t\treturn " + vo.getName(entityScope)+ ".from" + getterType + "(this.value);" + endl
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
				if (! att.getName(entityScope).isEmpty()) {
					out.println( "\t\tvalues.put(" + Util.toUpper(att.getName(entityScope)) + ".value, "
							+ Util.toUpper(att.getName(entityScope)) + ");" + endl
							+ "\t\tliterals.add(" + Util.toUpper(att.getName(entityScope)) + ".value);" + endl
							+ "\t\tnames.add(\"" + Util.toUpper(att.getName(entityScope)) + "\");" );
				}
			}
			out.println( "\t\tliterals = java.util.Collections.unmodifiableList(literals);" + endl
				+ "\t\tnames = java.util.Collections.unmodifiableList(names);" + endl
				+ "\t}" );
		}
		out.println( "}" );

		out.close();

	}

	void generateHibernateEnumeration(AbstractModelClass vo, int entityScope) throws FileNotFoundException, UnsupportedEncodingException {
		String file = generator.getCoreDir() + "/" + vo.getPackageDir(entityScope) + vo.getName(entityScope) + "Enum.java";
//		System.out.println( "Generating " + file );
		File f = new File(file);
		f.getParentFile().mkdirs();
		SmartPrintStream out = new SmartPrintStream (f);

		String dataType = vo.getAttributes().get(0).getJavaType(entityScope);
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
				+ "package " + vo.getPackage(entityScope) + ";" + endl
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
				+ "public final class " + vo.getName(entityScope)+ "Enum" + endl
				+ "\textends " + vo.getName(entityScope) + endl
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
				+ "\tpublic " + vo.getName(entityScope)+ "Enum()" + endl
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
				+ "\t\treturn " + vo.getName(entityScope)+ ".class;" + endl
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
				+ "\t\treturn java.lang.String.valueOf(((" + vo.getName(entityScope) + ")object).getValue());" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * @see org.hibernate.usertype.EnhancedUserType#toXMLString(Object object)" + endl
				+ "\t */" + endl
				+ "\tpublic String toXMLString(Object object)" + endl
				+ "\t{" + endl
				+ "\t\treturn java.lang.String.valueOf(((" + vo.getName(entityScope) + ")object).getValue());" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * @see org.hibernate.usertype.EnhancedUserType#fromXMLString(String string)" + endl
				+ "\t */" + endl
				+ "\tpublic Object fromXMLString(String string)" + endl
				+ "\t{" + endl
				+ "\t\treturn "+vo.getName(entityScope)+".from" + getterType
				+ "(" + dataType + ".valueOf(string));" + endl
				+ "\t}" + endl
				+ "}" );

		out.close();

	}

	void generateException(AbstractModelClass vo, int scope) throws FileNotFoundException, UnsupportedEncodingException {
		String file = generator.getCommonsDir() + "/" + vo.getPackageDir(scope) + vo.getName(scope) + ".java";
//		System.out.println( "Generating " + file );
		File f = new File(file);
		f.getParentFile().mkdirs();
		SmartPrintStream out = new SmartPrintStream (f);

		out.println( "//" + endl
				+ "// (C) 2013 Soffid" + endl
				+ "//" + endl
				+ "//" + endl
				);
		if (vo.getPackage()!= null)
			out.println( "package " + vo.getPackage(scope) + ";" );

		out.println ( "import org.apache.commons.beanutils.PropertyUtils;" + endl
				+ "/**" + endl
				+ " * Exception " + vo.getName(scope) + endl
				+ Util.formatComments(vo.getComments())
				+ " */" );
		out.println( "public class " + vo.getName(scope));
		if (vo.getSuperClass() != null) {
			out.println( " extends " + vo.getSuperClass().getFullName(scope) );
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
				+ "\tpublic " + vo.getName(scope) + "()" + endl
				+ "\t{}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * Constructs a new instance of " + vo.getName(scope) + "" + endl
				+ "\t *" + endl
				+ "\t * @param throwable the parent Throwable" + endl
				+ "\t */" + endl
				+ "\tpublic " + vo.getName(scope) + "(Throwable throwable)" + endl
				+ "\t{" + endl
				+ "\t\tsuper(findRootCause(throwable));" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * Constructs a new instance of " + vo.getName(scope) + "" + endl
				+ "\t *" + endl
				+ "\t * @param message the throwable message." + endl
				+ "\t */" + endl
				+ "\tpublic " + vo.getName(scope) + "(String message)" + endl
				+ "\t{" + endl
				+ "\t\tsuper(message);" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * Constructs a new instance of " + vo.getName(scope) + "" + endl
				+ "\t *" + endl
				+ "\t * @param message the throwable message." + endl
				+ "\t * @param throwable the parent of this Throwable." + endl
				+ "\t */" + endl
				+ "\tpublic " + vo.getName(scope) + "(String message, Throwable throwable)" + endl
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

	void generateValueObject(AbstractModelClass vo, int scope) throws FileNotFoundException, UnsupportedEncodingException {
		if (vo.isAbstract())
			return;
		
		String file = generator.getCommonsDir() + "/" + vo.getPackageDir(scope) + vo.getName(scope) + ".java";
//		System.out.println( "Generating " + file );
		File f = new File(file);
		f.getParentFile().mkdirs();
		SmartPrintStream out = new SmartPrintStream (f);

		out.println( "//" + endl
				+ "// (C) 2013 Soffid" + endl
				+ "//" + endl
				+ "//" + endl
				);
		if (vo.getPackage(scope) != null)
			out.println( "package " + vo.getPackage(scope) + ";" );

		out.println( "/**" + endl
				+ " * ValueObject " + vo.getName(scope) + endl
				+ Util.formatComments(vo.getComments())
				+ " **/" );
		if (vo.isJsonObject())
		{
			JsonObject json = vo.getJsonObject();
			if (json.serializerDelegate())
			{
				out.print("@com.fasterxml.jackson.databind.annotation.JsondDeserializer(using=");
				out.print(vo.getPackagePrefix(scope)+"json.");
				out.println(vo.getName()+"Serializer.class)");
				out.print("@com.fasterxml.jackson.databind.annotation.JsonSerializer(using=");
				out.print(vo.getPackagePrefix(scope)+"json.");
				out.println(vo.getName()+"Serializer.class)");
			}
		}
		out.println( "public class " + vo.getName(scope));
		if (vo.getSuperClass() != null) {
			out.print( " extends " );
			out.println( vo.getSuperClass().getFullName(scope));
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
			if (! att.getName(scope).isEmpty()) {
				if (att.isStatic())
				{
					String dataType = att.getDataType().getJavaType(scope);
					
					out.println( "\t/**" + endl
							+ "\t * Constant " + att.getName(scope) + endl
							+ Util.formatComments(att.getComments()) + endl
							+ "\t */" );
					out.print( "\tpublic static final " + dataType
							+ " " + att.getName(scope)) ;
					Object v = att.getStaticValue();
					if (v != null )
						if (v instanceof String)
							out.print( " = \"" + v.toString()+"\"");
						else
							out.print( " = " + v);
					out.println( ";" + endl );
				}
				else
				{
					String dataType = att.getDataType().getJavaType(scope);
					
					out.println( "\t/**" + endl
							+ "\t * Attribute " + att.getName(scope) + endl
							+ Util.formatComments(att.getComments()) + endl
							+ "\t */" );
					out.print( "\tprivate " + (att.isTransient()?"transient ": "")+ dataType
							+ " " + att.getName(scope)) ;
					if (att.getDefaultValue() != null)
						out.print( " = " + att.getDefaultValue());
					out.println( ";" + endl );
				}
			}
		}

		// empty constructor method
		out.println( "\tpublic " + vo.getName(scope) + "()" + endl
			+ "\t{" + endl
	        + "\t}" + endl );

		// Required attributes constructor
		List<AbstractModelAttribute> allAttributes = vo.getAllAttributes();
		boolean allRequired = true;
		boolean anyRequired = false;
		out.print( "\tpublic " + vo.getName(scope) + "(" );
		boolean first = true;
		for (AbstractModelAttribute att: allAttributes)
		{
			if (! att.getName(scope).isEmpty() && !att.isStatic()) {
				if (att.isRequired())
					anyRequired = true;
				else
					allRequired = false;

				if (first)
					first = false;
				else
					out.print( ", " ) ;
				out.print( att.getDataType().getJavaType(scope) + " " + att.getName(scope) );
			}
		}
		out.print( ")" + endl
				+ "\t{" + endl
				+ "\t\tsuper(") ;
		boolean superAttributes = true;
		boolean firstSuperAttribute = true;
		for (AbstractModelAttribute att: allAttributes)
		{
			if (! att.getName(scope).isEmpty() && !att.isStatic()) {
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
						out.print( att.getName(scope) );
						firstSuperAttribute = false;
					}
				}
				if ( !superAttributes)
					out.println( "\t\tthis." + att.getName(scope) + " = " + att.getName(scope) + ";" );
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
			out.print( "\tpublic " + vo.getName(scope) + "(" );
			first = true;
			for (AbstractModelAttribute att: allAttributes)
			{
				if (! att.getName(scope).isEmpty() && att.isRequired() && ! att.isStatic()) {
					if (first)
						first = false;
					else
						out.print( ", " );
					out.print( att.getDataType().getJavaType(scope) + " " + att.getName(scope) );
				}
			}
			out.print( ")" + endl
					+ "\t{" + endl
					+ "\t\tsuper(" ) ;
			for (AbstractModelAttribute att: allAttributes)
			{
				if (! att.getName(scope).isEmpty() && att.isRequired() && ! att.isStatic()) {
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
							out.print( att.getName(scope));
							firstSuperAttribute = false;
						}
					}
					if ( !superAttributes)
						out.println( "\t\tthis." + att.getName(scope) + " = " + att.getName(scope) + ";" );
				}
			}
			if (superAttributes)
				out.println( ");" );
			out.println( "\t}" + endl );
		}

		// Other  bean constructor
		out.print( "\tpublic " + vo.getName(scope) + "(" + vo.getName(scope) + " otherBean)" + endl
				+ "\t{" + endl
				+ "\t\tthis(");
		first = true;
		for (AbstractModelAttribute att: allAttributes)
		{
			if (! att.getName(scope).isEmpty() && ! att.isStatic()) {
				if (first)
					first = false;
				else
					out.print( ", " ) ;
				if (att.getModelClass() == vo)
					out.print( "otherBean." + att.getName(scope) );
				else
					out.print( "otherBean." + att.getterName(scope) + "()" );
			}
		}
		out.println( ");" + endl
				+ "\t}" + endl );
		//
		// Attributes getter & setter
		//
		for (AbstractModelAttribute att: vo.getAttributes())
		{
			if (! att.getName(scope).isEmpty() && ! att.isStatic()) {
				out.println( "\t/**" + endl
						+ "\t * Gets value for attribute " + att.getName(scope) + endl
						+ "\t */" );
				out.println( "\tpublic " + att.getDataType().getJavaType(scope)
						+ " " + att.getterName(scope) + "() {" + endl
						+ "\t\treturn this." + att.getName(scope) + ";" + endl
						+ "\t}" + endl + endl
						+ "\t/**" + endl
						+ "\t * Sets value for attribute " + att.getName(scope) + endl
						+ "\t */" + endl
						+ "\tpublic void " + att.setterName(scope) + "("
						+ att.getDataType().getJavaType(scope) + " "
						+ att.getName(scope) + ") {" + endl
						+ "\t\tthis." + att.getName(scope)
						+ " = " + att.getName(scope) + ";" + endl
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
			if (! att.getName(scope).isEmpty()  && ! att.isStatic()) {
			    out.print ( "\t\tb.append (\"" );
				if (first)
					out.print ( "[" );
				else
					out.print ( ", " ) ;
			    out.println( att.getName(scope) + ": \");" );
				out.println( "\t\tb.append (this." + att.getName(scope) + ");" );
				first = false;
			}
		}

	    out.println( "\t\tb.append (\"]\");" + endl
	        + "\t\treturn b.toString();" + endl
			+ "\t}" + endl );



	    // Translation transfomers
	    if (vo.isTranslated())
	    {
	    	int altScope = scope == Translate.SERVICE_SCOPE ? Translate.ALTSERVICE_SCOPE : Translate.SERVICE_SCOPE;
	    	out.println( "\t/**" + endl
				+ "\t * Creates a " + vo.getName(scope) + " value object based on a " + 
	    			vo.getName(altScope) +
	    			" object." + endl
				+ "\t */" );
			out.println( "\tpublic static " + vo.getName(scope) + " to" + 
				vo.getName(scope) + "(" + vo.getPackagePrefix(altScope) + vo.getName(altScope) + " vo)" );
			out.println ( "\t{" + endl
				+ "\t\tif (vo == null)" + endl
				+ "\t\t\treturn null;" + endl
				+ "\t\t" + vo.getName(scope) + " target = new " + vo.getName(scope) + "();" + endl
				+ "\t\tto"+vo.getName(scope)+" (vo, target);" + endl
				+ "\t\treturn target;"+endl
				+ "\t}"+endl);

			// List transformer
	    	out.println( "\t/**" + endl
				+ "\t * Creates a " + vo.getName(scope) + " list on a " + vo.getName(altScope) + " collection." + endl
				+ "\t */" );

			out.println( "\tpublic static java.util.List<" + vo.getName(scope) + "> to" + 	
					vo.getName(scope) + "List (java.util.Collection<" + vo.getFullName(altScope) + "> source)" );

			out.println ( "\t{" + endl
				+ "\t\tif (source == null) return null;" + endl + endl
				+ "\t\tjava.util.List<" + vo.getName(scope) + "> target = new java.util.LinkedList<" + 
					vo.getName(scope) + "> ();" + endl
				+ "\t\tfor (" + vo.getFullName(altScope) + " obj: source) " + endl
				+ "\t\t\t{" + endl
				+ "\t\t\t\ttarget.add ( to"+ vo.getName(scope) + "(obj));" + endl
				+ "\t\t\t}" + endl
				+ "\t\treturn target;" + endl
				+ "\t}" + endl );

			// Array transformer
	    	out.println( "\t/**" + endl
				+ "\t * Creates a " + vo.getName(scope) + " array on a " + vo.getName(altScope) + " array." + endl
				+ "\t */" );

			out.println( "\tpublic static  "+ vo.getName(scope) + "[] to" + 	
					vo.getName(scope) + "Array (" + vo.getFullName(altScope) + "[] source)" );

			out.println ( "\t{" + endl
				+ "\t\tif (source == null) return null;" + endl + endl
				+ "\t\t" + vo.getName(scope) + "[] target = new "+vo.getName(scope) + "[source.length];" + endl
				+ "\t\tfor (int i = 0; i < source.length;i ++) " + endl
				+ "\t\t\t{" + endl
				+ "\t\t\t\ttarget[i] = to"+ vo.getName(scope) + "(source[i]);" + endl
				+ "\t\t\t}" + endl
				+ "\t\treturn target;" + endl
				+ "\t}" + endl );

			// Copies all attributes
	    	out.println( "\t/**" + endl
				+ "\t * Updates a " + vo.getName(scope) + " value object based on a " + 
	    			vo.getName(altScope) +
	    			" object." + endl
				+ "\t */" );
			out.println( "\tpublic static void to" + 
				vo.getName(scope) + "(" + vo.getPackagePrefix(altScope) + vo.getName(altScope) + " source, "
					+ vo.getPackagePrefix(scope) + vo.getName(scope)+ " target)" );
			out.println ( "\t{" + endl
				+ "\t\tif (source == null)" + endl
				+ "\t\t\treturn;" + endl);
			first = true;
			if (vo.getSuperClass() != null)
			{
				AbstractModelClass sc = vo.getSuperClass();
				out.println( "\t\t"+sc.getFullName(scope)+".to" + sc.getName(scope) + "(source, target);" );
			}
			for (AbstractModelAttribute att: vo.getAttributes())
			{
				if (! att.getName(scope).isEmpty()  && ! att.isStatic()) {
					if (att.getDataType().isCollection() && att.getDataType().getChildClass() != null && 
						att.getDataType().getChildClass().isTranslated() && att.getDataType().getChildClass().isValueObject())
					{
						AbstractModelClass child = att.getDataType().getChildClass();
						out.println( "\t\ttarget." + att.getName(scope) + " = "
							+ child.getFullName(scope) + ".to"
							+ child.getName(scope) + "List (source."
							+ att.getterName(altScope) + "());" );
					} 
					else if (att.getDataType().isTranslated() && att.getDataType().isValueObject())
					{
						out.println( "\t\ttarget." + att.getName(scope) + " = "
													+ att.getDataType().getFullName(scope) + ".to"
													+ att.getDataType().getName(scope) + "(source."
													+ att.getterName(altScope) + "());" );
					}
					else if (att.getDataType().isEnumeration() && att.getDataType().isTranslated())
					{
						out.println( "\t\ttarget." + att.getName(scope) + " = "
								+ "source."+ att.getterName(altScope) + "() == null ? null : \n\t\t\t"
								+ att.getDataType().getJavaType(scope)+".fromString(source." + att.getterName(altScope) + "().getValue());" );
					}
					else
						out.println( "\t\ttarget." + att.getName(scope) + " = source." + att.getterName(altScope) + "();" );
				}
			}

			out.println( "\t}" + endl );

	    }
		out.println ( "}" );

		out.close();

	}
	
	void generateValueObjectQueryDescriptor(AbstractModelClass vo, int scope) throws FileNotFoundException, UnsupportedEncodingException {
		if (vo.isJsonObject())
		{
			JsonObject jsonObject = vo.getJsonObject();

			String modelPackage = generator.getModelPackage(scope);
			String file = generator.getCoreResourcesDir()+ "/" + vo.getPackageDir(scope)+ vo.getName(scope) + ".query.json";
//			System.out.println( "Generating " + file );
			File f = new File(file);
			f.getParentFile().mkdirs();
			SmartPrintStream out = new SmartPrintStream (f);
	
			out.println( "/*" + endl
					+ "// (C) 2015 Soffid" + endl
					+ "//" + endl
					+ "*/" + endl
					);
			out.println( "{" );
			AbstractModelClass entity = null;
			if (jsonObject.hibernateClass() != null)
			{
				entity = (AbstractModelClass) parser.getElement(jsonObject.hibernateClass());
				if (! entity.isEntity())
					throw new RuntimeException("Error parsing object "+
							vo.getFullName()+": Class "+
							jsonObject.hibernateClass().getName()+" is not an entity");
				out.println("  hibernateClass: \"" + 
							entity.getFullName(Translate.ENTITY_SCOPE)+"\",");
			}

			out.println ("  attributes: [");
			//
			// Attributes
			//
			boolean first = true;
			for (AbstractModelAttribute att: vo.getAttributes())
			{
				if (! att.isStatic())
				{
					if (first)
						first = false;
					else
						out.println (",");
					out.print("    {name:\"");
					out.print(att.getName(scope));
					out.print("\"");
					if (att.getJsonHibernateAttribute() != null)
					{
						out.print (",  hibernateName:\"");
						out.print (att.getJsonHibernateAttribute());
						out.print ("\"");
					} else if (entity != null){
						for (AbstractModelAttribute entityAtt: entity.getAllAttributes())
						{
							if (entityAtt.getName().equalsIgnoreCase(att.getName()))
							{
								out.print (", hibernateName:\"");
								out.print (entityAtt.getName());
								out.print ("\"");
							}
						}
					}
					out.print ("}");
				}
			}
			out.println ();
			out.println ("  ]");
			out.println ("};");
	
			out.close();
		}

	}
	
}
