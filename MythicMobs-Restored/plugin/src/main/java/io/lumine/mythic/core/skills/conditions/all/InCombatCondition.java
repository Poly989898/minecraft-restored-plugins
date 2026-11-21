package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.entity.Mob;

@MythicCondition(
   author = "Ashijin",
   name = "incombat",
   aliases = {},
   description = "If the target is considered in combat"
)
public class InCombatCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "timeout",
      aliases = {"t"},
      description = "Combat-tag window for players, in ticks"
   )
   private final int timeoutTicks;

   public InCombatCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.timeoutTicks = mlc.getInteger(new String[]{"timeout", "t"}, 200);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      if (entity.isPlayer()) {
         long timeoutMs = Math.max(0L, (long)this.timeoutTicks) * 50L;
         return MythicBukkit.inst().getPlayerManager().getCombatTracker().isInCombat(entity.getUniqueId(), timeoutMs);
      } else {
         if (MythicBukkit.inst().getMobManager().isActiveMob(entity)) {
            ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);
            if (am.hasThreatTable()) {
               return am.getThreatTable().inCombat();
            }

            if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(entity.getBukkitEntity())) {
               return Schedulers.of(entity.getBukkitEntity()).supply(() -> ((Mob)entity.getBukkitEntity()).getTarget() != null).join();
            }

            if (((Mob)entity.getBukkitEntity()).getTarget() != null) {
               return true;
            }
         }

         return false;
      }
   }
}
