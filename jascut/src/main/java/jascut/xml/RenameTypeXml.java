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
 * The JAXB class that represents configuration of "rename type".
 *
 * @author tronicek
 */
@XmlType(name = "rename-type", propOrder = {
    "description", "typeOrig", "typeNew"
})
public class RenameTypeXml implements Rule {

    private String description;
    private String typeOrig;
    private String typeNew;

    public RenameTypeXml() {
    }

    @Override
    public String getRefactoringClass() {
        return "refactoring.RenameType";
    }
    
    @XmlElement(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    @XmlElement(name = "type-new")
    public String getTypeNew() {
        return typeNew;
    }

    public void setTypeNew(String typeNew) {
        this.typeNew = typeNew;
    }

    @XmlElement(name = "type-orig")
    public String getTypeOrig() {
        return typeOrig;
    }

    public void setTypeOrig(String typeOrig) {
        this.typeOrig = typeOrig;
    }
}
