package com.Zrips.CMI.Modules.Signs;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIColorTypes;
import com.Zrips.CMI.Containers.CMIEvent;
import com.Zrips.CMI.Containers.CMITextCleaner;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.DynamicSigns.CMISign;
import com.Zrips.CMI.Modules.Hooks.CMIHookType;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.commands.list.dsign;
import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerSignEditListeners implements Listener {
  private CMI plugin;
  Set<UUID> signEditCache = new HashSet<>();

  public PlayerSignEditListeners(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onSignChangeEvent(SignChangeEvent signChangeEvent) {
    if (!signChangeEvent.isCancelled()) {
      Player player = signChangeEvent.getPlayer();
      if (player != null) {
        String[] strings = signChangeEvent.getLines();

        for (int value = 0; value < strings.length; value++) {
          String string = strings[value];
          string = CMITextCleaner.cleanFromColorCodes(player, string, CMIColorTypes.signs, true);
          string = string.replace("＆", "&");
          signChangeEvent.setLine(value, string);
        }

        if (signChangeEvent.getBlock().getState() instanceof Sign) {
          try {
            Sign sign = (Sign) signChangeEvent.getBlock().getState();

            for (int lineIndex = 0; lineIndex < 4; lineIndex++) {
              if (!sign.getLine(lineIndex).equals(signChangeEvent.getLines()[lineIndex])) {
                if (this.signEditCache.remove(player.getUniqueId())) {
                  disableNCP(player, false);
                }
                break;
              }
            }
          } catch (Throwable throwable) {
            throwable.printStackTrace();
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onSignInteractShift(PlayerInteractEvent playerInteractEvent) {
    if (!Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
      Player player = playerInteractEvent.getPlayer();
      if (player.isSneaking() && playerInteractEvent.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
        Block block = playerInteractEvent.getClickedBlock();
        if (block.getState() instanceof Sign) {
          CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
          if (cmiUser != null) {
            if (cmiUser.getOptionState(PlayerOption.shiftSignEdit)) {
              if (PermissionsManager.CMIPerm.signs_shiftedit.hasPermission(player)) {
                if (cmiUser.getItemInHand() == null
                    || !CMIMaterial.isSign(cmiUser.getItemInHand().getType())) {
                  Sign sign = (Sign) block.getState();
                  if (!this.plugin
                      .getConfigManager()
                      .getElevatorIndicators()
                      .contains(sign.getLine(0).toLowerCase())) {
                    if (this.plugin
                        .getSignManager()
                        .getSignEditBlackList()
                        .contains(CMIChatColor.stripColor(sign.getLine(0).toLowerCase()))) {
                      LC.info_cantEdit.sendMessage(player, new Object[0]);
                    } else if (Version.isCurrentEqual(Version.v1_16_R1)) {
                      LC.info_versionNotSupported.sendMessage(player, new Object[0]);
                    } else {
                      boolean flag = false;
                      if (CMIHookType.NoCheatPlus.isEnabled()) {
                        if (NCPExemptionManager.isExempted(player, CheckType.BLOCKPLACE_AUTOSIGN)) {
                          flag = true;
                        }

                        if (!flag) {
                          this.signEditCache.add(player.getUniqueId());
                          NCPExemptionManager.exemptPermanently(
                              player, CheckType.BLOCKPLACE_AUTOSIGN);
                        }
                      }

                      if (!PermissionsManager.CMIPerm.signs_shifteditbypass.hasPermission(player)) {
                        BlockPlaceEvent blockPlaceEvent =
                            CMIEvent.placeSignEvent(block, sign, player);
                        Bukkit.getServer().getPluginManager().callEvent(blockPlaceEvent);
                        if (blockPlaceEvent.isCancelled()) {
                          disableNCP(player, flag);
                          return;
                        }

                        String[] strings = sign.getLines();
                        SignChangeEvent signChangeEvent =
                            new SignChangeEvent(block, player, strings);
                        Bukkit.getServer().getPluginManager().callEvent(signChangeEvent);
                        if (signChangeEvent.isCancelled()) {
                          disableNCP(player, flag);
                          return;
                        }
                      }

                      CMISign cmiSign = this.plugin.getSignManager().getByLoc(block.getLocation());
                      if (cmiSign != null
                          && PermissionsManager.CMIPerm.command_$1.hasPermission(player, "dsign")) {
                        disableNCP(player, flag);
                        this.plugin.performCommand(
                            player,
                            CommandsHandler.getLabel()
                                + " "
                                + dsign.class.getSimpleName()
                                + " "
                                + this.plugin
                                    .getPlayerManager()
                                    .convertLocToString(block.getLocation()));
                        playerInteractEvent.setCancelled(true);
                      } else {
                        try {
                          if (Version.isCurrentHigher(Version.v1_8_R3)
                              && playerInteractEvent.getHand() != EquipmentSlot.HAND) {
                            disableNCP(player, flag);
                            return;
                          }
                        } catch (Exception exception) {
                        }

                        if (this.plugin.getReflectionManager().openSignUI(player, sign)) {
                          playerInteractEvent.setCancelled(true);
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
  }

  private static void disableNCP(Player player, boolean flag) {
    if (CMIHookType.NoCheatPlus.isEnabled() && !flag) {
      NCPExemptionManager.unexempt(player, CheckType.BLOCKPLACE_AUTOSIGN);
    }
  }
}
