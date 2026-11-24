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

   public ProfessionCondition(MMOLineConfig mmoLineConfig) {
      super("profession");
      mmoLineConfig.validate(new String[]{"profession", "level"});
      this.level = mmoLineConfig.getInt("level");
      String text = mmoLineConfig.getString("profession").toLowerCase().replace("_", "-");
      Validate.isTrue(MMOCore.plugin.professionManager.has(text), "Could not find profession " + text);
      this.profession = MMOCore.plugin.professionManager.get(text);
   }

   @Override
   public String formatDisplay(String text) {
      return text.replace("#level#", "" + this.level).replace("#profession#", this.profession.getName());
   }

   @Override
   public boolean isMet(PlayerData playerData) {
      return net.Indyuce.mmocore.api.player.PlayerData.get(playerData.getPlayer()).getCollectionSkills().getLevel(this.profession) >= this.level;
   }

   @Override
   public void whenCrafting(PlayerData playerData) {
   }
}
