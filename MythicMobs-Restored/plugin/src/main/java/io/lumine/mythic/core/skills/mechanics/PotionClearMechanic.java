package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@MythicMechanic(
   author = "Ashijin",
   name = "potionclear",
   aliases = {"clearpotions", "clearpotion"},
   description = "Clears all potion effects on the target entity"
)
public class PotionClearMechanic extends SkillMechanic implements ITargetedEntitySkill {
   private PlaceholderString types;

   public PotionClearMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.types = mlc.getPlaceholderString(new String[]{"types", "type", "t"}, null);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isLiving()) {
         return SkillResult.INVALID_TARGET;
      } else {
         String types = this.types == null ? "" : this.types.get(data, target);
         LivingEntity bukkitEntity = (LivingEntity)target.getBukkitEntity();
         if (types.isEmpty()) {
            Schedulers.runOnRegion(bukkitEntity, () -> {
               for (PotionEffect pe : bukkitEntity.getActivePotionEffects()) {
                  bukkitEntity.removePotionEffect(pe.getType());
               }
            });
         } else if (types.contains(",")) {
            List<PotionEffectType> toRemove = new ArrayList<>();

            for (String type : types.split(",")) {
               try {
                  PotionEffectType potionType = PotionEffectType.getByName(type);
                  if (potionType == null) {
                     return SkillResult.INVALID_CONFIG;
                  }

                  toRemove.add(potionType);
               } catch (Throwable e) {
               }
            }

            Schedulers.runOnRegion(bukkitEntity, () -> toRemove.forEach(bukkitEntity::removePotionEffect));
         } else {
            try {
               PotionEffectType potionType = PotionEffectType.getByName(types);
               if (potionType == null) {
                  return SkillResult.INVALID_CONFIG;
               }

               Schedulers.runOnRegion(bukkitEntity, () -> bukkitEntity.removePotionEffect(potionType));
            } catch (Throwable ex) {
            }
         }

         return SkillResult.SUCCESS;
      }
   }
}
