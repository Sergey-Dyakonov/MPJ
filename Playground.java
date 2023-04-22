import mpi.*;

import java.util.Arrays;
import java.util.stream.Stream;

public class Playground {
    public static void main(String[] args) {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

       /* if (rank == 0) {
            int sum = Stream.of(1, 2, 3, 4, 5).mapToInt(value -> value).sum();
            System.out.println("Sending " + sum + " from rank: " + rank);
            MPI.COMM_WORLD.Send(new int[]{sum}, 0, 1, MPI.INT, 1, 1);
        } else {
            int sum = Stream.of(6, 7, 8, 9, 10).mapToInt(value -> value).sum();
            int[] sum2 = {0};
            MPI.COMM_WORLD.Recv(sum2, 0, 1, MPI.INT, 0, 1);
            System.out.println("Received " + sum2[0] + " to rank: " + rank);
            System.out.println(sum + sum2[0]);
        }*/



        MPI.Finalize();
    }
}
