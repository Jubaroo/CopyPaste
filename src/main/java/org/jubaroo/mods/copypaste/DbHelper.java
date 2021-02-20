package org.jubaroo.mods.copypaste;

import com.wurmonline.server.DbConnector;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemMealData;
import com.wurmonline.server.utils.DbUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Jubaroo on 2/17/2021
 */

public class DbHelper {

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
        } catch (SQLException sqex) {
            Initiator.logException(String.format("Failed to save item (meal) data: %s", sqex.getMessage()), sqex);
        } finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }

    public static int loadAllMealData() {
        int count = 0;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM MEALDATA");
            rs = ps.executeQuery();
            while (rs.next()) {
                ++count;
                final long mealId = rs.getLong("MEALID");
                final short recipeId = rs.getShort("RECIPEID");
                final short calories = rs.getShort("CALORIES");
                final short carbs = rs.getShort("CARBS");
                final short fats = rs.getShort("FATS");
                final short proteins = rs.getShort("PROTEINS");
                final byte bonus = rs.getByte("BONUS");
                final byte stages = rs.getByte("STAGESCOUNT");
                final byte ingredients = rs.getByte("INGREDIENTSCOUNT");
                //add(new ItemMealData(mealId, recipeId, calories, carbs, fats, proteins, bonus, stages, ingredients));
            }
        } catch (SQLException sqex) {
            Initiator.logException("Failed to load all meal data: " + sqex.getMessage(), sqex);
        }

        return count;
    }
}
