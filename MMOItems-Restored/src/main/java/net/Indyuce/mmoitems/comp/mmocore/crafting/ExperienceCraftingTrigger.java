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

   public ExperienceCraftingTrigger(MMOLineConfig var1) {
      super("exp");
      var1.validate(new String[]{"profession", "amount"});
      this.amount = var1.getInt("amount");
      String var2 = var1.getString("profession").toLowerCase().replace("_", "-");
      if (!var2.equalsIgnoreCase("main")) {
         Validate.isTrue(MMOCore.plugin.professionManager.has(var2), "Could not find profession " + var2);
         this.profession = MMOCore.plugin.professionManager.get(var2);
      } else {
         this.profession = null;
      }
   }

   @Override
   public void whenCrafting(PlayerData var1) {
      if (this.profession == null) {
         net.Indyuce.mmocore.api.player.PlayerData.get(var1.getPlayer()).giveExperience(this.amount, EXPSource.SOURCE);
      } else {
         net.Indyuce.mmocore.api.player.PlayerData.get(var1.getPlayer()).getCollectionSkills().giveExperience(this.profession, this.amount, EXPSource.SOURCE);
      }
   }
}
