package com.example.domaciprvi;

import com.example.domaciprvi.objects.Ball;
import com.example.domaciprvi.objects.Hole;
import com.example.domaciprvi.objects.Player;
import javafx.animation.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.util.Duration;


public class Game extends Application implements EventHandler<MouseEvent> {
    private static final double WINDOW_WIDTH  = 600;
    private static final double WINDOW_HEIGHT = 700;
    public static final int GAME_TIME_MAX = 50; //in seconds
    private static final double PLAYER_WIDTH            = 20;
    private static final double PLAYER_HEIGHT           = 80;
    private static final double PLAYER_MAX_ANGLE_OFFSET = 60;
    private static final double PLAYER_MIN_ANGLE_OFFSET = -60;

    private static final double MS_IN_S            = 1e3;
    private static final double NS_IN_S            = 1e9;
    private static final double MAXIMUM_HOLD_IN_S  = 3;
    private static final double MAXIMUM_BALL_SPEED = 1500;
    private static final double BALL_RADIUS        = Game.PLAYER_WIDTH / 4;
    private static final double BALL_DAMP_FACTOR   = 0.995;
    private static final double MIN_BALL_SPEED     = 1;
    private static final double PLAYER2_MULT     = 2.5;
    private static int numOfTried = 15;
    private static final double HOLE_RADIUS = 3 * BALL_RADIUS;
    private static final double refreshRate = 1000;

    private Group root;
    private Player player;
    private Ball ball;
    private long time;
    private Hole holes[];
    private Rectangle[] obstacles;
    private Rectangle[] muds;
    private Rectangle[] ices;
    private double frameNs;
    private Image userBackground;
    private int playerType=1;
    private double speedMult = 1;
    private Stage stage;
    private Label clock = new Label();

    private Label numOfTries = null;
    private boolean isIce = false;
    private boolean isMud = false;
    public int getPoens() {
        return poens;
    }

    public void setHoleColX(double holeColX) {
        this.holeColX = holeColX;
    }

    public void setHoleColY(double holeColY) {
        this.holeColY = holeColY;
    }

    private double holeColX;
    private double holeColY;
    private int poens = 0;
    public void setPoens(int poens) {
        this.poens = poens;
        numOfPoens.setText("Poeni: "+ poens);
    }
    private Label numOfPoens;
    private Stage mainStage = null;
    private Timeline timelineClock;
    private boolean gameOver = false;
    Game(Image img,Stage stage,boolean playerType) throws IOException {
        this.root  = new Group ( );
        this.userBackground=img;
        if (!playerType) {
            this.playerType = 2;
            speedMult = Game.PLAYER2_MULT;
        }
        start(stage);
    }

