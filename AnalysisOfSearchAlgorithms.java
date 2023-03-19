import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.stream.IntStream;

public class AnalysisOfSearchAlgorithms {

    public static String[][] readInput(String file) {

        // read data from file to arraylist
        ArrayList<String[]> arrTmp = new ArrayList<>();
        try {
            Scanner sc = new Scanner(new FileReader(file));
            while (sc.hasNextLine())
                arrTmp.add(sc.nextLine().replace("'", "").split(","));
        } catch(Exception e) { e.printStackTrace(); }

        // conversion from arraylist to array
        String[][] arr = new String[arrTmp.size()][arrTmp.get(0).length];
        for (int i = 0; i < arrTmp.size(); i++)
            arr[i] = arrTmp.get(i);

        return arr;
    }

    public static void printArr(String msg, String[][] arr) {
        System.out.println(msg);
        for (String[] t : arr) System.out.println(Arrays.toString(t));
        System.out.println();
    }

    public static void executeAlgorithm(Tree tree, int i, String algorithm) {
        String path = "";
        long startTime = System.currentTimeMillis();
        switch (i) {
            case 0 -> path = tree.BFS();
            case 1 -> path = tree.DFS();
            case 2 -> path = tree.IDDFS(100);
            case 3 -> path = tree.GreedyBestFirstSearch(0);
            case 4 -> path = tree.GreedyBestFirstSearch(1);
            case 5 -> path = tree.GreedyBestFirstSearch(2);
            case 6 -> path = tree.GreedyBestFirstSearch(3);
            case 7 -> path = tree.GreedyBestFirstSearch(4);
            case 8 -> path = tree.Astar(0);
            case 9 -> path = tree.Astar(1);
            case 10 -> path = tree.Astar(2);
            case 11 -> path = tree.Astar(3);
            case 12 -> path = tree.Astar(4);
        }
        long endTime = System.currentTimeMillis();
        printStats(algorithm, (endTime - startTime), tree.allNodes, tree.depthOfSolution,  tree.visitedNodes, tree.maxGeneratedNodes, path);
        tree.resetCounters();
    }

    public static void printStats(String algorithm, long time,  int allNodes, int depth, int visitedNodes, int maxNodexInMem, String path) {
        System.out.println(String.format("%-64s", algorithm).replace(" ", "-") + "\n");
        System.out.printf("%-40s %d %s\n", "TOTAL Time:", time, "ms");
        System.out.printf("%-40s %f %s\n", "AVG Time for Processing a Node:", (Math.round(((double) (time) / visitedNodes) * Math.pow(10, 5)) / Math.pow(10, 5)), "ms");
        System.out.printf("%-40s %d\n", "NUM of Nodes (Generated + Visited):", allNodes);
        System.out.printf("%-40s %d\n", "NUM of Visited Nodes:", visitedNodes);
        System.out.printf("%-40s %d\n", "MAX Nodes in Memory:", maxNodexInMem);
        System.out.printf("%-40s %d\n", "DEPTH:", depth);
        System.out.printf("%-40s %s\n", "PATH: ", path);
        System.out.println("\n");
    }

    public static void main(String[] args) {
        String[][] start_conf = readInput("input/input5_start.txt");
        String[][] final_conf = readInput("input/input5_final.txt");

        printArr("Start Configuration", start_conf);
        printArr("Final Configuration", final_conf);

        Tree tree = new Tree(new Node(null, null, start_conf), new Node(null, null, final_conf));

        String[] algorithms = {
            "BFS",
            "DFS",
            "IDDFS",
            "GreedyBestFirstSearch (Boxes On Right Positions)",
            "GreedyBestFirstSearch (Sum Of X Y Distance)",
            "GreedyBestFirstSearch (Wrong Cols)",
            "GreedyBestFirstSearch (Wrong Rows)",
            "GreedyBestFirstSearch (Max X Y Distance)",
            "A* (Boxes On Right Position)",
            "A* (Sum Of X Y Distance)",
            "A* (Wrong Cols)",
            "A* (Wrong Rows)",
            "A* (Max X Y Distance)"
        };
        IntStream.range(0, algorithms.length).forEach(i -> executeAlgorithm(tree, i, algorithms[i]));
    }
}

class Tree {

    int allNodes = 0;
    int visitedNodes = 0;
    int maxGeneratedNodes = 0;
    int depthOfSolution = 0;
    Node first;
    Node endNode;

    public Tree(Node first, Node endNode) {
        this.first = first;
        this.endNode = endNode;
    }

