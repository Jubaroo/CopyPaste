package org.jubaroo.mods.copypaste.actions;

import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.jubaroo.mods.copypaste.Initiator;
import org.jubaroo.mods.copypaste.actions.copy.CopyTerrainDataPerformer;
import org.jubaroo.mods.copypaste.actions.paste.PasteTerrainDataPerformer;

import java.util.ArrayList;
import java.util.List;

public class TerrainMenuProvider implements BehaviourProvider {
    private final List<ActionEntry> menu;

    public TerrainMenuProvider() {
        menu = new ArrayList<>();
        menu.add(new CopyTerrainDataPerformer().actionEntry);
        menu.add(new PasteTerrainDataPerformer().actionEntry);
        menu.add(0, new ActionEntry((short) (-1 * menu.size()), "GM Copy/Paste Terrain", ""));
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, int tile) {
        if (performer instanceof Player) {
            if (tile > 0 && performer.getPower() >= Initiator.gmPower) {
                return menu;
            }
        }
        return null;
    }

}
