package net.Indyuce.inventory.inventory;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.slot.CustomSlot;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomInventoryHandler extends InventoryHandler {
   private final Map<CustomSlot, InventoryItem> items = new HashMap<>();

   public CustomInventoryHandler(MMOPlayerData playerData) {
      super(playerData);
   }

   @Override
   protected void insertItemAtSlot(@NotNull CustomSlot slot, @Nullable ItemStack item) {
      Validate.isTrue(slot.getType().isCustom(), "Not a custom slot");
      if (UtilityMethods.isAir(item)) {
         this.items.remove(slot);
      } else {
         this.items.put(slot, new InventoryItem(item.clone(), slot));
      }
   }

   @Override
   protected Collection<InventoryItem> retrieveItems() {
      return this.items.values();
   }

   @Nullable
   public ItemStack getItem(@NotNull CustomSlot slot) {
      if (!slot.getType().isCustom()) {
         return slot.getType().getVanillaSlotHandler().retrieveItem(this.getPlayer());
      }

      InventoryItem found = this.items.get(slot);
      return found == null ? null : found.getItemStack();
   }

   public boolean hasItem(@NotNull CustomSlot slot) {
      return slot.getType().isCustom()
         ? this.items.containsKey(slot)
         : !UtilityMethods.isAir(slot.getType().getVanillaSlotHandler().retrieveItem(this.getPlayer()));
   }

   @NotNull
   public Set<CustomSlot> getFilledSlots() {
      return this.items.keySet();
   }

   @Deprecated
   @NotNull
   public Set<Integer> getFilledSlotKeys() {
      return this.items.keySet().stream().map(CustomSlot::getIndex).collect(Collectors.toSet());
   }

   @Deprecated
   @Nullable
   public ItemStack getItem(int index) {
      CustomSlot slot = MMOInventory.plugin.getSlotManager().get(index);
      return slot != null && slot.getType().isCustom() ? this.getItem(slot) : null;
   }
}
