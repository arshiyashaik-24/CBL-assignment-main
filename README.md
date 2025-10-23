<p align="center">
  <b>🧟‍♂️ Keys of Survival</b><br>
  <i>By Arshiya Shaik & Priyanshu Guha</i><br>
  <small>CBL Assignment – Programming (2IP90), TU/e</small>
</p>

---

## Overview  
**Keys of Survival** is a lane-based survival game set in a **zombie apocalypse**, developed as part of the **Challenge-Based Learning (CBL)** project for the course *Programming (2IP90)* at the **Eindhoven University of Technology (TU/e)**.  

Players must **run through three (or more!) lanes**, **collect keys**, and **unlock matching doors** to save survivors — all while avoiding zombies and managing limited health. Each run tests quick thinking, timing, and adaptability in an unpredictable environment.  

---
## How to Run
1. Setup Requirements

Before running Keys of Survival, ensure your system meets the following prerequisites:

Java JDK 17 or higher installed on your system

Download Java JDK

A Java IDE (e.g., IntelliJ IDEA, Eclipse, or NetBeans) or a command line environment with Java configured in PATH.

All game assets (sprites, background images, audio files) are located in the project’s assets/ folder.

Ensure all .java files are compiled together — the Main.java file serves as the entry point of the program.

2. Step-by-Step Instructions to Run the Game
Open your IDE (e.g., IntelliJ or Eclipse).

Import or open the project folder named KeysOfSurvival.

Ensure the folder structure is as follows:

KeysOfSurvival/ <br/>
├── src/ <br/> 
│   ├── Main.java <br/>
│   ├── GamePanel.java <br/>
│   ├── Runner.java <br/>
│   ├── Door.java <br/>
│   ├── Key.java <br/>
│   ├── Zombie.java <br/>
│   └── ... <br/>
├── assets/ <br/>
│   ├── sprites/ <br/>
│   ├── sounds/ <br/>
│   └── background/ <br/>
└── README.md <br/>

Right-click on Main.java → Run ‘Main’.

The main window will launch with the title screen — click Start or press Enter to begin.

3. Features to Test During Gameplay

| **Feature**                   | **How to Access/Test**                                                    | **Expected Behavior**                                                                  |
| ----------------------------- | ------------------------------------------------------------------------- | -------------------------------------------------------------------------------------- |
| **Lane Movement (Core UX)**   | Use **Left** / **Right Arrow Keys** to move the runner between lanes.     | Runner moves smoothly with visible lane shifts and no input delay.                     |
| **Key Collection**            | Move over colored keys while running.                                     | Key disappears; HUD updates with new key icon.                                         |
| **Door Interaction**          | Encounter locked doors matching your collected keys.                      | Correct key opens door with animation; wrong key ends run or triggers mini-game.       |
| **Zombies (Obstacles)**       | Stay in the same lane as a zombie.                                        | Collision causes a **Game Over** or revival option if health > 3.                      |
| **Health & Revival**          | Open 5 doors to gain +1 heart; collide with zombie to test revival popup. | Revival prompt appears, allowing player to continue if hearts ≥ 3.                     |
| **Speed Progression**         | Play continuously for several minutes.                                    | Game speed increases gradually (×2 → ×5), “SPEED UP!” appears mid-screen.              |
| **Pause / Resume**            | Press **P** or **ESC** at any point.                                      | Game pauses with overlay; press again to resume.                                       |
| **Mute Button**               | Click the sound icon in top-right corner.                                 | Toggles sound on/off with visual change in icon.                                       |
| **HUD Functionality**         | Observe health, keys, and score during play.                              | All values update in real time with clear readability.                                 |
| **Mini-Games**                | Hit a locked door without a matching key.                                 | A random mini-game (Tic Tac Toe, Tap Fruits, Connect Wires, Button Sequence) launches. |
| **Randomized Map Generation** | Restart the game multiple times.                                          | Each run shows a unique layout of doors, keys, and zombies.                            |
| **Visual & Audio Feedback**   | Observe background, animations, and sounds.                               | Background scrolls; sound effects play contextually; sprites animate smoothly.         |


4. Troubleshooting

Black Screen / No Window Appears: Ensure Main.java contains your game initialization call (new GamePanel();).

No Sound: Check that .wav or .mp3 files are correctly placed in the assets/sounds directory.

Lag or Frame Drops: Reduce animation delay or disable background scrolling in code for testing.

5. Testing Checklist
<br/>
✅ Smooth lane transitions <br/>
✅ Correct key-door logic <br/>
✅ Functional HUD & live score updates <br/>
✅ Speed progression triggers correctly <br/>
✅ Pause/resume and mute systems work <br/>
✅ All mini-games launch and return to main gameplay <br/>
✅ Random map generation produces non-repetitive runs <br/>

## Core Gameplay Summary  
- **Lane Movement:** Move left and right using arrow keys. Smooth, responsive control ensures intuitive gameplay.  
- **Doors & Keys System:** Collect color-coded keys and unlock corresponding doors. The wrong key or no key ends the run.  
- **Zombies:** Randomly spawned obstacles that instantly end the game on collision.  
- **Health & Revival:** Earn +1 health for every 5 doors opened. Spend 3 health points to revive after death.  
- **HUD & Scoring:** Track distance, doors opened, survivors saved, and current health in real-time.  

---

## Advanced Topics: Random Generation & UX  

### **1. Random Generation (Algorithmic Fairness & Replayability)**  
Random generation was implemented to ensure **each playthrough feels unique** and unpredictable.  
- Keys, doors, and zombies are all placed procedurally within three lanes using randomized logic.  
- Placement algorithms guarantee **fair and solvable runs**, avoiding impossible scenarios (e.g., blocked paths or missing key-door pairs).  
- This demonstrates understanding of **advanced programming concepts** such as **state checking**, **object mapping**, and **algorithmic fairness**.  
- From a player’s perspective, it ensures lasting engagement, no two runs are ever the same.  

### **2. User Experience (UX Design Principles)**  
A major design focus was on **clarity, responsiveness, and feedback**.  
- The **HUD** provides immediate visual feedback on keys, health, and score, ensuring players always understand their status.  
- **Color-coded doors and keys** enhance recognition and reduce cognitive load during fast gameplay.  
- **Player and door animations** and **background design** reinforce immersion without cluttering the screen.  
- Overall, UX principles guide the game’s structure, players must feel in control, informed, and motivated.  

---

## Progression & Difficulty  
- The game’s **speed multiplier** increases steadily (×2 → ×5) over time, requiring faster reactions.  
- **New door colors** are introduced at timed intervals to increase complexity.  
- These mechanics create a balanced difficulty curve that evolves with the player’s performance.  

---

## Mini Games  
To expand gameplay variety and demonstrate modular programming, several themed **mini-games** are included:  
- **Tic Tac Toe**  
- **Tap Match Fruits**  
- **Connect Wires**  
- **Button Sequence**  

Each mini-game connects to the survival theme and explores different logic and interaction mechanics.  

---

## Visual & Audio Design  
- Post-apocalyptic backgrounds with muted tones and fog effects.  
- Color-coded keys and doors maintain clarity and contrast.  
- Sprite animations and eerie background enhance immersion and atmosphere.  

---

## 👥 Developers  
| Name |
|------|
| **Arshiya Shaik** |
| **Priyanshu Guha** |

---

## About  
This project was created as part of the **Programming (2IP90)** course at **TU/e** and is intended solely for **educational and non-commercial purposes**.  

---
