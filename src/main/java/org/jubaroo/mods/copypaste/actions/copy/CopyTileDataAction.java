package org.jubaroo.mods.copypaste.actions.copy;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.players.Player;
import org.gotti.wurmunlimited.modsupport.actions.*;
import org.jubaroo.mods.copypaste.Initiator;

import java.util.Collections;
import java.util.List;

public class CopyTileDataAction implements ModAction {
    private final short actionId;
    private final ActionEntry actionEntry;

    public CopyTileDataAction() {
        actionId = (short) ModActions.getNextActionId();
        actionEntry = ActionEntry.createEntry(actionId, "Copy terrain data to wand", "copying data", new int[]{});
        ModActions.registerAction(actionEntry);
    }

    @Override
    public BehaviourProvider getBehaviourProvider() {
        return new BehaviourProvider() {
            // Menu with activated object
            @Override
            public List<ActionEntry> getBehavioursFor(Creature performer, Item item, int tilex, int tiley, boolean onSurface, int tile) {
                if (performer instanceof Player && item != null && item.getTemplateId() == ItemList.wandDeity) {
                    return Collections.singletonList(actionEntry);
                }
                return null;
            }
        };
    }

    @Override
    public ActionPerformer getActionPerformer() {
        return new ActionPerformer() {

            @Override
            public short getActionId() {
                return actionId;
            }

            // With activated object
            @Override
            public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, int tile, short num, float counter) {
                try {
                    if (performer instanceof Player) {

                        // If not holding a wand, we abort the action
                        if (source.getTemplateId() != ItemList.wandDeity) {
                            performer.getCommunicator().sendNormalServerMessage("You must use an ebony wand.");
                            return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
                        }

                        // Change the aux data of the performers wand to the targeted tile's byte value
                        byte type = Tiles.decodeType(tile);
                        source.setAuxData(type);
                        // If enabled, send a message to the performer about what just happened
                        if (Initiator.messageOnCopy) {
                            performer.getCommunicator().sendNormalServerMessage(String.format("Your wand aux data has been updated to %d.", type));
                        }
                        return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
                    }
                    // Log a warning about a non player performing the action and terminate the action?
                    else {
                        Initiator.logWarning(String.format("Somehow a non-player performed action %s.", actionId));
                    }
                    return propagate(action, ActionPropagation.CONTINUE_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
                } catch (Exception e) {
                    Initiator.logException("[Error] in action in CopyTileDataAction", e);
                    return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
                }
            }

        };
    }

}