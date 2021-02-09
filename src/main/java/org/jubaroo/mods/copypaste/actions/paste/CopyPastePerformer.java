package org.jubaroo.mods.copypaste.actions.paste;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.*;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.spells.SpellEffect;
import com.wurmonline.shared.util.MaterialUtilities;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;
import org.jubaroo.mods.copypaste.Initiator;

public class CopyPastePerformer implements ActionPerformer {
    private final short actionId;
    public final ActionEntry actionEntry;

    public CopyPastePerformer() {
        actionId = (short) ModActions.getNextActionId();
        actionEntry = ActionEntry.createEntry(actionId, "Copy & Paste", "copying & pasting", new int[]{
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
                return propagate(act, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
            }
            try {
                copyPastItem(performer, target);
            } catch (NoSuchTemplateException | FailedException e) {
                e.printStackTrace();
            }
        } else {
            Initiator.logWarning("Somehow a non-player activated copyPaste action...");
        }
        return propagate(act, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
    }

    @Override
    public boolean action(Action act, Creature performer, Item source, Item target, short action, float counter) {
        return this.action(act, performer, target, action, counter);
    }

    private void copyPastItem(Creature performer, Item target) throws NoSuchTemplateException, FailedException {
        Item item;
        item = ItemFactory.createItem(target.getTemplateId(), target.getCurrentQualityLevel(), target.getMaterial(), target.getRarity(), target.getCreatorName());

        //Temperature
        //int temp = target.getTemperature();

        //Food
        //float nut = target.getNutritionLevel();
        //if (target.isFood()) {
        //    if (item != null) {
        //        item.nut
        //    }
        //}

        // Item Restrictions
        Initiator.copyItemRestrictions(item, target);

        //Catseye
        if (target.getTemplate().getTemplateId() == ItemList.catseye) {
            item.setAuxData((byte) 0);
        }

        // Locks
        // Copy lock if applicable
        //if (target.isLocked()) {
        //    if (copy != null) {
        //        Initiator.lock(target);
        //    }
        //}

        // Inscriptions
        //if (item != null) {
        //    final boolean hasInscription = item.canHaveInscription() && item.getInscription() != null && item.getInscription().hasBeenInscribed();
        //    if (hasInscription) {
        //        item.setInscription(Objects.requireNonNull(target.getInscription()).getInscription(), target.getInscription().getInscriber());
        //    }
        //}

        // Blessings
        if (target.getBless() != null) {
            item.bless(target.getBless().getNumber());
        }

        //Courier/Dark Messenger
        item.setHasCourier(target.hasCourier());
        item.setHasDarkMessenger(target.hasDarkMessenger());

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
        // save all changes to item
        item.savePermissions();

        // Begin placing the item
        performer.getInventory().insertItem(item, true, false);
        performer.getCommunicator().sendPlaceItem(item);
        performer.setPlacingItem(true);

        // Optional message when item is copied
        if (Initiator.messageOnCopy) {
            performer.getCommunicator().sendNormalServerMessage(String.format("You copy the %s and all it's data. The rarity is: %s, the material is: %s, and the quality is: %s", target.getName(), Initiator.getRarityString(target.getRarity()), MaterialUtilities.getMaterialString(target.getMaterial()), target.getCurrentQualityLevel()));
        }

        // Cancel placing action if no longer placing an item
        if (!performer.isPlacingItem()) {
            performer.getCommunicator().sendCancelPlacingItem();
            performer.setPlacingItem(false, item);
        }

    }

}

