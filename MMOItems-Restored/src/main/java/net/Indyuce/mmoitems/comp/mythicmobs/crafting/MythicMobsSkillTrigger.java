package net.Indyuce.mmoitems.comp.mythicmobs.crafting;

import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.ArrayList;
import java.util.Optional;
import net.Indyuce.mmoitems.api.crafting.trigger.Trigger;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.apache.commons.lang.Validate;

public class MythicMobsSkillTrigger extends Trigger {
   private final Skill skill;

   public MythicMobsSkillTrigger(MMOLineConfig var1) {
      super("mmskill");
      var1.validate(new String[]{"id"});
      String var2 = var1.getString("id");
      Optional var3 = MythicBukkit.inst().getSkillManager().getSkill(var2);
      Validate.isTrue(var3.isPresent(), "Could not find MM skill " + var2);
      this.skill = (Skill)var3.get();
   }

   @Override
   public void whenCrafting(PlayerData var1) {
      if (var1.isOnline()) {
         ArrayList var2 = new ArrayList();
         var2.add(var1.getPlayer());
         MythicBukkit.inst()
            .getAPIHelper()
            .castSkill(var1.getPlayer(), this.skill.getInternalName(), var1.getPlayer(), var1.getPlayer().getEyeLocation(), var2, null, 1.0F);
      }
   }
}
