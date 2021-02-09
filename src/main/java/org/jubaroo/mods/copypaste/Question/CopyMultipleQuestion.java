package org.jubaroo.mods.copypaste.Question;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.questions.Question;
import org.gotti.wurmunlimited.modsupport.questions.ModQuestion;
import org.gotti.wurmunlimited.modsupport.questions.ModQuestions;
import org.jubaroo.mods.copypaste.Initiator;
import org.jubaroo.mods.copypaste.actions.copy.CopyHelper;

import java.util.Properties;

public class CopyMultipleQuestion implements ModQuestion {
    private final Creature performer;
    private final Item item;

    public CopyMultipleQuestion(Creature performer, Item item) {
        this.performer = performer;
        this.item = item;
    }

    @Override
    public void sendQuestion(Question question) {
        String buf = String.format("%sharray{input{text='Multiplier to resize the item'; id='amount'; maxchars='6'; text=''}}text{text=''}text{text=''}%s", ModQuestions.getBmlHeader(question), ModQuestions.createAnswerButton2(question, "Confirm"));
        question.getResponder().getCommunicator().sendBml(300, 200, false, true, buf, 200, 200, 200, question.getTitle());
    }

    @Override
    public void answer(Question question, Properties answer) {
        String newSize = answer.getProperty("amount");

        if (newSize.isEmpty()) {
            performer.getCommunicator().sendNormalServerMessage("The item amount field is blank! Nothing changed.");
            return;
        }

        if (!containsIllegalCharacters(newSize, performer)) {
            int amount = (int) Float.parseFloat(answer.getProperty("amount"));
            for (int i = 0; i < amount; i++) {
                try {
                    CopyHelper.copyItem(performer, item);
                } catch (NoSuchTemplateException | FailedException e) {
                    e.printStackTrace();
                    Initiator.logException("[ERROR] answering CopyMultipleQuestion", e);
                }
            }
            if (Initiator.messageOnCopy) {
                performer.getCommunicator().sendNormalServerMessage(String.format("You copy the %s and all it's data %d times.",item.getName(), amount));
            }
        }

    }

    public static void send(Creature performer, Item renameTarget) {
        ModQuestions.createQuestion(performer, "Change Item amount", "What amount are we going to change to?", MiscConstants.NOID, new CopyMultipleQuestion(performer, renameTarget)).sendQuestion();
    }

    private boolean containsIllegalCharacters(String text, Creature performer) {
        if (Character.isWhitespace(text.charAt(0))) {
            performer.getCommunicator().sendNormalServerMessage("The new item amount must not start with a space.");
            return true;
        }

        return false;
    }
}