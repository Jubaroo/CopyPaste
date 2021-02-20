package org.jubaroo.mods.copypaste.actions;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.MiscConstants;
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

/**
 * Created by Jubaroo on 2/19/2021
 */

public class CopyTerrainAction implements ModAction {
    private final short actionId;
    private final ActionEntry actionEntry;

    public CopyTerrainAction() {
        actionId = (short) ModActions.getNextActionId();
        actionEntry = ActionEntry.createEntry(actionId, "Copy Terrain", "copying terrain", new int[]{});
        ModActions.registerAction(actionEntry);
    }

    @Override
    public BehaviourProvider getBehaviourProvider() {
        return new BehaviourProvider() {
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

            @Override
            public boolean action(Action action, Creature performer, Item wand, int tilex, int tiley, boolean onSurface, int heightOffset, int tile, short num, float counter) {
                try {
                    if (performer instanceof Player) {

                        // If not holding a wand, we abort the action
                        if (wand.getTemplateId() != ItemList.wandDeity) {
                            performer.getCommunicator().sendNormalServerMessage("You must use an ebony wand.");
                            return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
                        }

                        if (performer.getPower() < MiscConstants.POWER_IMPLEMENTOR) {
                            performer.getCommunicator().sendNormalServerMessage("You do not have permission to do that.");
                            return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
                        }

                        byte type = Tiles.decodeType(tile);
                        if (wand.getAuxData() != type) {
                            // Change the aux data of the performers wand to the targeted tile's byte value
                            wand.setAuxData(type);
                            // If enabled, send a message to the performer about what just happened
                            performer.getCommunicator().sendNormalServerMessage(String.format("Your wand aux data has been updated to %s.", Tiles.getTile(type).getName().toLowerCase()));
                            return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
                        } else {
                            performer.getCommunicator().sendNormalServerMessage(String.format("Your wand aux data is already set to %s.", Tiles.getTile(type).getName().toLowerCase()));
                        }
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