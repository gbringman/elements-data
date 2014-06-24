package net.gregorybringman.elementsreduce.grammar;

import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link ElementsGrammar} class.
 * 
 * @author Gregory Bringman
 * 
 */
public class ElementsGrammarTest {

    /*
     * Test that the instance of the grammar returned from {@link
     * ElementsGrammar#grammarAsMap} contains all the POS keys of the grammar
     * definition.
     */
    @Test
    public void grammarAsMap() {
        Map<String, Pattern> grammar = ElementsGrammar.grammarAsMap();
        for (String key : ElementsGrammar.GRAMMAR_KEYS) {
            Assert.assertTrue(grammar.containsKey(key));
        }
    }

    /*
     * Test that the same grammar object reference is always returned upon
     * subsequent calls to {@link ElementsGrammar#grammarAsMap()}.
     */
    @Test
    public void grammarAsMapIdempotent() {
        Map<String, Pattern> grammar1 = ElementsGrammar.grammarAsMap();
        Map<String, Pattern> grammar2 = ElementsGrammar.grammarAsMap();

        Assert.assertTrue(grammar1 == grammar2);
    }
}