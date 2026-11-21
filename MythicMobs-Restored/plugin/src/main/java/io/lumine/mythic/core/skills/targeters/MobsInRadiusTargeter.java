package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@MythicTargeter(
   author = "Ashijin",
   name = "MobsInRadius",
   aliases = {"MOBS", "MIR"},
   description = "Targets all MythicMobs or vanilla overrides of the given type(s) in a radius around the caster"
)
public class MobsInRadiusTargeter extends IEntitySelector {
   private PlaceholderDouble radius;
   private HashSet<MythicMob> mmTypes = new HashSet<>();
   private HashSet<BukkitEntityType> meTypes = new HashSet<>();
   private boolean checkIfTemplate;

   public MobsInRadiusTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.radius = mlc.getPlaceholderDouble(new String[]{"radius", "r"}, 5.0);
      this.checkIfTemplate = mlc.getBoolean(new String[]{"checkIfTemplate", "cit"}, false);
      String types = mlc.getString(new String[]{"types", "type", "t"}, "");
      List<String> ss = Arrays.stream(types.split(",")).toList();
      this.getPlugin().getSkillManager().queueSecondPass(() -> {
         for (String s : ss) {
            MythicMob mm = MythicBukkit.inst().getMobManager().getMythicMob(s).orElse(null);
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

         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "@MIR targeter loaded targeting " + (this.meTypes.size() + this.mmTypes.size()) + " types");
      });
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      SkillCaster am = data.getCaster();
      AbstractEntity caster = am.getEntity();
      HashSet<AbstractEntity> targets = new HashSet<>();
      double parsedRadius = this.radius.get(data);
      double radiusSq = parsedRadius * parsedRadius;
      HashSet<String> typesIds;
      if (this.checkIfTemplate) {
         typesIds = new HashSet<>(this.mmTypes.size());

         for (MythicMob mm : this.mmTypes) {
            typesIds.add(mm.getInternalName());
         }
      } else {
         typesIds = null;
      }

      if (parsedRadius > 128.0) {
         for (AbstractEntity p : am.getEntity().getWorld().getLivingEntities()) {
            if (!p.getUniqueId().equals(am.getEntity().getUniqueId())
               && p.getWorld().equals(am.getEntity().getWorld())
               && this.distanceSquared(am.getEntity().getLocation(), p) <= radiusSq) {
               ActiveMob amx = this.getPlugin().getMobManager().getMythicMobInstance(p);
               if (amx != null) {
                  if (this.checkIfTemplate && amx.getType().getConfig().isSet("TemplateParents")) {
                     List<String> mobTemplates = amx.getType().getConfig().getStringList("TemplateParents");
                     if (!Collections.disjoint(mobTemplates, typesIds)) {
                        targets.add(p);
                     }
                  }

                  if (this.mmTypes.contains(amx.getType())) {
                     targets.add(p);
                  }
               } else {
                  for (BukkitEntityType me : this.meTypes) {
                     if (me.compare(p.getBukkitEntity())) {
                        targets.add(p);
                        break;
                     }
                  }
               }
            }
         }
      } else {
         targets.addAll(this.getPlugin().getVolatileCodeHandler().getWorldHandler().getEntitiesNearLocation(am.getLocation(), parsedRadius, entity -> {
            if (entity.getUniqueId().equals(am.getEntity().getUniqueId())) {
               return false;
            } else if (this.distanceSquared(caster.getLocation(), entity) > radiusSq) {
               return false;
            } else if (!this.getPlugin().getMobManager().isLoadedMob(entity)) {
               return false;
            } else {
               ActiveMob amCompare = this.getPlugin().getMobManager().getMythicMobInstance(entity);
               if (amCompare != null) {
                  if (this.checkIfTemplate && amCompare.getType().getConfig().isSet("TemplateParents")) {
                     List<String> mobTemplates = amCompare.getType().getConfig().getStringList("TemplateParents");
                     if (!Collections.disjoint(mobTemplates, typesIds)) {
                        return true;
                     }
                  }

                  if (this.mmTypes.contains(amCompare.getType())) {
                     return true;
                  }
               } else {
                  for (BukkitEntityType mex : this.meTypes) {
                     if (mex.compare(entity.getBukkitEntity())) {
                        return true;
                     }
                  }
               }

               return false;
            }
         }));
      }

      return targets;
   }
}
