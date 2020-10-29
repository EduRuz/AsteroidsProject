package asteroids;
import java.awt.*;

public class Asteroid {
double x, y, xVelocity, yVelocity, radius;
int hitsLeft, numSplit;

public Asteroid(double x, double y, double radius, double minVelocity, double maxVelocity, int hitsLeft, int numSplit){
	
	this.x = x;
	this.y = y;
	this.radius = radius; 
	this.hitsLeft = hitsLeft;//number of shots left to destroy it
	this.numSplit = numSplit; //number of smaller asteroids it 
//breaks up into when shot calculates a random direction and a random velocity between minVelocity and maxVelocity
	double vel = minVelocity + Math.random()*(maxVelocity - minVelocity), dir = 2*Math.PI*Math.random(); // random direction
	xVelocity = vel*Math.cos(dir);
	yVelocity = vel*Math.sin(dir);
}

public void move(int scrnWidth, int scrnHeight){ //Move the Asteroid
	x += xVelocity;
	y += yVelocity;
	//wrap around code allowing the asteroid to go off the screen to a distance equal to its radius before entering on the
	 //other side. Otherwise, it would go halfway off the sceen, then disappear and reappear halfway on the other side
	 //of the screen.
	if(x<0-radius)
		 x+=scrnWidth+2*radius;
	else if(x>scrnWidth+radius)
		 x-=scrnWidth+2*radius;
	if(y<0-radius)
		 y+=scrnHeight+2*radius;
	else if(y>scrnHeight+radius)
		 y-=scrnHeight+2*radius; 
}

public void draw(Graphics g){
	g.setColor(Color.gray);
	g.fillOval((int)(x-radius+.5), (int)(y-radius+.5), (int)(2*radius), (int)(2*radius));
}

public int getHitsLeft(){
	return hitsLeft;
}

public int getNumSplit(){
	return numSplit;
}

public boolean shipCollision(Ship ship){
	if(Math.pow(radius+ship.getRadius(), 2) > Math.pow(ship.getX()-x, 2) + Math.pow(ship.getY()-y, 2) && ship.isActive())
		return true;
	return false;
				
}
public boolean shotCollision(Shot shot){
	if (Math.pow(radius,2) > Math.pow(shot.getX()-x,2) + Math.pow(shot.getY()-y,2))
		return true;
	return false;
}
public Asteroid createSplitAsteroid(double minVelocity, double maxVelocity){
	//when this asteroid gets hit by a shot, this method is called, numSplit times by AsteroidsGame to create numSplit 
	//smaller asteroids. Dividing the radius by sqrt(numSplit) makes the sum of the areas taken up by the smaller asteroids 
	//equal to the area of this asteroid. Each smaller asteroid has one less hit left before being completely destroyed.
	return new Asteroid(x,y,radius/Math.sqrt(numSplit),minVelocity,maxVelocity,hitsLeft-1,numSplit);
}

}
