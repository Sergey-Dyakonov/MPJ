import java.util.Arrays;

import mpi.MPI;
import mpi.Status;

public class ParallelPrimMPI {

    public static void main(String[] args) {
        MPI.Init(args);

        // Отримання інформації про ранк та кількість процесів
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        // Визначення кількості вершин графа
        int numVertices = 5;

        // Генерація випадкових ваг ребер та вершин
        int[][] graph = {{0, 5, 5, 4, 2},
                {5, 0, 5, 9, 4},
                {5, 5, 0, 9, 10},
                {4, 9, 9, 0, 5},
                {2, 4, 10, 5, 0}};

        GraphUtil.printGraph(graph);

        // Ініціалізація змінних для алгоритму Пріма
        int[] selectedVertices = new int[numVertices];
        int[] selectedWeights = new int[numVertices];
        boolean[] visited = new boolean[numVertices];

        // Початкове значення ваги для всіх вершин
        for (int i = 0; i < numVertices; i++) {
            selectedWeights[i] = Integer.MAX_VALUE;
        }

        // Вершина 0 вибирається як початкова
        selectedVertices[0] = 0;
        selectedWeights[0] = 0;
        visited[0] = true;

        // Повідомлення про готовність для початку алгоритму
        MPI.COMM_WORLD.Barrier();

        long start = System.currentTimeMillis();

        // Паралельний алгоритм Пріма
        for (int i = 0; i < numVertices - 1; i++) {
            // Пошук вершини з мінімальною вагою серед вибраних вершин
            int minWeight = Integer.MAX_VALUE;
            int minVertex = -1;
            for (int j = 0; j <= i; j++) {
                System.out.println("Searching min vertices among subgraph");
                int vertex = selectedVertices[j];
                for (int k = 0; k < numVertices; k++) {
                    if (!visited[k] && graph[vertex][k] != 0 && graph[vertex][k] < minWeight) {
                        minWeight = graph[vertex][k];
                        minVertex = k;
                    }
                }
            }

            // Розсилка мінімальної ваги та вершини всім процесам
            int[] message = new int[]{minWeight, minVertex};
            for (int j = 0; j < size; j++) {
                System.out.println("Sending message to " + j);
                if (j != rank) {
                    MPI.COMM_WORLD.Send(message, 0, 2, MPI.INT, j, 0);
                }
            }
            // Отримання мінімальної ваги та вершини від інших процесів
            for (int j = 0; j < size; j++) {
                if (j != rank) {
                    System.out.println("getting vertices from other processes");
                    Status status = MPI.COMM_WORLD.Probe(j, 0);
                    int[] receivedMessage = new int[2];
                    MPI.COMM_WORLD.Recv(receivedMessage, 0, 2, MPI.INT, j, 0);
                    int receivedWeight = receivedMessage[0];
                    int receivedVertex = receivedMessage[1];
                    if (receivedWeight < minWeight) {
                        minWeight = receivedWeight;
                        minVertex = receivedVertex;
                    }
                }
            }

            // Оновлення списку вибраних вершин та ваг
            selectedVertices[i + 1] = minVertex;
            selectedWeights[minVertex] = minWeight;
            visited[minVertex] = true;
        }

        long end = System.currentTimeMillis();

        // Виведення результатів
        if (rank == 0) {
            int totalWeight = 0;
            for (int i = 0; i < numVertices; i++) {
                totalWeight += selectedWeights[i];
            }
            System.out.println("MST: " + Arrays.toString(selectedVertices));
            System.out.println("Total weight: " + totalWeight);
            System.out.println("Execution time: " + (end - start) + " ms");
        }

        MPI.Finalize();
    }
}
