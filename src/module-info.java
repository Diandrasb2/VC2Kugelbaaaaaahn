module VC2Kugelbaaaaaahn {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	
	opens gui to javafx.graphics, javafx.fxml;
}
