# Utrecht University - AI Bachelor project

This is my final project for AI major.
In this repository I am researching how well deeplearning can be used for the game of [Reversi](https://en.wikipedia.org/wiki/Reversi)

## Getting started

You will need `java 8` and `maven` to get everything up and running.

Clone the repository and run `mvn compile exec:java`. This will pull up the interface and allow you to play against the AI.

You can also the the `-Dexec.mainClass="uucki.CreateData"` and `-Dexec.mainClass="uucki.Learn"` to create test data to feed to the neural network and train it. (Very much WorkInProgress at the moment)

## Various algorithms used

Minimax with (and without) alpha beta pruning has been implemented with a basic heuristic that gives weights to cells on the board to determine what move to make. This algorithm will beat most beginner players.

Monte Carlo Tree Search using DeepLearning heuristic - This is a Work in progress but the basic idea has been taken from AlphaGo. It will use a Convolutional Neural Network to determine if a board is better for the black or white player. And a neural network to simulate games at each leaf of the MCTS.
