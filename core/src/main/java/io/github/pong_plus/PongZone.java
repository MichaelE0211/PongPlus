package io.github.pong_plus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Timer;


//import java.util.Timer;
import java.util.TimerTask;

public class PongZone implements Screen {

    private int width, height;

    private Paddle playerPaddle;
    private Paddle enemyPaddle;
    private Ball ball;

    private int countdown;
    private boolean isCountdownActive = false;
    private  boolean isArrowVisible = false;
    private  boolean isGameRestarting = false;

    private int playerScore;
    private int enemyScore;

    private float[] vertices = new float[4]; // x1 (west), x2, (east), y1 (north), y2 (south)

    private Sprite arrowSprite;

    public PongZone(int width, int height) {
        this.width = width;
        this.height = height;

        // Calculate vertices
        float x = Gdx.graphics.getWidth() / 2.0f;
        float y = Gdx.graphics.getHeight() / 2.0f;

        vertices[0] = x - (width / 2.0f);      // west
        vertices[1] = x + (width / 2.0f);      // east
        vertices[2] = y + (height / 2.0f);     // north
        vertices[3] = y - (height / 2.0f);     // south

        this.playerPaddle = new Paddle(vertices[0] + 50, this.height / 2.0f, new Texture("paddle.png"), this);
        this.enemyPaddle = new Paddle(vertices[1] - 50, this.height / 2.0f, new Texture("paddle.png"), this);
        this.ball = new Ball(vertices[0] + width / 2.0f, this.height / 2.0f, new Texture("ball.png"), this);

        startCountdown();
    }

    public void resetZone() {
        playerPaddle.resetPosition();
        enemyPaddle.resetPosition();
        ball.reset();

        startCountdown();
    }

    private void startCountdown() {
        countdown = 4;
        isCountdownActive = true;

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                countdown--;

                if (countdown <= 0) {
                    isCountdownActive = false;
                    isArrowVisible = false;
                    ball.start();
                }
            }
        }, 0, 1, 4);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                isArrowVisible = !isArrowVisible;
            }
        }, 0, 0.5f, 8);
    }

    // Call this when a player scores
    public void playerScores() {
        playerScore++;
        ball.setStartDirection("Player");

        if (playerScore == 5) {
            restartGame();
        }
        else {
            resetZone();
        }

    }

    public void enemyScores() {
        enemyScore++;
        ball.setStartDirection("Enemy");

        if (enemyScore == 5) {
            restartGame();
        }
        else {
            resetZone();
        }
    }

    private void restartGame() {
        isGameRestarting = true;
        ball.reset();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                isGameRestarting = false;
                playerScore = 0;
                enemyScore = 0;
                resetZone();
            }
        }, 3); // Delay of 3 seconds
    }

    private void handleInput(float delta) {
        if (isCountdownActive || isGameRestarting) {
            return;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) playerPaddle.moveUp(delta);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) playerPaddle.moveDown(delta);
    }

    private void updateAI(float delta) {
        // Basic AI to follow the ball's vertical position
        float aiSpeed = 500f; // AI paddle speed

        if (playerScore >= 3) { aiSpeed = 650f; }

        if (ball.getY() + ball.getHeight() / 2 > enemyPaddle.getY() + enemyPaddle.getHeight() / 2) {
            enemyPaddle.moveUp(delta, aiSpeed);
        } else if (ball.getY() + ball.getHeight() / 2 < enemyPaddle.getY() + enemyPaddle.getHeight() / 2) {
            enemyPaddle.moveDown(delta, aiSpeed);
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public float[] getVertices() { return vertices; }

    public int getPlayerScore() { return playerScore; }
    public int getEnemyScore() { return enemyScore; }

    public Paddle getEnemyPaddle() { return enemyPaddle; }

    public boolean getIsGameRestarting() { return isGameRestarting; }

    @Override
    public void show() {
        arrowSprite = new Sprite(new Texture("arrow.png"));
        arrowSprite.setOriginCenter();
    }

    @Override
    public void render(float delta) {
        SpriteBatch batch = new SpriteBatch();
        BitmapFont font = new BitmapFont();
        GlyphLayout layout = new GlyphLayout();
        ShapeRenderer shapeRenderer = new ShapeRenderer();

        handleInput(delta);
        updateAI(delta);

        ball.update(delta);
        ball.checkCollision(playerPaddle);
        ball.checkCollision(enemyPaddle);

        // Draw outline
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(vertices[0], vertices[3], width, height);
        shapeRenderer.end();

        batch.begin();

        playerPaddle.render(batch);
        enemyPaddle.render(batch);
        ball.render(batch);

        // Display arrow on round start
        if (isCountdownActive) {
            // Display countdown number
            layout.setText(font, String.valueOf(countdown));
            float countdownTextWidth = layout.width;

            font.getData().setScale(4);
            font.draw(batch, String.valueOf(countdown), Gdx.graphics.getWidth() / 2.0f - countdownTextWidth / 2, Gdx.graphics.getHeight() - 50);
            font.getData().setScale(2);

            if (isArrowVisible) {
                // Display directional arrow.
                int angle = ball.getGlobalAngle();

                arrowSprite.setPosition(ball.getX() + (float) Math.cos(Math.toRadians(angle)) * 48,
                                        ball.getY() + (float) Math.sin(Math.toRadians(angle)) * 48);
                arrowSprite.setRotation(angle);
                arrowSprite.draw(batch);
            }
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
