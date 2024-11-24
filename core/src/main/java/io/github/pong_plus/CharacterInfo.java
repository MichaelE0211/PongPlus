package io.github.pong_plus;


import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
public class CharacterInfo implements Screen {
    private String name;
    private int currentHealth;
    private int maxHealth;
    private ArrayList<Sprite> healthBar;

    private String character;   // Used for sprite creation
    private Portrait portrait;

    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;


    public CharacterInfo(String name, int maxHealth, String character) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.character = character;

        this.healthBar = new ArrayList<Sprite>();
    }

    private void createPortrait() {
        if (character.equals("Player")) {
            portrait = new Portrait(
                160, Gdx.graphics.getHeight() - 100,
                new String[]{
                    "player_neutral.png",
                    "player_happy.png",
                    "player_sad.png",
                    "player_mad.png"
                },
                "Happy",
                false
            );
        }
        else {
            portrait = new Portrait(
                Gdx.graphics.getWidth() - 240, Gdx.graphics.getHeight() - 100,
                new String[]{
                    "enemy_neutral.png",
                    "enemy_neutral.png",
                    "enemy_mad.png",
                    "enemy_mad.png",
                },
                "Happy",
                true
            );
        }
    }

    private void createHealthBar() {
        int startX;
        int y = Gdx.graphics.getHeight() - 100;

        if (character.equals("Player")) {
            startX = 255;
            for (int i = 0; i < maxHealth; i++) {
                Sprite heart = new Sprite(new Texture("heart.png"));
                heart.setPosition(startX + (i * 37), y);
                healthBar.add(heart);
            }
        }
        else {
            startX = Gdx.graphics.getWidth() - new Texture("heart.png").getWidth() - 255;
            for (int i = 0; i < maxHealth; i++) {
                Sprite heart = new Sprite(new Texture("heart.png"));
                heart.setPosition(startX - (i * 37), y);
                healthBar.add(heart);
            }
        }
    }

    public void setMoodFromHealth() {
        if (currentHealth <= maxHealth && currentHealth >= maxHealth - 1) {
            portrait.setMood("Happy");
        }
        else if (currentHealth == 2) {
            portrait.setMood("Mad");
        }
        else if (currentHealth <= 1) {
            portrait.setMood("Sad");
        }
        else {
            portrait.setMood("Neutral");
        }
        portrait.flip(portrait.getIsFlipped(), false);
    }

    public void setCurrentHealth(int currentHealth) { this.currentHealth = currentHealth; }

    public int getCurrentHealth() { return currentHealth; }
    public int getMaxHealth() { return maxHealth; }


    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        layout = new GlyphLayout();

        font.getData().setScale(2);

        createPortrait();
        createHealthBar();
    }

    @Override
    public void render(float delta) {
        batch.begin();

        for (int i = 0; i < currentHealth; i++) {
            healthBar.get(i).draw(batch);
        }

        portrait.render(batch);

        if (character.equals("Player")) {
            font.draw(batch, name, 255, Gdx.graphics.getHeight() - 35);
        }
        else {
            layout.setText(font, name);
            float nameW = layout.width;

            font.draw(batch, name, Gdx.graphics.getWidth() / 2.0f + nameW / 2 + 55, Gdx.graphics.getHeight() - 35);
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
        batch.dispose();
        portrait.dispose();
        for(int i = 0; i < healthBar.size(); i++) {
            healthBar.get(i).getTexture().dispose();
        }
    }
}
