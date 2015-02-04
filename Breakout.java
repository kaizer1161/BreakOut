/*
 * this is a simple game of breakout.
 * this game is similar to that of "Dx-ball" or paranoid.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH =
			(WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	/** Number of turns */
	private static final int NTURNS = 3;

	/* Method: run() */
	/** Runs the Breakout program. */
	public void run() {
		bouncePaddleClip = MediaTools.loadAudioClip("bounce.au");
		bounceBrickClip = MediaTools.loadAudioClip("37943^bounced.au");
		version();
		setupFloor();
		startGame();

	}

	private void setupFloor() {

		layBricks();
		generatePaddle();
		generateBall();
		scoreBoard();
		deathCount();

	}

	private void startGame(){
		Death = NTURNS;
		addMouseListeners();
		ballMovement();
		waitForClick();
		while(Death > 0){
			ballMotion();
			if ((Ball.getY() >= HEIGHT)){
				generateBall();
				Death--;
				waitForClick();
			}
			//Result.setLabel("Score = " + Score);
			Life.setLabel("Life = " + Death);	
		}
	}

	private void layBricks(){

		for (int i=0; i < NBRICK_ROWS; i++){

			for (int j = 0; j < NBRICKS_PER_ROW; j++){

				double x = 1 + (BRICK_WIDTH * j) + (j * BRICK_SEP);
				double y = BRICK_Y_OFFSET + (BRICK_HEIGHT * i) + (i * BRICK_SEP);	

				GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				add(brick);

				if (i==0 || i==1){

					brick.setFilled(true);
					brick.setFillColor(Color.RED);
				} 

				if (i==2 || i==3){

					brick.setFilled(true);
					brick.setFillColor(Color.ORANGE);
				}

				if (i==4 || i==5){

					brick.setFilled(true);
					brick.setFillColor(Color.YELLOW);
				}

				if (i==6 || i==7){

					brick.setFilled(true);
					brick.setFillColor(Color.GREEN);
				}

				if (i==8 || i==9){

					brick.setFilled(true);
					brick.setFillColor(Color.CYAN);
				}



			}
		}	
	}

	private GRect generatePaddle(){

		double PaddleX = (APPLICATION_WIDTH-PADDLE_WIDTH)/2;
		PaddleVerticalPosition = (APPLICATION_HEIGHT-PADDLE_Y_OFFSET);
		Paddle = new GRect (PaddleX, PaddleVerticalPosition,  PADDLE_WIDTH,  PADDLE_HEIGHT);
		Paddle.setFilled(true);
		add (Paddle);
		return Paddle;
	}

	public void mouseMoved(MouseEvent e){
		if ((e.getX() <= (APPLICATION_WIDTH - (PADDLE_WIDTH/2))) && (e.getX() >= (PADDLE_WIDTH/2))) {

			double x = e.getX()-(PADDLE_WIDTH/2);
			Paddle.setLocation(x, PaddleVerticalPosition);
		} 

	}


	private GOval generateBall(){
		double ballx = (APPLICATION_WIDTH-BALL_RADIUS)/2;
		double bally = (APPLICATION_HEIGHT - BALL_RADIUS)/2;
		Ball = new GOval (ballx, bally, BALL_RADIUS, BALL_RADIUS );
		Ball.setFilled(true);
		add (Ball);
		return Ball;

	}

	private void ballMotion(){
		pause(1000);
		bounceOffWall();
		checkForCollition();
		Ball.move(vx, vy);

	}

	private void ballMovement(){
		vy = 3.0;
		vx = rgen.nextDouble(1.0, 3.0); 
		if (rgen.nextBoolean(0.5)) vx = -vx;

	}	

	private void bounceOffWall(){

		if ((Ball.getX() >= WIDTH-10) || (Ball.getX() <= 0))
			vx = -vx;

		if ((Ball.getY() <= 0))
			vy = -vy;

	}

	private GObject getCollidingObject(){

		if(getElementAt(Ball.getX(), Ball.getY()) != null)
			return getElementAt(Ball.getX(), Ball.getY());

		else if (getElementAt(Ball.getX(), ((2*BALL_RADIUS)+Ball.getY()-5)) != null)
			return getElementAt(Ball.getX(), ((2*BALL_RADIUS)+Ball.getY()));

		else if(getElementAt(((2*BALL_RADIUS) + Ball.getX()), Ball.getY()) != null)
			return getElementAt(((2*BALL_RADIUS) + Ball.getX()), Ball.getY());

		else if (getElementAt(((2*BALL_RADIUS) + Ball.getX()), ((2*BALL_RADIUS) + Ball.getY()-5)) != null)
			return getElementAt(((2*BALL_RADIUS) + Ball.getX()), ((2*BALL_RADIUS) + Ball.getY()));

		else { 
			return null; 
		}

	}		

	private void checkForCollition(){
		GObject collider = getCollidingObject();

		if (collider == Paddle || collider != null){

			if (collider == Paddle){
				bouncePaddleClip.play();
				vy =-vy;
				Score--;

			} else {
				bounceBrickClip.play();
				remove(collider);
				vy =-vy;
				Score += 10;

			}
		}	
	} 

	private GLabel scoreBoard(){

		Result = new GLabel ("Score = " + Score);
		Result.setFont("sansarif-10");
		Result.setLocation(340, 10);
		add(Result);
		return Result;

	}	

	private GLabel deathCount(){

		Life = new GLabel ("Life = " + Death);
		Life.setFont("sansarif-10");
		Life.setLocation(340, 20);
		add(Life);
		return Life;


	}

	private GLabel version(){
		/* 
		 * It shows the version of the program.
		 * to keep track amount of time it is updated.
		 */

		GLabel version = new GLabel ("Version 1.0");
		version.setFont("sansarif-10");
		version.setLocation(1, 10);
		add(version);

		return version;
	}


	/* Private instance variable */

	private GLabel Result;
	private GLabel Life;
	private AudioClip bouncePaddleClip;
	private AudioClip bounceBrickClip;
	private GRect Paddle;
	private GOval Ball;
	private int PaddleVerticalPosition;
	private double vx;
	private double vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int Score;
	private int Death;
}
