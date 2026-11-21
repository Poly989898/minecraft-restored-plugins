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
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.skills.variables.types.ItemVariable;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import java.util.Optional;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "item.itemstack",
   usedPlaceholderArguments = -1
)
public class EntityItemStackPlaceholder extends EntityScopedPlaceholder<String> implements StringPlaceholder {
   private final ResolvedPlaceholderSegment<PlaceholderString> slot = this.getPlaceholderString(0, new String[0]);

   public EntityItemStackPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
      this.initializeMetaKeywords();
   }

   @Nullable
   public String applyToScope(PlaceholderContext placeholderContext) {
      AbstractEntity entity = this.getEntity.get(placeholderContext);
      if (this.slot.value() != null && entity.isPlayer()) {
         String slot = this.slot.getOrDefault(placeholderContext, PlaceholderString::get, "0");
         EquipSlot tempSlot = EquipSlot.of(slot);
         ItemStack item;
         if (tempSlot != EquipSlot.NONE) {
            Optional<AbstractItemStack> maybeNamedSlotItemStack = tempSlot.get(entity);
            if (maybeNamedSlotItemStack.isEmpty() || !(maybeNamedSlotItemStack.get() instanceof BukkitItemStack bukkitItemStack)) {
               return "UNDEFINED";
            }

            item = bukkitItemStack.getItemStack();
         } else {
            int slotInt = Integer.parseInt(slot);
            Player player = BukkitAdapter.adapt(entity.asPlayer());
            item = player.getInventory().getItem(slotInt < 41 ? slotInt : 0);
         }

         return ItemVariable.Utils.serializeRawToString(item);
      } else {
         return entity.getBukkitEntity() instanceof Item itemEntity
            ? ItemVariable.Utils.serializeRawToString(itemEntity.getItemStack())
            : ItemVariable.Utils.getDefaultMaterialSerialized();
      }
   }
}
