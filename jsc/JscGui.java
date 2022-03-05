/*
 * JscGui: A simple but powerful GUI for the Jsc system.
 */
package jsc;

import static java.lang.System.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
//import java.lang.InterruptedException;

public class JscGui {
	public static ArrayList<Jsc> player_games = new ArrayList<Jsc>();
	public static ArrayList<String> player_names = new ArrayList<String>();
	public static File jsc_conf = new File("defaults.bowl");
	public static boolean quitGame = false;
	public static boolean exit = false;
	public static final String VERSION = "0.1";

	public static void main(String[] args) {
        Scanner scan = new Scanner(in);
		File ascii = new File("resources/asciiart.txt");

		cls();
		out.println(printFile(ascii));
		out.println("Java SCoring system by allison\n");
		initPlayer();
		
		/* game loop */
		String input;
		while(!quitGame && !exit) {
			cls();
			int currentPlayer = 0; // replace later with dynamic players
			out.println(player_names.get(currentPlayer) + ":");
			printGame(currentPlayer);
			out.println("enter command: ('h' for help)");

			input = scan.nextLine();
			parsePlayerInput(input);
		}
		if(exit) { return; }
	}

	
	/* ################################################ */
	/* ########## Session management methods ########## */
	/* ################################################ */

	/* displays a main menu. */
	public static void mainMenu() {
	}

	/* displays a prompt to enter player names and select configs. */
	public static void initPlayer() {
		Scanner scan = new Scanner(in);
		out.print("Enter player name: ");
		player_names.add(scan.nextLine());
		player_games.add(new Jsc());
	}

	/* ############################################# */
	/* ########## Display Utility Methods ########## */
	/* ############################################# */

	/* clears the screen. */
	public static void cls() {
		try {
			if(System.getProperty("os.name").contains("Windows")) {
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			}
			else {
				System.out.print("\033\143");
			}
		}
		catch(IOException | InterruptedException e) {
			System.out.println("JscGui:cls: Display error: cannot clear screen");
		}
	}
	
	/* returns a file's contents with newline characters. */
	public static String printFile(File file) {
		String outStr = "";
		try {
			Scanner scan = new Scanner(file);
			while(scan.hasNextLine()) {
				outStr += scan.nextLine() + "\n";
			}
		}
		catch(FileNotFoundException e) {
			out.println("Could not open file: " + file);
			e.printStackTrace();
		}
		return outStr;
	}

	/* ########################################## */
	/* ########## Game Display Methods ########## */
	/* ########################################## */

	public static void printGame(int ndx) {
		// |1    |2    |
		// | 1 1 | 2 / |
		String topLine = "";
		String bottomLine = "";

		for(int i = 0; i < player_games.get(ndx).max_frames; i++) {
			int[] frame = player_games.get(ndx).getFrame(i);
			String append = "";
			append += "| ";
			for(int val : frame) {
				if(val >= 0) {
					append += val + " ";
				}
				else {
					append += "_ ";
				}
			}
			bottomLine += append;
			topLine += "|" + String.format("%" + (append.length() - 1) + "s", i + 1); 
		}
		topLine += "|";
		bottomLine += "|";
		out.println(topLine + "\n" + bottomLine);
	}

	/* ###################################### */
	/* ########## Gameplay Methods ########## */
	/* ###################################### */

	public static String parsePlayerInput(String input) {
		player_games.get(0).addValue(input);
		return "";
	}
}
