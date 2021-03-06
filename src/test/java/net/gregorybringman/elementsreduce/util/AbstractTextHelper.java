package net.gregorybringman.elementsreduce.util;

import net.gregorybringman.elementsreduce.ElementsVersionsSearchReducer;

/**
 * Provides expected input or output text for unit tests for the
 * {@link ElementsVersionsSearchReducer}
 * 
 * @author Gregory Bringman
 * 
 */
public abstract class AbstractTextHelper {

    public static String tei(String body) {
        StringBuilder sb = new StringBuilder();
        sb.append(teiProlog());
        sb.append(teiHeader());
        sb.append(body == null ? teiBodyDPVOnly() : body);
        sb.append(teiPostlog());

        return sb.toString();
    }

    private static String teiProlog() {
        return "<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">";
    }

    private static String teiHeader() {
        return "<teiHeader>" + "<fileDesc>" + "<titleStmt>"
                + "<title>Elements of Physiology</title>"
                + "<author>Denis Diderot" + "</author>" + "<respStmt>"
                + "<resp>Translated and Encoded by</resp>"
                + "<name>Gregory Bringman</name>" + "</respStmt>"
                + "</titleStmt>" + "</fileDesc>" + "</teiHeader>";
    }

    protected static String teiBodyDPVOnly() {
        return "    <text xml:id=\"d1\">"
                + "<body xml:id=\"d2\">"
                + "<pb n=\"293\" />"
                + "<head>Preface</head>"
                + "<p>"
                + "<line>Mr *** conceived of the project of drawing up the elements of physiology while</line>"
                + "<line>reading the works of Baron de Haller. For several months, he collected</line>"
                + "<pb n=\"294\" />" + "</body>" + "</text>";
    }

    protected String teiBodyDPVOnlyAbnormal() {
        return "    <text xml:id=\"d1\">"
                + "<body xml:id=\"d2\">"
                + "<pb    n=\"293\"                          />"
                + "<head>Preface</head>"
                + "<p>"
                + "<line>Mr *** conceived of the project of drawing up the elements of physiology while</line>"
                + "<line>reading the works of Baron de Haller. For several months, he collected</line>"
                + "<pb n=\"294\"             />" + "</body>" + "</text>";
    }

    protected static String retrievedTeiSection() {
        return "<head>Preface</head>"
                + "<p>"
                + "<line>Mr *** conceived of the project of drawing up the elements of physiology while</line>"
                + "<line>reading the works of Baron de Haller. For several months, he collected</line>";
    }

    private static String teiPostlog() {
        return "</TEI>";
    }

    public static String expectedMarkupDPVOnly() {
        return "<div id=\"DPV\">"
                + "Mr *** conceived of the project of drawing up the elements of physiology while </div>"
                + "reading the works of Baron de Haller. For several months, he collected"
                + "</div>";
    }

    public static String expectedMarkupLOnly() {
        return "<div id=\"L\">" + "</div>";
    }
}