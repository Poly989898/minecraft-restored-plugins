package net.Indyuce.mmoitems.api.crafting.condition;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmoitems.api.player.PlayerData;

public class LevelCondition extends Condition {
   private final int level;
   private final boolean consume;

   public LevelCondition(MMOLineConfig mmoLineConfig) {
      super("level");
      mmoLineConfig.validate(new String[]{"level"});
      this.level = mmoLineConfig.getInt("level");
      this.consume = mmoLineConfig.getBoolean("consume", false);
   }

   @Override
   public boolean isMet(PlayerData playerData) {
      return playerData.getRPG().getLevel() >= this.level;
   }

   @Override
   public String formatDisplay(String text) {
      return text.replace("#level#", "" + this.level);
   }

   @Override
   public void whenCrafting(PlayerData playerData) {
      if (this.consume) {
         playerData.getPlayer().setLevel(Math.max(0, playerData.getPlayer().getLevel() - this.level));
      }
   }
}
