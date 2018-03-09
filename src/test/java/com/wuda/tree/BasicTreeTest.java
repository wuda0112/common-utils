package com.wuda.tree;

import java.util.List;

import com.wuda.tree.BasicTree.Node;

public class BasicTreeTest {

    public static void main(String[] args) {
        BasicTree<String> company = genOrganizationStructure(); // 生成公司组织架构
        BasicTreeTest.dfs(company); // 遍历并且打印
    }

    /**
     * 生成公司组织架构.
     *
     * @return 树形结构的组织架构
     */
    public static BasicTree genOrganizationStructure() {
        BasicTree tree = new BasicTree();
        Node 广东省总公司 = tree.createNode("广东省总公司");

        Node 销售部 = tree.createNode("销售部");
        Node 销售团队A = tree.createNode("销售团队A");
        Node 销售团队B = tree.createNode("销售团队B");
        Node 销售团队A_小明 = tree.createNode("销售团队A_小明");
        Node 销售团队B_小红 = tree.createNode("销售团队B_小红");


        Node 技术部 = tree.createNode("技术部");
        Node 技术部_lily = tree.createNode("技术部_lily");
        Node 技术部_lucy = tree.createNode("技术部_lucy");

        Node 运营部 = tree.createNode("运营部");
        Node 财务部 = tree.createNode("财务部");

        try {
            tree.createRelationShip(tree.getRoot(), 广东省总公司);
            tree.createRelationShip(广东省总公司, 销售部);
            tree.createRelationShip(广东省总公司, 技术部);
            tree.createRelationShip(广东省总公司, 运营部);
            tree.createRelationShip(广东省总公司, 财务部);

            tree.createRelationShip(销售部, 销售团队A);
            tree.createRelationShip(销售部, 销售团队B);

            tree.createRelationShip(销售团队A, 销售团队A_小明);
            tree.createRelationShip(销售团队B, 销售团队B_小红);

            tree.createRelationShip(技术部, 技术部_lily);
            tree.createRelationShip(技术部, 技术部_lucy);

        } catch (AlreadyHasParentException e) {
            e.printStackTrace();
        } catch (DuplicateElementException e) {
            e.printStackTrace();
        }

        return tree;
    }

    /**
     * 遍历tree.
     *
     * @param tree
     *         tree
     */
    public static void dfs(BasicTree<String> tree) {
        tree = genOrganizationStructure();
        List<Node<String>> nodes = tree.dfs(tree.getRoot());
        for (Node node : nodes) {
            int d = node.getDepth();
            String s = "";
            for (int i = 0; i < d; i++) {
                s += "  ";
            }
            if (node != tree.getRoot()) {
                System.out.println(s + node.getElement());
            }
        }
    }

}
