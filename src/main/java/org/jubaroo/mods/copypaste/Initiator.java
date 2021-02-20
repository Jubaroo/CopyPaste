package org.jubaroo.mods.copypaste;

import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;
import org.jubaroo.mods.copypaste.actions.CopyTerrainAction;
import org.jubaroo.mods.copypaste.actions.ItemMenuProvider;
import org.jubaroo.mods.copypaste.actions.TerrainMenuProvider;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Initiator implements WurmServerMod, ServerStartedListener, Configurable, PreInitable {
    public static final Logger logger = Logger.getLogger(Initiator.class.getName());
    public static int gmPower;

    @Override
    public void configure(Properties properties) {
        gmPower = Integer.parseInt(properties.getProperty("gmPower"));
        logInfo(String.format("GM Power Level To Copy Items: %d", gmPower));
    }

    @Override
    public void preInit() {
        ModActions.init();
    }

    @Override
    public void onServerStarted() {
        try {
            ModActions.registerBehaviourProvider(new ItemMenuProvider());
            ModActions.registerBehaviourProvider(new TerrainMenuProvider());
            ModActions.registerAction(new CopyTerrainAction());
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

    public String getVersion() {
        return "v1.5.4";
    }

    //TODO
    // copy food
    // copy inscriptions
    // copy items inside containers and piles

}