import java.util.Random;

/**
 * This class, used in conjunction with KeysOfSurvival.java controls when obstacles are spawned,
 * or when to speed up and add colors.
 */
public class Countdowns {
    Random random = new Random();

    // We have that KeysOfSurvival.MILLISECONDS_PER_FRAME = 20.
    // Every frame, the following countdowns decrease by the speed, which is set at 10 initially.
    // The calculated durations of the cooldowns are thus given.

    // However, the actual FPS of the game is somewhat slower, so the durations may be longer.

    // These are the durations set at the start of the game.
    final int keyCountdownStart = 300; // 0.6 seconds at speed 10
    final int zombieCountdownStart = 450 + 300 * random.nextInt(8); // 0.8 - 5.6 seconds at speed 10
    final int doorCountdownStart = 600; // 1.2 seconds at speed 10
    final int speedUpCountdownStart = 600; // 12 seconds
    final int newColorCountdownStart = 600; // 12 seconds
    
    // These are the replenished durations.
    int keyCountdownRestart = 600;
    int zombieCountdownRestart = 300 + 300 * random.nextInt(8);
    int doorCountdownRestart = 600;
    int speedUpCountdownRestart = 600;
    int newColorCountdownRestart = 600;
    
    // These are the actual countdowns.
    int keyCountdown = keyCountdownStart;
    int zombieCountdown = zombieCountdownStart;
    int doorCountdown = doorCountdownStart;
    int speedUpCountdown = speedUpCountdownStart;
    int newColorCountdown = newColorCountdownStart;

    /**
     * This function decreases the countdown for each obstacle type.
     * If countdown reaches 0, a number is returned that indicates which object needs to spawn.
     * 
     * The following list shows what happens in KeysOfSurvival when a number is returned.
     * 
     *  1 - Door spawns, 2 - Key spawns, 4 - Zombie spawns, 8 - Speed up 
     * 
     * @param speed is the speed in KeysOfSurvival, used to appropriately decrease the countdowns.
     * @return a byte if a countdown has reached 0.
     */
    public byte countdown(int speed) { // Not a constructor
        doorCountdown -= speed;
        keyCountdown -= speed;
        zombieCountdown -= speed;
        speedUpCountdown -= 1;
        newColorCountdown -= 1;

        if (doorCountdown < 1) {
            doorCountdown += doorCountdownRestart;
            return 1;
        }
        
        if (keyCountdown < 1) {
            keyCountdown += keyCountdownRestart;
            return 2;
        }

        if (zombieCountdown < 1) {
            zombieCountdown += zombieCountdownRestart;

            // Determine randomly when next zombie spawns
            zombieCountdownRestart = 300 + 300 * random.nextInt(8);
            return 4;
        }

        if (speedUpCountdown < 1) {
            speedUpCountdown += speedUpCountdownRestart;
            return 8;
        }

        if (newColorCountdown < 1) {
            newColorCountdown += speedUpCountdownRestart;
            return 16;
        }

        return 0;
    }
}
