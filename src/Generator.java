import edu.princeton.cs.algs4.StdRandom;

import java.util.*;

public class Generator {
    static int m;
    static int n;
    static int size;
    static int[] stepList = {15, 20, 30, 50};
    static int step;
    static int curStep = 0;
    static HashMap<Integer, Integer> gridType = new HashMap<>();
    static Node curNode;
    static HashSet<String> boardSet = new HashSet<>();
    static HashSet<Integer> gridsToCheck = new HashSet<>();
    static LinkedList<Node> nodesToCheck = new LinkedList<>();
    static LinkedList<Node> nodesToSelect = new LinkedList<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String str = generator(sc);

        sc = new Scanner(str);
        new Solver(sc);
    }

    public static String generator(Scanner sc) {
        int key = StdRandom.uniformInt(0, 5);
        step = stepList[key];

        //读入参数
        m = sc.nextInt();
        n = sc.nextInt();
        size = m * n;

        int[] gridBoard = new int[size];
        for (int i = 0; i < size; i++) {
            gridBoard[i] = i + 1;
        }
        gridBoard[size - 1] = 0;
        gridBoard[size - 2] = 0;

        //设置gridType的键值对
        for (int i = 0; i <= size; i++) {
            if (i == 0) {
                gridType.put(i, 0);
            } else {
                gridType.put(i, 11);
            }
        }
        int blockCount = sc.nextInt();
        for (int i = 0; i < blockCount; i++) {
            int num = sc.nextInt();
            String type = sc.next();
            if (type.equals("2*1")) {
                gridType.replace(num, 21);
                gridType.replace(num + n, -1);
            } else if (type.equals("1*2")) {
                gridType.replace(num, 12);
                gridType.replace(num + 1, -1);
            } else {
                gridType.replace(num, 22);
                gridType.replace(num + 1, -1);
                gridType.replace(num + n, -1);
                gridType.replace(num + n + 1, -1);
            }
        }

        curNode = new Node(gridBoard);
        System.out.println("Read In Successfully!");

        boolean flag = false;
        while (!flag) {
            flag = search();
        }

        StringBuilder str = new StringBuilder(String.format("%d %d", m, n));
        for (int i = 0; i < m; i++) {
            str.append("\n");
            for (int j = 0; j < n; j++) {
                str.append(curNode.gridBoard[j + i * n]).append(" ");
            }
        }

        str.append("\n").append(blockCount);
        for (int num : gridType.keySet()) {
            int type = gridType.get(num);
            if (type == 12) {
                str.append("\n").append(num).append(" 1*2");
            } else if (type == 21) {
                str.append("\n").append(num).append(" 2*1");
            } else if (type == 22) {
                str.append("\n").append(num).append(" 2*2");
            }
        }

        str.append("\n");

        String res = str.toString();

        System.out.println("-----------Successfully Generated Data----------------\n"+res+"-----------------------------------------------------");

        return res;
    }

    public static boolean search() {
        gridsToCheck.clear();
        nodesToSelect.clear();
        for (int i = 0; i < size; i++) {
            if (curNode.gridBoard[i] == 0) {
                int[] ids = {i - 2 * n - 1, i - 2 * n, i - n - 2, i - n - 1, i - n, i - n + 1, i - 2, i - 1, i + 1, i + n - 1, i + n};
                for (int j : ids) {
                    if (j >= 0 && j < size) {
                        gridsToCheck.add(j);
                    }
                }
            }
        }

        nodesToCheck.clear();
        for (int id : gridsToCheck) {
            if (gridType.get(curNode.gridBoard[id]) == 11) {
                search1x1(id, curNode);
            } else if (gridType.get(curNode.gridBoard[id]) == 21) {
                search2x1(id, curNode);
            } else if (gridType.get(curNode.gridBoard[id]) == 12) {
                search1x2(id, curNode);
            } else if (gridType.get(curNode.gridBoard[id]) == 22) {
                search2x2(id, curNode);
            }
        }

        //检查是否能往下走，若不能则return true | 若深度达到，随机选一个后return true
        for (Node nd : nodesToCheck) {
            String temp = Arrays.toString(nd.gridBoard);
            if (!boardSet.contains(temp) && nd.dist != 0) {
                boardSet.add(temp);
                nodesToSelect.add(nd);
            }
        }

        if (nodesToSelect.isEmpty()) {
            return true;
        } else {
            int key = StdRandom.uniformInt(0, nodesToSelect.size());
            curNode = nodesToSelect.get(key);
            curStep++;
            return curStep >= step;
        }
    }

    private static void search1x1(int id, Node node) {
        int[] newBoard;
        //上下左右
        if (checkBound(id - n) && node.gridBoard[id - n] == 0) {
            //交换，生成新的grid，并插入nodeToCheck中
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id] = 0;
            newBoard[id - n] = node.gridBoard[id];
            Node newNode = new Node(newBoard);
            nodesToCheck.add(newNode);
        }
        if (checkBound(id + n) && node.gridBoard[id + n] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id] = 0;
            newBoard[id + n] = node.gridBoard[id];
            Node newNode = new Node(newBoard);
            nodesToCheck.add(newNode);
        }
        if (checkBound(id - 1) && node.gridBoard[id - 1] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id] = 0;
            newBoard[id - 1] = node.gridBoard[id];
            Node newNode = new Node(newBoard);
            nodesToCheck.add(newNode);
        }
        if (checkBound(id + 1) && node.gridBoard[id + 1] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id] = 0;
            newBoard[id + 1] = node.gridBoard[id];
            Node newNode = new Node(newBoard);
            nodesToCheck.add(newNode);
        }
    }

    /**
     * @param id   gridsToCheck中2x1的点
     * @param node 当前node
     */
    private static void search2x1(int id, Node node) {
        int[] newBoard;
        //左右移动
        if (checkBound(id - 1) && checkBound(id + n - 1) && node.gridBoard[id - 1] == 0 && node.gridBoard[id + n - 1] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id] = 0;
            newBoard[id - 1] = node.gridBoard[id];
            newBoard[id + n] = 0;
            newBoard[id + n - 1] = node.gridBoard[id + n];
            Node newNode = new Node(newBoard);
            nodesToCheck.add(newNode);
        }
        if (checkBound(id + 1) && checkBound(id + n + 1) && node.gridBoard[id + 1] == 0 && node.gridBoard[id + n + 1] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id] = 0;
            newBoard[id + 1] = node.gridBoard[id];
            newBoard[id + n] = 0;
            newBoard[id + n + 1] = node.gridBoard[id + n];
            Node newNode = new Node(newBoard);
            nodesToCheck.add(newNode);
        }
        //上下移动
        if (checkBound(id - n) && node.gridBoard[id - n] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id - n] = node.gridBoard[id];
            newBoard[id] = node.gridBoard[id + n];
            newBoard[id + n] = 0;
            Node newNode = new Node(newBoard);
            nodesToCheck.add(newNode);
        }
        if (checkBound(id + 2 * n) && node.gridBoard[id + 2 * n] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id + 2 * n] = node.gridBoard[id + n];
            newBoard[id + n] = node.gridBoard[id];
            newBoard[id] = 0;
            Node newNode = new Node(newBoard);
            nodesToCheck.add(newNode);
        }
    }

    private static void search1x2(int id, Node node) {
        int[] newBoard;
        //上下移动
        if (checkBound(id - n) && checkBound(id + 1 - n) && node.gridBoard[id - n] == 0 && node.gridBoard[id + 1 - n] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id] = 0;
            newBoard[id + 1] = 0;
            newBoard[id - n] = node.gridBoard[id];
            newBoard[id + 1 - n] = node.gridBoard[id + 1];
            Node newNode = new Node(newBoard);
            nodesToCheck.add(newNode);
        }
        if (checkBound(id + n) && checkBound(id + 1 + n) && node.gridBoard[id + n] == 0 && node.gridBoard[id + 1 + n] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id] = 0;
            newBoard[id + 1] = 0;
            newBoard[id + n] = node.gridBoard[id];
            newBoard[id + 1 + n] = node.gridBoard[id + 1];
            Node newNode = new Node(newBoard);
            nodesToCheck.add(newNode);
        }
        //左右移动
        if (checkBound(id - 1) && node.gridBoard[id - 1] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id + 1] = 0;
            newBoard[id] = node.gridBoard[id + 1];
            newBoard[id - 1] = node.gridBoard[id];
            Node newNode = new Node(newBoard);
            nodesToCheck.add(newNode);
        }
        if (checkBound(id + 1) && node.gridBoard[id + 1] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id - 1] = 0;
            newBoard[id] = node.gridBoard[id - 1];
            newBoard[id + 1] = node.gridBoard[id];
            Node newNode = new Node(newBoard);
            nodesToCheck.add(newNode);
        }
    }

    private static void search2x2(int id, Node node) {
        int[] newBoard;
        //上下左右
        if (checkBound(id - n) && checkBound(id + 1 - n) && node.gridBoard[id - n] == 0 && node.gridBoard[id + 1 - n] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id + n] = 0;
            newBoard[id + 1 + n] = 0;
            newBoard[id] = node.gridBoard[id + n];
            newBoard[id + 1] = node.gridBoard[id + 1 + n];
            newBoard[id - n] = node.gridBoard[id];
            newBoard[id + 1 - n] = node.gridBoard[id + 1];
            Node newNode = new Node(newBoard);
            nodesToCheck.add(newNode);
        }
        if (checkBound(id + n) && checkBound(id + 1 + n) && node.gridBoard[id + n] == 0 && node.gridBoard[id + 1 + n] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id - n] = 0;
            newBoard[id + 1 - n] = 0;
            newBoard[id] = node.gridBoard[id - n];
            newBoard[id + 1] = node.gridBoard[id + 1 - n];
            newBoard[id + n] = node.gridBoard[id];
            newBoard[id + 1 + n] = node.gridBoard[id + 1];
            Node newNode = new Node(newBoard);
            nodesToCheck.add(newNode);
        }
        if (checkBound(id - 1) && checkBound(id + n - 1) && node.gridBoard[id - 1] == 0 && node.gridBoard[id + n - 1] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id + 1] = 0;
            newBoard[id + 1 + n] = 0;
            newBoard[id] = node.gridBoard[id + 1];
            newBoard[id + n] = node.gridBoard[id + n + 1];
            newBoard[id - 1] = node.gridBoard[id];
            newBoard[id + n - 1] = node.gridBoard[id + n];
            Node newNode = new Node(newBoard);
            nodesToCheck.add(newNode);
        }
        if (checkBound(id + 2) && checkBound(id + n + 2) && node.gridBoard[id + 2] == 0 && node.gridBoard[id + n + 2] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id] = 0;
            newBoard[id + n] = 0;
            newBoard[id + 1] = node.gridBoard[id];
            newBoard[id + n + 1] = node.gridBoard[id + n];
            newBoard[id + 2] = node.gridBoard[id + 1];
            newBoard[id + n + 2] = node.gridBoard[id + n + 1];
            Node newNode = new Node(newBoard);
            nodesToCheck.add(newNode);
        }
    }

    private static boolean checkBound(int id) {
        return id >= 0 && id < size;
    }

    static class Node implements Comparable<Node> {
        int[] gridBoard = null;
        int dist = 0;


        @Override
        public int compareTo(Node o) {
            return this.dist - o.dist;
        }

        public Node(int[] gridBoard) {
            this.gridBoard = gridBoard;
            // calc the Manhattan Distance: 先试试
            for (int i = 0; i < size; i++) {
                int value = gridBoard[i];
                if (value != 0) {
                    int res_x = (value - 1) % n;
                    int res_y = (value - 1) / n;
                    int x = i % n;
                    int y = i / n;
                    this.dist += Math.abs(res_x - x) + Math.abs(res_y - y);
                }
            }

        }
    }

}
