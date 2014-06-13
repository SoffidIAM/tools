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
 * The JAXB class that represents configuration of "move method".
 *
 * @author tronicek
 */
@XmlType(name = "move-method", propOrder = {
    "description", "typeOrig", "methodOrig", "argsOrig", "typeNew", "methodNew"
})
public class MoveMethodXml implements Rule {

    private String description;
    private String typeOrig;
    private String methodOrig;
    private String argsOrig;
    private String typeNew;
    private String methodNew;

    public MoveMethodXml() {
    }

    @Override
    public String getRefactoringClass() {
        return "refactoring.MoveMethod";
    }

    @XmlElement(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name = "args-orig")
    public String getArgsOrig() {
        return argsOrig;
    }

    public void setArgsOrig(String argsOrig) {
        this.argsOrig = argsOrig;
    }

    @XmlElement(name = "method-new")
    public String getMethodNew() {
        return methodNew;
    }

    public void setMethodNew(String methodNew) {
        this.methodNew = methodNew;
    }

    @XmlElement(name = "method-orig")
    public String getMethodOrig() {
        return methodOrig;
    }

    public void setMethodOrig(String methodOrig) {
        this.methodOrig = methodOrig;
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
