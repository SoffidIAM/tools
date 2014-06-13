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
 * The JAXB class that represents configuration of "rename method".
 *
 * @author tronicek
 */
@XmlType(name = "rename-method", propOrder = {
    "description", "type", "methodOrig", "argsOrig", "methodNew"
})
public class RenameMethodXml implements Rule {

    private String description;
    private String type;
    private String methodOrig;
    private String argsOrig;
    private String methodNew;

    public RenameMethodXml() {
    }

    @Override
    public String getRefactoringClass() {
        return "refactoring.RenameMethod";
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

    @XmlElement(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
