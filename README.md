# Battleship Assignment - COMP10205

## Overview
This was the last assignment in Data Structures and Algorithms - COMP10205 in the 4th semester at Mohawk College. The full description of the assignment is as below. 

The main difference between the traditional battleship game and this assignment is that this assignment only simulates the hunting and sinking part of the game and the player is not notified of which opponent's ship is being hit.

Overall, the main purpose of the lab is to create a bot that utilizes strategies to achieve the lowest average number of shots per 10,000 or 100,000 battleship games. 

## Run the project
1. Clone the repository
2. Open the folder with Intellij or an IDE for Java
3. Hit Run to run the project

## Achievements 
- Final Mark: 125%
- 2020 Battleship Winner 
- The best student score since the assignment was introduced to the course in 2014
- Average: `The Average # of Shots = 46.15`
- Best Average: `The Average # of Shots = 45.75`


## Team Members 
- Man Vu 
- Huy Mac

## Bot Strategy
Our bot combines the concept of **Probability Density Function** and **Parity Hunting** to minimize the number of shots taken to sink all ships. 

The first concept calculates the most probable locations where the opponent's ships can be possibly placed. After each shot, the probability of each cell of the 10x10 board is cleared and re-calculated based on previous shots. As the game progresses, some locations become more probable (higher probability values are assigned to these cells) and others become impossible. 

The second concept is activated only if the last remaining ship on the board is the smallest ship which has the length of 2. Applying Parity Hunting, also known as Diagonal Skew, can increase the chance of hitting the last ship. 

## Full Description
### Background
You are to write a program that will play a limited game of battleship where you will attempt to sink all of the ships with a minimum number of shots. The game of battleship is typically played with two players, each of which place 5 ships of various sizes on a 10 x 10 grid. Each player on a turn by turn basis attempts to place a shot where the opponent has placed a ship. Of course, your opponent can not see where you have placed your ships and you cannot see where the opponent has placed their ships. You typically call out shots to try and find and sink each of the ships. The player that can sink all the opponent ships. The first player to sink all the other ships is the Winner. For a more complete description see Battleship.

In this instance of BattleShip the API you have been provided will randomly place 5 ships (lengths 2,3,3,4,5) on the board. A total of 17 spaces on the board out of 100 will have a ship. Once you obtain 17 hits you have solved the game. Your goal is to achieve the lowest average number of shots when the game is played 10000 times. There are many different strategies that can be used to solve this problem.

The starting project provided includes the BattleShip API. The public methods of the Battleship API are described below:

- BattleShip() - you need to call the constructor once in your program to create an instance of the battleship game.

- boolean shoot(Point shot) - you need to call this function to make each shot. See the sample source code for an example use.

- int numberOfShipsSunk() - returns the total number of ships sunk at any point during the game. It is a good idea to use this method to determine when a ship has been sunk.

- boolean allSunk() - returns a boolean value that indicates whether all the ships have been sunk.

- int totalShotsTaken() - returns the total number of shots taken. Your code needs to be responsible for ensuring the same shot is not taken more than once.

- int[] shipSizes() - returns an array of the ship sizes. The length of the array indicates how many ships are present. This array is fixed for the game with ship sizes of 2,3,3,4,5. It does not update when a ship is sunk. You must write logic in your code to determine which ship has been sunk.

- enum CellState - this enum object is very useful for marking cells has either Empty, Hit or Miss. It also has a convenience toString method so that can be used for printing purposes. You may also create your own Enum / Class for this in your code, but it is suggested that you use this instead of integers / characters to mark a Cell state

### Best Scores (based on a run of 10000 games)
- Student best score 2014 class = 51.61
- Student best score 2015 class = 46.86
- Student best score 2016 class = 47.70
- Student best score 2017 class = 46.83
- Student best score 2019 class = 48.56
- Student best score 2020 class (so far) = 51.21
- Professor best score all time = 46.10