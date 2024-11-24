package io.github.pong_plus;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;

import java.util.Random;

public class Ball {
    private Texture texture;
    private float x, y;
    private float dx, dy;

    private String startDirection = "Enemy";
    private String currentDirection = startDirection;
    private int startAngle;
    private int relativeAngle;
    private int globalAngle;
    private int quadrant;

    private float currentSpeed;
    private final float START_SPEED = 500f;
    private final float MAX_SPEED = 1250f;
    private int bounces = 0;

    private boolean isActive = false;
    private PongZone zone;

    // Bug fixes
    private int verticleDebounce = 0;
    private int paddleDebounce = 0;

    public Ball(float x, float y, Texture texture, PongZone zone) {
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.zone = zone;

        reset();
    }

    public void update(float delta) {
        if (isActive) {
            x += dx * delta;
            y += dy * delta;

            // Bounce off top and bottom walls
            if (verticleDebounce == 0 && (y <= zone.getVertices()[3] || y + texture.getHeight() >= zone.getVertices()[2])) {

                quadrant = flipQuadrantVertically(quadrant);
                globalAngle = relativeToGlobal(relativeAngle);

                dx = (float) Math.cos(Math.toRadians(globalAngle)) * currentSpeed;
                dy = (float) Math.sin(Math.toRadians(globalAngle)) * currentSpeed;

                // set debounce
                verticleDebounce = 15;
            }
            else if (verticleDebounce > 0){
                verticleDebounce--;
            }

            // Check if ball passed a paddle
            if (x < zone.getVertices()[0]) {
                zone.enemyScores();
            }
            else if (x > zone.getVertices()[1]) {
                zone.playerScores();
            }
        }

    }

    private void calculateAngle(Paddle paddle) {
        System.out.println("Calculating new angle starting from " + relativeAngle);

        final int MAX_RELATIVE_ANGLE = 45;
        final int MIN_RELATIVE_ANGLE = 25;

        float paddleTopY = paddle.getY() + paddle.getHeight() / 2;
        float paddleBottomY = paddle.getY() - paddle.getHeight() / 2;
        float paddleMidY = (paddleTopY + paddleBottomY) / 2;
        float paddleSize = (paddleTopY - paddleBottomY);

        float relativeY = y - paddleBottomY;
        float relativeM = paddleMidY - paddleBottomY;
        float c;

        if (relativeY > relativeM) {
            c = Math.abs(relativeY - (2 * relativeM)) / paddleSize;
        }
        else if (relativeY < relativeM) {
            c = relativeY / paddleSize;
        }
        else {
            c = 1;
        }

        System.out.println("   ball to center (c) = " + c);

        int r = MAX_RELATIVE_ANGLE - MIN_RELATIVE_ANGLE;
        System.out.println("   Angle range (r) = " + r);

        int angleChange = (int) (r * (1 - c)) / 2;

        if (Math.signum(paddle.getDirection()) != 0) {
            System.out.println("   Paddle moved as it hit the ball:");

            if (Math.signum(paddle.getDirection()) == Math.signum(dy)) {
                System.out.println("      Paddle moved alongside ball");
                relativeAngle = angleChange + MIN_RELATIVE_ANGLE;
                System.out.println("      Relative angle set to angleChange (" + angleChange + ")");

            }
            else {
                System.out.println("      Paddle moved against ball");
                relativeAngle = angleChange + MIN_RELATIVE_ANGLE;
                if (relativeAngle > MAX_RELATIVE_ANGLE) { relativeAngle = MAX_RELATIVE_ANGLE; }
                System.out.println("      Relative angle set to " + relativeAngle);

                quadrant = flipQuadrantVertically(quadrant);
                System.out.println("      Quadrant flipped vertically");

            }

        }

        System.out.print("   Quadrant flipped horizontally from " + quadrant + " to ");
        quadrant = flipQuadrantHorizontally(quadrant);
        System.out.println(quadrant);

        globalAngle = relativeToGlobal(relativeAngle);
        System.out.println("Final Angle = " + globalAngle);

    }

    private int flipQuadrantVertically(int startQuadrant) {
        switch (startQuadrant) {
            case 1:
                return 4;
            case 2:
                return 3;
            case 3:
                return 2;
            case 4:
                return 1;
            default:
                return startQuadrant;
        }
    }

    private int flipQuadrantHorizontally(int startQuadrant) {
        switch (startQuadrant) {
            case 1:
                return 2;
            case 2:
                return 1;
            case 3:
                return 4;
            case 4:
                return 3;
            default:
                return startQuadrant;
        }
    }

    private int relativeToGlobal(int startAngle) {
        switch (quadrant) {
            case 1:
                return startAngle;
            case 2:
                return 180 - startAngle;
            case 3:
                return 180 + startAngle;
            case 4:
                return 360 - startAngle;
            default:
                return startAngle;
        }
    }

