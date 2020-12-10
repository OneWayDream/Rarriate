package ru.itis.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ShootingGame extends Application {

    private static final int WIDTH = 600 ;
    private static final int HEIGHT = 600 ;
    private static final int NUM_TARGETS = 30 ;
    private static final int GUN_RADIUS = 50 ;
    private static final Random RNG = new Random();

    @Override
    public void start(Stage primaryStage) {
        final Pane root = new Pane();

        final Arc gun = new Arc(WIDTH/2, HEIGHT, GUN_RADIUS, GUN_RADIUS, 0, 180);
        gun.setFill(Color.BLUEVIOLET);
        gun.setType(ArcType.ROUND);
        root.getChildren().add(gun);

        final List<Shape> targets = createTargets();
        root.getChildren().addAll(targets);

        final Scene scene = new Scene(root, WIDTH, HEIGHT, Color.SKYBLUE);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                fireBullet(root, targets);
            }
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private List<Shape> createTargets() {
        List<Shape> targets = new ArrayList<>();

        for (int i=0; i<NUM_TARGETS; i++) {
            int size = WIDTH / ( 2 * NUM_TARGETS) ;
            int centerX = size * (2 * i + 1) ;
            int type = RNG.nextInt(3);
            final Shape shape ;
            switch (type) {
                case 0: shape = createSquare(size, centerX) ;
                    break ;
                case 1: shape = createCircle(size, centerX) ;
                    break ;
                default: shape = createTriangle(size, centerX) ;
                    break ;
            }
            shape.setFill(Color.BLANCHEDALMOND);
            targets.add(shape);
        }
        return targets ;
    }

    private Shape createSquare(int size, int centerX) {
        return new Rectangle(centerX - size / 2, size, size, size);
    }

    private Shape createCircle(int size, int centerX) {
        return new Circle(centerX, size*1.5, size/2);
    }

    private Shape createTriangle(int size, int centerX) {
        Path triangle = new Path(
                new MoveTo(centerX - size/2,  size * 2),
                new LineTo(centerX + size/2, size * 2),
                new LineTo(centerX, size),
                new ClosePath());
        triangle.setStroke(Color.TRANSPARENT);
        return triangle ;
    }

    private void fireBullet(final Pane root, final List<Shape> targets) {
        final Shape bullet = new Circle(2, Color.BLACK);
        root.getChildren().add(bullet);
        final TranslateTransition bulletAnimation = new TranslateTransition(Duration.seconds(1), bullet);
        final int bulletTargetX = RNG.nextInt(WIDTH);
        bulletAnimation.setFromX(WIDTH/2);
        bulletAnimation.setFromY(HEIGHT-GUN_RADIUS);
        bulletAnimation.setToX(bulletTargetX);
        bulletAnimation.setToY(0);

        bullet.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable,
                                Bounds oldValue, Bounds newValue) {
                for (final Shape target : new ArrayList<Shape>(targets)) {
                    if (((Path)Shape.intersect(bullet, target)).getElements().size() > 0) {
                        System.out.println("Hit!");
                        targets.remove(target);
                        root.getChildren().remove(target);
                        bulletAnimation.stop();
                        root.getChildren().remove(bullet);
                    }
                }
            }
        });
        bulletAnimation.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                root.getChildren().remove(bullet);
            }
        });
        bulletAnimation.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
