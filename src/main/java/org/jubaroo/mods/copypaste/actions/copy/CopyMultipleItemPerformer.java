package org.jubaroo.mods.copypaste.actions.copy;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;
import org.jubaroo.mods.copypaste.Initiator;
import org.jubaroo.mods.copypaste.Question.CopyMultipleQuestion;

public class CopyMultipleItemPerformer implements ActionPerformer {
    private final short actionId;
    public final ActionEntry actionEntry;

    public CopyMultipleItemPerformer() {
        actionId = (short) ModActions.getNextActionId();
        actionEntry = ActionEntry.createEntry(actionId, "Copy item multiple times", "copying", new int[]{
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
            if (!Initiator.canUse(performer, target)) {
                performer.getCommunicator().sendNormalServerMessage("You cannot copy that right now.");
                return true;
            }
            CopyMultipleQuestion.send(performer, target);
            return true;
        } else {
            Initiator.logWarning("Somehow a non-player activated copy action...");
        }
        return true;
    }

    @Override
    public boolean action(Action act, Creature performer, Item source, Item target, short action, float counter) {
        return this.action(act, performer, target, action, counter);
    }

}


