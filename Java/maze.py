import collections
import numpy as np
import json
import heapq
from dataclasses import dataclass


class Maze:
    def __init__(self):
        self.test_case = TestCase(0, 0, 0, 0, 0, 0, [], 0, 0, False)
        self.grid = np.array(0)
        self.q = []
        self.visited = set()
        self.explored = 0

    def item_delivery(self):
        self.grid[self.test_case.yu, self.test_case.xu] = 1
        inter_path, inter_cost = self.aStar(self.test_case.xb, self.test_case.yb, self.test_case.xi, self.test_case.yi, self.grid, "", -1, True)
        self.grid[self.test_case.yu, self.test_case.xu] = 0
        path, cost = self.aStar(self.test_case.xi, self.test_case.yi, self.test_case.xu, self.test_case.yu, self.grid, inter_path, inter_cost-1, True)
        return
    
    def input_parse(self, test_case_num):
        '''
        Parse json file and extract inputs to populate maze
        Inputs
            test_case_num: test case number
        '''
        # parse json file and extract inputs
        file = open("test_cases.json")
        data = json.load(file)
        case = data["test_cases"][test_case_num-1]
        # create grid
        self.test_case.a, self.test_case.b = case["line1"]
        self.grid = np.zeros((self.test_case.b, self.test_case.a))
        # bot position (start)
        self.test_case.xb, self.test_case.yb = case["line2"]
        # user position (end)
        self.test_case.xu, self.test_case.yu = case["line3"]
        # obstacles
        if "line4" in case:
            for i in range(0, len(case["line4"]), 2):
                self.test_case.obs.append((case["line4"][i], case["line4"][i+1]))
                self.grid[case["line4"][i+1], case["line4"][i]] = 1
        # items
        if "line5" in case:
            self.test_case.xi, self.test_case.yi = case["line5"]
            self.test_case.item_exist = True

    
    def manhattan_dist(self, curr_x, curr_y, end_x, end_y):
        '''
        Calculate Manhattan Distance for heuristic
        Inputs
            curr_x: current bot x position
            curr_y: current bot y position
            end_x: user end x position
            end_y: user end y position
        Output
            dist: Manhattan distance
        '''
        dist = abs(curr_x - end_x) + abs(curr_y - end_y)
        return dist
    
    def aStar(self, sx, sy, ex, ey, grid, initPath, initCost, printResult):
        '''
        A* algorithm using priority queue and manhattan distnace for heuristic
        Inputs 
            sx: starting bot x position
            sy: starting bot y position
            ex: end user x position
            ey: end user y position
            initPath: initial path (empty)
            initCost: initial cost (-1)
            printResult: boolean to print path and cost
        Output

        '''
        dirs = [(0,1), (0,-1), (1,0), (-1,0)]
        self.visited.clear()
        self.q.clear()
        
        h = self.manhattan_dist(sx, sy, ex, ey)
        F = initCost + h
        init_node = Node(sx, sy, initPath, initCost)
        self.q.append((F, id(init_node), init_node))
        while self.q:
            # F = g(n) + h(n) where g(n) is cost
            F, i, curr = heapq.heappop(self.q)
            if (curr.x, curr.y) in self.visited:
                continue
            curr.path += ", (" + str(curr.x) + "," + str(curr.y) + ")"
            curr.cost += 1
            self.explored += 1

            # if the current coordinates are that of the end points, we have found the destination and can print the results
            if (curr.x == ex and curr.y == ey):
                if printResult:
                    print("Complete path: ", curr.path)
                    print("Cost: ", curr.cost)
                return curr.path, curr.cost
            
            for i, j in dirs:
                next_node = [curr.x + i, curr.y + j]
                if next_node[0]<0 or next_node[0]>len(grid[0])-1 or next_node[1]<0 or next_node[1]>len(grid)-1 or (next_node[0], next_node[1]) in self.visited or grid[next_node[1], next_node[0]] == 1:
                    continue
                new_h = self.manhattan_dist(curr.x, curr.y, ex, ey)
                new_F = curr.cost + new_h + 1
                new_node = Node(next_node[0], next_node[1], curr.path, curr.cost)
                heapq.heappush(self.q, (new_F, id(new_node), new_node))
                
            self.visited.add((curr.x, curr.y))

        return curr.path, curr.cost
            
# Data class for nodes in priority queue       
@dataclass
class Node:
    x: int
    y: int
    path: str
    cost: int

# Data class for test case information
@dataclass
class TestCase:
    # grid dimensions
    a: int
    b: int
    # bot coord
    xb: int
    yb: int
    # user coord
    xu: int
    yu: int
    # obstacle coords
    obs: list
    # item coord
    xi: int
    yi: int
    item_exist: bool = False



if __name__ == '__main__':
    test_num = input("Enter the test case number: ")
    maze = Maze()
    maze.input_parse(int(test_num))
    if maze.test_case.item_exist:
        maze.item_delivery()
    else:
        maze.aStar(maze.test_case.xb, maze.test_case.yb, maze.test_case.xu, maze.test_case.yu, maze.grid, "", -1, True)
