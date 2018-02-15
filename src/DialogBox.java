
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DialogBox extends Application {
	Label msg;
	
	public DialogBox(String msg) {
		this.msg = new Label(msg);
		start
	}
	public void start(Stage primaryStage) {	
		Scene scene = new Scene(msg, 200, 200);
		primaryStage.setScene(scene);
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		primaryStage.show();
	}
	
	

	public static void main(String[] args) {
		launch(args);
	}
}
