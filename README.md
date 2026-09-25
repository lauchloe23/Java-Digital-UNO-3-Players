# Multiplayer Modern Uno Framework

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-MVC-blue?style=for-the-badge)
![Network](https://img.shields.io/badge/Networking-Java%20Sockets-brightgreen?style=for-the-badge)
![GUI](https://img.shields.io/badge/GUI-Swing%2FAWT-orange?style=for-the-badge)

A multi-threaded desktop implementation of the classic Uno card game built in Java. Designed around the **Model-View-Controller (MVC)** architectural pattern, this application demonstrates low-level socket networking, event-driven UI state binding, and object-oriented game logic design.

---

## Technical Highlights & Architecture

```

* **Model-View-Controller (MVC) Pattern**: Decouples domain logic (`UnoModel`), event handling/networking (`UnoController`), and UI updates (`UnoView`), resulting in a clean separation of concerns and maintainable architecture.
* **Low-Level Socket Communication (`UnoNetwork`)**: Utilizes custom Java TCP sockets and thread-safe channels to broadcast state changes, handle real-time chat, and synchronize turn transitions between distributed game instances.
* **Event-Driven UI**: Manages real-time graphical updates (card rendering, hand animations, action prompts) triggered dynamically by state mutations in the model layer.
* **Object-Oriented Design Principles**: Encapsulates card behaviors, player states, turn rules, and deck operations into robust, reusable domain objects.

---

## Core Features

* **Real-time Peer Synchronization**: Dedicated network ports manage peer-to-peer or client-server message serialization for continuous match state sync.
* **State & Deck Management**: Comprehensive implementation of core rules, card validation logic, draw/discard mechanics, score computation, and multi-player turn management.
* **Interactive Graphical Interface**: Dynamic GUI component layout supporting card selection, live match chat, operational action buttons, and status indicators.

---

## Tech Stack

* **Language**: Java 11+
* **Frameworks/Libraries**: Java Swing / AWT (GUI)
* **Networking**: Custom TCP/IP Sockets (`java.net.Socket`, `java.net.ServerSocket`)
* **Design Patterns**: Model-View-Controller (MVC), Observer, Event Dispatcher

---

## Getting Started

### Prerequisites

* **Java Development Kit (JDK) 11 or higher**: Verify installation via `java -version`.
* Any standard Java IDE (VS Code, IntelliJ IDEA, Eclipse) or terminal CLI.

### Installation & Execution

1. **Clone the Repository**
   ```bash
   git clone [https://github.com/lauchloe23/Java-Uno-Game.git](https://github.com/lauchloe23/Java-Uno-Game.git)
   cd Java-Uno-Game

```

2. **Compile the Project**
Ensure all source files (`UnoController.java`, `UnoModel.java`, `UnoView.java`, `UnoNetwork.java`, `Main.java`) are in the root source directory:
```bash
javac *.java

```


3. **Run the Application**
```bash
java Main

```



---

## System Components

| File | Technical Role |
| --- | --- |
| **`UnoModel.java`** | Core domain logic. Manages decks, player hands, game state mutations, rule validation, and score calculation. |
| **`UnoView.java`** | Presentation layer. Renders UI components, player hands, action prompts, and chat boxes. |
| **`UnoController.java`** | Application controller. Intercepts UI triggers, updates the model state, and relays commands to `UnoNetwork`. |
| **`UnoNetwork.java`** | Networking pipeline. Handles raw socket IO, packet serialization, and multi-threaded event dispatching. |

```

```
