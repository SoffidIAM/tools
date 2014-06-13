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
 * The JAXB class that represents configuration of "format literal".
 *
 * @author tronicek
 */
@XmlType(name = "format-literal", propOrder = {"description", "groups", "minimumLength"})
public class FormatLiteralXml implements Rule {

    private String description;
    private int groups = 3;
    private int minimumLength = 0;

    @Override
    public String getRefactoringClass() {
        return "refactoring.FormatLiteral";
    }

    @XmlElement(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlAttribute(name = "groups")
    public int getGroups() {
        return groups;
    }

    public void setGroups(int groups) {
        this.groups = groups;
    }

    @XmlAttribute(name = "minimum-length")
    public int getMinimumLength() {
        return minimumLength;
    }

    public void setMinimumLength(int minimumLength) {
        this.minimumLength = minimumLength;
    }
}
