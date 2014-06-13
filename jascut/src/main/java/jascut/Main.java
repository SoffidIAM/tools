/*
 * Copyright(c) Zdenek Tronicek, FIT CTU in Prague. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (CDDL). You can obtain a copy of the CDDL at
 * http://www.netbeans.org/cddl.html.
 *
 */
package jascut;

import java.io.FileReader;
import java.util.Properties;
import jascut.xml.RuleList;
import reporting.Report;

/**
 * The entry class.
 *
 * @author tronicek
 */
public class Main {

    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                System.err.println("expected argument: properties file (e.g. JavaAPI.properties)");
                System.exit(0);
            }
            Properties conf = new Properties();
            conf.load(new FileReader(args[0]));
            Engine eng = new Engine(conf);
            RuleList rlist = RuleList.read(conf.getProperty("config"));
            Report.rules(rlist.getRules().size());
            eng.perform(rlist);
            Report.print();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
