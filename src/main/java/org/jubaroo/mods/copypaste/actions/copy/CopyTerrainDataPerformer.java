package org.jubaroo.mods.copypaste.actions.copy;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.players.Player;
import net.bdew.wurm.tools.server.ModData;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;
import org.jubaroo.mods.copypaste.Initiator;

public class CopyTerrainDataPerformer implements ActionPerformer {
    private final short actionId;
    public final ActionEntry actionEntry;

    public CopyTerrainDataPerformer() {
        actionId = (short) ModActions.getNextActionId();
        actionEntry = ActionEntry.createEntry(actionId, "Copy Terrain", "copying terrain to wand", new int[]{
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
    public boolean action(Action action, Creature player, Item wand, int tilex, int tiley, boolean onSurface, int heightOffset, int tile, short num, float counter) {
        try {
            if (player instanceof Player) {
                if (wand.getTemplateId() == ItemList.wandDeity) {
                    if (player.getPower() != Initiator.gmPower) {
                        byte type = Tiles.decodeType(tile);
                        wand.setAuxData(type);
                        ModData.set(wand, "jubaroo.copyTerrain.type", String.valueOf(type));
                        ModData.set(wand, "jubaroo.copyTerrain.data", String.valueOf(Tiles.decodeData(tile)));
                        player.getCommunicator().sendNormalServerMessage(String.format("You copy the %s data to your wand.", Tiles.getTile(type).getName().toLowerCase()));
                    } else {
                        Initiator.logWarning(String.format("[WARNING] Someone tried to use %s without GM privileges!", CopyTerrainDataPerformer.class.getName()));
                        return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
                    }
                } else {
                    player.getCommunicator().sendNormalServerMessage("You need to use a wand.");
                    return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
                }
            } else {
                Initiator.logWarning(String.format("[WARNING] Somehow a non-player activated action: %s", actionId));
                return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
            }
            return propagate(action, ActionPropagation.CONTINUE_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        } catch (Exception e) {
            Initiator.logException("[Error] in action in CopyTerrainDataPerformer", e);
            return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }
    }

}


