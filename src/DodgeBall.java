import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.Timer;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;
import acm.util.RandomGenerator;

public class DodgeBall extends GraphicsProgram implements ActionListener 
{
	// variables
	private ArrayList<GOval> balls;
	private ArrayList<GRect> enemies;

	// CHANGED (Bonus): split score/time into separate labels so we can show more info
	private GLabel enemiesAliveLabel;
	private GLabel enemiesDestroyedLabel;
	private GLabel survivalTimeLabel;
	private GLabel gameOverLabel;

	private Timer moveTimer;
	private RandomGenerator rgen;

	private int numTimes;

	// CHANGED (Bonus): keep a separate counter for destroyed enemies
	private int enemiesDestroyed;

	// CHANGED (Bonus): simple flag so mouse clicks stop doing things after game over
	private boolean gameOver;
	// end variables

	// constants
	public static final int SIZE = 25;
	public static final int SPEED = 2;
	public static final int MS = 50;
	public static final int MAX_ENEMIES = 10;
	public static final int WINDOW_HEIGHT = 600;
	public static final int WINDOW_WIDTH = 300;
	// end constants

	public void run() 
	{
		rgen = RandomGenerator.getInstance();
		balls = new ArrayList<GOval>();
		enemies = new ArrayList<GRect>();

		// CHANGED (Bonus): initialize extra tracking variables
		numTimes = 0;
		enemiesDestroyed = 0;
		gameOver = false;

		// CHANGED (Bonus): replace the single label with multiple labels
		enemiesAliveLabel = new GLabel("Enemies alive: " + enemies.size(), 5, WINDOW_HEIGHT - 5);
		add(enemiesAliveLabel);

		enemiesDestroyedLabel = new GLabel("Destroyed: " + enemiesDestroyed, 5, WINDOW_HEIGHT - 25);
		add(enemiesDestroyedLabel);

		survivalTimeLabel = new GLabel("Time: 0s", 5, WINDOW_HEIGHT - 45);
		add(survivalTimeLabel);

		// CHANGED (Bonus): create the gameOver label now (but only add/show it when you lose)
		gameOverLabel = new GLabel("", 0, 0);

		moveTimer = new Timer(MS, this);
		moveTimer.start();
		addMouseListeners();
	}

	public void actionPerformed(ActionEvent e) 
	{
		// CHANGED (Bonus): if the game is over, do nothing on ticks
		if (gameOver)
		{
			return;
		}

		// movement + checks
		hitDetection();

		// CHANGED (Bonus): tick the timer and update survival time each tick
		numTimes++;
		updateSurvivalTimeLabel();

		// add enemies periodically
		if (numTimes % 40 == 0)
		{
			addAnEnemy();
		}

		moveAllBallsOnce();
		moveAllEnemiesOnce();

		// CHANGED (Bonus): lose condition if MAX_ENEMIES is exceeded
		if (enemies.size() > MAX_ENEMIES)
		{
			loseGame();
		}
	}

	public void mousePressed(MouseEvent e) 
	{
		// CHANGED (Bonus): no interactions after game over
		if (gameOver)
		{
			return;
		}

		// (kept from prior part) if any ball has moved too far left, don't add new balls
		for (GOval b : balls) 
		{
			if (b.getX() < SIZE * 2.5) 
			{
				return;
			}
		}
		addABall(e.getY());
	}

	private void addABall(double y) 
	{
		GOval ball = makeBall(SIZE / 2, y);
		add(ball);
		balls.add(ball);
	}

	public GOval makeBall(double x, double y) 
	{
		GOval temp = new GOval(x - SIZE / 2, y - SIZE / 2, SIZE, SIZE);
		temp.setColor(Color.RED);
		temp.setFilled(true);
		return temp;
	}

	private void addAnEnemy() 
	{
		GRect e = makeEnemy(rgen.nextInt(0, WINDOW_HEIGHT - SIZE / 2));
		enemies.add(e);

		// CHANGED (Bonus): update labels whenever enemy count changes
		updateEnemiesAliveLabel();

		add(e);
	}

	public GRect makeEnemy(double y) 
	{
		GRect temp = new GRect(WINDOW_WIDTH - SIZE, y - SIZE / 2, SIZE, SIZE);
		temp.setColor(Color.GREEN);
		temp.setFilled(true);
		return temp;
	}

	private void moveAllBallsOnce() 
	{
		for (GOval ball : balls)
		{
			ball.move(SPEED, 0);
		}
	}

	private void moveAllEnemiesOnce()
	{
		for (GRect enemy : enemies)
		{
			enemy.move(0, rgen.nextInt(-2, 2));
		}
	}

	private void hitDetection()
	{
		for (int i = enemies.size() - 1; i >= 0; i--)
		{
			GRect tempEnemy = enemies.get(i);

			for (GOval ball : balls)
			{
				GObject tempObj = getElementAt(ball.getX() + ball.getWidth() + 1, ball.getY() + ball.getHeight() / 2);

				if (tempObj == tempEnemy)
				{
					remove(tempEnemy);
					enemies.remove(i);

					// CHANGED (Bonus): track destroyed enemies and update labels
					enemiesDestroyed++;
					updateEnemiesDestroyedLabel();
					updateEnemiesAliveLabel();

					break;
				}
			}
		}
	}

