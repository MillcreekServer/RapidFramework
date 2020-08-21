package util.trie;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static util.trie.TestShare.fillTrie;

public class StringListTrieTest {

    private StringListTrie trie;

    @Before
    public void init() {
        trie = new StringListTrie();
        fillTrie(trie);
    }

    @Test
    public void getAllStartsWith() {
        Collection<String> list = new LinkedList<>();
        list.add("Programming");
        list.add("Programming is good");

        assertEquals(list, trie.getAllStartsWith("Prog"));
    }
}