package org.jubaroo.mods.copypaste.actions.copy;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.players.Player;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;
import org.jubaroo.mods.copypaste.Initiator;

public class CopyItemPerformer implements ActionPerformer {
    private final short actionId;
    public final ActionEntry actionEntry;

    public CopyItemPerformer() {
        actionId = (short) ModActions.getNextActionId();
        actionEntry = ActionEntry.createEntry(actionId, "Copy Once", "copying", new int[]{
                        Actions.ACTION_TYPE_IGNORERANGE,
                        Actions.ACTION_TYPE_QUICK
                }
        );
        ModActions.registerAction(actionEntry);
        ModActions.registerActionPerformer(this);
    }

    @Override
    public short getActionId() {
        return actionId;
    }

    @Override
    public boolean action(Action act, Creature performer, Item target, short action, float counter) {
        if (performer instanceof Player) {
            if (CopyHelper.cannotUse(performer, target)) {
                performer.getCommunicator().sendNormalServerMessage("You cannot copy that.");
                return true;
            }
            try {
                CopyHelper.copyItemData(performer, target, actionEntry);
            } catch (NoSuchTemplateException | FailedException e) {
                e.printStackTrace();
            }
        } else {
            Initiator.logWarning(String.format("[WARNING] Somehow a non-player activated action: %s", actionId));
        }
        return true;
    }

    @Override
    public boolean action(Action act, Creature performer, Item source, Item target, short action, float counter) {
        return this.action(act, performer, target, action, counter);
    }

}


