

// TODO 1: write a graph visualizer (maybe as API, endpoint gets matrix and returns an image)
// TODO 2: introduce complexity parameter for GraphUtil#generateGraph (complexity reflects percentage of connections)
// TODO 3: write tests with known graphs and their MSTs (different size, from 5 to 30+ with different complexity)
// TODO 4: write a parallel Prim's algorithm using threads
// TODO 5: write a parallel Prim's algorithm using MPI
// TODO 6: (probably use AOP for getting execution time) compare efficiency of sequential, threaded and MPI Prim's algorithms (different graphs' sizes, complexity and different amount of processes/threads)
// TODO 7: visualize results
// TODO 8: make conclusion
// TODO 9: clean-up & refactor

class SequentialPrim {
    private final int graphSize;

    public SequentialPrim(int v) {
        graphSize = v;
    }

    int minKey(int[] key, boolean[] mstSet) {
        int min = Integer.MAX_VALUE, minIndex = -1;

        for (int i = 0; i < graphSize; i++) {
            if (!mstSet[i] && key[i] < min) {
                min = key[i];
                minIndex = i;
            }
        }

        return minIndex;
    }

    void primMST(int[][] graph) {
        int[] parent = new int[graphSize];
        int[] key = new int[graphSize];
        boolean[] mstSet = new boolean[graphSize];

        for (int i = 0; i < graphSize; i++) {
            key[i] = Integer.MAX_VALUE;
            mstSet[i] = false;
        }

        key[0] = 0;
        parent[0] = -1;

        for (int count = 0; count < graphSize - 1; count++) {
            int u = minKey(key, mstSet);
            mstSet[u] = true;

            for (int v = 0; v < graphSize; v++) {
                if (graph[u][v] != 0 && !mstSet[v] && graph[u][v] < key[v]) {
                    parent[v] = u;
                    key[v] = graph[u][v];
                }
            }
        }

        printMST(parent, graph);
    }

    void printMST(int[] parent, int[][] graph) {
        System.out.println("Edge \tWeight");
        int sum = 0;
        for (int i = 1; i < graphSize; i++) {
            System.out.println(parent[i] + " - " + i + "\t" + graph[i][parent[i]]);
            sum += graph[i][parent[i]];
        }
        System.out.println("Sum: " + sum);
    }

    public static void main(String[] args) {
        int[][] graph =
                /*{
                {0, 2, 0, 6, 0},
                {2, 0, 3, 8, 5},
                {0, 3, 0, 0, 7},
                {6, 8, 0, 0, 9},
                {0, 5, 7, 9, 0}};*/
                {
                        {0, 3, 8, 7, 5, 3, 8, 2, 10, 10, 7, 7, 5, 9, 10, 9, 4, 8, 2, 9,},
                        {3, 0, 3, 9, 8, 2, 6, 3, 7, 8, 8, 7, 9, 6, 10, 10, 9, 8, 6, 5,},
                        {8, 3, 0, 7, 8, 10, 9, 6, 5, 3, 3, 3, 10, 4, 4, 6, 5, 6, 4, 7,},
                        {7, 9, 7, 0, 6, 5, 7, 5, 7, 2, 10, 6, 4, 1, 4, 3, 7, 7, 4, 5,},
                        {5, 8, 8, 6, 0, 10, 2, 6, 7, 4, 1, 5, 6, 7, 7, 1, 9, 5, 8, 1,},
                        {3, 2, 10, 5, 10, 0, 3, 3, 7, 10, 3, 5, 3, 10, 10, 3, 10, 1, 5, 10,},
                        {8, 6, 9, 7, 2, 3, 0, 5, 4, 10, 10, 5, 1, 3, 8, 3, 9, 2, 8, 7,},
                        {2, 3, 6, 5, 6, 3, 5, 0, 4, 10, 1, 3, 8, 2, 9, 3, 6, 1, 3, 10,},
                        {10, 7, 5, 7, 7, 7, 4, 4, 0, 2, 8, 9, 6, 3, 6, 10, 10, 3, 4, 1,},
                        {10, 8, 3, 2, 4, 10, 10, 10, 2, 0, 10, 10, 3, 6, 9, 4, 9, 6, 3, 8,},
                        {7, 8, 3, 10, 1, 3, 10, 1, 8, 10, 0, 8, 8, 9, 5, 2, 3, 6, 2, 4,},
                        {7, 7, 3, 6, 5, 5, 5, 3, 9, 10, 8, 0, 8, 10, 6, 10, 10, 1, 9, 8,},
                        {5, 9, 10, 4, 6, 3, 1, 8, 6, 3, 8, 8, 0, 4, 10, 6, 5, 9, 8, 1,},
                        {9, 6, 4, 1, 7, 10, 3, 2, 3, 6, 9, 10, 4, 0, 5, 1, 3, 5, 7, 9,},
                        {10, 10, 4, 4, 7, 10, 8, 9, 6, 9, 5, 6, 10, 5, 0, 10, 9, 5, 10, 10,},
                        {9, 10, 6, 3, 1, 3, 3, 3, 10, 4, 2, 10, 6, 1, 10, 0, 10, 3, 2, 6,},
                        {4, 9, 5, 7, 9, 10, 9, 6, 10, 9, 3, 10, 5, 3, 9, 10, 0, 3, 7, 4,},
                        {8, 8, 6, 7, 5, 1, 2, 1, 3, 6, 6, 1, 9, 5, 5, 3, 3, 0, 7, 3,},
                        {2, 6, 4, 4, 8, 5, 8, 3, 4, 3, 2, 9, 8, 7, 10, 2, 7, 7, 0, 4,},
                        {9, 5, 7, 5, 1, 10, 7, 10, 1, 8, 4, 8, 1, 9, 10, 6, 4, 3, 4, 0,},
                };

        SequentialPrim mst = new SequentialPrim(graph.length);
        mst.primMST(graph);
    }
}
