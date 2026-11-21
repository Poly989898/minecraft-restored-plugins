package com.nexomc.libs.gui.guis;

import com.nexomc.libs.gui.TriumphGui;
import com.nexomc.libs.gui.components.GuiAction;
import com.nexomc.libs.gui.components.GuiContainer;
import com.nexomc.libs.gui.components.GuiType;
import com.nexomc.libs.gui.components.InteractionModifier;
import com.nexomc.libs.gui.components.exception.GuiException;
import com.nexomc.libs.gui.components.util.GuiFiller;
import com.nexomc.libs.gui.components.util.VersionHelper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseGui implements InventoryHolder {
   private static final Plugin plugin = TriumphGui.getPlugin();
   private static Method GET_SCHEDULER_METHOD = null;
   private static Method EXECUTE_METHOD = null;
   private final GuiFiller filler = new GuiFiller(this);
   private final Map<Integer, GuiItem> guiItems;
   private final Map<Integer, GuiAction<InventoryClickEvent>> slotActions;
   private final Set<InteractionModifier> interactionModifiers;
   private final GuiContainer guiContainer;
   private Inventory inventory;
   private GuiAction<InventoryClickEvent> defaultClickAction;
   private GuiAction<InventoryClickEvent> defaultTopClickAction;
   private GuiAction<InventoryClickEvent> playerInventoryAction;
   private GuiAction<InventoryDragEvent> dragAction;
   private GuiAction<InventoryCloseEvent> closeGuiAction;
   private GuiAction<InventoryOpenEvent> openGuiAction;
   private GuiAction<InventoryClickEvent> outsideClickAction;
   private boolean updating;
   private boolean runCloseAction = true;
   private boolean runOpenAction = true;

   public BaseGui(@NotNull GuiContainer guiContainer, @NotNull Set<InteractionModifier> interactionModifiers) {
      this.interactionModifiers = this.safeCopyOf(interactionModifiers);
      this.guiContainer = guiContainer;
      this.inventory = guiContainer.createInventory(this);
      this.slotActions = new LinkedHashMap(guiContainer.inventorySize());
      this.guiItems = new LinkedHashMap(guiContainer.inventorySize());
   }

   private @NotNull Set<InteractionModifier> safeCopyOf(@NotNull Set<InteractionModifier> set) {
      return set.isEmpty() ? EnumSet.noneOf(InteractionModifier.class) : EnumSet.copyOf(set);
   }

   public @NotNull Component title() {
      return this.guiContainer.title();
   }

   public void setItem(int slot, @NotNull GuiItem guiItem) {
      this.validateSlot(slot);
      this.guiItems.put(slot, guiItem);
   }

   public void removeItem(@NotNull GuiItem item) {
      this.guiItems.entrySet().stream().filter((it) -> ((GuiItem)it.getValue()).equals(item)).findFirst().ifPresent((it) -> {
         this.guiItems.remove(it.getKey());
         this.inventory.remove(((GuiItem)it.getValue()).getItemStack());
      });
   }

   public void removeItem(@NotNull ItemStack item) {
      this.guiItems.entrySet().stream().filter((it) -> ((GuiItem)it.getValue()).getItemStack().equals(item)).findFirst().ifPresent((it) -> {
         this.guiItems.remove(it.getKey());
         this.inventory.remove(item);
      });
   }

   public void removeItem(int slot) {
      this.validateSlot(slot);
      this.guiItems.remove(slot);
      this.inventory.setItem(slot, (ItemStack)null);
   }

   public void removeItem(int row, int col) {
      this.removeItem(this.getSlotFromRowCol(row, col));
   }

   public void setItem(@NotNull List<Integer> slots, @NotNull GuiItem guiItem) {
      for(int slot : slots) {
         this.setItem(slot, guiItem);
      }

   }

   public void setItem(int row, int col, @NotNull GuiItem guiItem) {
      this.setItem(this.getSlotFromRowCol(row, col), guiItem);
   }

   public void addItem(GuiItem... items) {
      this.addItem(false, items);
   }

   public void addItem(boolean expandIfFull, GuiItem... items) {
      List<GuiItem> notAddedItems = new ArrayList();
      int rows = this.guiContainer.rows();
      GuiType guiType = this.guiContainer.guiType();

      for(GuiItem guiItem : items) {
         for(int slot = 0; slot < rows * 9; ++slot) {
            if (this.guiItems.get(slot) == null) {
               this.guiItems.put(slot, guiItem);
               break;
            }

            if (slot == rows * 9 - 1) {
               notAddedItems.add(guiItem);
            }
         }
      }

      if (expandIfFull && rows < 6 && !notAddedItems.isEmpty() && guiType == GuiType.CHEST) {
         if (this.guiContainer instanceof GuiContainer.Chest) {
            ((GuiContainer.Chest)this.guiContainer).rows(this.guiContainer.rows() + 1);
            this.inventory = this.guiContainer.createInventory(this);
            this.update();
            this.addItem(true, (GuiItem[])notAddedItems.toArray(new GuiItem[0]));
         }
      }
   }

   public void addSlotAction(int slot, @Nullable GuiAction<@NotNull InventoryClickEvent> slotAction) {
      this.validateSlot(slot);
      this.slotActions.put(slot, slotAction);
   }

   public void addSlotAction(int row, int col, @Nullable GuiAction<@NotNull InventoryClickEvent> slotAction) {
      this.addSlotAction(this.getSlotFromRowCol(row, col), slotAction);
   }

   public @Nullable GuiItem getGuiItem(int slot) {
      return (GuiItem)this.guiItems.get(slot);
   }

   public boolean isUpdating() {
      return this.updating;
   }

   public void setUpdating(boolean updating) {
      this.updating = updating;
   }

   public void open(@NotNull HumanEntity player) {
      if (!player.isSleeping()) {
         this.inventory.clear();
         this.populateGui();
         player.openInventory(this.inventory);
      }
   }

   public void close(@NotNull HumanEntity player) {
      this.close(player, true);
   }

   public void close(@NotNull HumanEntity player, boolean runCloseAction) {
      Runnable task = () -> {
         this.runCloseAction = runCloseAction;
         player.closeInventory();
         this.runCloseAction = true;
      };
      if (VersionHelper.IS_FOLIA) {
         if (GET_SCHEDULER_METHOD != null && EXECUTE_METHOD != null) {
            try {
               EXECUTE_METHOD.invoke(GET_SCHEDULER_METHOD.invoke(player), plugin, task, null, 2L);
            } catch (InvocationTargetException | IllegalAccessException e) {
               throw new GuiException("Could not invoke Folia task.", e);
            }
         } else {
            throw new GuiException("Could not find Folia Scheduler methods.");
         }
      } else {
         Bukkit.getScheduler().runTaskLater(plugin, task, 2L);
      }
   }

   public void update() {
      this.inventory.clear();
      this.populateGui();
   }

   @Contract("_ -> this")
   public @NotNull BaseGui updateTitle(@NotNull Component title) {
      this.updating = true;
      List<HumanEntity> viewers = new ArrayList(this.inventory.getViewers());
      this.guiContainer.title(title);
      this.inventory = this.guiContainer.createInventory(this);

      for(HumanEntity player : viewers) {
         this.open(player);
      }

      this.updating = false;
      return this;
   }

   public void updateItem(int slot, @NotNull ItemStack itemStack) {
      GuiItem guiItem = (GuiItem)this.guiItems.get(slot);
      if (guiItem == null) {
         this.updateItem(slot, new GuiItem(itemStack));
      } else {
         guiItem.setItemStack(itemStack);
         this.updateItem(slot, guiItem);
      }
   }

   public void updateItem(int row, int col, @NotNull ItemStack itemStack) {
      this.updateItem(this.getSlotFromRowCol(row, col), itemStack);
   }

   public void updateItem(int slot, @NotNull GuiItem item) {
      this.guiItems.put(slot, item);
      this.inventory.setItem(slot, item.getItemStack());
   }

   public void updateItem(int row, int col, @NotNull GuiItem item) {
      this.updateItem(this.getSlotFromRowCol(row, col), item);
   }

   @Contract(" -> this")
   public @NotNull BaseGui disableItemPlace() {
      this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_PLACE);
      return this;
   }

   @Contract(" -> this")
   public @NotNull BaseGui disableItemTake() {
      this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_TAKE);
      return this;
   }

   @Contract(" -> this")
   public @NotNull BaseGui disableItemSwap() {
      this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_SWAP);
      return this;
   }

   @Contract(" -> this")
   public @NotNull BaseGui disableItemDrop() {
      this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_DROP);
      return this;
   }

   @Contract(" -> this")
   public @NotNull BaseGui disableOtherActions() {
      this.interactionModifiers.add(InteractionModifier.PREVENT_OTHER_ACTIONS);
      return this;
   }

   @Contract(" -> this")
   public @NotNull BaseGui disableAllInteractions() {
      this.interactionModifiers.addAll(InteractionModifier.VALUES);
      return this;
   }

   @Contract(" -> this")
   public @NotNull BaseGui enableItemPlace() {
      this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_PLACE);
      return this;
   }

   @Contract(" -> this")
   public @NotNull BaseGui enableItemTake() {
      this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_TAKE);
      return this;
   }

   @Contract(" -> this")
   public @NotNull BaseGui enableItemSwap() {
      this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_SWAP);
      return this;
   }

   @Contract(" -> this")
   public @NotNull BaseGui enableItemDrop() {
      this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_DROP);
      return this;
   }

   @Contract(" -> this")
   public @NotNull BaseGui enableOtherActions() {
      this.interactionModifiers.remove(InteractionModifier.PREVENT_OTHER_ACTIONS);
      return this;
   }

   @Contract(" -> this")
   public @NotNull BaseGui enableAllInteractions() {
      this.interactionModifiers.clear();
      return this;
   }

   public boolean allInteractionsDisabled() {
      return this.interactionModifiers.size() == InteractionModifier.VALUES.size();
   }

   public boolean canPlaceItems() {
      return !this.interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_PLACE);
   }

   public boolean canTakeItems() {
      return !this.interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_TAKE);
   }

   public boolean canSwapItems() {
      return !this.interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_SWAP);
   }

   public boolean canDropItems() {
      return !this.interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_DROP);
   }

   public boolean allowsOtherActions() {
      return !this.interactionModifiers.contains(InteractionModifier.PREVENT_OTHER_ACTIONS);
   }

   public @NotNull GuiFiller getFiller() {
      return this.filler;
   }

   public @NotNull Map<@NotNull Integer, @NotNull GuiItem> getGuiItems() {
      return this.guiItems;
   }

   public @NotNull Inventory getInventory() {
      return this.inventory;
   }

   public void setInventory(@NotNull Inventory inventory) {
      this.inventory = inventory;
   }

   public int getRows() {
      return this.guiContainer.rows();
   }

   public @NotNull GuiType guiType() {
      return this.guiContainer.guiType();
   }

   @Nullable GuiAction<InventoryClickEvent> getDefaultClickAction() {
      return this.defaultClickAction;
   }

   public void setDefaultClickAction(@Nullable GuiAction<@NotNull InventoryClickEvent> defaultClickAction) {
      this.defaultClickAction = defaultClickAction;
   }

   @Nullable GuiAction<InventoryClickEvent> getDefaultTopClickAction() {
      return this.defaultTopClickAction;
   }

   public void setDefaultTopClickAction(@Nullable GuiAction<@NotNull InventoryClickEvent> defaultTopClickAction) {
      this.defaultTopClickAction = defaultTopClickAction;
   }

   @Nullable GuiAction<InventoryClickEvent> getPlayerInventoryAction() {
      return this.playerInventoryAction;
   }

   public void setPlayerInventoryAction(@Nullable GuiAction<@NotNull InventoryClickEvent> playerInventoryAction) {
      this.playerInventoryAction = playerInventoryAction;
   }

   @Nullable GuiAction<InventoryDragEvent> getDragAction() {
      return this.dragAction;
   }

   public void setDragAction(@Nullable GuiAction<@NotNull InventoryDragEvent> dragAction) {
      this.dragAction = dragAction;
   }

   @Nullable GuiAction<InventoryCloseEvent> getCloseGuiAction() {
      return this.closeGuiAction;
   }

   public void setCloseGuiAction(@Nullable GuiAction<@NotNull InventoryCloseEvent> closeGuiAction) {
      this.closeGuiAction = closeGuiAction;
   }

   @Nullable GuiAction<InventoryOpenEvent> getOpenGuiAction() {
      return this.openGuiAction;
   }

   public void setOpenGuiAction(@Nullable GuiAction<@NotNull InventoryOpenEvent> openGuiAction) {
      this.openGuiAction = openGuiAction;
   }

   @Nullable GuiAction<InventoryClickEvent> getOutsideClickAction() {
      return this.outsideClickAction;
   }

   public void setOutsideClickAction(@Nullable GuiAction<@NotNull InventoryClickEvent> outsideClickAction) {
      this.outsideClickAction = outsideClickAction;
   }

   @Nullable GuiAction<InventoryClickEvent> getSlotAction(int slot) {
      return (GuiAction)this.slotActions.get(slot);
   }

   void populateGui() {
      for(Map.Entry<Integer, GuiItem> entry : this.guiItems.entrySet()) {
         this.inventory.setItem((Integer)entry.getKey(), ((GuiItem)entry.getValue()).getItemStack());
      }

   }

   boolean shouldRunCloseAction() {
      return this.runCloseAction;
   }

   boolean shouldRunOpenAction() {
      return this.runOpenAction;
   }

   int getSlotFromRowCol(int row, int col) {
      return col + (row - 1) * 9 - 1;
   }

   private void validateSlot(int slot) {
      GuiType guiType = this.guiContainer.guiType();
      int limit = guiType.getLimit();
      if (guiType == GuiType.CHEST) {
         if (slot < 0 || slot >= this.guiContainer.rows() * limit) {
            this.throwInvalidSlot(slot);
         }

      } else {
         if (slot < 0 || slot > limit) {
            this.throwInvalidSlot(slot);
         }

      }
   }

   private void throwInvalidSlot(int slot) {
      if (this.guiContainer.guiType() == GuiType.CHEST) {
         throw new GuiException("Slot " + slot + " is not valid for the gui type - " + this.guiContainer.guiType().name() + " and rows - " + this.guiContainer.rows() + "!");
      } else {
         throw new GuiException("Slot " + slot + " is not valid for the gui type - " + this.guiContainer.guiType().name() + "!");
      }
   }

   protected @NotNull GuiContainer guiContainer() {
      return this.guiContainer;
   }

   static {
      try {
         GET_SCHEDULER_METHOD = Entity.class.getMethod("getScheduler");
         Class<?> entityScheduler = Class.forName("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
         EXECUTE_METHOD = entityScheduler.getMethod("execute", Plugin.class, Runnable.class, Runnable.class, Long.TYPE);
      } catch (ClassNotFoundException | NoSuchMethodException var1) {
      }

      Bukkit.getPluginManager().registerEvents(new GuiListener(), plugin);
      Bukkit.getPluginManager().registerEvents(new InteractionModifierListener(), plugin);
   }
}
