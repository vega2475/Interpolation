package com.example.interpolation;

import com.example.interpolation.math.InterpolatingLagrangePolynomial;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App extends Application {

    private final static int height = 900;
    private final static int width = 900;

    // Области значений
    private static double minX = -10;
    private static double maxX = 10;
    private static double minY = -10;
    private static double maxY = 10;
    private double lastX;
    private double lastY;
    private boolean isPanning = false;
    private NumberAxis xAxis;
    private NumberAxis yAxis;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("График функции");

        xAxis = new NumberAxis(minX, maxX, 1);
        yAxis = new NumberAxis(minY, maxY, 1);
        xAxis.setLabel("X");
        yAxis.setLabel("Y");

        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setPrefSize(width, height);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        List<XYChart.Data<Number, Number>> dataPoints = new ArrayList<>();


        List<Double> xValues = new ArrayList<>();
        List<Double> yValues = new ArrayList<>();

        scatterChart.setOnMouseClicked(event -> {
            handleMouseClick(event, series, xValues, yValues);
        });

        // Добавление точек в график
        series.getData().addAll(dataPoints);
        scatterChart.getData().add(series);

        // Обработка событий масштабирования и перемещения
        scatterChart.setOnScroll(this::handleScroll);
        scatterChart.setOnMousePressed(this::handleMousePressed);
        scatterChart.setOnMouseDragged(this::handleMouseDragged);
        scatterChart.setOnMouseReleased(this::handleMouseReleased);

        // Интерфейс сцены
        Pane chartPane = new Pane(scatterChart);
        Scene scene = new Scene(chartPane, width, height);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleScroll(ScrollEvent event) {
        double delta = event.getDeltaY();
        double zoomFactor = 1.1;

        // Изменение масштаба графика при прокрутке
        if (delta < 0) {
            minX /= zoomFactor;
            maxX /= zoomFactor;
            minY /= zoomFactor;
            maxY /= zoomFactor;
        } else {
            minX *= zoomFactor;
            maxX *= zoomFactor;
            minY *= zoomFactor;
            maxY *= zoomFactor;
        }

        // Обновление границ осей
        xAxis.setLowerBound(minX);
        xAxis.setUpperBound(maxX);
        yAxis.setLowerBound(minY);
        yAxis.setUpperBound(maxY);

        event.consume();
    }

    private void handleMousePressed(MouseEvent event) {
        //Начальные координаты, для реализации перемещения
        lastX = event.getX();
        lastY = event.getY();
        isPanning = true;
    }

    private void handleMouseDragged(MouseEvent event) {
        if (isPanning) {
            double deltaX = event.getX() - lastX;
            double deltaY = event.getY() - lastY;

            // Перемещение графика при перетаскивании
            minX -= deltaX / 50.0;
            maxX -= deltaX / 50.0;
            minY += deltaY / 50.0;
            maxY += deltaY / 50.0;

            // Обновление границ осей
            xAxis.setLowerBound(minX);
            xAxis.setUpperBound(maxX);
            yAxis.setLowerBound(minY);
            yAxis.setUpperBound(maxY);

            lastX = event.getX();
            lastY = event.getY();
        }
    }

    private void handleMouseClick(MouseEvent event, XYChart.Series<Number, Number> series, List<Double> xValues, List<Double> yValues) {
        double x = xAxis.getValueForDisplay(event.getX()).doubleValue();
        double y = yAxis.getValueForDisplay(event.getY()).doubleValue();

        // Создание новой точки на графике
        XYChart.Data<Number, Number> clickedDataPoint = new XYChart.Data<>(x, y);
        Circle circle = new Circle(5.0);
        circle.setFill(Color.RED);
        clickedDataPoint.setNode(circle);

        // Добавление точки на график и в массивы xValues и yValues
        series.getData().add(clickedDataPoint);
        xValues.add(x - 1.57);
        yValues.add(y + (0.38));
        System.out.println(x - 1.57 + " " + (y  + 0.38));

        // После добавления новой точки, пересчитайте и обновите график
        recalculateAndRedrawGraph(series, xValues, yValues);
    }

    // Метод для пересчета и обновления графика
    private void recalculateAndRedrawGraph(XYChart.Series<Number, Number> series, List<Double> xValues, List<Double> yValues) {
        series.getData().clear();

        InterpolatingLagrangePolynomial polynomial = new InterpolatingLagrangePolynomial();
        for (double i = 0.0; i <= 10; i += 0.01) {
            XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(i, polynomial.calculate(xValues, yValues, i));
            Circle circle = new Circle(1.0);
            dataPoint.setNode(circle);
            series.getData().add(dataPoint);
        }
    }


    private void handleMouseReleased(MouseEvent event) {
        isPanning = false;
    }
}