# Uno Multiplayer Game

Welcome to the **Uno Multiplayer Game**! 
An interactive Java game developed for the Grade 12 Computer Science (ICS4U1) Culminating Performance Task. This project demonstrates core computer science concepts, including object-oriented programming, event-driven design, and graphical user interface development by structuring the game loop, user input, and logic across multiple classes.
This repository contains the core controller implementation that coordinates game states, user actions, and real-time network interactions.

---

## Installation

Follow these steps to clone the repository and set up the project locally.

**Prerequisites**
1. Java Development Kit
2. Java IDE

**Setup Instructions**
Clone the respository through: [https://github.com/chloelau26/ISC4U1_Chloe-Lau-Sydney-Khang-CPT]
After cloning, open the project folder in your preferred IDE and locate the main entry file (Main.java).

**Java File**
1. **Model (`UnoModel`)**: 
   * Manages the underlying game state, including player hands, deck shuffling, rules validation, scoring, and turn management.
2. **View (`UnoView`)**: 
   * Handles the visual presentation of the game, displaying player cards, chat windows, buttons, and animations.
3. **Controller (`UnoController`)**: 
   * Actively mediates communication between the Model and View. It captures user inputs from the UI, instructs the model to update states, and broadcasts live changes to the connected player via the networking layer.
4. **Network Helper (`UnoNetwork`)**: 
   * Manages low-level Java socket connections, allowing communication over dedicated network ports.

---
## Features
1. Interactive gameplay
2. Object-oriented design
3. Graphic user interface
4. Game state management

---
## Documentation
**Usage & Running the Program**
You can launch the game either through your IDE or directly from the terminal.

**Using an IDE:**
1. Open the project folder in your IDE.
2. Right-click the Main.java class and select Run.

**Using the terminal:**
Navigate to your project's source directory and execute the following commands:

---
### Execution Steps
1. Ensure all associated files (`UnoController.java`, `UnoModel.java`, `UnoView.java`, `UnoNetwork.java`) are located in the same source directory.
2. Open your terminal or command prompt and navigate to the source directory.
3. Compile all files
