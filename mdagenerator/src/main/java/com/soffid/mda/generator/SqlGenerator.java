package com.soffid.mda.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import com.soffid.mda.parser.AbstractModelAttribute;
import com.soffid.mda.parser.AbstractModelClass;
import com.soffid.mda.parser.ModelElement;
import com.soffid.mda.parser.Parser;

public class SqlGenerator {
	
	final static String endl = "\n";
	private Generator generator;
	private Parser parser;

	public void generate(Generator generator, Parser parser) throws FileNotFoundException, UnsupportedEncodingException {
		
		this.parser = parser;
		this.generator = generator;
		
		File f = new File(generator.getCoreResourcesDir() + "/" +
				( generator.isPlugin() ? "plugin" : "core" ) +
				"-ddl.xml");
		f.getParentFile().mkdirs();
		SmartPrintStream out = new SmartPrintStream(f, "UTF-8");

//		System.out.println ("Generating "+f.getPath());

		out.println ( "<?xml version='1.0' encoding='UTF-8'?>" + endl
				+ "<database name='Soffid'>" );

		// Create sequence
		// Create table
		for (ModelElement modelElement: parser.getModelElements()) 
		{
			if (modelElement instanceof AbstractModelClass && ((AbstractModelClass) modelElement).isEntity() &&
					((AbstractModelClass) modelElement).isGenerated())
			{
				AbstractModelClass classElement = (AbstractModelClass) modelElement;
				if (classElement.getSuperClass() == null) {

					out.println ( endl
						+ "\t<!-- " + classElement.getFullName(Translate.ENTITY_SCOPE) + " -->" + endl
						);
					String tag = classElement.getTableName();
					if (tag == null || tag.isEmpty())
					{
						throw new RuntimeException ( "ERROR: Table " + classElement.getFullName(Translate.ENTITY_SCOPE)
								+ " does not have a @andromda.table.name" );
					}
					else
					{
						out.println ( "\t<table name='" + tag + "'>" );

						generateTableSql ( classElement, out);

						out.println ( "\t</table>" );

					}

					generateFK (out, classElement);
				}
			}
			if (modelElement instanceof AbstractModelClass && ((AbstractModelClass) modelElement).isIndex())
			{
				generateIndex (out, (AbstractModelClass) modelElement);
			}
		}

		if (! generator.isPlugin())
		{
			out.println ( "\t<table name='SC_SEQUENCE'>" + endl
					+ "\t\t<column name='SEQ_NEXT' type='BIGINT' notNull='true'/>" + endl
					+ "\t\t<column name='SEQ_CACHE' type='BIGINT' notNull='true'/>" + endl
					+ "\t\t<column name='SEQ_INCREMENT' type='BIGINT' notNull='true'/>" + endl
					+ "\t</table>" + endl );
		}


		out.println ( "</database>" );

		out.close();
	}


	void generateTableSql (AbstractModelClass entity, SmartPrintStream out)
	{
		// Create columns
		AbstractModelAttribute pk = entity.getIdentifier();
		// Discriminator
		String d = entity.getDiscriminatorColumn();
		if (d != null && ! d.isEmpty())
		{
			if (entity.getSuperClass() != null && entity.getSuperClass().isEntity() && ! entity.getSuperClass().isAbstract())
			{
				// Nothing to do
			}
			else
				out.println ( "\t\t<column name=\"" + d + "\" type=\"VARCHAR\" length=\"16\" notNull=\"true\"/>" );
		}
		//
		for (AbstractModelAttribute att : entity.getAttributes())
		{
			String javaType = att.getDataType().getJavaType();
			String length = att.getLength();


			if (att.getDataType().isCollection() && att.getDataType().getChildClass().isEntity())
			{
				// Nothing to do
			}
			else
			{
				String ddlType = att.getDdlType(Translate.ENTITY_SCOPE);
				if (ddlType.isEmpty())
				{
					System.out.println ( "Warning: Cannot generate DDL for " + entity.getFullName(Translate.ENTITY_SCOPE)
						+ "." + att.getName(Translate.ENTITY_SCOPE) + "(" + javaType
						+ "/" + att.getDataType().getFullName(Translate.ENTITY_SCOPE) + ")" );
				}
				else {
					out.print ( "\t\t<column name='" + att.getColumn() + "' "
							+ ddlType );

					if (entity.getSuperClass() != null && entity.getSuperClass().isEntity())
						out.print ( " notNull='false'" );
					else if (att.isRequired())
						out.print ( " notNull='true'" );
					else
						out.print ( " notNull='false'");
					if (att == pk)
						out.print ( " primaryKey='true' autoIncrement='false'");
					out.println ( "/>" );
				}
			}

		}


		for (AbstractModelClass specialization: entity.getSpecializations())
		{
			generateTableSql( specialization, out);
		}
	}


	void generateIndex( SmartPrintStream out, AbstractModelClass index )
	{
		AbstractModelClass table = index.getIndexEntity();
		String tableName = table.getTableName();
		
		String tag = index.getIndexName();
		boolean unique = index.isIndexUnique();
		out.println ( "\t<index name='" + tag  + "' table='" + tableName + "' unique='" + unique + "'>" );

		for (String column: index.getIndexColumns())
		{
			out.println ( "\t\t<column name='" + column + "'/>" );
		}
		
		out.println ("\t</index>");
		
	}


	static void generateFK (SmartPrintStream out, AbstractModelClass entity)
	{
		// Create columns
		for (AbstractModelAttribute att: entity.getAttributes())
		{
			if (att.getDataType().isEntity())
			{
				AbstractModelClass foreignEntity = att.getDataType();
				while (foreignEntity.getSuperClass() != null && foreignEntity.getSuperClass().isEntity())
				{
					foreignEntity = foreignEntity.getSuperClass();
				}
				out.println ( "\t<foreignKey name='"
							+ entity.getTableName() + "_FK_" + att.getColumn() + "' "
							+ "table='" + entity.getTableName() + "' "
							+ (att.isCascadeDelete()? "cascade='delete' " :
								att.isCascadeNullify()? "cascade='nullify' " :
									"")
							+ "foreignTable='" + foreignEntity.getTableName() + "'> "
							+ endl
							+ "\t\t<column name='" + att.getColumn() + "'/>" + endl
							+ "\t\t<foreignColumn name='"
							+ foreignEntity.getIdentifier().getColumn()
							+ "'/>" + endl
							+ "\t</foreignKey>" );
			}

		}

	}



}
