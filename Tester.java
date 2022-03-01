import static java.lang.System.*;
import java.io.File;

public class Tester {
	public static void main(String[] args) {
		//File f = new File(args[0]);
		Jsc game = new Jsc(new File("defaults.bowl"));
		//System.out.println(game);

		game.addValue(1);
		game.addValue(4);
		game.addValue("10");
		game.addValue("X");
		game.addValue("c");
		game.addValue(9);
		game.addValue("/");
		game.addValue("/");
		System.out.println(game); // 1|4 , 10|0 , 10|0 , 9|1 , 10|0
		int[] a1 = {11,1};
		int[] a2 = {6};
		int[] a3 = {1,1,1};
		game.replaceFrame(1, a1);
		game.replaceFrame(2, a2);
		game.replaceFrame(4, a3);
		System.out.println(game); // 1|4 , 10|0 , 6|0 , 9|1 , 1|1
		System.out.println(game.isValidFrame(10)); // false 
		System.out.println(game.isValidFrame(-1));
		System.out.println(game.isValidBall(10, 0));
		System.out.println(game.isValidBall(3, 2));
		System.out.println(game.isValidBall(4,1)); // true
		game.replaceValue(0, 0, 4);
		game.replaceValue(2, 1, 6);
		System.out.println(game);

		/* TEST CODE FOR addValue()
		int[] frame = {5, 5, -1, -1};
		int pinsThisFrame = 0;
		for(int i = 0; i < frame.length; i++) { // find total pins this frame
			if(frame[i] >= 0) { pinsThisFrame += frame[i]; }
		}
		if(pinsThisFrame >= 10) { // replce all negative values w/ 0 if strike/spare
			for(int i = 0; i < frame.length; i++) {
				if(frame[i] < 0) { frame[i] = 0; }
			}
		}
		System.out.println(java.util.Arrays.toString(frame));
		*/
	}
}
