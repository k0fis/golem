package kfs.golem.sys;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import kfs.golem.GolemMain;
import kfs.golem.comp.DialogueComponent;
import kfs.golem.comp.PositionComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsSystem;

public class DialogueSystem implements KfsSystem {

    private final GolemMain golemMain;
    private BitmapFont font;

    public DialogueSystem(GolemMain golemMain) {
        this.golemMain = golemMain;
    }

    @Override
    public void init() {
        font = new BitmapFont();
    }

    @Override
    public void update(float delta) {
        for (Entity e : golemMain.world.getEntitiesWith(DialogueComponent.class)) {
            DialogueComponent dlg = golemMain.world.getComponent(e, DialogueComponent.class);
            if (!dlg.active) continue;
            PositionComponent pos = golemMain.world.getComponent(e, PositionComponent.class);
            dlg.timeVisible += delta;

            String text = dlg.lines[dlg.index];

            // poetické bubliny + jemné vyblednutí
        }
    }

    public void triggerDialogue(Entity e) {
        DialogueComponent dlg = golemMain.world.getComponent(e, DialogueComponent.class);
        dlg.active = true;
        dlg.timeVisible = 0f;
    }

    public void advanceDialogue(Entity e) {
        DialogueComponent dlg = golemMain.world.getComponent(e, DialogueComponent.class);
        dlg.index++;

        if (dlg.index >= dlg.lines.length) {
            dlg.active = false; // dialog skončil
            dlg.index = 0;
        } else {
            dlg.timeVisible = 0f;
        }
    }
}
