package net.Indyuce.mmoitems.comp.mmocore.crafting;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.experience.Profession;
import net.Indyuce.mmoitems.api.crafting.condition.Condition;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.apache.commons.lang.Validate;

public class ProfessionCondition extends Condition {
   private final Profession profession;
   private final int level;

   public ProfessionCondition(MMOLineConfig var1) {
      super("profession");
      var1.validate(new String[]{"profession", "level"});
      this.level = var1.getInt("level");
      String var2 = var1.getString("profession").toLowerCase().replace("_", "-");
      Validate.isTrue(MMOCore.plugin.professionManager.has(var2), "Could not find profession " + var2);
      this.profession = MMOCore.plugin.professionManager.get(var2);
   }

   @Override
   public String formatDisplay(String var1) {
      return var1.replace("#level#", "" + this.level).replace("#profession#", this.profession.getName());
   }

   @Override
   public boolean isMet(PlayerData var1) {
      return net.Indyuce.mmocore.api.player.PlayerData.get(var1.getPlayer()).getCollectionSkills().getLevel(this.profession) >= this.level;
   }

   @Override
   public void whenCrafting(PlayerData var1) {
   }
}
