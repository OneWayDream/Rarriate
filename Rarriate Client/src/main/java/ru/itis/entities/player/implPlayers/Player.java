package ru.itis.entities.player.implPlayers;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import ru.itis.entities.player.AbstractPlayer;
import ru.itis.utils.PropertiesLoader;
import ru.itis.utils.TextureLoader;


public class Player extends AbstractPlayer {

    private Image[] idleImages;
    private Image[] runRightImages;
    private Image[] runLeftImages;
    private static final int ANIMATION_MILLIS = 150;

    public Player() {
        super(PropertiesLoader.getInstance().getProperty("PLAYER_NAME"));
        runRightImages = TextureLoader.getPlayer1SpriteRight();
        runLeftImages = TextureLoader.getPlayer1SpriteLeft();
        idleImages = TextureLoader.getPlayer1SpriteIdle();

//        fillPlayer(TextureLoader.getPlayer1Texture());
        startAnimation();
    }

    @Override
    protected void startAnimation() {
        Thread animation = new Thread() {
            @Override
            public void run() {
                while (true){
                    switch (state) {
                        case Player.RUN_LEFT:
                            for (int i = 0; i < runLeftImages.length && state == Player.RUN_LEFT; i++) {
                                fillPlayer(runLeftImages[i]);
                                try {
                                    Thread.sleep(ANIMATION_MILLIS);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;


                        case Player.RUN_RIGHT:
                            for (int i = 0; i < runRightImages.length && state == Player.RUN_RIGHT; i++) {
                                fillPlayer(runRightImages[i]);
                                try {
                                    Thread.sleep(ANIMATION_MILLIS);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;

                            case Player.IDLE:
                                for (int i = 0; i < idleImages.length && state == Player.IDLE; i++) {
                                    fillPlayer(idleImages[i]);
                                    try {
                                        Thread.sleep(10);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;

                        default:
                            break;
                    }
                }
            }
        };
        animation.start();
    }
}
