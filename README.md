# TicTacToe - Java
## By: Joel Fischer

### Overview
A project to build a simple TicTacToe game in college. I went slightly overboard. I decided to build in legitimate AI
play and network play over TCP. At the time I had a lot of time on my hands, so I decided to stretch myself.

I think the friend (who was in the same class) I got to test it's first reaction was "Holy s\*\*\*\* this thing
actually works!", his second was "Dude, what the hell, you're gonna make my project look terrible". To be honest, I'm 
not even sure my Professor ran the projects...

So, yeah, 2 player local, 2 player network (buggy if you try to play another match without
disconnecting/reconnecting) including a chat window (I told you, I went nuts), and 1 player vs AI, a fairly decent AI, 
though totally beatable basically every time once you learn his patterns, and AI vs AI (again, I was insane).

After all that remember that requirements of the project did not include AI, did not include a GUI, did not include
network play, and certainly not chat functionality. I was just bored at the time.

It's kind of ironic that the project I'm most proud of out of college is a TicTacToe game...

### Compiling ([thanks infotek](https://github.com/joeljfischer/TicTacToe/issues/1))
Compile the .java files to .class files

javac *.java

Create the jar file:

jar cvf tictactoe.jar *.class

Execute the Class with main()

java -cp tictactoe.jar Core

To run it without having to tell java what class has main(), create the following file in the src directory:

MANIFEST.MF

with the following contents:
`
Main-Class: Core

`
It requires that extra newline

Create the jar file including the manifest:

jar cmvf MANIFEST.MF tictactoe.jar *.class

Execute it using the following command:

java -jar tictactoe.jar
