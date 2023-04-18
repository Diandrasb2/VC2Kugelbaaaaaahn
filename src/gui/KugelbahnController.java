package gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class KugelbahnController {

	@FXML
	private Circle Kugel;
	@FXML
	private Pane MainBG;
	@FXML
	private Pane SecondBG;
	@FXML
	private Slider GravitySlider;
	@FXML
	private Slider StartVSlider;
	@FXML
	private Label GravityLabel;
	@FXML
	private Label StartVLabel;
	@FXML
	private Rectangle Ebene1;
	@FXML
	private Button StartButton;

	Canvas canvas;

	double KugelPositionY;

	double dT = 0.025;	//delta T
	double t;	
	double s = 50;
	double v;
	// a = gravityValue

	//Gravitation per Slider einstellen, auslesen und anzeigen
	@FXML
	public void onDragDone() {
		double gravityValue = GravitySlider.getValue();
		System.out.println("Gravity Value: " + gravityValue);
		GravityLabel.setText(gravityValue + " m/s²");
	}

	//Startgeschwindigkeit per Slider einstellen, auslesen und anzeigen
	@FXML
	public void onMouseReleased() {
		double StartVValue = StartVSlider.getValue();
		System.out.println("Startgeschwindigkeit Value: " + StartVValue);
		StartVLabel.setText(StartVValue + " m/s");
	}

	//Kugel bewegt sich nach unten bis zum Boden
	public void movement(GraphicsContext graphicsContext,double gravityValue) {
		for(int i = 1; i <= 100; i++) {
			v = StartVSlider.getValue();
			v = v + gravityValue * dT;
			s = s + v * dT + 0.5 * gravityValue * Math.pow(dT, 2);	// Strecke S
			if(Kugel.getLayoutY() >= 614) {
				System.out.println("Boden");
				v = -v;
			}else{
				Kugel.setLayoutY(s);
			}
		}
	}
	// Startknopf betätigen --> Kugel fängt an sich zu bewegen und man kann sie verschieben
	@FXML
	public void onStart() {
		makeDraggable(Kugel);
		canvas = new Canvas(750, 650);
		double gravityValue = GravitySlider.getValue();
		System.out.println("Button pressed.");

		GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(dT*1000),e -> movement(graphicsContext, gravityValue)));
		timeline.setCycleCount(Timeline.INDEFINITE);

		timeline.play();

	}

	// Drag and Drop Funktion
	private double startX;
	private double startY;

	private void makeDraggable(Node node) {
		node.setOnMousePressed(e -> {
			startX = e.getSceneX() - node.getLayoutX();
			startY = e.getSceneY() - node.getLayoutY();
		});

		node.setOnMouseDragged(e -> {
			node.setLayoutX(e.getSceneX() - startX);
			node.setLayoutY(e.getSceneY() - startY);
		});
	}

}