    // Breadth First Search
    public String BFS() {
        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();
        ArrayList<Move> solution = new ArrayList<>();
        queue.add(first);
        visited.add(first);
        while (!queue.isEmpty()) {
            allNodes = Math.max(allNodes, visited.size());
            maxGeneratedNodes = Math.max(maxGeneratedNodes, queue.size());
            Node curNode = queue.remove();
            if (endNode.equals(curNode)) {
                queue.forEach(visited::remove);
                visitedNodes = visited.size();
                while (curNode.previousNode != null) {
                    solution.add(curNode.previousMove);
                    curNode = curNode.previousNode;
                    depthOfSolution++;
                }
                Collections.reverse(solution);
                return solution.toString();
            }
            Node nextNode;
            ArrayList<Move> moves = curNode.allPossibleMoves;
            for (Move move : moves) {
                nextNode = curNode.move(move);
                if (!visited.contains(nextNode)) {
                    queue.add(nextNode);
                    visited.add(nextNode);
                }
            }
        }
        return "Can not find the path.";
    }

    // Depth First Search
    public String DFS() {
        Stack<Node> stack = new Stack<>();
        Set<Node> visited = new HashSet<>();
        ArrayList<Move> solution = new ArrayList<>();
        stack.push(first);
        visited.add(first);
        while (!stack.isEmpty()) {
            allNodes = Math.max(allNodes, visited.size());
            maxGeneratedNodes = Math.max(maxGeneratedNodes, stack.size());
            Node curNode = stack.peek();
            if (endNode.equals(curNode)) {
                stack.forEach(visited::remove);
                visitedNodes = visited.size();
                while (curNode.previousNode != null) {
                    solution.add(curNode.previousMove);
                    curNode = curNode.previousNode;
                    depthOfSolution++;
                }
                Collections.reverse(solution);
                return solution.toString();
            }
            boolean found = false;
            Node nextNode;
            ArrayList<Move> moves = curNode.allPossibleMoves;
            for (Move move : moves) {
                nextNode = curNode.move(move);
                if (!visited.contains(nextNode)) {
                    stack.push(nextNode);
                    visited.add(nextNode);
                    found = true;
                    break;
                }
            }
            if (!found) stack.pop();
        }
        return "Can not find the path.";
    }

    // Greed Best First Search
    public String GreedyBestFirstSearch(int heuristic) {
        Set<Node> open = new HashSet<>();
        Set<Node> closed = new HashSet<>();
        ArrayList<Move> solution = new ArrayList<>();
        HashMap<Node, Integer> fScore = new HashMap<>();
        switch (heuristic) {
            case 0 -> fScore.put(first, Node.heuristicBoxesOnRightPosition(first, endNode));
            case 1 -> fScore.put(first, Node.heuristicSumOfDistances(first, endNode));
            case 2 -> fScore.put(first, Node.heuristicWrongCols(first, endNode));
            case 3 -> fScore.put(first, Node.heuristicWrongRows(first, endNode));
            default -> fScore.put(first, Node.heuristicMaxXYDistance(first, endNode));
        }
        open.add(first);
        while (!open.isEmpty()) {
            int minVal = Integer.MAX_VALUE;
            maxGeneratedNodes = Math.max(maxGeneratedNodes, open.size());
            Node curNode = first;
            for (Node node : open) {
                int fScoreVal = fScore.get(node);
                if (fScoreVal < minVal) {
                    minVal = fScoreVal;
                    curNode = node;
                }
            }
            open.remove(curNode);
            closed.add(curNode);
            if (endNode.equals(curNode)) {
                visitedNodes = closed.size();
                closed.addAll(open);
                allNodes = closed.size();
                while (curNode.previousNode != null) {
                    solution.add(curNode.previousMove);
                    curNode = curNode.previousNode;
                    depthOfSolution++;
                }
                Collections.reverse(solution);
                return solution.toString();
            }
            Node nextNode;
            ArrayList<Move> moves = curNode.allPossibleMoves;
            for (Move move : moves) {
                nextNode = curNode.move(move);
                if (!closed.contains(nextNode)) {
                    open.add(nextNode);
                    switch (heuristic) {
                        case 0 -> fScore.put(nextNode, Node.heuristicBoxesOnRightPosition(nextNode, endNode));
                        case 1 -> fScore.put(nextNode, Node.heuristicSumOfDistances(nextNode, endNode));
                        case 2 -> fScore.put(nextNode, Node.heuristicWrongCols(nextNode, endNode));
                        case 3 -> fScore.put(nextNode, Node.heuristicWrongRows(nextNode, endNode));
                        default -> fScore.put(nextNode, Node.heuristicMaxXYDistance(nextNode, endNode));
                    }
                }
            }
        }
        return "Can not find the path.";
    }

