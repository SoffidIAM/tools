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
 * The JAXB adapter that omits superfluous spaces from strings.
 *
 * @author tronicek
 */
public class MyNormalizedStringAdapter extends XmlAdapter<String, String> {

    @Override
    public String marshal(String v) {
        return v;
    }

    @Override
    public String unmarshal(String v) {
        String p = v.replaceAll("\\s+", " ");
        StringBuilder sb = new StringBuilder();
        int i = -1;
        for (int j = 0; j < p.length(); j++) {
            char c = p.charAt(j);
            if (c == '(' || c == ')' || c == '<' || c == '>'
                    || c == '[' || c == ']' || c == ';' || c == '.'
                    || c == ',' || c == '&') {
                String s = p.substring(i + 1, j);
                sb.append(s.trim());
                sb.append(c);
                i = j;
            }
        }
        String s = p.substring(i + 1);
        sb.append(s.trim());
        return sb.toString();
    }
}
