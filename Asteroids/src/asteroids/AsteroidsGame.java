package asteroids;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;





public class AsteroidsGame extends Applet implements Runnable, KeyListener {


Thread thread;
long startTime, endTime, framePeriod; //long is just a very big integer
Dimension dim; // stores the size of the back buffer
Image img; // the back buffer object
Graphics g; // used to draw on the back buffer
Ship ship;	
boolean paused;  // True if the game is paused. Enter is the pause key
Shot[] shots; //Variable that stores the new array of Shots
int numShots;  //Stores the number of shots in the array
boolean shooting; //true if the ship is currently shooting
Asteroid[] asteroids;
int numAsteroids;
double astRadius, minAstVel, maxAstVel;
int astNumHits, astNumSplit;

int level;

	public void init(){
		resize(500,500);
		
		
		
		shots = new Shot[41];
		numAsteroids = 0;
		level = 0;
		astRadius = 60;
		minAstVel=.5;
		maxAstVel = 5;
		astNumHits = 3;
		astNumSplit = 2;
		
		addKeyListener(this);
		startTime= 0;
		endTime = 0;
		framePeriod = 25; // 25 milliseconds is a good frame period 
		dim = getSize();
		img = createImage(dim.width, dim.height);
		g = img.getGraphics();
		thread = new Thread(this); // create the thread
		thread.start(); // start the thread running
		
		
	}
	
	public void setUpNextLevel(){
		level++;
		ship = new Ship(250, 250, 0, .35, .98, .1, 12); //creates the ship
		// I like .35 for acceleration, .98 for velocityDecay, and .1 for rotationalSpeed. They give the controls a nice feel.
		numShots = 0;
		paused = false;
		shooting = false;
		
		asteroids = new Asteroid[level * (int)Math.pow(astNumSplit, astNumHits-1)+1];
		numAsteroids = level;
		
		for(int i=0;i<numAsteroids;i++)
			asteroids[i] = new Asteroid
			(Math.random()*dim.width,Math.random()*dim.height,astRadius,minAstVel,maxAstVel,astNumHits,astNumSplit);
	}
	
	public void paint(Graphics gfx){// Paints to the back buffer g & gfx then paints it all directly to the screen
		g.setColor(Color.black);  
		g.fillRect(0, 0, 500, 500);
		
		
		for(int i=0;i<numShots;i++)
			shots[i].draw(g);
		
		for(int i=0;i<numAsteroids;i++)
			asteroids[i].draw(g);
			ship.draw(g);
		
		g.setColor(Color.cyan);
		g.drawString("Level " + level,20,20);
			
			
		gfx.drawImage(img, 0,0, this);
	}
	
	public void update(Graphics gfx){
		paint(gfx);
	}
	
	public void run() {
		for (;;){	
				startTime=System.currentTimeMillis();//mark start time
				
				if(numAsteroids <= 0)
					setUpNextLevel();
				
				if (!paused){
					ship.move(dim.width, dim.height); //Move the ship
				
					for(int i=0;i<numShots;i++){
						shots[i].move(dim.width, dim.height);
				
						if(shots[i].getLifeLeft() <= 0){
							deleteShot(i);
						}
					}
				
				updateAsteroids();
					
				if (shooting && ship.canShoot()){
					shots[numShots] = ship.shoot();
					numShots++;
				}
			}
				repaint();
				// the next lines pause execution for 20 milliseconds minus the time it took to move the circle and repaint.
				try{
					//mark end time
					endTime = System.currentTimeMillis();
					// don’t sleep for a negative amount of time
					if (framePeriod-(endTime-startTime)>0)
							Thread.sleep(framePeriod - (endTime-startTime));
				}catch(InterruptedException e){	
				}
			
		}
	}
	
	private void deleteShot(int index){
		
		numShots--;
		for(int i=index; i<numShots; i++)
			shots[i] =shots[i + 1];
		shots[numShots] = null;
	}
	
	private void deleteAsteroid(int index){
		
		numAsteroids--;
		for(int i=index;i<numAsteroids;i++)
			asteroids[i] = asteroids[i+1];
		asteroids[numAsteroids] = null;
	}
	
	private void addAsteroid(Asteroid ast){
		asteroids[numAsteroids]=ast;
		numAsteroids++;
	}
	
	private void updateAsteroids(){
		for(int i=0;i<numAsteroids;i++){
			asteroids[i].move(dim.width, dim.height);
			
			if(asteroids[i].shipCollision(ship)){
				level--;
				numAsteroids=0;
				return;
			}
			for(int j=0;j<numShots;j++){
				if(asteroids[i].shotCollision(shots[j])){
					deleteShot(j);
					
					if(asteroids[i].getHitsLeft()>1){
						for(int k=0;k<asteroids[i].getNumSplit();k++)
							addAsteroid(asteroids[i].createSplitAsteroid(minAstVel, maxAstVel));
					}
					deleteAsteroid(i);
					j=numShots;
					i--;
				}
			}
		}
		
	}
	
	public void keyPressed(KeyEvent e){
		if(e.getKeyCode()==KeyEvent.VK_ENTER){
			 //These first two lines allow the asteroids to move
			 //while the player chooses when to enter the game.
			 //This happens when the player is starting a new life.
			 if(!ship.isActive() && !paused)
				 ship.setActive(true);
			 else{
				 paused=!paused; //enter is the pause button
				 if(paused) // grays out the ship if paused
					 ship.setActive(false);
				 else
					 ship.setActive(true);
			 }
			 }else if(paused || !ship.isActive()) //if the game is
				 return; //paused or ship is inactive, do not respond to the controls except for enter to unpause
	
			 else if(e.getKeyCode()==KeyEvent.VK_UP)
				 ship.setAccelerating(true);
			 else if(e.getKeyCode()==KeyEvent.VK_LEFT)
				 ship.setTurningLeft(true);
			 else if(e.getKeyCode()==KeyEvent.VK_RIGHT)
				 ship.setTurningRight(true); 
			 else if(e.getKeyCode()==KeyEvent.VK_CONTROL)
				 shooting = true;

				
	}
	
	public void keyReleased(KeyEvent e){
		
		if(e.getKeyCode()==KeyEvent.VK_UP)
			 ship.setAccelerating(false);
		else if(e.getKeyCode()==KeyEvent.VK_LEFT)
			 ship.setTurningLeft(false);
		else if(e.getKeyCode()==KeyEvent.VK_RIGHT)
			 ship.setTurningRight(false); 
		else if(e.getKeyCode()==KeyEvent.VK_CONTROL)
			shooting = false;
	}
	
	public void keyTyped(KeyEvent e){ //empty method, but still needed to implement the KeyListener interface
	}

	
}

