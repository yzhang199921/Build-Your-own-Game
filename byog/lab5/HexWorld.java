package byog.lab5;
import org.junit.Test;
import static org.junit.Assert.*;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import byog.Core.RandomUtils;


import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */


public class HexWorld {
    static Random  RANDOM = new Random();

    private static class Position {
        int x;
        int y;

        private Position(int xCor, int yCor){
            x = xCor;
            y = yCor;
        }
    }

    public static int hexRowWidth(int s, int i) {
        int effectiveI = i;
        if (i >= s) {
            effectiveI = 2 * s - 1 - effectiveI;
        }

        return s + 2 * effectiveI;
    }


    public static int hexRowOffset(int s, int i) {
        int effectiveI = i;
        if (i >= s) {
            effectiveI = 2 * s - 1 - effectiveI;
        }
        return -effectiveI;
    }

    public static void addRow(TETile[][] world, Position p, int width, TETile t) {
        for (int xi = 0; xi < width; xi += 1) {
            int xCoord = p.x + xi;
            int yCoord = p.y;
            world[xCoord][yCoord] = TETile.colorVariant(t, 32, 32, 32, RANDOM);
        }
    }


    public static void addHexagon(TETile[][] world, Position p, int s, TETile t) {

        if (s < 2) {
            throw new IllegalArgumentException("Hexagon must be at least size 2.");
        }

        // hexagons have 2*s rows. this code iterates up from the bottom row,
        // which we call row 0.
        for (int yi = 0; yi < 2 * s; yi += 1) {
            int thisRowY = p.y + yi;

            int xRowStart = p.x + hexRowOffset(s, yi);
            Position rowStartP = new Position(xRowStart, thisRowY);

            int rowWidth = hexRowWidth(s, yi);

            addRow(world, rowStartP, rowWidth, t);
        }
    }

    public static Position bottomright(Position p, int s) {
       Position newp = new Position(p.x + (s * 2) - 1, p.y - s);
       return newp;
    }

    public static Position topright(Position p, int s) {
        Position newp = new Position(p.x + (s * 2) - 1, p.y + s);
        return newp;
    }

    public static void addColumn(int num, TETile[][] world, Position start, int s) {
        while (num > 0) {
            Position p = start;
            addHexagon(world, p, s, Tileset.FLOOR);
            start.y = start.y + s * 2;
            num -= 1;
        }
    }

    public static void Tesselation(TETile[][] world, int size, Position p) {
        Position second = bottomright(p, size);
        Position third = bottomright(second, size);
        Position fourth = topright(third, size);
        Position fifth = topright(fourth, size);

        addColumn(3, world, p, size);
        addColumn(4, world, second, size);
        addColumn(5, world, third, size);
        addColumn(4, world, fourth, size);
        addColumn(3, world, fifth, size);

    }

    public static void main(String[] arg) {
        Position p = new Position(10, 20);
        TETile[][] world = new TETile[70][70];
        for (int x = 0; x < 70; x += 1) {
            for (int y = 0; y < 70; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        TERenderer ter = new TERenderer();
        ter.initialize(70, 70);
        Tesselation(world, 3, p);
        ter.renderFrame(world);
    }
}
