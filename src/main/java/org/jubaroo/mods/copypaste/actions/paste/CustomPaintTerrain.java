package org.jubaroo.mods.copypaste.actions.paste;

import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.mesh.GrassData;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Terraforming;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;

/**
 * Created by Jubaroo on 2/16/2021
 */

public class CustomPaintTerrain {

    public static void pasteTerrain(final Player player, final Item wand, final int tileX, final int tileY) {
        final byte aux = wand.getAuxData();
        if (aux == 0) {
            return;
        }
        final byte newtype = Tiles.getTile(aux).id;
        if (!player.isOnSurface()) {
            Terraforming.paintCaveTerrain(player, newtype, tileX, tileY);
            return;
        }
        int dx = Math.max(0, wand.getData1());
        int dy = Math.max(0, wand.getData2());
        if (dx > 10) {
            dx = 0;
        }
        if (dy > 10) {
            dy = 0;
        }
        if (dx > 10 || dy > 10 || dx < 0 || dy < 0) {
            player.getCommunicator().sendNormalServerMessage("The data1 and data2 range should be between 0 and 10.");
            return;
        }
        if (dx == 0 && dy == 0 && Tiles.decodeType(Server.surfaceMesh.getTile(tileX, tileY)) == newtype) {
            player.getCommunicator().sendNormalServerMessage("The terrain is already of that type.");
            return;
        }
        if (Tiles.isSolidCave(newtype) || newtype == Tiles.Tile.TILE_CAVE.id || newtype == Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id) {
            if (player.getPower() >= MiscConstants.POWER_IMPLEMENTOR) {
                if (Tiles.isSolidCave(newtype)) {
                    final Tiles.Tile theNewTile = Tiles.getTile(newtype);
                    if (theNewTile != null) {
                        Server.caveMesh.setTile(tileX, tileY, Tiles.encode(Tiles.decodeHeight(Server.caveMesh.getTile(tileX, tileY)), theNewTile.id, Tiles.decodeData(Server.caveMesh.getTile(tileX, tileY))));
                        Players.getInstance().sendChangedTiles(tileX, tileY, 1, 1, false, false);
                    }
                }
                else {
                    player.getCommunicator().sendNormalServerMessage("You can only change to solid rock types at the moment.");
                }
            }
            else {
                player.getCommunicator().sendNormalServerMessage("Only implementors may set the terrain to some sort of rock.");
            }
            return;
        }
        for (int x = 0; x < Math.max(1, dx); ++x) {
            for (int y = 0; y < Math.max(1, dy); ++y) {
                final byte oldType = Tiles.decodeType(Server.surfaceMesh.getTile(tileX - dx / 2 + x, tileY - dy / 2 + y));
                if (player.getPower() < 5 && (newtype == Tiles.Tile.TILE_ROCK.id || oldType == Tiles.Tile.TILE_ROCK.id || newtype == Tiles.Tile.TILE_CLIFF.id || oldType == Tiles.Tile.TILE_CLIFF.id)) {
                    player.getCommunicator().sendNormalServerMessage("That would have impact on the rock layer, and is not allowed for now.");
                }
                else {
                    final Tiles.Tile theNewTile2 = Tiles.getTile(newtype);
                    byte data = 0;
                    final byte theNewType;
                    if ((theNewType = newtype) == Tiles.Tile.TILE_GRASS.id) {
                        final GrassData.FlowerType flowerType = Terraforming.getRandomFlower(GrassData.FlowerType.NONE, false);
                        if (flowerType != GrassData.FlowerType.NONE) {
                            final GrassData.GrowthStage stage = GrassData.GrowthStage.decodeTileData(0);
                            data = GrassData.encodeGrassTileData(stage, flowerType);
                        }
                    }
                    if (newtype == Tiles.Tile.TILE_ROCK.id) {
                        Server.caveMesh.setTile(tileX - dx / 2 + x, tileY - dy / 2 + y, Tiles.encode((short)(-100), Tiles.Tile.TILE_CAVE_WALL.id, (byte)0));
                        Server.rockMesh.setTile(tileX - dx / 2 + x, tileY - dy / 2 + y, Tiles.encode(Tiles.decodeHeight(Server.surfaceMesh.getTile(tileX - dx / 2 + x, tileY - dy / 2 + y)), Tiles.Tile.TILE_ROCK.id, (byte)0));
                    }
                    else if (theNewTile2.isTree() || theNewTile2.isBush()) {
                        final byte treeAge = (byte)Server.rand.nextInt(FoliageAge.values().length);
                        final byte grass = (byte)(1 + Server.rand.nextInt(3));
                        data = Tiles.encodeTreeData(treeAge, false, false, grass);
                    }
                    if (Tiles.getTile(aux).id == Tiles.Tile.TILE_ROCK.id) {
                        Server.caveMesh.setTile(tileX - dx / 2 + x, tileY - dy / 2 + y, Tiles.encode((short)(-100), Tiles.Tile.TILE_CAVE_WALL.id, (byte)0));
                        Server.rockMesh.setTile(tileX - dx / 2 + x, tileY - dy / 2 + y, Tiles.encode(Tiles.decodeHeight(Server.surfaceMesh.getTile(tileX - dx / 2 + x, tileY - dy / 2 + y)), Tiles.Tile.TILE_ROCK.id, (byte)0));
                    }
                    else if (oldType != Tiles.Tile.TILE_HOLE.id && !Tiles.isMineDoor(oldType)) {
                        Server.setSurfaceTile(tileX - dx / 2 + x, tileY - dy / 2 + y, Tiles.decodeHeight(Server.surfaceMesh.getTile(tileX - dx / 2 + x, tileY - dy / 2 + y)), theNewType, data);
                    }
                }
            }
        }
        Players.getInstance().sendChangedTiles(tileX - dx / 2, tileY - dy / 2, Math.max(1, dx), Math.max(1, dy), true, true);
    }

}
