package org.jubaroo.mods.copypaste.actions.paste;

import com.wurmonline.mesh.GrassData;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.behaviours.Crops;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.players.Player;
import org.gotti.wurmunlimited.modsupport.actions.*;
import org.jubaroo.mods.copypaste.Initiator;

import java.util.Collections;
import java.util.List;

public class PasteTerrainDataPerformer implements ActionPerformer {
    private final short actionId;
    public final ActionEntry actionEntry;

    public PasteTerrainDataPerformer() {
        actionId = (short) ModActions.getNextActionId();
        actionEntry = ActionEntry.createEntry(actionId, "Paste Terrain Data", "pasting data to wand", new int[]{
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
    public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, int tile, short num, float counter) {
        try {
            final byte data = Tiles.decodeData(tile);
            final byte type = Tiles.decodeType(tile);
            final int crop = Crops.getCropNumber(type, data);
            final Tiles.Tile theTile = Tiles.getTile(type);
            final String tileName = theTile.getName().toLowerCase();

            if (performer instanceof Player) {

                // If not holding a wand, we abort the action
                if (source.getTemplateId() != ItemList.wandDeity) {
                    performer.getCommunicator().sendNormalServerMessage("You must use an ebony wand.");
                    return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
                }

                if (tile > 0) {
                    Initiator.logWarning(String.format("Something is wrong with the tile and its data is [%s]", type));
                    return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
                }



                //flower
                Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), Tiles.Tile.TILE_GRASS.id, GrassData.encodeGrassTileData(growthStage, GrassData.FlowerType.NONE));


            } else {
                Initiator.logWarning(String.format("[WARNING] Somehow a non-player activated action: %s", actionId));
            }
            return propagate(action, ActionPropagation.CONTINUE_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        } catch (Exception e) {
            Initiator.logException("[Error] in action in CopyTerrainDataPerformer", e);
            return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }
    }


    private static void setFarmTile(int tilex, int tiley, int tile, byte crop) {
        Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), Crops.getTileType(crop), Crops.encodeFieldData(false, 0, crop));
    }

    private static void setFarmTile(int tilex, int tiley, int tile, byte crop, boolean tended, int fieldAge) {
        Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), Crops.getTileType(crop), Crops.encodeFieldData(tended, fieldAge, crop));
    }

}

