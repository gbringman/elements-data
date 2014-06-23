package net.gregorybringman.elementsreduce.grammar;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * A (poor person's) context free grammar for the Mayer data on page
 * correspondences of versions of the <em>Éléments de Physiologie</em>.
 * 
 * @author Gregory Bringman
 */
public class ElementsGrammar {

	public static final String OR = "|";
	public static final String LINE_OR_PAGE = "(\\d+)";
	public static final String PAGE_TO_PAGE = "(\\s*à+\\s*)";
	public static final String LEVEL_DELIMITER = "(\\,(\\s)*)*";
	public static final String TEXT = "[^\\d]+";
	public static final String RANGE_DELIMITER = "\\-";
	public static final String PAGE_RECTO = LINE_OR_PAGE + "r";
	public static final String PAGE_VERSO = LINE_OR_PAGE + "v";
	public static final String PAGE_TYPE = "r|v";

	public static final Pattern PAGE = Pattern.compile(LINE_OR_PAGE
			+ PAGE_TO_PAGE + "*" + LINE_OR_PAGE + "+" + LEVEL_DELIMITER + "");
	public static final Pattern PAGE_WITH_TEXT = Pattern.compile(LINE_OR_PAGE
			+ LEVEL_DELIMITER + TEXT + OR + LINE_OR_PAGE + LEVEL_DELIMITER
			+ LINE_OR_PAGE + TEXT);
	public static final Pattern PAGE_WITH_LINE_RANGE = Pattern
			.compile(LINE_OR_PAGE + LEVEL_DELIMITER + LINE_OR_PAGE
					+ RANGE_DELIMITER + LINE_OR_PAGE);
	public static final Pattern PAGE_AND_LINE_TO_PAGE_AND_LINE = Pattern
			.compile(LINE_OR_PAGE + LEVEL_DELIMITER + LINE_OR_PAGE
					+ PAGE_TO_PAGE + LINE_OR_PAGE + LEVEL_DELIMITER
					+ LINE_OR_PAGE);
	public static final Pattern LINE_RANGE = Pattern.compile(LINE_OR_PAGE
			+ RANGE_DELIMITER + LINE_OR_PAGE);
	public static final Pattern LINE_TO_PAGE_WITH_LINE = Pattern
			.compile(LINE_OR_PAGE + LEVEL_DELIMITER + LINE_OR_PAGE + "*"
					+ PAGE_TO_PAGE + LINE_OR_PAGE + LEVEL_DELIMITER
					+ LINE_OR_PAGE);
	public static final Pattern STRUCTURAL_TEXT = Pattern.compile(TEXT);
	public static final Pattern FOLIO_PAGE = Pattern.compile(PAGE_RECTO + OR
			+ PAGE_VERSO);

	private static final Map<String, Pattern> grammar = Maps.newHashMap();
	public static final String[] GRAMMAR_KEYS = { "page", "pagewtext",
			"pagewlinerng", "pagelinetopageline", "linerng", "linetopagewline",
			"structuraltext" };

	/**
	 * Bundle and return all rules of the grammar. Only populate the map the
	 * first time for which {@link ElementsGrammar#grammarAsMap()} is called.
	 * 
	 * @return A {@link Map} of the bundled grammar.
	 */
	public static Map<String, Pattern> grammarAsMap() {

		if (grammar.isEmpty()) {
			Iterator<String> it = Lists.newArrayList(GRAMMAR_KEYS).iterator();
			grammar.put(it.next(), PAGE);
			grammar.put(it.next(), PAGE_WITH_TEXT);
			grammar.put(it.next(), PAGE_WITH_LINE_RANGE);
			grammar.put(it.next(), PAGE_AND_LINE_TO_PAGE_AND_LINE);
			grammar.put(it.next(), LINE_RANGE);
			grammar.put(it.next(), LINE_TO_PAGE_WITH_LINE);
			grammar.put(it.next(), STRUCTURAL_TEXT);
		}

		return grammar;
	}
}