package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;

@MythicTargeter(
   author = "Ashijin",
   name = "MobsNearOrigin",
   aliases = {"MobssNearSource"},
   description = "Targets all entities of the given types in the given radius around the origin of the metaskill"
)
public class MobsNearOriginTargeter extends IEntitySelector {
   double radius;
   HashSet<MythicMob> mmTypes = new HashSet<>();
   HashSet<BukkitEntityType> meTypes = new HashSet<>();

   public MobsNearOriginTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.radius = mlc.getDouble("radius", 5.0);
      this.radius = mlc.getDouble("r", this.radius);
      String types = mlc.getString("types", null);
      types = mlc.getString("type", types);
      types = mlc.getString("t", types);
      if (types != null && !types.isEmpty()) {
         String[] ss = types.split(",");
         this.getPlugin().getSkillManager().queueSecondPass(() -> {
            for (String s : ss) {
               MythicMob mm = this.getPlugin().getMobManager().getMythicMob(s).orElseGet(() -> null);
               if (mm != null) {
                  this.mmTypes.add(mm);
               } else {
                  BukkitEntityType me = BukkitEntityType.getMythicEntity(s);
                  if (me != null) {
                     this.meTypes.add(me);
                  } else {
                     MythicLogger.errorTargeterConfig(this, mlc, "The 'type' attribute must be a valid MythicMob or MythicEntity type.");
                  }
               }
            }
         });
      } else {
         MythicLogger.errorTargeterConfig(this, mlc, "The 'type' attribute is required (one or more MythicMob or MythicEntity types, comma-separated).");
      }
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      Collection<AbstractEntity> targets = Lists.newArrayList();

      for (AbstractEntity p : data.getCaster().getEntity().getWorld().getLivingEntities()) {
         if (!p.getUniqueId().equals(data.getCaster().getEntity().getUniqueId())
            && p.getWorld().equals(data.getCaster().getEntity().getWorld())
            && this.distanceSquared(data.getOrigin(), p) < this.radius * this.radius) {
            ActiveMob amx = this.getPlugin().getMobManager().getMythicMobInstance(p);
            if (amx == null) {
               for (BukkitEntityType me : this.meTypes) {
                  if (me.compare(p.getBukkitEntity())) {
                     targets.add(p);
                     break;
                  }
               }
            } else if (this.mmTypes.contains(amx.getType())) {
               targets.add(p);
               amx = null;
            }
         }
      }

      return targets;
   }
}