    public Group getRoot() {
        return this.root;
    }
    private void addFourHoles ( ) {
        Translate hole0Position = new Translate (
                Game.WINDOW_WIDTH / 2,
                Game.WINDOW_HEIGHT * 0.1
        );
        Hole hole0 = new Hole ( Game.HOLE_RADIUS, hole0Position , 20, this);
        this.root.getChildren ( ).addAll ( hole0 );

        Translate hole1Position = new Translate (
                Game.WINDOW_WIDTH / 2,
                Game.WINDOW_HEIGHT * 0.4
        );
        Hole hole1 = new Hole ( Game.HOLE_RADIUS, hole1Position, 5, this);
        this.root.getChildren ( ).addAll ( hole1 );

        Translate hole2Position = new Translate (
                Game.WINDOW_WIDTH / 3,
                Game.WINDOW_HEIGHT * 0.25
        );
        Hole hole2 = new Hole ( Game.HOLE_RADIUS, hole2Position,10, this );
        this.root.getChildren ( ).addAll ( hole2 );

        Translate hole3Position = new Translate (
                Game.WINDOW_WIDTH * 2 / 3,
                Game.WINDOW_HEIGHT * 0.25
        );
        Hole hole3 = new Hole ( Game.HOLE_RADIUS, hole3Position, 10, this );
        this.root.getChildren ( ).addAll ( hole3 );

        this.holes = new Hole[] {
                hole0,
                hole1,
                hole2,
                hole3,
        };
    }
    private void addStillObstacles() {
        URL url = getClass().getResource("/images/fence.jpg");
        Image fenceImg = new Image(url.toString());

        final Rectangle branch0 = new Rectangle(20, WINDOW_HEIGHT/7, 80, 20);
        branch0.setFill(new ImagePattern(fenceImg));
        root.getChildren().add(branch0);

        final Rectangle branch1 = new Rectangle(150, WINDOW_HEIGHT/5, 80, 20);
        branch1.setFill(new ImagePattern(fenceImg));
        root.getChildren().add(branch1);

        obstacles = new Rectangle[]{
                branch0,
                branch1
        };

    }
    private void addTerens() {
        URL url = getClass().getResource("/images/mud.jpeg");
        Image mudImg = new Image(url.toString());

        final Rectangle mud0 = new Rectangle(40, 40, 50, 50);
        mud0.setFill(new ImagePattern(mudImg));
        root.getChildren().add(mud0);

        final Rectangle mud1 = new Rectangle(530, 500, 50, 50);
        mud1.setFill(new ImagePattern(mudImg));
        root.getChildren().add(mud1);

        muds = new Rectangle[] {
                mud0,
                mud1
        };

        URL url1 = getClass().getResource("/images/ice.jpeg");
        Image iceImg = new Image(url1.toString());

        final Rectangle ice0 = new Rectangle(220, 220, 50, 50);
        ice0.setFill(new ImagePattern(iceImg));
        root.getChildren().add(ice0);

        final Rectangle ice1 = new Rectangle(340, 340, 50, 50);
        ice1.setFill(new ImagePattern(iceImg));
        root.getChildren().add(ice1);

        ices = new Rectangle[] {
                ice0,
                ice1
        };

    }


