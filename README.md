# Analysis-of-Search-Algorithms

Conducting and examining **Search Algorithms** within a basic testing environment:  
- *Breadth-First Search Algorithm*
- *Depth-First Search Algorithm*
- *Iterative Deepening Depth-First Search Algorithm*
- *Greedy Best-First Search Algorithm*
- *Astar Search Algorithm*

## Testing Environment

The warehouse has P parking positions, where each position can accommodate N equally sized boxes. The figure below shows a warehouse with P=4 and N=5. The warehouse is managed by a robotic arm, which can only access the top box at each parking position. The robotic arm recognizes the command: "MOVE p,r", which takes the top box at the p-th position (1 ≤ p ≤ P) and places it on top of the stack at the r-th position (1 ≤ r ≤ P). If the r-th position is empty, the box is placed on the ground. If the p-th position is empty, nothing happens.
The operator of the robotic arm is provided with the start and final configurations of the warehouse, and then seeks the shortest sequence of commands that will rearrange the boxes according to the desired pattern.

![image](https://user-images.githubusercontent.com/75141731/226174223-eb451ebd-9681-4a29-935e-749c309c0c26.png)

## AnalysisOfSearchAlgorithms.java Structure

```java
class AnalysisOfSearchAlgorithms {
  main {
    // start and final warehouse configuration from /input
    // run search algorithms
    // print stats and path
  }
}

class Tree {
  public String BFS {} // return path
  public String DFS {}
  // + other search algorithms
}

class Node {
  // a node represents one possible warehouse configuration
  // + heuristics
}

class Move {
  // stores last move of the box
}
```

## In-Depth Code Explaination

The program receives two text files as input, representing the start and final configurations of boxes in a warehouse, converts them to a two-dimensional string array, and prints them to the screen.
```java
String[][] start_config = readInput("input/input5_start.txt");
String[][] final_config = readInput("input/input5_final.txt");
```

The next step is to declare and initialize a tree or a state graph using the `Tree` class, where each state is represented by a `Node` class, which represents one of the possible configurations of the warehouse.
```java
Tree tree = new Tree(new Node(null, null, zacetna), new Node(null, null, koncna));
```

When the new `Node` constructor is called, in addition to links to the previous box configuration `previousNode` and the previous box movement `previousMove`, aar two-dimensional array of the current box configuration `boxes` and a list of all possible movements from the current configuration `allPossibleMoves` are also stored. The array is generated using the `generateAllPossibleMoves()` method.
```java
public Node(Node previousNode, Move previousMove, String[][] boxes) {
  this.previousNode = previousNode;
  this.previousMove = previousMove;
  this.boxes = boxes;
  this.allPossibleMoves = this.generateAllPossibleMoves();
}
```

Inside the `Node` class, there is also a method called `move()`, which takes a `Move` object as an argument, representing a movement of a box from one column to another. The `applyMove()` method confirms the movement and returns a new node or a new configuration of the warehouse.
```JAVA
public Node move(Move move) {
  String[][] tmpBoxes = this.copy2dArray(this.boxes);
  this.applyMove(tmpBoxes, move.s, move.d);
  return new Node(this, move, tmpBoxes);
}
```

The above `move()` method is applied in search algorithms written within the `Tree` class as public methods. Five search algorithms have been implemented, which are called in the `main()` method of the `AnalysisOfSearchAlgorithms` class as follows. The *IDDFS* algorithm takes a number as an argument representing the limit or depth of iterative deepening, while *GreedyBestFirstSearch* and *Astar* take a number that only allows the choice of a particular heuristic. All algorithms return a path or string of commands generated by the `move()` method.
```java
tree.BFS(); 
tree.DFS();
tree.IDDFS(100);
tree.GreedyBestFirstSearch(0); 
tree.Astar(0);
```

## Heuristics

Two search algorithms, *GreedyBestFirstSearch* and *Astar* are implemented using five different heuristics:
- `heuristicBoxesOnRightPosition` *number of boxes in the correct position*
- `heuristicMaxXYDistance`: *maximum distance a box is from its correct position, in terms of X and Y coordinates*
- `heuristicWrongRows`: *number of boxes in the wrong row*
- `heuristicWrongCols`: *number of boxes in the wrong column*
- `heuristicSumOfDistances`: *the sum of the distances a box is from its correct position, in terms of both X and Y coordinates*

The heuristics are implemented as methods inside the `Node` class and are written as functions that take in two arguments: the current warehouse configuration `curNode` and the final warehouse configuration `endNode`. Each method calculates the result of a specific function in its own way and returns an integer that represents the heuristic on the edge between the nodes `curNode` and `endNode`.
```java
public static int heuristicBoxesOnRightPosition(Node curNode, Node endNode)
public static int heuristicMaxXYDistance(Node curNode, Node endNode)
public static int heuristicWrongRows(Node curNode, Node endNode)
public static int heuristicWrongCols(Node curNode, Node endNode)
public static int heuristicSumOfDistances(Node curNode, Node endNode)
```

In the code of both algorithms, `switch` statement is used to select which heuristic to use, with the difference being that in *GreedyBestFirstSearch*, we only use the heuristic `h` to select the next node, while in the *Astar* algorithm, we use the sum of the heuristic `h` and the cost of the edge `g`. In our implementation, this is represented by the variable `dist`, which indicates the number of moves made up to that point.
```java
int h;
switch (heuristic) {
    case 0 -> { h = Node.heuristicBoxesOnRightPosition(nextNode, endNode); }
    case 1 -> { h = Node.heuristicSumOfDistances(nextNode, endNode); }
    case 2 -> { h = Node.heuristicWrongCols(nextNode, endNode); }
    case 3 -> { h = Node.heuristicWrongRows(nextNode, endNode); }
    default -> { h = Node.heuristicMaxXYDistance(nextNode, endNode); }
}
fScore.put(nextNode, dist + h);
```
