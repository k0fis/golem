package kfs.golem.comp;

import com.badlogic.gdx.graphics.Color;
import kfs.golem.ecs.KfsComp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SubtitleMultilineComponent implements KfsComp {

    public List<String> lines;

    public float baseX = 20f;
    public float baseY;
    public float lineHeight = 40f;

    public float scrollOffset = 10f;     // hlavní posun
    public float scrollSpeed = 17f;      // spočítá se z timePerLine

    public float fadeDuration = 0.2f;
    public float endDuration = 2f;

    public int maxLines = 4;
    public Color color;
    public Runnable onComplete;                // index prvního viditelného řádku

    public SubtitleMultilineComponent(List<String> lines, Color color, Runnable onComplete) {
        this.lines = lines;
        this.color = color;
        this.onComplete = onComplete;
        if (lines.size() < maxLines) maxLines = lines.size();
    }

}
