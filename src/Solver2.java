import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Queue;

import java.util.*;

public class Solver2 {
    static int m;
    static int n;
    static int size;
    /**
     * 不变 | 最开始的情况
     */
    static Node start;
    /**
     * 不变 | 最终成功找到答案的node
     */
    static Node end;
    /**
     * 不变 | 用于存储grid的type【11，12，21，22，-1】
     */
    static HashMap<Integer, Integer> gridType = new HashMap<>();
    /**
     * 不变 | 所有走过的情况，用于检查重复
     */
    static HashSet<String> boardSet = new HashSet<>();
    /**
     * 迭代：每个Node | 需要检查的grid，由空节点得出
     */
    static HashSet<Integer> gridsToCheck = new HashSet<>();
    /**
     * 迭代：每个Node | 每个可能的node
     */
    static LinkedList<Node> nodesToCheck = new LinkedList<>();
    /**
     * 不变 | 需要迭代的队列，贪婪算法
     */
    static Queue<Node> queue = new Queue<>();
    /**
     * 不变 | 成功搜索后，得到的正序路径
     */
    static LinkedList<String> paths = new LinkedList<>();
    /**
     * 不变 | 成功搜索后，得到的正序boards变化，包括start
     */
    static LinkedList<String> boards = new LinkedList<>();


    public Solver2(Scanner sc) {
        String[] in = sc.nextLine().split(" ");
        m = Integer.parseInt(in[0]);
        n = Integer.parseInt(in[1]);
        size = m * n;

        //填充start的gridBoard
        int[] gridBoard = new int[size];
        for (int i = 0; i < m; i++) {
            in = sc.nextLine().split(" ");
            for (int j = 0; j < n; j++) {
                gridBoard[j + i * n] = Integer.parseInt(in[j]);
            }
        }

        //设置gridType的键值对
        for (int i = 0; i <= size; i++) {
            if (i==0) {
                gridType.put(i, 0);
            } else {
                gridType.put(i, 11);
            }
        }
        int blockCount = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < blockCount; i++) {
            int key = sc.nextInt();
            String type = sc.next();
            int keyId = findIndex(gridBoard, key);
            if (type.equals("2*1")) {
                gridType.replace(key, 21);
                gridType.replace(gridBoard[keyId + n], -1);
            } else if (type.equals("1*2")) {
                gridType.replace(key, 12);
                gridType.replace(gridBoard[keyId + 1], -1);
            } else {
                gridType.replace(key, 22);
                gridType.replace(gridBoard[keyId + 1], -1);
                gridType.replace(gridBoard[keyId + n], -1);
                gridType.replace(gridBoard[keyId + n + 1], -1);
            }
        }

        start = new Node(gridBoard, null, null);
        System.out.println("Read In Successfully!");

