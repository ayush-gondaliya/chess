# ♟️ Chess — Java OOP Implementation

A fully playable two-player Chess game built from scratch in **Java**, using **Swing** for the GUI and core **Object-Oriented Programming principles** throughout the design.

This was a learning project to understand how abstract thinking in programming maps to a real-world system. Chess was a perfect fit because every piece has its own behavior, rules, movement logic, and interaction with the board.

---

## 🎮 What It Does

This project allows two players to play a complete game of chess on the same machine by taking turns.

The chessboard is rendered visually using **Java Swing**, and pieces can be moved using mouse interaction. The game checks the rules internally, so illegal moves are not allowed.

### Supported Features

- All standard chess piece movements:
  - King
  - Queen
  - Rook
  - Bishop
  - Knight
  - Pawn
- Castling support
  - Kingside castling
  - Queenside castling
- En-passant captures
- Pawn promotion with piece selection
- Check detection
- Checkmate detection
- Stalemate detection
- Legal move highlighting
- Turn-based two-player gameplay
- GUI-based board rendering using Java Swing

---

## 🏗️ Project Structure

The project follows a clean structure inspired by the **MVC architecture**.

```text
chess/
├── model/
│   ├── Board.java
│   ├── Square.java
│   ├── Move.java
│   └── PieceColour.java
│
├── model/pieces/
│   ├── Piece.java
│   ├── King.java
│   ├── Queen.java
│   ├── Rook.java
│   ├── Bishop.java
│   ├── Knight.java
│   └── Pawn.java
│
├── controller/
│   └── GameController.java
│
├── view/
│   ├── ChessBoardPanel.java
│   └── PromotionDialog.java
│
└── Main.java

```

## Folder Explanation

| Folder/File     | Description                                        |
| --------------- | -------------------------------------------------- |
| `model/`        | Contains the main game logic and data              |
| `model/pieces/` | Contains all chess piece classes                   |
| `controller/`   | Connects the model and view and controls game flow |
| `view/`         | Handles GUI rendering using Java Swing             |
| `Main.java`     | Entry point of the application                     |


## 🚀 Getting Started
Requirements :
  JDK 11 or higher.
  IntelliJ IDEA, Eclipse, NetBeans, or any Java-supported IDE(VsCode).


## Clone the Repository
git clone https://github.com/ayush-gondaliya/chess.git.
cd chess.

## Run the Project
Open the project in IntelliJ IDEA or VsCode.
Set the Project SDK to JDK 11 or higher.
Open Main.java.
Run the file.

## 📸 Screenshots 

Chess Board
![Chess Board](screenshots/chess-board.png) 

Pawn movements
 














En-passant:

 



Before En-passant
 

After En-passant
 


Bishop Movement
 
Bishop Eliminating Knight
 
 

 
Knight move
 
 

 
 

 

  
CASTLING

Before Castling
 
After Castling
 
 
Queen’s move
 
 
 
 
 
 
 






Check
 
 

Pawn saving king by eliminating knight
 



 
STALEMATE
Before Stalemate
 

After Stalemate (when you are in check then you can’t move unnecessary pieces)
 


 


 
 












King movement
    

 

 




 
Promotion
 
 
 
 
 

 







Rest of the play
 
 
 
 
 
 
 
 
 
 
