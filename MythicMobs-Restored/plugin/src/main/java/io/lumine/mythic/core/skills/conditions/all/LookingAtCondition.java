package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

@MythicCondition(
   author = "Seyarada",
   name = "lookingAt",
   description = "Checks if the player is looking at something"
)
public class LookingAtCondition extends SkillCondition implements IEntityCondition {
   private final String thing;
   private final PlaceholderInt distance;

   public LookingAtCondition(String line, MythicLineConfig config) {
      super(line);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.thing = config.getString(new String[]{"block", "b", "entity", "e", "type", "t"}, null);
      this.distance = config.getPlaceholderInteger(new String[]{"distance", "d"}, 5);
   }

   @Override
   public boolean check(AbstractEntity target) {
      if (!target.isPlayer()) {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Target is not a player, returning false");
         return false;
      } else if (this.thing == null) {
         return false;
      } else {
         Player player = BukkitAdapter.adapt(target.asPlayer());
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(player)) {
            Boolean result = Schedulers.of(player).supply(() -> this.checkLookingAt(player, target)).join();
            return result != null ? result : false;
         } else {
            return this.checkLookingAt(player, target);
         }
      }
   }

   private boolean checkLookingAt(Player player, AbstractEntity target) {
      RayTraceResult blockRayTraceResult = player.getWorld()
         .rayTraceBlocks(player.getEyeLocation(), player.getLocation().getDirection(), this.distance.get(target));
      if (blockRayTraceResult != null) {
         Block targetBlock = blockRayTraceResult.getHitBlock();
         if (targetBlock != null) {
            Material blockType = targetBlock.getType();
            if (matches(blockType.toString(), this.thing)) {
               return true;
            }
         }
      }

      RayTraceResult entityRayTraceResult = player.getWorld()
         .rayTraceEntities(player.getEyeLocation(), player.getLocation().getDirection(), this.distance.get(target), e -> e != player);
      if (entityRayTraceResult != null) {
         Entity targetEntity = entityRayTraceResult.getHitEntity();
         if (targetEntity != null) {
            EntityType entityType = targetEntity.getType();
            return matches(entityType.toString(), this.thing);
         }
      }

      return false;
   }

   public static boolean matches(String obj, String wantType) {
      wantType = wantType.toUpperCase();
      switch (wantType.charAt(0)) {
         case '#':
            if (obj.contains(wantType.substring(1))) {
               return true;
            }
            break;
         case '@':
            if (obj.startsWith(wantType.substring(1))) {
               return true;
            }
            break;
         default:
            try {
               if (Material.valueOf(wantType) == Material.valueOf(obj)) {
                  return true;
               }
            } catch (IllegalArgumentException e) {
            }

            try {
               if (EntityType.valueOf(wantType) == EntityType.valueOf(obj)) {
                  return true;
               }
            } catch (IllegalArgumentException ex) {
            }

            return false;
      }

      return false;
   }
}