    @Override
    public void start ( Stage stage ) throws IOException {
        Image image = this.userBackground;
        ImagePattern background = new ImagePattern(image);
        Scene scene = new Scene ( this.root, Game.WINDOW_WIDTH, WINDOW_HEIGHT, background );
        URL url = getClass().getResource("/images/fence.jpg");
        Image fenceImg = new Image(url.toString());


        final Rectangle left = new Rectangle(0, 0, 20, WINDOW_HEIGHT);
        left.setFill(new ImagePattern(fenceImg));
        root.getChildren().add(left);

        final Rectangle right = new Rectangle(WINDOW_WIDTH-20, 0, 20, WINDOW_HEIGHT);
        right.setFill(new ImagePattern(fenceImg));
        root.getChildren().add(right);

        final Rectangle top = new Rectangle(0, 0, WINDOW_WIDTH,40);
        top.setFill(new ImagePattern(fenceImg));
        root.getChildren().add(top);

        final Rectangle bottom = new Rectangle(0, WINDOW_HEIGHT-20, WINDOW_WIDTH, 20);
        bottom.setFill(new ImagePattern(fenceImg));
        root.getChildren().add(bottom);

        addStillObstacles();

        Translate playerPosition = new Translate (
                Game.WINDOW_WIDTH / 2 - Game.PLAYER_WIDTH / 2,
                Game.WINDOW_HEIGHT - Game.PLAYER_HEIGHT
        );

        this.player = new Player (
                Game.PLAYER_WIDTH,
                Game.PLAYER_HEIGHT,
                playerPosition,
                playerType
        );

        this.root.getChildren ( ).addAll ( this.player );

        this.addFourHoles ( );

        scene.addEventHandler (
                MouseEvent.MOUSE_MOVED,
                mouseEvent -> this.player.handleMouseMoved (
                        mouseEvent,
                        Game.PLAYER_MIN_ANGLE_OFFSET,
                        Game.PLAYER_MAX_ANGLE_OFFSET
                )
        );

        scene.addEventHandler ( MouseEvent.ANY, this );


        scene.addEventFilter(KeyEvent.KEY_PRESSED, event->{
            if (event.getCode() == KeyCode.SPACE) {
                this.root.getChildren ( ).remove ( this.ball );
                this.ball = null;
            }
        });
        AtomicBoolean isInHole = new AtomicBoolean(false);

        Timer timer = new Timer (
                deltaNanoseconds -> {
                    double deltaSeconds = ( double ) deltaNanoseconds / Game.NS_IN_S;
                    if ( this.ball != null ) {
                        double dampFact = Game.BALL_DAMP_FACTOR;
                        if (isOnIce(this.ball)) {
                            dampFact += 0.055;
                        }
                        if (isOnMud(this.ball)) {
                            dampFact -= 0.055;
                        }
                        if (dampFact <= 0.45) dampFact = 0.45;

                        boolean stopped = this.ball.update(
                                deltaSeconds,
                                20,
                                Game.WINDOW_WIDTH - 20,
                                40,
                                Game.WINDOW_HEIGHT - 20,
                                dampFact,
                                Game.MIN_BALL_SPEED,
                                obstacles
                        );

                        isInHole.set(Arrays.stream(this.holes).anyMatch(hole -> hole.handleCollision(this.ball)));

                        if (stopped || isInHole.get()) {
                            this.root.getChildren().remove(this.ball);
                            this.ball = null;
                        }
                    }
                }
        );
        timer.start ( );


        final Rectangle rectBasicTimeline = new Rectangle(0, 0, WINDOW_WIDTH, 20);
        rectBasicTimeline.setFill(Color.RED);

        final Timeline timeline = new Timeline();
        timeline.setCycleCount(1);

        final KeyValue kv = new KeyValue(rectBasicTimeline.xProperty(), -WINDOW_WIDTH);
        final KeyFrame kf = new KeyFrame(Duration.millis(GAME_TIME_MAX*1000), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();

        root.getChildren().add(rectBasicTimeline);

        final int[] timeSeconds = {GAME_TIME_MAX};

        // update timerLabel
        clock.setText(String.valueOf(timeSeconds[0]));
        timelineClock = new Timeline();
        timelineClock.setCycleCount(Timeline.INDEFINITE);
        timelineClock.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        new EventHandler() {
                            @Override
                            public void handle(Event event) {
                                timeSeconds[0]--;
                                // update timerLabel
                                clock.setText(
                                        String.valueOf(timeSeconds[0]));
                                if (timeSeconds[0] <= 0) {
                                    root.getChildren ( ).remove ( ball );
                                    ball = null;
                                    gameOver = true;
                                    timelineClock.stop();
                                }
                            }
                        }));
        timelineClock.playFromStart();
        this.root.getChildren().add(clock);

        addTerens();

        numOfTries = new Label("Preostali broj pokusaja: " + numOfTried);
        numOfTries.setLayoutX(30);
        numOfTries.setLayoutY(WINDOW_HEIGHT-80);
        this.root.getChildren().add(numOfTries);

        numOfPoens = new Label("Poeni: " + poens);
        numOfPoens.setLayoutX(30);
        numOfPoens.setLayoutY(WINDOW_HEIGHT-60);
        this.root.getChildren().add(numOfPoens);

