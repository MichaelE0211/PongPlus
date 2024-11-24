package io.github.pong_plus;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import org.w3c.dom.Text;

public class Portrait extends Sprite {
    private Texture texture;
    private String[] moods;     // Neutral (0), Happy (1), Sad (2), Mad (3)
    private String mood;
    private boolean isFlipped;
    public Portrait(float x, float y, String[] moods, String mood, boolean isFlipped) {
        this.moods = moods;
        this.mood = mood;
        this.isFlipped = isFlipped;

        setTextureFromMood(mood);

        setPosition(x, y);
        setScale(2.5f);
        flip(isFlipped, false);
    }

    private void setTextureFromMood(String mood) {
        if (texture != null) texture.dispose();

        switch (mood) {
            case "Neutral":
                texture = new Texture(moods[0]);
                break;
            case "Happy":
                texture = new Texture(moods[1]);
                break;
            case "Sad":
                texture = new Texture(moods[2]);
                break;
            case "Mad":
                texture = new Texture(moods[3]);
                break;
            default:
                this.mood = "Neutral";
                texture = new Texture(moods[0]);
                break;
        }

        setRegion(texture); // Associate the texture with the sprite
        setSize(texture.getWidth(), texture.getHeight()); // Set the sprite's size
    }

    public void setMood(String mood) {
        this.mood = mood;
        setTextureFromMood(mood);
    }

    public boolean getIsFlipped() { return isFlipped; }

    public void render(SpriteBatch batch) {
        draw(batch);
    }

    public void dispose() {
        if (texture != null) texture.dispose();
    }

}
