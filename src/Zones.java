/**
 * Missions 4 and 5. The slides: a directed graph. Cells are 0 .. n-1, slide i goes from s.from()[i] to s.to()[i]
 * and takes s.time()[i]. There are no slides from a cell to itself, and the exit can always be reached from the start.
 *
 * Ready for you: Graphs.slidesFrom(s)[u] is the list of the slides that leave cell u, and Graphs.slidesInto(s)[v] is the list
 * of the slides that enter cell v (reversed graph). The lists hold slide numbers i; the cell at the other end is s.to()[i]
 * or s.from()[i].
 */
public class Zones {

    /**
     * Mission 4. Kosaraju. Returns zoneOf: zoneOf[cell] = number of the zone, numbers 0 .. zones-1.
     * Number the zones in the order in which the second pass finds them. That order is a topological order
     * of the zone graph: every slide between two zones goes from a smaller number to a larger number.
     *
     * Plan: 1) DFS on the slides, write the cells in the order in which they are finished (use a stack, not recursion);
     * 2) go through the cells from the last finished to the first; if a cell has no zone yet, DFS from it on the reversed
     * slides (slidesInto) and give every cell it reaches the next zone number.
     * Is DAG in Main is 1 when the number of zones equals the number of cells (no cycle), so you do not need a separate check.
     */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] from = Graphs.slidesFrom(s), into = Graphs.slidesInto(s);
        boolean[] seen = new boolean[n];
        int[] stack = new int[n], next = new int[n], finished = new int[n];
        int count = 0;
        for (int start = 0; start < n; start++) {
            if (seen[start]) continue;
            int top = 0;
            stack[0] = start;
            next[0] = 0;
            seen[start] = true;
            while (top >= 0) {
                int u = stack[top];
                if (next[top] == from[u].length) {
                    finished[count++] = u;
                    top--;
                } else {
                    int v = s.to()[from[u][next[top]++]];
                    if (!seen[v]) {
                        seen[v] = true;
                        stack[++top] = v;
                        next[top] = 0;
                    }
                }
            }
        }

        int[] zoneOf = new int[n];
        java.util.Arrays.fill(zoneOf, -1);
        int zones = 0;
        for (int i = n - 1; i >= 0; i--) {
            int start = finished[i];
            if (zoneOf[start] != -1) continue;
            int top = 0;
            stack[0] = start;
            zoneOf[start] = zones;
            while (top >= 0) {
                int u = stack[top--];
                for (int k : into[u]) {
                    int v = s.from()[k];
                    if (zoneOf[v] == -1) {
                        zoneOf[v] = zones;
                        stack[++top] = v;
                    }
                }
            }
            zones++;
        }
        return zoneOf;
    }

    /**
     * Mission 5. The graph of zones: the edge X to Y costs the cheapest slide from X to Y, walking inside a zone is free.
     * Go through the zones in the order of their numbers (no priority queue). Returns two numbers:
     * [0] flow stages: the number of zones on the longest chain of zones;
     * [1] the cost of the cheapest route from the zone of cell 0 to the zone of the last cell.
     */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        int[] head = new int[zoneCount], next = new int[s.count()];
        java.util.Arrays.fill(head, -1);
        for (int k = 0; k < s.count(); k++) {
            int from = zoneOf[s.from()[k]], to = zoneOf[s.to()[k]];
            if (from == to) continue;
            next[k] = head[from];
            head[from] = k;
        }

        int[] stages = new int[zoneCount];
        java.util.Arrays.fill(stages, 1);
        long[] distance = new long[zoneCount];
        java.util.Arrays.fill(distance, Long.MAX_VALUE / 4);
        distance[zoneOf[0]] = 0;
        int longest = 0;
        for (int z = 0; z < zoneCount; z++) {
            longest = Math.max(longest, stages[z]);
            for (int k = head[z]; k != -1; k = next[k]) {
                int to = zoneOf[s.to()[k]];
                stages[to] = Math.max(stages[to], stages[z] + 1);
                if (distance[z] != Long.MAX_VALUE / 4) {
                    distance[to] = Math.min(distance[to], distance[z] + s.time()[k]);
                }
            }
        }
        return new long[]{longest, distance[zoneOf[s.n() - 1]]};
    }
}
