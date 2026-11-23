# Java Digital UNO (3 Players)

A 3-player networked UNO card game with a graphical interface, built in Java using the Model-View-Controller (MVC) pattern and TCP sockets.

## Authors
Built by Chloe Lau and teammates as a group project.

## Features

- **Online multiplayer:** one player hosts and two others join over the network
- **Full UNO rules:** card matching, skips, reverses, draw 2, wild and wild draw 4 cards, and player elimination
- **Swing GUI:** see your hand, play cards, and follow each turn
- **Card themes:** theme selection screen (the public version ships with the standard card set)
- **Custom decks:** card data is loaded from `cards.csv`

## Tech stack

- Java
- Java Swing / AWT for the GUI
- Java TCP sockets for networking (default port 5555)

## Project structure

| File | Purpose |
|---|---|
| `UnoModel.java` | Game rules, deck, turn order, and scoring |
| `UnoView.java` | GUI layout and user interaction |
| `UnoController.java` | Connects the model and view, handles network events (entry point) |
| `UnoNetwork.java` | Hosting and joining games over sockets |
| `SuperSocketMaster.java` | Socket helper class |
| `cards.csv` | Card list and image mapping |
| `build-jar.sh` | Builds a runnable `UnoGame.jar` |

## Getting started

**Option 1: run the jar**

```bash
java -jar UnoGame.jar
```

**Option 2: build from source**

```bash
git clone https://github.com/lauchloe23/Java-Digital-UNO-3-Players.git
cd Java-Digital-UNO-3-Players
javac *.java
java UnoController
```

To rebuild the jar, run `./build-jar.sh`.

**To play:** one player hosts a game, and the other two join using the host's IP address. All three need to be on the same network.
