package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.drops.EquipSlot;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.segments.types.ResolvedPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.ItemPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "slot.item",
   description = "Returns the item in the entity's slot as an item (supports item meta keywords)",
   usedPlaceholderArguments = -1
)
public class SlotItemPlaceholder extends EntityScopedPlaceholder<BukkitItemStack> implements ItemPlaceholder {
   private static final BukkitItemStack AIR = BukkitAdapter.adapt(new ItemStack(Material.AIR));
   private final ResolvedPlaceholderSegment<PlaceholderString> slot = this.getPlaceholderString(0, new String[0]);

   public SlotItemPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments arguments) {
      super(arguments);
      this.initializeMetaKeywords();
   }

   @Nullable
   public BukkitItemStack applyToScope(PlaceholderContext placeholderContext) {
      AbstractEntity entity = this.getEntity.get(placeholderContext);
      if (entity == null) {
         return AIR;
      } else {
         String slot = this.slot.getOrDefault(placeholderContext, PlaceholderString::get, "0");
         EquipSlot equipSlot = EquipSlot.of(slot);
         if (equipSlot != EquipSlot.NONE) {
            Optional<AbstractItemStack> maybeItem = equipSlot.get(entity);
            return !maybeItem.isEmpty() && maybeItem.get() instanceof BukkitItemStack bukkitItemStack ? bukkitItemStack : AIR;
         } else if (!entity.isPlayer()) {
            return AIR;
         } else {
            int slotInt = Integer.parseInt(slot);
            Player player = BukkitAdapter.adapt(entity.asPlayer());
            ItemStack item = player.getInventory().getItem(slotInt < 41 ? slotInt : 0);
            return item == null ? AIR : BukkitAdapter.adapt(item);
         }
      }
   }
}
