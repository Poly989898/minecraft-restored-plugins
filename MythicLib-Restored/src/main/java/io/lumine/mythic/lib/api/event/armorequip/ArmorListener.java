package io.lumine.mythic.lib.api.event.armorequip;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
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

public class ArmorListener implements Listener {
   private final List<Material> blockedMaterials;

   public ArmorListener(List<String> blockedMaterialNames) {
      this.blockedMaterials = blockedMaterialNames.stream().<Material>map(Material::getMaterial).filter(Objects::nonNull).collect(Collectors.toList());
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public final void onClick(InventoryClickEvent event) {
      boolean shift = false;
      boolean numberkey = false;
      if (event.getAction() != InventoryAction.NOTHING) {
         if (event.getClick().equals(ClickType.SHIFT_LEFT) || event.getClick().equals(ClickType.SHIFT_RIGHT)) {
            shift = true;
         }

         if (event.getClick().equals(ClickType.NUMBER_KEY)) {
            numberkey = true;
         }

         if (event.getSlotType() == SlotType.ARMOR || event.getSlotType() == SlotType.QUICKBAR || event.getSlotType() == SlotType.CONTAINER) {
            if (event.getClickedInventory() == null || event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
               if (event.getInventory().getType().equals(InventoryType.CRAFTING) || event.getInventory().getType().equals(InventoryType.PLAYER)) {
                  if (event.getWhoClicked() instanceof Player) {
                     ArmorType newArmorType = ArmorType.matchType(shift ? event.getCurrentItem() : event.getCursor());
                     if (shift || newArmorType == null || event.getRawSlot() == newArmorType.getSlot()) {
                        if (shift) {
                           newArmorType = ArmorType.matchType(event.getCurrentItem());
                           if (newArmorType != null) {
                              boolean equipping = true;
                              if (event.getRawSlot() == newArmorType.getSlot()) {
                                 equipping = false;
                              }

                              if (newArmorType.equals(ArmorType.HELMET)
                                    && (
                                       equipping
                                          ? isAirOrNull(event.getWhoClicked().getInventory().getHelmet())
                                          : !isAirOrNull(event.getWhoClicked().getInventory().getHelmet())
                                    )
                                 || newArmorType.equals(ArmorType.CHESTPLATE)
                                    && (
                                       equipping
                                          ? isAirOrNull(event.getWhoClicked().getInventory().getChestplate())
                                          : !isAirOrNull(event.getWhoClicked().getInventory().getChestplate())
                                    )
                                 || newArmorType.equals(ArmorType.LEGGINGS)
                                    && (
                                       equipping
                                          ? isAirOrNull(event.getWhoClicked().getInventory().getLeggings())
                                          : !isAirOrNull(event.getWhoClicked().getInventory().getLeggings())
                                    )
                                 || newArmorType.equals(ArmorType.BOOTS)
                                    && (
                                       equipping
                                          ? isAirOrNull(event.getWhoClicked().getInventory().getBoots())
                                          : !isAirOrNull(event.getWhoClicked().getInventory().getBoots())
                                    )) {
                                 ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(
                                    (Player)event.getWhoClicked(),
                                    ArmorEquipEvent.EquipMethod.SHIFT_CLICK,
                                    newArmorType,
                                    equipping ? null : event.getCurrentItem(),
                                    equipping ? event.getCurrentItem() : null
                                 );
                                 Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                                 if (armorEquipEvent.isCancelled()) {
                                    event.setCancelled(true);
                                 }
                              }
                           }
                        } else {
                           ItemStack newArmorPiece = event.getCursor();
                           ItemStack oldArmorPiece = event.getCurrentItem();
                           if (numberkey) {
                              if (event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
                                 ItemStack hotbarItem = event.getClickedInventory().getItem(event.getHotbarButton());
                                 if (!isAirOrNull(hotbarItem)) {
                                    newArmorType = ArmorType.matchType(hotbarItem);
                                    newArmorPiece = hotbarItem;
                                    oldArmorPiece = event.getClickedInventory().getItem(event.getSlot());
                                 } else {
                                    newArmorType = ArmorType.matchType(!isAirOrNull(event.getCurrentItem()) ? event.getCurrentItem() : event.getCursor());
                                 }
                              }
                           } else if (isAirOrNull(event.getCursor()) && !isAirOrNull(event.getCurrentItem())) {
                              newArmorType = ArmorType.matchType(event.getCurrentItem());
                           }

                           if (newArmorType != null && event.getRawSlot() == newArmorType.getSlot()) {
                              ArmorEquipEvent.EquipMethod method = ArmorEquipEvent.EquipMethod.PICK_DROP;
                              if (event.getAction().equals(InventoryAction.HOTBAR_SWAP) || numberkey) {
                                 method = ArmorEquipEvent.EquipMethod.HOTBAR_SWAP;
                              }

                              ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(
                                 (Player)event.getWhoClicked(), method, newArmorType, oldArmorPiece, newArmorPiece
                              );
                              Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                              if (armorEquipEvent.isCancelled()) {
                                 event.setCancelled(true);
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

   @EventHandler(priority = EventPriority.HIGHEST)
   public void onInteract(PlayerInteractEvent event) {
      if (!event.useItemInHand().equals(Result.DENY)) {
         if (event.getAction() != Action.PHYSICAL) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
               Player player = event.getPlayer();
               if (!event.useInteractedBlock().equals(Result.DENY)
                  && event.getClickedBlock() != null
                  && event.getAction() == Action.RIGHT_CLICK_BLOCK
                  && !player.isSneaking()) {
                  Material mat = event.getClickedBlock().getType();
                  if (this.blockedMaterials.contains(mat)) {
                     return;
                  }
               }

               ArmorType newArmorType = ArmorType.matchType(event.getItem());
               if (event.getItem() != null && event.getItem().getType() == Material.CARVED_PUMPKIN) {
                  return;
               }

               if (newArmorType != null
                  && (
                     newArmorType.equals(ArmorType.HELMET) && isAirOrNull(event.getPlayer().getInventory().getHelmet())
                        || newArmorType.equals(ArmorType.CHESTPLATE) && isAirOrNull(event.getPlayer().getInventory().getChestplate())
                        || newArmorType.equals(ArmorType.LEGGINGS) && isAirOrNull(event.getPlayer().getInventory().getLeggings())
                        || newArmorType.equals(ArmorType.BOOTS) && isAirOrNull(event.getPlayer().getInventory().getBoots())
                  )) {
                  ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(
                     event.getPlayer(), ArmorEquipEvent.EquipMethod.HOTBAR, ArmorType.matchType(event.getItem()), null, event.getItem()
                  );
                  Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                  if (armorEquipEvent.isCancelled()) {
                     event.setCancelled(true);
                     player.updateInventory();
                  }
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void onDrag(InventoryDragEvent event) {
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
   public void onBreak(PlayerItemBreakEvent event) {
      ArmorType type = ArmorType.matchType(event.getBrokenItem());
      if (type != null) {
         Player p = event.getPlayer();
         ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.BROKE, type, event.getBrokenItem(), null);
         Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
         if (armorEquipEvent.isCancelled()) {
            ItemStack i = event.getBrokenItem().clone();
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
   public void onDeath(PlayerDeathEvent event) {
      Player p = event.getEntity();
      if (!event.getKeepInventory()) {
         for (ItemStack i : p.getInventory().getArmorContents()) {
            if (!isAirOrNull(i)) {
               Bukkit.getServer().getPluginManager().callEvent(new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.DEATH, ArmorType.matchType(i), i, null));
            }
         }
      }
   }

   public static boolean isAirOrNull(ItemStack item) {
      return item == null || item.getType().equals(Material.AIR);
   }
}
