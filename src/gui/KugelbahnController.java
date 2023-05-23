package gui;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
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
	@FXML
	private Button cpButton;
	@FXML
	private Label vxAnzeige;
	@FXML
	private Label vyAnzeige;

	Canvas canvas;

	double KugelPositionY;

	double dT = 0.025;	//delta T
	double t;	

	// Liste von Linien vom Fenster wird erstellt
	ArrayList<Line> lines = new ArrayList<Line>();


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

	//Geschwindigkeit v
	double vx;
	double vy;

	// Startposition bzw Position der Kugel
	double sx;
	double sy;

	//Radius Kugel
	double radius = 36;

	//Kugel bewegt sich
	public void movement(GraphicsContext graphicsContext, double gravityValue) {

		//Position wird ausgegeben
		Kugel.setLayoutX(sx);
		Kugel.setLayoutY(sy);


		//Bei einer Kollision wird die Richtung der Kugel geändert
		lines.forEach((line)-> {
			//System.out.println(getDistance(line));
			if(getDistance(line) <= radius) {
				System.out.println("Kollision");
				changeDirection(line);
			}
		});

		//Berechnung für die nächste Bewegung
		vx = vx + 0 * dT; 
		vy = vy + gravityValue * dT;

		//Berechnung der Position der Kugel
		sx = sx + vx * dT + 0.5 * 1 * Math.pow(dT, 2);
		sy = sy + vy * dT + 0.5 * gravityValue * Math.pow(dT, 2);

		//Anzeige der Geschwindigkeit
		vxAnzeige.setText("x-Richtung: " + vx);
		vyAnzeige.setText("y-Richtung: " + vy);


		/** ---Alte Kollisionserkennung der Wände---
		if(checkCollision(Kugel, Ebene1)) {
			vy = -vy;			
			//System.out.println("Kugel bewegt sich mit Geschwindigkeit " + vy  + " in Y-Richtung");
		}

		else if(Kugel.getLayoutY() >= 614) {
			//System.out.println("Boden");
			vy = -500;
		}

		else if(Kugel.getLayoutX() >= 714) {
			//System.out.println("Rechte Wand");
			vx = -400;
		}

		else if(Kugel.getLayoutX() <= 36) {
			//System.out.println("Linke Wand");
			vx = 100;
		}

		if(in()) {
			Kugel.setLayoutX(sx);
			Kugel.setLayoutY(300 - 36);
			System.out.println(vx + " " + vy);
		}*/
	}

	// Startknopf betätigen --> Kugel fängt an sich zu bewegen
	@FXML
	public void onStart() {
		canvas = new Canvas(750, 650);
		double gravityValue = GravitySlider.getValue();
		System.out.println("Button pressed.");

		// Startgeschwindigkeit
		vx = 300; 
		vy = StartVSlider.getValue();

		//Position der Kugel
		sx = Kugel.getLayoutX();
		sy = Kugel.getLayoutY();

		// Ränder des Feldes
		lines.add(new Line(0, 0, 750, 0)); //oben links --> oben rechts
		lines.add(new Line(750, 0, 750, 650)); // oben rechts --> unten rechts
		lines.add(new Line(750, 650, 0, 650)); // unten rechts --> unten links
		lines.add(new Line(0, 650, 0, 0)); //unten links --> oben links

		//Animation
		GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(dT*1000),e -> movement(graphicsContext, gravityValue * 350)));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}

	//Change Position Button (Drag & Drop)
	@FXML
	public void onCP() {
		System.out.println("Change Position activated.");
		makeDraggable(Kugel);
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

	//Kollisionserkennung der Ebene (noch mit festen Werten)
	public boolean checkCollision(Circle Kugel, Rectangle Ebene1) {
		//if(Kugel.getBoundsInParent().intersects(Ebene1.getBoundsInParent())) {
		//System.out.println("Kollision");
		//}

		//Kollision obere Kante
		if(Kugel.getLayoutX() - radius >= 30 
				&& Kugel.getLayoutX() - radius <= 330 
				&& Kugel.getLayoutY() - radius >= 300 
				&& Kugel.getLayoutY() - radius <= 350
				&& vy < 0) {
			//System.out.println("Kollision");
			return true;
		}

		//Kollision untere Kante
		if(Kugel.getLayoutX() + radius >= 30 
				&& Kugel.getLayoutX() + radius <= 330 
				&& Kugel.getLayoutY() + radius >= 300 
				&& Kugel.getLayoutY() + radius <= 350
				&& vy >= 0) {
			//System.out.println("Kollision");
			return true;
		}
		return false;
	}

	//Prüfen ob die Kugel in die Ebene eindringt
	public boolean in() {

		//Obere Kante
		if(
				Kugel.getLayoutX() - radius >= 30 
				&& Kugel.getLayoutX() - radius <= 330 
				&& Kugel.getLayoutY() - radius >= 300 
				&& Kugel.getLayoutY() - radius <= 350
				) {
			return true;
		}

		//Untere Kante
		if(
				Kugel.getLayoutX() + radius >= 30 
				&& Kugel.getLayoutX() + radius <= 330 
				&& Kugel.getLayoutY() + radius >= 300 
				&& Kugel.getLayoutY() + radius <= 350
				) {
			return true;

		}
		return false;
	}

	//Vektorlänge berechnen
	public double getVectorLength(double x, double y) {
		return Math.sqrt(x*x + y*y);
	}

	//Lotfußpunkt berechnen
	public Point2D getBasePoint(Point2D point, Line line) {
		Point2D richtungsvektor = new Point2D(line.getEndX() - line.getStartX(), line.getEndY() - line.getStartY());
		//System.out.println("Richtungsvektor: " + richtungsvektor);
		Point2D ortsvektor = new Point2D(line.getStartX() - point.getX(), line.getStartY() - point.getY());
		//System.out.println("Ortsvektor: " + ortsvektor);

		double skalarRichtung = richtungsvektor.dotProduct(richtungsvektor);
		//System.out.println("Skalarrichtung: " + skalarRichtung);
		double skalarOrt = ortsvektor.dotProduct(richtungsvektor);
		//System.out.println("Skalar Ort: " + skalarOrt);

		double scale = skalarRichtung / (-1 * skalarOrt);

		return new Point2D(line.getStartX() + richtungsvektor.getX() / scale, line.getStartY() + richtungsvektor.getY() / scale);
	}

	//Distanz zwischen Kugel und Ebene berechnen
	public double getDistance(Line line) {
		Point2D basepoint = getBasePoint(new Point2D(Kugel.getLayoutX(), Kugel.getLayoutY()), line);
		return getVectorLength(basepoint.getX() - Kugel.getLayoutX(), basepoint.getY() - Kugel.getLayoutY());
	}

	// Kollisionshandling: Wände 
	public void changeDirection(Line line) {
		Point2D basepoint = getBasePoint(new Point2D(Kugel.getLayoutX(), Kugel.getLayoutY()), line);

		Line normal = new Line(Kugel.getLayoutX(), Kugel.getLayoutY(), basepoint.getX(), basepoint.getY());

		Point2D corner = getBasePoint(new Point2D(Kugel.getLayoutX() + vx, Kugel.getLayoutY() + vy), normal);
		Point2D orthogonal = new Point2D(Kugel.getLayoutX() - corner.getX(), Kugel.getLayoutY() - corner.getY());
		Point2D parallel = new Point2D(Kugel.getLayoutX() + vx - corner.getX(), Kugel.getLayoutY() + vy - corner.getY());

		// Addition Vektor x und y
		Point2D sum = parallel.add(orthogonal);

		vx = sum.getX();
		vy = sum.getY();
	}

}