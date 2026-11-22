package com.Zrips.CMI.Modules.AttachedCommands;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIEquipmentSlot;
import com.Zrips.CMI.Containers.CMIInteractType;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.list.attachcommand;
import com.Zrips.CMI.commands.list.util;
import com.Zrips.CMI.utils.CMIEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class CustomNBTListener implements Listener {
  private CMI plugin;
  private static HashMap<UUID, Long> timer = new HashMap<>();
  private static HashMap<UUID, HashMap<Integer, itemCooldown>> cooldownMap = new HashMap<>();

  public CustomNBTListener(CMI cmi) {
    this.plugin = cmi;
  }

  public static void clearCache(UUID uuid) {
    timer.remove(uuid);
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void playerInteractCommand(PlayerInteractEvent playerInteractEvent) {
    if (processInteract(
        playerInteractEvent.getPlayer(),
        null,
        CMIEquipmentSlot.get(playerInteractEvent),
        playerInteractEvent.getAction(),
        playerInteractEvent.getClickedBlock(),
        null,
        false)) {
      playerInteractEvent.setCancelled(true);
    }
  }

  private static boolean isOnCooldown(Player player, boolean flag, CMINBT cminbt) {
    var list = cminbt.getList(CustomNBTManager.CustomNBTType.ItemCooldown.name());
    if (list == null) {
      return false;
    }

    var hashMap = cooldownMap.get(player.getUniqueId());
    if (hashMap == null) {
      hashMap = new HashMap();
      cooldownMap.put(player.getUniqueId(), hashMap);
    }

    int value = 0;
    Integer integer = 0;

    for (String string : list) {
      if (string.contains(":")) {
        String[] strings = string.split(":");
        if (strings.length <= 2
            || (flag || !strings[2].equalsIgnoreCase("!left!"))
                && (!flag || !strings[2].equalsIgnoreCase("!right!"))) {
          try {
            integer = Integer.parseInt(strings[0]);
          } catch (Exception exception) {
            continue;
          }

          try {
            value = Integer.parseInt(strings[1]);
          } catch (Exception exception2) {
          }
        }
      }
    }

    if (value != 0 && integer > 0) {
      itemCooldown itemCooldown = (itemCooldown) hashMap.get(value);
      if (itemCooldown == null) {
        itemCooldown = new itemCooldown();
        itemCooldown.setId(value);
        itemCooldown.setClickedOn(System.currentTimeMillis() - integer.intValue() * 1000L);
        hashMap.put(value, itemCooldown);
      }

      if (itemCooldown.getClickedOn() + integer.intValue() * 1000L > System.currentTimeMillis()) {
        CMILC.info(
            attachcommand.class,
            player,
            "cooldown",
            "[time]",
            CMITimeManager.to24hourShort(
                itemCooldown.getClickedOn()
                    + integer.intValue() * 1000L
                    + 1000L
                    - System.currentTimeMillis()));
        return true;
      }

      itemCooldown.setClickedOn(System.currentTimeMillis());
    }

    return false;
  }

  static boolean processInteract(
      Player player,
      Player target,
      CMIEquipmentSlot cmiEquipmentSlot,
      Action action,
      Block block,
      Entity entity,
      boolean flag) {
    cmiEquipmentSlot = cmiEquipmentSlot == null ? CMIEquipmentSlot.HAND : cmiEquipmentSlot;
    if (cmiEquipmentSlot != CMIEquipmentSlot.HAND
        && cmiEquipmentSlot != CMIEquipmentSlot.OFF_HAND) {
      return false;
    }

    Long lastUse = timer.get(player.getUniqueId());
    if (lastUse != null && lastUse + 100L > System.currentTimeMillis()) {
      return true;
    }

    boolean cancel = false;
    boolean leftClick = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
    ItemStack itemStack = null;
    if (cmiEquipmentSlot == CMIEquipmentSlot.HAND) {
      itemStack = CMIItemStack.getItemInMainHand(player);
    } else {
      itemStack = CMIItemStack.getItemInOffHand(player);
    }

    if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
      CMINBT cminbt = new CMINBT(itemStack);
      if (!flag && containsConsume(itemStack)) {
        return false;
      }

      Integer integer = cminbt.getInt(CustomNBTManager.CustomNBTType.CooldownId.name());
      if (integer != null) {
        var hashMap = cooldownMap.get(player.getUniqueId());
        if (hashMap == null) {
          hashMap = new HashMap();
          cooldownMap.put(player.getUniqueId(), hashMap);
        }

        itemCooldown itemCooldown = (itemCooldown) hashMap.get(integer);
        Integer cooldown = cminbt.getInt(CustomNBTManager.CustomNBTType.Cooldown.name());
        if (itemCooldown == null) {
          itemCooldown = new itemCooldown();
          itemCooldown.setId(integer);
          itemCooldown.setClickedOn(
              cooldown != null
                  ? System.currentTimeMillis() - cooldown.intValue() * 1000L
                  : System.currentTimeMillis());
          hashMap.put(integer, itemCooldown);
        }

        if (cooldown != null) {
          if (itemCooldown.getClickedOn() + cooldown.intValue() * 1000L
              > System.currentTimeMillis()) {
            CMILC.info(
                attachcommand.class,
                player,
                "cooldown",
                "[time]",
                CMITimeManager.to24hourShort(
                    itemCooldown.getClickedOn()
                        + cooldown.intValue() * 1000L
                        + 1000L
                        - System.currentTimeMillis()));
            return true;
          }

          itemCooldown.setClickedOn(System.currentTimeMillis());
        }
      }

      if (isOnCooldown(player, leftClick, cminbt)) {
        return true;
      }

      boolean handled = false;
      boolean sneaking = player.isSneaking();
      var list = cminbt.getList(CustomNBTManager.CustomNBTType.NBTCommands.name());
      boolean applied = false;
      LinkedHashMap<CMIInteractType, List> linkedHashMap = new LinkedHashMap();
      if (list != null && !list.isEmpty()) {
        for (String string : list) {
          string = decode(player, string);
          if (string != null && string.toLowerCase().startsWith("delay! ")) {
            applied = true;
            break;
          }
        }
      }

      if (list != null && !list.isEmpty()) {
        boolean consumed = false;

        for (String command : list) {
          command = decode(player, command);
          if (command != null) {
            if (target != null) {
              command = command.replace("[interactedPlayer]", target.getName());
            }

            if (block != null) {
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
            } else {
              command = command.replace("[interactedBlock]", "");
              command = command.replace("[interactedBlockX]", "");
              command = command.replace("[interactedBlockY]", "");
              command = command.replace("[interactedBlockZ]", "");
            }

            if (entity != null) {
              command =
                  command.replace(
                      "[interactedEntity]", CMIEntityType.get(entity.getType()).toString());
            } else {
              command = command.replace("[interactedEntity]", "");
            }

            if (!command.contains("[interactedPlayer]")) {
              if (command.toLowerCase().startsWith("!left!") && leftClick) {
                String commandBody = command.substring("!left!".length());
                if (commandBody.startsWith("!silent!")) {
                  commandBody = commandBody.substring("!silent!".length());
                }

                if (commandBody.startsWith("!ignore!")) {
                  commandBody = commandBody.substring("!ignore!".length());
                  consumed = true;
                }

                commandBody = handleCmd(player, commandBody);
                timer.put(player.getUniqueId(), System.currentTimeMillis());
                CMIInteractType cmiInteractType =
                    sneaking ? CMIInteractType.LeftShiftClick : CMIInteractType.LeftClick;
                if (applied) {
                  List leftActions = linkedHashMap.getOrDefault(cmiInteractType, new ArrayList());
                  leftActions.add(commandBody);
                  linkedHashMap.put(cmiInteractType, leftActions);
                  handled = true;
                } else if (CMI.getInstance()
                    .getSpecializedCommandManager()
                    .executeCmd(commandBody, player, cmiInteractType)) {
                  handled = true;
                }
              } else if (command.toLowerCase().startsWith("!right!") && !leftClick) {
                String rightBody = command.substring("!right!".length());
                if (rightBody.startsWith("!silent!")) {
                  rightBody = rightBody.substring("!silent!".length());
                }

                if (rightBody.startsWith("!ignore!")) {
                  rightBody = rightBody.substring("!ignore!".length());
                  consumed = true;
                }

                rightBody = handleCmd(player, rightBody);
                timer.put(player.getUniqueId(), System.currentTimeMillis());
                CMIInteractType rightType =
                    sneaking ? CMIInteractType.RightShiftClick : CMIInteractType.RightClick;
                if (applied) {
                  List rightActions = linkedHashMap.getOrDefault(rightType, new ArrayList());
                  rightActions.add(rightBody);
                  linkedHashMap.put(rightType, rightActions);
                  handled = true;
                } else if (CMI.getInstance()
                    .getSpecializedCommandManager()
                    .executeCmd(rightBody, player, rightType)) {
                  handled = true;
                }
              } else {
                if (command.toLowerCase().startsWith("!consume!")) {
                  return false;
                }

                if (!command.toLowerCase().startsWith("!right!")
                    && !command.toLowerCase().startsWith("!left!")) {
                  String rawCommand = command;
                  if (rawCommand.startsWith("!silent!")) {
                    rawCommand = rawCommand.substring("!silent!".length());
                  }

                  if (rawCommand.startsWith(" ")) {
                    rawCommand = rawCommand.substring(1);
                  }

                  if (rawCommand.startsWith("!ignore!")) {
                    rawCommand = rawCommand.substring("!ignore!".length());
                    consumed = true;
                  }

                  if (rawCommand.startsWith(" ")) {
                    rawCommand = rawCommand.substring(1);
                  }

                  rawCommand = handleCmd(player, rawCommand);
                  timer.put(player.getUniqueId(), System.currentTimeMillis());
                  CMIInteractType airType =
                      leftClick
                          ? (sneaking ? CMIInteractType.LeftShiftClick : CMIInteractType.LeftClick)
                          : (sneaking
                              ? CMIInteractType.RightShiftClick
                              : CMIInteractType.RightClick);
                  if (applied) {
                    List airActions = linkedHashMap.getOrDefault(airType, new ArrayList());
                    airActions.add(rawCommand);
                    linkedHashMap.put(airType, airActions);
                    handled = true;
                  } else if (CMI.getInstance()
                      .getSpecializedCommandManager()
                      .executeCmd(rawCommand, player, airType)) {
                    handled = true;
                  }
                }
              }
            }
          }
        }

        if (CMIPlayerInventory.getTopInventory(player) != null
            && !CMIPlayerInventory.getTopInventory(player)
                .getType()
                .equals(InventoryType.CRAFTING)) {
          timer.put(player.getUniqueId(), System.currentTimeMillis());
        }

        if (!consumed) {
          cancel = true;
        }
      }

      if (applied) {
        for (Entry entry : linkedHashMap.entrySet()) {
          CMI.getInstance()
              .getSpecializedCommandManager()
              .processCmds(
                  "CMIAttackedCommand",
                  (List<String>) entry.getValue(),
                  player,
                  (CMIInteractType) entry.getKey());
        }
      }

      if (handled) {
        Integer leftCount =
            cminbt.getInt(CustomNBTManager.CustomNBTType.CMILimitedUseCurrent.name());
        if (leftCount != null) {
          if (CMIPlayerInventory.getTopInventory(player) != null
              && !CMIPlayerInventory.getTopInventory(player)
                  .getType()
                  .equals(InventoryType.CRAFTING)) {
            timer.put(player.getUniqueId(), System.currentTimeMillis());
          }

          cancel = true;
          int value = leftCount;
          if (value == 1
              && CMIItemStack.getItemInMainHand(player) != null
              && CMIItemStack.getItemInMainHand(player).getAmount() > 1) {
            value = cminbt.getInt(CustomNBTManager.CustomNBTType.CMILimitedUseMax.name());
          } else {
            value--;
          }

          if (leftCount > 1) {
            itemStack =
                CMI.getInstance().getCustomNBTManager().updateUsesLore(itemStack, leftCount - 1);
            itemStack =
                (ItemStack)
                    cminbt.setInt(
                        CustomNBTManager.CustomNBTType.CMILimitedUseCurrent.name(), leftCount - 1);
            if (cmiEquipmentSlot == CMIEquipmentSlot.HAND) {
              CMIItemStack.setItemInMainHand(player, itemStack);
            } else {
              CMIItemStack.setItemInOffHand(player, itemStack);
            }

            if (CMI.getInstance().getConfigManager().isOnLimitedItemUseInform()) {
              CMILC.info(attachcommand.class, player, "usesLeft", "[itemUses]", value);
            }
          } else {
            Integer rightCount =
                cminbt.getInt(CustomNBTManager.CustomNBTType.CMISafeLimitedUse.name());
            if (itemStack.getAmount() > 1) {
              itemStack.setAmount(itemStack.getAmount() - 1);
              if (rightCount != null) {
                ItemStack copiedItem = itemStack.clone();
                copiedItem.setAmount(1);
                copiedItem = CMI.getInstance().getCustomNBTManager().clear(copiedItem);
                copiedItem = CMI.getInstance().getCustomNBTManager().updateUsesLore(copiedItem, 0);
                player.getInventory().addItem(new ItemStack[] {copiedItem});
              }

              itemStack =
                  (ItemStack)
                      cminbt.setInt(
                          CustomNBTManager.CustomNBTType.CMILimitedUseCurrent.name(),
                          cminbt.getInt(CustomNBTManager.CustomNBTType.CMILimitedUseMax.name()));
              if (CMI.getInstance().getConfigManager().isOnLimitedItemUseInform()) {
                CMILC.info(attachcommand.class, player, "usesLeft", "[itemUses]", value);
              }
            } else if (rightCount == null) {
              itemStack = null;
            } else {
              itemStack = CMI.getInstance().getCustomNBTManager().clear(itemStack);
              itemStack = CMI.getInstance().getCustomNBTManager().updateUsesLore(itemStack, 0);
            }

            itemStack = CMI.getInstance().getCustomNBTManager().updateUsesLore(itemStack);
            if (cmiEquipmentSlot == CMIEquipmentSlot.HAND) {
              CMIItemStack.setItemInMainHand(player, itemStack);
            } else {
              CMIItemStack.setItemInOffHand(player, itemStack);
            }
          }
        }
      }

      return handled
              || !cancel
              || itemStack == null
              || !CMIMaterial.isTool(itemStack.getType())
                  && !CMIMaterial.isWeapon(itemStack.getType())
          ? cancel
          : false;
    } else {
      return false;
    }
  }

  private static boolean containsConsume(ItemStack itemStack) {
    var list = new CMINBT(itemStack).getList(CustomNBTManager.CustomNBTType.NBTCommands.name());
    if (list != null && !list.isEmpty()) {
      for (String string : list) {
        string = decode(null, string);
        if (string == null) {
          return false;
        }

        if (string.toLowerCase().startsWith("!consume!")) {
          return true;
        }
      }

      return false;
    } else {
      return false;
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void playerConsumeEvent(PlayerItemConsumeEvent playerItemConsumeEvent) {
    Player player = playerItemConsumeEvent.getPlayer();
    var list =
        new CMINBT(playerItemConsumeEvent.getItem())
            .getList(CustomNBTManager.CustomNBTType.NBTCommands.name());
    if (list != null && !list.isEmpty()) {
      boolean flag = false;

      for (String string : list) {
        string = decode(player, string);
        if (string == null) {
          return;
        }

        if (string.toLowerCase().startsWith("!consume!")) {
          flag = true;
        }
      }

      if (flag) {
        for (String command : list) {
          command = decode(player, command);
          if (command == null) {
            return;
          }

          if (command.toLowerCase().startsWith("!consume!")) {
            command = command.substring("!consume!".length());
          }

          if (command.startsWith("!silent!")) {
            command = command.substring("!silent!".length());
          }

          if (!command.isEmpty()) {
            command = handleCmd(player, command);
            CMI.getInstance().getSpecializedCommandManager().executeCmd(command, player);
          }
        }

        if (processInteract(
            player, null, CMIEquipmentSlot.HAND, Action.LEFT_CLICK_AIR, null, null, true)) {
          playerItemConsumeEvent.setCancelled(true);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void playerInteractAtEntityDamageCommand(
      EntityDamageByEntityEvent entityDamageByEntityEvent) {
    Entity entity = entityDamageByEntityEvent.getEntity();
    if (entity instanceof Player) {
      Player player = (Player) entity;
      if (entityDamageByEntityEvent.getDamager() instanceof Player) {
        Player damager = (Player) entityDamageByEntityEvent.getDamager();
        if (processInteract(
            damager,
            player,
            CMIEquipmentSlot.HAND,
            Action.LEFT_CLICK_AIR,
            null,
            entityDamageByEntityEvent.getEntity(),
            false)) {
          if (util.imortaldebug) {
            CMIMessages.consoleMessage(
                "playerInteractAtEntityDamageCommand->EntityDamageByEntityEvent");
          }

          entityDamageByEntityEvent.setCancelled(true);
        }
      }
    }
  }

  private static String decode(Player player, String string) {
    if (!CMIEncoder.isEncoded(string)) {
      return null;
    }

    String decoded = CMIEncoder.decode(string);
    if (decoded == null && player != null) {
      CMILC.info_CantDecode.sendMessage(player);
    }

    return decoded;
  }

  private static String handleCmd(Player player, String string) {
    Snd snd = new Snd(player, player);
    string = CMI.getInstance().getLM().updateSnd(snd, string);
    string = CMI.getInstance().getCustomNBTManager().handleConsoleCommand(player, string);
    string = CMI.getInstance().getPlaceholderAPIManager().translateOwnPlaceHolder(player, string);
    CMINBT cminbt = new CMINBT(CMIItemStack.getItemInMainHand(player));
    Integer integer = cminbt.getInt(CustomNBTManager.CustomNBTType.CMILimitedUseCurrent.name());
    if (integer != null) {
      if (integer == 1
          && CMIItemStack.getItemInMainHand(player) != null
          && CMIItemStack.getItemInMainHand(player).getAmount() > 1) {
        integer = cminbt.getInt(CustomNBTManager.CustomNBTType.CMILimitedUseMax.name());
      } else {
        integer = integer - 1;
      }

      if (integer != null) {
        string = string.replace("[itemUses]", "" + integer).replace("[itemuses]", "" + integer);
      }
    }

    return string;
  }
}
