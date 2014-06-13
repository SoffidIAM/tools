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
 * The JAXB class that represents configuration of "convert to diamond".
 *
 * @author tronicek
 */
@XmlType(name = "convert-to-diamond", propOrder = {"description"})
public class ConvertToDiamondXml implements Rule {

    private String description;

    @Override
    public String getRefactoringClass() {
        return "refactoring.ConvertToDiamond";
    }

    @XmlElement(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