        boolean flag = BFS();


    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        new Solver2(sc);
    }

    static class Node implements Comparable<Node> {
        Node parentNode = null;
        int[] gridBoard = null;
        int dist = 0;
        String lastOperation = null;


        @Override
        public int compareTo(Node o) {
            return this.dist - o.dist;
        }

        public Node(int[] gridBoard, String lastOperation, Node parent) {
            this.gridBoard = gridBoard;
            this.lastOperation = lastOperation;
            this.parentNode = parent;
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

    public boolean BFS() {
        //判断答案是否已经trivial
        queue.enqueue(start);
        if (start.dist == 0) {
            System.out.println("Trivial: the input game has already been ordered");
            return false;
        }

        //搜索
        boolean flag = false;
        try {
            while (!queue.isEmpty() && !flag) {
                flag = search(queue.dequeue());
            }
        } catch (OutOfMemoryError e) {
            //若内存溢出，则输出并清理内存
            System.out.println("Machine Exhausted (●･̆⍛･̆●) ");
            boardSet = null;
        }

        //回溯
        if (flag) {
            Node iterator = end;
            while (iterator != start) {
                paths.add(iterator.lastOperation);
                boards.add(Arrays.toString(iterator.gridBoard));
                iterator = iterator.parentNode;
            }
            boards.add(Arrays.toString(start.gridBoard));

            Collections.reverse(paths);
            Collections.reverse(boards);

            System.out.printf("Yes\n%d\n", paths.size());
            for (String str: paths) {
                System.out.println(str);
            }

        } else {
            System.out.println("No");
        }
        return flag;
    }
    

    private static boolean search(Node node) {
        //找到每个需要进行检查的grid

        gridsToCheck.clear();
        for (int i = 0; i < size; i++) {
            if (node.gridBoard[i] == 0) {
                int[] ids = {i - 2 * n - 1, i - 2 * n, i - n - 2, i - n - 1, i - n, i - n + 1, i - 2, i - 1, i + 1, i + n - 1, i + n};
                for (int j : ids) {
                    if (j >= 0 && j < size) {
                        gridsToCheck.add(j);
                    }
                }
            }
        }

        //分类别进行search
        nodesToCheck.clear();
        for (int id : gridsToCheck) {
            if (gridType.get(node.gridBoard[id]) == 11) {
                search1x1(id, node);
            } else if (gridType.get(node.gridBoard[id]) == 21) {
                search2x1(id, node);
            } else if (gridType.get(node.gridBoard[id]) == 12) {
                search1x2(id, node);
            } else if (gridType.get(node.gridBoard[id]) == 22) {
                search2x2(id, node);
            }
        }

        //检查是否存在解 | 将nodeToCheck转入minPQ和nodeSet里
        for (Node nd: nodesToCheck) {
            if (nd.dist == 0) {
                end = nd;
                return true;
            }

            String temp = Arrays.toString(nd.gridBoard);
            if (!boardSet.contains(temp)) {
                boardSet.add(temp);
                queue.enqueue(nd);
            }
        }
        return false;
    }


    /**
     * 检查grid的index上下越界情况
     *
     * @param id 检查的index
     * @return true: 在边界内 | false：不在边界内
     */
    private static boolean checkULBound(int id) {
        return id >= 0 && id < size;
    }

    /**
     * @param id   gridsToCheck中1x1的点
     * @param node 当前node
     */
    private static void search1x1(int id, Node node) {
        int[] newBoard;
        //上下左右
        if (checkULBound(id - n) && node.gridBoard[id - n] == 0) {
            //交换，生成新的grid，并插入nodeToCheck中
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id] = 0;
            newBoard[id - n] = node.gridBoard[id];
            Node newNode = new Node(newBoard, String.format("%d U", node.gridBoard[id]), node);
            nodesToCheck.add(newNode);
        }
        if (checkULBound(id + n) && node.gridBoard[id + n] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id] = 0;
            newBoard[id + n] = node.gridBoard[id];
            Node newNode = new Node(newBoard, String.format("%d D", node.gridBoard[id]), node);
            nodesToCheck.add(newNode);
        }
        if (id % n != 0 && node.gridBoard[id - 1] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id] = 0;
            newBoard[id - 1] = node.gridBoard[id];
            Node newNode = new Node(newBoard, String.format("%d L", node.gridBoard[id]), node);
            nodesToCheck.add(newNode);
        }
        if (id % n != n - 1 && node.gridBoard[id + 1] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id] = 0;
            newBoard[id + 1] = node.gridBoard[id];
            Node newNode = new Node(newBoard, String.format("%d R", node.gridBoard[id]), node);
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
        if (id % n != 0 && node.gridBoard[id - 1] == 0 && node.gridBoard[id + n - 1] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id] = 0;
            newBoard[id - 1] = node.gridBoard[id];
            newBoard[id + n] = 0;
            newBoard[id + n - 1] = node.gridBoard[id + n];
            Node newNode = new Node(newBoard, String.format("%d L", node.gridBoard[id]), node);
            nodesToCheck.add(newNode);
        }
        if (id % n != n - 1 && node.gridBoard[id + 1] == 0 && node.gridBoard[id + n + 1] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id] = 0;
            newBoard[id + 1] = node.gridBoard[id];
            newBoard[id + n] = 0;
            newBoard[id + n + 1] = node.gridBoard[id + n];
            Node newNode = new Node(newBoard, String.format("%d R", node.gridBoard[id]), node);
            nodesToCheck.add(newNode);
        }
        //上下移动
        if (checkULBound(id - n) && node.gridBoard[id - n] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id - n] = node.gridBoard[id];
            newBoard[id] = node.gridBoard[id + n];
            newBoard[id + n] = 0;
            Node newNode = new Node(newBoard, String.format("%d U", node.gridBoard[id]), node);
            nodesToCheck.add(newNode);
        }
        if (checkULBound(id + 2 * n) && node.gridBoard[id + 2 * n] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id + 2 * n] = node.gridBoard[id + n];
            newBoard[id + n] = node.gridBoard[id];
            newBoard[id] = 0;
            Node newNode = new Node(newBoard, String.format("%d D", node.gridBoard[id]), node);
            nodesToCheck.add(newNode);
        }
    }

    private static void search1x2(int id, Node node) {
        int[] newBoard;
        //上下移动
        if (checkULBound(id - n) && checkULBound(id + 1 - n) && node.gridBoard[id - n] == 0 && node.gridBoard[id + 1 - n] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id] = 0;
            newBoard[id + 1] = 0;
            newBoard[id - n] = node.gridBoard[id];
            newBoard[id + 1 - n] = node.gridBoard[id + 1];
            Node newNode = new Node(newBoard, String.format("%d U", node.gridBoard[id]), node);
            nodesToCheck.add(newNode);
        }
        if (checkULBound(id + n) && checkULBound(id + 1 + n) && node.gridBoard[id + n] == 0 && node.gridBoard[id + 1 + n] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id] = 0;
            newBoard[id + 1] = 0;
            newBoard[id + n] = node.gridBoard[id];
            newBoard[id + 1 + n] = node.gridBoard[id + 1];
            Node newNode = new Node(newBoard, String.format("%d D", node.gridBoard[id]), node);
            nodesToCheck.add(newNode);
        }
        //左右移动
        if (id % n != 0 && node.gridBoard[id - 1] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id + 1] = 0;
            newBoard[id] = node.gridBoard[id + 1];
            newBoard[id - 1] = node.gridBoard[id];
            Node newNode = new Node(newBoard, String.format("%d L", node.gridBoard[id]), node);
            nodesToCheck.add(newNode);
        }
        if ((id + 1) % n != n - 1 && node.gridBoard[id + 2] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id] = 0;
            newBoard[id + 2] = node.gridBoard[id + 1];
            newBoard[id + 1] = node.gridBoard[id];
            Node newNode = new Node(newBoard, String.format("%d R", node.gridBoard[id]), node);
            nodesToCheck.add(newNode);
        }
    }

    private static void search2x2(int id, Node node) {
        int[] newBoard;
        //上下左右
        if (checkULBound(id - n) && checkULBound(id + 1 - n) && node.gridBoard[id - n] == 0 && node.gridBoard[id + 1 - n] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id + n] = 0;
            newBoard[id + 1 + n] = 0;
            newBoard[id] = node.gridBoard[id + n];
            newBoard[id + 1] = node.gridBoard[id + 1 + n];
            newBoard[id - n] = node.gridBoard[id];
            newBoard[id + 1 - n] = node.gridBoard[id + 1];
            Node newNode = new Node(newBoard, String.format("%d U", node.gridBoard[id]), node);
            nodesToCheck.add(newNode);
        }
        if (checkULBound(id + n) && checkULBound(id + 1 + n) && node.gridBoard[id + n] == 0 && node.gridBoard[id + 1 + n] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id - n] = 0;
            newBoard[id + 1 - n] = 0;
            newBoard[id] = node.gridBoard[id - n];
            newBoard[id + 1] = node.gridBoard[id + 1 - n];
            newBoard[id + n] = node.gridBoard[id];
            newBoard[id + 1 + n] = node.gridBoard[id + 1];
            Node newNode = new Node(newBoard, String.format("%d D", node.gridBoard[id]), node);
            nodesToCheck.add(newNode);
        }
        if (id % n != 0 && node.gridBoard[id - 1] == 0 && node.gridBoard[id + n - 1] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id + 1] = 0;
            newBoard[id + 1 + n] = 0;
            newBoard[id] = node.gridBoard[id + 1];
            newBoard[id + n] = node.gridBoard[id + n + 1];
            newBoard[id - 1] = node.gridBoard[id];
            newBoard[id + n - 1] = node.gridBoard[id + n];
            Node newNode = new Node(newBoard, String.format("%d L", node.gridBoard[id]), node);
            nodesToCheck.add(newNode);
        }
        if ((id + 1) % n != n - 1 && node.gridBoard[id + 2] == 0 && node.gridBoard[id + n + 2] == 0) {
            newBoard = Arrays.copyOf(node.gridBoard, node.gridBoard.length);
            newBoard[id] = 0;
            newBoard[id + n] = 0;
            newBoard[id + 1] = node.gridBoard[id];
            newBoard[id + n + 1] = node.gridBoard[id + n];
            newBoard[id + 2] = node.gridBoard[id + 1];
            newBoard[id + n + 2] = node.gridBoard[id + n + 1];
            Node newNode = new Node(newBoard, String.format("%d R", node.gridBoard[id]), node);
            nodesToCheck.add(newNode);
        }
    }

    public static int findIndex(int[] arr, int key) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == key) {
                return i;
            }
        }
        return -1;
    }

}