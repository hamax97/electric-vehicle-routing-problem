Authors: Hamilton Tobon Mosquera, Santiago Toro
Software used: Java 10

- To be able to compile and run the program you need:
  1- Java compiler -> javac. Or another java compiler if you want
  (with another compiler, you have to compile and run manually).

- If you are in a unix environment:

  - Go to src/ directory. In src/ directory run:
     - To compile:
     
       $ make
       
     - To run the program:
     
       $ make FILE="path/to/file" run

       In this case the test files are located in electric-vehilce-routing-problem/EVRP/tests so:

       $ make FILE=../tests/filename.txt run
       
     - Take care, you have to run the make from the src/ directory.
     - Or if you want to compile manually you can do it.
       
- If you are using windows:

  - If you have cygwin you can use the above instructions.
  - Else open the project in an ide, compile it and run it passing as argument the location of the file.

- If you want to read the documentation go to: electric-vehilce-routing-problem/EVRP/doc
