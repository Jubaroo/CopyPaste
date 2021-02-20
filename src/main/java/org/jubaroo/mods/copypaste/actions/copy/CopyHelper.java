package org.jubaroo.mods.copypaste.actions.copy;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.*;
import com.wurmonline.server.spells.SpellEffect;
import org.jubaroo.mods.copypaste.Initiator;
import org.jubaroo.mods.copypaste.actions.paste.CopyPastePerformer;

public class CopyHelper {

    @SuppressWarnings("ConstantConditions")
    public static boolean canUse(Creature performer, Item object) {
        return performer.isPlayer() && object != null && performer.getPower() >= Initiator.gmPower ||
                (!object.isInventory() || !object.isInventoryGroup() || !object.isTopParentPile() || !object.isBodyPart());
    }

    public static void copyItemData(Creature player, Item target, ActionEntry act) throws NoSuchTemplateException, FailedException {
        Item item = ItemFactory.createItem(target.getTemplateId(), target.getCurrentQualityLevel(), target.getMaterial(), target.getRarity(), target.getCreatorName());

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
        item.setIsPlanted(target.isPlanted());
        item.savePermissions();

        if (target.getTemplate().getTemplateId() == ItemList.catseye) {
            item.setAuxData((byte) 0);
        }

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

        //Courier/Dark Messenger
        item.setHasCourier(target.hasCourier());
        item.setHasDarkMessenger(target.hasDarkMessenger());

        // Message to warn target will not copy the lock
        if (target.isLocked()) {
            player.getCommunicator().sendNormalServerMessage("Locked items will not copy locks.");
        }

        // Copy inscriptions
        //if (item != null) {
        //    final boolean hasInscription = item.canHaveInscription() && item.getInscription() != null && item.getInscription().hasBeenInscribed();
        //    if (hasInscription) {
        //        item.setInscription(Objects.requireNonNull(target.getInscription()).getInscription(), target.getInscription().getInscriber());
        //    }
        //}

        if (act == CopyPastePerformer.actionEntry) {
            // Begin placing the item
            player.getInventory().insertItem(item, true, false);
            player.getCommunicator().sendPlaceItem(item);
            player.setPlacingItem(true);

            // Cancel placing action if no longer placing an item
            if (!player.isPlacingItem()) {
                player.getCommunicator().sendCancelPlacingItem();
                player.setPlacingItem(false, item);
            }
        } else {
            player.getInventory().insertItem(item);
        }

    }

}

