package byog.Core;

import byog.TileEngine.Tileset;
import byog.TileEngine.TETile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class WorldGenerator implements Serializable {

    private Player player;
    TETile[][] world;
    private ArrayList<Room> rooms;
    private Random random;

    public WorldGenerator(int seed) {
        random = new Random(seed);
        world = new TETile[Game.WIDTH][Game.HEIGHT];
        rooms = new ArrayList<>();
        fillWorld();
    }

    private class Position {
        int x;
        int y;

        private Position(int xCor, int yCor) {
            x = xCor;
            y = yCor;
        }

        public boolean equals(Position p) {
            return (this.x == p.x && this.y == p.y);
        }
    }


    private class Room {
        Position p;
        int width;
        int height;
        ArrayList<Position> tiles;

        private Room(Position p, int w, int h) {
            this.p = p;
            width = w;
            height = h;
            tiles = new ArrayList<>();
            fillTiles();
        }

        private void fillTiles() {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Position tp = new Position(p.x + i, p.y + j);
                    tiles.add(tp);
                }
            }
        }
    }

    private void generator() {
        int numRooms = Game.WIDTH * Game.HEIGHT / 250;
        int invalid = 0;

        while (numRooms > 0 && invalid < 35) {
            int x = random.nextInt(Game.WIDTH);
            int y = random.nextInt(Game.HEIGHT);
            int w = random.nextInt(Game.WIDTH / 6) + 2;
            int h = random.nextInt(Game.HEIGHT / 3) + 2;

            Position rp = new Position(x, y);
            Room newR = new Room(rp, w, h);
            if (roomIsValid(newR)) {
                rooms.add(newR);
                numRooms -= 1;
            } else {
                invalid += 1;
            }
        }

        for (Room r : rooms) {
            for (Position p : r.tiles) {
                world[p.x][p.y] = Tileset.FLOOR;
            }
        }

    }


    private boolean roomIsValid(Room room) {
        if (room.p.x > Game.WIDTH - room.width - 2) {
            return false;
        }
        if (room.p.y > Game.HEIGHT - room.height - 2) {
            return false;
        }
        if (room.p.x < 2) {
            return false;
        }
        if (room.p.y < 2) {
            return false;
        }
        for (Room otherR : rooms) {
            for (Position p : room.tiles) {
                for (Position otherP : otherR.tiles) {
                    if (p.equals(otherP)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    private void connect(Room rm1, Room rm2) {
        ArrayList<Position> hallways = new ArrayList<>();

        Position hall1 = new Position(rm1.p.x + rm1.width / 2, rm1.p.y + rm1.height / 2);
        Position hall2 = new Position(rm2.p.x + rm2.width / 2, rm2.p.y + rm2.height / 2);

        if (hall1.x < hall2.x) {
            while (hall1.x < hall2.x) {
                hallways.add(hall1);
                hall1 = new Position(hall1.x + 1, hall1.y);
            }
            if (hall1.y < hall2.y) {
                while (hall1.y < hall2.y) {
                    hallways.add(hall1);
                    hall1 = new Position(hall1.x, hall1.y + 1);
                }
            }
            if (hall1.y > hall2.y) {
                while (hall1.y > hall2.y) {
                    hallways.add(hall1);
                    hall1 = new Position(hall1.x, hall1.y - 1);
                }
            }
        }

        if (hall1.x > hall2.x) {
            while (hall1.x > hall2.x) {
                hallways.add(hall1);
                hall1 = new Position(hall1.x - 1, hall1.y);
            }
            if (hall1.y < hall2.y) {
                while (hall1.y < hall2.y) {
                    hallways.add(hall1);
                    hall1 = new Position(hall1.x, hall1.y + 1);
                }
            }
            if (hall1.y > hall2.y) {
                while (hall1.y > hall2.y) {
                    hallways.add(hall1);
                    hall1 = new Position(hall1.x, hall1.y - 1);
                }
            }
        }

        if (hall1.x == hall2.x) {
            if (hall1.y < hall2.y) {
                while (hall1.y < hall2.y) {
                    hallways.add(hall1);
                    hall1 = new Position(hall1.x, hall1.y + 1);
                }
            }
            if (hall1.y > hall2.y) {
                while (hall1.y > hall2.y) {
                    hallways.add(hall1);
                    hall1 = new Position(hall1.x, hall1.y - 1);
                }
            }
        }

        for (Position p : hallways) {
            world[p.x][p.y] = Tileset.FLOOR;
        }

    }

    private void pairing() {
        int i = 0;
        int j = 0;
        int oldi = 0;
        int oldj = 0;
        int h = 0;
        while (h < rooms.size() * 6) {
            i = random.nextInt(rooms.size() - 1);
            j = random.nextInt(rooms.size() - 1);
            if ((i != oldi && j != oldj)) {
                Room rm1 = rooms.get(i);
                Room rm2 = rooms.get(j);
                connect(rm1, rm2);
                h += 1;
                oldi = i;
                oldj = j;
            }
        }
    }

    public void createWall() {
        ArrayList<Position> walls = new ArrayList<>();

        for (int i = 0; i < Game.WIDTH; i++) {
            for (int j = 0; j < Game.HEIGHT; j++) {
                if (world[i][j] == Tileset.FLOOR) {
                    if (world[i + 1][j] == Tileset.NOTHING) {
                        Position wallp = new Position(i + 1, j);
                        walls.add(wallp);
                    }
                    if (world[i - 1][j] == Tileset.NOTHING) {
                        Position wallp = new Position(i - 1, j);
                        walls.add(wallp);
                    }
                    if (world[i][j + 1] == Tileset.NOTHING) {
                        Position wallp = new Position(i, j + 1);
                        walls.add(wallp);
                    }
                    if (world[i][j - 1] == Tileset.NOTHING) {
                        Position wallp = new Position(i, j - 1);
                        walls.add(wallp);
                    }
                }
            }
        }

        for (Position p : walls) {
            world[p.x][p.y] = Tileset.WALL;
        }
        int h = random.nextInt(walls.size() - 1);
        Position z = walls.get(h);
        world[z.x][z.y] = Tileset.LOCKED_DOOR;
    }

    private void generatePlayer() {
        int h = random.nextInt(rooms.size() - 1);
        Room z = rooms.get(h);
        int pos = random.nextInt(z.tiles.size() - 1);
        Position r = z.tiles.get(pos);
        world[r.x][r.y] = Tileset.PLAYER;
        player = new Player(r.x, r.y, world);
    }


    public TETile[][] createWorld() {
        generator();
        pairing();
        createWall();
        generatePlayer();
        return world;
    }

    public Player createPlayer() {
        return player;
    }

    private void fillWorld() {
        for (int i = 0; i < Game.WIDTH; i++) {
            for (int j = 0; j < Game.HEIGHT; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
    }

}
