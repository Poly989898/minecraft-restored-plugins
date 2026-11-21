package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "raytrace",
   aliases = {"raycast"},
   usedPlaceholderArguments = -1
)
public class RayTracePlaceholder extends EntityScopedPlaceholder<String> implements StringPlaceholder {
   private final PlaceholderDouble maxDistance;

   public RayTracePlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
      MythicLineConfig config = context.config;
      if (config != null) {
         this.maxDistance = config.getPlaceholderDouble(new String[]{"maxdistance", "md"}, 4.5);
      } else if (this.args != null && this.args.length != 0) {
         if (this.args.length == 1) {
            this.usedPlaceholderArguments.set(1);
            this.maxDistance = PlaceholderDouble.of(this.args[0]);
         } else {
            this.usedPlaceholderArguments.set(2);
            this.maxDistance = PlaceholderDouble.of(this.args[0] + "." + this.args[1]);
         }
      } else {
         this.maxDistance = PlaceholderDouble.of("4.5");
      }

      this.initializeMetaKeywords();
   }

   private String rayCast(Entity entity, double maxDistance) {
      if (entity instanceof LivingEntity livingEntity) {
         RayTraceResult rayTraceResult = entity.getWorld().rayTraceBlocks(livingEntity.getEyeLocation(), entity.getLocation().getDirection(), maxDistance);
         if (rayTraceResult != null) {
            Block targetBlock = rayTraceResult.getHitBlock();
            if (targetBlock != null) {
               Material blockType = targetBlock.getType();
               return blockType.toString();
            }
         }

         return "AIR";
      } else {
         return "AIR";
      }
   }

   @Nullable
   public String applyToScope(PlaceholderContext placeholderContext) {
      return this.rayCast(this.getEntity.get(placeholderContext).getBukkitEntity(), this.maxDistance.get(placeholderContext));
   }
}