    // A* Search
    public String Astar(int heuristic) {
        Set<Node> open = new HashSet<>();
        Set<Node> closed = new HashSet<>();
        ArrayList<Move> solution = new ArrayList<>();
        HashMap<Node, Integer> fScore = new HashMap<>();
        HashMap<Node, Integer> gScore = new HashMap<>();
        gScore.put(first, 0);
        switch (heuristic) {
            case 0 -> fScore.put(first, Node.heuristicBoxesOnRightPosition(first, endNode));
            case 1 -> fScore.put(first, Node.heuristicSumOfDistances(first, endNode));
            case 2 -> fScore.put(first, Node.heuristicWrongCols(first, endNode));
            case 3 -> fScore.put(first, Node.heuristicWrongRows(first, endNode));
            default -> fScore.put(first, Node.heuristicMaxXYDistance(first, endNode));
        }
        open.add(first);
        while (!open.isEmpty()) {
            int minVal = Integer.MAX_VALUE;
            maxGeneratedNodes = Math.max(maxGeneratedNodes, open.size());
            Node curNode = first;
            for (Node node : open) {
                int fScoreVal = fScore.get(node);
                if (fScoreVal < minVal) {
                    minVal = fScoreVal;
                    curNode = node;
                }
            }
            open.remove(curNode);
            closed.add(curNode);
            if (endNode.equals(curNode)) {
                visitedNodes = closed.size();
                closed.addAll(open);
                allNodes = closed.size();
                while (curNode.previousNode != null) {
                    solution.add(curNode.previousMove);
                    curNode = curNode.previousNode;
                    depthOfSolution++;
                }
                Collections.reverse(solution);
                return solution.toString();
            }
            Node nextNode;
            ArrayList<Move> moves = curNode.allPossibleMoves;
            for (Move move : moves) {
                nextNode = curNode.move(move);
                if (!closed.contains(nextNode)) {
                    if (!open.contains(nextNode)) {
                        fScore.putIfAbsent(nextNode, Integer.MAX_VALUE);
                        gScore.putIfAbsent(nextNode, Integer.MAX_VALUE);
                        open.add(nextNode);
                    }
                    int dist = gScore.get(curNode) + 1;
                    if (dist < gScore.get(nextNode)) {
                        gScore.put(nextNode, dist);
                        int h;
                        switch (heuristic) {
                            case 0 -> h = Node.heuristicBoxesOnRightPosition(nextNode, endNode);
                            case 1 -> h = Node.heuristicSumOfDistances(nextNode, endNode);
                            case 2 -> h = Node.heuristicWrongCols(nextNode, endNode);
                            case 3 -> h = Node.heuristicWrongRows(nextNode, endNode);
                            default -> h = Node.heuristicMaxXYDistance(nextNode, endNode);
                        }
                        fScore.put(nextNode, dist + h);
                    }

                }
            }
        }
        return "Can not find the path.";
    }

    // Iterative Deepening Depth First Search
    public String IDDFS(int depth) {
        for (int depthLimit = 0; depthLimit < depth; depthLimit++) {
            Stack<Node> stack = new Stack<>();
            Set<Node> marked = new HashSet<>();
            ArrayList<Move> solution = new ArrayList<>();
            marked.add(first);
            stack.push(first);
            while (!stack.isEmpty()) {
                allNodes = Math.max(allNodes, marked.size());
                maxGeneratedNodes = Math.max(maxGeneratedNodes, stack.size());
                Node curNode = stack.peek();
                if (endNode.equals(curNode)) {
                    stack.forEach(marked::remove);
                    visitedNodes = marked.size();
                    while (curNode.previousNode != null) {
                        solution.add(curNode.previousMove);
                        curNode = curNode.previousNode;
                        depthOfSolution++;
                    }
                    Collections.reverse(solution);
                    return solution.toString();
                }
                boolean found = false;
                if (stack.size() <= depthLimit) {
                    Node nextNode;
                    ArrayList<Move> moves = curNode.allPossibleMoves;
                    for (Move move : moves) {
                        nextNode = curNode.move(move);
                        if (!marked.contains(nextNode)) {
                            marked.add(nextNode);
                            stack.push(nextNode);
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) stack.pop();
            }
        }
        return "Can not find the path.";
    }

    public void resetCounters() {
        allNodes = 0;
        visitedNodes = 0;
        maxGeneratedNodes = 0;
        depthOfSolution = 0;
    }
}

class Node {
    public Node previousNode;
    public Move previousMove;
    public String[][] boxes;
    public ArrayList<Move> allPossibleMoves;


    public Node(Node previousNode, Move previousMove, String[][] boxes) {
        this.previousNode = previousNode;
        this.previousMove = previousMove;
        this.boxes = boxes;
        this.allPossibleMoves = this.generateAllPossibleMoves();
    }

    public ArrayList<Move> generateAllPossibleMoves() {
        int P = boxes[0].length;
        int N = boxes.length;
        ArrayList<Move> moves = new ArrayList<>();
        for (int s = 0; s < P; s++)
            for (int d = 0; d < P; d++)
                if (!boxes[N - 1][s].equals(" ") && boxes[0][d].equals(" ") && s != d) moves.add(new Move(s, d));
        return moves;
    }

    public Node move(Move move) {
        String[][] tmpBoxes = this.copy2dArray(this.boxes);
        this.applyMove(tmpBoxes, move.s, move.d);
        return new Node(this, move, tmpBoxes);
    }

    private String[][] copy2dArray(String[][] boxes) {
        String[][] tmp = new String[boxes.length][boxes[0].length];
        for (int i = 0; i < boxes.length; i++) tmp[i] = boxes[i].clone();
        return tmp;
    }

    private void applyMove(String[][] start_conf, int s, int e) {
        String tmp = "";
        for (int i = 0; i < start_conf.length; i++)
            if (!start_conf[i][s].equals(" ")) {
                tmp = start_conf[i][s];
                start_conf[i][s] = " ";
                break;
            }
        if (tmp.equals("")) return;

        for (int i = 0; i < start_conf.length; i++)
            if ((!start_conf[i][e].equals(" ") && i - 1 >= 0) || i == start_conf.length - 1) {
                if (((!start_conf[i][e].equals(" ") && i - 1 >= 0))) --i;
                start_conf[i][e] = tmp;
                break;
            }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\n");
        for (String[] box : boxes) sb.append(Arrays.toString(box)).append("\n");
        sb.append(((previousMove != null) ? previousMove : "null"));
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node node)) return false;
        return Arrays.deepEquals(boxes, node.boxes);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(boxes);
    }

