package com.example.domaciprvi.objects;

import com.example.domaciprvi.Utilities;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;
public class Ball extends Circle {
	private static final double MAX_BALL_SPEED_TO_FALL = 500;

	public Translate getPosition() {
		return position;
	}

	private Translate position;
	private Point2D speed;
	private boolean tooFast = false;

	public void setInHole(boolean inHole) {
		isInHole = inHole;
	}

	private boolean isInHole =false;
	public boolean isNotStopped() {
		return notStopped;
	}

	public void setNotStopped(boolean notStopped) {
		this.notStopped = notStopped;
	}

	private boolean notStopped = false;
	public Ball ( double radius, Translate position, Point2D speed ) {
		super ( radius, Color.RED );
		
		this.position = position;
		this.speed = speed;
		
		super.getTransforms ( ).addAll ( this.position );
	}
	public boolean update (double ds, double left, double right, double top, double bottom, double dampFactor, double minBallSpeed, Rectangle[] obstacles) {
		boolean result = false;
		
		double newX = this.position.getX ( ) + this.speed.getX ( ) * ds;
		double newY = this.position.getY ( ) + this.speed.getY ( ) * ds;
		
		double radius = super.getRadius ( );
		
		double minX = left + radius;
		double maxX = right - radius;
		double minY = top + radius;
		double maxY = bottom - radius;

		this.position.setX ( Utilities.clamp ( newX, minX, maxX ) );
		this.position.setY ( Utilities.clamp ( newY, minY, maxY ) );
	
		if ( newX < minX || newX > maxX ) {
			this.speed = new Point2D ( -this.speed.getX ( ), this.speed.getY ( ) );
		}
		
		if ( newY < minY || newY > maxY ) {
			this.speed = new Point2D ( this.speed.getX ( ), -this.speed.getY ( ) );
		}
		//this.speed = checkObstaclesCollision(newX,newY,obstacles);

		this.speed = this.speed.multiply ( dampFactor );
		
		double ballSpeed = this.speed.magnitude ( );

		if(ballSpeed>MAX_BALL_SPEED_TO_FALL) {
			tooFast = true;
		}
		else {
			tooFast = false;
		}

		if ( ballSpeed < minBallSpeed ) {
			result = true;
		}

		return result;
	}

	public boolean isTooFast() {
		return tooFast;
	}

	public Point2D checkObstaclesCollision(double newX, double newY, Rectangle[] obstacles) {
		int i = 0;
		Point2D speed = this.speed;
		while(obstacles[i] != null) {
			double leftX = obstacles[i].getX();
			double rightX = obstacles[i].getX()+obstacles[i].getWidth();
			double topY = obstacles[i].getY();
			double bottomY = obstacles[i].getY()+obstacles[i].getHeight();

			double ballRadius = super.getRadius();

			if (((((newY+ballRadius)>topY && (newY+ballRadius)<bottomY)) || ((newY-ballRadius)>topY && (newY-ballRadius)<bottomY))
					&& ((newX-ballRadius)<rightX && (newX+ballRadius)>rightX)) {
					return speed = new Point2D ( -this.speed.getX ( ), this.speed.getY ( ) );
			}

			i++;
		}
		return speed;
	}
}
