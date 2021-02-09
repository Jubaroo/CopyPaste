package org.jubaroo.mods.copypaste.actions.copy;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.items.*;
import com.wurmonline.server.spells.SpellEffect;
import com.wurmonline.server.zones.NoSuchZoneException;
import org.jubaroo.mods.copypaste.Initiator;

public class CopyHelper {

    static public void copyItem(Creature performer, Item target) throws NoSuchTemplateException, FailedException {
        Item item;
        //int temp = target.getTemperature();
        //float nut = target.getNutritionLevel();

        // Copy all item restrictions
        item = ItemFactory.createItem(target.getTemplateId(), target.getCurrentQualityLevel(), target.getMaterial(), target.getRarity(), target.getCreatorName());
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
        item.setHasCourier(target.hasCourier());
        item.setHasDarkMessenger(target.hasDarkMessenger());
        item.savePermissions();

        if (target.getTemplate().getTemplateId() == ItemList.catseye) {
            item.setAuxData((byte) 0);
        }

        // Copy lock if applicable
        //if (target.isLocked()) {
        //    if (copy != null) {
        //        Initiator.lock(target);
        //    }
        //}

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
            item.bless(target.getBless().getNumber());
        }

        // Copy boolean enchant if applicable
        if (target.enchantment != 0) {
            item.enchant(target.enchantment);
        }

        // Copy non boolean enchants
        ItemSpellEffects targetSpellEffects = target.getSpellEffects();
        ItemSpellEffects itemSpellEffects;
        itemSpellEffects = item.getSpellEffects();
        if (targetSpellEffects == null) {
            targetSpellEffects = new ItemSpellEffects(target.getWurmId());
        }
        if (itemSpellEffects == null) {
            itemSpellEffects = new ItemSpellEffects(item.getWurmId());
        }
        for (SpellEffect spellEffect : targetSpellEffects.getEffects()) {
            byte type = spellEffect.type;
            SpellEffect newEff;
            newEff = new SpellEffect(item.getWurmId(), type, spellEffect.getPower(), spellEffect.timeleft);
            itemSpellEffects.addSpellEffect(newEff);
        }

        // Place item on ground if config is set
        if (Initiator.placeLargeItemsOnGround && target.getWeightGrams() >= Initiator.weightLimit * 1000) {
            try {
                item.putItemInfrontof(performer, 2f);
            } catch (NoSuchCreatureException | NoSuchItemException | NoSuchPlayerException | NoSuchZoneException e) {
                e.printStackTrace();
            }
        } else {
            // Place item in inventory if config set
            performer.getInventory().insertItem(item);
        }
    }

}