        scene.setCursor ( Cursor.DEFAULT );
        stage.setTitle ( "Golfer" );
        stage.setResizable ( false );
        stage.setScene ( scene );
        stage.show ( );
    }

    public static void main ( String[] args ) {
        launch ( );
    }
    private final Timeline timelineMouse = new Timeline();
    private Rectangle rectBasicTimeline1;

    @Override public void handle ( MouseEvent mouseEvent ) {
        if ( mouseEvent.getEventType ( ).equals ( MouseEvent.MOUSE_PRESSED ) && mouseEvent.isPrimaryButtonDown ( ) ) {
            this.time = System.currentTimeMillis ( );
            if (this.ball == null) {
                numOfTried--;
            }
            if (numOfTried>=0 && !gameOver) {
                rectBasicTimeline1 = new Rectangle(0, WINDOW_HEIGHT, 30, 100);
                rectBasicTimeline1.setFill(Color.RED);
                timelineMouse.setCycleCount(1);

                final KeyValue kv = new KeyValue(rectBasicTimeline1.yProperty(), 600);
                final KeyFrame kf = new KeyFrame(Duration.millis(3000), kv);
                timelineMouse.getKeyFrames().add(kf);
                timelineMouse.play();

                root.getChildren().add(rectBasicTimeline1);
            }
        } else if ( mouseEvent.getEventType ( ).equals ( MouseEvent.MOUSE_RELEASED ) ) {
            if (numOfTried>=0 && !gameOver) {
                timelineMouse.stop();
                if (numOfTried>=0 && !gameOver)
                    numOfTries.setText("Preostali broj pokusaja: " + numOfTried);
                root.getChildren().remove(rectBasicTimeline1);
                if (this.time != -1 && this.ball == null) {
                    double value = (System.currentTimeMillis() - this.time) / Game.MS_IN_S;
                    double deltaSeconds = Utilities.clamp(value, 0, Game.MAXIMUM_HOLD_IN_S);

                    double ballSpeedFactor = deltaSeconds / Game.MAXIMUM_HOLD_IN_S * Game.MAXIMUM_BALL_SPEED * speedMult;

                    Translate ballPosition = this.player.getBallPosition();
                    Point2D ballSpeed = this.player.getSpeed().multiply(ballSpeedFactor);

                    this.ball = new Ball(Game.BALL_RADIUS, ballPosition, ballSpeed);
                    this.root.getChildren().addAll(this.ball);
                }
                this.time = -1;
            }
        }
    }
    public boolean isOnIce(Ball ball) {
        double x0 = ices[0].getX();
        double y0 = ices[0].getY();
        double x1 = x0 + ices[0].getWidth();
        double y1 = y0 + ices[0].getHeight();

        double x01 = ices[1].getX();
        double y01 = ices[1].getY();
        double x11 = x01 + ices[1].getWidth();
        double y11 = y01 + ices[1].getHeight();

        isIce = false;

        if ((ball.getBoundsInParent().getCenterX() > x0) && (ball.getBoundsInParent().getCenterX() < x1) && (ball.getBoundsInParent().getCenterY() > y0) && (ball.getBoundsInParent().getCenterY() < y1)) {
            isIce = true;
        }
        if ((ball.getBoundsInParent().getCenterX() >= x01) && (ball.getBoundsInParent().getCenterX() <= x11) && (ball.getBoundsInParent().getCenterY() >= y01) && (ball.getBoundsInParent().getCenterY() <= y11)) {
            isIce = true;
        }
        return isIce;
    }

    public boolean isOnMud(Ball ball) {
        double x0 = muds[0].getX();
        double y0 = muds[0].getY();
        double x1 = x0 + muds[0].getWidth();
        double y1 = y0 + muds[0].getHeight();

        double x01 = muds[1].getX();
        double y01 = muds[1].getY();
        double x11 = x01 + muds[1].getWidth();
        double y11 = y01 + muds[1].getHeight();

        Boolean isMudOld = isMud;
        isMud = false;
        if ((ball.getBoundsInParent().getCenterX() >= x0) && (ball.getBoundsInParent().getCenterX() <= x1) && (ball.getBoundsInParent().getCenterY() >= y0) && (ball.getBoundsInParent().getCenterX() <= y1)) {
            isMud = true;
        }
        if ((ball.getBoundsInParent().getCenterX() >= x01) && (ball.getBoundsInParent().getCenterX() <= x11) && (ball.getBoundsInParent().getCenterY() >= y01) && (ball.getBoundsInParent().getCenterX() <= y11)) {
            isMud = true;
        }
        if(isMudOld && isMud) {
            isMud = false;
        }
        if (isMud) System.out.print("width "+muds[0].getWidth()  + "  y0" + y0 + "  y1 "+y1);
        return isMud;
    }
}