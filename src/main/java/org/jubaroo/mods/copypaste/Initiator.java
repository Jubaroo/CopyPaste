package org.jubaroo.mods.copypaste;

import com.wurmonline.server.MiscConstants;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;
import org.jubaroo.mods.copypaste.actions.CopyMenuProvider;
import org.jubaroo.mods.copypaste.actions.CopyTerrainMenuProvider;
import org.jubaroo.mods.copypaste.actions.copy.CopyTerrainDataPerformer;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Initiator implements WurmServerMod, ServerStartedListener, Configurable, PreInitable {
    public static final Logger logger = Logger.getLogger(Initiator.class.getName());
    public static int gmPower = MiscConstants.POWER_IMPLEMENTOR;

    @Override
    public void configure(Properties properties) {
        Initiator.gmPower = Integer.parseInt(properties.getProperty("gmPower", String.valueOf(Initiator.gmPower)));
        logInfo(String.format("GM Power Level To Copy Items: %d", Initiator.gmPower));
    }

    @Override
    public void preInit() {
        ModActions.init();
    }

    @Override
    public void onServerStarted() {
        try {
            ModActions.registerBehaviourProvider(new CopyMenuProvider());
            ModActions.registerBehaviourProvider(new CopyTerrainMenuProvider());
        } catch (IllegalArgumentException | ClassCastException e) {
            e.printStackTrace();
            logException("Error in onServerStarted()", e);
        }
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
        return "v1.5"; // updated 3/26/19
    }

    //TODO
    // message telling that a locked container will not stay locked when copied
    // copy food
    // copy inscriptions
    // copy items inside containers

}