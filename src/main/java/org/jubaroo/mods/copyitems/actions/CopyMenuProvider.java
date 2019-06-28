package org.jubaroo.mods.copyitems.actions;

import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.jubaroo.mods.copyitems.Initiator;
import org.jubaroo.mods.copyitems.actions.copy.SingleCopyItemPerformer;
import org.jubaroo.mods.copyitems.actions.paste.CopyPastePerformer;

import java.util.ArrayList;
import java.util.List;

public class CopyMenuProvider implements BehaviourProvider {
    private List<ActionEntry> menu;

    public CopyMenuProvider() {
        menu = new ArrayList<>();
        if (Initiator.copyAction) {
            menu.add(new SingleCopyItemPerformer().actionEntry);
        }
        if (Initiator.copyPasteAction) {
            menu.add(new CopyPastePerformer().actionEntry);
        }
        if (Initiator.copyAction || Initiator.copyPasteAction) {
            menu.add(0, new ActionEntry((short) (-1 * menu.size()), "Copy Item", ""));
        }
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Item object) {
        return this.getBehavioursFor(performer, object);
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item object) {
        if (performer instanceof Player && object != null && Initiator.canUse(performer, object)) {
            return menu;
        }
        return null;
    }
}
