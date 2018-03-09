package com.wuda.graph;

import java.util.LinkedList;


public class AdjacencyListGraphTest {

    public static AdjacencyListGraph<Long> gen() {
        AdjacencyListGraph<Long> graph = new AdjacencyListGraph();

        Vertex<Long> v6 = graph.getOrCreateVertex(6L);
        Vertex<Long> v4 = graph.getOrCreateVertex(4L);
        Vertex<Long> v2 = graph.getOrCreateVertex(2L);
        Vertex<Long> v3 = graph.getOrCreateVertex(3L);
        Vertex<Long> v1 = graph.getOrCreateVertex(1L);

        Vertex<Long> v5 = graph.getOrCreateVertex(5L);

        v1.createRelationshipTo(v2, MyRelationshipType.OUT);

        v2.createRelationshipTo(v4, MyRelationshipType.OUT);
        v2.createRelationshipTo(v1, MyRelationshipType.OUT);

        v3.createRelationshipTo(v6, MyRelationshipType.OUT);

        v4.createRelationshipTo(v6, MyRelationshipType.OUT);
        v4.createRelationshipTo(v3, MyRelationshipType.OUT);

        v5.createRelationshipTo(v6, MyRelationshipType.OUT);

        v1.createRelationshipTo(v3, MyRelationshipType.OUT);
        v1.createRelationshipTo(v6, MyRelationshipType.OUT);

        v3.createRelationshipTo(v5, MyRelationshipType.OUT);
        v3.createRelationshipTo(v4, MyRelationshipType.OUT);

        v2.createRelationshipTo(v6, MyRelationshipType.OUT);

        return graph;
    }


    public static void main(String[] args) {
        AdjacencyListGraph<Long> graph = gen();
        Vertex<Long> v1 = graph.getVertex(1L);
        Vertex<Long> v2 = graph.getVertex(2L);
        Vertex<Long> v3 = graph.getVertex(3L);

        /*
         * 找出v2到v3的所有路径.
         */
        LinkedList<Path> paths = new LinkedList<>();
        graph.getMorePaths(v2, v3, paths);
        System.out.println("====v2-v3的所有路径====");
        for (Path path : paths) {
            System.out.println(path.getVertices());
        }
        /*
         * 从v1节点开始,执行广度优先遍历.
         */
        System.out.println("====广度优先遍历====");
        LinkedList<Vertex<Long>> result = graph.bfs(v1);
        System.out.println(result);
    }

}
