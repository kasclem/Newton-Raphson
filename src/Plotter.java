import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

import java.util.function.Function;

// Java 8 code
public class Plotter extends Application {
	final static int zoom = 10;
	Stage stage;
	private static Axes axes;
	private static Plot plot;
	static StackPane layout;
	static String expression;
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage stage) {
	}

	public static StackPane getPane(String expression, String tanLine) {
		Plotter.expression = expression;
		axes = new Axes(
				600, 600,
				-zoom, zoom, zoom/10,
				-zoom, zoom, zoom/10
				);



		plot = new Plot(
				new Function<Double, Double>() {

					@Override
					public Double apply(Double t) {
						return Methods.evaluate(expression, t);
					}
				},
				-zoom, zoom, 0.001,
				axes, Color.BLACK
				);

//		Plot plotLineTangent = new Plot(
//				new Function<Double, Double>() {
//
//					@Override
//					public Double apply(Double t) {
//						return Methods.evaluate(tanLine, t);
//					}
//				},
//				-zoom, zoom, 0.2,
//				axes, Color.BLUE
//				);



		layout = new StackPane(plot);		
		return layout;
	}

	static class Axes extends Pane {
		private NumberAxis xAxis;
		private NumberAxis yAxis;

		public Axes(
				int width, int height,
				double xLow, double xHi, double xTickUnit,
				double yLow, double yHi, double yTickUnit
				) {
			setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
			setPrefSize(width, height);
			setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);

			xAxis = new NumberAxis(xLow, xHi, xTickUnit);
			xAxis.setSide(Side.BOTTOM);
			xAxis.setMinorTickVisible(false);
			xAxis.setPrefWidth(width);
			xAxis.setLayoutY(height / 2);

			yAxis = new NumberAxis(yLow, yHi, yTickUnit);
			yAxis.setSide(Side.LEFT);
			yAxis.setMinorTickVisible(false);
			yAxis.setPrefHeight(height);
			yAxis.layoutXProperty().bind(
					Bindings.subtract(
							(width / 2) + 1,
							yAxis.widthProperty()
							)
					);

			getChildren().setAll(xAxis, yAxis);
		}

		public NumberAxis getXAxis() {
			return xAxis;
		}

		public NumberAxis getYAxis() {
			return yAxis;
		}

		public void axisUp() {
			xAxis.setLayoutY(xAxis.getLayoutY()+10);
			yAxis.setLayoutY(yAxis.getLayoutY()+10);
		}

		public void zoom(double zoomFactor) {
			this.setScaleX(this.getScaleX()*zoomFactor);
			this.setScaleY(this.getScaleY()*zoomFactor);
		}
	}

	static class Plot extends Pane {
		Function f;
		double xMin;
		double xMax;
		double xInc;
		Axes axes;
		Color strokeColor;
		
		public Plot(
				Function<Double, Double> f,
				double xMin, double xMax, double xInc,
				Axes axes, Color strokeColor
				) {
			Path path = new Path();
			path.setStroke(strokeColor);
			path.setStrokeWidth(0.5);
			
			path.setClip(
					new Rectangle(
							0, 0, 
							axes.getPrefWidth(), 
							axes.getPrefHeight()
							)
					);

			double x = xMin;
			double y = f.apply(x);

			path.getElements().add(
					new MoveTo(
							mapX(x, axes), mapY(y, axes)
							)
					);

			x += xInc;
			while (x < xMax) {
				y = f.apply(x);

				path.getElements().add(
						new LineTo(
								mapX(x, axes), mapY(y, axes)
								)
						);

				x += xInc;
			}

			setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
			setPrefSize(axes.getPrefWidth(), axes.getPrefHeight());
			setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);

			getChildren().setAll(axes, path);
		}

		private double mapX(double x, Axes axes) {
			double tx = axes.getPrefWidth() / 2;
			double sx = axes.getPrefWidth() / 
					(axes.getXAxis().getUpperBound() - 
							axes.getXAxis().getLowerBound());

			return x * sx + tx;
		}

		private double mapY(double y, Axes axes) {
			double ty = axes.getPrefHeight() / 2;
			double sy = axes.getPrefHeight() / 
					(axes.getYAxis().getUpperBound() - 
							axes.getYAxis().getLowerBound());

			return -y * sy + ty;
		}

		public void plotUp() {			
			this.setTranslateY(this.getTranslateY()+10);
		}

		public void zoom(double zoomFactor) {
			this.setScaleX(this.getScaleX()*zoomFactor);
			this.setScaleY(this.getScaleY()*zoomFactor);
		}

		public void plotDown() {
			this.setTranslateY(this.getTranslateY()-10);
		}

		

		public void plotLeft() {
			this.setTranslateX(this.getTranslateX()-10);
		}
		public void plotRight() {
			this.setTranslateX(this.getTranslateX()+10);
		}
	}

	public static void up() {
		//axes.axisUp();
		plot.plotUp();
	}

	public static void zoom(double zoomFactor) {
		//axes.zoom(zoomFactor);
		plot.zoom(zoomFactor);
	}

	public static void down() {
		plot.plotDown();
	}

	public static void left() {
		plot.plotLeft();
	}
	public static void right() {
		plot.plotRight();
	}
}
