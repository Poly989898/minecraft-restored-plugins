package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.FloatPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "block.hardness"
)
public class BlockHardnessPlaceholder extends EntityScopedPlaceholder<Float> implements FloatPlaceholder {
   public BlockHardnessPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments arguments) {
      super(arguments);
   }

   @Nullable
   public Float applyToScope(PlaceholderContext placeholderContext) {
      try {
         return BukkitAdapter.adapt(this.getLocation.get(placeholderContext)).getBlock().getType().getHardness();
      } catch (Exception e) {
         return 0.0F;
      }
   }
}
