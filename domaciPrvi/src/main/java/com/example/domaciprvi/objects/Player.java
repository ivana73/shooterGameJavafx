package com.example.domaciprvi.objects;

import com.example.domaciprvi.Utilities;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Player extends Group {
	
	private double width;
	private double height;
	private Translate position;
	private Rotate rotate;
	private double baseRadius;
	private int playerType=1;
	private double coefPlayer2 = 5;

	public Player ( double width, double height, Translate position, int playerType ) {
		this.playerType = playerType;
		this.width = width;
		this.height = height;
		this.position = position;

		baseRadius = width/2;

		switch (playerType) {
			case 1:
				Circle circle = new Circle(baseRadius, Color.ORANGE);
				circle.getTransforms().add(new Translate(width/2,
						height-baseRadius));


				//Rectangle cannon = new Rectangle ( width, height, Color.LIGHTBLUE );
				Path cannon = new Path(
						new MoveTo(width/4,0),
						new LineTo(0, height-baseRadius),
						new HLineTo(width),
						new LineTo(width*3/4, 0),
						new ClosePath()
				);

				cannon.setFill(Color.LIGHTBLUE);

				super.getChildren ( ).add ( cannon );
				super.getChildren().add(circle);
				this.rotate = new Rotate ( );

				super.getTransforms ( ).addAll (
						position,
						new Translate ( width / 2, height-baseRadius ),
						rotate,
						new Translate ( -width / 2, -(height-baseRadius) )
				);
				break;
			case 2:
				Circle circle1 = new Circle(baseRadius*1.2, Color.DARKGREY);
				circle1.getTransforms().add(new Translate(width/2,
						height-baseRadius));


				//Rectangle cannon = new Rectangle ( width, height, Color.LIGHTBLUE );
				Path cannon1 = new Path(
						new MoveTo(0,0),
						new LineTo(width/4, height-baseRadius),
						new HLineTo(width*3/4),
						new LineTo(width, 0),
						new ClosePath()
				);

				cannon1.setFill(Color.LIGHTGREEN);

				super.getChildren ( ).add ( cannon1 );
				super.getChildren().add(circle1);
				this.rotate = new Rotate ( );

				super.getTransforms ( ).addAll (
						position,
						new Translate ( width / 2, height-baseRadius ),
						rotate,
						new Translate ( -width / 2, -(height-baseRadius) )
				);
				break;
		}


	}
	
	public void handleMouseMoved ( MouseEvent mouseEvent, double minAngleOffset, double maxAngleOffset ) {
		Bounds bounds = super.getBoundsInParent ( );
		
		double startX = bounds.getCenterX ( );
		double startY = bounds.getMaxY ( );
		
		double endX = mouseEvent.getX ( );
		double endY = mouseEvent.getY ( );
		
		Point2D direction     = new Point2D ( endX - startX, endY - startY ).normalize ( );
		Point2D startPosition = new Point2D ( 0, -1 );
		
		double angle = ( endX > startX ? 1 : -1 ) * direction.angle ( startPosition );
		
		this.rotate.setAngle ( Utilities.clamp ( angle, minAngleOffset, maxAngleOffset ) );
	}
	
	public Translate getBallPosition ( ) {
		double startX = this.position.getX ( ) + this.width/2;
		double startY = this.position.getY ( ) + this.height-baseRadius;
		
		double x = startX + Math.sin ( Math.toRadians ( this.rotate.getAngle ( ) ) ) * this.height;
		double y = startY - Math.cos ( Math.toRadians ( this.rotate.getAngle ( ) ) ) * this.height;
		
		Translate result = new Translate ( x, y );
		
		return result;
	}
	
	public Point2D getSpeed ( ) {
		double startX = this.position.getX ( ) + this.width / 2;
		double startY = this.position.getY ( ) + this.height-baseRadius;
		
		double endX = startX + Math.sin ( Math.toRadians ( this.rotate.getAngle ( ) ) ) * this.height;
		double endY = startY - Math.cos ( Math.toRadians ( this.rotate.getAngle ( ) ) ) * this.height;
		
		Point2D result = new Point2D ( endX - startX, endY - startY );
		
		return result.normalize ( );
	}
}
