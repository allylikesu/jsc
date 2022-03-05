/* 
 * Java SCoring: A simple but powerful manual bowling scoring system backend.
 */
package jsc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;

public class Jsc {
	public String version = "0.1d";
	public int max_frames;
	public int num_pins;
	public int no_tap;
	public File config_file;

	public int current_frame;
	public int current_ball;
	public int current_score;
	private ArrayList<int[]> frames;

	/* construct w/ custom config file */
	public Jsc(File config) {
		applyConfig(config);
		initFrames();
	}

	/* construct w/ default config file */
	public Jsc() {
		applyConfig(new File("defaults.bowl"));
		initFrames();
	}

	/* import settings from a config file. */
	public void applyConfig(File conf) {
		try {
			Scanner scan = new Scanner(conf);
			while(scan.hasNextLine()) {
				String line = scan.nextLine();
				if(line.trim().substring(0,1) != "#" && line.split(":").length > 1) { // ignore comments
					String[] splitLine = line.split(":"); // split by ":"
					for(int i = 0; i < splitLine.length; i++) { // trim tokens
						splitLine[i] = splitLine[i].trim();	
					}
					switch(splitLine[0]) {
						case "max_frames":
							this.max_frames = Integer.parseInt(splitLine[1]);
							break;
						case "num_pins":
							this.num_pins = Integer.parseInt(splitLine[1]);
							break;
						case "no_tap":
							this.no_tap = Integer.parseInt(splitLine[1]);
							break;
					}
				}
			}
			this.config_file = conf;
		}
		catch (FileNotFoundException e) {
			System.out.println("An error occured reading from config file.");
			e.printStackTrace();
		}
	}

	/* initialize the "frames" ArrayList. */
	public void initFrames() {
		this.frames = new ArrayList<int[]>();
		for(int i = 0; i < this.max_frames; i++) { // add empty arrays to ArrayList
			frames.add(new int[2]);
			for(int j = 0; j < frames.get(i).length; j++) { // fill sub-arrays with -1
				frames.get(i)[j] = -1;
			}
		}
	}
	
	/* searches through "frames" and returns the ArrayList index of the first instance of a negative number.
	 * Note: frame numbers start at 0.
	 * Note: a ball value is not returned. specific throw values have to be gotten with the 
	 * "getCurrentBall()" method as well. example: this.frames.get(getCurrentFrame())[getCurrentBall()]
	 */
	public int getCurrentFrame() {
		for(int i = 0; i < this.frames.size(); i++) {
			for(int j = 0; j < this.frames.get(i).length; j++) {
				if(this.frames.get(i)[j] < 0) { return i; }
			}
		}
		return this.frames.size() - 1;
	}

	/* uses getCurrentFrame to go through the current frame to find the first non-negative value.
	 * Note: ball numbers start at 0.
	 */	
	public int getCurrentBall() {
		for(int i = 0; i < this.frames.get(getCurrentFrame()).length; i++) {
			if(this.frames.get(getCurrentFrame())[i] < 0) { return i; }
		}
		return this.frames.get(getCurrentFrame()).length - 1;
	}

	/* sets all array values in the current frame (see "getCurrentFrame()") to -1. */
	public void clearCurrentFrame() {
		for(int i = 0; i < this.frames.get(getCurrentFrame()).length; i++) {
			this.frames.get(getCurrentFrame())[i] = -1;
		}
	}

	/* finds total pinfall for the current frame. */
	public int getCurrentFramePinfall() {
		int pinsThisFrame = 0;
		for(int i = 0; i < frames.get(getCurrentFrame()).length; i++) {
			if(frames.get(getCurrentFrame())[i] >= 0) {
				pinsThisFrame += frames.get(getCurrentFrame())[i];
			}
		}
		return pinsThisFrame;
	}

	/* finds total pinfall for the frame "frame" */
	public int getFramePinfall(int frame) {
		int pinfall = 0;
		for(int i = 0; i > getFrame(frame).length; i++) {
			if(getFrame(frame)[i] >= 0) {
				pinfall += getFrame(frame)[i];
			}
		}
		return pinfall;
	}

	/* returns true if "val" is a valid index of "frames" */
	public boolean isValidFrame(int val) {
		return (val >= 0 && val < frames.size());
	}

