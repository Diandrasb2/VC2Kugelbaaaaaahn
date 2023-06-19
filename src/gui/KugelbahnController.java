package gui;

import java.io.IOException;
import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import javafx.stage.Stage;

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
	private Line spinner;
	@FXML
	private Line lineEbene;
	@FXML
	private Button restartBT;

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

	//Geschwindigkeit v weiße Kugel
	double vx;
	double vy;

	//Geschwindigkeit v schwarze Kugel
	double vx2;
	double vy2;

	// Startposition bzw Position der weißen Kugel
	double sx;
	double sy;

	// Startposition bzw Position der schwarzen Kugel
	double sx2;
	double sy2;

	//Radius Kugel
	double radius = 36;
	
	//Masse
	double m1 = 5;
	double m2 = 7;

	//Kugel bewegt sich
	public void movement(GraphicsContext graphicsContext, double gravityValue) {

		//Position wird ausgegeben
		Kugel.setLayoutX(sx);
		Kugel.setLayoutY(sy);

		Kugel2.setLayoutX(sx2);
		Kugel2.setLayoutY(sy2);

		//Bei einer Kollision wird die Richtung der weißen Kugel geändert
		lines.forEach((line)-> {
			if(getDistance(line, Kugel) <= radius) {
				//System.out.println("Mögliche Kollision");

				//Testen ob Lotfußpunkt auf der Linie oder nur auf der Höhe der Linie liegt
				if(inLine(getBasePoint(new Point2D(Kugel.getLayoutX(), Kugel.getLayoutY()), line), line)) {
					System.out.println("in line --> Tatsächliche Kollision");
					changeDirection(line, Kugel);
				}
			}
		});


		//Bei einer Kollision wird die Richtung der schwarzen Kugel geändert
		lines.forEach((line)-> {
			if(getDistance(line, Kugel2) <= radius) {
				//System.out.println("Mögliche Kollision");

				//Testen ob Lotfußpunkt auf der Linie oder nur auf der Höhe der Linie liegt
				if(inLine(getBasePoint(new Point2D(Kugel2.getLayoutX(), Kugel2.getLayoutY()), line), line)) {
					System.out.println("2: in line --> Tatsächliche Kollision");
					changeDirection2(line, Kugel2);
				}
			}
		});

		if(circleCollision(Kugel, Kugel2)) {
			vx = circleCollisionV(m1, m2, vx, vx2);
			vy = circleCollisionV(m1, m2, vy, vy2);
			
			vx2 = circleCollisionV(m2, m1, vx2, vx);
			vy2 = circleCollisionV(m2, m1, vy2, vy);
			
			vx = -vx;
			vy = -vy;
			vx2 = -vx2;
			vy2 = -vy2;
			
			
		}
		
		//Berechnung für die nächste Bewegung
		vx = vx + 0 * dT; 
		vy = vy + gravityValue * dT;

		//Berechnung für die nächste Bewegung
		vx2 = vx2 + 0 * dT; 
		vy2 = vy2 + gravityValue * dT;

		//Berechnung der Position der weißen Kugel
		sx = sx + vx * dT + 0.5 * 1 * Math.pow(dT, 2);
		sy = sy + vy * dT + 0.5 * gravityValue * Math.pow(dT, 2);

		//Berechnung der Position der schwarzen Kugel
		sx2 = sx2 + vx2 * dT + 0.5 * 1 * Math.pow(dT, 2);
		sy2 = sy2 + vy2 * dT + 0.5 * gravityValue * Math.pow(dT, 2);

		//Anzeige der Geschwindigkeit
		vxAnzeige.setText("x-Richtung: " + (int) vx);
		vyAnzeige.setText("y-Richtung: " + (int) vy);
		
		vxAnzeige.setText("x-Richtung 2: " + (int) vx2);
		vyAnzeige.setText("y-Richtung 2: " + (int) vy2);
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
		
		// Startgeschwindigkeit
		vx2 = 300; 
		vy2 = StartVSlider.getValue();

		//Position der weißen Kugel
		sx = Kugel.getLayoutX();
		sy = Kugel.getLayoutY();

		//Position der schwarzen Kugel
		sx2 = Kugel2.getLayoutX();
		sy2 = Kugel2.getLayoutY();

		// Ränder des Feldes
		lines.add(new Line(0, 0, 750, 0)); //oben links --> oben rechts
		lines.add(new Line(750, 0, 750, 650)); // oben rechts --> unten rechts
		lines.add(new Line(750, 650, 0, 650)); // unten rechts --> unten links
		lines.add(new Line(0, 650, 0, 0)); //unten links --> oben links

		//Linie Ebene
		//lineEbene.setStartX(70);
		//lineEbene.setStartY(150);
		//lineEbene.setEndX(200);
		//lineEbene.setEndY(150);



		System.out.println("Linie -- StartX: " + lineEbene.getStartX() + " StartY: " + lineEbene.getStartY() 
		+ " EndX: " + lineEbene.getEndX() + " EndY: " + lineEbene.getEndY());

		System.out.println("Kugel -- X: " + Kugel.getLayoutX() + " Y: " + Kugel.getLayoutY());

		//Testlinie
		//lines.add(new Line(30, 200, 200 , 200));


		//LineEbene Linie (116, 45)
		lines.add(new Line(lineEbene.getStartX(), lineEbene.getStartY(), lineEbene.getEndX(), lineEbene.getEndY()));

		//lines.add(new Line(20.5, 61, 215.5 , 61));

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
		spin.setDuration(Duration.millis(2000));
		spin.setNode(spinner);
		spin.play();
	}


	//Change Position Button (Drag & Drop)
	@FXML
	public void onCP() {
		System.out.println("Change Position activated.");
		makeDraggable(Kugel);
		makeDraggable(Kugel2);
		makeLineDraggable(lineEbene);

	}

	//Rotation Slider auslesen und Wert anzeigen
	@FXML
	public void onRotate() {
		int angle = (int) rotateE.getValue();
		System.out.println("Angle changed to: " + angle);
		drehenButton.setText("Drehung auf " + angle + "° einstellen.");
		
		//System.out.println("LinieD -- StartX: " + lineEbene.getStartX() + " StartY: " + lineEbene.getStartY() 
		//+ " EndX: " + lineEbene.getEndX() + " EndY: " + lineEbene.getEndY());
		
		double LStartX = lineEbene.getStartX();
		double LStartY = lineEbene.getStartY();
		
		double LEndX = lineEbene.getEndX();
		double LEndY = lineEbene.getEndY();
		
		System.out.println("Before Rotation: StartX: " + lineEbene.getStartX() + " StartY: " + lineEbene.getStartY() + 
				" EndX: " + lineEbene.getEndX() + " EndY: " + lineEbene. getEndY());
		
		lineEbene.setRotate(rotateE.getValue());
		
		
		Point2D lineStart = lineEbene.localToParent(LStartX, LStartY);
		Point2D lineEnd = lineEbene.localToParent(LEndX, LEndY);
		
		lineEbene.setStartX(lineStart.getX());
		lineEbene.setStartY(lineStart.getY());
		
		lineEbene.setEndX(lineEnd.getX());
		lineEbene.setEndY(lineEnd.getY());
		
		System.out.println("After Rotation: StartX: " + lineEbene.getStartX() + " StartY: " + lineEbene.getStartY() + 
				" EndX: " + lineEbene.getEndX() + " EndY: " + lineEbene. getEndY());
		
	}


	//Rotation ausführen über Button
	@FXML
	public void onChangeRotation() {
		onRotate();
	}

	//Neustart
	@FXML
	public void onRestart() {
		//Fenster schließen
		Stage stage = (Stage) restartBT.getScene().getWindow();
		stage.close();

		//Fenster neu öffnen
		try {
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("Kugelbahn.fxml"));
			Scene scene = new Scene(root,1000,650);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setTitle("Kugelbahn");
			stage.setScene(scene);
			stage.show();
		} catch(IOException iOException) {
			System.out.println("Restart failed.");
		}
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
	
	private double LStartX;
	private double LStartY;
	private double LEndX;
	private double LEndY;
	
	private void makeLineDraggable(Line line) {
		line.setOnMousePressed(e -> {
			LStartX = e.getSceneX() - line.getStartX();
			LStartY = e.getSceneY() - line.getStartY();
			
			LEndX = e.getSceneX() - line.getEndX();
			LEndY = e.getSceneY() - line.getEndY();
		});
		
		line.setOnMouseDragged(e -> {
			line.setStartX(e.getSceneX() - LStartX);
			line.setStartY(e.getSceneY() - LStartY);
			line.setEndX(e.getSceneX() - LEndX);
			line.setEndY(e.getSceneY() - LEndY);
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
	public double getDistance(Line line, Circle circle) {
		Point2D basepoint = getBasePoint(new Point2D(circle.getLayoutX(), circle.getLayoutY()), line);

		return getVectorLength(basepoint.getX() - circle.getLayoutX(), basepoint.getY() - circle.getLayoutY());
	}
	
	/**
	 * Verhindern, dass die Kugel zittert (Überprüfung, ob die Distanz zwischen zwei Frames zu oder abnimmt)
	 
	public boolean distanceLower(Line line, Circle circle) {
		double richtungX = line.getEndX() - line.getStartX();
		double richtungY = line.getEndY() - line.getStartY();
		
		if(getDistance(line, circle) > getDistance(line, circle (Circle Position im nächsten Frame))) {
			return true;
		}
		return false;
	}

*/

	// Kollisionshandling: Wände 
	public void changeDirection(Line line, Circle circle) {
		Point2D basepoint = getBasePoint(new Point2D(circle.getLayoutX(), circle.getLayoutY()), line);

		Line normal = new Line(circle.getLayoutX(), circle.getLayoutY(), basepoint.getX(), basepoint.getY());

		Point2D corner = getBasePoint(new Point2D(circle.getLayoutX() + vx, circle.getLayoutY() + vy), normal);
		Point2D orthogonal = new Point2D(circle.getLayoutX() - corner.getX(), circle.getLayoutY() - corner.getY());
		Point2D parallel = new Point2D(circle.getLayoutX() + vx - corner.getX(), circle.getLayoutY() + vy - corner.getY());

		// Addition Vektor x und y
		Point2D sum = parallel.add(orthogonal);

		vx = sum.getX();
		vy = sum.getY();
	}
	
	// Kollisionshandling: Wände 
		public void changeDirection2(Line line, Circle circle) {
			Point2D basepoint = getBasePoint(new Point2D(circle.getLayoutX(), circle.getLayoutY()), line);

			Line normal = new Line(circle.getLayoutX(), circle.getLayoutY(), basepoint.getX(), basepoint.getY());

			Point2D corner = getBasePoint(new Point2D(circle.getLayoutX() + vx2, circle.getLayoutY() + vy2), normal);
			Point2D orthogonal = new Point2D(circle.getLayoutX() - corner.getX(), circle.getLayoutY() - corner.getY());
			Point2D parallel = new Point2D(circle.getLayoutX() + vx2 - corner.getX(), circle.getLayoutY() + vy2 - corner.getY());

			// Addition Vektor x und y
			Point2D sum = parallel.add(orthogonal);

			vx2 = sum.getX();
			vy2 = sum.getY();
		}
		
		// Prüfen, ob zwei Kugeln miteinander kollidieren
		public boolean circleCollision(Circle circle, Circle circle2) {
			double circleDistance = Math.sqrt(Math.pow(circle2.getLayoutX() - circle.getLayoutX(), 2) + Math.pow(circle2.getLayoutY() - circle.getLayoutY(), 2));
			
			if(circleDistance < circle.getRadius() + circle2.getRadius()) {
				System.out.println("Kugel Kollision");
				return true;
			} else {
				return false;
			}
		}
		
		public double circleCollisionV(double m1, double m2, double v1, double v2) {
			double newV = (m1 * v1 + m2 * (2 * v2 - v1)) / (m1 + m2);
			return newV;
		}
		
}