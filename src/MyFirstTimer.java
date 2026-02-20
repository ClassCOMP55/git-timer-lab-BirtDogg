import acm.graphics.*;
import acm.program.*;
import javax.swing.*;
import java.awt.event.*;

public class MyFirstTimer extends GraphicsProgram implements ActionListener
{
	public static final int PROGRAM_HEIGHT = 600;
	public static final int PROGRAM_WIDTH = 800;
	public static final int MAX_STEPS = 20;
	private GLabel myLabel;
	private int numTimes;
	Timer someTimerVar;
	
	public void init() 
	{
		setSize(PROGRAM_WIDTH, PROGRAM_HEIGHT);
		requestFocus();
	}
	
	public void run() 
	{
		numTimes = 0;
		someTimerVar = new Timer(1000, this);
		myLabel = new GLabel("# of times called?", 0, 100);
		add(myLabel);
//		Timer t = new Timer(1000, this);
		someTimerVar.setInitialDelay(3000); // → delay before starting.
		someTimerVar.start();
		
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		//TODO add what we want to do every second
		myLabel.move(5, 0);
		numTimes += 1; 
		myLabel.setLabel("times called? "+ numTimes);
		if (numTimes == 10) 
		{
			someTimerVar.stop(); // → stops the timer.
		}
	}

	
	public static void main(String[] args) 
	{
		new MyFirstTimer().start();
	}
}

//	class Name extends GraphicsProgram implements ActionListener {}

//	public void actionPerformed(ActionEvent e) {}

