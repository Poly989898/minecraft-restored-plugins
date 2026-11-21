package com.nexomc.nexo.mechanics.misc.backpack;

import com.nexomc.libs.gui.builder.gui.StorageBuilder;
import com.nexomc.libs.gui.guis.Gui;
import com.nexomc.libs.gui.guis.StorageGui;
import com.nexomc.libs.morepersistentdatatypes.DataType;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import com.nexomc.nexo.utils.ItemUtils;
import com.nexomc.nexo.utils.SchedulerUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000l\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u001c\u0010\u0006\u001a\u00020\u0007*\u00020\bH\u0007b\u000e\b\t\u0012\n\b\n\u0012\u0006\b\n0\u000b8\fJ\u0010\u0010\r\u001a\u00020\u0007*\u00020\u000eH\u0007b\u0002\b\tJ\u0010\u0010\u000f\u001a\u00020\u0007*\u00020\u0010H\u0007b\u0002\b\tJ\u001c\u0010\u0011\u001a\u00020\u0007*\u00020\u0012H\u0007b\u000e\b\t\u0012\n\b\n\u0012\u0006\b\n0\u000b8\u0013J\u0010\u0010\u0014\u001a\u00020\u0007*\u00020\u0015H\u0007b\u0002\b\tJ\u001a\u0010\u0016\u001a\u00020\u0007*\u00020\u0017H\u0007b\f\b\t\u0012\b\b\u0018\u0012\u0004\b\u0007\u0010\u0002J\u001a\u0010\u0019\u001a\u0004\u0018\u00010\u001a2\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001eH\u0002J\u0012\u0010\u001f\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\u001eH\u0002J\u0010\u0010\"\u001a\u00020\u00072\u0006\u0010#\u001a\u00020$H\u0002J\u001a\u0010%\u001a\u00020\u00072\u0006\u0010#\u001a\u00020$2\b\b\u0002\u0010&\u001a\u00020 H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006'"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/backpack/BackpackListener;", "Lorg/bukkit/event/Listener;", "factory", "Lcom/nexomc/nexo/mechanics/misc/backpack/BackpackMechanicFactory;", "<init>", "(Lcom/nexomc/nexo/mechanics/misc/backpack/BackpackMechanicFactory;)V", "onBlockPlace", "", "Lorg/bukkit/event/block/BlockPlaceEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "MONITOR", "onDrop", "Lorg/bukkit/event/player/PlayerDropItemEvent;", "onPlayerDisconnect", "Lorg/bukkit/event/player/PlayerQuitEvent;", "onPlayerInteract", "Lorg/bukkit/event/player/PlayerInteractEvent;", "HIGH", "onPlayerSwapHandItems", "Lorg/bukkit/event/player/PlayerSwapHandItemsEvent;", "onPickupItem", "Lorg/bukkit/event/entity/EntityPickupItemEvent;", "ignoreCancelled", "createGUI", "Lcom/nexomc/libs/gui/guis/StorageGui;", "mechanic", "Lcom/nexomc/nexo/mechanics/misc/backpack/BackpackMechanic;", "backpack", "Lorg/bukkit/inventory/ItemStack;", "isBackpack", "", "item", "openBackpack", "player", "Lorg/bukkit/entity/Player;", "closeBackpack", "force", "com.nexomc:core"}
)
public final class BackpackListener implements Listener {
   @NotNull
   private final BackpackMechanicFactory factory;

