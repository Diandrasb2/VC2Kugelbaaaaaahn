package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;

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
	private Slider StartBWSlider;
	@FXML
	private Label GravityLabel;
	@FXML
	private Label StartBWLabel;
	@FXML
	private Rectangle Ebene1;
	@FXML
	private Button StartButton;


	//double KugelX = Kugel.getLayoutX();
	//double KugelY = Kugel.getLayoutY();


	// Konstante Beschleunigung
	//double v0 = 0;
	//double a;
	//double t;

	//double v = v0 + a * t;

	//double s0 = 0;
	//double s = s0 + v0 * t + 0.5 * a * t * t;

	//Circle Kugel = new Circle();
	//TranslateTransition move = new TranslateTransition(Duration.millis(2000), Kreis);



	//Gravity = gravityValue
	@FXML
	public void onDragDone() {
		double gravityValue = GravitySlider.getValue();
		System.out.println("Gravity Value: " + gravityValue);
		GravityLabel.setText(gravityValue + " m/s²");
	}


	@FXML
	public void onMouseReleased() {
		double StartBWValue = StartBWSlider.getValue();
		System.out.println("Startbewegung Value: " + StartBWValue);
		StartBWLabel.setText(StartBWValue + " m/s");
	}

	public void movement() {
		double gravityValue = GravitySlider.getValue();
		for (int b = 1; b <= 10; b++) {
			double KugelPositionY = Kugel.getLayoutY() + gravityValue;
			Kugel.setLayoutY(KugelPositionY);
		}
	}

	
		@FXML
		public void onStart() {
			System.out.println("Button pressed.");
			movement();

			//double KugelGr = Kugel.getLayoutY() - gravityValue;
			//Kugel.setLayoutY(KugelGr);

		}
	}



	//	Translate translate = new Translate();
	//	Kugel.getTransforms();




