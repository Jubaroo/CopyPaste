package org.jubaroo.mods.copypaste.actions;

import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.jubaroo.mods.copypaste.Initiator;
import org.jubaroo.mods.copypaste.actions.copy.CopyMultipleItemPerformer;
import org.jubaroo.mods.copypaste.actions.copy.CopyTerrainDataPerformer;
import org.jubaroo.mods.copypaste.actions.paste.CopyPastePerformer;
import org.jubaroo.mods.copypaste.actions.paste.PasteTerrainDataPerformer;

import java.util.ArrayList;
import java.util.List;

public class CopyTerrainMenuProvider implements BehaviourProvider {
    private final List<ActionEntry> menu;

    public CopyTerrainMenuProvider() {
        menu = new ArrayList<>();
        menu.add(new CopyTerrainDataPerformer().actionEntry);
        menu.add(new PasteTerrainDataPerformer().actionEntry);
        menu.add(0, new ActionEntry((short) (-1 * menu.size()), "GM Terrain Copy/Paste", ""));
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Item object) {
        return this.getBehavioursFor(performer, object);
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item object) {
        if (performer instanceof Player) {
            if (object != null && performer.getPower() >= Initiator.gmPower) {
                return menu;
            }
        }
        return null;
    }

}
