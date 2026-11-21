package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.items.ItemExecutor;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.segments.types.ResolvedPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.IntegerPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "itemcount",
   usedPlaceholderArguments = 1
)
public class ItemCountPlaceholder extends EntityScopedPlaceholder<Integer> implements IntegerPlaceholder {
   private final ResolvedPlaceholderSegment<PlaceholderString> identifier = this.getPlaceholderString(0, new String[0]);

   public ItemCountPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments arguments) {
      super(arguments);
   }

   @Nullable
   public Integer applyToScope(PlaceholderContext context) {
      AbstractEntity entity = this.getEntity.get(context);
      if (entity != null && entity.isPlayer()) {
         String id = this.identifier.getOrDefault(context, PlaceholderString::get, null);
         if (id != null && !id.isEmpty()) {
            Player player = (Player)entity.getBukkitEntity();
            PlayerInventory inventory = player.getInventory();
            Material material = Material.getMaterial(id.toUpperCase());
            return material != null ? this.countVanilla(inventory, material) : this.countMythic(inventory, id);
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   private int countVanilla(PlayerInventory inventory, Material material) {
      int total = 0;

      for (int slot = 0; slot < 36; slot++) {
         ItemStack item = inventory.getItem(slot);
         if (item != null && item.getType() == material) {
            total += item.getAmount();
         }
      }

      for (ItemStack item : inventory.getArmorContents()) {
         if (item != null && item.getType() == material) {
            total += item.getAmount();
         }
      }

      ItemStack offhand = inventory.getItemInOffHand();
      if (offhand.getType() == material) {
         total += offhand.getAmount();
      }

      return total;
   }

   private int countMythic(PlayerInventory inventory, String mythicId) {
      ItemExecutor itemManager = this.manager.getPlugin().getItemManager();
      int total = 0;

      for (int slot = 0; slot < 36; slot++) {
         ItemStack item = inventory.getItem(slot);
         if (item != null && !item.getType().isAir() && mythicId.equalsIgnoreCase(itemManager.getMythicTypeFromItem(item))) {
            total += item.getAmount();
         }
      }

      for (ItemStack item : inventory.getArmorContents()) {
         if (item != null && !item.getType().isAir() && mythicId.equalsIgnoreCase(itemManager.getMythicTypeFromItem(item))) {
            total += item.getAmount();
         }
      }

      ItemStack offhand = inventory.getItemInOffHand();
      if (!offhand.getType().isAir() && mythicId.equalsIgnoreCase(itemManager.getMythicTypeFromItem(offhand))) {
         total += offhand.getAmount();
      }

      return total;
   }
}
