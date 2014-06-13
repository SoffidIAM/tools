/*
 * Copyright(c) Zdenek Tronicek, FIT CTU in Prague. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (CDDL). You can obtain a copy of the CDDL at
 * http://www.netbeans.org/cddl.html.
 *
 */
package jascut.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * The JAXB adapter for comma-delimited list of strings.
 *
 * @author tronicek
 */
public class CommaDelimitedListAdapter extends XmlAdapter<String, List<String>> {

    @Override
    public List<String> unmarshal(String str) throws Exception {
        List<String> p = new ArrayList<String>();
        for (String s : str.split(",")) {
            String t = s.trim();
            if (t.length() > 0) {
                p.add(t);
            }
        }
        return p;
    }

    @Override
    public String marshal(List<String> strs) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (String s : strs) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(s);
        }
        return sb.toString();
    }
}
