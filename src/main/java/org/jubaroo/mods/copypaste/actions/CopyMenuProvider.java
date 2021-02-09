package org.jubaroo.mods.copypaste.actions;

import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.jubaroo.mods.copypaste.Initiator;
import org.jubaroo.mods.copypaste.actions.copy.CopyItemPerformer;
import org.jubaroo.mods.copypaste.actions.copy.CopyMultipleItemPerformer;
import org.jubaroo.mods.copypaste.actions.paste.CopyPastePerformer;

import java.util.ArrayList;
import java.util.List;

public class CopyMenuProvider implements BehaviourProvider {
    private final List<ActionEntry> menu;

    public CopyMenuProvider() {
        menu = new ArrayList<>();
        menu.add(new CopyItemPerformer().actionEntry);
        menu.add(new CopyPastePerformer().actionEntry);
        menu.add(new CopyMultipleItemPerformer().actionEntry);
        menu.add(0, new ActionEntry((short) (-1 * menu.size()), "GM Copy", ""));
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
