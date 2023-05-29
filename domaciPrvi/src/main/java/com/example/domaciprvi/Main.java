package com.example.domaciprvi;

import com.example.domaciprvi.objects.Ball;
import com.example.domaciprvi.objects.Hole;
import com.example.domaciprvi.objects.Player;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class Main extends Application implements EventHandler<MouseEvent> {
	private static final double WINDOW_WIDTHM  = 600;
	private static final double WINDOW_HEIGHTM = 700;
	private Group root;



	@Override
	public void start ( Stage stage ) throws IOException {
		this.root = new Group();
		Scene scene = new Scene ( this.root, Main.WINDOW_WIDTHM, WINDOW_HEIGHTM, Color.LIGHTBLUE );

		final ToggleGroup playerChoice = new ToggleGroup();

		RadioButton rb1 = new RadioButton("Cannon 1");
		rb1.setToggleGroup(playerChoice);
		rb1.setSelected(true);
		rb1.setLayoutY(100);

		RadioButton rb2 = new RadioButton("Cannon 2");
		rb2.setToggleGroup(playerChoice);
		rb2.setLayoutY(200);

		this.root.getChildren ( ).addAll ( rb1 );
		this.root.getChildren ( ).addAll ( rb2 );

		Button grass = new Button("GRASS");
		grass.setLayoutX(Main.WINDOW_WIDTHM/3-Main.WINDOW_WIDTHM/6);
		grass.setLayoutY(Main.WINDOW_HEIGHTM/2);

		grass.setOnAction(e -> {
			URL url = getClass().getResource("/images/grass.jpg");
			Image image = new Image(url.toString());
			Game game = null;
			try {
				game = new Game(image, stage, rb1.isSelected());
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		});

		Button water = new Button("WATER");
		water.setLayoutX(Main.WINDOW_WIDTHM/3+50);
		water.setLayoutY(Main.WINDOW_HEIGHTM/2-water.getHeight());

		water.setOnAction(e -> {
			URL url = getClass().getResource("/images/water.jpeg");
			Image image2 = new Image(url.toString());
			Game game = null;
			try {
				game = new Game(image2, stage,rb1.isSelected());
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		});

		Button sand = new Button("SAND");
		sand.setLayoutX(Main.WINDOW_WIDTHM-Main.WINDOW_WIDTHM/3);
		sand.setLayoutY(Main.WINDOW_HEIGHTM/2);

		sand.setOnAction(e -> {
			URL url = getClass().getResource("/images/sand.jpeg");
			Image image = new Image(url.toString());
			Game game = null;
			try {
				game = new Game(image, stage, rb1.isSelected());
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		});

		this.root.getChildren ( ).addAll ( grass );
		this.root.getChildren ( ).addAll ( water );
		this.root.getChildren ( ).addAll ( sand );

		scene.addEventHandler ( MouseEvent.ANY, this );
		scene.setCursor ( Cursor.HAND );
		
		stage.setTitle ( "Menu" );
		stage.setResizable ( false );
		stage.setScene ( scene );
		stage.show ( );
	}

	@Override
	public void handle(MouseEvent mouseEvent) {

	}
}