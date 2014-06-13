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
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * The JAXB class that represents a value of argument.
 *
 * @author tronicek
 */
@XmlType(name = "value", propOrder = {"kind", "value"})
public class ArgumentValueXml {

    @XmlType
    public enum Kind {

        @XmlEnumValue("reference")
        REFERENCE,
        @XmlEnumValue("literal")
        LITERAL,
        @XmlEnumValue("identifier")
        IDENTIFIER,
        @XmlEnumValue("null")
        NULL
    }
    private Kind kind;
    private String value;

    @XmlAttribute(name = "kind")
    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    @XmlValue
    @XmlJavaTypeAdapter(value = MyIdentityStringAdapter.class)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
