/*
 * Copyright(c) Zdenek Tronicek, FIT CTU in Prague. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (CDDL). You can obtain a copy of the CDDL at
 * http://www.netbeans.org/cddl.html.
 *
 */
package jascut.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The JAXB class that represents configuration of "rename field".
 *
 * @author tronicek
 */
@XmlType(name = "rename-field", propOrder = {
    "description", "type", "fieldOrig", "fieldNew"
})
public class RenameFieldXml implements Rule {

    private String description;
    private String type;
    private String fieldOrig;
    private String fieldNew;

    public RenameFieldXml() {
    }

    @Override
    public String getRefactoringClass() {
        return "refactoring.RenameField";
    }

    @XmlElement(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }    
    
    @XmlElement(name = "field-new")
    public String getFieldNew() {
        return fieldNew;
    }

    public void setFieldNew(String fieldNew) {
        this.fieldNew = fieldNew;
    }

    @XmlElement(name = "field-orig")
    public String getFieldOrig() {
        return fieldOrig;
    }

    public void setFieldOrig(String fieldOrig) {
        this.fieldOrig = fieldOrig;
    }

    @XmlElement(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
