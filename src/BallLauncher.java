import acm.graphics.*;
import acm.program.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class BallLauncher extends GraphicsProgram implements ActionListener
{
	//constants
	public static final int PROGRAM_HEIGHT = 600;
	public static final int PROGRAM_WIDTH = 800;
	public static final int SIZE = 25;
	public static final int MS = 50; //→ Timer wake interval.
	public static final int SPEED = 2; //→ movement speed.
	// end constants
	
	//variables
	private ArrayList<GOval> balls;
	Timer daTimer;

	//end variables
	
	public void init() 
	{
		setSize(PROGRAM_WIDTH, PROGRAM_HEIGHT);
		requestFocus();
	}
	
	public void run() 
	{
		addMouseListeners();
        balls = new ArrayList<GOval>();
		daTimer = new Timer(MS, this);
		daTimer.start();

	}
	
	public void mousePressed(MouseEvent e) 
	{
		GOval ball = makeBall(SIZE/2, e.getY());
		add(ball);
	}
	
	public GOval makeBall(double x, double y) 
	{
		GOval temp = new GOval(x-SIZE/2, y-SIZE/2, SIZE, SIZE);
		temp.setColor(Color.RED);
		temp.setFilled(true);
		balls.add(temp);
		return temp;
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		for (GOval ball:balls)  
		{
		    ball.move(SPEED, 0);
		}
		
	}
	
	public static void main(String[] args) 
	{
		new BallLauncher().start();
	}

}
