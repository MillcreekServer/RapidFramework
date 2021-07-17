package io.github.wysohn.rapidframework4.utils.graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

//https://www.electricmonk.nl/docs/dependency_resolving_algorithm/dependency_resolving_algorithm.html
public class DependencyGraph {
    private Object[] nodes;
    private boolean[][] edges;

    /**
     * Create an un-initialized graph.
     *
     * @param nodes
     */
    public DependencyGraph(Object... nodes) {
        this.nodes = nodes;
        edges = new boolean[this.nodes.length][this.nodes.length];
    }

    public DependencyGraph(Collection<?> nodes) {
        this.nodes = nodes.toArray();
        edges = new boolean[this.nodes.length][this.nodes.length];
    }

    /**
     * Add dependency. (ex. Node[from] requires Node[to])
     *
     * @param from target node
     * @param to   other node to connect with
     */
    public void addEdge(int from, int to) {
        if (from < 0 || from >= nodes.length || to < 0 || to >= nodes.length)
            throw new ArrayIndexOutOfBoundsException("Incorrect index for the node.");

        edges[from][to] = true;
    }

    public void removeEdge(int from, int to) {
        if (from < 0 || from >= nodes.length || to < 0 || to >= nodes.length)
            throw new ArrayIndexOutOfBoundsException("Incorrect index for the node.");

        edges[from][to] = false;
    }

    /**
     * Resolve dependency and return the nodes in order so that each node can be safely
     * used without a dependency issues.
     *
     * @return Nodes that are sorted in a way that doesn't cause dependency issue.
     */
    public <T> List<T> resolveDependency() {
        List<Integer> resolved = new LinkedList<>();
        boolean[] seen = new boolean[nodes.length];

        for (int i = 0; i < nodes.length; i++)
            resolveDependency(i, resolved, seen);

        return resolved.stream()
                .map(index -> nodes[index])
                .map(obj -> (T) obj)
                .collect(Collectors.toList());
    }

    private void resolveDependency(int node, List<Integer> resolve, boolean[] seen) {
        if (seen[node])
            return;

        seen[node] = true;
        for (int edge = 0; edge < nodes.length; edge++) {
            if (node == edge)
                continue; // itself

            if (!edges[node][edge])
                continue; // not connected

            if (resolve.contains(edge))
                continue; // already finished node

            if (seen[edge])
                throw new RuntimeException(String.format("Circular dependency detected. %s -> %s",
                        nodes[node], nodes[edge]));

            resolveDependency(edge, resolve, seen);
        }
        resolve.add(node);
    }
}
