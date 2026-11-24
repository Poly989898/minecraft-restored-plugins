package net.Indyuce.inventory.gui;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.version.VInventoryView;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.api.event.ItemEquipEvent;
import net.Indyuce.inventory.inventory.CustomInventoryHandler;
import net.Indyuce.inventory.slot.CustomSlot;
import net.Indyuce.inventory.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerInventoryView implements InventoryHolder {
   private final CustomInventoryHandler targetData;
   private final Player player;
   private final Player target;
   private static final List<InventoryAction> supported = Arrays.asList(InventoryAction.PICKUP_ALL, InventoryAction.SWAP_WITH_CURSOR, InventoryAction.PLACE_ALL);

   public PlayerInventoryView(Player player) {
      this(player, player);
   }

   public PlayerInventoryView(Player player, Player target) {
      this.target = target;
      this.player = player;
      this.targetData = (CustomInventoryHandler)MMOInventory.plugin.getDataManager().get(target);
   }

   public Inventory getInventory() {
      Inventory inv = Bukkit.createInventory(
         this,
         MMOInventory.plugin.inventorySlots,
         MMOInventory.plugin
            .getPlaceholderParser()
            .parse(
               this.target,
               this.target.equals(this.player)
                  ? MMOInventory.plugin.getTranslation("inventory-name.self")
                  : MMOInventory.plugin.getTranslation("inventory-name.other").replace("{name}", this.target.getName())
            )
      );

      for (CustomSlot slot : MMOInventory.plugin.getSlotManager().getLoaded()) {
         ItemStack item = this.targetData.getItem(slot);
         inv.setItem(slot.getIndex(), UtilityMethods.isAir(item) ? slot.displayItem(this.target) : item);
      }

      for (int j = 0; j < inv.getSize(); j++) {
         ItemStack item = inv.getItem(j);
         if (UtilityMethods.isAir(item) && MMOInventory.plugin.getSlotManager().hasFiller()) {
            inv.setItem(j, MMOInventory.plugin.getSlotManager().getFiller().displayItem(this.target));
         }
      }

      return inv;
   }

   public void open() {
      this.player.openInventory(this.getInventory());
   }

   public void synchronize(Inventory inv, boolean trustBukkit) {
      for (CustomSlot slot : MMOInventory.plugin.getSlotManager().getLoaded()) {
         ItemStack bukkitItem = this.realItemOrNull(inv.getItem(slot.getIndex()));
         ItemStack pluginItem = this.realItemOrNull(this.targetData.getItem(slot));
         boolean match = bukkitItem == pluginItem
            || bukkitItem != null && pluginItem != null && pluginItem.hashCode() == bukkitItem.hashCode() && pluginItem.equals(bukkitItem);
         if (!match) {
            if (trustBukkit) {
               this.targetData.setItem(slot, bukkitItem);
            } else {
               inv.setItem(slot.getIndex(), UtilityMethods.isAir(pluginItem) ? slot.displayItem(this.target) : pluginItem);
            }
         }
      }
   }

   @Nullable
   private ItemStack realItemOrNull(@Nullable ItemStack item) {
      return !UtilityMethods.isAir(item) && !Utils.isGuiItem(item) ? item : null;
   }

   public void whenCanceled(InventoryClickEvent event) {
      this.synchronize(event.getInventory(), true);
   }

   public void whenClicked(InventoryClickEvent event) {
      this.synchronize(event.getInventory(), false);
      ItemStack picked = event.getCurrentItem();
      if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
         event.setCancelled(true);
         if (!UtilityMethods.isAir(picked) && !Utils.isGuiItem(picked)) {
            if (event.getClickedInventory().equals(VersionUtils.getView(event).getBottomInventory())) {
               CustomSlot best = this.findBestSlot(picked);
               if (best == null) {
                  return;
               }

               ItemEquipEvent called = new ItemEquipEvent(this.target, picked, best, ItemEquipEvent.EquipAction.SHIFT_CLICK_EQUIP);
               Bukkit.getPluginManager().callEvent(called);
               if (called.isCancelled()) {
                  return;
               }

               this.targetData.setItem(best, picked);
               event.getInventory().setItem(best.getIndex(), picked);
               event.setCurrentItem(null);
               this.forEachWatcher(view -> view.getTopInventory().setItem(best.getIndex(), picked));
            } else {
               if (picked != null && !this.canBeMoved(picked)) {
                  event.setCancelled(true);
                  return;
               }

               CustomSlot slot = MMOInventory.plugin.getSlotManager().get(event.getRawSlot());
               if (slot == null) {
                  return;
               }

               int empty = this.player.getInventory().firstEmpty();
               if (empty == -1) {
                  return;
               }

               ItemEquipEvent called = new ItemEquipEvent(this.target, picked, null, ItemEquipEvent.EquipAction.SHIFT_CLICK_UNEQUIP);
               Bukkit.getPluginManager().callEvent(called);
               if (called.isCancelled()) {
                  return;
               }

               this.targetData.setItem(slot, null);
               this.player.getInventory().setItem(empty, picked);
               event.setCurrentItem(slot.displayItem(this.target));
               this.forEachWatcher(view -> view.getTopInventory().setItem(slot.getIndex(), slot.displayItem(this.target)));
            }
         }
      } else if (!supported.contains(event.getAction())) {
         event.setCancelled(true);
      } else if (Utils.isGuiItem(picked) && Utils.getGuiItemId(picked).equals("fill")) {
         event.setCancelled(true);
      } else if (Utils.isGuiItem(picked) && UtilityMethods.isAir(event.getCursor())) {
         event.setCancelled(true);
      } else {
         CustomSlot slot = MMOInventory.plugin.getSlotManager().get(event.getRawSlot());
         if (slot == null) {
            if (event.getClickedInventory().equals(event.getInventory())) {
               event.setCancelled(true);
            }
         } else if (picked != null && !this.canBeMoved(picked)) {
            event.setCancelled(true);
         } else {
            ItemStack cursor = event.getCursor();
            if (!UtilityMethods.isAir(event.getCursor())) {
               if (MMOInventory.plugin.getConfig().getBoolean("disable-equiping-stacked-items", true) && event.getCursor().getAmount() > 1) {
                  event.setCancelled(true);
                  return;
               }

               if (!slot.canHost(this.targetData, cursor)) {
                  event.setCancelled(true);
                  return;
               }
            }

            ItemEquipEvent.EquipAction action = UtilityMethods.isAir(event.getCursor())
               ? ItemEquipEvent.EquipAction.UNEQUIP
               : (Utils.isGuiItem(cursor) ? ItemEquipEvent.EquipAction.EQUIP : ItemEquipEvent.EquipAction.SWAP_ITEMS);
            ItemEquipEvent equipEvent = new ItemEquipEvent(this.target, event.getCursor(), slot, action);
            Bukkit.getPluginManager().callEvent(equipEvent);
            if (equipEvent.isCancelled()) {
               event.setCancelled(true);
            } else {
               this.targetData.setItem(slot, event.getCursor());
               ItemStack newItem = UtilityMethods.isAir(event.getCursor()) ? slot.displayItem(this.target) : event.getCursor();
               this.forEachWatcher(view -> view.getTopInventory().setItem(slot.getIndex(), newItem));
               if (Utils.isGuiItem(picked)) {
                  event.setCurrentItem(null);
               }

               if (UtilityMethods.isAir(event.getCursor())) {
                  Bukkit.getScheduler().runTask(MMOInventory.plugin, () -> event.getInventory().setItem(slot.getIndex(), slot.displayItem(this.target)));
               }

               MMOInventory.plugin.updateInventory(this.player);
            }
         }
      }
   }

   private boolean canBeMoved(@NotNull ItemStack item) {
      return !item.hasItemMeta() || !item.getItemMeta().hasEnchant(Enchantment.BINDING_CURSE);
   }

   private CustomSlot findBestSlot(ItemStack item) {
      for (CustomSlot slot : MMOInventory.plugin.getSlotManager().getLoaded()) {
         if ((slot.getType().isCustom() || slot.getType().getVanillaSlotHandler().supportsShiftClick())
            && !this.targetData.hasItem(slot)
            && slot.canHost(this.targetData, item)) {
            return slot;
         }
      }

      return null;
   }

   public void forEachWatcher(@NotNull Consumer<VInventoryView> consumer) {
      for (Player online : Bukkit.getOnlinePlayers()) {
         VInventoryView open = VersionUtils.getOpen(online);
         if (open.getTopInventory().getHolder() instanceof PlayerInventoryView) {
            PlayerInventoryView customGui = (PlayerInventoryView)open.getTopInventory().getHolder();
            if (!this.equals(customGui) && customGui.target.equals(this.target)) {
               consumer.accept(open);
            }
         }
      }
   }
}
