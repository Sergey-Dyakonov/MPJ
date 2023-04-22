import java.util.Random;

public class GraphUtil {
    public static int[][] generateGraph(int numVertices) {
        int[][] graph = new int[numVertices][numVertices];
        Random random = new Random();
        for (int i = 0; i < numVertices; i++) {
            for (int j = i + 1; j < numVertices; j++) {
                int weight = random.nextInt(10) + 1;
                graph[i][j] = weight;
                graph[j][i] = weight;
            }
        }
        return graph;
    }

    public static void printGraph(int[][] graph) {
        System.out.println("{");
        for (int[] line : graph) {
            System.out.print("{");
            for (int j = 0; j < line.length; j++) {
                System.out.print(line[j] + ", ");
            }
            System.out.println("},");
        }
        System.out.println("}");
    }
}
