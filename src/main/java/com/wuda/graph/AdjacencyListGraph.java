package com.wuda.graph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.wuda.graph.Relationship.RelationshipStatus;
import com.wuda.graph.Vertex.VertexStatus;

/**
 * 图的邻接表实现.
 *
 * @param <E>
 *         图中顶点的元素类型
 * @author wuda
 */
public class AdjacencyListGraph<E extends Comparable<E>> implements Graph {
    /**
     * 图中所有顶点的集合.
     */
    private LinkedList<Vertex<E>> vertices = new LinkedList<>();

    /**
     * 图中所有关系的集合.这个不是组成图的必要元素,放这里只是为了查询更方便.
     */
    private static final LinkedList<Relationship> relationships = new LinkedList<>();

    /**
     * 如果此元素所在的顶点已经存在则返回,如果不存在则创建.
     *
     * @param vertexElement
     *         顶点元素
     * @return {@link Vertex}
     */
    public Vertex<E> getOrCreateVertex(E vertexElement) {
        Vertex<E> v = getVertex(vertexElement);
        if (v == null) {
            v = new Vertex<>(vertexElement);
            vertices.addLast(v);
        }
        return v;
    }

    /**
     * 获取此元素所在的顶点.
     *
     * @param vertexElement
     *         顶点中的元素
     * @return {@link Vertex}
     */
    public Vertex<E> getVertex(E vertexElement) {
        for (Vertex<E> vertex : vertices) {
            if (vertex.getElement().compareTo(vertexElement) == 0) {
                return vertex;
            }
        }
        return null;
    }

    /**
     * Dumps an Graph to a GraphViz's dot language description for
     * visualization. Example of use:
     * <pre>
     * graph.toDot("e:/graph.dot");
     * </pre>
     * and then, from command line:
     * <pre>
     * dot -Tpng -o e:/out.png e:/graph.dot
     * </pre>
     *
     * @param filePath
     *         dot文件完整路径
     * @throws IOException
     *         操作文件时异常
     * @see <a href="http://www.graphviz.org/">graphviz project</a>
     */
    public void toDot(String filePath) throws IOException {
        StringBuilder builder = new StringBuilder("digraph g {\n");
        for (Vertex<E> v : vertices) {
            LinkedList<Relationship<E>> relationships = v.getRelationships();
            for (Relationship relationship : relationships) {
                builder.append(relationship.getStartVertex().getElement());
                builder.append(" -> ");
                builder.append(relationship.getEndVertex().getElement());
                builder.append(" [label=\"");
                builder.append(relationship.getId());
                builder.append(" ");
                builder.append(relationship.getRelationshipType());
                builder.append("\"]");
                builder.append(";\n");
            }
        }
        builder.append("}");
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(new File(filePath)));
            writer.write(builder.toString());
            writer.flush();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * 注册一个<strong>崭新的</strong>关系对象.如果已经注册过,不要重复调用此方法注册,此方法
     * <strong>不会检查重复的关系</strong>.只需要在
     * {@link Vertex#createRelationshipTo(Vertex, RelationshipType)}
     * 成功创建一个新的关系时才需要调用此方法.
     *
     * @param relationship
     *         Relationship
     */
    static void registerNewRelationship(final Relationship relationship) {
        relationships.addLast(relationship);
    }

    /**
     * 广度优先遍历.
     *
     * @param start
     *         开始节点
     */
    public LinkedList<Vertex<E>> bfs(Vertex<E> start) {
        LinkedList<Vertex<E>> backtrack = new LinkedList<>();// 回溯
        backtrack.addLast(start);
        LinkedList<Vertex<E>> result = new LinkedList<>();// 保存结果
        while (!backtrack.isEmpty()) {
            Vertex<E> v = backtrack.removeFirst();
            result.addLast(v);
            v.setStatus(VertexStatus.VISITED);
            LinkedList<Relationship<E>> relationships = v.getRelationships();// 顶点v的所有邻接
            for (Relationship<E> relationship : relationships) {
                Vertex<E> neighbor = relationship.getEndVertex();
                if (neighbor.getStatus() == VertexStatus.UNDISCOVERED) {
                    backtrack.addLast(neighbor);
                    neighbor.setStatus(VertexStatus.DISCOVERED);
                }
            }
        }
        return result;
    }

    /**
     * 深度优先遍历.
     *
     * @param start
     *         开始节点
     */
    public void dfs(Vertex<E> start) {
        start.setStatus(VertexStatus.DISCOVERED);
        System.out.println(start);
        LinkedList<Relationship<E>> relationships = start.getRelationships();
        for (Relationship<E> relationship : relationships) {
            Vertex<E> neighbor = relationship.getEndVertex();
            switch (neighbor.getStatus()) {
                case UNDISCOVERED:
                    neighbor.setParent(start);
                    dfs(neighbor);
                    break;
                case DISCOVERED:
                    break;
                case VISITED:
                    break;
                default:
                    break;
            }
        }
        start.setStatus(VertexStatus.VISITED);
    }

    /**
     * 获取两点之间<strong>尽可能多的</strong>路径.对于无环图,可以获得所有的路径;对于有环图,则只能尽可能多的获取路径.
     *
     * @param start
     *         开始顶点
     * @param end
     *         结束顶点
     * @param paths
     *         将所有发现的路径保存在此容器中
     */
    public void getMorePaths(Vertex<E> start, Vertex<E> end, List<Path> paths) {
        LinkedList<Relationship<E>> relationships = start.getRelationships();
        for (Relationship<E> forwardRelationship : relationships) {
            Vertex<E> neighbor = forwardRelationship.getEndVertex();
            LinkedList<Relationship<E>> reverseRelationships = neighbor.getRelationships(start);
            if (reverseRelationships != null && !reverseRelationships.isEmpty()) {// 两个顶点之间有环
                for (Relationship reverseRelationship : reverseRelationships) {
                    reverseRelationship.setStatus(RelationshipStatus.BACKWARD);
                }
                if (forwardRelationship.getStatus().equals(RelationshipStatus.BACKWARD)) {
                    continue;
                }
            }
            neighbor.setParent(start);
            if (neighbor.equals(end)) {
                Path path = extractPath(neighbor);
                paths.add(path);
            } else {
                getMorePaths(neighbor, end, paths);
            }
        }
    }

    /**
     * 提取路径.
     *
     * @param end
     *         结束顶点
     * @return 路径
     */
    private Path extractPath(Vertex end) {
        Path path = new Path();
        Vertex current = end;
        while (current != null) {
            path.addFirst(current);
            current = current.getParent();
        }
        return path;
    }
}
