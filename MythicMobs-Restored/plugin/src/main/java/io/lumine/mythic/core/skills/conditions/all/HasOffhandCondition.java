package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

@MythicCondition(
   author = "Joshinn",
   name = "hasoffhand",
   aliases = {"offhand"},
   description = "Tests if the target entity has something in offhand."
)
public class HasOffhandCondition extends SkillCondition implements IEntityCondition {
   public HasOffhandCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity e) {
      if (e.getBukkitEntity() instanceof LivingEntity living) {
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(living)) {
            Boolean result = Schedulers.of(living).supply(() -> this.hasOffhandItem(living)).join();
            return result != null ? result : false;
         } else {
            return this.hasOffhandItem(living);
         }
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Target is not a LivingEntity, returning false");
         return false;
      }
   }

   private boolean hasOffhandItem(LivingEntity living) {
      EntityEquipment equipment = living.getEquipment();
      if (equipment == null) {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Target cannot equip items, returning false");
         return false;
      } else {
         ItemStack item = equipment.getItemInOffHand();
         if (item != null && !item.getType().equals(Material.AIR)) {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "In offhand condition, offhand was not empty, returning true.");
            return true;
         } else {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "In offhand condition, offhand was empty, returning false.");
            return false;
         }
      }
   }
}
