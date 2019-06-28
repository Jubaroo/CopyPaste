package org.jubaroo.mods.copyitems.actions.copy;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.players.Player;
import com.wurmonline.shared.util.MaterialUtilities;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;
import org.jubaroo.mods.copyitems.Initiator;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SingleCopyItemPerformer implements ActionPerformer {
    private static Logger logger = Logger.getLogger(SingleCopyItemPerformer.class.getName());
    private final short actionId;
    public final ActionEntry actionEntry;

    public SingleCopyItemPerformer() {
        actionId = (short) ModActions.getNextActionId();
        actionEntry = ActionEntry.createEntry(actionId, "Copy Item", "copying", new int[]{
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
            try {
                Copy.copyItem(performer, target);
            } catch (NoSuchTemplateException | FailedException | NoSuchItemException e) {
                e.printStackTrace();
            }
            // Optional message when item is copied
            if (Initiator.messageOnCopy) {
                performer.getCommunicator().sendNormalServerMessage(String.format("You copy the %s and all it's data. The rarity is: %s, the material is: %s, and the quality is: %s", target.getName(), Initiator.getRarityString(target.getRarity()), MaterialUtilities.getMaterialString(target.getMaterial()), target.getCurrentQualityLevel()));
            }
            //return true;
        } else {
            logger.log(Level.WARNING, "Somehow a non-player activated copy action...");
        }
        return true;
    }

    @Override
    public boolean action(Action act, Creature performer, Item source, Item target, short action, float counter) {
        return this.action(act, performer, target, action, counter);
    }

}


