package io.lumine.mythic.lib.util;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.comp.interaction.InteractionType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class CustomProjectileHandler {
   private final Set<Integer> untargetable = new HashSet<>();
   private final PlayerMetadata caster;
   private final InteractionType interactionType;
   private static final double BOUNDING_BOX_EXPANSION = 0.2;

   public CustomProjectileHandler(PlayerMetadata caster) {
      this(caster, InteractionType.OFFENSE_SKILL);
   }

   public CustomProjectileHandler(PlayerMetadata caster, InteractionType interactionType) {
      this.caster = caster;
      this.interactionType = interactionType;
   }

   public boolean canTarget(@NotNull Entity target) {
      return this.canTarget(null, target);
   }

   public boolean canTarget(@Nullable Location loc, @NotNull Entity target) {
      if (loc != null && !target.getBoundingBox().expand(0.2).contains(loc.toVector())) {
         return false;
      } else if (this.untargetable.contains(target.getEntityId())) {
         return false;
      } else if (!MythicLib.plugin.getEntities().canInteract(this.caster.getPlayer(), target, this.interactionType)) {
         this.untargetable.add(target.getEntityId());
         return false;
      } else {
         return true;
      }
   }

   @Nullable
   public LivingEntity findTarget(@NotNull Location loc) {
      for (Entity ent : UtilityMethods.getNearbyChunkEntities(loc)) {
         if (this.canTarget(loc, ent)) {
            return (LivingEntity)ent;
         }
      }

      return null;
   }
}
