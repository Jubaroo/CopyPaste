package org.jubaroo.mods.copypaste.actions;

import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.jubaroo.mods.copypaste.Initiator;
import org.jubaroo.mods.copypaste.actions.copy.CopyHelper;
import org.jubaroo.mods.copypaste.actions.copy.CopyItemPerformer;
import org.jubaroo.mods.copypaste.actions.copy.CopyMultipleItemPerformer;
import org.jubaroo.mods.copypaste.actions.paste.CopyPastePerformer;

import java.util.ArrayList;
import java.util.List;

public class ItemMenuProvider implements BehaviourProvider {
    private final List<ActionEntry> menu;

    public ItemMenuProvider() {
        menu = new ArrayList<>();
        menu.add(new CopyItemPerformer().actionEntry);
        new CopyPastePerformer();
        menu.add(CopyPastePerformer.actionEntry);
        new CopyMultipleItemPerformer();
        menu.add(CopyMultipleItemPerformer.actionEntry);
        menu.add(0, new ActionEntry((short) (-1 * menu.size()), "GM Copy & Paste", ""));
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Item object) {
        return this.getBehavioursFor(performer, object);
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item object) {
        if (performer instanceof Player) {
            if (CopyHelper.canUse(performer, object)) {
                return menu;
            }
        }
        return null;
    }

}
