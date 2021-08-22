package mouserecmacro;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MacroTimer {
	
	Robot mouseBotClicker;
	Timer timer;
	
	int maxLength = -1;
	int currentIndex = -1;
	
	ArrayList<String> mousePositionsArrayList;
	
	public MacroTimer(ArrayList<String> mPositionsArrayList, int macroTotalDurationTime)
	{
		// TODO Auto-generated constructor stub
		mousePositionsArrayList = mPositionsArrayList;
		
		try {
			this.mouseBotClicker = new Robot();
		} catch (AWTException ex) {
			System.out.println("ERROR ON BOT");
			Logger.getLogger(MacroTimer.class.getName()).log(Level.SEVERE, (String) null, ex);
		}
		
		int mousePositionsInterval = (int) Math.floor((double)macroTotalDurationTime / (double) mousePositionsArrayList.size());
		maxLength = mousePositionsArrayList.size();
		currentIndex = 0;
		
		timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				if (currentIndex < maxLength) {
					String mousePositionString = mousePositionsArrayList.get(currentIndex);
					
					String[] mousePositionStrings = mousePositionString.split(";");
					
					int mousePositionButton = Integer.valueOf(mousePositionStrings[0]);
					int mousePositionX = Integer.valueOf(mousePositionStrings[1]);
					int mousePositionY = Integer.valueOf(mousePositionStrings[2]);
					
					System.out.println("EXECUTION POSITION: " + mousePositionString);
					
					mouseBotClicker.mouseMove(mousePositionX, mousePositionY);
					
					switch (mousePositionButton) {
						case 2:
							mouseBotClicker.mousePress(8);
							mouseBotClicker.mouseRelease(8);
						break;
						case 3:
							mouseBotClicker.mousePress(4);
							mouseBotClicker.mouseRelease(4);
						break;
						default:
							mouseBotClicker.mousePress(16);
							mouseBotClicker.mouseRelease(16);
						break;
					}
					
					currentIndex++;
					if (currentIndex >= maxLength) {
						finished();
					}
				} else {
					finished();
				}
			}
			
		};
		
		if (mousePositionsInterval < 1) {
			mousePositionsInterval = 1;
		}
		
		timer.scheduleAtFixedRate(timerTask, mousePositionsInterval, mousePositionsInterval);
	}
	
	public void stop() {
		timer.cancel();
	}
	
	public void finished() {
		this.stop();
		Program.actualTimerFinished();
	}
}