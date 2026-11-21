package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "block.data"
)
public class BlockDataPlaceholder extends EntityScopedPlaceholder<String> implements StringPlaceholder {
   public BlockDataPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
   }

   @Nullable
   public String applyToScope(PlaceholderContext placeholderContext) {
      Block block = BukkitAdapter.adapt(this.getLocation.get(placeholderContext)).getBlock();
      return block.getBlockData().getAsString();
   }
}
