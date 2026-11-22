package com.Zrips.CMI.Modules.Warps;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIPaperItemSerializator;
import com.Zrips.CMI.Containers.CMISoundPreset;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.list.editwarp;
import com.Zrips.CMI.commands.list.homes;
import com.Zrips.CMI.commands.list.kiteditor;
import com.Zrips.CMI.commands.list.warp;
import com.Zrips.CMI.events.CMIWarpCreateEvent;
import com.Zrips.CMI.events.CMIWarpRemoveEvent;
import com.Zrips.CMI.utils.Util;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Chat.ChatMessageEdit;
import net.Zrips.CMILib.Chat.ChatMessageListEdit;
import net.Zrips.CMILib.Chat.ChatMessageListEdit.ChatEditType;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.GUI.GUIManager.GUIRows;
import net.Zrips.CMILib.GUI.GUIManager.InvType;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.RawMessages.RawMessageCommand;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WarpManager {
  private CMI plugin;
  LinkedHashMap<String, CmiWarp> warps = new LinkedHashMap<>();
  HashMap<UUID, HashMap<String, CmiWarp>> userWarps = new HashMap<>();
  private boolean warpGUI = true;
  private boolean GUIOnCreation = true;
  private boolean warpShowCreator = true;
  private boolean warpRequirePerm = false;
  private int warpPerPage = 50;
  private int MinLength = 4;
  private int MaxLength = 16;
  private Boolean guiCloseButton;
  private int guiCloseButtonSlot;
  private CMIItemStack guiCloseButtonItem;
  private List<String> guiCloseButtonCommands;
  private boolean guiInfoButton;
  private int guiInfoButtonSlot;
  private CMIItemStack guiInfoButtonItem;
  private List<String> guiInfoButtonCommands;
  public String fileName = "Warps.yml";
  boolean saving = false;
  private CMITask saveId = null;

  public WarpManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void onDisable() {
    if (this.saveId != null) {
      this.saveId.cancel();
      this.saveId = null;
      this.save();
    }
  }

  public HashMap<String, CmiWarp> getWarps() {
    return this.warps;
  }

  public int getWarpCount(Player player) {
    return this.userWarps.get(player.getUniqueId()) == null
        ? 0
        : this.userWarps.get(player.getUniqueId()).size();
  }

  public int getMaxWarps(CommandSender commandSender) {
    return commandSender instanceof Player ? this.getMaxWarps((Player) commandSender) : 9999;
  }

  public int getMaxWarps(Player player) {
    byte b = 1;
    if (player == null || !player.isOnline()) {
      return b;
    } else if (PermissionsManager.CMIPerm.command_setwarp_unlimited.hasPermission(player)) {
      return 9999;
    } else {
      PermissionInfo permissionInfo =
          this.plugin
              .getPermissionsManager()
              .getPermissionInfo(player, PermissionsManager.CMIPerm.command_setwarp_$1);
      if (permissionInfo == null) {
        return 0;
      } else {
        return permissionInfo.getMaxValue() == null ? 0 : permissionInfo.getMaxValue().intValue();
      }
    }
  }

  public List<CmiWarp> getWarps(Player player, String string) {
    return this.getWarps(player, null, string, true, false);
  }

  public List<CmiWarp> getWarps(Player player) {
    return this.getWarps(player, null, null, true, false);
  }

  public List<CmiWarp> getWarps(Player player, Integer integer, String string) {
    return this.getWarps(player, integer, string, true, false);
  }

  public List<CmiWarp> getWarps(Player player, Integer integer, String string, boolean flag) {
    return this.getWarps(player, integer, string, flag, false);
  }

  public List<CmiWarp> getWarps(
      Player player, Integer integer, String string, boolean flag, boolean onlyOwn) {
    ArrayList<CmiWarp> arrayList = new ArrayList();
    boolean isWildcardPerm = PermissionsManager.CMIPerm.command_warp_$1.hasPermission(player, "*");
    if (integer != null) {
      for (var entry : this.warps.entrySet()) {
        if ((!onlyOwn || ((CmiWarp) entry.getValue()).getCreator().equals(player.getUniqueId()))
            && (flag || !((CmiWarp) entry.getValue()).isHidden())
            && ((CmiWarp) entry.getValue()).getLoc().isValid()
            && ((CmiWarp) entry.getValue()).getLoc() != null
            && ((CmiWarp) entry.getValue()).getLoc().getWorld() != null
            && ((CmiWarp) entry.getValue()).getSlot() != null
            && ((CmiWarp) entry.getValue()).getPage() == integer
            && (isWildcardPerm
                || !((CmiWarp) entry.getValue()).isReqPerm()
                || PermissionsManager.CMIPerm.command_warp_$1.hasPermission(
                    player, ((String) entry.getKey()).toLowerCase())
                || ((CmiWarp) entry.getValue()).isShowUnavailable())
            && (string == null || ((CmiWarp) entry.getValue()).sameGroup(string))) {
          arrayList.add((CmiWarp) entry.getValue());
        }
      }

      for (var warpEntry : this.warps.entrySet()) {
        if ((!onlyOwn || ((CmiWarp) warpEntry.getValue()).getCreator().equals(player.getUniqueId()))
            && (flag || !((CmiWarp) warpEntry.getValue()).isHidden())
            && ((CmiWarp) warpEntry.getValue()).getLoc().isValid()
            && ((CmiWarp) warpEntry.getValue()).getLoc() != null
            && ((CmiWarp) warpEntry.getValue()).getLoc().getWorld() != null
            && ((CmiWarp) warpEntry.getValue()).getSlot() == null
            && ((CmiWarp) warpEntry.getValue()).getPage() == integer
            && (isWildcardPerm
                || !((CmiWarp) warpEntry.getValue()).isReqPerm()
                || PermissionsManager.CMIPerm.command_warp_$1.hasPermission(
                    player, ((String) warpEntry.getKey()).toLowerCase())
                || ((CmiWarp) warpEntry.getValue()).isShowUnavailable())
            && (string == null || ((CmiWarp) warpEntry.getValue()).sameGroup(string))) {
          arrayList.add((CmiWarp) warpEntry.getValue());
        }
      }
    } else {
      for (var warpEntry : this.warps.entrySet()) {
        if ((!onlyOwn || ((CmiWarp) warpEntry.getValue()).getCreator().equals(player.getUniqueId()))
            && (flag || !((CmiWarp) warpEntry.getValue()).isHidden())
            && (isWildcardPerm
                || !((CmiWarp) warpEntry.getValue()).isReqPerm()
                || PermissionsManager.CMIPerm.command_warp_$1.hasPermission(
                    player, ((String) warpEntry.getKey()).toLowerCase())
                || ((CmiWarp) warpEntry.getValue()).isShowUnavailable())
            && (string == null || ((CmiWarp) warpEntry.getValue()).sameGroup(string))) {
          arrayList.add((CmiWarp) warpEntry.getValue());
        }
      }
    }

    return arrayList;
  }

  private void process(
      Player player,
      HashMap<Integer, HashMap<Integer, CmiWarp>> hashMap,
      LinkedHashMap<String, CmiWarp> linkedHashMap,
      List<CmiWarp> list,
      String string,
      boolean flag,
      boolean showHidden,
      boolean onlyOwn,
      boolean limitSlots) {
    for (var entry : this.warps.entrySet()) {
      if (onlyOwn && !((CmiWarp) entry.getValue()).getCreator().equals(player.getUniqueId())) {
        linkedHashMap.remove(entry.getKey());
      } else if (!showHidden && ((CmiWarp) entry.getValue()).isHidden()) {
        linkedHashMap.remove(entry.getKey());
      } else if (!((CmiWarp) entry.getValue()).getLoc().isValid()) {
        linkedHashMap.remove(entry.getKey());
      } else if (((CmiWarp) entry.getValue()).getLoc() != null
          && ((CmiWarp) entry.getValue()).getLoc().getWorld() != null) {
        if (!flag
            && ((CmiWarp) entry.getValue()).isReqPerm()
            && !PermissionsManager.CMIPerm.command_warp_$1.hasPermission(
                player, ((String) entry.getKey()).toLowerCase())
            && !((CmiWarp) entry.getValue()).isShowUnavailable()) {
          linkedHashMap.remove(entry.getKey());
        } else if (string != null && !((CmiWarp) entry.getValue()).sameGroup(string)) {
          linkedHashMap.remove(entry.getKey());
        } else if (((CmiWarp) entry.getValue()).getPage() != null) {
          HashMap pageSlots =
              hashMap.getOrDefault(((CmiWarp) entry.getValue()).getPage(), new HashMap());
          if (pageSlots.size() < 28
              && (((CmiWarp) entry.getValue()).getSlot() == null
                  || ((CmiWarp) entry.getValue()).getSlot() < 28)) {
            if (limitSlots) {
              if (((CmiWarp) entry.getValue()).getSlot() != null
                  && !pageSlots.containsKey(((CmiWarp) entry.getValue()).getSlot())) {
                pageSlots.put(((CmiWarp) entry.getValue()).getSlot(), (CmiWarp) entry.getValue());
                hashMap.put(((CmiWarp) entry.getValue()).getPage(), pageSlots);
                linkedHashMap.remove(entry.getKey());
              }
            } else if (linkedHashMap.containsKey(entry.getKey())) {
              if (((CmiWarp) entry.getValue()).getSlot() != null
                  && !pageSlots.containsKey(((CmiWarp) entry.getValue()).getSlot())) {
                pageSlots.put(((CmiWarp) entry.getValue()).getSlot(), (CmiWarp) entry.getValue());
                hashMap.put(((CmiWarp) entry.getValue()).getPage(), pageSlots);
                linkedHashMap.remove(entry.getKey());
              } else {
                int value =
                    ((CmiWarp) entry.getValue()).getSlot() == null
                        ? 0
                        : ((CmiWarp) entry.getValue()).getSlot();
                boolean placed = false;
                int slotCandidate = value;

                while (true) {
                  if (slotCandidate < 28) {
                    if (pageSlots.containsKey(slotCandidate)) {
                      slotCandidate++;
                      continue;
                    }

                    placed = true;
                    pageSlots.put(slotCandidate, (CmiWarp) entry.getValue());
                    hashMap.put(((CmiWarp) entry.getValue()).getPage(), pageSlots);
                  }

                  if (!placed) {
                    list.add((CmiWarp) entry.getValue());
                  }

                  linkedHashMap.remove(entry.getKey());
                  break;
                }
              }
            }
          } else if (linkedHashMap.containsKey(entry.getKey())) {
            list.add((CmiWarp) entry.getValue());
            linkedHashMap.remove(entry.getKey());
          }
        }
      } else {
        linkedHashMap.remove(entry.getKey());
      }
    }
  }

  public HashMap<Integer, HashMap<Integer, CmiWarp>> getPagedWarps(
      Player player, String string, boolean flag, boolean showHidden) {
    HashMap<Integer, HashMap<Integer, CmiWarp>> hashMap = new HashMap<>();
    ArrayList arrayList = new ArrayList();
    boolean isWildcardPerm = PermissionsManager.CMIPerm.command_warp_$1.hasPermission(player, "*");
    var linkedHashMap = new LinkedHashMap<>(this.warps);
    this.process(
        player, hashMap, linkedHashMap, arrayList, string, isWildcardPerm, flag, showHidden, true);
    this.process(
        player, hashMap, linkedHashMap, arrayList, string, isWildcardPerm, flag, showHidden, false);

    for (CmiWarp cmiWarp : ((Iterable<CmiWarp>) arrayList)) {
      linkedHashMap.put(cmiWarp.getName().toLowerCase(), cmiWarp);
    }

    int value = 1;

    for (Integer integer : hashMap.keySet()) {
      if (integer > value) {
        value = integer;
      }
    }

    LinkedHashMap pageCopy = new LinkedHashMap(linkedHashMap);

    label93:
    for (Entry entry : linkedHashMap.entrySet()) {
      for (int page = 1; page <= value; page++) {
        HashMap pageSlots = hashMap.getOrDefault(page, new HashMap());
        if (pageSlots.size() < 28) {
          int startSlot =
              ((CmiWarp) entry.getValue()).getSlot() == null
                  ? 0
                  : ((CmiWarp) entry.getValue()).getSlot();

          for (int slotIndex = startSlot; slotIndex < 28; slotIndex++) {
            if (!pageSlots.containsKey(slotIndex)) {
              pageSlots.put(slotIndex, (CmiWarp) entry.getValue());
              hashMap.put(page, pageSlots);
              pageCopy.remove(entry.getKey());
              continue label93;
            }
          }
        }
      }
    }

    if (!pageCopy.isEmpty()) {
      for (Entry pageEntry : ((java.util.Map<?, ?>) pageCopy).entrySet()) {
        HashMap nextPageSlots = hashMap.getOrDefault(value + 1, new HashMap());
        if (nextPageSlots.size() < 28) {
          int nextStartSlot =
              ((CmiWarp) pageEntry.getValue()).getSlot() == null
                  ? 0
                  : ((CmiWarp) pageEntry.getValue()).getSlot();

          for (int nextSlotIndex = nextStartSlot; nextSlotIndex < 28; nextSlotIndex++) {
            if (!nextPageSlots.containsKey(nextSlotIndex)) {
              nextPageSlots.put(nextSlotIndex, (CmiWarp) pageEntry.getValue());
              hashMap.put(value + 1, nextPageSlots);
              break;
            }
          }
        }
      }
    }

    int emptySlots = 0;

    for (Entry warpEntry : hashMap.entrySet()) {
      emptySlots += ((HashMap) warpEntry.getValue()).size();
    }

    return hashMap;
  }

  public void openWarpEditor(Player player, String string) {
    this.openWarpEditor(player, this.getWarp(string));
  }

  public void openWarpEditor(final Player player, final CmiWarp cmiWarp) {
    if (player != null) {
      if (player.isOnline()) {
        if (cmiWarp != null) {
          CMIGui cmiGui =
              new CMIGui(player) {
                public void onClose() {
                  WarpManager.this.safeSave();
                }
              };
          cmiGui.setTitle(cmiWarp.getName());
          cmiGui.setInvSize(GUIRows.r6);
          CMIGuiButton cmiGuiButton =
              new CMIGuiButton(WarpManager.warpEditorSlots.icon.getSlot(), cmiWarp.getIcon()) {
                public void click() {
                  ItemStack itemStack = player.getItemOnCursor();
                  if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
                    cmiWarp.setItem(itemStack.clone());
                  } else {
                    cmiWarp.setItem(null);
                  }

                  WarpManager.this.safeSave();
                  WarpManager.this.openWarpEditor(player, cmiWarp);
                }
              };
          cmiGuiButton.addLore("");
          cmiGuiButton.addLore(CMILC.getIM(editwarp.class, "placeItem"));
          cmiGui.addButton(cmiGuiButton);
          cmiGuiButton =
              new CMIGuiButton(
                  WarpManager.warpEditorSlots.offIcon.getSlot(), cmiWarp.getOffIcon()) {
                public void click() {
                  ItemStack cursorItem = player.getItemOnCursor();
                  if (cursorItem != null && !cursorItem.getType().equals(Material.AIR)) {
                    cmiWarp.setOffItem(cursorItem.clone());
                  } else {
                    cmiWarp.setOffItem(null);
                  }

                  WarpManager.this.safeSave();
                  WarpManager.this.plugin.getWarpManager().openWarpEditor(player, cmiWarp);
                }
              };
          cmiGuiButton.addLore("");
          cmiGuiButton.addLore(CMILC.getIM(editwarp.class, "placeOffItem"));
          cmiGui.addButton(cmiGuiButton);
          cmiGuiButton =
              new CMIGuiButton(
                  WarpManager.warpEditorSlots.randomYaw.getSlot(),
                  cmiWarp.isAutoLore()
                      ? CMIMaterial.COMPASS.newItemStack()
                      : CMIMaterial.COMPASS.newItemStack()) {
                public void click() {
                  cmiWarp.setRandomizeYaw(!cmiWarp.isRandomizeYaw());
                  WarpManager.this.plugin.getWarpManager().openWarpEditor(player, cmiWarp);
                  WarpManager.this.safeSave();
                }
              };
          cmiGuiButton.setName(
              CMILC.getIM(editwarp.class, "randomizeYaw", "[state]", cmiWarp.isRandomizeYaw()));
          cmiGui.addButton(cmiGuiButton);
          cmiGuiButton =
              new CMIGuiButton(
                  WarpManager.warpEditorSlots.autoLore.getSlot(),
                  cmiWarp.isAutoLore()
                      ? CMIMaterial.GREEN_WOOL.newItemStack()
                      : CMIMaterial.RED_WOOL.newItemStack()) {
                public void click() {
                  cmiWarp.setAutoLore(!cmiWarp.isAutoLore());
                  WarpManager.this.plugin.getWarpManager().openWarpEditor(player, cmiWarp);
                  WarpManager.this.safeSave();
                }
              };
          cmiGuiButton.setName(
              CMILC.getIM(editwarp.class, "autoLore", "[state]", cmiWarp.isAutoLore()));
          cmiGui.addButton(cmiGuiButton);
          cmiGuiButton =
              new CMIGuiButton(
                  WarpManager.warpEditorSlots.permission.getSlot(),
                  cmiWarp.isReqPerm()
                      ? CMIMaterial.GREEN_WOOL.newItemStack()
                      : CMIMaterial.RED_WOOL.newItemStack()) {
                public void click() {
                  cmiWarp.setReqPerm(!cmiWarp.isReqPerm());
                  WarpManager.this.plugin.getWarpManager().openWarpEditor(player, cmiWarp);
                  WarpManager.this.safeSave();
                }
              };
          cmiGuiButton.setName(
              CMILC.getIM(editwarp.class, "permission", "[state]", cmiWarp.isReqPerm()));
          cmiGui.addButton(cmiGuiButton);
          cmiGuiButton =
              new CMIGuiButton(
                  WarpManager.warpEditorSlots.reqPermission.getSlot(),
                  cmiWarp.isShowUnavailable()
                      ? CMIMaterial.GREEN_WOOL.newItemStack()
                      : CMIMaterial.RED_WOOL.newItemStack()) {
                public void click() {
                  cmiWarp.setShowUnavailable(!cmiWarp.isShowUnavailable());
                  WarpManager.this.plugin.getWarpManager().openWarpEditor(player, cmiWarp);
                  WarpManager.this.safeSave();
                }
              };
          cmiGuiButton.setName(
              CMILC.getIM(editwarp.class, "reqPermission", "[state]", cmiWarp.isShowUnavailable()));
          cmiGui.addButton(cmiGuiButton);
          cmiGuiButton =
              new CMIGuiButton(WarpManager.warpEditorSlots.hidden.getSlot()) {
                public void click() {
                  cmiWarp.setHidden(!cmiWarp.isHidden());
                  this.updateLooks();
                  this.update();
                  WarpManager.this.safeSave();
                }

                public void updateLooks() {
                  this.setItem(
                      !cmiWarp.isHidden()
                          ? CMIMaterial.GREEN_WOOL.newItemStack()
                          : CMIMaterial.YELLOW_WOOL.newItemStack());
                  this.setName(
                      CMILC.getIM(editwarp.class, "hidden", "[state]", cmiWarp.isHidden()));
                }
              };
          cmiGui.addButton(cmiGuiButton);
          if (this.plugin.getWarpManager().isWarpGUI()) {
            cmiGuiButton =
                new CMIGuiButton(
                    WarpManager.warpEditorSlots.back.getSlot(),
                    CMILib.getInstance().getConfigManager().getGUIPreviousPage()) {
                  public void click() {
                    WarpManager.this.safeSave();
                  }
                };
            cmiGuiButton.setName(LC.info_Back.getLocale(new Object[0]));
            cmiGuiButton.addCommand("cmi warp -p:" + cmiWarp.getPage());
            cmiGui.addButton(cmiGuiButton);
          }

          cmiGuiButton =
              new CMIGuiButton(
                  WarpManager.warpEditorSlots.location.getSlot(),
                  CMIMaterial.STICK.newItemStack()) {
                public void click() {
                  cmiWarp.setLoc(new CMILocation(player.getLocation()));
                  WarpManager.this.plugin.getWarpManager().openWarpEditor(player, cmiWarp);
                  WarpManager.this.safeSave();
                }
              };
          cmiGuiButton.setName(CMILC.getIM(editwarp.class, "primaryLocation"));
          ArrayList<String> arrayList = new ArrayList();
          arrayList.add(CMIChatColor.translate("&e" + cmiWarp.getLoc().getWorldName()));
          arrayList.add(
              CMIChatColor.translate(
                  "&e"
                      + cmiWarp.getLoc().getBlockX()
                      + ":"
                      + cmiWarp.getLoc().getBlockY()
                      + ":"
                      + cmiWarp.getLoc().getBlockZ()));
          cmiGuiButton.addLore(arrayList);
          cmiGui.addButton(cmiGuiButton);
          if (PermissionsManager.CMIPerm.command_setwarp_unlimited.hasPermission(player)) {
            cmiGuiButton =
                new CMIGuiButton(
                    WarpManager.warpEditorSlots.seclocation.getSlot(),
                    CMIMaterial.LADDER.newItemStack()) {
                  public void click() {
                    CMIUser.closeInventory(player);
                    ChatMessageListEdit chatMessageListEdit =
                        new ChatMessageListEdit(
                            player, cmiWarp.getLocations(), ChatEditType.Location, 10) {
                          public void onUpdate() {
                            cmiWarp.resetQueue();
                            WarpManager.this.safeSave();
                          }
                        };
                    RawMessage rawMessage = new RawMessage();
                    RawMessageCommand rawMessageCommand =
                        new RawMessageCommand() {
                          public void run(CommandSender commandSender) {
                            cmiWarp.resetQueue();
                            WarpManager.this
                                .plugin
                                .getWarpManager()
                                .openWarpEditor(player, cmiWarp);
                          }
                        };
                    rawMessageCommand.setKeep(true);
                    rawMessage
                        .addText(LC.Locations.getLocale(new Object[0]))
                        .addHover(CMILC.getIM(kiteditor.class, "ListTitleHover"))
                        .addCommand(rawMessageCommand.getCommand());
                    chatMessageListEdit.setTopLine(rawMessage);
                    chatMessageListEdit.print();
                    WarpManager.this.safeSave();
                  }
                };
            cmiGuiButton.setName(CMILC.getIM(editwarp.class, "secondaryLocation"));
            arrayList = new ArrayList();

            for (CMILocation cmiLocation : cmiWarp.getLocations()) {
              arrayList.add(CMIChatColor.translate("&f" + CMILocation.toString(cmiLocation)));
            }

            arrayList.add("");
            arrayList.add(LC.modify_ChangeHover.getLocale(new Object[0]));
            cmiGuiButton.addLore(arrayList);
            cmiGui.addButton(cmiGuiButton);
            if (!cmiWarp.getLocations().isEmpty()) {
              cmiGuiButton =
                  new CMIGuiButton(
                      WarpManager.warpEditorSlots.repeat.getSlot(),
                      CMIMaterial.RAIL.newItemStack()) {
                    public void click() {
                      cmiWarp.setRepeatingLocation(!cmiWarp.isRepeatingLocation());
                      WarpManager.this.plugin.getWarpManager().openWarpEditor(player, cmiWarp);
                      WarpManager.this.safeSave();
                    }
                  };
              cmiGuiButton.setName(
                  CMILC.getIM(
                      editwarp.class, "uniqueLocation", "[state]", cmiWarp.isRepeatingLocation()));
              arrayList = new ArrayList();
              cmiGuiButton.addLore(arrayList);
              cmiGui.addButton(cmiGuiButton);
            }
          }

          cmiGuiButton =
              new CMIGuiButton(
                  WarpManager.warpEditorSlots.group.getSlot(), CMIMaterial.BOOK.newItemStack()) {
                public void click() {
                  CMIUser.closeInventory(player);
                  CMILC.info(editwarp.class, player, "newGroup");
                  ChatMessageEdit chatMessageEdit =
                      new ChatMessageEdit(player, cmiWarp.getGroup()) {
                        public void run(String string) {
                          string = CMIChatColor.stripColor(CMIText.getFirstVariable(string));
                          if (CMIText.isValidString(string)) {
                            if (string.contains(" ")) {
                              string = string.split(" ")[0];
                            }

                            cmiWarp.setGroup(string);
                            WarpManager.this.openWarpEditor(player, cmiWarp);
                            WarpManager.this.safeSave();
                          }
                        }

                        public void onCancel() {
                          WarpManager.this.openWarpEditor(player, cmiWarp);
                        }
                      };
                  chatMessageEdit.setCheckForCancel(true);
                  chatMessageEdit.printMessage();
                }
              };
          cmiGuiButton.setName(
              CMILC.getIM(
                  editwarp.class,
                  "group",
                  "[group]",
                  cmiWarp.getGroup() == null ? "N/A" : cmiWarp.getGroup()));
          cmiGui.addButton(cmiGuiButton);
          cmiGuiButton =
              new CMIGuiButton(
                  WarpManager.warpEditorSlots.displayName.getSlot(), CMIMaterial.ITEM_FRAME) {
                public void click() {
                  CMIUser.closeInventory(player);
                  CMILC.info(editwarp.class, player, "newDisplayName");
                  ChatMessageEdit chatEdit =
                      new ChatMessageEdit(player, cmiWarp.getDisplayName()) {
                        public void run(String input) {
                          if (CMIText.isValidString(input)) {
                            if (!CMIChatColor.stripColor(input).equalsIgnoreCase(cmiWarp.getName())
                                && !PermissionsManager.CMIPerm.command_warp_diffdisplayname
                                    .hasPermission(player, true)) {
                              WarpManager.this.openWarpEditor(player, cmiWarp);
                            } else {
                              cmiWarp.setDisplayName(input);
                              WarpManager.this.openWarpEditor(player, cmiWarp);
                              WarpManager.this.safeSave();
                            }
                          }
                        }

                        public void onCancel() {
                          WarpManager.this.openWarpEditor(player, cmiWarp);
                        }
                      };
                  chatEdit.setCheckForCancel(true);
                  chatEdit.printMessage();
                }
              };
          cmiGuiButton.setName(
              CMILC.getIM(
                  editwarp.class,
                  "displayName",
                  "[displayName]",
                  cmiWarp.getDisplayName() == null ? "N/A" : cmiWarp.getDisplayName()));
          cmiGui.addButton(cmiGuiButton);
          cmiGuiButton =
              new CMIGuiButton(
                  WarpManager.warpEditorSlots.iconSlot.getSlot(),
                  CMIMaterial.BROWN_WOOL.newItemStack()) {
                public void click(GUIClickType guiClickType) {
                  int value = cmiWarp.getSlot() == null ? -1 : cmiWarp.getSlot();
                  switch (guiClickType) {
                    case Left:
                      cmiWarp.setSlot(value + 1);
                      break;
                    case LeftShift:
                      cmiWarp.setSlot(value + 10);
                      break;
                    case Right:
                      cmiWarp.setSlot(value - 1);
                      break;
                    case RightShift:
                      cmiWarp.setSlot(value - 10);
                  }

                  WarpManager.this.safeSave();
                  WarpManager.this.plugin.getWarpManager().openWarpEditor(player, cmiWarp);
                }
              };
          cmiGuiButton.setName(
              CMILC.getIM(
                  editwarp.class,
                  "slot",
                  "[slot]",
                  cmiWarp.getSlot() == null
                      ? CMILC.getIM(editwarp.class, "notSet")
                      : cmiWarp.getSlot() + 1));
          var list = this.getWarps(cmiWarp.getPage(), cmiWarp.getSlot(), cmiWarp.getGroup());
          ArrayList<String> warpNames = new ArrayList();
          if (list.size() > 1) {
            warpNames.add(CMILC.getIM(editwarp.class, "overllaps"));

            for (CmiWarp warp : list) {
              if (!warp.equals(cmiWarp)) {
                warpNames.add("&7" + warp.getName());
              }
            }

            cmiGuiButton.addLore(warpNames);
          }

          cmiGui.addButton(cmiGuiButton);
          cmiGuiButton =
              new CMIGuiButton(
                  WarpManager.warpEditorSlots.page.getSlot(),
                  CMIMaterial.PURPLE_WOOL.newItemStack()) {
                public void click(GUIClickType clickType) {
                  int pageValue = cmiWarp.getPage() == null ? -1 : cmiWarp.getPage();
                  switch (clickType) {
                    case Left:
                    case LeftShift:
                      cmiWarp.setPage(pageValue + 1);
                      break;
                    case Right:
                    case RightShift:
                      cmiWarp.setPage(pageValue - 1);
                  }

                  WarpManager.this.safeSave();
                  WarpManager.this.plugin.getWarpManager().openWarpEditor(player, cmiWarp);
                }
              };
          cmiGuiButton.setName(CMILC.getIM(editwarp.class, "page", "[page]", cmiWarp.getPage()));
          list = this.getWarps(cmiWarp.getPage(), cmiWarp.getSlot(), cmiWarp.getGroup());
          warpNames = new ArrayList();
          if (list.size() > 1) {
            warpNames.add(CMILC.getIM(editwarp.class, "overllaps"));

            for (CmiWarp warp : list) {
              if (!warp.equals(cmiWarp)) {
                warpNames.add("&7" + warp.getName());
              }
            }

            cmiGuiButton.addLore(warpNames);
          }

          cmiGui.addButton(cmiGuiButton);
          cmiGui.fillEmptyButtons();
          cmiGui.open();
        }
      }
    }
  }

  public List<CmiWarp> getWarps(int value, String string) {
    ArrayList<CmiWarp> arrayList = new ArrayList();

    for (var entry : this.getWarps().entrySet()) {
      if (((CmiWarp) entry.getValue()).sameGroup(string)
          && ((CmiWarp) entry.getValue()).getPage() == value) {
        arrayList.add((CmiWarp) entry.getValue());
      }
    }

    return arrayList;
  }

  public int getPageCountFrom(int value, String string) {
    int page = value;

    for (var entry : this.getWarps().entrySet()) {
      if (((CmiWarp) entry.getValue()).sameGroup(string)
          && ((CmiWarp) entry.getValue()).getPage() > page) {
        page = ((CmiWarp) entry.getValue()).getPage();
      }
    }

    return page - value;
  }

  public int getPageCount(Player player) {
    return this.getPageCount(this.getWarps(player));
  }

  public int getPageCount(List<CmiWarp> list) {
    int value = 1;

    for (CmiWarp cmiWarp : list) {
      if (cmiWarp.getPage() != null && cmiWarp.getPage() > value) {
        value = cmiWarp.getPage();
      }
    }

    if (Math.ceil(list.size() / 28.0) > value) {
      value = (int) Math.ceil(list.size() / 28.0);
    }

    return value;
  }

  public List<CmiWarp> getWarps(int value, Integer integer, String string) {
    ArrayList<CmiWarp> arrayList = new ArrayList();

    for (var entry : this.getWarps().entrySet()) {
      if (((CmiWarp) entry.getValue()).sameGroup(string)
          && ((CmiWarp) entry.getValue()).getPage() == value
          && ((CmiWarp) entry.getValue()).getSlot() != null
          && integer != null
          && ((CmiWarp) entry.getValue()).getSlot() == integer) {
        arrayList.add((CmiWarp) entry.getValue());
      }
    }

    return arrayList;
  }

  private static int slotToGUIrelativeSlot(int value) {
    return 9 + value / 7 * 2 + 1 + value;
  }

  public CMIGui openComplexGUI(Player player, int value, String string) {
    var hashMap = this.getPagedWarps(player, string, false, false);
    return this.openComplexGUI(player, value, hashMap);
  }

  public CMIGui openComplexGUI(
      final Player player, int value, final HashMap<Integer, HashMap<Integer, CmiWarp>> hashMap) {
    CMIGui cmiGui =
        new CMIGui(player) {
          public void pageChange(int page) {
            WarpManager.this.openComplexGUI(player, page, hashMap);
          }
        };
    cmiGui.setOpenSound(CMISoundPreset.WarpGuiOpen.getSound());
    boolean flag =
        PermissionsManager.CMIPerm.command_$1.hasPermission(player, editwarp.class.getSimpleName());
    boolean isOwnerView =
        PermissionsManager.CMIPerm.command_$1_others.hasPermission(
            player, editwarp.class.getSimpleName());
    cmiGui.setInvSize(GUIRows.r6);
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(this.guiCloseButtonSlot, this.guiCloseButtonItem) {
          public void click(GUIClickType guiClickType) {
            Snd snd = new Snd();
            snd.setTarget(player);
            snd.setSender(player);
            snd.setSource(player);
            WarpManager.this
                .plugin
                .getSpecializedCommandManager()
                .processCmds(
                    WarpManager.this
                        .plugin
                        .getLM()
                        .updateSnd(snd, new ArrayList<>(WarpManager.this.guiCloseButtonCommands)),
                    player);
          }
        };
    cmiGuiButton.setName(CMILC.getIM(homes.class, "closeButton"));
    if (this.guiCloseButton) {
      cmiGui.addButton(cmiGuiButton);
    }

    cmiGuiButton =
        new CMIGuiButton(this.guiInfoButtonSlot, this.guiInfoButtonItem) {
          public void click(GUIClickType clickType) {
            Snd clickSound = new Snd();
            clickSound.setTarget(player);
            clickSound.setSender(player);
            clickSound.setSource(player);
            WarpManager.this
                .plugin
                .getSpecializedCommandManager()
                .processCmds(
                    WarpManager.this
                        .plugin
                        .getLM()
                        .updateSnd(
                            clickSound, new ArrayList<>(WarpManager.this.guiInfoButtonCommands)),
                    player);
          }
        };
    cmiGuiButton.setName(CMILC.getIM(homes.class, "infoButton"));
    if (this.guiInfoButton) {
      cmiGui.addButton(cmiGuiButton);
    }

    HashMap pageSlots = (HashMap) hashMap.get(value);
    int slotIndex = 1;

    for (var entry : hashMap.entrySet()) {
      if ((Integer) entry.getKey() > slotIndex) {
        slotIndex = (Integer) entry.getKey();
      }
    }

    PageInfo pageInfo = new PageInfo(1, slotIndex, value);
    cmiGui.addPagination(pageInfo);
    cmiGui.fillInEdges();
    if (pageSlots != null) {
      for (Entry pageEntry : ((java.util.Map<?, ?>) pageSlots).entrySet()) {
        CmiWarp cmiWarp = (CmiWarp) pageEntry.getValue();
        if (cmiWarp.getLoc() != null && cmiWarp.getLoc().getWorld() != null) {
          Integer integer = slotToGUIrelativeSlot((Integer) pageEntry.getKey());
          cmiGuiButton = new CMIGuiButton(integer, cmiWarp.getIcon());
          cmiGuiButton.hideItemFlags();
          if (cmiWarp.isReqPerm()
              && !PermissionsManager.CMIPerm.command_warp_$1.hasPermission(
                  player, cmiWarp.getName().toLowerCase())
              && cmiWarp.isShowUnavailable()) {
            cmiGuiButton.setItem(cmiWarp.getOffIcon());
          }

          cmiGuiButton.addCommand(GUIClickType.Left, "cmi warp " + cmiWarp.getName());
          cmiGuiButton.addCommand(GUIClickType.Right, "cmi warp " + cmiWarp.getName());
          cmiGuiButton.addCommand(GUIClickType.MiddleMouse, "cmi editwarp " + cmiWarp.getName());
          cmiGuiButton.addCommand(GUIClickType.Q, "cmi editwarp " + cmiWarp.getName());
          if (flag && (cmiWarp.getCreator().equals(player.getUniqueId()) || isOwnerView)) {
            cmiGuiButton.addLore(" ");
            cmiGuiButton.addLore(LC.modify_middlemouse.getLocale(new Object[0]));
            cmiGuiButton.addLore(" ");
            cmiGuiButton.addLore(
                CMILC.getIM(editwarp.class, "autoLore", "[state]", cmiWarp.isAutoLore()));
            cmiGuiButton.addLore(
                CMILC.getIM(editwarp.class, "randomizeYaw", "[state]", cmiWarp.isRandomizeYaw()));
            cmiGuiButton.addLore(
                CMILC.getIM(editwarp.class, "permission", "[state]", cmiWarp.isReqPerm()));
            cmiGuiButton.addLore(
                CMILC.getIM(
                    editwarp.class, "reqPermission", "[state]", cmiWarp.isShowUnavailable()));
          }

          cmiGui.addButton(cmiGuiButton, 44);
        }
      }
    }

    cmiGui.addLock(InvType.Gui);
    String string = CMILC.getIM(warp.class, "guiTitle");
    var list = CMILC.getIML(warp.class, "pageTitles");
    if (list.size() >= value) {
      string = (String) list.get(value - 1);
    }

    cmiGui.setTitle(string);
    cmiGui.open();
    return cmiGui;
  }

  public CMIGui openGUI(Player player, int value, String string) {
    return this.openGUI(
        player, this.plugin.getWarpManager().getWarps(player, value, string, false), string);
  }

  public CMIGui openGUI(final Player player, List<CmiWarp> list, final String string) {
    CMIGui cmiGui =
        new CMIGui(player) {
          public void pageChange(int value) {
            WarpManager.this.openGUI(player, value, string);
          }
        };
    cmiGui.setOpenSound(CMISoundPreset.WarpGuiOpen.getSound());
    boolean flag =
        PermissionsManager.CMIPerm.command_$1.hasPermission(player, editwarp.class.getSimpleName());
    boolean showHidden =
        PermissionsManager.CMIPerm.command_$1_others.hasPermission(
            player, editwarp.class.getSimpleName());
    int page = 1;

    for (CmiWarp cmiWarp : list) {
      if (cmiWarp.getLoc() != null
          && cmiWarp.getLoc().getWorld() != null
          && (cmiWarp.getSlot() == null || cmiWarp.getSlot() < 45)) {
        if (cmiGui.getButtons().size() > 45) {
          cmiWarp.setPage(cmiWarp.getPage() + 1);
        } else {
          CMIGuiButton cmiGuiButton = new CMIGuiButton(cmiWarp.getSlot(), cmiWarp.getIcon());
          cmiGuiButton.hideItemFlags();
          if (cmiWarp.isReqPerm()
              && !PermissionsManager.CMIPerm.command_warp_$1.hasPermission(
                  player, cmiWarp.getName().toLowerCase())
              && cmiWarp.isShowUnavailable()) {
            cmiGuiButton.setItem(cmiWarp.getOffIcon());
          }

          cmiGuiButton.addCommand(GUIClickType.Left, "cmi warp " + cmiWarp.getName());
          cmiGuiButton.addCommand(GUIClickType.Right, "cmi warp " + cmiWarp.getName());
          cmiGuiButton.addCommand(GUIClickType.MiddleMouse, "cmi editwarp " + cmiWarp.getName());
          cmiGuiButton.addCommand(GUIClickType.Q, "cmi editwarp " + cmiWarp.getName());
          if (flag && (cmiWarp.getCreator().equals(player.getUniqueId()) || showHidden)) {
            cmiGuiButton.addLore(" ");
            cmiGuiButton.addLore(LC.modify_middlemouse.getLocale(new Object[0]));
            cmiGuiButton.addLore(" ");
            cmiGuiButton.addLore(
                CMILC.getIM(editwarp.class, "autoLore", "[state]", cmiWarp.isAutoLore()));
            cmiGuiButton.addLore(
                CMILC.getIM(editwarp.class, "randomizeYaw", "[state]", cmiWarp.isRandomizeYaw()));
            cmiGuiButton.addLore(
                CMILC.getIM(editwarp.class, "permission", "[state]", cmiWarp.isReqPerm()));
            cmiGuiButton.addLore(
                CMILC.getIM(
                    editwarp.class, "reqPermission", "[state]", cmiWarp.isShowUnavailable()));
          }

          page = cmiWarp.getPage();
          cmiGui.addButton(cmiGuiButton, 45);
        }
      }
    }

    cmiGui.autoResize();
    cmiGui.addLock(InvType.Gui);
    PageInfo pageInfo = new PageInfo(1, this.getPageCountFrom(page, string) + page, page);
    cmiGui.addPagination(pageInfo);
    cmiGui.fillEmptyButtons();
    String guiTitle = CMILC.getIM(warp.class, "guiTitle");
    var pageTitles = CMILC.getIML(warp.class, "pageTitles");
    if (pageTitles.size() >= page) {
      guiTitle = (String) pageTitles.get(page - 1);
    }

    cmiGui.setTitle(guiTitle);
    cmiGui.open();
    return cmiGui;
  }

  public void remove(CmiWarp cmiWarp) {
    CMIWarpRemoveEvent cmiWarpRemoveEvent = new CMIWarpRemoveEvent(cmiWarp);
    this.plugin.getServer().getPluginManager().callEvent(cmiWarpRemoveEvent);
    this.warps.remove(cmiWarp.getName().toLowerCase());
    var hashMap = this.userWarps.remove(cmiWarp.getCreator());
    if (hashMap != null) {
      hashMap.remove(cmiWarp.getName().toLowerCase());
      if (!hashMap.isEmpty()) {
        this.userWarps.put(cmiWarp.getCreator(), hashMap);
      }
    }

    this.safeSave();
  }

  public void addWarp(CmiWarp cmiWarp) {
    this.addWarp(cmiWarp, true);
  }

  public void addWarp(CmiWarp cmiWarp, boolean flag) {
    if (this.plugin.isFullyLoaded()) {
      CMIWarpCreateEvent cmiWarpCreateEvent = new CMIWarpCreateEvent(cmiWarp);
      this.plugin.getServer().getPluginManager().callEvent(cmiWarpCreateEvent);
      if (cmiWarpCreateEvent.isCancelled()) {
        return;
      }
    }

    this.warps.put(cmiWarp.getName().toLowerCase(), cmiWarp);
    var hashMap = this.userWarps.get(cmiWarp.getCreator());
    if (hashMap == null) {
      hashMap = new HashMap();
    }

    hashMap.put(cmiWarp.getName().toLowerCase(), cmiWarp);
    this.userWarps.put(cmiWarp.getCreator(), hashMap);
    if (flag) {
      this.safeSave();
    }
  }

  public CmiWarp getWarp(Player player, String string) {
    if (string == null) {
      return null;
    } else {
      CmiWarp cmiWarp = this.warps.get(string.toLowerCase());
      if (cmiWarp != null) {
        return cmiWarp.isReqPerm()
                && !PermissionsManager.CMIPerm.command_warp_$1.hasPermission(
                    player, cmiWarp.getName().toLowerCase())
            ? null
            : cmiWarp;
      } else {
        return null;
      }
    }
  }

  public CmiWarp getWarp(String string) {
    return string == null ? null : this.warps.get(string.toLowerCase());
  }

  public void loadConfig(ConfigReader configReader) {
    configReader.addComment(
        "Warps.GUI",
        new String[] {"When set to true, warps list will be shown in GUI instead of chat list"});
    this.warpGUI = configReader.get("Warps.GUI", true);
    configReader.addComment(
        "Warps.GUIOnCreation",
        new String[] {"Automatically opens GUI when created new warp point"});
    this.GUIOnCreation = configReader.get("Warps.GUIOnCreation", true);
    configReader.addComment("Warps.MinLength", new String[] {"Minimal length of warp name"});
    this.MinLength =
        configReader.get("Warps.MinLength", configReader.getC().getInt("Warps.MinLenght", 4));
    configReader.addComment("Warps.MaxLength", new String[] {"Maximal length of warp name"});
    this.MaxLength =
        configReader.get("Warps.MaxLength", configReader.getC().getInt("Warps.MaxLenght", 16));
    configReader.addComment("Warps.perPage", new String[] {"How many warps to show in each page"});
    this.warpPerPage = configReader.get("Warps.perPage", 50);
    configReader.addComment(
        "Warps.showCreator", new String[] {"Do you want to show creator in warp list"});
    this.warpShowCreator = configReader.get("Warps.showCreator", false);
    configReader.addComment(
        "Warps.requirePerm",
        new String[] {
          "When set to true, new warps by default will require permission node to use them"
        });
    this.warpRequirePerm = configReader.get("Warps.requirePerm", false);
    String string = "Warps.Complex.CloseButton.";
    this.guiCloseButton = configReader.get(string + "Use", true);
    this.guiCloseButtonSlot = configReader.get(string + "Slot", 9) - 1;
    this.guiCloseButtonItem =
        CMILib.getInstance()
            .getItemManager()
            .getItem(
                configReader.get(
                    string + "Material",
                    "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNjYmY5ODgzZGQzNTlmZGYyMzg1YzkwYTQ1OWQ3Mzc3NjUzODJlYzQxMTdiMDQ4OTVhYzRkYzRiNjBmYyJ9fX0="));
    this.guiCloseButtonCommands = configReader.get(string + "Commands", Arrays.asList("closeinv!"));
    string = "Warps.Complex.InfoButton.";
    configReader.addComment(
        string + "Use",
        new String[] {
          "Extra button to be used in case you want to provide any aditional information when"
              + " clicking on it"
        });
    this.guiInfoButton = configReader.get(string + "Use", false);
    this.guiInfoButtonSlot = configReader.get(string + "Slot", 1) - 1;
    this.guiInfoButtonItem =
        CMILib.getInstance()
            .getItemManager()
            .getItem(
                configReader.get(
                    string + "Material",
                    "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDZiYTYzMzQ0ZjQ5ZGQxYzRmNTQ4OGU5MjZiZjNkOWUyYjI5OTE2YTZjNTBkNjEwYmI0MGE1MjczZGM4YzgyIn19fQ=="));
    this.guiInfoButtonCommands = configReader.get(string + "Commands", Arrays.asList("closeinv!"));
  }

  public boolean isWarpGUI() {
    return this.warpGUI;
  }

  public void load() {
    this.onDisable();
    CMI.getInstance()
        .safeRenameFile(this.fileName.toLowerCase(), "Saves" + File.separator + this.fileName);
    Long startTime = System.currentTimeMillis();
    File file = new File(this.plugin.getDataFolder(), "Saves" + File.separator + this.fileName);
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    } else {
      YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
      if (!yamlConfiguration.getKeys(false).isEmpty()) {
        this.warps.clear();
        var map = yamlConfiguration.getValues(false);
        Iterator iterator = map.entrySet().iterator();

        while (true) {
          CmiWarp cmiWarp;
          while (true) {
            if (!iterator.hasNext()) {
              this.plugin.loadMessage(
                  this.warps.size(), "warps", System.currentTimeMillis() - startTime);
              return;
            }

            Entry entry = (Entry) iterator.next();
            cmiWarp = this.getWarp((String) entry.getKey());
            if (cmiWarp == null) {
              cmiWarp = new CmiWarp((String) entry.getKey());
            } else {
              cmiWarp.reset();
            }

            try {
              var warpValues =
                  yamlConfiguration
                      .getConfigurationSection((String) entry.getKey())
                      .getValues(false);
              if (warpValues.containsKey("ReqPerm")) {
                cmiWarp.setReqPerm((Boolean) warpValues.get("ReqPerm"));
              }

              if (warpValues.containsKey("ShowWithNoPerm")) {
                cmiWarp.setShowUnavailable((Boolean) warpValues.get("ShowWithNoPerm"));
              }

              if (warpValues.containsKey("HideFromList")) {
                cmiWarp.setHidden((Boolean) warpValues.get("HideFromList"));
              }

              if (warpValues.containsKey("AutoLore")) {
                cmiWarp.setAutoLore((Boolean) warpValues.get("AutoLore"));
              }

              if (warpValues.containsKey("RandYaw")) {
                cmiWarp.setRandomizeYaw((Boolean) warpValues.get("RandYaw"));
              }

              try {
                if (warpValues.containsKey("Slot")) {
                  cmiWarp.setSlot((Integer) warpValues.get("Slot"));
                }
              } catch (Exception exception) {
              }

              try {
                cmiWarp.setSlot((Integer) warpValues.get("Slots") - 1);
              } catch (Exception exception2) {
              }

              if (warpValues.containsKey("Page")) {
                try {
                  cmiWarp.setPage((Integer) warpValues.get("Page"));
                } catch (Exception exception3) {
                }
              }

              if (warpValues.containsKey("Group")) {
                cmiWarp.setGroup((String) warpValues.get("Group"));
              }

              if (warpValues.containsKey("DisName")) {
                cmiWarp.setDisplayName((String) warpValues.get("DisName"));
              }

              try {
                if (warpValues.containsKey("Creator")) {
                  if (warpValues.get("Creator") instanceof UUID) {
                    cmiWarp.setCreator((UUID) warpValues.get("Creator"));
                  } else if (((String) warpValues.get("Creator")).length() == 36) {
                    CMIUser cmiUser =
                        this.plugin
                            .getPlayerManager()
                            .getUser(UUID.fromString((String) warpValues.get("Creator")));
                    if (cmiUser != null) {
                      cmiWarp.setCreator(cmiUser.getUniqueId());
                    } else {
                      cmiWarp.setCreator(UUID.fromString((String) warpValues.get("Creator")));
                    }
                  } else {
                    CMIUser ownerUser =
                        this.plugin.getPlayerManager().getUser((String) warpValues.get("Creator"));
                    if (ownerUser == null) {
                      cmiWarp.setCreator(this.plugin.getServerUUID());
                    } else {
                      cmiWarp.setCreator(ownerUser.getUniqueId());
                    }
                  }
                } else {
                  cmiWarp.setCreator(this.plugin.getServerUUID());
                }
              } catch (Exception exception4) {
              }

              CMILocation cmiLocation = null;
              if (yamlConfiguration.isConfigurationSection(cmiWarp.getName() + ".Location")) {
                cmiLocation =
                    this.plugin
                        .getPlayerManager()
                        .getLoc(yamlConfiguration, cmiWarp.getName() + ".Location.");
              } else if (yamlConfiguration.isString(cmiWarp.getName() + ".Location")) {
                cmiLocation =
                    this.plugin
                        .getPlayerManager()
                        .convertStringToLocation(
                            yamlConfiguration.getString(cmiWarp.getName() + ".Location"));
              }

              if (cmiLocation != null) {
                if (yamlConfiguration.isList(cmiWarp.getName() + ".sLocs")) {
                  cmiWarp.setLocations(
                      CMILocation.fromString(
                          yamlConfiguration.getStringList(cmiWarp.getName() + ".sLocs")));
                }

                if (yamlConfiguration.isBoolean(cmiWarp.getName() + ".Repeat")) {
                  cmiWarp.setRepeatingLocation((Boolean) warpValues.get("HideFromList"));
                }

                cmiWarp.setLoc(cmiLocation);
                if (warpValues.containsKey("Item")) {
                  try {
                    Util.fixHeadNameSection(
                        yamlConfiguration.getConfigurationSection(
                            (String) entry.getKey() + ".Item"));
                    ItemStack itemStack =
                        CMIPaperItemSerializator.getItem(
                            yamlConfiguration
                                .getConfigurationSection((String) entry.getKey())
                                .get("Item"));
                    cmiWarp.setItem(itemStack);
                  } catch (Exception exception5) {
                  }
                }

                if (warpValues.containsKey("OffItem")) {
                  try {
                    Util.fixHeadNameSection(
                        yamlConfiguration.getConfigurationSection(
                            (String) entry.getKey() + ".OffItem"));
                    ItemStack cursorItem =
                        CMIPaperItemSerializator.getItem(
                            yamlConfiguration
                                .getConfigurationSection((String) entry.getKey())
                                .get("OffItem"));
                    cmiWarp.setOffItem(cursorItem);
                  } catch (Exception exception6) {
                  }
                }
                break;
              }
            } catch (Exception exception7) {
              CMIMessages.sendMessage(
                  Bukkit.getConsoleSender(), "Cant load warp (" + (String) entry.getKey() + ")");
              exception7.printStackTrace();
            }
          }

          this.addWarp(cmiWarp, false);
        }
      }
    }
  }

  public void safeSave() {
    if (this.saveId == null) {
      this.saveId =
          CMIScheduler.runTaskLater(
              this.plugin,
              () -> {
                this.asyncSave();
                this.saveId = null;
              },
              20L);
    }
  }

  public void asyncSave() {
    if (!this.saving) {
      CMIScheduler.runTaskAsynchronously(
          this.plugin,
          () -> {
            this.saving = true;
            this.save();
            this.saving = false;
          });
    }
  }

  public void save() {
    if (this.saveId != null) {
      this.saveId.cancel();
      this.saveId = null;
    }

    try {
      File file = new File(this.plugin.getDataFolder(), "Saves" + File.separator + this.fileName);
      if (file.exists()) {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);

        for (String string : yamlConfiguration.getKeys(false)) {
          yamlConfiguration.set(string, null);
        }

        for (var entry : new HashMap<>(this.warps).entrySet()) {
          try {
            String warpKey = ((CmiWarp) entry.getValue()).getName().replace(".", "_");
            CmiWarp cmiWarp = (CmiWarp) entry.getValue();
            yamlConfiguration.set(
                warpKey + ".Location",
                this.plugin.getPlayerManager().convertLocToString(cmiWarp.getLoc()));
            if (cmiWarp.hasSecondaryLocations()) {
              yamlConfiguration.set(
                  warpKey + ".sLocs", CMILocation.toString(cmiWarp.getLocations()));
            }

            if (cmiWarp.isRepeatingLocation()) {
              yamlConfiguration.set(warpKey + ".Repeat", cmiWarp.isRepeatingLocation());
            }

            if (cmiWarp.getGroup() != null) {
              yamlConfiguration.set(warpKey + ".Group", cmiWarp.getGroup());
            }

            if (cmiWarp.getDisplayName() != null) {
              yamlConfiguration.set(warpKey + ".DisName", cmiWarp.getDisplayName());
            }

            if (cmiWarp.isReqPerm()) {
              yamlConfiguration.set(warpKey + ".ReqPerm", cmiWarp.isReqPerm());
            }

            if (cmiWarp.isShowUnavailable()) {
              yamlConfiguration.set(warpKey + ".ShowWithNoPerm", cmiWarp.isShowUnavailable());
            }

            if (cmiWarp.isHidden()) {
              yamlConfiguration.set(warpKey + ".HideFromList", cmiWarp.isHidden());
            }

            if (!cmiWarp.isAutoLore()) {
              yamlConfiguration.set(warpKey + ".AutoLore", cmiWarp.isAutoLore());
            }

            if (cmiWarp.isRandomizeYaw()) {
              yamlConfiguration.set(warpKey + ".RandYaw", cmiWarp.isRandomizeYaw());
            }

            yamlConfiguration.set(
                warpKey + ".Slots", cmiWarp.getSlot() == null ? null : cmiWarp.getSlot() + 1);
            yamlConfiguration.set(
                warpKey + ".Page", cmiWarp.getPage() == 1 ? null : cmiWarp.getPage());
            yamlConfiguration.set(
                warpKey + ".Creator",
                cmiWarp.getCreator() != null
                        && !cmiWarp.getCreator().equals(this.plugin.getServerUUID())
                    ? cmiWarp.getCreator().toString()
                    : null);
            yamlConfiguration.set(warpKey + ".Item", null);
            if (cmiWarp.getItem() != null) {
              CMIPaperItemSerializator.setTo(
                  yamlConfiguration, warpKey + ".Item", cmiWarp.getItem());
            }

            yamlConfiguration.set(warpKey + ".OffItem", null);
            if (cmiWarp.getOffItem() != null) {
              CMIPaperItemSerializator.setTo(
                  yamlConfiguration, warpKey + ".OffItem", cmiWarp.getOffItem());
            }
          } catch (Throwable throwable) {
            this.plugin.consoleMessage(
                "&cFailed to save " + ((CmiWarp) entry.getValue()).getName() + " warp into file");
            throwable.printStackTrace();
          }
        }

        try {
          yamlConfiguration.save(file);
        } catch (Throwable throwable2) {
          throwable2.printStackTrace();
        }

        this.saving = false;
        return;
      }

      try {
        file.createNewFile();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    } catch (Throwable throwable3) {
      throwable3.printStackTrace();
      return;
    } finally {
      this.saving = false;
    }
  }

  public int getWarpPerPage() {
    return this.warpPerPage;
  }

  public boolean isWarpShowCreator() {
    return this.warpShowCreator;
  }

  public boolean isGUIOnCreation() {
    return this.GUIOnCreation;
  }

  public int getMinLength() {
    return this.MinLength;
  }

  public void setMinLenght(int value) {
    this.MinLength = value;
  }

  public int getMaxLength() {
    return this.MaxLength;
  }

  public void setMaxLenght(int value) {
    this.MaxLength = value;
  }

  public boolean isWarpRequirePerm() {
    return this.warpRequirePerm;
  }

  private enum warpEditorSlots {
    icon(10),
    offIcon(19),
    randomYaw(20),
    autoLore(11),
    permission(13),
    reqPermission(22),
    hidden(32),
    group(25),
    displayName(34),
    iconSlot(14),
    page(23),
    back(8),
    location(37),
    seclocation(38),
    repeat(39);

    private int slot;

    warpEditorSlots(int value) {
      this.slot = value;
    }

    public int getSlot() {
      return this.slot;
    }

    public void setSlot(int value) {
      this.slot = value;
    }
  }
}
