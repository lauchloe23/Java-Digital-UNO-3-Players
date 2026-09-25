# Java Uno Game

A GUI-based multiplayer Uno card game built in Java using the Model-View-Controller (MVC) design pattern and socket networking.

## Features

* **Multiplayer Support**: Connect and play with other users via Java sockets.
* **Graphical Interface**: Interactive GUI for viewing cards, playing turns, and chatting.
* **Game Logic**: Full implementation of standard Uno rules, card matching, and scoring.

## Tech Stack

* **Language**: Java
* **GUI**: Java Swing / AWT
* **Networking**: Java TCP Sockets (`java.net`)

## Project Structure

* **`UnoModel.java`**: Manages game rules, deck state, and scoring logic.
* **`UnoView.java`**: Handles visual elements, layout, and user interactions.
* **`UnoController.java`**: Connects the Model and View, managing user actions and network events.
* **`UnoNetwork.java`**: Handles low-level socket connections for real-time play.

## Getting Started

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/lauchloe23/Java-Uno-Game.git](https://github.com/lauchloe23/Java-Uno-Game.git)
   cd Java-Uno-Game

Compile the source files:
javac *.java

Run the game:
java Main

