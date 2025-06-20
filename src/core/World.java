package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import java.util.*;
import java.util.List;

public class World {

    ///  VARIABLES /////
    static TETile floor = Tileset.GRASS;
    static TETile wall = Tileset.WALL;
    static TETile skinA;
    static avatar ourAvatar;
    static long seed;
    static Coordinate position;
    private static int screenHeight = 55;
    private static final int worldHeight = 50;
    private static final int worldWidth = 60;
    static HashSet<Coordinate> allFloorTiles = new HashSet<>();

    static ArrayList<Coordinate> worldList = new ArrayList<>();
    static List<Coordinate> actualFinalPath = new ArrayList<>();
    static TETile[][] world = new TETile[worldWidth][screenHeight];
    static TETile[][] world2 = new TETile[worldWidth][screenHeight];
    static TETile[][] showWorld = world;
    static int roomCounter = 0;

    public World(long seed, Coordinate position, TETile skin) {
        World.seed = seed;
        World.position = position;
        skinA = skin;
    }

    public static class Coordinate implements Comparable<Coordinate> {
        int x;
        int y;

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Coordinate) {
                Coordinate other = (Coordinate) o;
                return this.x == other.x && this.y == other.y;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }


        @Override
        public int compareTo(Coordinate o) {
            return this.x - o.x;
        }
    }

    public static class avatar {
        Coordinate c;
        TETile skin;

        public avatar() {

            Random rand = new Random();
            if (World.position == null){
                this.c = worldList.get(rand.nextInt(worldList.size()));
            } else{
                this.c = World.position;
            }

            skin = World.skinA;

            world[this.c.x][this.c.y] = skin;
        }

        public void move(String movement) {
            if (movement.equals("up")  && (world[this.c.x][this.c.y + 1] != wall)) {
                world[this.c.x][this.c.y + 1] = ourAvatar.skin;
                world[this.c.x][this.c.y] = floor;
                world2[this.c.x][this.c.y + 1] = ourAvatar.skin;
                world2[this.c.x][this.c.y] = floor;
                this.c.y = this.c.y+1;

            } else if (movement.equals("down")  && (world[this.c.x][this.c.y - 1] != wall)){
                world[this.c.x][this.c.y - 1] = ourAvatar.skin;
                world[this.c.x][this.c.y] = floor;
                world2[this.c.x][this.c.y - 1] = ourAvatar.skin;
                world2[this.c.x][this.c.y] = floor;
                this.c.y = this.c.y -1;
            } else if (movement.equals("left") && (world[this.c.x -1][this.c.y] != wall)) {
                world[this.c.x - 1][this.c.y] = ourAvatar.skin;
                world[this.c.x][this.c.y] = floor;
                world2[this.c.x - 1][this.c.y] = ourAvatar.skin;
                world2[this.c.x][this.c.y] = floor;
                this.c.x = this.c.x-1;
            } else if (movement.equals("right") && (world[this.c.x+1][this.c.y] != wall)) {
                world[this.c.x + 1][this.c.y] = ourAvatar.skin;
                world[this.c.x][this.c.y] = floor;
                world2[this.c.x + 1][this.c.y] = ourAvatar.skin;
                world2[this.c.x][this.c.y] = floor;
                this.c.x = this.c.x+1;
            }
        }
    }




    private static void addRandomSquares(TETile[][] world, Random rand) {
        int numRooms = RandomUtils.uniform(rand, 13, 25);
        while (roomCounter != numRooms) {
            int x = RandomUtils.uniform(rand, 4, 8);
            int y = RandomUtils.uniform(rand, 4, 8);


            int w = RandomUtils.uniform(rand, 3, worldWidth - 3);
            int h = RandomUtils.uniform(rand, 3, worldHeight - 3);

            drawSquare(world, w, h, x, y, floor);
        }
    }
    private static void drawSquare(TETile[][] world, int startX, int startY, int sizex, int sizey, TETile tile) {

        if (startY + sizey >= worldHeight - 1|| startX + sizex >= worldWidth - 1) {
            return;
        }
        roomCounter += 1;
        int xMiddle = ((sizex)-1)/2 + startX;
        int yMiddle = ((sizey)-1)/2 + startY;
        for (int x = startX; x < startX + sizex; x++) {
            for (int y = startY; y < startY + sizey; y++) {
                world[x][y] = tile;
                world[xMiddle][yMiddle] = skinA;
                worldList.add(new Coordinate(xMiddle,yMiddle));

            }

        }



    }
    public static void light() {
        int sight = 3;
        // black out world2
        for (int i = 0; i < worldWidth; i++) {
            for (int j = 0; j < screenHeight; j++)

            {
                world2[i][j] = Tileset.NOTHING;
            }
        }
        for (int x = ourAvatar.c.x - sight; x <= ourAvatar.c.x + sight; x++) {
            for (int y = ourAvatar.c.y - sight; y <= ourAvatar.c.y + sight; y++) {


                if (x < 0 || y < 0 || x >= worldWidth || y >= worldHeight) {
                    continue;
                }


                if (visible(ourAvatar.c.x, ourAvatar.c.y, x, y)) {
                    world2[x][y] = world[x][y];
                }
            }
        }

    }



    private static boolean visible(int ax, int ay, int tx, int ty) {
        HashSet<Coordinate> visited = new HashSet<>();
        HashMap<Coordinate, Coordinate> path = new HashMap<>();
        Coordinate start = new Coordinate(ax, ay);
        Coordinate end = new Coordinate(tx, ty);


        bfs(start, end, visited, path);


        List<Coordinate> visblepath = reconstructPath(path, end);

        int checker = 0;
        for (Coordinate c : visblepath) {
            if (world[c.x][c.y] == wall){
                checker++;
            }
        }

        return checker == 0 || world[tx][ty] == wall;


    }




    public static void main(String[] args) {

        TERenderer ter = new TERenderer();

        ter.initialize(worldWidth, screenHeight);
        // create a world on NOTHING

        for (int i = 0; i < worldWidth; i ++) {
            for (int j = 0; j < screenHeight; j++) {
                world[i][j]  = Tileset.NOTHING;
            }
        }
        //create Random r variable so all tiles are the same position every time you write the same seed number
        Random r = new Random(seed);


        addRandomSquares(world, r);
        Collections.sort(worldList);
        for (int i = 0; i < worldList.size()-1; i++) {
            actualFinalPath.addAll(traverse(worldList.get(i), worldList.get(i+1)));
        }
        colorPath(actualFinalPath);
        ourAvatar = new avatar();
        spawnAkumas(world, r);

        ter.renderFrame(showWorld);

        char m;// Variable for saving the most recent movement char typed by the user.

        boolean light = true;

        while (true) {


            StdDraw.clear();
            ter.drawTiles(showWorld);

            int currX = (int) StdDraw.mouseX();
            int currY = (int) StdDraw.mouseY();
            String type = "";
            if (!(currX > worldWidth || currX < 0 || currY > worldHeight || currY < 0)) {
                type = world[currX][currY].description();
            }
            StdDraw.setPenColor(StdDraw.GREEN);
            //StdDraw.filledRectangle((double) worldWidth /2, 5, 60, 52.1);
            StdDraw.text(4, screenHeight - 0.6, "Type: " + type);
            StdDraw.text(worldWidth - 10, screenHeight - 0.6, "Press l for line of sight feature");
            StdDraw.show();



            while (StdDraw.hasNextKeyTyped()) {
                // nextKeyTyped returns the next key to process.
                // Always check hasNextKeyTyped before calling nextKeyTyped.
                // If you call nextKeyTyped and there's no key to process, code will crash!
                m = StdDraw.nextKeyTyped();
                m = Character.toLowerCase(m);
                System.out.print(m);
                if (!light) {
                    light();
                }
                switch (m) {

                    case 'w':
                        ourAvatar.move("up");
                        StdDraw.clear();

                        ter.drawTiles(showWorld);
                        break;

                    case 'a':
                        ourAvatar.move("left");
                        StdDraw.clear();

                        ter.drawTiles(showWorld);
                        break;

                    case 's':
                        ourAvatar.move("down");
                        StdDraw.clear();

                        ter.drawTiles(showWorld);
                        break;
                    case 'd':
                        ourAvatar.move("right");
                        StdDraw.clear();

                        ter.drawTiles(showWorld);
                        break;
                    case 'l':
                        if (light) {
                            light = false;
                            light();
                            showWorld = world2;
                            StdDraw.clear();
                            ter.drawTiles(showWorld);
                            break;
                        }
                        else {
                            showWorld = world;
                            light = true;
                            StdDraw.clear();
                            ter.drawTiles(showWorld);

                            break;
                        }
                    case ':':
                        while (true){
                        if (StdDraw.hasNextKeyTyped()) {
                            m = StdDraw.nextKeyTyped();
                            m = Character.toLowerCase(m);

                            if (m == 'q') {
                                System.out.print(skinA.description());
                                try (PrintWriter out = new PrintWriter("saveworld.csv")) {
                                    out.printf("%d,%s,%s,%s%n", seed, ourAvatar.c.x, ourAvatar.c.y, skinA.description());
                                } catch (FileNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                                System.exit(0);
                            } else{
                                break;
                            }
                        } }
                        System.out.print(m);



                }
            }
        }

    }
    public static void getFloorTiles(TETile[][] someWorld) {
        for (int i = 0; i < someWorld.length; i++) {
            for (int j = 0; j < someWorld[0].length; j++) {
                if (someWorld[i][j] == floor) {
                    allFloorTiles.add(new Coordinate(i, j));
                }
            }
        }
    }

    public static void spawnAkumas(TETile[][] someWorld, Random rand){
        int numAkumas = RandomUtils.uniform(rand, 13, 20);
        getFloorTiles(someWorld);
        List<Coordinate> floorList = new ArrayList<>(allFloorTiles);
        Set<Coordinate> usedPositions = new HashSet<>();

        for (int i = 0; i < numAkumas && usedPositions.size() < floorList.size(); i++) {
            Coordinate pos;
            do {
                int index = RandomUtils.uniform(rand, floorList.size());
                pos = floorList.get(index);
            } while (usedPositions.contains(pos)); // Avoid duplicates

            usedPositions.add(pos);
            someWorld[pos.x][pos.y] = Tileset.akuma; // Or whatever tile you use for Akumas
        }

    }

    public static HashSet<Coordinate> neighbors(Coordinate m) {
        HashSet<Coordinate> n = new HashSet<>();
        if (!(m.x > worldWidth)){
            n.add(new Coordinate(m.x+1, m.y));}
        if (!(m.x - 1 < 0)){
            n.add(new Coordinate(m.x-1, m.y)); }
        if (!(m.y > worldHeight)){
            n.add(new Coordinate(m.x, m.y+1));}
        if (!(m.y - 1 < 0)) {
            n.add(new Coordinate(m.x, m.y-1));}
        return n;
    }
    public static List<Coordinate> traverse(Coordinate start, Coordinate end) {
        HashSet<Coordinate> visited = new HashSet<>();
        HashMap<Coordinate, Coordinate> path = new HashMap<>();
        if (!start.equals(end)) {
            bfs(start, end, visited, path);
        }
        return reconstructPath(path, end);

    }
    public static void bfs(Coordinate start, Coordinate end,  HashSet<Coordinate> visited, HashMap<Coordinate, Coordinate> path) {
        visited.add(start);
        Queue<Coordinate> fringe = new LinkedList();
        fringe.add(start);
        while (!fringe.isEmpty()) {
            Coordinate current = fringe.poll();
            if (current.equals(end)) {
                break;
            }

            for (Coordinate child : neighbors(current)) {
                if (!visited.contains(child)) {
                    fringe.add(child);
                    visited.add(child);
                    path.put(child, current);
                }
            }

        }

    }
    public static void colorPath(List <Coordinate> path) {
        for (Coordinate c : path) {
            world[c.x][c.y] = floor;
        }
        fillWalls();
    }

    public static void fillWalls(){
        for (int i = 1; i < worldWidth - 1; i++){
            for (int u = 1; u < worldHeight - 1; u++) {
                if ((world[i][u] == Tileset.NOTHING) && ((world[i + 1][u] == floor) || (world[i - 1][u] == floor) || (world[i][u + 1] == floor) || (world[i][u - 1] == floor))){
                    world[i][u] = wall;
                }
            }
        }
    }
    public static List<Coordinate> reconstructPath(Map<Coordinate, Coordinate> path, Coordinate end) {

        List<Coordinate> finalpath = new ArrayList<>();
        Coordinate current = end;
        while (current != null) {
            finalpath.add(0, current);
            Coordinate x = path.get(current);
            current = x;
        }
        return finalpath;
    }


}
