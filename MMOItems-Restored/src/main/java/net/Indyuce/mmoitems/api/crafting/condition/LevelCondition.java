package net.Indyuce.mmoitems.api.crafting.condition;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmoitems.api.player.PlayerData;

public class LevelCondition extends Condition {
   private final int level;
   private final boolean consume;

   public LevelCondition(MMOLineConfig var1) {
      super("level");
      var1.validate(new String[]{"level"});
      this.level = var1.getInt("level");
      this.consume = var1.getBoolean("consume", false);
   }

   @Override
   public boolean isMet(PlayerData var1) {
      return var1.getRPG().getLevel() >= this.level;
   }

   @Override
   public String formatDisplay(String var1) {
      return var1.replace("#level#", "" + this.level);
   }

   @Override
   public void whenCrafting(PlayerData var1) {
      if (this.consume) {
         var1.getPlayer().setLevel(Math.max(0, var1.getPlayer().getLevel() - this.level));
      }
   }
}
