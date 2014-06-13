/*
 * Copyright(c) Zdenek Tronicek, FIT CTU in Prague. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (CDDL). You can obtain a copy of the CDDL at
 * http://www.netbeans.org/cddl.html.
 *
 */
package jascut.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * The JAXB adapter that reads strings as they are.
 *
 * @author tronicek
 */
public class MyIdentityStringAdapter extends XmlAdapter<String, String> {

    @Override
    public String marshal(String v) {
        return v;
    }

    @Override
    public String unmarshal(String v) {
        return v;
    }
}
