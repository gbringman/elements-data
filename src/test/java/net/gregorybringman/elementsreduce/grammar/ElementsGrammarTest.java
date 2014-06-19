package net.gregorybringman.elementsreduce.grammar;

import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class ElementsGrammarTest {
	
	@Test
	public void grammarAsMap() {
		Map<String, Pattern> grammar = ElementsGrammar.grammarAsMap();
		for (String key : ElementsGrammar.GRAMMAR_KEYS) {
			Assert.assertTrue(grammar.containsKey(key));
		}
	}
	
	@Test
	public void grammarAsMapIdempotent() {
		Map<String, Pattern> grammar1 = ElementsGrammar.grammarAsMap();
		Map<String, Pattern> grammar2 = ElementsGrammar.grammarAsMap();	
		
		Assert.assertTrue(grammar1 == grammar2);
	}
}