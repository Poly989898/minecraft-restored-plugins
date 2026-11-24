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

   public PlaceholderCondition(MMOLineConfig var1) {
      super("placeholder", var1);
      var1.validateKeys(new String[]{"placeholder"});
      String[] var2 = var1.getString("placeholder").split("~");
      Validate.isTrue(var2.length == 3, "Please use exactly twice ~");
      this.expression1 = var2[0];
      this.comparator = var2[1];
      this.expression2 = var2[2];
   }

   @Override
   public boolean isMet(PlayerData var1) {
      String var2 = PlaceholderAPI.setPlaceholders(var1.getPlayer(), this.expression1);
      String var3 = PlaceholderAPI.setPlaceholders(var1.getPlayer(), this.expression2);

      try {
         switch (this.comparator) {
            case "<":
               return Double.parseDouble(var2) < Double.parseDouble(var3);
            case "<=":
               return Double.parseDouble(var2) <= Double.parseDouble(var3);
            case ">":
               return Double.parseDouble(var2) > Double.parseDouble(var3);
            case ">=":
               return Double.parseDouble(var2) >= Double.parseDouble(var3);
            case "==":
            case "=":
               return Math.abs(Double.parseDouble(var2) - Double.parseDouble(var3)) <= EQUALITY_THRESHOLD;
            case "!=":
               return Math.abs(Double.parseDouble(var2) - Double.parseDouble(var3)) > EQUALITY_THRESHOLD;
            case "equals":
               return var2.equals(var3);
            default:
               throw new RuntimeException("Comparator not recognized");
         }
      } catch (RuntimeException var6) {
         MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not evaluate placeholder condition expression: " + var6.getMessage());
         return false;
      }
   }

   @Override
   public void whenCrafting(PlayerData var1) {
   }
}
