/*
 * Copyright(c) Zdenek Tronicek, FIT CTU in Prague. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (CDDL). You can obtain a copy of the CDDL at
 * http://www.netbeans.org/cddl.html.
 *
 */
package reporting;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author tronicek
 */
public class Report {

    private static Report r = new Report();
    private Integer ruleCount;
    private Map<String, Integer> changeCounts = new TreeMap<String, Integer>();
    private Map<String, Integer> warningCounts = new TreeMap<String, Integer>();

    private Report() {
    }

    public static void reset() {
        r = new Report();
    }

    public static void rules(Integer count) {
        r.ruleCount = count;
    }

    public static void changes(String file, Integer count) {
        r.changeCounts.put(file, count);
    }

    public static void warnings(String file, Integer count) {
        r.warningCounts.put(file, count);
    }

    public static void print() {
        r.printReport();
    }

    private void printReport() {
        System.out.printf("number of rules: %d%n", ruleCount);
        System.out.println("--- changes ---");
        int tc = 0;
        for (String f : changeCounts.keySet()) {
            Integer c = changeCounts.get(f);
            if (c > 0) {
                System.out.printf("file: %s, number of changes: %d%n", f, c);
                tc += c;
            }
        }
        System.out.printf("total number of changes: %d%n", tc);
        System.out.println("--- warnings ---");
        int tw = 0;
        for (String f : warningCounts.keySet()) {
            Integer c = warningCounts.get(f);
            if (c > 0) {
                System.out.printf("file: %s, number of warnings: %d%n", f, c);
                tw += c;
            }
        }
        System.out.printf("total number of warnings: %d%n", tw);
    }
}
