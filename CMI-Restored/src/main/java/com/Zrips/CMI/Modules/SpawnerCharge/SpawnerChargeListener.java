package com.Zrips.CMI.Modules.SpawnerCharge;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Entities.CMIEntity;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class SpawnerChargeListener implements Listener {
  private CMI plugin;
  Random rand = new Random();
  static HashMap<UUID, Block> breaking = new HashMap<>();

  public SpawnerChargeListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void BlockBreakEventExpDrop(BlockBreakEvent blockBreakEvent) {
    if (!blockBreakEvent.isCancelled()) {
      if (CMIMaterial.SPAWNER.equals(blockBreakEvent.getBlock().getType())) {
        if (!this.plugin.getSpawnerChargesManager().isSpawnersSupportDisabled()) {
          if (!this.plugin.getSpawnerChargesManager().isSpawnersXpDrop()) {
            blockBreakEvent.setExpToDrop(0);
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void BlockBreakEventFull(BlockBreakEvent blockBreakEvent) {
    if (!blockBreakEvent.isCancelled()) {
      if (CMIMaterial.SPAWNER.equals(blockBreakEvent.getBlock().getType())) {
        if (!this.plugin.getSpawnerChargesManager().isSpawnersSupportDisabled()) {
          breaking.remove(blockBreakEvent.getPlayer().getUniqueId());
          Player player = blockBreakEvent.getPlayer();
          if (this.plugin.getSpawnerChargesManager().isChargesUse()) {
            CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
            if (cmiUser == null) {
              return;
            }

            boolean flag = cmiUser.getPCharge().have();
            if (!PermissionsManager.CMIPerm.spawners_charge_bypass.hasPermission(player)) {
              if (!flag) {
                player.sendMessage(
                    this.plugin.getLM().getMessage("command.charges.info.cantBreak"));
                if (!this.plugin.getSpawnerChargesManager().isChargesBreakWithoutCharge()) {
                  blockBreakEvent.setCancelled(true);
                  return;
                }
              } else if (!cmiUser.getPCharge().takeCharge()) {
                player.sendMessage(
                    this.plugin
                        .getLM()
                        .getMessage(
                            "command.charges.info.allUsed",
                            "[time]",
                            cmiUser.getPCharge().getLeftTime()));
                if (!this.plugin.getSpawnerChargesManager().isChargesBreakWithoutCharge()) {
                  blockBreakEvent.setCancelled(true);
                  return;
                }

                return;
              }
            }
          }

          if (this.plugin.getSpawnerChargesManager().isSpawnersBreakEnabled()) {
            double number =
                this.plugin
                    .getSpawnerChargesManager()
                    .getDropChance(player, blockBreakEvent.getBlock());
            if (!(number <= 0.0) && !(this.rand.nextDouble() * 100.0 > number)) {
              breaking.put(player.getUniqueId(), blockBreakEvent.getBlock());
              blockBreakEvent.setExpToDrop(0);
            } else {
              if (number >= 0.0) {
                CMILC.info_FailedSpawnerMine.sendMessage(
                    player, "[percent]", CMINumber.format2Decimals(number));
              }

              blockBreakEvent.setExpToDrop(0);
            }
          }
        }
      }
    }
  }

  public static void clearCache(UUID uuid) {
    breaking.remove(uuid);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void blockBreakEventFull(BlockBreakEvent blockBreakEvent) {
    if (!this.plugin.getSpawnerChargesManager().isSpawnersSupportDisabled()) {
      if (!breaking.isEmpty()) {
        Block block = breaking.remove(blockBreakEvent.getPlayer().getUniqueId());
        if (!blockBreakEvent.isCancelled()) {
          if (block != null) {
            if (blockBreakEvent.getBlock().equals(block)) {
              ItemStack itemStack = CMIMaterial.SPAWNER.newItemStack();
              BlockState blockState = blockBreakEvent.getBlock().getState();
              if (blockState instanceof CreatureSpawner) {
                CreatureSpawner creatureSpawner = (CreatureSpawner) blockState;
                if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
                  ItemMeta itemMeta = itemStack.getItemMeta();
                  if (itemMeta instanceof BlockStateMeta) {
                    BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
                    if (blockStateMeta.getBlockState() instanceof CreatureSpawner) {
                      CreatureSpawner spawnerCopy =
                          (CreatureSpawner) blockStateMeta.getBlockState();
                      spawnerCopy.setSpawnedType(creatureSpawner.getSpawnedType());
                      blockStateMeta.setBlockState(spawnerCopy);
                      itemStack.setItemMeta(blockStateMeta);
                    }
                  }

                  if (this.plugin.getSpawnerChargesManager().isSpawnersCustomName()) {
                    itemStack =
                        CMIEntity.setEntityType(itemStack, creatureSpawner.getSpawnedType());
                  }

                  if (this.plugin.getSpawnerChargesManager().isSpawnersIntoInventory()) {
                    CMIUser.getUser(blockBreakEvent.getPlayer())
                        .getInventory()
                        .addItem(itemStack, CMIPlayerInventory.CMIFullInventoryAction.Drop);
                  } else {
                    blockBreakEvent
                        .getBlock()
                        .getWorld()
                        .dropItemNaturally(blockBreakEvent.getBlock().getLocation(), itemStack);
                  }
                } else {
                  if (Version.isCurrentEqualOrHigher(Version.v1_19_R2)) {
                    CMINBT cminbt = new CMINBT(block);
                    if (cminbt.hasNBT("SpawnData.entity.id")
                        && this.plugin.getSpawnerChargesManager().isSpawnersCustomName()) {
                      itemStack =
                          CMIEntity.setEntityType(itemStack, creatureSpawner.getSpawnedType());
                    }
                  } else if (this.plugin.getSpawnerChargesManager().isSpawnersCustomName()) {
                    itemStack =
                        CMIEntity.setEntityType(itemStack, creatureSpawner.getSpawnedType());
                  }

                  if (this.plugin.getSpawnerChargesManager().isSpawnersIntoInventory()) {
                    CMIUser.getUser(blockBreakEvent.getPlayer())
                        .getInventory()
                        .addItem(itemStack, CMIPlayerInventory.CMIFullInventoryAction.Drop);
                  } else {
                    blockBreakEvent
                        .getBlock()
                        .getWorld()
                        .dropItemNaturally(blockBreakEvent.getBlock().getLocation(), itemStack);
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
  public void BlockPlaceEvent(BlockPlaceEvent blockPlaceEvent) {
    if (!blockPlaceEvent.isCancelled()) {
      if (CMIMaterial.SPAWNER.equals(blockPlaceEvent.getBlock().getType())) {
        if (this.plugin.getSpawnerChargesManager().isChargesUse()) {
          if (!this.plugin.getSpawnerChargesManager().isSpawnersSupportDisabled()) {
            Player player = blockPlaceEvent.getPlayer();
            CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
            if (cmiUser != null) {
              if (cmiUser.getPCharge().have() && cmiUser.getPCharge().lowerCd()) {
                CMIActionBar.send(
                    player,
                    this.plugin
                        .getLM()
                        .getMessage(
                            "command.charges.info.minusCd",
                            "[sec]",
                            cmiUser.getPCharge().getBonus()));
              }
            }
          }
        }
      }
    }
  }
}
