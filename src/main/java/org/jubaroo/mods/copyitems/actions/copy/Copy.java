package org.jubaroo.mods.copyitems.actions.copy;

import com.wurmonline.server.DbConnector;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.items.*;
import com.wurmonline.server.spells.SpellEffect;
import com.wurmonline.server.utils.DbUtilities;
import com.wurmonline.server.zones.NoSuchZoneException;
import org.jubaroo.mods.copyitems.Initiator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

class Copy {
    private static Logger logger = Logger.getLogger(Initiator.class.getName());

    static void copyItem(Creature performer, Item target) throws NoSuchTemplateException, FailedException, NoSuchItemException {
        Item copy = null;
        //int temp = target.getTemperature();
        //float nut = target.getNutritionLevel();
//
        try {
            // Copy all item restrictions
            copy = ItemFactory.createItem(target.getTemplateId(), target.getCurrentQualityLevel(), target.getMaterial(), target.getRarity(), target.getCreatorName());
            copy.setAuxData(target.getAuxData());
            copy.setData1(target.getData1());
            copy.setData2(target.getData2());
            copy.setExtra1(target.getExtra1());
            copy.setExtra2(target.getExtra2());
            copy.setWeight(target.getWeightGrams(), true);
            copy.setColor(target.getColor());
            copy.setColor2(target.getColor2());
            copy.setName(target.getName());
            copy.setDescription(target.getDescription());
            copy.setHasNoDecay(target.hasNoDecay());
            copy.setIsIndestructible(target.isIndestructible());
            copy.setIsNoPut(target.isNoPut());
            copy.setIsNoTake(target.isNoTake());
            copy.setIsNoMove(target.isNoMove());
            copy.setIsNoImprove(target.isNoImprove());
            copy.setIsNotLockable(target.isNotLockable());
            copy.setIsNoDrop(target.isNoDrop());
            copy.setIsNotPaintable(target.isNotPaintable());
            copy.setIsNotLockpickable(target.isNotLockpickable());
            copy.setIsNoDrag(target.isNoDrag());
            copy.setIsNoRepair(target.isNoRepair());
            copy.setIsNotRuneable(target.isNotRuneable());
            copy.setIsAlwaysLit(target.isAlwaysLit());
            copy.setIsAutoLit(target.isAutoLit());
            copy.setIsAutoFilled(target.isAutoFilled());
            copy.setIsOwnerMoveable(target.isOwnerTurnable());
            copy.setIsOwnerTurnable(target.isOwnerTurnable());
            copy.setIsNotTurnable(target.isNotTurnable());
            copy.setIsSealedByPlayer(target.isSealedByPlayer());
            copy.setIsNotSpellTarget(target.isNotSpellTarget());
            copy.setHasCourier(target.hasCourier());
            copy.setHasDarkMessenger(target.hasDarkMessenger());
            copy.savePermissions();
        } catch (FailedException | NoSuchTemplateException e) {
            e.printStackTrace();
        }
        // Copy lock if applicable
        if (target.isLocked()) {
            if (copy != null) {
                Initiator.lock(target);
            }
        }
        // Copy nutrition of food
        //if (target.isFood()) {
        //    if (copy != null) {
        //        dbSaveMealData(target, copy);
        //        //Recipe recipe = Recipes.getRecipeFor(performer.getWurmId(), (byte)0, null, target, true, true);
        //        //if (recipe != null) {
        //        //    item.calculateAndSaveNutrition(null, target, recipe);
        //        //}
        //    }
        //}
        // Copy inscriptions
        //if (item != null) {
        //    final boolean hasInscription = item.canHaveInscription() && item.getInscription() != null && item.getInscription().hasBeenInscribed();
        //    if (hasInscription) {
        //        item.setInscription(Objects.requireNonNull(target.getInscription()).getInscription(), target.getInscription().getInscriber());
        //    }
        //}
        // Copy blessing if applicable
        if (target.getBless() != null) {
            if (copy != null) {
                copy.bless(target.getBless().getNumber());
            }
        }
        // Copy boolean enchant if applicable
        if (target.enchantment != 0) {
            if (copy != null) {
                copy.enchant(target.enchantment);
            }
        }
        // Copy non boolean enchants
        ItemSpellEffects targetSpellEffects = target.getSpellEffects();
        ItemSpellEffects itemSpellEffects = null;
        if (copy != null) {
            itemSpellEffects = copy.getSpellEffects();
        }
        if (targetSpellEffects == null) {
            targetSpellEffects = new ItemSpellEffects(target.getWurmId());
        }
        if (itemSpellEffects == null) {
            if (copy != null) {
                itemSpellEffects = new ItemSpellEffects(copy.getWurmId());
            }
        }
        for (SpellEffect spellEffect : targetSpellEffects.getEffects()) {
            byte type = spellEffect.type;
            SpellEffect newEff = null;
            if (copy != null) {
                newEff = new SpellEffect(copy.getWurmId(), type, spellEffect.getPower(), spellEffect.timeleft);
            }
            if (itemSpellEffects != null) {
                itemSpellEffects.addSpellEffect(newEff);
            }
        }
        // Place item on ground if config is set
        if (Initiator.placeLargeItemsOnGround && target.getWeightGrams() >= Initiator.weightLimit * 1000) {
            try {
                if (copy != null) {
                    copy.putItemInfrontof(performer, 2f);
                }
            } catch (NoSuchCreatureException | NoSuchItemException | NoSuchPlayerException | NoSuchZoneException e) {
                e.printStackTrace();
            }
        } else {
            // Place item in inventory if config set
            if (copy != null) {
                performer.getInventory().insertItem(copy);
            }
        }
    }
}
