package io.github.wysohn.rapidframework3.utils.trie;

import io.github.wysohn.rapidframework4.utils.trie.Trie;
import org.junit.Before;
import org.junit.Test;

import static io.github.wysohn.rapidframework3.utils.trie.TestShare.fillTrie;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TrieTest {

    private Trie trie;

    @Before
    public void createExampleTrie() {
        trie = new Trie();
        fillTrie(trie);
    }

    @Test
    public void insert() {
        assertFalse(trie.isEmpty());
    }

    @Test
    public void find() {
        assertFalse(trie.find("3"));
        assertFalse(trie.find("vida"));
        assertTrue(trie.find("life"));
    }

    @Test
    public void delete() {
        assertTrue(trie.find("Programming"));

        trie.delete("Programming");
        assertFalse(trie.find("Programming"));
    }
}