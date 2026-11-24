package net.Indyuce.mmoitems.api.crafting.condition;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.logging.Level;
import me.clip.placeholderapi.PlaceholderAPI;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.apache.commons.lang.Validate;

public class PlaceholderCondition extends GenericCondition {
   private final String expression1;
   private final String comparator;
   private final String expression2;
   private static final double EQUALITY_THRESHOLD = Math.pow(10.0, -5.0);

   public PlaceholderCondition(MMOLineConfig mmoLineConfig) {
      super("placeholder", mmoLineConfig);
      mmoLineConfig.validateKeys(new String[]{"placeholder"});
      String[] texts = mmoLineConfig.getString("placeholder").split("~");
      Validate.isTrue(texts.length == 3, "Please use exactly twice ~");
      this.expression1 = texts[0];
      this.comparator = texts[1];
      this.expression2 = texts[2];
   }

   @Override
   public boolean isMet(PlayerData playerData) {
      String text = PlaceholderAPI.setPlaceholders(playerData.getPlayer(), this.expression1);
      String parsedExpression = PlaceholderAPI.setPlaceholders(playerData.getPlayer(), this.expression2);

      try {
         switch (this.comparator) {
            case "<":
               return Double.parseDouble(text) < Double.parseDouble(parsedExpression);
            case "<=":
               return Double.parseDouble(text) <= Double.parseDouble(parsedExpression);
            case ">":
               return Double.parseDouble(text) > Double.parseDouble(parsedExpression);
            case ">=":
               return Double.parseDouble(text) >= Double.parseDouble(parsedExpression);
            case "==":
            case "=":
               return Math.abs(Double.parseDouble(text) - Double.parseDouble(parsedExpression)) <= EQUALITY_THRESHOLD;
            case "!=":
               return Math.abs(Double.parseDouble(text) - Double.parseDouble(parsedExpression)) > EQUALITY_THRESHOLD;
            case "equals":
               return text.equals(parsedExpression);
            default:
               throw new RuntimeException("Comparator not recognized");
         }
      } catch (RuntimeException exception) {
         MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not evaluate placeholder condition expression: " + exception.getMessage());
         return false;
      }
   }

   @Override
   public void whenCrafting(PlayerData playerData) {
   }
}
