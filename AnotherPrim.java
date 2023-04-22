import mpi.*;

import java.util.*;

public class AnotherPrim {

    public static void main(String[] args) throws Exception {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int[][] graph = {{0, 2, 0, 6, 0},
                {2, 0, 3, 8, 5},
                {0, 3, 0, 0, 7},
                {6, 8, 0, 0, 9},
                {0, 5, 7, 9, 0}};
        System.out.println("Initialized");


        int n = graph.length;
        int[] key = new int[n];
        Arrays.fill(key, Integer.MAX_VALUE);
        boolean[] mstSet = new boolean[n];
        int[] parent = new int[n];

        if (rank == 0) {
            mstSet[0] = true;
            key[0] = 0;
            parent[0] = -1;

            System.out.println("sending data to processes");
            for (int i = 1; i < size; i++) {
                MPI.COMM_WORLD.Send(new int[] {i}, 0, 1, MPI.INT, i, 0);
                MPI.COMM_WORLD.Send(mstSet, 0, n, MPI.BOOLEAN, i, 1);
                MPI.COMM_WORLD.Send(key, 0, n, MPI.INT, i, 2);
                MPI.COMM_WORLD.Send(parent, 0, n, MPI.INT, i, 3);
            }

            for (int i = 1; i < n; i++) {
                int[] msg = new int[2];
                MPI.COMM_WORLD.Recv(msg, 0, 2, MPI.INT, MPI.ANY_SOURCE, 4);
                int minKey = msg[0];
                int minVertex = msg[1];

                mstSet[minVertex] = true;

                for (int j = 1; j < size; j++) {
                    System.out.println("Send: minVertex = " + minVertex + ", j = " + j);
                    MPI.COMM_WORLD.Send(new int[] {minVertex}, 0, 1, MPI.INT, j, 5);
                }

                for (int j = 1; j < n; j++) {
                    if (!mstSet[j]) {
                        System.out.println("Recv: graph = " + Arrays.deepToString(graph));
                        MPI.COMM_WORLD.Recv(new int[] {graph[minVertex][j], j}, 0, 2, MPI.INT, MPI.ANY_SOURCE, 6);
                    }
                }

                for (int j = 1; j < size; j++) {
                    System.out.println("Send: minKey = " + minKey + ", j = " + j);
                    MPI.COMM_WORLD.Send(new int[] {minKey}, 0, 1, MPI.INT, j, 7);
                }
            }

            for (int i = 1; i < size; i++) {
                System.out.println("Send: i = " + i);
                MPI.COMM_WORLD.Send(new int[] {-1}, 0, 1, MPI.INT, i, 8);
            }
        } else {
            int[] msg = new int[1];
            MPI.COMM_WORLD.Recv(msg, 0, 1, MPI.INT, 0, 0);
            int myVertex = msg[0];

            System.out.println("Receiving data");
            MPI.COMM_WORLD.Recv(mstSet, 0, n, MPI.BOOLEAN, 0, 1);
            MPI.COMM_WORLD.Recv(key, 0, n, MPI.INT, 0, 2);
            MPI.COMM_WORLD.Recv(parent, 0, n, MPI.INT, 0, 3);

            while (myVertex != -1) {
                int minKey = Integer.MAX_VALUE;
                int minVertex = -1;
                int minEdgeWeight = Integer.MAX_VALUE;

                for (int i = 0; i < n; i++) {
                    if (mstSet[i]) {
                        for (int j = 0; j < n; j++) {
                            if (!mstSet[j] && graph[i][j] != 0 && graph[i][j] < minEdgeWeight) {
                                minEdgeWeight = graph[i][j];
                                minVertex = j;
                            }
                        }
                    }
                }

                MPI.COMM_WORLD.Send(new int[] {minKey, minVertex}, 0, 2, MPI.INT, 0, 4);

                int[] recvMsg = new int[1];
                System.out.println("Recv: recvMsg = " + recvMsg);
                MPI.COMM_WORLD.Recv(recvMsg, 0, 1, MPI.INT, 0, 5);
                int selectedVertex = recvMsg[0];

                if (graph[minVertex][selectedVertex] < key[selectedVertex]) {
                    key[selectedVertex] = graph[minVertex][selectedVertex];
                    parent[selectedVertex] = minVertex;
                }

                System.out.println("Send: key = " + Arrays.toString(key));
                MPI.COMM_WORLD.Send(new int[] {key[selectedVertex]}, 0, 1, MPI.INT, 0, 6);
            }
        }

        MPI.Finalize();
    }
}
