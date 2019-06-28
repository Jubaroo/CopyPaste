package org.jubaroo.mods.copyitems;

import com.wurmonline.server.DbConnector;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.Items;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.behaviours.MethodsItems;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.ItemMealData;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.utils.DbUtilities;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;
import org.jubaroo.mods.copyitems.actions.CopyMenuProvider;
import org.jubaroo.mods.copyitems.actions.CopyMultipleMenuProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Initiator implements WurmServerMod, ServerStartedListener, Configurable, PreInitable {
    private static Logger logger = Logger.getLogger(Initiator.class.getName());
    public static boolean copyAction = true;
    public static boolean copyPasteAction = true;
    private static boolean copyMultipleAction = true;
    public static boolean messageOnCopy = false;
    public static boolean placeLargeItemsOnGround = false;
    public static double weightLimit = 150.0d;
    private static int gmPower = 5;
    private static boolean debug = false;

    @Override
    public void configure(Properties properties) {
        Initiator.copyAction = Boolean.parseBoolean(properties.getProperty("copyAction", String.valueOf(Initiator.copyAction)));
        Initiator.copyPasteAction = Boolean.parseBoolean(properties.getProperty("copyPasteAction", String.valueOf(Initiator.copyPasteAction)));
        Initiator.copyMultipleAction = Boolean.parseBoolean(properties.getProperty("copyMultipleAction", String.valueOf(Initiator.copyMultipleAction)));
        Initiator.messageOnCopy = Boolean.parseBoolean(properties.getProperty("messageOnCopy", String.valueOf(Initiator.messageOnCopy)));
        Initiator.placeLargeItemsOnGround = Boolean.parseBoolean(properties.getProperty("placeItemsOnGround", String.valueOf(Initiator.placeLargeItemsOnGround)));
        Initiator.weightLimit = Double.parseDouble(properties.getProperty("weightLimit", String.valueOf(Initiator.weightLimit)));
        Initiator.gmPower = Integer.parseInt(properties.getProperty("gmPower", String.valueOf(Initiator.gmPower)));
        Initiator.debug = Boolean.parseBoolean(properties.getProperty("debug", String.valueOf(Initiator.debug)));
        logger.log(Level.INFO, "========================== Copy Items Mod Settings =============================");
        if (Initiator.debug) {
            logger.log(Level.INFO, "Mod Logging: Enabled");
        } else {
            logger.log(Level.INFO, "Mod Logging: Disabled");
        }
        if (Initiator.copyAction) {
            jDebug("Copy Actions: Enabled");
        } else {
            jDebug("Copy Actions: Disabled");
        }
        if (Initiator.copyPasteAction) {
            jDebug("Copy & Paste Action: Enabled");
        } else {
            jDebug("Copy & Paste Action: Disabled");
        }
        if (Initiator.copyMultipleAction) {
            jDebug("Copy Multiple Items Action: Enabled");
        } else {
            jDebug("Copy Multiple Items Action: Disabled");
        }
        if (Initiator.messageOnCopy) {
            jDebug("Message To GM On Copy: Enabled");
        } else {
            jDebug("Message To GM On Copy: Disabled");
        }
        if (Initiator.placeLargeItemsOnGround) {
            jDebug("Boats And Wagons Copied On Ground: Enabled");
        } else {
            jDebug("Boats And Wagons Copied On Ground: Disabled");
        }
        jDebug("Weight Limit To Copy Items On Ground: " + Initiator.weightLimit + " kilograms");
        jDebug("GM Power Level To Copy Items: " + Initiator.gmPower);
        logger.log(Level.INFO, "========================== Copy Items Mod Settings =============================");
    }

    @Override
    public void preInit() {
        ModActions.init();
    }

    @Override
    public void onServerStarted() {
        jDebug("onServerStarted called");
        try {
            ModActions.registerBehaviourProvider(new CopyMenuProvider());
            if (copyMultipleAction) {
                ModActions.registerBehaviourProvider(new CopyMultipleMenuProvider());
            }
        } catch (IllegalArgumentException | ClassCastException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Error in onServerStarted()", e);
        }
        jDebug("all onServerStarted completed");
    }

    public static void jDebug(String msg) {
        if (debug) {
            logger.log(Level.INFO, msg);
        }
    }

    public static String getRarityString(byte rarity) {
        switch (rarity) {
            case 0:
                return "common ";
            case 1:
                return "rare ";
            case 2:
                return "supreme ";
            case 3:
                return "fantastic ";
            default:
                return "";
        }
    }

    public static boolean canUse(Creature performer, Item object) {
        return performer.isPlayer() && object != null && performer.getPower() >= Initiator.gmPower && performer.getPower() != 0 &&
                !object.isInventory() && !object.isInventoryGroup() && !object.isTopParentPile() && !object.isBodyPart();
    }

    public static void lock(Item target) throws NoSuchTemplateException, FailedException, NoSuchItemException {
        final long oldLockId = target.getLockId();
        if (oldLockId != -10L) {
            Item oldLock = Items.getItem(oldLockId);
            Item newLock = ItemFactory.createItem(oldLock.getTemplateId(), oldLock.getCurrentQualityLevel(), oldLock.getMaterial(), oldLock.getRarity(), oldLock.getBridgeId(), oldLock.getCreatorName());
            newLock.putInVoid();
            target.setLockId(newLock.getWurmId());
            newLock.setLocked(true);
        }
    }

    private static void dbSaveMealData(Item target, Item copy) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        final ItemMealData imd = ItemMealData.getItemMealData(target.getWurmId());
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("INSERT INTO MEALDATA(MEALID,RECIPEID,CALORIES,CARBS,FATS,PROTEINS,BONUS,STAGESCOUNT,INGREDIENTSCOUNT) VALUES(?,?,?,?,?,?,?,?,?)");
            ps.setLong(1, copy.getWurmId());
            ps.setShort(2, imd.getRecipeId());
            ps.setShort(3, target.getCalories());
            ps.setShort(4, target.getCarbs());
            ps.setShort(5, target.getFats());
            ps.setShort(6, target.getProteins());
            ps.setByte(7, (byte) target.getBonus());
            ps.setByte(8, target.getFoodStages());
            ps.setByte(9, target.getFoodIngredients());
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            logger.log(Level.WARNING, "Failed to save item (meal) data: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }

    public String getVersion() {
        return "v1.3"; // updated 3/26/19
    }

    //TODO
    // copy locks
    // copy food
    // copy inscriptions
    // copy items inside containers

}