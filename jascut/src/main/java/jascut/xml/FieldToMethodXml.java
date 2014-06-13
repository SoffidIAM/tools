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
 * The JAXB class that represents configuration of "field to method".
 *
 * @author tronicek
 */
@XmlType(name = "field-to-method", propOrder = {
    "description", "type", "fieldOrig", "getterNew", "setterNew"
})
public class FieldToMethodXml implements Rule {

    private String description;
    private String type;
    private String fieldOrig;
    private String getterNew;
    private String setterNew;

    public FieldToMethodXml() {
    }

    @Override
    public String getRefactoringClass() {
        return "refactoring.FieldToMethod";
    }

    @XmlElement(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }    
    
    @XmlElement(name = "field-orig")
    public String getFieldOrig() {
        return fieldOrig;
    }

    public void setFieldOrig(String fieldOrig) {
        this.fieldOrig = fieldOrig;
    }

    @XmlElement(name = "getter-new")
    public String getGetterNew() {
        return getterNew;
    }

    public void setGetterNew(String getterNew) {
        this.getterNew = getterNew;
    }

    @XmlElement(name = "setter-new")
    public String getSetterNew() {
        return setterNew;
    }

    public void setSetterNew(String setterNew) {
        this.setterNew = setterNew;
    }

    @XmlElement(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
