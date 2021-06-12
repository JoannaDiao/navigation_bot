import java.util.*; 

public class maze {
	// Constants 
	public static final int MIN_ARGUMENTS = 3;
	public static final int ROBOT_COORD_INDEX = 1;
	public static final int USER_COORD_INDEX = 2;
	public static final int OBSTACLE_COORD_INDEX = 3;
	public static final int ITEM_COORD_INDEX = 4;
	public static final String INPUT_COORD_DELIM = " ";

	// default initial cost is -1 since the code "visits" the initial node and increments step count which causes our counts to be 1 higher
	public static final int DEFAULT_INIT_COST = -1; 


	/** 
	 * Main function for testing inputs - function parses inputs and calls the appropriate pathfinding algorithm 
	 * 
	 * @param input 		String given in multi line, space delimited format 
	 * @throws Exception 	exception is thrown when input is improperly formatted
	 */
	public static void testInput(String input) throws Exception{
		String[] inputArr = input.split("\\n");
		if (inputArr.length < MIN_ARGUMENTS){
			throw new Exception("Unexpected number of inputs");
		}

		int[][] maze = generateMaze(inputArr);
		String[] robotCoord = inputArr[ROBOT_COORD_INDEX].split(INPUT_COORD_DELIM);
		String[] userCoord = inputArr[USER_COORD_INDEX].split(INPUT_COORD_DELIM);

		// coordinate inputs are given in format [Y, X]
		int robotX = Integer.valueOf(robotCoord[1]); 
		int robotY = Integer.valueOf(robotCoord[0]); 
		int userX = Integer.valueOf(userCoord[1]); 
		int userY = Integer.valueOf(userCoord[0]); 

		System.out.println("\nA* traversal"); 
		if (inputArr.length > ITEM_COORD_INDEX){
			// we have an item to pick up 
			System.out.println("Agent starting at Robot, picking up Item and ending at User\n");
			String[] itemCoord = inputArr[ITEM_COORD_INDEX].split(INPUT_COORD_DELIM);
			int itemX = Integer.valueOf(itemCoord[1]);
			int itemY = Integer.valueOf(itemCoord[0]);

			// for first iteration we make user an obstacle (since we want to avoid user) and traverse from robot to item
			maze[userX][userY] = 1;
			Node tmp = aStar(robotX, robotY, itemX, itemY, maze, "", DEFAULT_INIT_COST, false);

			// indicating item pick up and reducing cost by 1 since we are revisiting the item node (which increments costs) at start of second iteration
			tmp.path += ", Picked Up Item"; 
			tmp.cost -= 1;

			// for second iteration we turn user back from an obstacle and traverse from item to user continuing values from first traversal 
			maze[userX][userY] = 0;
			aStar(itemX, itemY, userX, userY, maze, tmp.path, tmp.cost, true);
		} else { 
			System.out.println("Agent starting at Robot and ending at User\n");
			aStar(robotX, robotY, userX, userY, maze, "", DEFAULT_INIT_COST, true);
		}
	}

	/**
	  * Helper method to generate input maze - represented as an array of integers with 1 representing a obstacle square and 0 representing empty
	  * 
	  * @param inputArr input array split by line
	  */
	public static int[][] generateMaze(String[] inputArr){
		String[] arrayDimensions = inputArr[0].split(INPUT_COORD_DELIM);
		int[][] maze = new int[Integer.valueOf(arrayDimensions[1])][Integer.valueOf(arrayDimensions[0])];

		// if there are more inputs that obstacle coordinate index, we have to add obstacles to maze
		if (inputArr.length>OBSTACLE_COORD_INDEX){
			String[] obstacleCoord = inputArr[OBSTACLE_COORD_INDEX].split(INPUT_COORD_DELIM);

			// obstacles are divided in pairs so we process pairs of obstacles from the input
			for (int i=0; i<obstacleCoord.length/2; i++){
				maze[Integer.valueOf(obstacleCoord[2*i+1])][Integer.valueOf(obstacleCoord[2*i])] = 1;
			}
		}			
		return maze;			
	}

	/**
	 * Node class is used to store information about node location as well as the path and cost to reach node
	 */
	public static class Node {
		public int x; 
		public int y; 
		public String path;
		public int cost;
		
		/**
		 * Constructor for Node class
		 * @param x		x coordinate of node
		 * @param y		y coordinate of node
		 * @param path	path to reach node 
		 * @param cost	cost to reach node
		 */
		public Node(int x, int y, String path, int cost){
			this.x = x; 
			this.y = y;
			this.path = path;
			this.cost = cost;	
		}
	}

