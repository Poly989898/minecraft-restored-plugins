package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "block.type"
)
public class BlockTypePlaceholder extends EntityScopedPlaceholder<String> implements StringPlaceholder {
   public BlockTypePlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments arguments) {
      super(arguments);
   }

   @Nullable
   public String applyToScope(PlaceholderContext placeholderContext) {
      try {
         return BukkitAdapter.adapt(this.getLocation.get(placeholderContext)).getBlock().getType().toString();
      } catch (Exception e) {
         return "AIR";
      }
   }
}
