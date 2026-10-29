/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */
public class Escape {

    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int[][] open = Graphs.open(w, broken);
        int n = w.cells(), deadEnds = 0;
        for (int[] neighbours : open) if (neighbours.length == 1) deadEnds++;

        boolean[] seen = new boolean[n];
        int[] stack = new int[n], direction = new int[n];
        stack[0] = 0;
        seen[0] = true;
        int visited = 1, top = 0;
        while (top >= 0 && stack[top] != n - 1) {
            int u = stack[top];
            if (direction[top] == 4) {
                top--;
                continue;
            }
            int v = switch (direction[top]++) {
                case 0 -> u >= w.cols() ? u - w.cols() : -1;
                case 1 -> u % w.cols() + 1 < w.cols() ? u + 1 : -1;
                case 2 -> u / w.cols() + 1 < w.rows() ? u + w.cols() : -1;
                default -> u % w.cols() > 0 ? u - 1 : -1;
            };
            if (v < 0 || seen[v]) continue;
            boolean door = false;
            for (int neighbour : open[u]) if (neighbour == v) { door = true; break; }
            if (!door) continue;
            seen[v] = true;
            visited++;
            stack[++top] = v;
            direction[top] = 0;
        }
        int[] path = top < 0 ? new int[0] : java.util.Arrays.copyOf(stack, top + 1);
        return new Result(path, visited, deadEnds);
    }
}