    public void checkCollision(Paddle paddle) {
        if (paddleDebounce > 0) {
            paddleDebounce--;
            return;
        }

        if (x < paddle.getX() + paddle.getWidth() && x + texture.getWidth() > paddle.getX() &&
            y < paddle.getY() + paddle.getHeight() && y + texture.getHeight() > paddle.getY()) {

            paddleDebounce = 60;
            bounces++;

            // Increase speed after every 3 bounces
            if (bounces % 3 == 0) {
                currentSpeed *= 1.10f;
                if (currentSpeed > MAX_SPEED) { currentSpeed = MAX_SPEED; }

            }

            if (paddle == zone.getEnemyPaddle() && zone.getPlayerScore() >= 3) {
                currentSpeed *= 1.25f;
                texture = new Texture("super_ball.png");

            }
            else {
                texture = new Texture("ball.png");
            }

            calculateAngle(paddle);

            // Set dx, dy
            dx = (float) Math.cos(Math.toRadians(globalAngle)) * currentSpeed;
            dy = (float) Math.sin(Math.toRadians(globalAngle)) * currentSpeed;

//            // Flip direction
//            if (currentDirection.equals("Player")) {
//                currentDirection = "Enemy";
//            }
//            else {
//                currentDirection = "Player";
//            }
//            System.out.println(currentDirection);
//
//
//            // Calculate new angle
//            System.out.print("   " + currentAngle + " was modified to ");
//            float paddleTopY = paddle.getY() + paddle.getHeight() / 2;
//            float paddleBottomY = paddle.getY() - paddle.getHeight() / 2;
//            float paddleMidY = (paddleTopY + paddleBottomY) / 2;
//            float paddleSize = (paddleTopY - paddleBottomY);
//
//            int unadjustedAngle = (int) (currentAngle * (1 + (1 - (Math.abs(y - paddleMidY)) / paddleSize)));
//            System.out.println(unadjustedAngle);
//
//            // Keep angle between 0 - 360 deg.
//            System.out.print("   After checking its in range: ");
//            while (unadjustedAngle > 360) {
//                unadjustedAngle -= 360;
//            }
//            System.out.println(unadjustedAngle);
//
//            System.out.print("   After final corrections ");
//            // Ensure angle is facing the right direction
//            if (currentDirection.equals("Player")) {
//                if (unadjustedAngle >= 0 && unadjustedAngle <= 90) {
//                    unadjustedAngle += 90;
//                } else if (unadjustedAngle >= 270) {
//                    unadjustedAngle -= 90;
//                }
//            }
//            else {
//                if (unadjustedAngle >= 90 && unadjustedAngle <= 180) {
//                    unadjustedAngle -= 90;
//                }
//                else if (unadjustedAngle >= 180 && unadjustedAngle <= 270) {
//                    unadjustedAngle += 90;
//                }
//            }
//
//            currentAngle = unadjustedAngle; // Adjusted
//            System.out.println(currentAngle);
//



//            // Reverse horizontal direction
//            dx = -Math.signum(dx) * Math.abs(dx);
//
//
//
//            // Adjust dy based on paddle movement, with scaling
//            float scale = 0.1f * Math.abs(dx);
//            dy += (paddle.getDirection() * scale);
//
//            // Cap vertical speed
//            if (Math.abs(dy) > 600f) {
//                dy = Math.signum(dy) * 600f;
//            }
//            else if (Math.abs(dy) < 100f) {
//                dy = Math.signum(dy) * 100f;
//            }
        }
    }
    public void reset() {
        // Reset position
        x = (zone.getVertices()[0] + zone.getWidth() / 2.0f) - texture.getWidth() / 2.0f;
        y = (zone.getVertices()[3] + zone.getHeight() / 2.0f) - texture.getHeight() / 2.0f;

        // Determine angle between 15 - 25 deg. then modify for each direction
        startAngle = (new Random().nextInt(11) + 15);
        relativeAngle = startAngle;

        quadrant = (startDirection.equals("Enemy")) ? 1 : 2;
        if (new Random().nextInt(2) == 0) {
            quadrant = flipQuadrantVertically(quadrant);
        }

        currentSpeed = START_SPEED;
        globalAngle = relativeToGlobal(relativeAngle);

        dx = (float) Math.cos(Math.toRadians(globalAngle)) * currentSpeed;
        dy = (float) Math.sin(Math.toRadians(globalAngle)) * currentSpeed;

        // Clear bounces
        bounces = 0;

        texture = new Texture("ball.png");

        // Stop ball
        this.isActive = false;
    }
    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public void start() { this.isActive = true; }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return texture.getWidth(); }
    public float getHeight() { return texture.getWidth(); }

    public int getStartAngle() { return startAngle; }

    public int getGlobalAngle() { return globalAngle; }

    public void setStartDirection(String direction) { startDirection = direction; }

}

