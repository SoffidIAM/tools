/*
 * Copyright(c) Zdenek Tronicek, FIT CTU in Prague. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (CDDL). You can obtain a copy of the CDDL at
 * http://www.netbeans.org/cddl.html.
 *
 */
package jascut.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The JAXB class that represents an argument of method call.
 *
 * @author tronicek
 */
@XmlType(name = "arg", propOrder = {"ref", "name", "type", "value"})
public class ArgumentXml {

    private Integer ref;
    private String name;
    private String type;
    private ArgumentValueXml value;

    public ArgumentXml() {
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "ref")
    public Integer getRef() {
        return ref;
    }

    public void setRef(Integer ref) {
        this.ref = ref;
    }

    @XmlElement(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement(name = "value")
    public ArgumentValueXml getValue() {
        return value;
    }

    public void setValue(ArgumentValueXml value) {
        this.value = value;
    }
}
