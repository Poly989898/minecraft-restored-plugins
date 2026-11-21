package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.ItemPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "held.item",
   description = "Returns the entity's held item"
)
public class HeldItemPlaceholder extends EntityScopedPlaceholder<BukkitItemStack> implements ItemPlaceholder {
   private static final BukkitItemStack AIR = BukkitAdapter.adapt(new ItemStack(Material.AIR));

   public HeldItemPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments arguments) {
      super(arguments);
   }

   @Nullable
   public BukkitItemStack applyToScope(PlaceholderContext placeholderContext) {
      AbstractEntity entity = this.getEntity.get(placeholderContext);
      return entity != null && entity.isLiving() ? entity.getItemMainHand().map(itemStack -> (BukkitItemStack)itemStack).orElse(AIR) : AIR;
   }
}
