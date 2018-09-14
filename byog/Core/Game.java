package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.awt.Color;
import java.awt.Font;
//import java.awt.*;


public class Game {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 87;
    public static final int HEIGHT = 43;
    private String name = "";
    private Player player;
    private String commands = "";
    private String seedValue;
    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */



    private int seeds(String s) {
        return ((int) Long.parseLong(s.substring(1, s.indexOf("s"))));
    }

    private void initialize() {
        StdDraw.setCanvasSize();
        Font font = new Font("Arial", Font.PLAIN, 20);
        StdDraw.setFont(font);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.enableDoubleBuffering();
    }

    private void setMenu() {
        StdDraw.text(0.5, 0.8, "61b game");
        StdDraw.text(0.5, 0.45, "N: New Game");
        StdDraw.text(0.5, 0.39, "L: Load Game");
        StdDraw.text(0.5, 0.33, "Q: Quit Game");
        StdDraw.text(0.5, 0.27, "C: Character Name");
        StdDraw.show();
    }

    private void showSeed(String seed) {
        StdDraw.setFont(new Font("Arial", Font.PLAIN, 30));
        StdDraw.clear(Color.BLACK);
        StdDraw.text(0.5, 0.7, "Enter Seed; End with 's'");
        if (seed.length() > 10) {
            StdDraw.setFont(new Font("Arial", Font.PLAIN, 20));
        }
        StdDraw.text(0.5, 0.3, seed);
        StdDraw.show();
    }

    private String getSeed() {
        String seed = "n";
        showSeed(seed);
        char seedInput = ' ';
        while (seedInput != 's' && seedInput != 'S') {
            if (StdDraw.hasNextKeyTyped()) {
                seedInput = StdDraw.nextKeyTyped();
                if (seedInput >= 48 && seedInput <= 57 || seedInput == 's' || seedInput == 'S') {
                    seed += seedInput;
                }
            }
            showSeed(seed);
        }
        seedValue = seed;
        return seed;
    }

    private void showName() {
        StdDraw.setFont(new Font("Arial", Font.PLAIN, 30));
        StdDraw.clear(Color.BLACK);
        StdDraw.text(0.5, 0.7, "Enter name; End with '.'");
        StdDraw.text(0.5, 0.3, name);
        StdDraw.show();
    }

    private void getName() {
        char nameInput = ' ';
        showName();
        while (nameInput != '.') {
            if (StdDraw.hasNextKeyTyped()) {
                nameInput = StdDraw.nextKeyTyped();
                if (nameInput != '.') {
                    name += nameInput;
                }
            }
            showName();
        }
    }

    private void showHUD(TETile[][] world) {
        StdDraw.setPenColor(Color.WHITE);
        double x = StdDraw.mouseX();
        double y = StdDraw.mouseY();

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (i == (int) x && j == (int) y) {
                    StdDraw.textLeft(1, 43, world[i][j].description());
                }
            }
        }
        StdDraw.textRight(85, 43, name);
        StdDraw.show();
    }

    private void gameLoop(TETile[][] world) {
        TERenderer tr = new TERenderer();
        tr.initialize(WIDTH, HEIGHT + 1);
        tr.renderFrame(world);
        String inputs = "";

        while (!inputs.toLowerCase().contains(":q")) {
            if (inputs.toLowerCase().contains(":r")) {
                menuLoop();
                break;
            }
            tr.renderFrame(world);
            showHUD(world);
            if (StdDraw.hasNextKeyTyped()) {
                char d = StdDraw.nextKeyTyped();
                if (d != 'q' && d != 'Q' && d != ':') {
                    commands += d;
                }
                player.move(d);
                inputs += d;
            }
        }
        saveworld(commands, true);
        commands = "";
        System.exit(0);
    }

    private void menuLoop() {
        initialize();
        setMenu();
        boolean flag = true;
        char input = ' ';

        while (flag) {
            if (StdDraw.hasNextKeyTyped()) {
                input = StdDraw.nextKeyTyped();
            }
            if (input == 'n' || input == 'N') {
                String seed = getSeed();
                TETile[][] world = playWithInputString(seed);
                gameLoop(world);
                flag = false;
            } else if (input == 'c' || input == 'C') {
                getName();
                menuLoop();
                break;
            } else if (input == 'l' || input == 'L') {
                SGame game = loadworld();
                this.name = game.getName();
                this.seedValue = game.getSeedValue();
                this.commands = game.getCommands();
                TETile[][] world = playWithInputString(this.seedValue + this.commands);
                gameLoop(world);
                flag = false;
            } else if (input == 'q' || input == 'Q') {
                System.exit(0);
                flag = false;
            }
        }

    }

    public void playWithKeyboard() {
        menuLoop();
    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        if (input.toLowerCase().charAt(0) == 'l') {
            SGame game = loadworld();
            this.name = game.getName();
            this.seedValue = game.getSeedValue();
            this.commands = game.getCommands();
            input = seedValue + commands + input.substring(input.indexOf('l'));
        } else {
            this.seedValue = input.substring(0, input.indexOf('s') + 1);
        }
        int seed = seeds(input);
        String movements = input.substring(input.indexOf('s') + 1);
        WorldGenerator rg = new WorldGenerator(seed);
        TETile[][] finalWorldFrame = rg.createWorld();
        Player temp = rg.createPlayer();
        this.player = new Player(temp.xPos(), temp.yPos(), finalWorldFrame);
        for (int i = 0; i < movements.length(); i++) {
            char d = movements.charAt(i);
            player.move(d);
        }
        saveworld(movements, true);
        return finalWorldFrame;
    }


    private void saveworld(String c, boolean b) {
        try {
            FileOutputStream fo = new FileOutputStream("world.txt");
            ObjectOutputStream os = new ObjectOutputStream(fo);
            os.writeObject(new SGame(c, b, seedValue, name));
            os.close();
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SGame loadworld() {
        try {
            FileInputStream fi = new FileInputStream("world.txt");
            ObjectInputStream oi = new ObjectInputStream(fi);
            SGame g = (SGame) oi.readObject();
            oi.close();
            fi.close();
            return g;
        } catch (IOException e) {
            e.printStackTrace();
            return new SGame("N0S", false, seedValue, name);
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found");
            e.printStackTrace();
            return new SGame("N0S", false, seedValue, name);
        }
    }
}
