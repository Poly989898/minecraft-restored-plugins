package io.lumine.mythic.bukkit.listeners;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMobEggEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.WorldScaling;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EggListeners extends ReloadableModule<MythicBukkit> {
   public EggListeners(MythicBukkit plugin) {
      super(plugin);
   }

   public void load(MythicBukkit plugin) {
      Events.subscribe(PlayerInteractEvent.class, EventPriority.HIGHEST).handler(this::onEggInteract).bindWith(this);
      Events.subscribe(BlockDispenseEvent.class, EventPriority.HIGH).handler(this::onEggDispense).bindWith(this);
   }

   @Override
   public void unload() {
   }

   public void onEggInteract(PlayerInteractEvent e) {
      if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
         if (e.getHand() == EquipmentSlot.HAND) {
            if (e.getItem() != null) {
               if (e.useItemInHand() != Result.DENY) {
                  if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.useInteractedBlock() != Result.DENY) {
                     Player player = e.getPlayer();
                     ItemStack itemStack = e.getItem();
                     CompoundTag compTag = this.getPlugin().getVolatileCodeHandler().getItemHandler().getNBTData(itemStack);
                     if (compTag.containsKey("MYTHIC_EGG")) {
                        e.setCancelled(true);
                        String internalName = compTag.getString("MYTHIC_EGG");
                        MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "Detected a Mythic Egg usage for {0}", internalName);
                        MythicMob mm = this.getPlugin().getMobManager().getMythicMob(internalName).orElseGet(() -> null);
                        if (mm != null) {
                           if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                              AbstractLocation location = BukkitAdapter.adapt(e.getClickedBlock().getLocation()).clone().add(0.5, 1.0, 0.5);
                              MythicMobEggEvent event = Events.callAndReturn(new MythicMobEggEvent(player, mm, location));
                              if (event.isCancelled()) {
                                 return;
                              }

                              mm.spawn(location, WorldScaling.getLevelBonus(location));
                              MythicLogger.debug(MythicLogger.DebugLevel.INFO, "+ Mythic Mob {0} was spawned from an egg.", mm.getInternalName());
                           } else if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                              ItemStack stack = itemStack.clone();
                              stack.setAmount(1);
                              Item entity = player.getWorld().dropItem(player.getLocation().clone().add(0.0, 1.5, 0.0), stack);
                              entity.setThrower(player.getUniqueId());
                              entity.setCanMobPickup(false);
                              entity.setCanPlayerPickup(false);
                              entity.setVelocity(player.getLocation().getDirection().multiply(1));
                              Schedulers.of(player).runLater(() -> {
                                 Location location = entity.getLocation();
                                 AbstractLocation aLocation = BukkitAdapter.adapt(entity.getLocation());
                                 MythicMobEggEvent event = Events.callAndReturn(new MythicMobEggEvent(player, mm, aLocation));
                                 entity.remove();
                                 if (!event.isCancelled()) {
                                    mm.spawn(aLocation, 1.0);
                                    MythicLogger.debug(MythicLogger.DebugLevel.INFO, "+ Mythic Mob {0} was spawned from an egg.", mm.getInternalName());
                                 }
                              }, 25L);
                           }

                           if (itemStack.getAmount() > 1) {
                              itemStack.setAmount(itemStack.getAmount() - 1);
                           } else {
                              this.getPlugin().getVolatileCodeHandler().getItemHandler().destroyItem(itemStack);
                              player.updateInventory();
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void onEggDispense(BlockDispenseEvent e) {
      if (e.getBlock().getType() != Material.DROPPER) {
         if (e.getItem().getType() == Material.PIG_SPAWN_EGG) {
            ItemStack itemStack = e.getItem();
            CompoundTag compTag = this.getPlugin().getVolatileCodeHandler().getItemHandler().getNBTData(itemStack);
            if (compTag.containsKey("MYTHIC_EGG")) {
               String internalName = compTag.getString("MYTHIC_EGG");
               MythicLogger.debug(MythicLogger.DebugLevel.EVENT, "Detected a Mythic Egg usage for {0}", internalName);
               MythicMob mm = this.getPlugin().getMobManager().getMythicMob(internalName).orElseGet(() -> null);
               if (mm != null) {
                  Block block = e.getBlock();
                  BlockFace blockFace = ((Directional)block.getBlockData()).getFacing();
                  AbstractLocation location = BukkitAdapter.adapt(block.getLocation());
                  switch (blockFace) {
                     case DOWN:
                        location.add(0.5, -1.95, 0.5);
                        break;
                     case UP:
                        location.add(0.5, 1.0, 0.5);
                        break;
                     case EAST:
                        location.add(1.5, 0.0, 0.5);
                        break;
                     case WEST:
                        location.add(-0.5, 0.0, 0.5);
                        break;
                     case NORTH:
                        location.add(0.5, 0.0, -0.5);
                        break;
                     case SOUTH:
                        location.add(0.5, 0.0, 1.5);
                        break;
                     default:
                        location.add(0.5, 1.0, 0.5);
                  }

                  mm.spawn(location, 1.0);
                  MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Mythic Mob {0} was spawned from a dispensed egg.", mm.getInternalName());
                  e.setCancelled(true);
                  Dispenser dispenser = (Dispenser)block.getState();
                  Inventory dispenserInventory = dispenser.getInventory();
                  Schedulers.of(e.getBlock().getLocation())
                     .runLater(() -> dispenserInventory.removeItem(new ItemStack[]{ItemFactory.of(itemStack).amount(1).build()}), 1L);
               }
            }
         }
      }
   }
}
