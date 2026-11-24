package net.Indyuce.mmoitems.comp.mmocore.crafting;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmocore.experience.Profession;
import net.Indyuce.mmoitems.api.crafting.trigger.Trigger;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.apache.commons.lang.Validate;

public class ExperienceCraftingTrigger extends Trigger {
   private final Profession profession;
   private final int amount;

   public ExperienceCraftingTrigger(MMOLineConfig mmoLineConfig) {
      super("exp");
      mmoLineConfig.validate(new String[]{"profession", "amount"});
      this.amount = mmoLineConfig.getInt("amount");
      String text = mmoLineConfig.getString("profession").toLowerCase().replace("_", "-");
      if (!text.equalsIgnoreCase("main")) {
         Validate.isTrue(MMOCore.plugin.professionManager.has(text), "Could not find profession " + text);
         this.profession = MMOCore.plugin.professionManager.get(text);
      } else {
         this.profession = null;
      }
   }

   @Override
   public void whenCrafting(PlayerData playerData) {
      if (this.profession == null) {
         net.Indyuce.mmocore.api.player.PlayerData.get(playerData.getPlayer()).giveExperience(this.amount, EXPSource.SOURCE);
      } else {
         net.Indyuce.mmocore.api.player.PlayerData.get(playerData.getPlayer()).getCollectionSkills().giveExperience(this.profession, this.amount, EXPSource.SOURCE);
      }
   }
}
