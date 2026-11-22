package com.Zrips.CMI.Modules.InteractiveCommand;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIInteractType;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Alias.AliasManager;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.list.ic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class InteractiveCommandListener implements Listener {
  private CMI plugin;
  private static int MAX_ENTRIES = 50;
  public static LinkedHashMap<UUID, Long> limit =
      new LinkedHashMap<UUID, Long>(MAX_ENTRIES + 1, 0.75F, false) {
        @Override
        protected boolean removeEldestEntry(Entry<UUID, Long> entry) {
          return this.size() > InteractiveCommandListener.MAX_ENTRIES;
        }
      };

  public InteractiveCommandListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void PlayerInteractCancelableEvent(PlayerInteractEvent playerInteractEvent) {
    CMIInteractiveCommand cmiInteractiveCommand = this.getCommand(playerInteractEvent);
    if (cmiInteractiveCommand != null) {
      if (cmiInteractiveCommand.isCancelInteraction()) {
        playerInteractEvent.setCancelled(true);
      }
    }
  }

  private CMIInteractiveCommand getCommand(PlayerInteractEvent playerInteractEvent) {
    Block block = playerInteractEvent.getClickedBlock();
    Action action = playerInteractEvent.getAction();
    if (action != Action.PHYSICAL
        && action != Action.RIGHT_CLICK_BLOCK
        && action != Action.LEFT_CLICK_BLOCK) {
      return null;
    }

    try {
      if (action != Action.PHYSICAL && playerInteractEvent.getHand() != EquipmentSlot.HAND) {
        return null;
      }
    } catch (Exception | NoSuchMethodError value) {
    }

    return this.plugin.getInteractiveCommandManager().getByLoc(block.getLocation());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void PlayerInteractEvent(PlayerInteractEvent playerInteractEvent) {
    CMIInteractiveCommand cmiInteractiveCommand = this.getCommand(playerInteractEvent);
    if (cmiInteractiveCommand != null) {
      Long lastUse = limit.get(playerInteractEvent.getPlayer().getUniqueId());
      if (lastUse == null || lastUse + 100L <= System.currentTimeMillis()) {
        limit.put(playerInteractEvent.getPlayer().getUniqueId(), System.currentTimeMillis());
        Player player = playerInteractEvent.getPlayer();
        Block block = playerInteractEvent.getClickedBlock();
        CMIScheduler.runAtLocation(
            this.plugin,
            block.getLocation(),
            () -> {
              var list = cmiInteractiveCommand.getCommands(player);
              if (block.getState() instanceof Sign) {
                Sign sign = (Sign) block.getState();
                ArrayList<String> arrayList = new ArrayList();
                arrayList.add("");

                for (int value = 1; value < 4; value++) {
                  if (!sign.getLine(value).isEmpty()) {
                    arrayList.addAll(Arrays.asList(sign.getLine(value).split(" ")));
                  }
                }

                for (String string : list) {
                  Matcher matcher = AliasManager.patern.matcher(string);
                  if (matcher.find()) {
                    list =
                        CMI.getInstance().getAliasManager().updateCommands(null, arrayList, list);
                    break;
                  }
                }
              }

              for (int index = 0; index < list.size(); index++) {
                String command = (String) list.get(index);
                command = command.replace("[interactedBlock]", CMIMaterial.get(block).toString());
                command =
                    command.replace(
                        "[interactedBlockX]", String.valueOf(block.getLocation().getBlockX()));
                command =
                    command.replace(
                        "[interactedBlockY]", String.valueOf(block.getLocation().getBlockY()));
                command =
                    command.replace(
                        "[interactedBlockZ]", String.valueOf(block.getLocation().getBlockZ()));
                list.set(index, command);
              }

              CMIInteractType cmiInteractType = CMIInteractType.LeftClick;
              switch (playerInteractEvent.getAction()) {
                case LEFT_CLICK_BLOCK:
                  if (player.isSneaking()) {
                    cmiInteractType = CMIInteractType.LeftShiftClick;
                  } else {
                    cmiInteractType = CMIInteractType.LeftClick;
                  }
                  break;
                case RIGHT_CLICK_BLOCK:
                  if (player.isSneaking()) {
                    cmiInteractType = CMIInteractType.RightShiftClick;
                  } else {
                    cmiInteractType = CMIInteractType.RightClick;
                  }
              }

              this.plugin
                  .getSpecializedCommandManager()
                  .processCmds(
                      "CMIIC-" + cmiInteractiveCommand.getName(), list, player, cmiInteractType);
              cmiInteractiveCommand.updateSignText(block);
            });
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void blockBreak(BlockPhysicsEvent blockPhysicsEvent) {
    if (!blockPhysicsEvent.isCancelled()
        && !blockPhysicsEvent.getBlock().getType().equals(Material.AIR)) {
      CMIInteractiveCommand cmiInteractiveCommand =
          this.plugin
              .getInteractiveCommandManager()
              .getByLoc(blockPhysicsEvent.getBlock().getLocation());
      if (cmiInteractiveCommand != null) {
        if (blockPhysicsEvent.getSourceBlock().getType().equals(Material.AIR)) {
          Material material = blockPhysicsEvent.getBlock().getType();
          CMIScheduler.runAtLocation(
              this.plugin,
              blockPhysicsEvent.getBlock().getLocation(),
              () -> {
                if (!material.equals(
                    blockPhysicsEvent.getBlock().getLocation().getBlock().getType())) {
                  this.plugin
                      .getInteractiveCommandManager()
                      .removeLoc(new CMILocation(blockPhysicsEvent.getBlock().getLocation()));
                }
              });
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void blockBreak(BlockBreakEvent blockBreakEvent) {
    if (!blockBreakEvent.isCancelled()) {
      CMIInteractiveCommand cmiInteractiveCommand =
          this.plugin
              .getInteractiveCommandManager()
              .getByLoc(blockBreakEvent.getBlock().getLocation());
      if (cmiInteractiveCommand != null) {
        this.plugin
            .getInteractiveCommandManager()
            .removeLoc(new CMILocation(blockBreakEvent.getBlock().getLocation()));
      }
    }
  }

  @EventHandler
  public void onSignChangeEvent(SignChangeEvent signChangeEvent) {
    if (!signChangeEvent.isCancelled()) {
      Player player = signChangeEvent.getPlayer();
      if (player != null) {
        String[] strings = signChangeEvent.getLines();
        String string = strings[0];
        if (!string.isEmpty()) {
          Matcher matcher = Config.InteractiveCommandsSignRegex.getMatcher(string);
          if (matcher != null) {
            while (matcher.find()) {
              if (matcher.groupCount() >= 2) {
                String commandName = matcher.group(2);
                CMIInteractiveCommand cmiInteractiveCommand =
                    this.plugin.getInteractiveCommandManager().getByName(commandName);
                if (cmiInteractiveCommand == null) {
                  CMILC.info(ic.class, player, "dontExist");
                  return;
                }

                if (!cmiInteractiveCommand.isPublic()) {
                  CMILC.info(ic.class, player, "notPublicIc");
                  return;
                }

                if (cmiInteractiveCommand.isSignRequiresPerm()
                    && !PermissionsManager.CMIPerm.interactivesign_$1.hasPermission(
                        player, true, cmiInteractiveCommand.getName())) {
                  signChangeEvent.setCancelled(true);
                  return;
                }

                cmiInteractiveCommand.addLoc(
                    new CMILocation(signChangeEvent.getBlock().getLocation()));
                cmiInteractiveCommand.updateSignText(signChangeEvent.getBlock());
                this.plugin
                    .getInteractiveCommandManager()
                    .addInteractiveCommand(cmiInteractiveCommand);
                CMILC.info(
                    ic.class, player, "createdPublic", "[name]", cmiInteractiveCommand.getName());
              }
            }
          }
        }
      }
    }
  }
}
