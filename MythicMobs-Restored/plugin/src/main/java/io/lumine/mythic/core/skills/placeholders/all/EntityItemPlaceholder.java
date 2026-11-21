package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.ItemPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "item",
   description = "Returns the item entity's itemstack"
)
public class EntityItemPlaceholder extends EntityScopedPlaceholder<BukkitItemStack> implements ItemPlaceholder {
   private static final BukkitItemStack AIR = BukkitAdapter.adapt(new ItemStack(Material.AIR));

   public EntityItemPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments arguments) {
      super(arguments);
   }

   @Nullable
   public BukkitItemStack applyToScope(PlaceholderContext placeholderContext) {
      return this.getEntity.get(placeholderContext) instanceof Item itemEntity ? BukkitAdapter.adapt(itemEntity.getItemStack()) : AIR;
   }
}