    public static int heuristicBoxesOnRightPosition(Node curNode, Node endNode) {
        int heuristic = 0;
        for (int i = 0; i < curNode.boxes.length; i++)
            for (int j = 0; j < curNode.boxes[0].length; j++)
                if (!curNode.boxes[i][j].equals(endNode.boxes[i][j]) && !curNode.boxes[i][j].equals(" "))
                    heuristic++;
        return heuristic;
    }

    public static int heuristicMaxXYDistance(Node curNode, Node endNode) {
        int rowDifference, colDifference, heuristic = 0;
        for (int i = 0; i < curNode.boxes.length; i++)
            for (int j = 0; j < curNode.boxes[0].length; j++)
                if (!curNode.boxes[i][j].equals(endNode.boxes[i][j]) && !curNode.boxes[i][j].equals(" ")) {
                    rowDifference = Math.abs(i - endNode.getXY(curNode.boxes[i][j])[0]);
                    colDifference = Math.abs(j - endNode.getXY(curNode.boxes[i][j])[1]);
                    heuristic += Math.max(rowDifference, colDifference);
                }
        return heuristic;
    }

    public static int heuristicWrongRows(Node curNode, Node endNode) {
        int heuristic = 0;
        for (int i = 0; i < curNode.boxes.length; i++)
            for (int j = 0; j < curNode.boxes[0].length; j++)
                if (!curNode.boxes[i][j].equals(endNode.boxes[i][j]) && !curNode.boxes[i][j].equals(" ") && i != endNode.getXY(curNode.boxes[i][j])[0])
                    heuristic++;
        return heuristic;
    }

    public static int heuristicWrongCols(Node curNode, Node endNode) {
        int heuristic = 0;
        for (int i = 0; i < curNode.boxes.length; i++)
            for (int j = 0; j < curNode.boxes[0].length; j++)
                if (!curNode.boxes[i][j].equals(endNode.boxes[i][j]) && !curNode.boxes[i][j].equals(" ") && j != endNode.getXY(curNode.boxes[i][j])[0])
                    heuristic++;
        return heuristic;
    }

    public static int heuristicSumOfDistances(Node curNode, Node endNode) {
        int rowDifference, colDifference, heuristic = 0;
        for (int i = 0; i < curNode.boxes.length; i++)
            for (int j = 0; j < curNode.boxes[0].length; j++)
                if (!curNode.boxes[i][j].equals(endNode.boxes[i][j]) && !curNode.boxes[i][j].equals(" ")) {
                    rowDifference = Math.abs(i - endNode.getXY(curNode.boxes[i][j])[0]);
                    colDifference = Math.abs(j - endNode.getXY(curNode.boxes[i][j])[1]);
                    heuristic += (rowDifference + colDifference);
                }
        return heuristic;
    }

    public int[] getXY(String box) {
        for (int i = 0; i < this.boxes.length; i++)
            for (int j = 0; j < this.boxes[0].length; j++)
                if (this.boxes[i][j].equals(box))
                    return new int[]{i, j};
        return new int[]{-1, -1};
    }
}

class Move {
    public int s;
    public int d;

    public Move(int s, int d) {
        this.s = s;
        this.d = d;
    }

    @Override
    public String toString() {
        return "(" + "" + s + ";" + d + ")";
    }
}
