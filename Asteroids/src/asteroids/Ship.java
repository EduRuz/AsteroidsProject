package asteroids;
import java.awt.*; 

public class Ship {

	final double[] origXPts = {14, -10, -6, -10}, origYPts = {0, -8, 0, 8}, origFlameXPts = {-6, -23, -6}, 
				   origFlameYPts = {-3,0,3};
	final int radius =6;
	double x, y, angle, xVelocity, yVelocity, acceleration, velocityDecay, rotationalSpeed;
	boolean turningLeft, turningRight, accelerating, active;
	int[] xPts, yPts, flameXPts, flameYPts;
	int shotDelay, shotDelayLeft;


public Ship(double x, double y, double angle, double acceleration, double velocityDecay, double rotationalSpeed, int shotDelay){
	// define the shape of the ship and its flame
	
	this.x = x;
	this.y = y;
	this.angle = angle;
	this.acceleration = acceleration;
	this.velocityDecay = velocityDecay;
	this.rotationalSpeed = rotationalSpeed;
	xVelocity = 0;  // not moving
	yVelocity = 0;
	turningLeft = false; // not turning
	turningRight = false;
	accelerating = false;  // not accelerating
	active = false; // start off paused
	xPts = new int[4];  // allocate space for the arrays
	yPts = new int[4];
	flameXPts = new int[3];
	flameYPts = new int[3];
	this.shotDelay = shotDelay;  // # of frames between shots
	shotDelayLeft = 0; // ready to shoot
}

public void draw(Graphics g){
	//rotate the points, translate them to the ship's location (by
	//adding x and y), then round them by adding .5 and casting them
	//as integers (which truncates any decimal place)
	if (accelerating && active){ // draw flame if accelerating
		for (int i = 0; i<3; i++){
			flameXPts[i]=(int) (origFlameXPts[i]*Math.cos(angle) - origFlameYPts[i]*Math.sin(angle) + x+.5);
			flameYPts[i]=(int)(origFlameXPts[i]*Math.sin(angle)+ origFlameYPts[i]*Math.cos(angle)+ y+.5); 
		}
		g.setColor(Color.red);  //set color of flame
		g.fillPolygon(flameXPts, flameYPts, 3); // 3 is # of points
	}
	//calculate the polygon for the ship, then draw it
	for(int i=0;i<4;i++){
		xPts[i] = (int)(origXPts[i]*Math.cos(angle) - origYPts[i]*Math.sin(angle) + x+.5);
		yPts[i] = (int)(origXPts[i]*Math.sin(angle) + origYPts[i]*Math.cos(angle) + y+.5);
	}
	if(active)
			g.setColor(Color.white);
	else 
			g.setColor(Color.darkGray);
	g.fillPolygon(xPts, yPts, 4);
}

public void move (int scrnWidth, int scrnHeight){  
	//move() is called every frame that the game is run, so this ticks down the shot delay 
	//this is backwards from typical polar coordinates
	//because positive y is downward. Because of that, adding to the angle is rotating clockwise (to the right)
	 //Keep angle within bounds of 0 to 2*PI
	if(shotDelayLeft > 0)
		shotDelayLeft--;
	if(turningLeft)
		angle -= rotationalSpeed;
	if(turningRight)
		angle += rotationalSpeed;
	if(angle>(2*Math.PI))
		angle -= (2*Math.PI);
	else if (angle < 0)
		angle += (2*Math.PI);
	if(accelerating) {//adds accel to velocity in direction pointed, calculates components of accel and adds them to velocity
		
		xVelocity += acceleration*Math.cos(angle);
		yVelocity += acceleration*Math.sin(angle);
	}
	x += xVelocity; //move the ship by adding velocity to position
	y += yVelocity;
	xVelocity *= velocityDecay; //slows ship down by percentages (velDecay
	yVelocity *= velocityDecay; //should be a decimal between 0 and 1 
	
	if(x < 0)
		x += scrnWidth;
	else if(x > scrnWidth)
		x -= scrnWidth;
	if(y < 0)
		y += scrnHeight;
	else if(y > scrnHeight)
		y -= scrnHeight;
		
}
public void setAccelerating(boolean accelerating){
this.accelerating=accelerating; //start or stop accelerating the ship
}

public void setTurningLeft(boolean turningLeft){
this.turningLeft=turningLeft; //start or stop turning the ship
}

public void setTurningRight(boolean turningRight){
this.turningRight=turningRight;
}

public double getX(){
return x; // returns the ship�s x location
}

public double getY(){
return y;
}

public double getRadius(){
return radius; // returns radius of circle that approximates the ship
}

public void setActive(boolean active){
this.active=active; //used when the game is paused or unpaused
}

public boolean isActive(){
return active;
}

public boolean canShoot(){
if(shotDelayLeft>0) //checks to see if the ship is ready to
 return false; //shoot again yet or if it needs to wait longer
else
 return true;
}

public Shot shoot(){
	shotDelayLeft=shotDelay; //set delay till next shot can be fired
	
	//a life of 40 makes the shot travel about the width of the screen before disappearing
	return new Shot (x, y, angle, xVelocity, yVelocity, 40);
}

}