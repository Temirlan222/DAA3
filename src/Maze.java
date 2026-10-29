/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet d = new DisjointSet(w.cells());
        int[] chosen = new int[w.cells() - 1];
        int count = 0;
        for (int k = 0; k < w.count() && count < chosen.length; k++) {
            if (d.union(w.a()[k], w.b()[k])) chosen[count++] = k;
        }
        return java.util.Arrays.copyOf(chosen, count);
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        long[] order = new long[w.count()];
        for (int k = 0; k < order.length; k++) {
            order[k] = ((long) w.cost()[k] << 32) | k;
        }
        java.util.Arrays.sort(order);
        DisjointSet d = new DisjointSet(w.cells());
        int[] chosen = new int[w.cells() - 1];
        int count = 0;
        for (long entry : order) {
            int k = (int) entry;
            if (d.union(w.a()[k], w.b()[k])) {
                chosen[count++] = k;
                if (count == chosen.length) break;
            }
        }
        return java.util.Arrays.copyOf(chosen, count);
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        int n = w.cells(), m = w.count();
        int[] head = new int[n], next = new int[2 * m], edge = new int[2 * m];
        java.util.Arrays.fill(head, -1);
        int p = 0;
        for (int k = 0; k < m; k++) {
            int a = w.a()[k], b = w.b()[k];
            edge[p] = k; next[p] = head[a]; head[a] = p++;
            edge[p] = k; next[p] = head[b]; head[b] = p++;
        }

        boolean[] visited = new boolean[n];
        java.util.PriorityQueue<Long> frontier = new java.util.PriorityQueue<>();
        visited[0] = true;
        addEdges(0, w, head, next, edge, visited, frontier);
        long total = 0;
        int cells = 1;
        while (!frontier.isEmpty() && cells < n) {
            long item = frontier.remove();
            int k = (int) item;
            int a = w.a()[k], b = w.b()[k];
            int newCell = visited[a] ? b : a;
            if (visited[newCell]) continue;
            visited[newCell] = true;
            cells++;
            total += w.cost()[k];
            addEdges(newCell, w, head, next, edge, visited, frontier);
        }
        return total;
    }

    private static void addEdges(int cell, Input.Walls w, int[] head, int[] next,
                                 int[] edge, boolean[] visited,
                                 java.util.PriorityQueue<Long> frontier) {
        for (int p = head[cell]; p != -1; p = next[p]) {
            int k = edge[p];
            int other = w.a()[k] == cell ? w.b()[k] : w.a()[k];
            if (!visited[other]) frontier.add(((long) w.cost()[k] << 32) | k);
        }
    }
}
