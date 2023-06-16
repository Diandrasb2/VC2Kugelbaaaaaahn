package gui;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.RotateTransition;
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
	private Circle Kugel2;
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
	@FXML
	private Slider rotateE;
	@FXML
	private Button drehenButton;
	@FXML
	private Rectangle spinner;
	@FXML
	private Line lineEbene;

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
			if(getDistance(line) <= radius) {
				System.out.println("Mögliche Kollision");

				//Testen ob Lotfußpunkt auf der Linie oder nur auf der Höhe der Linie liegt
				if(inLine(getBasePoint(new Point2D(Kugel.getLayoutX(), Kugel.getLayoutY()), line), line)) {
					System.out.println("in line --> Tatsächliche Kollision");
					changeDirection(line);
				}
			}
		});

		//Berechnung für die nächste Bewegung
		vx = vx + 0 * dT; 
		vy = vy + gravityValue * dT;

		//Berechnung der Position der Kugel
		sx = sx + vx * dT + 0.5 * 1 * Math.pow(dT, 2);
		sy = sy + vy * dT + 0.5 * gravityValue * Math.pow(dT, 2);

		//Anzeige der Geschwindigkeit
		vxAnzeige.setText("x-Richtung: " + (int) vx);
		vyAnzeige.setText("y-Richtung: " + (int) vy);
	}


	// Startknopf betätigen --> Kugel fängt an sich zu bewegen
	@FXML
	public void onStart() {

		//inLine(new Point2D(2, 3), new Line(1, 1, 3, 3));
		//inLine(new Point2D(2, 2), new Line(1, 1, 3, 3));

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

		//Linie Ebene
		//System.out.println("StartX: " + lineEbene.getLayoutX() + " StartY: " + lineEbene.getLayoutY() 
		//+ " EndX: " + lineEbene.getEndX() + " EndY: " + lineEbene.getEndY());

		//Testlinie
		lines.add(new Line(30, 200, 200 , 200));


		//LineEbene Linie
		//lines.add(new Line(lineEbene.getStartX(), lineEbene.getStartY(), lineEbene.getEndX(), lineEbene.getEndY()));


		//Ränder der geraden Ebene
		//lines.add(new Line(30, 300, 330, 300)); //oben links --> oben rechts
		//lines.add(new Line(330, 300, 330, 350)); // oben rechts --> unten rechts
		//lines.add(new Line(330, 350, 30, 350)); // unten rechts --> unten links
		//lines.add(new Line(30, 350, 30, 300)); //unten links --> oben links


		//Animation
		GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(dT*1000),e -> movement(graphicsContext, gravityValue * 350)));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();


		//Rotation of Spinner
		RotateTransition spin = new RotateTransition();
		spin.setByAngle(360);
		spin.setCycleCount(500);
		spin.setDuration(Duration.millis(1000));
		spin.setNode(spinner);
		spin.play();
	}


	//Change Position Button (Drag & Drop)
	@FXML
	public void onCP() {
		System.out.println("Change Position activated.");
		makeDraggable(Kugel);
		makeDraggable(Kugel2);
		makeDraggable(lineEbene);

	}

	//Rotation Slider auslesen und Wert anzeigen
	@FXML
	public void onRotate() {
		int angle = (int) rotateE.getValue();
		System.out.println("Angle changed to: " + angle);
		drehenButton.setText("Drehung auf " + angle + "° einstellen.");
	}


	//Rotation ausführen über Button
	@FXML
	public void onChangeRotation() {
		onRotate();
		lineEbene.setRotate(rotateE.getValue());
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

	/**
	//Kollisionserkennung der Ebene (noch mit festen Werten)
	public boolean checkCollision(Circle Kugel, Rectangle Ebene1) {

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
	 */
	//Prüfen, ob sich ein Punkt zwischen dem Start- und Endpunkt einer Linie befindet
	public boolean inLine(Point2D point, Line line) {

		//Richtungsvektoren der Linie (X und Y)
		double richtungX = line.getEndX() - line.getStartX();
		double richtungY = line.getEndY() - line.getStartY();

		//Prüfen, ob Punkt != (ungleich) Startpunkt der Linie ist, da in diesem Falle der Punkt nicht auf der Linie ist (=False)
		if(richtungX == 0 && point.getX() != line.getStartX()) {
			return false;
		}

		if(richtungY == 0 && point.getY() != line.getStartY()) {
			return false;
		}

		//Prüfen, ob Punkt = (gleich) Startpunkt der Linie ist, da in diesem Falle der Punkt auf der Linie liegen könnte
		if(richtungX == 0 && point.getX() == line.getStartX()) {
			double p2 = (point.getY() - line.getStartY()) / richtungY; //Parameter der Geradengleichung ausrechnen (für Y)
			return 0 <= p2 && p2 <= 1; //Prüfen ob der Parameter zwischen 0 und 1 liegt (=Punkt liegt in der Linie)
		}

		if(richtungY == 0 && point.getY() == line.getStartY()) {
			double p1 = (point.getX() - line.getStartX()) / richtungX; //Parameter der Geradengleichung ausrechnen (für X)
			return 0 <= p1 && p1 <= 1; //Prüfen ob der Parameter zwischen 0 und 1 liegt (=Punkt liegt in der Linie)
		}

		//Falls alle if-Abfragen nicht zutreffen: 1. Parameter wird ausgerechnet
		double p1 = (point.getX() - line.getStartX()) / richtungX;
		double p2 = (point.getY() - line.getStartY()) / richtungY;

		//System.out.println("p1: " + p1 + " p2: " + p2);

		//Prüfen ob der Parameter für X und Y gleich sind und ob er zwischen 0 und 1 liegt
		if(p1 == p2 && 0 <= p1 && p1 <= 1) {
			return true;
			//System.out.println("In Line True");
		}else {
			return false;
			//System.out.println("In Line False");
		}
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