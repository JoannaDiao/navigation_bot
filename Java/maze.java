import java.util.*; 

public class maze {
	public static void parseInputs(String input) throws Exception{
		String[] inputArr = input.split("\\n");
		if (inputArr.length<3){
			throw new Exception("Unexpected number of inputs");
		}

		int[][] maze = generateMaze(inputArr);
		String[] robotCoord = inputArr[1].split(" ");
		String[] userCoord = inputArr[2].split(" ");
		int robotX = Integer.valueOf(robotCoord[1]); 
		int robotY = Integer.valueOf(robotCoord[0]); 
		int userX = Integer.valueOf(userCoord[1]); 
		int userY = Integer.valueOf(userCoord[0]); 
		System.out.println("\nA* traversal"); 
		if (inputArr.length > 4){
			System.out.println("Agent starting at Robot, picking up Item and ending at User\n");
			// we have an item to pick up 
			String[] itemCoord = inputArr[4].split(" ");
			int itemX = Integer.valueOf(itemCoord[1]);
			int itemY = Integer.valueOf(itemCoord[0]);

			// for first iteration we make user an obstacle and traverse from robot to item
			maze[userX][userY] = 1;
			Node tmp = aStar(robotX, robotY, itemX, itemY, maze, "", -1, false);

			tmp.path += "-> Picked Up Item "; 
			// then we traverse from item to user continuing values from first traversal 
			maze[userX][userY] = 0;
			aStar(itemX, itemY, userX, userY, maze, tmp.path, tmp.cost-1, true);
		} else { 
			System.out.println("Agent starting at Robot and ending at User\n");
			aStar(robotX, robotY, userX, userY, maze, "", -1, true);
		}
	}
	/**
	  * Helper method to generate input maze - represented as an array of integers with 1 representing a blocked square and 0 representing empty
	  * 
	  * @param inputArr input array split by line
	  */
	public static int[][] generateMaze(String[] inputArr){
		String[] arrayDimensions = inputArr[0].split(" ");
		int[][] maze = new int[Integer.valueOf(arrayDimensions[1])][Integer.valueOf(arrayDimensions[0])];

		// we know there are obstacles
		if (inputArr.length>3){
			String[] obstacleCoord = inputArr[3].split(" ");
			for (int i=0; i<obstacleCoord.length/2; i++){
				maze[Integer.valueOf(obstacleCoord[2*i+1])][Integer.valueOf(obstacleCoord[2*i])] = 1;
			}
		}			
		return maze;			
	}

	public static class Node {
		public int x; 
		public int y; 
		public String path;
		public int cost;
		
		public Node(int x, int y, String path, int cost){
			this.x = x; 
			this.y = y;
			this.path = path;
			this.cost = cost;	
		}
	}

	/*
	 * A* search algorithm using priority queue and manhattan distance for heuristic 
	 */ 
	public static Node aStar(int sx, int sy, int ex, int ey, int[][] maze, String initPath, int initCost, boolean printResult){
		int[][] dirs = new int[][]{{0,1}, {0,-1},{1,0},{-1,0}};
		int explored = 0;
		HashSet<String> visited = new HashSet<>();
		PriorityQueue<Node> pq = new PriorityQueue<>((Node a, Node b) -> {
			// using manhattan distance to provide heuristic h(n) 
			int dxA = Math.abs(a.x - ex); 
			int dyA = Math.abs(a.y - ey); 
			int dxB = Math.abs(b.x - ex); 
			int dyB = Math.abs(b.y - ey); 
			return (dxA + dyA + a.cost) - (dxB + dyB + b.cost); 
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
			explored++;

			// if coordinates are that of end point - we have found the destination and can print the current path/cost and exit	
			if (curr.x == ex && curr.y == ey){
				if (printResult){
					System.out.println("Complete path " + curr.path.substring(1)); 
					System.out.println("Cost: " + curr.cost); 
					// System.out.println("Number of explored Nodes: " + explored); 
				}
				return curr;
			}

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
	  * Main function - executes each search technique three times 
	 * @throws Exception
	  */
	public static void main(String[] args) throws Exception{
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
        while((inputSelection = scanner.nextInt()) > 0 && inputSelection<5) {
            parseInputs(testCases.get(inputSelection-1));
			System.out.println("Please enter another test case you would like to test (1-4) or any other number to quit:");
        }
	}
}
