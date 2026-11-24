package net.Indyuce.mmoitems.api.crafting.condition;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.Arrays;
import java.util.List;
import net.Indyuce.mmoitems.api.player.PlayerData;

public class ClassCondition extends Condition {
   private final List<String> classes;

   public ClassCondition(MMOLineConfig mmoLineConfig) {
      super("class");
      mmoLineConfig.validate(new String[]{"list"});
      this.classes = Arrays.asList(mmoLineConfig.getString("list").split(","));
   }

   @Override
   public boolean isMet(PlayerData playerData) {
      return this.classes.contains(playerData.getRPG().getClassName());
   }

   @Override
   public String formatDisplay(String text) {
      return text.replace("#class#", String.join(", ", this.classes));
   }

   @Override
   public void whenCrafting(PlayerData playerData) {
   }
}
