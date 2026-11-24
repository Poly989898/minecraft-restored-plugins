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

   public MythicMobsSkillTrigger(MMOLineConfig mmoLineConfig) {
      super("mmskill");
      mmoLineConfig.validate(new String[]{"id"});
      String id = mmoLineConfig.getString("id");
      Optional skill = MythicBukkit.inst().getSkillManager().getSkill(id);
      Validate.isTrue(skill.isPresent(), "Could not find MM skill " + id);
      this.skill = (Skill)skill.get();
   }

   @Override
   public void whenCrafting(PlayerData playerData) {
      if (playerData.isOnline()) {
         ArrayList list = new ArrayList();
         list.add(playerData.getPlayer());
         MythicBukkit.inst()
            .getAPIHelper()
            .castSkill(playerData.getPlayer(), this.skill.getInternalName(), playerData.getPlayer(), playerData.getPlayer().getEyeLocation(), list, null, 1.0F);
      }
   }
}
