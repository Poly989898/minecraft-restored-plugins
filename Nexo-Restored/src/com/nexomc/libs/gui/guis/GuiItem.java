package com.nexomc.libs.gui.guis;

import com.google.common.base.Preconditions;
import com.nexomc.libs.gui.components.GuiAction;
import com.nexomc.libs.gui.components.util.ItemNbt;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuiItem {
   private final UUID uuid;
   private GuiAction<InventoryClickEvent> action;
   private ItemStack itemStack;

   public GuiItem(@NotNull ItemStack itemStack, @Nullable GuiAction<@NotNull InventoryClickEvent> action) {
      this.uuid = UUID.randomUUID();
      Preconditions.checkNotNull(itemStack, "The ItemStack for the GUI Item cannot be null!");
      this.action = action;
      this.setItemStack(itemStack);
   }

   public GuiItem(@NotNull ItemStack itemStack) {
      this((ItemStack)itemStack, (GuiAction)null);
   }

   public GuiItem(@NotNull Material material) {
      this((ItemStack)(new ItemStack(material)), (GuiAction)null);
   }

   public GuiItem(@NotNull Material material, @Nullable GuiAction<@NotNull InventoryClickEvent> action) {
      this(new ItemStack(material), action);
   }

   public @NotNull ItemStack getItemStack() {
      return this.itemStack;
   }

   public void setItemStack(@NotNull ItemStack itemStack) {
      Preconditions.checkNotNull(itemStack, "The ItemStack for the GUI Item cannot be null!");
      if (itemStack.getType() != Material.AIR) {
         this.itemStack = ItemNbt.setString(itemStack.clone(), "mf-gui", this.uuid.toString());
      } else {
         this.itemStack = itemStack.clone();
      }

   }

   public @Nullable GuiAction<InventoryClickEvent> getAction() {
      return this.action;
   }

   public void setAction(@Nullable GuiAction<@NotNull InventoryClickEvent> action) {
      this.action = action;
   }

   @NotNull UUID getUuid() {
      return this.uuid;
   }
}
