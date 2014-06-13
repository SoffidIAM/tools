/*
 * Copyright(c) Zdenek Tronicek, FIT CTU in Prague. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (CDDL). You can obtain a copy of the CDDL at
 * http://www.netbeans.org/cddl.html.
 *
 */
package jascut.xml;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/**
 * The JAXB class that represents configuration of "add
 *
 * @Override".
 *
 * @author tronicek
 */
@XmlType(name = "factory-method",
propOrder = {"description", "typeOrig", "argsOrig", "typeNew", "typeArgsNew", "methodNew", "argsNew"})
public class FactoryMethodXml implements Rule {

    private String description;
    private String typeOrig;
    private String argsOrig;
    private String typeNew;
    private String typeArgsNew;
    private String methodNew;
    private List<ArgumentXml> argsNew;

    @Override
    public String getRefactoringClass() {
        return "refactoring.FactoryMethod";
    }

    @XmlElement(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name = "type-orig")
    public String getTypeOrig() {
        return typeOrig;
    }

    public void setTypeOrig(String typeOrig) {
        this.typeOrig = typeOrig;
    }

    @XmlElement(name = "args-orig")
    public String getArgsOrig() {
        return argsOrig;
    }

    public void setArgsOrig(String argsOrig) {
        this.argsOrig = argsOrig;
    }

    @XmlElement(name = "type-new")
    public String getTypeNew() {
        return typeNew;
    }

    public void setTypeNew(String typeNew) {
        this.typeNew = typeNew;
    }

    @XmlElement(name = "type-args-new")
    public String getTypeArgsNew() {
        return typeArgsNew;
    }

    public void setTypeArgsNew(String typeArgsNew) {
        this.typeArgsNew = typeArgsNew;
    }

    @XmlElement(name = "method-new")
    public String getMethodNew() {
        return methodNew;
    }

    public void setMethodNew(String methodNew) {
        this.methodNew = methodNew;
    }

    @XmlElementWrapper(name = "args-new")
    @XmlElement(type = ArgumentXml.class, name = "arg")
    public List<ArgumentXml> getArgsNew() {
        return argsNew;
    }

    public void setArgsNew(List<ArgumentXml> argsNew) {
        this.argsNew = argsNew;
    }
}
