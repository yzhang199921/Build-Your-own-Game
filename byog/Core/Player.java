package byog.Core;

import java.io.Serializable;
import byog.TileEngine.Tileset;
import byog.TileEngine.TETile;


public class Player implements Serializable {

    private int x;
    private int y;
    private TETile[][] world;

    public Player(int xCor, int yCor, TETile[][] world) {
        x = xCor;
        y = yCor;
        this.world = world;
    }

    public int xPos() {
        return x;
    }

    public int yPos() {
        return y;
    }

    public void move(char command) {
        if (command == 'w') {
            if (world[x][y + 1] != Tileset.WALL) {
                y += 1;
                world[x][y] = Tileset.PLAYER;
                world[x][y - 1] = Tileset.FLOOR;
            }
        } else if (command == 's') {
            if (world[x][y - 1] != Tileset.WALL) {
                y -= 1;
                world[x][y] = Tileset.PLAYER;
                world[x][y + 1] = Tileset.FLOOR;
            }
        } else if (command == 'a') {
            if (world[x - 1][y] != Tileset.WALL) {
                x -= 1;
                world[x][y] = Tileset.PLAYER;
                world[x + 1][y] = Tileset.FLOOR;
            }
        } else if (command == 'd') {
            if (world[x + 1][y] != Tileset.WALL) {
                x += 1;
                world[x][y] = Tileset.PLAYER;
                world[x - 1][y] = Tileset.FLOOR;
            }
        }
    }


}
