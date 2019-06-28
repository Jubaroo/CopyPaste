package org.jubaroo.mods.copyitems.actions.paste;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.ItemSpellEffects;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.spells.SpellEffect;
import com.wurmonline.shared.util.MaterialUtilities;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;
import org.jubaroo.mods.copyitems.Initiator;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CopyPastePerformer implements ActionPerformer {
    private static Logger logger = Logger.getLogger(CopyPastePerformer.class.getName());
    private final short actionId;
    public final ActionEntry actionEntry;

    public CopyPastePerformer() {
        actionId = (short) ModActions.getNextActionId();
        actionEntry = ActionEntry.createEntry(actionId, "Copy & Paste", "copying", new int[]{
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
    public boolean action(Action act, Creature performer, Item target, short action, float counter) {
        if (performer instanceof Player) {
            if (!Initiator.canUse(performer, target)) {
                performer.getCommunicator().sendNormalServerMessage("You cannot copy that right now.");
                return true;
            }
            try {
                copyPastItem(performer, target);
            } catch (NoSuchTemplateException | FailedException | NoSuchItemException e) {
                e.printStackTrace();
            }
            //return true;
        } else {
            logger.log(Level.WARNING, "Somehow a non-player activated copyPaste action...");
        }
        return true;
    }

    @Override
    public boolean action(Action act, Creature performer, Item source, Item target, short action, float counter) {
        return this.action(act, performer, target, action, counter);
    }

    private void copyPastItem(Creature performer, Item target) throws NoSuchTemplateException, FailedException, NoSuchItemException {
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
        //    if (item != null) {
        //        item.nut
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
        // Begin placing the item
        if (copy != null) {
            performer.getInventory().insertItem(copy, true, false);
            performer.getCommunicator().sendPlaceItem(copy);
        }
        performer.setPlacingItem(true);
        // Optional message when item is copied
        if (Initiator.messageOnCopy) {
            performer.getCommunicator().sendNormalServerMessage(String.format("You copy the %s and all it's data. The rarity is: %s, the material is: %s, and the quality is: %s", target.getName(), Initiator.getRarityString(target.getRarity()), MaterialUtilities.getMaterialString(target.getMaterial()), target.getCurrentQualityLevel()));
        }
        // Cancel placing action if no longer placing an item
        if (!performer.isPlacingItem()) {
            performer.getCommunicator().sendCancelPlacingItem();
            if (copy != null) {
                performer.setPlacingItem(false, copy);
            }
        }

    }
}

