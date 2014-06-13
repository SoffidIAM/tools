/*
 * Copyright(c) Zdenek Tronicek, FIT CTU in Prague. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (CDDL). You can obtain a copy of the CDDL at
 * http://www.netbeans.org/cddl.html.
 *
 */
package jascut.xml;

import javax.xml.bind.annotation.XmlType;

/**
 * The interface that represents a refactoring rule.
 *
 * @author tronicek
 */
@XmlType
public interface Rule {

    String getRefactoringClass();
}
