package com.Zrips.CMI.Modules.SpawnerCharge;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.util.List;
import java.util.Random;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class SpawnerActionsListener implements Listener {
  private CMI plugin;

  public SpawnerActionsListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockBreakExplodeTnT(EntityExplodeEvent entityExplodeEvent) {
    if (!entityExplodeEvent.isCancelled()
        && entityExplodeEvent.getEntity() instanceof TNTPrimed
        && this.plugin.getSpawnerChargesManager().isSpawnerDropFromTnt()
        && !this.plugin.getSpawnerChargesManager().isSpawnersSupportDisabled()) {
      int value = this.plugin.getSpawnerChargesManager().getSpawnerDropChanceTnt();
      this.dropSpawner(value, entityExplodeEvent.blockList());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerInteractEvent(PlayerInteractEvent playerInteractEvent) {
    if (!playerInteractEvent.isCancelled()) {
      Block block = playerInteractEvent.getClickedBlock();
      if (block != null
          && CMIMaterial.SPAWNER.equals(CMIMaterial.get(block.getType()))
          && !this.plugin.getSpawnerChargesManager().isSpawnersSupportDisabled()) {
        Player player = playerInteractEvent.getPlayer();
        if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) {
          try {
            if (Version.isCurrentHigher(Version.v1_8_R3)
                && playerInteractEvent.getHand() != EquipmentSlot.HAND) {
              return;
            }
          } catch (Exception exception) {
          }

          if (!player.isSneaking()) {
            if (this.plugin.getSpawnerChargesManager().isSpawnersBreakEnabled()) {
              ItemStack itemStack =
                  CMI.getInstance().getPlayerManager().getUser(player).getItemInHand();
              if (CMIMaterial.isPickaxe(itemStack.getType())) {
                CMILC.info_ClickSpawner.sendMessage(
                    player,
                    "[percent]",
                    this.plugin.getSpawnerChargesManager().getSpawnerDropChance(player, block));
              }
            }
          } else if (PermissionsManager.CMIPerm.command_spawner_shiftclick.hasPermission(
              player, true, false)) {
            this.plugin.getSpawnerChargesManager().openSpawnerPickerUI(player, 1);
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockBreakExplodeCreeper(EntityExplodeEvent entityExplodeEvent) {
    if (!entityExplodeEvent.isCancelled()
        && entityExplodeEvent.getEntity() instanceof Creeper
        && this.plugin.getSpawnerChargesManager().isSpawnerDropFromCreeper()
        && !this.plugin.getSpawnerChargesManager().isSpawnersSupportDisabled()) {
      int value = this.plugin.getSpawnerChargesManager().getSpawnerDropChanceCreeper();
      this.dropSpawner(value, entityExplodeEvent.blockList());
    }
  }

  private void dropSpawner(int value, List<Block> list) {
    Random random = new Random(System.nanoTime());

    for (Block block : list) {
      if (CMIMaterial.SPAWNER.equals(block.getType())) {
        int roll = random.nextInt(100);
        if (roll <= value) {
          ItemStack itemStack = CMIMaterial.SPAWNER.newItemStack();
          BlockState blockState = block.getState();
          CreatureSpawner creatureSpawner = (CreatureSpawner) blockState;
          itemStack =
              this.plugin
                  .getUtilManager()
                  .setEntityType(itemStack, creatureSpawner.getSpawnedType());
          block.setType(Material.AIR);
          block.getWorld().dropItemNaturally(block.getLocation(), itemStack);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onBlockPlace(BlockPlaceEvent blockPlaceEvent) {
    if (this.plugin.getSpawnerChargesManager().isSpawnersPlaceEnabled()
        && !blockPlaceEvent.isCancelled()
        && CMIMaterial.SPAWNER.equals(blockPlaceEvent.getBlock().getType())
        && !this.plugin.getSpawnerChargesManager().isSpawnersSupportDisabled()) {
      Player player = blockPlaceEvent.getPlayer();
      ItemStack itemStack = blockPlaceEvent.getItemInHand();
      if (itemStack != null) {
        BlockState blockState = blockPlaceEvent.getBlock().getState();
        CreatureSpawner creatureSpawner = (CreatureSpawner) blockState;
        boolean flag = false;
        EntityType entityType = null;
        if (Version.isCurrentEqualOrHigher(Version.v1_19_R2)
            && Version.isCurrentEqualOrLower(Version.v1_20_R3)) {
          CMINBT cminbt = new CMINBT(itemStack);
          flag = !cminbt.hasNBT("BlockEntityTag");
        }

        if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
          ItemMeta itemMeta = itemStack.getItemMeta();
          if (itemMeta instanceof BlockStateMeta) {
            BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
            if (blockStateMeta.getBlockState() instanceof CreatureSpawner) {
              CreatureSpawner spawner = (CreatureSpawner) blockStateMeta.getBlockState();
              entityType = spawner.getSpawnedType();
            }
          }
        } else {
          entityType = new CMIItemStack(itemStack).getEntityType();
        }

        if (entityType != null || flag) {
          if (this.plugin.getSpawnerChargesManager().isSpawnersPlaceRequiresPermission()) {
            if (!this.plugin.getSpawnerChargesManager().isSpawnersPlaceRequiresExactPermission()) {
              if (!PermissionsManager.CMIPerm.placespawner.hasPermission(player, true)) {
                blockPlaceEvent.setCancelled(true);
                return;
              }
            } else if (entityType != null
                && !PermissionsManager.CMIPerm.placespawner_$1.hasPermission(
                    player, entityType.name().toLowerCase())) {
              blockPlaceEvent.setCancelled(true);
              return;
            }
          }

          if (!flag) {
            creatureSpawner.setSpawnedType(entityType);
            creatureSpawner.update();
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onTrialSpawnerInteract(PlayerInteractEvent playerInteractEvent) {
    this.processEvent(playerInteractEvent, CMIMaterial.SPAWNER);
    if (Version.isCurrentEqualOrHigher(Version.v1_21_R1)) {
      this.processEvent(playerInteractEvent, CMIMaterial.TRIAL_SPAWNER);
    }
  }

  private void processEvent(PlayerInteractEvent playerInteractEvent, CMIMaterial cmiMaterial) {
    if (!playerInteractEvent.isCancelled()
        && playerInteractEvent.getClickedBlock() != null
        && cmiMaterial.getMaterial().equals(playerInteractEvent.getClickedBlock().getType())
        && !this.plugin.getSpawnerChargesManager().isSpawnersSupportDisabled()) {
      Player player = playerInteractEvent.getPlayer();
      ItemStack itemStack = playerInteractEvent.getItem();
      if (itemStack != null && itemStack.getType() != Material.AIR) {
        if (CMIMaterial.isSpawnEgg(itemStack.getType())) {
          if ((!cmiMaterial.equals(Material.SPAWNER)
                  || this.plugin
                      .getSpawnerChargesManager()
                      .isSpawnersEggInteractRequiresPermission())
              && (!Version.isCurrentEqualOrHigher(Version.v1_20_R1)
                  || !CMIMaterial.get(cmiMaterial.getMaterial()).equals(CMIMaterial.TRIAL_SPAWNER)
                  || this.plugin
                      .getSpawnerChargesManager()
                      .isTrialSpawnersEggInteractRequiresPermission())) {
            EntityType entityType = new CMIItemStack(itemStack).getEntityType();
            if (entityType != null) {
              if (cmiMaterial.equals(Material.SPAWNER)
                      && !PermissionsManager.CMIPerm.egginteract_$1.hasPermission(
                          player, true, entityType.name().toLowerCase())
                  || Version.isCurrentEqualOrHigher(Version.v1_20_R1)
                      && CMIMaterial.get(cmiMaterial.getMaterial())
                          .equals(CMIMaterial.TRIAL_SPAWNER)
                      && !PermissionsManager.CMIPerm.trialegginteract_$1.hasPermission(
                          player, true, entityType.name().toLowerCase())) {
                playerInteractEvent.setCancelled(true);
              }
            }
          }
        }
      }
    }
  }
}
