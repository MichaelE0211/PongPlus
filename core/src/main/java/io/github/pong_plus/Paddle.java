package io.github.pong_plus;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;

public class Paddle {
    private float x, y;
    private float previousY;
    private int direction = 0;
    private Texture texture;
    private PongZone zone;
    private static final float SPEED = 500f;


    public Paddle(float x, float y, Texture texture, PongZone zone) {
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.zone = zone;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
        direction = Float.compare(y, previousY); // Automatically sets direction to -1, 0, or 1
        previousY = y;
    }
    public void resetPosition() {
        y = (zone.getVertices()[3] + zone.getHeight() / 2.0f) - texture.getHeight() / 2.0f;

        if (this == zone.getEnemyPaddle() && zone.getPlayerScore() >= 3) {
            texture = new Texture("super_paddle.png");
        }

    }
    public void moveUp(float delta) {
        y += SPEED * delta;
        if (y + texture.getHeight() > zone.getVertices()[2]) y = zone.getVertices()[2] - texture.getHeight();
    }

    public void moveUp(float delta, float customSpeed) {
        y += customSpeed * delta;
        if (y + texture.getHeight() > zone.getVertices()[2]) y = zone.getVertices()[2] - texture.getHeight();
    }

    public void moveDown(float delta) {
        previousY = y;
        y -= SPEED * delta;
        if (y < zone.getVertices()[3]) y = zone.getVertices()[3];
    }

    public void moveDown(float delta, float customSpeed) {
        previousY = y;
        y -= customSpeed * delta;
        if (y < zone.getVertices()[3]) y = zone.getVertices()[3];
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public int getDirection() { return direction; }
    public float getWidth() { return texture.getWidth(); }
    public float getHeight() { return texture.getHeight(); }
}
