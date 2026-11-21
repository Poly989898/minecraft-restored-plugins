package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@MythicMechanic(
   name = "showentity",
   aliases = {"showplayer"}
)
public class ShowEntityMechanic extends SkillMechanic implements ITargetedEntitySkill {
   public ShowEntityMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isPlayer() && target.getUniqueId() != data.getCaster().getEntity().getUniqueId()) {
         Player player = BukkitAdapter.adapt(target.asPlayer());
         if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_18)) {
            Entity caster = BukkitAdapter.adapt(data.getCaster().getEntity());
            player.showEntity(this.getPlugin(), caster);
         } else {
            if (!data.getCaster().getEntity().isPlayer()) {
               return SkillResult.CONDITION_FAILED;
            }

            Player caster = BukkitAdapter.adapt(data.getCaster().getEntity().asPlayer());
            player.showPlayer(this.getPlugin(), caster);
         }

         HideMechanic.removeHiddenPlayer(data.getCaster().getEntity().getDataContainer(), target.getUniqueId());
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}
