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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * The JAXB class that represents configuration of "change method signature".
 *
 * @author tronicek
 */
@XmlType(name = "change-method-signature", propOrder = {
    "description", "type", "methodOrig", "argsOrig", "modsNew",
    "returnNew", "methodNew", "argsNew", "throwsNew"
})
public class ChangeMethodSignatureXml implements Rule {

    private String description;
    private String type;
    private String methodOrig;
    private String argsOrig;
    private String modsNew;
    private String returnNew;
    private String methodNew;
    private List<ArgumentXml> argsNew;
    private List<String> throwsNew;

    public ChangeMethodSignatureXml() {
    }

    @Override
    public String getRefactoringClass() {
        return "refactoring.ChangeMethodSignature";
    }

    @XmlElement(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    @XmlElementWrapper(name = "args-new")
    @XmlElement(type = ArgumentXml.class, name = "arg")
    public List<ArgumentXml> getArgsNew() {
        return argsNew;
    }

    public void setArgsNew(List<ArgumentXml> argsNew) {
        this.argsNew = argsNew;
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

    public void setMethodOrig(String method) {
        this.methodOrig = method;
    }

    @XmlElement(name = "mods-new")
    public String getModsNew() {
        return modsNew;
    }

    public void setModsNew(String modsNew) {
        this.modsNew = modsNew;
    }

    @XmlElement(name = "return-new")
    public String getReturnNew() {
        return returnNew;
    }

    public void setReturnNew(String returnNew) {
        this.returnNew = returnNew;
    }

    @XmlJavaTypeAdapter(CommaDelimitedListAdapter.class)
    @XmlElement(name = "throws-new")
    public List<String> getThrowsNew() {
        return throwsNew;
    }

    public void setThrowsNew(List<String> throwsNew) {
        this.throwsNew = throwsNew;
    }

    @XmlElement(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
