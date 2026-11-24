package net.Indyuce.mmoitems.api.crafting.condition;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.Arrays;
import java.util.List;
import net.Indyuce.mmoitems.api.player.PlayerData;

public class ClassCondition extends Condition {
   private final List<String> classes;

   public ClassCondition(MMOLineConfig var1) {
      super("class");
      var1.validate(new String[]{"list"});
      this.classes = Arrays.asList(var1.getString("list").split(","));
   }

   @Override
   public boolean isMet(PlayerData var1) {
      return this.classes.contains(var1.getRPG().getClassName());
   }

   @Override
   public String formatDisplay(String var1) {
      return var1.replace("#class#", String.join(", ", this.classes));
   }

   @Override
   public void whenCrafting(PlayerData var1) {
   }
}
