package org.jubaroo.mods.copypaste.actions.copy;

import com.wurmonline.mesh.GrassData;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.mesh.TreeData;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.behaviours.Crops;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.items.Materials;
import com.wurmonline.server.players.Player;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;
import org.jubaroo.mods.copypaste.Initiator;

public class CopyTerrainDataPerformer implements ActionPerformer {
    private final short actionId;
    public final ActionEntry actionEntry;

    public CopyTerrainDataPerformer() {
        actionId = (short) ModActions.getNextActionId();
        actionEntry = ActionEntry.createEntry(actionId, "Copy Terrain Data", "copying data to wand", new int[]{
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
            final Tiles.Tile theTile = Tiles.getTile(type);
            final String tileName = theTile.getName().toLowerCase();
            //farm
            final int crop = Crops.getCropNumber(type, data);
            final int tileAge = Crops.decodeFieldAge(data);
            final boolean farmed = Crops.decodeFieldState(data);
            // tree
            final TreeData.TreeType treeData = Materials.getTreeTypeForWood(type);
            // flowers
            final GrassData.GrowthStage growthStage = GrassData.GrowthStage.decodeTileData(Tiles.decodeData(tile));
            final GrassData.FlowerType flowerType = GrassData.FlowerType.decodeTileData(data);
            final byte flowerData = GrassData.encodeGrassTileData(growthStage, flowerType);

            if (performer instanceof Player) {
                if (source.getTemplateId() != ItemList.wandDeity) {
                    performer.getCommunicator().sendNormalServerMessage("You must use an ebony wand.");
                    return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
                }

                if (tile > 0) {
                    Initiator.logWarning(String.format("Something is wrong with the tile and its data is [%s]", type));
                    return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
                }

                // detect the type of tile being used
                // if normal - copy tile data to aux on wand - and also
                // if farm plot - copy crop, tended and age data
                // if tree - copy age data
                // if flower on grass - copy flower type

                source.setAuxData(type);

                if (crop > 0) {
                // stuff
                }

                if (Tiles.isTree(type) || Tiles.isBush(type)) {
                // stuff
                }

                if (Tiles.isFlower) {
                // stuff
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


