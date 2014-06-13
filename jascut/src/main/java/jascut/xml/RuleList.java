/*
 * Copyright(c) Zdenek Tronicek, FIT CTU in Prague. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (CDDL). You can obtain a copy of the CDDL at
 * http://www.netbeans.org/cddl.html.
 *
 */
package jascut.xml;

import java.io.File;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The JAXB class that represents a list of refactoring rules.
 *
 * @author tronicek
 */
@XmlRootElement(name = "refactorings")
public class RuleList {

    private List<Rule> rules;

    public RuleList() {
    }

    @XmlElements({
        @XmlElement(name = "add-override", type = AddOverrideXml.class),
        @XmlElement(name = "change-method-signature", type = ChangeMethodSignatureXml.class),
        @XmlElement(name = "convert-to-diamond", type = ConvertToDiamondXml.class),
        @XmlElement(name = "factory-method", type=FactoryMethodXml.class),
        @XmlElement(name = "field-to-method", type=FieldToMethodXml.class),
        @XmlElement(name = "format-literal", type = FormatLiteralXml.class),
        @XmlElement(name = "move-field", type = MoveFieldXml.class),
        @XmlElement(name = "move-method", type = MoveMethodXml.class),
        @XmlElement(name = "rename-field", type = RenameFieldXml.class),
        @XmlElement(name = "rename-method", type = RenameMethodXml.class),
        @XmlElement(name = "rename-type", type = RenameTypeXml.class)
    })
    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public static RuleList read(String fileName) throws JAXBException {
        JAXBContext ctx = JAXBContext.newInstance("jascut.xml", RuleList.class.getClassLoader());
        Unmarshaller um = ctx.createUnmarshaller();
        return (RuleList) um.unmarshal(new File(fileName));
    }
}
