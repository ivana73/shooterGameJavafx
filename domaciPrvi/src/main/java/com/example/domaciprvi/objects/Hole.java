package com.example.domaciprvi.objects;

import com.example.domaciprvi.Game;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Translate;

public class Hole extends Circle {
    private int holeCost;
    private Game game = null;
    private int idHole;
    private static int incId = 0;
    public Hole(double radius, Translate position, int holeCost, Game game) {
        super(radius);
        this.game = game;
        this.holeCost = holeCost;
        Color c = Color.YELLOW;
        switch (holeCost) {
            case 5: c = Color.YELLOW;
                    break;
            case 10: c = Color.ORANGE;
                    break;
            case 20: c = Color.RED;
                    break;
        }

        Stop stops[] = {
                new Stop(1, c),
                new Stop(0, Color.BLACK)
        };

        RadialGradient radialGradient = new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE, stops);

        super.setFill(radialGradient);


        super.getTransforms().addAll(position);
    }

    public boolean handleCollision(Ball ball) {
        Bounds ballBounds = ball.getBoundsInParent();

        double ballX = ballBounds.getCenterX();
        double ballY = ballBounds.getCenterY();
        double ballRadius = ball.getRadius();

        Bounds holeBounds = super.getBoundsInParent();
        double holeX = holeBounds.getCenterX();
        double holeY = holeBounds.getCenterY();
        double holeRadius = super.getRadius();

        double distanceX = holeX - ballX;
        double distanceY = holeY - ballY;

        double distanceSquared = distanceX * distanceX + distanceY * distanceY;

        boolean result = distanceSquared < (holeRadius * holeRadius);

        if (ball.isTooFast()) {
            result = false;
        }
        if (result) {
            game.setPoens(game.getPoens() + holeCost);
        }

        if (result) {
            ball.setInHole(true);
        }
        return result;
    }
    ;
}
