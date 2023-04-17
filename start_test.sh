# Script used for starting an test class (HelloWorld) on Ubuntu
# To specify number of processes used in the program,
# pass the argument to script, e.g this command start the program with 4 processes: start.sh 4
javac -cp .:$MPJ_HOME/lib/mpj.jar -d out/MPI HelloWorld.java
cd out/MPI
mpjrun.sh -np "$1" HelloWorld