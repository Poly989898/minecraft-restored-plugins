package io.lumine.mythic.bukkit.utils.events.extra;

import io.lumine.mythic.bukkit.utils.plugin.LoaderUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

public class ArmorEquipEventListener implements Listener {
   private final List<String> blockedMaterials = this.getBlocks();

   public ArmorEquipEventListener() {
      LoaderUtils.getPlugin().getServer().getPluginManager().registerEvents(this, LoaderUtils.getPlugin());
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public final void inventoryClick(InventoryClickEvent e) {
      boolean shift = false;
      boolean numberkey = false;
      if (!e.isCancelled()) {
         if (e.getAction() != InventoryAction.NOTHING) {
            if (e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT)) {
               shift = true;
            }

            if (e.getClick().equals(ClickType.NUMBER_KEY)) {
               numberkey = true;
            }

            if (e.getSlotType() == SlotType.ARMOR || e.getSlotType() == SlotType.QUICKBAR || e.getSlotType() == SlotType.CONTAINER) {
               if (e.getClickedInventory() == null || e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
                  if (e.getInventory().getType().equals(InventoryType.CRAFTING) || e.getInventory().getType().equals(InventoryType.PLAYER)) {
                     if (e.getWhoClicked() instanceof Player) {
                        ArmorType newArmorType = ArmorType.matchType(shift ? e.getCurrentItem() : e.getCursor());
                        if (shift || newArmorType == null || e.getRawSlot() == newArmorType.getSlot()) {
                           if (shift) {
                              newArmorType = ArmorType.matchType(e.getCurrentItem());
                              if (newArmorType != null) {
                                 boolean equipping = true;
                                 if (e.getRawSlot() == newArmorType.getSlot()) {
                                    equipping = false;
                                 }

                                 if (newArmorType.equals(ArmorType.HELMET)
                                       && (
                                          equipping
                                             ? isAirOrNull(e.getWhoClicked().getInventory().getHelmet())
                                             : !isAirOrNull(e.getWhoClicked().getInventory().getHelmet())
                                       )
                                    || newArmorType.equals(ArmorType.CHESTPLATE)
                                       && (
                                          equipping
                                             ? isAirOrNull(e.getWhoClicked().getInventory().getChestplate())
                                             : !isAirOrNull(e.getWhoClicked().getInventory().getChestplate())
                                       )
                                    || newArmorType.equals(ArmorType.LEGGINGS)
                                       && (
                                          equipping
                                             ? isAirOrNull(e.getWhoClicked().getInventory().getLeggings())
                                             : !isAirOrNull(e.getWhoClicked().getInventory().getLeggings())
                                       )
                                    || newArmorType.equals(ArmorType.BOOTS)
                                       && (
                                          equipping
                                             ? isAirOrNull(e.getWhoClicked().getInventory().getBoots())
                                             : !isAirOrNull(e.getWhoClicked().getInventory().getBoots())
                                       )) {
                                    ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(
                                       (Player)e.getWhoClicked(),
                                       ArmorEquipEvent.EquipMethod.SHIFT_CLICK,
                                       newArmorType,
                                       equipping ? null : e.getCurrentItem(),
                                       equipping ? e.getCurrentItem() : null
                                    );
                                    Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                                    if (armorEquipEvent.isCancelled()) {
                                       e.setCancelled(true);
                                    }
                                 }
                              }
                           } else {
                              ItemStack newArmorPiece = e.getCursor();
                              ItemStack oldArmorPiece = e.getCurrentItem();
                              if (numberkey) {
                                 if (e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
                                    ItemStack hotbarItem = e.getClickedInventory().getItem(e.getHotbarButton());
                                    if (!isAirOrNull(hotbarItem)) {
                                       newArmorType = ArmorType.matchType(hotbarItem);
                                       newArmorPiece = hotbarItem;
                                       oldArmorPiece = e.getClickedInventory().getItem(e.getSlot());
                                    } else {
                                       newArmorType = ArmorType.matchType(!isAirOrNull(e.getCurrentItem()) ? e.getCurrentItem() : e.getCursor());
                                    }
                                 }
                              } else if (isAirOrNull(e.getCursor()) && !isAirOrNull(e.getCurrentItem())) {
                                 newArmorType = ArmorType.matchType(e.getCurrentItem());
                              }

                              if (newArmorType != null && e.getRawSlot() == newArmorType.getSlot()) {
                                 ArmorEquipEvent.EquipMethod method = ArmorEquipEvent.EquipMethod.PICK_DROP;
                                 if (e.getAction().equals(InventoryAction.HOTBAR_SWAP) || numberkey) {
                                    method = ArmorEquipEvent.EquipMethod.HOTBAR_SWAP;
                                 }

                                 ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(
                                    (Player)e.getWhoClicked(), method, newArmorType, oldArmorPiece, newArmorPiece
                                 );
                                 Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                                 if (armorEquipEvent.isCancelled()) {
                                    e.setCancelled(true);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void playerInteractEvent(PlayerInteractEvent e) {
      if (!e.useItemInHand().equals(Result.DENY)) {
         if (e.getAction() != Action.PHYSICAL) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
               Player player = e.getPlayer();
               if (!e.useInteractedBlock().equals(Result.DENY)
                  && e.getClickedBlock() != null
                  && e.getAction() == Action.RIGHT_CLICK_BLOCK
                  && !player.isSneaking()) {
                  Material mat = e.getClickedBlock().getType();

                  for (String s : this.blockedMaterials) {
                     if (mat.name().equalsIgnoreCase(s)) {
                        return;
                     }
                  }
               }

               ArmorType newArmorType = ArmorType.matchType(e.getItem());
               if (newArmorType != null) {
                  ItemStack oldArmorPiece = switch (newArmorType) {
                     case HELMET -> e.getPlayer().getInventory().getHelmet();
                     case CHESTPLATE -> e.getPlayer().getInventory().getChestplate();
                     case LEGGINGS -> e.getPlayer().getInventory().getLeggings();
                     case BOOTS -> e.getPlayer().getInventory().getBoots();
                  };
                  ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(
                     e.getPlayer(), ArmorEquipEvent.EquipMethod.HOTBAR, newArmorType, isAirOrNull(oldArmorPiece) ? null : oldArmorPiece, e.getItem()
                  );
                  Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                  if (armorEquipEvent.isCancelled()) {
                     e.setCancelled(true);
                     player.updateInventory();
                  }
               }
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void inventoryDrag(InventoryDragEvent event) {
      ArmorType type = ArmorType.matchType(event.getOldCursor());
      if (!event.getRawSlots().isEmpty()) {
         if (type != null && type.getSlot() == event.getRawSlots().stream().findFirst().orElse(0)) {
            ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(
               (Player)event.getWhoClicked(), ArmorEquipEvent.EquipMethod.DRAG, type, null, event.getOldCursor()
            );
            Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
            if (armorEquipEvent.isCancelled()) {
               event.setResult(Result.DENY);
               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler
   public void itemBreakEvent(PlayerItemBreakEvent e) {
      ArmorType type = ArmorType.matchType(e.getBrokenItem());
      if (type != null) {
         Player p = e.getPlayer();
         ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.BROKE, type, e.getBrokenItem(), null);
         Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
         if (armorEquipEvent.isCancelled()) {
            ItemStack i = e.getBrokenItem().clone();
            i.setAmount(1);
            i.setDurability((short)(i.getDurability() - 1));
            if (type.equals(ArmorType.HELMET)) {
               p.getInventory().setHelmet(i);
            } else if (type.equals(ArmorType.CHESTPLATE)) {
               p.getInventory().setChestplate(i);
            } else if (type.equals(ArmorType.LEGGINGS)) {
               p.getInventory().setLeggings(i);
            } else if (type.equals(ArmorType.BOOTS)) {
               p.getInventory().setBoots(i);
            }
         }
      }
   }

   @EventHandler
   public void playerDeathEvent(PlayerDeathEvent e) {
      Player p = e.getEntity();
      if (!e.getKeepInventory()) {
         for (ItemStack i : p.getInventory().getArmorContents()) {
            if (!isAirOrNull(i)) {
               Bukkit.getServer().getPluginManager().callEvent(new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.DEATH, ArmorType.matchType(i), i, null));
            }
         }
      }
   }

   @EventHandler
   public void dispenseArmorEvent(BlockDispenseArmorEvent event) {
      ArmorType type = ArmorType.matchType(event.getItem());
      if (type != null && event.getTargetEntity() instanceof Player) {
         Player p = (Player)event.getTargetEntity();
         ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.DISPENSER, type, null, event.getItem());
         Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
         if (armorEquipEvent.isCancelled()) {
            event.setCancelled(true);
         }
      }
   }

   public static boolean isAirOrNull(ItemStack item) {
      return item == null || item.getType().equals(Material.AIR);
   }

   private List<String> getBlocks() {
      List<String> blocks = new ArrayList<>();
      blocks.add("DAYLIGHT_DETECTOR");
      blocks.add("DAYLIGHT_DETECTOR_INVERTED");
      blocks.add("FURNACE");
      blocks.add("CHEST");
      blocks.add("TRAPPED_CHEST");
      blocks.add("BEACON");
      blocks.add("DISPENSER");
      blocks.add("DROPPER");
      blocks.add("HOPPER");
      blocks.add("WORKBENCH");
      blocks.add("ENCHANTMENT_TABLE");
      blocks.add("ENDER_CHEST");
      blocks.add("ANVIL");
      blocks.add("BED_BLOCK");
      blocks.add("FENCE_GATE");
      blocks.add("SPRUCE_FENCE_GATE");
      blocks.add("BIRCH_FENCE_GATE");
      blocks.add("ACACIA_FENCE_GATE");
      blocks.add("JUNGLE_FENCE_GATE");
      blocks.add("DARK_OAK_FENCE_GATE");
      blocks.add("IRON_DOOR_BLOCK");
      blocks.add("WOODEN_DOOR");
      blocks.add("SPRUCE_DOOR");
      blocks.add("BIRCH_DOOR");
      blocks.add("JUNGLE_DOOR");
      blocks.add("ACACIA_DOOR");
      blocks.add("DARK_OAK_DOOR");
      blocks.add("WOOD_BUTTON");
      blocks.add("STONE_BUTTON");
      blocks.add("TRAP_DOOR");
      blocks.add("IRON_TRAPDOOR");
      blocks.add("DIODE_BLOCK_OFF");
      blocks.add("DIODE_BLOCK_ON");
      blocks.add("REDSTONE_COMPARATOR_OFF");
      blocks.add("REDSTONE_COMPARATOR_ON");
      blocks.add("FENCE");
      blocks.add("SPRUCE_FENCE");
      blocks.add("BIRCH_FENCE");
      blocks.add("JUNGLE_FENCE");
      blocks.add("DARK_OAK_FENCE");
      blocks.add("ACACIA_FENCE");
      blocks.add("NETHER_FENCE");
      blocks.add("BREWING_STAND");
      blocks.add("CAULDRON");
      blocks.add("SIGN_POST");
      blocks.add("WALL_SIGN");
      blocks.add("SIGN");
      blocks.add("DRAGON_EGG");
      blocks.add("LEVER");
      blocks.add("SHULKER_BOX");
      return blocks;
   }
}