   public BackpackListener(@NotNull BackpackMechanicFactory factory) {
      super();
      this.factory = factory;
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public final void onBlockPlace(@NotNull BlockPlaceEvent blockPlaceEvent) {
      if (this.isBackpack(blockPlaceEvent.getPlayer().getInventory().getItemInMainHand())) {
         blockPlaceEvent.setCancelled(true);
      }

   }

   @EventHandler
   public final void onDrop(@NotNull PlayerDropItemEvent playerDropItemEvent) {
      Player player = playerDropItemEvent.getPlayer();
      this.closeBackpack(player, true);
   }

   @EventHandler
   public final void onPlayerDisconnect(@NotNull PlayerQuitEvent playerQuitEvent) {
      Player player = playerQuitEvent.getPlayer();
      this.closeBackpack(player, true);
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public final void onPlayerInteract(@NotNull PlayerInteractEvent playerInteractEvent) {
      boolean flag;
      if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR || playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) {
         if (playerInteractEvent.getHand() != EquipmentSlot.OFF_HAND && playerInteractEvent.useItemInHand() != Result.DENY) {
            if (this.isBackpack(playerInteractEvent.getItem())) {
               Material material;
               block: {
                  Block clickedBlock = playerInteractEvent.getClickedBlock();
                  if (clickedBlock != null) {
                     Material type = clickedBlock.getType();
                     if (type != null) {
                        Material it = type;
                        int value = 0;
                        material = it == Material.DECORATED_POT || it == Material.WATER_CAULDRON ? it : null;
                        break block;
                     }
                  }

                  material = null;
               }

               if (material != null) {
                  if (playerInteractEvent.getPlayer().isSneaking()) {
                     playerInteractEvent.setUseInteractedBlock(Result.DENY);
                  } else if (!playerInteractEvent.getPlayer().isSneaking()) {
                     return;
                  }
               }

               block: {
                  ItemStack item = playerInteractEvent.getItem();
                  if (item != null) {
                     Equippable data = (Equippable)item.getData(DataComponentTypes.EQUIPPABLE);
                     if (data != null) {
                        flag = data.swappable();
                        break block;
                     }
                  }

                  flag = false;
               }

               if (flag) {
                  if (playerInteractEvent.getPlayer().isSneaking()) {
                     playerInteractEvent.setUseItemInHand(Result.DENY);
                  } else {
                     if (!playerInteractEvent.getPlayer().isSneaking()) {
                        return;
                     }

                     playerInteractEvent.setUseItemInHand(Result.ALLOW);
                  }
               } else {
                  playerInteractEvent.setUseItemInHand(Result.ALLOW);
               }

               Player player = playerInteractEvent.getPlayer();
               this.openBackpack(player);
            }
         }
      }
   }

   @EventHandler
   public final void onPlayerSwapHandItems(@NotNull PlayerSwapHandItemsEvent playerSwapHandItemsEvent) {
      if (this.isBackpack(playerSwapHandItemsEvent.getOffHandItem()) || this.isBackpack(playerSwapHandItemsEvent.getMainHandItem())) {
         InventoryHolder holder = playerSwapHandItemsEvent.getPlayer().getOpenInventory().getTopInventory().getHolder();
         StorageGui storageGui = holder instanceof StorageGui ? (StorageGui)holder : null;
         if ((holder instanceof StorageGui ? (StorageGui)holder : null) != null) {
            storageGui.close((HumanEntity)playerSwapHandItemsEvent.getPlayer(), true);
         }

      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public final void onPickupItem(@NotNull EntityPickupItemEvent entityPickupItemEvent) {
      LivingEntity entity = entityPickupItemEvent.getEntity();
      Player player2 = entity instanceof Player ? (Player)entity : null;
      if ((entity instanceof Player ? (Player)entity : null) != null) {
         Player player = player2;
         if (player.getOpenInventory().getTopInventory().getHolder() instanceof Gui) {
            this.closeBackpack(player, false);
            this.openBackpack(player);
         }
      }
   }

   private final StorageGui createGUI(BackpackMechanic mechanic, ItemStack backpack) {
      StorageGui storageGui = ((StorageBuilder)((StorageBuilder)((StorageBuilder)Gui.storage().title(AdventureUtilsKt.deserialize(mechanic.getTitle()))).rows(mechanic.getRows())).inventory(BackpackListener::createGUI$lambda$0)).create();
      StorageGui gui = storageGui;
      gui.disableItemDrop();
      gui.disableItemSwap();
      gui.setPlayerInventoryAction((a0_ -> BackpackListener.createGUI$lambda$1(this, mechanic, (InventoryClickEvent)a0_)));
      gui.setDefaultClickAction((a0_ -> BackpackListener.createGUI$lambda$2(this, mechanic, backpack, gui, (InventoryClickEvent)a0_)));
      gui.setDragAction((a0_ -> BackpackListener.createGUI$lambda$3(this, mechanic, (InventoryDragEvent)a0_)));
      gui.setOutsideClickAction((a0_ -> BackpackListener.createGUI$lambda$4(this, (InventoryClickEvent)a0_)));
      gui.setOpenGuiAction((a0_ -> BackpackListener.createGUI$lambda$5(backpack, gui, mechanic, (InventoryOpenEvent)a0_)));
      gui.setCloseGuiAction((a0_ -> BackpackListener.createGUI$lambda$6(backpack, mechanic, gui, (InventoryCloseEvent)a0_)));
      return gui;
   }

   private final boolean isBackpack(ItemStack item) {
      return item != null && item.getType() != Material.BUNDLE && this.factory.getMechanic(NexoItems.idFromItem(item)) != null;
   }

   private final void openBackpack(Player player) {
      ItemStack it = player.getInventory().getItemInMainHand();
      int value = 0;
      ItemStack itemStack = it.getType() != Material.BUNDLE ? it : null;
      if (itemStack != null) {
         ItemStack itemInHand = itemStack;
         BackpackMechanic backpackMechanic = this.factory.getMechanic(NexoItems.idFromItem(itemInHand));
         if (backpackMechanic != null) {
            BackpackMechanic mechanic = backpackMechanic;
            if (!player.hasCooldown(itemInHand)) {
               ItemUtils.INSTANCE.triggerCooldown(player, itemInHand);
               StorageGui storageGui = this.createGUI(mechanic, itemInHand);
               if (storageGui != null) {
                  storageGui.open((HumanEntity)player);
               }

            }
         }
      }
   }

   private final void closeBackpack(Player player, boolean force) {
      InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
      if (force || this.isBackpack(player.getInventory().getItemInMainHand())) {
         if (holder instanceof StorageGui) {
            ((StorageGui)holder).close((HumanEntity)player, true);
         }

      }
   }

   private static final Inventory createGUI$lambda$0(Component t, InventoryHolder o, int r) {
      return Bukkit.createInventory(o, r, t);
   }

   private static final void createGUI$lambda$1(BackpackListener backpackListener, BackpackMechanic mechanic, InventoryClickEvent event) {
      if (backpackListener.isBackpack(event.getCurrentItem()) || backpackListener.isBackpack(event.getCursor())) {
         event.setCancelled(true);
      }

      if (mechanic.isBlacklisted(event.getCurrentItem()) && event.isShiftClick()) {
         event.setCancelled(true);
      }

   }

   private static final void createGUI$lambda$2$0$0(StorageGui gui, PersistentDataContainer it) {
      it.set(BackpackMechanic.Companion.getBACKPACK_KEY(), DataType.ITEM_STACK_ARRAY, gui.getInventory().getContents());
   }

   private static final Unit createGUI$lambda$2$0(ItemStack backpack, StorageGui gui) {
      backpack.editPersistentDataContainer((a0_ -> BackpackListener.createGUI$lambda$2$0$0(gui, (PersistentDataContainer)a0_)));
      return Unit.INSTANCE;
   }

   private static final void createGUI$lambda$2(BackpackListener backpackListener, BackpackMechanic mechanic, ItemStack backpack, StorageGui gui, InventoryClickEvent event) {
      if (!backpackListener.isBackpack(event.getCurrentItem()) && !backpackListener.isBackpack(event.getCursor())) {
         Inventory topInventory = event.getWhoClicked().getOpenInventory().getTopInventory();
         Inventory backpackInv = topInventory;
         if (mechanic.isBlacklisted(event.getCurrentItem()) && (Intrinsics.areEqual(backpackInv, event.getClickedInventory()) || event.isShiftClick())) {
            event.setCancelled(true);
         }

         if (mechanic.isBlacklisted(event.getCursor()) && Intrinsics.areEqual(backpackInv, event.getClickedInventory())) {
            event.setCancelled(true);
         }
      } else {
         event.setCancelled(true);
      }

      if (event.getCursor().isEmpty()) {
         ItemStack currentItem = event.getCurrentItem();
         if (!(currentItem != null ? !currentItem.isEmpty() : false)) {
            return;
         }
      }

      SchedulerUtils.INSTANCE.runTaskLater(1L, (() -> BackpackListener.createGUI$lambda$2$0(backpack, gui)));
   }

   private static final void createGUI$lambda$3(BackpackListener backpackListener, BackpackMechanic mechanic, InventoryDragEvent event) {
      if (backpackListener.isBackpack(event.getCursor())) {
         event.setCancelled(true);
      } else {
         Inventory topInventory = event.getWhoClicked().getOpenInventory().getTopInventory();
         Inventory backpackInv = topInventory;
         if (mechanic.isBlacklisted(event.getCursor()) && Intrinsics.areEqual(backpackInv, event.getInventory())) {
            event.setCancelled(true);
         }
      }

   }

   private static final void createGUI$lambda$4(BackpackListener backpackListener, InventoryClickEvent event) {
      if (backpackListener.isBackpack(event.getCursor())) {
         event.setCancelled(true);
      }

   }

   private static final void createGUI$lambda$5(ItemStack backpack, StorageGui gui, BackpackMechanic mechanic, InventoryOpenEvent event) {
      HumanEntity humanEntity = event.getPlayer();
      Player player = (Player)humanEntity;
      ItemStack[] contents = (ItemStack[])backpack.getPersistentDataContainer().get(BackpackMechanic.Companion.getBACKPACK_KEY(), DataType.ITEM_STACK_ARRAY);
      if (contents != null) {
         gui.getInventory().setContents(contents);
      }

      player.playSound(player.getLocation(), mechanic.getOpenSound(), mechanic.getVolume(), mechanic.getPitch());
   }

   private static final void createGUI$lambda$6$0(StorageGui gui, PersistentDataContainer it) {
      it.set(BackpackMechanic.Companion.getBACKPACK_KEY(), DataType.ITEM_STACK_ARRAY, gui.getInventory().getContents());
   }

   private static final void createGUI$lambda$6(ItemStack backpack, BackpackMechanic mechanic, StorageGui gui, InventoryCloseEvent event) {
      HumanEntity humanEntity = event.getPlayer();
      Player player = (Player)humanEntity;
      backpack.editPersistentDataContainer((a0_ -> BackpackListener.createGUI$lambda$6$0(gui, (PersistentDataContainer)a0_)));
      player.getWorld().playSound(player.getLocation(), mechanic.getCloseSound(), mechanic.getVolume(), mechanic.getPitch());
   }
}
