package io.github.wysohn.rapidframework3.utils.trie;

import io.github.wysohn.rapidframework4.utils.trie.StringListTrie;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;

import static io.github.wysohn.rapidframework3.utils.trie.TestShare.fillTrie;
import static org.junit.Assert.assertEquals;

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