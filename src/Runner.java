







import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Runner extends Application {
	static String exp = "sin(x)";
	public TextField txtFrom;
	TextField txtTo;
	TextField txtExp;
	Button btnPlot;
	Button btnCalculate;
	TableView<Row> tb;
	SplitPane root;
	@Override
	public void start(Stage stage) {
		root = new SplitPane();
		root.setBorder(new Border(new BorderStroke(null, null, null, null)));
		stage.setResizable(false);
		StackPane sp = Plotter.getPane(exp, Methods.getTangentFunction(exp, 1.5));		
		sp.setLayoutX(10);
		sp.setLayoutY(10);
		root.getItems().add(sp);
		initControls(root);
		Scene scene = new Scene(root, 1200, 620);
		keyHandle(scene);
		stage.setScene(scene);
		stage.show();		
	}

	private void keyHandle(Scene scene) {
		scene.setOnKeyPressed(e -> {
			switch(e.getCode()) {
			case UP: Plotter.up(); break;
			case DOWN: Plotter.down(); break;
			case LEFT: Plotter.left(); break;
			case RIGHT: Plotter.right(); break;
			default: e.consume(); return;
			}
		});
	}

	private void handleNavigationEvents(Button btnUp, Button btnDown, Button btnLeft, Button btnRight, Button btnZoomIn,
			Button btnZoomOut) {
		btnUp.setOnAction(e -> {
			Plotter.up();
		});
		btnZoomIn.setOnAction(e -> Plotter.zoom(1.1));
		btnZoomOut.setOnAction(e -> Plotter.zoom(0.9));
		btnDown.setOnAction(e -> Plotter.down());
		btnLeft.setOnAction(e -> Plotter.left());
		btnRight.setOnAction(e -> Plotter.right());
	}

	private void initControls(SplitPane root) {
		VBox vb = new VBox();
		GridPane grid = new GridPane();
		Button btnUp = new Button("⇧"), btnDown = new Button("⇩"), btnLeft = new Button("⇦"), btnRight = new Button("⇨"),
				btnZoomIn = new Button("+"), btnZoomOut = new Button("-");
		GridPane.setConstraints(btnUp, 1, 0);
		GridPane.setConstraints(btnLeft, 0, 1);
		GridPane.setConstraints(btnDown, 1, 2);
		GridPane.setConstraints(btnRight, 2, 1);
		GridPane.setConstraints(btnZoomIn, 0, 2);
		GridPane.setConstraints(btnZoomOut, 2, 2);
		grid.getChildren().addAll(btnUp, btnDown, btnLeft, btnRight, btnZoomIn, btnZoomOut);
		handleNavigationEvents(btnUp, btnDown, btnLeft, btnRight, btnZoomIn, btnZoomOut);
		grid.setAlignment(Pos.CENTER);
		vb.getChildren().add(grid);
		askForBracket(vb);
		expressionInput(vb);
		tableView(vb);
		calculatePlotHandler();
		root.getItems().add(vb);
		root.setDividerPositions(620d/1200);			
	}


	private void calculatePlotHandler() {
		btnCalculate.setOnAction(e -> {
			tb.setItems(generateList());
			root.getItems().set(0, Plotter.getPane(txtExp.getText(), ""));
		});
		btnPlot.setOnAction(e -> {
			root.getItems().set(0, Plotter.getPane(txtExp.getText(), ""));
		});
	}

	private void tableView(VBox vb) {
		tb = new TableView<>();
		final ObservableList<Row> data = null;
		
		TableColumn colN = new TableColumn("n");
        colN.setMinWidth(50);
        colN.setCellValueFactory(
                new PropertyValueFactory<>("nNR"));
 
        TableColumn colXn = new TableColumn("xn");
        colXn.setMinWidth(120);
        colXn.setCellValueFactory(
                new PropertyValueFactory<>("xn"));
 
        TableColumn colFxn = new TableColumn("f(xn)");
        colFxn.setMinWidth(115);
        colFxn.setCellValueFactory(
                new PropertyValueFactory<>("fxn"));
        
        TableColumn colfxPrimeN = new TableColumn("f'(xn)");
        colfxPrimeN.setMinWidth(115);
        colfxPrimeN.setCellValueFactory(
                new PropertyValueFactory<>("fPrimeXn"));        
        tb.setItems(data);
        tb.getColumns().addAll(colN, colXn, colFxn, colfxPrimeN);
        vb.getChildren().add(tb);        
	}
	
	private ObservableList<Row> generateList() {
		LinkedList<Row> list = new LinkedList<>();
		String exp = txtExp.getText();
		Row row = null;
		try {
		row = createRow(exp);
		}catch(NumberFormatException|NullPointerException e ){
			Alert alert = new Alert(AlertType.WARNING, "Invalid bracket input", null);
			alert.showAndWait();
			return null;
		}
		System.out.println(row);
		list.add(row);
		do {
			row = createRow(exp, list);
			System.out.println(row);
			list.add(row);
		}while(notStopping(list));
		
		return FXCollections.observableList(list);
	}

	private boolean notStopping(LinkedList<Row> list) {
		Row last = list.pollLast();
		Row last2 = list.pollLast();
		//return to list:
		list.addLast(last2); list.addLast(last);
		double xPrev = last2.getXn();
		double xCurr = last.getXn();
		return Math.abs(xCurr-xPrev)>= 0.0000001;
	}

	private Row createRow(String exp, LinkedList<Row> list) {
		Row prev = list.getLast();
		int nNR = prev.getNNR()+1;
		double xn = prev.getXn()  -  prev.getFxn()/prev.getFPrimeXn();
		double fxn = Methods.evaluate(exp, xn);
		double fPrimeXn = Methods.getSlope(exp, xn);
		return new Row(nNR, xn, fxn, fPrimeXn);
	}

	private Row createRow(String exp) {
		double from;
		double to;
		try {
		from = Double.parseDouble(txtFrom.getText());
		to = Double.parseDouble(txtTo.getText());
		}catch(NumberFormatException|NullPointerException e) {
			throw e;
		}
		System.out.println(from + " " + to);
		double mid = (double)(from+to)/2;
		System.out.println(mid);
		return new Row(0, mid, Methods.evaluate(exp, mid), Methods.getSlope(exp, mid));
	}

	public static class Row {
	    public final SimpleIntegerProperty nNR;
	    public final SimpleDoubleProperty xn;
	    public final SimpleDoubleProperty fxn;
	    public final SimpleDoubleProperty fPrimeXn;
	    
	    public int getNNR() {
			return nNR.get();
		}
	   

		public double getXn() {
			return xn.get();
		}

		public double getFxn() {
			return fxn.get();
		}

		public double getFPrimeXn() {
			return fPrimeXn.get();
		}

		public Row(int nNR, double xn, double fxn, double fPrimeXn) {
	    	this.nNR = new SimpleIntegerProperty(nNR);
	    	this.xn = new SimpleDoubleProperty(xn);
	    	this.fxn = new SimpleDoubleProperty(fxn);
	    	this.fPrimeXn = new SimpleDoubleProperty(fPrimeXn);
	    }


		@Override
		public String toString() {
			return String.format("%d %f %f %f", this.getNNR(), this.getXn(), this.getFxn(), this.getFPrimeXn());
		}
		
		
	 }

	private void expressionInput(VBox vb) {
		Label lblExpression = new Label("Enter f(x):");
		txtExp = new TextField("sin(x)");
		btnPlot = new Button("Plot");		
		btnCalculate = new Button("Calculate!");
		HBox hb = new HBox(btnCalculate, btnPlot);
		hb.setSpacing(20);
		vb.getChildren().addAll(lblExpression, txtExp, hb);
		
	}

	private void askForBracket(VBox vb) {
		Label lblFrom = new Label("Bracket from:"), lblTo = new Label("Bracket to:");
		txtFrom = new TextField();
		txtTo = new TextField();
		vb.getChildren().addAll(lblFrom, txtFrom, lblTo, txtTo);		
	}

	public static void main(String[] args) {
		launch(args);
	}
}
