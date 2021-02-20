package org.jubaroo.mods.copypaste.actions.paste;

import com.wurmonline.mesh.GrassData;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;
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

public class PasteTerrainDataPerformer implements ActionPerformer {
    private final short actionId;
    public final ActionEntry actionEntry;

    public PasteTerrainDataPerformer() {
        actionId = (short) ModActions.getNextActionId();
        actionEntry = ActionEntry.createEntry(actionId, "Paste Terrain", "pasting terrain", new int[]{
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
                        final String typeString = ModData.getNullable(wand, "jubaroo.copyTerrain.type");
                        final String dataString = ModData.getNullable(wand, "jubaroo.copyTerrain.data");

                        if (typeString != null && dataString != null) {
                            final byte type = Tiles.decodeType(Byte.parseByte(typeString));
                            final byte data = Tiles.decodeData(Byte.parseByte(dataString));
                            final GrassData.FlowerType flowerType = GrassData.FlowerType.decodeTileData(data);
                            Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), type, GrassData.encodeGrassTileData(GrassData.GrowthStage.decodeTileData(data), flowerType));
                            Players.getInstance().sendChangedTile(tilex, tiley, true, false);
                        } else {
                            player.getCommunicator().sendNormalServerMessage("The wand does not have any data stored.");
                            return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
                        }
                    } else {
                        Initiator.logWarning(String.format("[WARNING] Someone tried to use %s without GM privileges!", PasteTerrainDataPerformer.class.getName()));
                        return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
                    }
                } else {
                    player.getCommunicator().sendNormalServerMessage("You need to use a wand.");
                    return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
                }
            } else {
                Initiator.logWarning(String.format("[WARNING] Somehow a non-player activated action: %s", actionId));
            }
            return propagate(action, ActionPropagation.CONTINUE_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        } catch (Exception e) {
            Initiator.logException("[Error] in action in CopyTerrainDataPerformer", e);
            return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }
    }

}