	/** 
	 * A* search algorithm using priority queue and manhattan distance for heuristic 
	 * 
	 * @param sx 			starting point x coordinate
	 * @param sy 			starting point y coordinate
	 * @param ex 			ending point x coordinate 
	 * @param ey 			ending point y coordinate
	 * @param maze 			2d integer representation for maze (1 represents obstacle, 0 is empty)
	 * @param initPath		initial path for starting node - used to store intermediate path when visiting item then user
	 * @param initCost 		initial cost for starting node - used to store intermediate path cost when visiting item then user
	 * @param printResult 	whether or not to print the results
	 */ 
	public static Node aStar(int sx, int sy, int ex, int ey, int[][] maze, String initPath, int initCost, boolean printResult){
		// movement directions allowed to robot
		int[][] dirs = new int[][]{{0,1}, {0,-1},{1,0},{-1,0}};
		int nodesExplored = 0;
		HashSet<String> visited = new HashSet<>();
		PriorityQueue<Node> pq = new PriorityQueue<>((Node a, Node b) -> {
			// using manhattan distance to provide heuristic h(n) 
			int dxA = Math.abs(a.x - ex); 
			int dyA = Math.abs(a.y - ey); 
			int dxB = Math.abs(b.x - ex); 
			int dyB = Math.abs(b.y - ey); 

			/* A* algorithm chooses next node based on f = g(n) + h(n) where h is the heuristic and g is the distance to
			   reach the node (given by node.cost + 1) */
			return (dxA + dyA + a.cost + 1) - (dxB + dyB + b.cost + 1); 
		}); 
		pq.offer(new Node(sx, sy, initPath, initCost));
		while(!pq.isEmpty()){
			Node curr = pq.poll();
			if (visited.contains(curr.x + "," + curr.y)){
				continue; 
			}			

			// update current path and current cost  
			curr.path += ", (" + curr.y + "," + curr.x + ")"; 
			curr.cost++;
			nodesExplored++;

			// if coordinates are that of end point - we have found the destination and can print the current path/cost and exit	
			if (curr.x == ex && curr.y == ey){
				if (printResult){
					System.out.println("Complete path " + curr.path.substring(1)); 
					System.out.println("Cost: " + curr.cost); 
					// System.out.println("Number of explored Nodes: " + explored); 
				}
				return curr;
			}
			
			// for current node visit all possible adjacent spaces and add to queue if they are valid
			for (int[] dir:dirs){
				int[] next = new int[]{curr.x+dir[0], curr.y+dir[1]};
				if (next[0]<0 || next[0]>=maze.length || next[1]<0 || next[1]>=maze[0].length || maze[next[0]][next[1]] == 1 || visited.contains(next[0] + "," + next[1])){
					continue;
				}
				pq.offer(new Node(next[0], next[1], curr.path, curr.cost));
			}
			
			// add current node to visited set
			visited.add(curr.x + "," + curr.y); 	
		}
		return null;
	}
	
	/**
	  * Main function - contains input test cases and scanner to allow user to specify which test case to execute
	  */
	public static void main(String[] args){
		List<String> testCases = new ArrayList<>();
		
		// input 1
		String input1 = new StringBuilder()
			.append("3 3\n")
			.append("0 0\n")
			.append("2 2\n")
			.toString();
		testCases.add(input1);

		// input 2
		String input2 = new StringBuilder()
			.append("4 4\n")
			.append("3 3\n")
			.append("0 1\n")
			.append("2 1 1 3\n")
			.toString();
		testCases.add(input2);

		// input 3
		String input3 = new StringBuilder()
			.append("6 6\n")
			.append("1 1\n")
			.append("4 4\n")
			.append("2 0 3 1 2 2 1 2 1 4\n")
			.toString();
		testCases.add(input3);

		// input 4
		String input4 = new StringBuilder()
			.append("6 8\n")
			.append("2 6\n")
			.append("3 5\n")
			.append("2 1 4 2 1 3 3 3 1 5 1 6 2 5 4 5 4 6\n")
			.append("3 2\n")
			.toString();
		testCases.add(input4);

		Scanner scanner = new Scanner(System.in);
		int inputSelection;
		System.out.println("Enter the test case you would like to test (1-4) or any other number to quit:");
        while((inputSelection = scanner.nextInt()) > 0 && inputSelection < 5) {
			try	{
            	testInput(testCases.get(inputSelection-1));
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
			System.out.println("Please enter another test case you would like to test (1-4) or any other number to quit:");
        }
	}
}