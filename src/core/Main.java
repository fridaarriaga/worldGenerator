
package core;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;
import java.awt.Font;

import tileengine.TETile;
import tileengine.Tileset;


public class Main {
    //FONT STYLES!!
    static Font videoGame = new Font("Arcade", Font.BOLD, 20);


    static World currentWorld;
    static TETile skin = Tileset.AVATAR;

    public static void menu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(videoGame);

        StdDraw.text(0.5, 0.7, "CS61B: BYOW");
        StdDraw.text(0.5, 0.6, "(N) New World");
        StdDraw.text(0.5, 0.5, "(L) Load World");
        StdDraw.text(0.5, 0.4, "(A) Change Avatar");
        StdDraw.text(0.5, 0.3, "(Q) Quit World");
    }

    public static void main(String[] args) {
        menu();

        char c; // Variable for saving the most recent character typed by the user.

        while (true) {

            // hasNextKeyTyped checks if the user has typed a key that we haven't processed.
            // This loop runs until all unprocessed keys are processed.
            // If there are no unprocessed keys, we go back to the outer infinite loop to wait for the next key.
            while (StdDraw.hasNextKeyTyped()) {
                // nextKeyTyped returns the next key to process.
                // Always check hasNextKeyTyped before calling nextKeyTyped.
                // If you cwall nextKeyTyped and there's no key to process, code will crash!
                c = StdDraw.nextKeyTyped();
                c = Character.toLowerCase(c);
                // Switch statements can be useful to replace long if-else statements!
                switch (c) {
                    case 'n':
                        StdDraw.clear(Color.BLACK);
                        StdDraw.text(0.5, 0.7, "Enter seed followed by s (e.g 667s) : ");
                        StringBuilder userInput = new StringBuilder();

                        while(true){
                            char d;

                            if(StdDraw.hasNextKeyTyped()){
                                d = StdDraw.nextKeyTyped();
                                switch (d) {
                                    case 's':
                                        currentWorld =  new World(Long.parseLong(userInput.toString()), null, skin);
                                        currentWorld.main(args);
                                    default:
                                        userInput.append(d);
                                        StdDraw.clear(Color.BLACK);
                                        StdDraw.setPenColor(Color.WHITE);
                                        StdDraw.text(0.5, 0.7, "Enter seed followed by s (e.g 667) : ");
                                        StdDraw.setPenColor(Color.YELLOW);
                                        StdDraw.text(0.5, 0.5, userInput + "   ");
                                }

                            }
                        }
                        //currentWorld = new World(Long.parseLong(userInput.toString()));

                    case 'l':

                        try (Scanner in = new Scanner(new File("saveworld.csv"))) {


                            while (in.hasNextLine()) {
                                String line = in.nextLine();         // e.g. "2,banana"


                                String[] parts = line.split(",");


                                int seed      = Integer.parseInt(parts[0]);
                                int cordx  = Integer.parseInt(parts[1]);
                                int cordy  = Integer.parseInt(parts[2]);
                                String savedSkin = parts[3];

                                //String savedSkin =
                                World.Coordinate presavedCoordinate = new World.Coordinate(cordx, cordy);
                                if(savedSkin.equals("villager")){
                                    skin = Tileset.villager;
                                } else if(savedSkin.equals("creeper")){
                                    skin = Tileset.creeper;
                                }

                                currentWorld =  new World(seed, presavedCoordinate, skin);
                                currentWorld.main(args);
                            }
                        } catch (FileNotFoundException e) {
                            System.out.print("bruhhhh");
                            throw new RuntimeException(e);
                        }

                        break;

                    case 'q':
                        System.exit(0); // Closes the game window and quits the game.
                        break;
                    case 'a':
                        char a;
                        StdDraw.clear(Color.BLACK);
                        StdDraw.text(0.5, 0.5, "Type # of selected avatar (e.g 1):");
                        StdDraw.text(0.5, 0.4, "(1) Creeper");
                        StdDraw.text(0.5, 0.3, "(2) Villager");
                        while (true) {
                            if (StdDraw.hasNextKeyTyped()) {
                                a = StdDraw.nextKeyTyped();
                                switch (a) {
                                    case '1':
                                        skin = Tileset.creeper;

                                        break;
                                    case '2':
                                        skin = Tileset.villager;

                                    default:
                                        break;
                                }
                                StdDraw.clear(Color.BLACK);
                                menu();
                                break;
                            }
                        }

                    default:
                        break;
                }


            }
            // Draws the world to the screen.
            // This is in the while(true) loop, because we want to frequently re-render the world.
        }

    }


}