package org.jubaroo.mods.copypaste.questions;

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
import org.jubaroo.mods.copypaste.actions.copy.CopyMultipleItemPerformer;

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
        String buf = String.format("%sharray{input{text='Number of times to copy'; id='amount'; maxchars='3'; text=''}}text{text=''}text{text=''}%s", ModQuestions.getBmlHeader(question), ModQuestions.createAnswerButton2(question, "Confirm"));
        question.getResponder().getCommunicator().sendBml(300, 200, false, true, buf, 200, 200, 200, question.getTitle());
    }

    @Override
    public void answer(Question question, Properties answer) {
        String newSize = answer.getProperty("amount");

        if (isIllegalCharacters(newSize)) {
            performer.getCommunicator().sendNormalServerMessage("The number must not be blank, start with a space or not be a number.");
        } else {
            int amount = (int) Float.parseFloat(answer.getProperty("amount"));
            for (int i = 0; i < amount; i++) {
                try {
                    CopyHelper.copyItemData(performer, item, CopyMultipleItemPerformer.actionEntry);
                } catch (NoSuchTemplateException | FailedException e) {
                    e.printStackTrace();
                    Initiator.logException("[ERROR] answering CopyMultipleQuestion", e);
                }
            }
            performer.getCommunicator().sendNormalServerMessage(String.format("You copy the %s and all it's data %d times.", item.getName(), amount));
        }

    }

    public static void send(Creature performer, Item renameTarget) {
        ModQuestions.createQuestion(performer, "Copy Item", "How many copies to make?", MiscConstants.NOID, new CopyMultipleQuestion(performer, renameTarget)).sendQuestion();
    }

    public static boolean isIllegalCharacters(String string) {
        if (string.matches("^\\s*$")) {
            return true;
        } else return !string.matches("^[0-9]+$");
    }

}