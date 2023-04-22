import java.util.Map;
import java.util.concurrent.*;

class ParallelThreadPrim {
    private final int graphSize;

    public ParallelThreadPrim(int graphSize) {
        this.graphSize = graphSize;
    }

    int minKey(ConcurrentHashMap<Integer, Integer> key, boolean[] mstSet) {
        int min = Integer.MAX_VALUE, minIndex = -1;

        for (Map.Entry<Integer, Integer> entry : key.entrySet()) {
            int index = entry.getKey();
            int value = entry.getValue();

            if (!mstSet[index] && value < min) {
                min = value;
                minIndex = index;
            }
        }

        System.out.println(key.values());
        System.out.println(minIndex);
        return minIndex;
    }

    void primMST(int[][] graph, int numThreads) throws InterruptedException, ExecutionException {
        int[] parent = new int[graphSize];
        ConcurrentHashMap<Integer, Integer> key = new ConcurrentHashMap<>();
        boolean[] mstSet = new boolean[graphSize];
        ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < graphSize; i++) {
            key.put(i, Integer.MAX_VALUE);
            mstSet[i] = false;
            queue.add(i);
        }

        key.put(0, 0);
        parent[0] = -1;

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        Future<int[]>[] futures = new Future[numThreads];

        for (int i = 0; i < numThreads; i++) {
            Callable<int[]> task = () -> {
                int[] localParent = new int[graphSize];
                boolean[] localMstSet = new boolean[graphSize];

                for (int j = 0; j < graphSize; j++) {
                    localParent[j] = -1;
                    localMstSet[j] = false;
                }

                while (!queue.isEmpty()) {
                    int u = minKey(key, mstSet);

                    if (u == -1) {
                        break;
                    }

                    mstSet[u] = true;
                    queue.remove(u);

                    for (int v = 0; v < graphSize; v++) {
                        if (graph[u][v] != 0 && !mstSet[v] && graph[u][v] < key.get(v)) {
                            localParent[v] = u;
                            key.put(v, graph[u][v]);
                        }
                    }
                }

                return localParent;
            };

            futures[i] = executor.submit(task);
        }

        executor.shutdown();

        for (int i = 0; i < numThreads; i++) {
            int[] result = futures[i].get();

            for (int j = 0; j < graphSize; j++) {
                if (result[j] != -1) {
                    parent[j] = result[j];
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

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int[][] graph = {
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
        GraphUtil.printGraph(graph);

        ParallelThreadPrim prim = new ParallelThreadPrim(graph.length);
        prim.primMST(graph, graph.length);
    }
}