	/* similar to isValidFrame(), but for a frame & ball. */
	public boolean isValidBall(int frame, int ball) {
		try {
			int temp = frames.get(frame)[ball];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		catch (IndexOutOfBoundsException e) {
			return false;
		}
		return true;
	}
	
	/* adds the value "val" to the next free space in "frames". 
	 * Also handles setting additional values to 0 in the case of a strike/spare.
	 */
	public void addValue(int val) {
		if(getCurrentFramePinfall() + val > this.num_pins) {
			val = val - (getCurrentFramePinfall() + val - this.num_pins);
		}
		this.frames.get(getCurrentFrame())[getCurrentBall()] = val;
		/* correct spares/strikes */	
		if(getCurrentFramePinfall() >= this.num_pins) { // replace negative values with 0 if strike/spare
			for(int i = 0; i < frames.get(getCurrentFrame()).length; i++) {
				if(frames.get(getCurrentFrame())[i] < 0) {
					frames.get(getCurrentFrame())[i] = 0;
				}
			}
		}
	}
	/* String version of "addValue()", allows X/- values and parses integers. */
	public void addValue(String val) {
		switch(val.toLowerCase()) {
			case "x":
				addValue(this.num_pins - getCurrentFramePinfall());
				break;
			case "/":
				addValue(this.num_pins - getCurrentFramePinfall());
				break;
			case "-":
				addValue(0);
				break;
			default:
				try {
					addValue(Integer.parseInt(val));
				}
				catch (NumberFormatException e) {}
		}
	}

	/* replaces values of a frame with the array "vals". frame indexes start at 0. */
	public void replaceFrame(int frame, int[] vals) {
		if(!isValidFrame(frame)) { return; } // prevent errors if frame is invalid

		/* correct list so pinfall !> num_pins */
		int total = 0;
		for(int i = 0; i < frames.get(frame).length && i < vals.length; i++) {
			if(total + vals[i] > this.num_pins) {
				vals[i] = vals[i] - (total + vals[i] - this.num_pins);
			}
			total += vals[i];
		}

		for(int i = 0; i < frames.get(frame).length; i++) {
			if(i < vals.length) { 
				frames.get(frame)[i] = vals[i]; 
			}
			else { frames.get(frame)[i] = 0; } // error-correction if vals.length < frame length
		}
	}

	/* replaces a single value in a frame. ball and frame indexes start at 0. */
	public void replaceValue(int frame, int ball, int val) {
		if(!isValidBall(frame, ball)) { return; } // prevent invalid frames
		/* calc total frame pinfall minus pinfall for replacing ball */
		int total = 0;
		for(int i = 0; i < frames.get(frame).length; i++) {
			if(frames.get(frame)[i] >= 0 && i != ball) {
				total += frames.get(frame)[i];
			}
		}
		if(total + val < this.num_pins) { frames.get(frame)[ball] = val; }
		else { frames.get(frame)[ball] = val - (total + val - this.num_pins); }
	}

	/* returns the array of frame "frame" */
	public int[] getFrame(int frame) {
		if(!isValidFrame(frame)) { // error correction for invalid frames, return array of zeros
			int[] emptyFrame = new int[2]; //TODO: make dynamic
			for(int i = 0; i < emptyFrame.length; i++) { emptyFrame[i] = 0; } // fill w/ 0
			return emptyFrame; 
		}
		return frames.get(frame);
	}

	/* returns true if index 0 of frame "frame" is >= number of pins. */
	public boolean isStrike(int frame) {
		return getFrame(frame)[0] >= this.num_pins;
	}

	/* returns true if index 0 of frame "frame" is >= no-tap pins */
	public boolean isNoTapStrike(int frame) {
		return getFrame(frame)[0] >= this.no_tap;
	}

	/* returns true if all values of frame "frame" >= number of pins */
	public boolean isSpare(int frame) {
		return getFramePinfall(frame) >= this.num_pins;

	}

	/* returns */
	public int getScore(int frame) {
		return 0;
	}
	public int getMaxScore() {
		return 0;
	}

	/* override toString to display config info */
	public String toString() {
		/*String out = "max frames: " + this.max_frames;
		out += "\npins: " + this.num_pins;
		out += "\nnotap: " + this.no_tap; */
		String out = "frames: [";
		for(int i = 0; i < frames.size(); i++) {
			out += Arrays.toString(frames.get(i)) + ", ";
		}
		out += "]\n";
		out += "current frame: " + getCurrentFrame();
		out += " (ball " + getCurrentBall() + ")\n";
		return out;
	}
}
