package io.github.wysohn.rapidframework3.utils.graph;

import io.github.wysohn.rapidframework4.utils.graph.DependencyGraph;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DependencyGraphTest {

    @Test
    public void resolveDependency() {
        DependencyGraph graph = new DependencyGraph("a", "b", "c", "d", "e");
        graph.addEdge(0, 1);
        graph.addEdge(0, 3);
        graph.addEdge(1, 2);
        graph.addEdge(0, 4);
        graph.addEdge(2, 3);
        graph.addEdge(2, 4);

        List<Object> expected = new LinkedList<>();
        expected.add("d");
        expected.add("e");
        expected.add("c");
        expected.add("b");
        expected.add("a");
        assertEquals(expected, graph.resolveDependency());
    }

    @Test(expected = RuntimeException.class)
    public void resolveDependency2() {
        DependencyGraph graph = new DependencyGraph("a", "b");
        graph.addEdge(0, 1);
        graph.addEdge(1, 0);

        graph.resolveDependency();
    }

    @Test(expected = RuntimeException.class)
    public void resolveDependency3() {
        DependencyGraph graph = new DependencyGraph("a", "b", "c", "d", "e");
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);
        graph.addEdge(4, 0);

        graph.resolveDependency();
    }

    @Test
    public void resolveDependency4() {
        DependencyGraph graph = new DependencyGraph("a", "b", "c", "d", "e");
        graph.addEdge(0, 1);
        graph.addEdge(0, 3);
        graph.addEdge(1, 2);
        graph.addEdge(0, 4);
        graph.addEdge(2, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);

        List<Object> expected = new LinkedList<>();
        expected.add("e");
        expected.add("d");
        expected.add("c");
        expected.add("b");
        expected.add("a");
        assertEquals(expected, graph.resolveDependency());
    }

    @Test
    public void resolveDependency5() {
        DependencyGraph graph = new DependencyGraph("a", "b", "c", "d", "e");

        List<Object> expected = new LinkedList<>();
        expected.add("a");
        expected.add("b");
        expected.add("c");
        expected.add("d");
        expected.add("e");
        assertEquals(expected, graph.resolveDependency());
    }

    @Test
    public void resolveDependency6() {
        DependencyGraph graph = new DependencyGraph("a", "b", "c", "d", "e");
        graph.addEdge(0, 1);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);

        List<Object> expected = new LinkedList<>();
        expected.add("b");
        expected.add("a");
        expected.add("e");
        expected.add("d");
        expected.add("c");
        assertEquals(expected, graph.resolveDependency());
    }
}