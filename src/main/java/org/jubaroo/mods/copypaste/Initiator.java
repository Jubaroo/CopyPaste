package org.jubaroo.mods.copypaste;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;
import org.jubaroo.mods.copypaste.actions.CopyMenuProvider;
import org.jubaroo.mods.copypaste.actions.copy.CopyTileDataAction;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Initiator implements WurmServerMod, ServerStartedListener, Configurable, PreInitable {
    public static final Logger logger = Logger.getLogger(Initiator.class.getName());
    public static boolean messageOnCopy = true;
    public static boolean placeLargeItemsOnGround = false;
    public static double weightLimit = 150.0d;
    public static int gmPower = 5;

    @Override
    public void configure(Properties properties) {
        Initiator.messageOnCopy = Boolean.parseBoolean(properties.getProperty("messageOnCopy", String.valueOf(Initiator.messageOnCopy)));
        Initiator.placeLargeItemsOnGround = Boolean.parseBoolean(properties.getProperty("placeItemsOnGround", String.valueOf(Initiator.placeLargeItemsOnGround)));
        Initiator.weightLimit = Double.parseDouble(properties.getProperty("weightLimit", String.valueOf(Initiator.weightLimit)));
        Initiator.gmPower = Integer.parseInt(properties.getProperty("gmPower", String.valueOf(Initiator.gmPower)));
        logger.log(Level.INFO, "========================== Copy Items Mod Settings =============================");
        logInfo(String.format("GM Power Required: %d", Initiator.gmPower));
        if (Initiator.messageOnCopy) {
            logInfo("Message To GM On Copy: Enabled");
        } else {
            logInfo("Message To GM On Copy: Disabled");
        }
        if (Initiator.placeLargeItemsOnGround) {
            logInfo("Large Items Placed On Ground: Enabled");
        } else {
            logInfo("Large Items Placed On Ground: Disabled");
        }
        logInfo(String.format("Weight Limit To Copy Items On Ground: %s kilograms", Initiator.weightLimit));
        logInfo(String.format("GM Power Level To Copy Items: %d", Initiator.gmPower));
        logger.log(Level.INFO, "========================== Copy Items Mod Settings =============================");
    }

    @Override
    public void preInit() {
        ModActions.init();
    }

    @Override
    public void onServerStarted() {
        logInfo("onServerStarted called");
        try {
            ModActions.registerBehaviourProvider(new CopyMenuProvider());
            ModActions.registerAction(new CopyTileDataAction());
        } catch (IllegalArgumentException | ClassCastException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Error in onServerStarted()", e);
        }
        logInfo("all onServerStarted completed");
    }

    public static void logException(String msg, Throwable e) {
        if (logger != null)
            logger.log(Level.SEVERE, msg, e);
    }

    public static void logWarning(String msg) {
        if (logger != null)
            logger.log(Level.WARNING, msg);
    }

    public static void logInfo(String msg) {
        if (logger != null)
            logger.log(Level.INFO, msg);
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

    public static void copyItemRestrictions(Item item, Item target) {
        // Copy all item restrictions
        item.setAuxData(target.getAuxData());
        item.setData1(target.getData1());
        item.setData2(target.getData2());
        item.setExtra1(target.getExtra1());
        item.setExtra2(target.getExtra2());
        item.setWeight(target.getWeightGrams(), true);
        item.setColor(target.getColor());
        item.setColor2(target.getColor2());
        item.setName(target.getName());
        item.setDescription(target.getDescription());
        item.setHasNoDecay(target.hasNoDecay());
        item.setIsIndestructible(target.isIndestructible());
        item.setIsNoPut(target.isNoPut());
        item.setIsNoTake(target.isNoTake());
        item.setIsNoMove(target.isNoMove());
        item.setIsNoImprove(target.isNoImprove());
        item.setIsNotLockable(target.isNotLockable());
        item.setIsNoDrop(target.isNoDrop());
        item.setIsNotPaintable(target.isNotPaintable());
        item.setIsNotLockpickable(target.isNotLockpickable());
        item.setIsNoDrag(target.isNoDrag());
        item.setIsNoRepair(target.isNoRepair());
        item.setIsNotRuneable(target.isNotRuneable());
        item.setIsAlwaysLit(target.isAlwaysLit());
        item.setIsAutoLit(target.isAutoLit());
        item.setIsAutoFilled(target.isAutoFilled());
        item.setIsOwnerMoveable(target.isOwnerTurnable());
        item.setIsOwnerTurnable(target.isOwnerTurnable());
        item.setIsNotTurnable(target.isNotTurnable());
        item.setIsSealedByPlayer(target.isSealedByPlayer());
        item.setIsNotSpellTarget(target.isNotSpellTarget());
    }

    /*
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
*/
    public String getVersion() {
        return "v1.4"; // updated 3/26/19
    }

    //TODO
    // question for multiple copies instead of multiple choice
    // copy locks
    // copy food
    // copy inscriptions
    // copy items inside containers
    // copy catseye

}