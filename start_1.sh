# Script used for starting a program on Ubuntu
# To specify number of processes and size of graph used in the program,
# pass the arguments to script, e.g this command start the program with 4 processes and 100 vertices: start.sh 4 100
javac -cp .:$MPJ_HOME/lib/mpj.jar -d out/MPI AnotherPrim.java
cd out/MPI
mpjrun.sh -np "$1" AnotherPrim