	// CHANGED (Bonus): keep label updates in tiny helper methods
	private void updateEnemiesAliveLabel()
	{
		enemiesAliveLabel.setLabel("Enemies alive: " + enemies.size());
	}

	// CHANGED (Bonus): keep label updates in tiny helper methods
	private void updateEnemiesDestroyedLabel()
	{
		enemiesDestroyedLabel.setLabel("Destroyed: " + enemiesDestroyed);
	}

	// CHANGED (Bonus): show survival time in seconds (numTimes ticks * MS ms per tick)
	private void updateSurvivalTimeLabel()
	{
		int elapsedMs = numTimes * MS;
		int elapsedSeconds = elapsedMs / 1000;
		survivalTimeLabel.setLabel("Time: " + elapsedSeconds + "s");
	}

	// CHANGED (Bonus): lose logic (stop timer, clear screen, show message)
	private void loseGame()
	{
		gameOver = true;

		// stop animation
		moveTimer.stop();

		// clear everything
		removeAll();

		// show lose message (simple)
		gameOverLabel.setLabel("You lost");
		double x = (WINDOW_WIDTH / 2.0) - (gameOverLabel.getWidth() / 2.0);
		double y = WINDOW_HEIGHT / 2.0;
		gameOverLabel.setLocation(x, y);
		add(gameOverLabel);

		// CHANGED (Bonus): show final stats on the lose screen
		GLabel finalScore = new GLabel("Destroyed: " + enemiesDestroyed, 0, 0);
		finalScore.setLocation((WINDOW_WIDTH / 2.0) - (finalScore.getWidth() / 2.0), y + 25);
		add(finalScore);

		int elapsedSeconds = (numTimes * MS) / 1000;
		GLabel finalTime = new GLabel("Survival time: " + elapsedSeconds + "s", 0, 0);
		finalTime.setLocation((WINDOW_WIDTH / 2.0) - (finalTime.getWidth() / 2.0), y + 45);
		add(finalTime);
	}

	public void init() 
	{
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
	}

	public static void main(String args[]) 
	{
		new DodgeBall().start();
	}
}



/*
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.Timer;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;
import acm.util.RandomGenerator;

public class DodgeBall extends GraphicsProgram implements ActionListener 
{
	//variables
	private ArrayList<GOval> balls;
	private ArrayList<GRect> enemies;
	private GLabel text;
	private Timer moveTimer;
	private RandomGenerator rgen;
	private int numTimes;
	//end variables
	
	//constants
	public static final int SIZE = 25;
	public static final int SPEED = 2;
	public static final int MS = 50;
	public static final int MAX_ENEMIES = 10;
	public static final int WINDOW_HEIGHT = 600;
	public static final int WINDOW_WIDTH = 300;
	//end constants
	
	public void run() 
	{
		rgen = RandomGenerator.getInstance();
		balls = new ArrayList<GOval>();
		enemies = new ArrayList<GRect>();
		numTimes = 0;
		
		text = new GLabel(""+enemies.size(), 0, WINDOW_HEIGHT);
		add(text);
		
		moveTimer = new Timer(MS, this);
		moveTimer.start();
		addMouseListeners();
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		hitDetection();
		numTimes ++;
		if (numTimes % 40 == 0) addAnEnemy();
		moveAllBallsOnce();
		moveAllEnemiesOnce();
		
		
	}
	
	public void mousePressed(MouseEvent e) 
	{
		for(GOval b:balls) 
		{
			if(b.getX() < SIZE * 2.5) 
			{
				return;
			}
		}
		addABall(e.getY());     
	}
	
	private void addABall(double y) 
	{
		GOval ball = makeBall(SIZE/2, y);
		add(ball);
		balls.add(ball);
	}
	
	public GOval makeBall(double x, double y) 
	{
		GOval temp = new GOval(x-SIZE/2, y-SIZE/2, SIZE, SIZE);
		temp.setColor(Color.RED);
		temp.setFilled(true);
		return temp;
	}
	
	private void addAnEnemy() 
	{
		GRect e = makeEnemy(rgen.nextInt(0, WINDOW_HEIGHT-SIZE/2));
		enemies.add(e);
		text.setLabel("" + enemies.size());
		add(e);
	}
	
	public GRect makeEnemy(double y) 
	{
		GRect temp = new GRect(WINDOW_WIDTH-SIZE, y-SIZE/2, SIZE, SIZE);
		temp.setColor(Color.GREEN);
		temp.setFilled(true);
		return temp;
	}

	private void moveAllBallsOnce() 
	{
		for(GOval ball:balls) {
			ball.move(SPEED, 0);
		}
	}
	
	private void moveAllEnemiesOnce()
	{
		for (GRect enemy:enemies)
		{
			enemy.move(0, rgen.nextInt(-2, 2));
		}
	}

	private void hitDetection()
	{
		for (int i = enemies.size() - 1; i >= 0; i--)
		{
			GRect tempEnemy = enemies.get(i);

			for (GOval ball : balls)
			{
				GObject tempObj = getElementAt(ball.getX() + ball.getWidth() + 1, ball.getY() + ball.getHeight() / 2);

				if (tempObj == tempEnemy)
				{
					remove(tempEnemy);
					enemies.remove(i);
					text.setLabel("" + enemies.size());
					break;
				}
			}
		}
	}
	
	public void init() 
	{
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
	}
	
	public static void main(String args[]) 
	{
		new DodgeBall().start();
	}
}
*/