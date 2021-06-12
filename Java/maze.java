import java.util.*; 

public class maze {
	/**
	  * Helper method to generate the example maze provided in the assignment - represented as an array of integers with 1 representing a blocked square and 0 representing empty
	  */
	public static int[][] generateMaze(String[] inputArr){
		String[] arrayDimensions = inputArr[0].split(" ");
		int[][] maze = new int[Integer.valueOf(arrayDimensions[0])][Integer.valueOf(arrayDimensions[1])];

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
	public static void aStar(int sx, int sy, int ex, int ey, int[][] maze){
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
		pq.offer(new Node(sx, sy, "", 0));
		while(!pq.isEmpty()){
			Node curr = pq.poll();
			if (visited.contains(curr.x + "," + curr.y)){
				continue; 
			}			

			// update current path and current cost  
			curr.path += "-> (" + curr.x + "," + curr.y + ") "; 
			curr.cost++;
			explored++;

			// if coordinates are that of end point - we have found the destination and can print the current path/cost and exit	
			if (curr.x == ex && curr.y == ey){
				System.out.println("Complete path " + curr.path); 
				System.out.println("Cost: " + curr.cost); 
				System.out.println("Number of explored Nodes: " + explored); 
				return;
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
	}
	
	/**
	  * Main function - executes each search technique three times 
	 * @throws Exception
	  */
	public static void main(String[] args) throws Exception{
		// String input = new StringBuilder()
		// 	.append("3 3\n")
		// 	.append("0 0\n")
		// 	.append("2 2\n")
		// 	.toString();

		String input = new StringBuilder()
			.append("6 6\n")
			.append("1 1\n")
			.append("4 4\n")
			.append("2 0 3 1 2 2 1 2 1 4\n")
			.toString();

		String[] inputArr = input.split("\\n");

		if (inputArr.length<3){
			throw new Exception("Unexpected number of inputs");
		}

		int[][] maze;
		try {
			maze = generateMaze(inputArr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		String[] robotCoord = inputArr[1].split(" ");
		String[] userCoord = inputArr[2].split(" ");
		
		int startX = Integer.valueOf(robotCoord[0]); 
		int startY = Integer.valueOf(robotCoord[1]); 
		int endX = Integer.valueOf(userCoord[0]); 
		int endY = Integer.valueOf(userCoord[1]);  
	

		System.out.println("\nA* traversal"); 
		System.out.println("Agent starting at S and ending at E1\n");
		aStar(startX, startY, endX, endY, maze);
	}

	
}
