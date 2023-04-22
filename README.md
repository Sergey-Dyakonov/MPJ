# Parallel Prim's algorithm research

---
### DoD:
1. Write a graph visualizer (maybe as API, endpoint gets matrix and returns an image)
2. Introduce complexity parameter for GraphUtil#generateGraph (complexity reflects percentage of connections)
3. Write tests with known graphs and their MSTs (different size, from 5 to 30+ with different complexity)
4. Write a parallel Prim's algorithm using threads
5. Write a parallel Prim's algorithm using MPI
6. Compare efficiency of sequential, threaded and MPI Prim's algorithms (different graphs' sizes, complexity and different amount of processes/threads)
7. Visualize results
8. Make conclusion