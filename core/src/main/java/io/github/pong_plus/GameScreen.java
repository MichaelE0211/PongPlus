package io.github.pong_plus;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Timer;
import java.util.TimerTask;

public class GameScreen implements Screen {
    private final PongGame game;
    private PongZone zone;
    private BitmapFont font;
    private GlyphLayout layout;
    private SpriteBatch batch;

    private CharacterInfo playerInfo;
    private CharacterInfo enemyInfo;

    private int previousPlayerScore = 0;
    private int previousEnemyScore = 0;

    private boolean isGameResetting = false;

    private String[] splashTextArray = {
        "Deal damage by scoring.",
        "Move against the ball to flip its angle.",
        "Careful, the orc is stronger than he looks.",
        "Good luck!",
        "Almost there.",
        "YOU WIN!"
    };

    private String splashText;


    public GameScreen(PongGame game) {
        this.game = game;
        zone = new PongZone(960, 720);

        playerInfo = new CharacterInfo("You", 5, "Player");
        enemyInfo = new CharacterInfo("Definitely not you", 5, "Enemy");

        splashText = splashTextArray[zone.getPlayerScore()];
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2); // Adjust font size
        layout = new GlyphLayout();

        zone.show();
        playerInfo.show();
        enemyInfo.show();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        zone.render(delta);
        playerInfo.render(delta);
        enemyInfo.render(delta);
        game.batch.end();

        // Show splash text
        batch.begin();

        layout.setText(font, splashText);
        float splashWidth = layout.width;

        font.draw(batch, splashText, Gdx.graphics.getWidth() / 2 - splashWidth / 2, 100);

        if (zone.getIsGameRestarting()) {
            layout.setText(font, "Resetting game...");
            splashWidth = layout.width;
            font.draw(batch, "Resetting game...", Gdx.graphics.getWidth() / 2 - splashWidth / 2, 50);
        }

        batch.end();

        if (zone.getPlayerScore() != previousPlayerScore) {
            previousPlayerScore = zone.getPlayerScore();
            enemyInfo.setCurrentHealth(enemyInfo.getMaxHealth() - zone.getPlayerScore());
            enemyInfo.setMoodFromHealth();
            splashText = splashTextArray[zone.getPlayerScore()];
        }
        if (zone.getEnemyScore() != previousEnemyScore) {
            previousEnemyScore = zone.getEnemyScore();
            playerInfo.setCurrentHealth(playerInfo.getMaxHealth() - zone.getEnemyScore());
            playerInfo.setMoodFromHealth();

            if (zone.getEnemyScore() == 5) {
                splashText = "You lose.";
            }

        }
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    // Other required methods for Screen
    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}


