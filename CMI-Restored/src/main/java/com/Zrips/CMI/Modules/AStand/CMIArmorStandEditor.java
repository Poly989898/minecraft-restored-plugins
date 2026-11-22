package com.Zrips.CMI.Modules.AStand;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIEvent;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.commands.list.armorstand;
import com.Zrips.CMI.events.CMIPlayerOpenArmorStandEditorEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Chat.ChatMessageEdit;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIAttribute;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Container.CommandType;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.CmiInventoryType;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.GUI.GUIManager.GUIRows;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Teleporters.CMITeleporter;
import net.Zrips.CMILib.Version.Version;
import net.Zrips.CMILib.commands.CMICommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class CMIArmorStandEditor {
  private static void addSaveButton(
      CMIGui cmiGui,
      int value,
      final ArmorStandManager.armorStandSaveOptions armorStandSaveOptions) {
    final ArmorStandSave armorStandSave = (ArmorStandSave) cmiGui.getWhatShows();
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(value) {
          public void click(GUIClickType guiClickType) {
            if (armorStandSave.getSaveOptions().contains(armorStandSaveOptions)) {
              armorStandSave.getSaveOptions().remove(armorStandSaveOptions);
            } else {
              armorStandSave.getSaveOptions().add(armorStandSaveOptions);
            }

            this.updateLooks();
            this.update();
          }

          public void updateLooks() {
            this.setItem(
                armorStandSave.getSaveOptions().contains(armorStandSaveOptions)
                    ? CMIMaterial.GREEN_WOOL.newItemStack()
                    : CMIMaterial.YELLOW_WOOL.newItemStack());
            this.setName(CMILC.getIM("armorstand", "action." + armorStandSaveOptions.toString()));
          }
        };
    cmiGui.addButton(cmiGuiButton);
  }

  private static void addCopyButton(
      CMIGui cmiGui, int value, final ArmorStandManager.armorStandActions armorStandActions) {
    final ArmorStandCopy armorStandCopy = (ArmorStandCopy) cmiGui.getWhatShows();
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(value) {
          public void click(GUIClickType guiClickType) {
            if (armorStandCopy.getCopy().contains(armorStandActions)) {
              armorStandCopy.getCopy().remove(armorStandActions);
            } else {
              armorStandCopy.getCopy().add(armorStandActions);
            }

            this.updateLooks();
            this.update();
          }

          public void updateLooks() {
            this.setItem(
                armorStandCopy.getCopy().contains(armorStandActions)
                    ? CMIMaterial.GREEN_WOOL.newItemStack()
                    : CMIMaterial.RED_WOOL.newItemStack());
            this.setName(CMILC.getIM("armorstand", "action." + armorStandActions.toString()));
          }
        };
    cmiGui.addButton(cmiGuiButton);
  }

  private static void addCopyButton(
      CMIGui cmiGui,
      int value,
      final ArmorStandManager.armorStandExtraActions armorStandExtraActions) {
    final ArmorStandCopy armorStandCopy = (ArmorStandCopy) cmiGui.getWhatShows();
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(value) {
          public void click(GUIClickType guiClickType) {
            if (armorStandCopy.getExtraCopy().contains(armorStandExtraActions)) {
              armorStandCopy.getExtraCopy().remove(armorStandExtraActions);
            } else {
              armorStandCopy.getExtraCopy().add(armorStandExtraActions);
            }

            this.updateLooks();
            this.update();
          }

          public void updateLooks() {
            this.setItem(
                armorStandCopy.getExtraCopy().contains(armorStandExtraActions)
                    ? CMIMaterial.GREEN_WOOL.newItemStack()
                    : CMIMaterial.RED_WOOL.newItemStack());
            this.setName(CMILC.getIM("armorstand", "action." + armorStandExtraActions.toString()));
          }
        };
    cmiGui.addButton(cmiGuiButton);
  }

  public static boolean openCopyWindow(final Player player, final ArmorStand armorStand) {
    if (!Version.isCurrentHigher(Version.v1_7_R4)) {
      return false;
    }

    if (armorStand.isDead()) {
      CMIUser.closeInventory(player);
      return false;
    }

    final ArmorStandCopy armorStandCopy =
        new ArmorStandCopy(
            player,
            armorStand,
            new HashSet<>(
                Arrays.asList(
                    ArmorStandManager.armorStandActions.head,
                    ArmorStandManager.armorStandActions.torso,
                    ArmorStandManager.armorStandActions.leftArm,
                    ArmorStandManager.armorStandActions.rightArm,
                    ArmorStandManager.armorStandActions.leftLeg,
                    ArmorStandManager.armorStandActions.rightLeg,
                    ArmorStandManager.armorStandActions.body)),
            new HashSet<>(
                Arrays.asList(
                    ArmorStandManager.armorStandExtraActions.size,
                    ArmorStandManager.armorStandExtraActions.glow,
                    ArmorStandManager.armorStandExtraActions.arms,
                    ArmorStandManager.armorStandExtraActions.plate,
                    ArmorStandManager.armorStandExtraActions.name,
                    ArmorStandManager.armorStandExtraActions.scale,
                    ArmorStandManager.armorStandExtraActions.interactable,
                    ArmorStandManager.armorStandExtraActions.invulnerable)));
    CMIGui cmiGui =
        new CMIGui(player) {
          public void onClose() {
            CMI.getInstance()
                .getArmorStandManager()
                .addEditor(player, armorStand)
                .setCopy(armorStandCopy);
            CMILC.info(armorstand.class, player, "click");
          }
        };
    cmiGui.setTitle(CMILC.getIM(armorstand.class, "copyTitle"));
    cmiGui.setWhatShows(armorStandCopy);
    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandActions.body.name())) {
      addCopyButton(cmiGui, 19, ArmorStandManager.armorStandActions.body);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandActions.head.name())) {
      addCopyButton(cmiGui, 20, ArmorStandManager.armorStandActions.head);
    } else {
      armorStandCopy.getCopy().remove(ArmorStandManager.armorStandActions.head);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandActions.torso.name())) {
      addCopyButton(cmiGui, 21, ArmorStandManager.armorStandActions.torso);
    } else {
      armorStandCopy.getCopy().remove(ArmorStandManager.armorStandActions.torso);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandActions.leftArm.name())) {
      addCopyButton(cmiGui, 22, ArmorStandManager.armorStandActions.leftArm);
    } else {
      armorStandCopy.getCopy().remove(ArmorStandManager.armorStandActions.leftArm);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandActions.rightArm.name())) {
      addCopyButton(cmiGui, 23, ArmorStandManager.armorStandActions.rightArm);
    } else {
      armorStandCopy.getCopy().remove(ArmorStandManager.armorStandActions.rightArm);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandActions.leftLeg.name())) {
      addCopyButton(cmiGui, 24, ArmorStandManager.armorStandActions.leftLeg);
    } else {
      armorStandCopy.getCopy().remove(ArmorStandManager.armorStandActions.leftLeg);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandActions.rightLeg.name())) {
      addCopyButton(cmiGui, 25, ArmorStandManager.armorStandActions.rightLeg);
    } else {
      armorStandCopy.getCopy().remove(ArmorStandManager.armorStandActions.rightLeg);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandExtraActions.arms.name())) {
      addCopyButton(cmiGui, 28, ArmorStandManager.armorStandExtraActions.arms);
    } else {
      armorStandCopy.getExtraCopy().remove(ArmorStandManager.armorStandExtraActions.arms);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandExtraActions.glow.name())) {
      addCopyButton(cmiGui, 29, ArmorStandManager.armorStandExtraActions.glow);
    } else {
      armorStandCopy.getExtraCopy().remove(ArmorStandManager.armorStandExtraActions.glow);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandExtraActions.plate.name())) {
      addCopyButton(cmiGui, 30, ArmorStandManager.armorStandExtraActions.plate);
    } else {
      armorStandCopy.getExtraCopy().remove(ArmorStandManager.armorStandExtraActions.plate);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandExtraActions.size.name())) {
      addCopyButton(cmiGui, 31, ArmorStandManager.armorStandExtraActions.size);
    } else {
      armorStandCopy.getExtraCopy().remove(ArmorStandManager.armorStandExtraActions.size);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandExtraActions.name.name())) {
      addCopyButton(cmiGui, 32, ArmorStandManager.armorStandExtraActions.name);
    } else {
      armorStandCopy.getExtraCopy().remove(ArmorStandManager.armorStandExtraActions.name);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandExtraActions.interactable.name())) {
      addCopyButton(cmiGui, 33, ArmorStandManager.armorStandExtraActions.interactable);
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)
        && PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
            player, ArmorStandManager.armorStandExtraActions.scale.name())) {
      addCopyButton(cmiGui, 41, ArmorStandManager.armorStandExtraActions.scale);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandExtraActions.invulnerable.name())) {
      addCopyButton(cmiGui, 34, ArmorStandManager.armorStandExtraActions.invulnerable);
    }

    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(
            53,
            CMILib.getInstance()
                .getItemManager()
                .getItem(
                    "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNjYmY5ODgzZGQzNTlmZGYyMzg1YzkwYTQ1OWQ3Mzc3NjUzODJlYzQxMTdiMDQ4OTVhYzRkYzRiNjBmYyJ9fX0=")) {
          public void click(GUIClickType guiClickType) {
            CMIArmorStandEditor.openEditorInternal(player, armorStand, false);
          }
        };
    cmiGuiButton.setName(LC.info_Back.getLocale(new Object[0]));
    cmiGui.addButton(cmiGuiButton);
    cmiGui.setCmiInventoryType(CmiInventoryType.ArmorStandCopy);
    cmiGui.setInvSize(GUIRows.r6);
    cmiGui.fillEmptyButtons();
    cmiGui.open();
    return true;
  }

  public static boolean openSaveWindow(final Player player, final Entity entity) {
    if (!Version.isCurrentHigher(Version.v1_7_R4)) {
      return false;
    }

    if (!entity.getType().equals(EntityType.ARMOR_STAND)) {
      return false;
    }

    if (entity.getCustomName() != null && entity.getCustomName().equals("CMIArmorStandForSit")) {
      return false;
    }

    Integer integer = new CMINBT(entity).getInt("DisabledSlots");
    if (integer != null && integer == 2096896) {
      return false;
    }

    ArmorStand armorStand = (ArmorStand) entity;
    ArmorStandSave armorStandSave =
        new ArmorStandSave(
            player,
            armorStand,
            new HashSet<>(Arrays.asList(ArmorStandManager.armorStandSaveOptions.values())));
    armorStandSave.getSaveOptions().remove(ArmorStandManager.armorStandSaveOptions.body);
    final CMIGui cmiGui =
        new CMIGui(player) {
          public void onClose() {
            CMI.getInstance().getArmorStandManager().removeEditor(player);
          }
        };
    cmiGui.setTitle(CMILC.getIM(armorstand.class, "saveTitle"));
    cmiGui.setWhatShows(armorStandSave);
    addSaveButton(cmiGui, 10, ArmorStandManager.armorStandSaveOptions.helmet);
    addSaveButton(cmiGui, 11, ArmorStandManager.armorStandSaveOptions.chest);
    addSaveButton(cmiGui, 12, ArmorStandManager.armorStandSaveOptions.offhand);
    addSaveButton(cmiGui, 13, ArmorStandManager.armorStandSaveOptions.mainhand);
    addSaveButton(cmiGui, 14, ArmorStandManager.armorStandSaveOptions.leggings);
    addSaveButton(cmiGui, 15, ArmorStandManager.armorStandSaveOptions.boots);
    addSaveButton(cmiGui, 16, ArmorStandManager.armorStandSaveOptions.name);
    addSaveButton(cmiGui, 22, ArmorStandManager.armorStandSaveOptions.visible);
    addSaveButton(cmiGui, 28, ArmorStandManager.armorStandSaveOptions.body);
    addSaveButton(cmiGui, 29, ArmorStandManager.armorStandSaveOptions.head);
    addSaveButton(cmiGui, 30, ArmorStandManager.armorStandSaveOptions.torso);
    addSaveButton(cmiGui, 31, ArmorStandManager.armorStandSaveOptions.leftArm);
    addSaveButton(cmiGui, 32, ArmorStandManager.armorStandSaveOptions.rightArm);
    addSaveButton(cmiGui, 33, ArmorStandManager.armorStandSaveOptions.leftLeg);
    addSaveButton(cmiGui, 34, ArmorStandManager.armorStandSaveOptions.rightLeg);
    addSaveButton(cmiGui, 37, ArmorStandManager.armorStandSaveOptions.arms);
    addSaveButton(cmiGui, 38, ArmorStandManager.armorStandSaveOptions.glow);
    addSaveButton(cmiGui, 39, ArmorStandManager.armorStandSaveOptions.plate);
    addSaveButton(cmiGui, 40, ArmorStandManager.armorStandSaveOptions.size);
    addSaveButton(cmiGui, 41, ArmorStandManager.armorStandSaveOptions.name);
    addSaveButton(cmiGui, 42, ArmorStandManager.armorStandSaveOptions.interactable);
    addSaveButton(cmiGui, 43, ArmorStandManager.armorStandSaveOptions.invulnerable);
    addSaveButton(cmiGui, 44, ArmorStandManager.armorStandSaveOptions.scale);
    addFinalSaveButton(cmiGui, 48);
    addFinalSaveButton(cmiGui, 49);
    addFinalSaveButton(cmiGui, 50);
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(
            53,
            CMILib.getInstance()
                .getItemManager()
                .getItem(
                    "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNjYmY5ODgzZGQzNTlmZGYyMzg1YzkwYTQ1OWQ3Mzc3NjUzODJlYzQxMTdiMDQ4OTVhYzRkYzRiNjBmYyJ9fX0=")) {
          public void click(GUIClickType guiClickType) {
            CMIArmorStandEditor.openEditorInternal(player, entity, false);
          }
        };
    cmiGuiButton.setName(LC.info_Back.getLocale(new Object[0]));
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(
            45,
            CMILib.getInstance()
                .getItemManager()
                .getItem(
                    "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDZiYTYzMzQ0ZjQ5ZGQxYzRmNTQ4OGU5MjZiZjNkOWUyYjI5OTE2YTZjNTBkNjEwYmI0MGE1MjczZGM4YzgyIn19fQ==")) {
          public void click(GUIClickType clickType) {
            cmiGui.getPlayer().closeInventory();
            Bukkit.dispatchCommand(
                cmiGui.getPlayer(),
                CommandsHandler.getLabel() + " " + armorstand.class.getSimpleName() + " list");
          }
        };
    cmiGuiButton.setName("...");
    cmiGui.addButton(cmiGuiButton);
    cmiGui.setInvSize(GUIRows.r6);
    cmiGui.fillEmptyButtons();
    cmiGui.open();
    return true;
  }

  private static void addFinalSaveButton(final CMIGui cmiGui, int value) {
    final ArmorStandSave armorStandSave = (ArmorStandSave) cmiGui.getWhatShows();
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(value) {
          public void click(GUIClickType guiClickType) {
            ChatMessageEdit chatMessageEdit =
                new ChatMessageEdit(cmiGui.getPlayer()) {
                  public void run(String string) {
                    string = CMIChatColor.stripColor(CMIText.getFirstVariable(string));
                    if (CMIText.isValidString(string)) {
                      if (CMI.getInstance().getArmorStandManager().getSavedArmorStand(string)
                          != null) {
                        LC.info_alreadyexist.sendMessage(cmiGui.getPlayer(), new Object[0]);
                      } else {
                        String text = string;
                        CMIScheduler.runAtLocation(
                            CMI.getInstance(),
                            armorStandSave.getStand().getLocation(),
                            () -> {
                              armorStandSave.setSerialized(armorStandSave.serialize());
                              CMI.getInstance().getArmorStandManager().save();
                            });
                        armorStandSave.setName(text);
                        CMI.getInstance().getArmorStandManager().addSavedArmorStand(armorStandSave);
                        ItemStack itemStack = armorStandSave.createItem();
                        CMIUser cmiUser =
                            CMI.getInstance().getPlayerManager().getUser(cmiGui.getPlayer());
                        var hashMap =
                            cmiUser
                                .getInventory()
                                .addItem(
                                    itemStack, CMIPlayerInventory.CMIFullInventoryAction.Abort);
                        if (hashMap.isEmpty()) {
                          CMILC.info(armorstand.class, cmiGui.getPlayer(), "receivedItem");
                        } else {
                          LC.info_Inventory_Full.sendMessage(cmiGui.getPlayer(), new Object[0]);
                        }

                        CMICommand.performCommand(
                            cmiGui.getPlayer(),
                            CommandsHandler.getLabel()
                                + " "
                                + armorstand.class.getSimpleName()
                                + " list",
                            CommandType.acmd);
                      }
                    }
                  }

                  public void onDisable() {}
                };
            chatMessageEdit.setCheckForCancel(true);
            CMIMessages.sendMessage(
                cmiGui.getPlayer(), LC.modify_nameAddInfo.getLocale(new Object[0]));
            cmiGui.getPlayer().closeInventory();
          }

          public void updateLooks() {
            this.setItem(CMIMaterial.ORANGE_WOOL.newItemStack());
            this.setName(CMILC.getIM(armorstand.class, "saveButton"));
          }
        };
    cmiGui.addButton(cmiGuiButton);
  }

  public static boolean openEditor(Player player, Entity entity, boolean flag) {
    if (!Version.isCurrentHigher(Version.v1_7_R4)) {
      return false;
    } else if (!entity.getType().equals(EntityType.ARMOR_STAND)) {
      return false;
    } else {
      return CMI.getInstance().getArmorStandManager().isBeingEdited(entity.getUniqueId())
          ? false
          : openEditorInternal(player, entity, flag);
    }
  }

  private static boolean openEditorInternal(Player player, Entity entity, boolean flag) {
    if (entity.getCustomName() != null && entity.getCustomName().equals("CMIArmorStandForSit")) {
      return false;
    }

    Integer integer = new CMINBT(entity).getInt("DisabledSlots");
    if (integer != null && integer == 2096896) {
      return false;
    }

    CMIArmorStandEdit cmiArmorStandEdit =
        CMI.getInstance().getArmorStandManager().getEditor(player);
    if (cmiArmorStandEdit != null) {
      cmiArmorStandEdit.setCopy(null);
    }

    if (CMI.getInstance().getInteractiveCommandManager().getByUUID(entity.getUniqueId()) == null
        && !CMI.getInstance().getArmorStandManager().canInteract(player, entity, flag)) {
      return false;
    }

    if (entity.isDead()) {
      player.closeInventory();
      return false;
    }

    ArmorStand armorStand = (ArmorStand) entity;
    CMIPlayerOpenArmorStandEditorEvent cmiPlayerOpenArmorStandEditorEvent =
        new CMIPlayerOpenArmorStandEditorEvent(player, armorStand);
    Bukkit.getPluginManager().callEvent(cmiPlayerOpenArmorStandEditorEvent);
    if (cmiPlayerOpenArmorStandEditorEvent.isCancelled()) {
      return false;
    }

    CMIScheduler.runAtEntity(
        CMI.getInstance(),
        entity,
        () -> {
          openDelayedEditor(player, entity);
          CMI.getInstance().getArmorStandManager().addEditor(player, entity);
        });
    return true;
  }

  public static boolean isOk(CMIGui cmiGui) {
    if (!(cmiGui.getWhatShows() instanceof Entity)) {
      return false;
    } else if (CMI.getInstance()
        .getArmorStandManager()
        .isTooFar(cmiGui.getPlayer(), (Entity) cmiGui.getWhatShows())) {
      CMILC.info(armorstand.class, cmiGui.getPlayer(), "tooFar");
      cmiGui.getPlayer().closeInventory();
      return false;
    } else {
      Entity entity = (Entity) cmiGui.getWhatShows();
      if (!entity.getType().equals(EntityType.ARMOR_STAND)) {
        return false;
      } else {
        ArmorStand armorStand = (ArmorStand) entity;
        if (armorStand.isDead()) {
          cmiGui.getPlayer().closeInventory();
          return false;
        } else {
          return cmiGui
              .getPlayer()
              .getUniqueId()
              .equals(
                  CMI.getInstance()
                      .getArmorStandManager()
                      .getEditorOf(((Entity) cmiGui.getWhatShows()).getUniqueId()));
        }
      }
    }
  }

  private static void openDelayedEditor(final Player player, final Entity entity) {
    final ArmorStand armorStand = (ArmorStand) entity;
    final CMIGui cmiGui =
        new CMIGui(player) {
          public void onClose() {
            CMI.getInstance().getArmorStandManager().removeEditor(player);
          }

          public boolean click(int value, GUIClickType guiClickType, ItemStack itemStack) {
            return CMIArmorStandEditor.isOk(this);
          }
        };
    cmiGui.setWhatShows(entity);
    cmiGui.setCmiInventoryType(CmiInventoryType.ArmorStandEditor);
    cmiGui.setInvSize(GUIRows.r6);
    cmiGui.setTitle(CMILC.getIM(armorstand.class, "title"));
    CMIGuiButton cmiGuiButton = null;
    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandExtraActions.updateitems.name())) {
      cmiGuiButton =
          new CMIGuiButton(ArmorStandManager.armorStandEditorSlots.helmet.getSlot()) {
            public void click(GUIClickType clickType) {
              if (CMIArmorStandEditor.isOk(cmiGui)) {
                CMIScheduler.runAtEntity(
                    CMI.getInstance(),
                    entity,
                    () -> CMIArmorStandEditor.updateArmorStandItems(cmiGui));
              }
            }
          };
      cmiGuiButton.unlockField();
      cmiGuiButton.setItem(armorStand.getHelmet());
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(ArmorStandManager.armorStandEditorSlots.chest.getSlot()) {
            public void click(GUIClickType clickType) {
              if (CMIArmorStandEditor.isOk(cmiGui)) {
                CMIScheduler.runAtEntity(
                    CMI.getInstance(),
                    entity,
                    () -> CMIArmorStandEditor.updateArmorStandItems(cmiGui));
              }
            }
          };
      cmiGuiButton.unlockField();
      cmiGuiButton.setItem(armorStand.getChestplate());
      cmiGui.addButton(cmiGuiButton);
      if (Version.isCurrentHigher(Version.v1_8_R3)) {
        LivingEntity livingEntity = (LivingEntity) entity;
        if (armorStand.hasArms() || livingEntity.getEquipment().getItemInOffHand() != null) {
          cmiGuiButton =
              new CMIGuiButton(ArmorStandManager.armorStandEditorSlots.offhand.getSlot()) {
                public void click(GUIClickType clickType) {
                  if (CMIArmorStandEditor.isOk(cmiGui)) {
                    CMIScheduler.runAtEntity(
                        CMI.getInstance(),
                        entity,
                        () -> CMIArmorStandEditor.updateArmorStandItems(cmiGui));
                  }
                }
              };
          cmiGuiButton.unlockField();
          cmiGuiButton.setItem(livingEntity.getEquipment().getItemInOffHand().clone());
          cmiGui.addButton(cmiGuiButton);
        }
      }

      if (armorStand.hasArms() || armorStand.getItemInHand() != null) {
        cmiGuiButton =
            new CMIGuiButton(ArmorStandManager.armorStandEditorSlots.mainhand.getSlot()) {
              public void click(GUIClickType clickType) {
                if (CMIArmorStandEditor.isOk(cmiGui)) {
                  CMIScheduler.runAtEntity(
                      CMI.getInstance(),
                      entity,
                      () -> CMIArmorStandEditor.updateArmorStandItems(cmiGui));
                }
              }
            };
        cmiGuiButton.unlockField();
        cmiGuiButton.setItem(armorStand.getItemInHand().clone());
        cmiGui.addButton(cmiGuiButton);
      }

      cmiGuiButton =
          new CMIGuiButton(ArmorStandManager.armorStandEditorSlots.leggings.getSlot()) {
            public void click(GUIClickType clickType) {
              if (CMIArmorStandEditor.isOk(cmiGui)) {
                CMIScheduler.runAtEntity(
                    CMI.getInstance(),
                    entity,
                    () -> CMIArmorStandEditor.updateArmorStandItems(cmiGui));
              }
            }
          };
      cmiGuiButton.unlockField();
      cmiGuiButton.setItem(armorStand.getLeggings().clone());
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(ArmorStandManager.armorStandEditorSlots.boots.getSlot()) {
            public void click(GUIClickType clickType) {
              if (CMIArmorStandEditor.isOk(cmiGui)) {
                CMIScheduler.runAtEntity(
                    CMI.getInstance(),
                    entity,
                    () -> CMIArmorStandEditor.updateArmorStandItems(cmiGui));
              }
            }
          };
      cmiGuiButton.unlockField();
      cmiGuiButton.setItem(armorStand.getBoots().clone());
      cmiGui.addButton(cmiGuiButton);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandExtraActions.plate.name())) {
      cmiGuiButton =
          new CMIGuiButton(
              17, armorStand.hasBasePlate() ? CMIMaterial.GREEN_WOOL : CMIMaterial.RED_WOOL) {
            public void click(GUIClickType clickType) {
              if (CMIArmorStandEditor.isOk(cmiGui)) {
                Entity shownEntity = (Entity) cmiGui.getWhatShows();
                ArmorStand armorStand = (ArmorStand) shownEntity;
                CMIScheduler.runAtEntity(
                    CMI.getInstance(),
                    shownEntity,
                    () -> {
                      armorStand.setBasePlate(!armorStand.hasBasePlate());
                      this.updateLooks();
                      this.update();
                    });
              }
            }

            public void updateLooks() {
              this.setItem(
                  armorStand.hasBasePlate()
                      ? CMIMaterial.GREEN_WOOL.newItemStack()
                      : CMIMaterial.RED_WOOL.newItemStack());
              this.setName(
                  CMILC.getIM("armorstand", ArmorStandManager.armorStandExtraActions.plate.name()));
            }
          };
      cmiGui.addButton(cmiGuiButton);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandExtraActions.size.name())) {
      cmiGuiButton =
          new CMIGuiButton(
              6, armorStand.isSmall() ? CMIMaterial.GREEN_WOOL : CMIMaterial.RED_WOOL) {
            public void click(GUIClickType clickType) {
              if (CMIArmorStandEditor.isOk(cmiGui)) {
                Entity shownEntity = (Entity) cmiGui.getWhatShows();
                ArmorStand armorStand = (ArmorStand) shownEntity;
                CMIScheduler.runAtEntity(
                    CMI.getInstance(),
                    shownEntity,
                    () -> {
                      armorStand.setSmall(!armorStand.isSmall());
                      this.updateLooks();
                      this.update();
                    });
              }
            }

            public void updateLooks() {
              this.setItem(
                  armorStand.isSmall()
                      ? CMIMaterial.GREEN_WOOL.newItemStack()
                      : CMIMaterial.RED_WOOL.newItemStack());
              this.setName(
                  CMILC.getIM("armorstand", ArmorStandManager.armorStandExtraActions.size.name()));
            }
          };
      cmiGui.addButton(cmiGuiButton);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandExtraActions.visible.name())) {
      cmiGuiButton =
          new CMIGuiButton(
              5, armorStand.isVisible() ? CMIMaterial.GREEN_WOOL : CMIMaterial.RED_WOOL) {
            public void click(GUIClickType clickType) {
              if (CMIArmorStandEditor.isOk(cmiGui)) {
                Entity shownEntity = (Entity) cmiGui.getWhatShows();
                ArmorStand armorStand = (ArmorStand) shownEntity;
                CMIScheduler.runAtEntity(
                    CMI.getInstance(),
                    shownEntity,
                    () -> {
                      if ((!Version.isCurrentHigher(Version.v1_12_R1) || !armorStand.isInvisible())
                          && armorStand.isVisible()) {
                        if (Version.isCurrentHigher(Version.v1_12_R1)) {
                          armorStand.setInvisible(true);
                        } else {
                          armorStand.setVisible(false);
                        }
                      } else if (Version.isCurrentHigher(Version.v1_12_R1)) {
                        armorStand.setInvisible(false);
                      } else {
                        armorStand.setVisible(true);
                      }

                      this.updateLooks();
                      this.update();
                    });
              }
            }

            public void updateLooks() {
              this.setItem(
                  armorStand.isVisible()
                      ? CMIMaterial.GREEN_WOOL.newItemStack()
                      : CMIMaterial.RED_WOOL.newItemStack());
              this.setName(
                  CMILC.getIM(
                      "armorstand", ArmorStandManager.armorStandExtraActions.visible.name()));
            }
          };
      cmiGui.addButton(cmiGuiButton);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandExtraActions.gravity.name())) {
      cmiGuiButton =
          new CMIGuiButton(
              7, armorStand.hasGravity() ? CMIMaterial.GREEN_WOOL : CMIMaterial.RED_WOOL) {
            public void click(GUIClickType clickType) {
              if (CMIArmorStandEditor.isOk(cmiGui)) {
                Entity shownEntity = (Entity) cmiGui.getWhatShows();
                CMIScheduler.runAtEntity(
                    CMI.getInstance(),
                    shownEntity,
                    () -> {
                      ArmorStand armorStand = (ArmorStand) shownEntity;
                      armorStand.setGravity(!armorStand.hasGravity());
                      this.updateLooks();
                      this.update();
                      CMIGuiButton actionButton = (CMIGuiButton) cmiGui.getButtons().get(44);
                      if (actionButton != null) {
                        actionButton.updateLooks();
                        actionButton.update();
                      }
                    });
              }
            }

            public void updateLooks() {
              this.setItem(
                  armorStand.hasGravity()
                      ? CMIMaterial.GREEN_WOOL.newItemStack()
                      : CMIMaterial.RED_WOOL.newItemStack());
              this.setName(
                  CMILC.getIM(
                      "armorstand", ArmorStandManager.armorStandExtraActions.gravity.name()));
            }
          };
      cmiGui.addButton(cmiGuiButton);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandExtraActions.arms.name())) {
      cmiGuiButton =
          new CMIGuiButton(8, CMIMaterial.STONE) {
            public void click(GUIClickType clickType) {
              if (CMIArmorStandEditor.isOk(cmiGui)) {
                Entity shownEntity = (Entity) cmiGui.getWhatShows();
                ArmorStand armorStand = (ArmorStand) shownEntity;
                CMIScheduler.runAtEntity(
                    CMI.getInstance(),
                    shownEntity,
                    () -> {
                      armorStand.setArms(!armorStand.hasArms());
                      this.updateLooks();
                      this.update();
                    });
              }
            }

            public void updateLooks() {
              this.setItem(
                  armorStand.hasArms()
                      ? CMIMaterial.GREEN_WOOL.newItemStack()
                      : CMIMaterial.RED_WOOL.newItemStack());
              this.setName(
                  CMILC.getIM("armorstand", ArmorStandManager.armorStandExtraActions.arms.name()));
            }
          };
      cmiGui.addButton(cmiGuiButton);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandExtraActions.name.name())) {
      cmiGuiButton =
          new CMIGuiButton(16, CMIMaterial.STONE) {
            public void click(GUIClickType clickType) {
              if (CMIArmorStandEditor.isOk(cmiGui)) {
                final Entity shownEntity = (Entity) cmiGui.getWhatShows();
                if (clickType != GUIClickType.MiddleMouse && clickType != GUIClickType.Q) {
                  ArmorStand armorStand = (ArmorStand) shownEntity;
                  armorStand.setCustomNameVisible(!armorStand.isCustomNameVisible());
                  this.updateLooks();
                  this.update();
                } else if (PermissionsManager.CMIPerm.command_armorstand_renaming.hasPermission(
                    player)) {
                  ChatMessageEdit chatMessageEdit =
                      new ChatMessageEdit(
                          player,
                          shownEntity.getCustomName() == null
                              ? (shownEntity.getName() == null
                                  ? CMIEntityType.getRealNameByType(shownEntity.getType())
                                  : shownEntity.getName())
                              : shownEntity.getCustomName()) {
                        public void run(String string) {
                          CMIScheduler.runAtLocation(
                              CMI.getInstance(),
                              shownEntity.getLocation(),
                              () -> shownEntity.setCustomName(CMIChatColor.translate(string)));
                          CMIArmorStandEditor.openEditorInternal(player, shownEntity, false);
                        }

                        public void onDisable() {
                          CMIArmorStandEditor.openEditorInternal(player, shownEntity, false);
                        }
                      };
                  chatMessageEdit.setCheckForCancel(true);
                  chatMessageEdit.printMessage();
                  player.closeInventory();
                }
              }
            }

            public void updateLooks() {
              this.setItem(
                  armorStand.isCustomNameVisible()
                      ? CMIMaterial.GREEN_WOOL.newItemStack()
                      : CMIMaterial.RED_WOOL.newItemStack());
              this.setName(
                  CMILC.getIM("armorstand", ArmorStandManager.armorStandExtraActions.name.name()));
              if (PermissionsManager.CMIPerm.command_armorstand_renaming.hasPermission(player)) {
                this.clearLore();
                this.addLore(CMILC.getIM("armorstand", "nameEdit"));
              }
            }
          };
      cmiGui.addButton(cmiGuiButton);
    }

    try {
      if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
          player, ArmorStandManager.armorStandExtraActions.glow.name())) {
        cmiGuiButton =
            new CMIGuiButton(15, CMIMaterial.STONE) {
              public void click(GUIClickType clickType) {
                if (CMIArmorStandEditor.isOk(cmiGui)) {
                  Entity shownEntity = (Entity) cmiGui.getWhatShows();
                  ArmorStand armorStand = (ArmorStand) shownEntity;
                  armorStand.setGlowing(!armorStand.isGlowing());
                  this.updateLooks();
                  this.update();
                }
              }

              public void updateLooks() {
                this.setItem(
                    armorStand.isGlowing()
                        ? CMIMaterial.GREEN_WOOL.newItemStack()
                        : CMIMaterial.RED_WOOL.newItemStack());
                this.setName(
                    CMILC.getIM(
                        "armorstand", ArmorStandManager.armorStandExtraActions.glow.name()));
              }
            };
        cmiGui.addButton(cmiGuiButton);
      }
    } catch (NoSuchMethodError noSuchMethodError) {
    }

    try {
      if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
          player, ArmorStandManager.armorStandExtraActions.invulnerable.name())) {
        cmiGuiButton =
            new CMIGuiButton(14, CMIMaterial.STONE) {
              public void click(GUIClickType clickType) {
                if (CMIArmorStandEditor.isOk(cmiGui)) {
                  Entity shownEntity = (Entity) cmiGui.getWhatShows();
                  ArmorStand armorStand = (ArmorStand) shownEntity;
                  armorStand.setInvulnerable(!armorStand.isInvulnerable());
                  this.updateLooks();
                  this.update();
                }
              }

              public void updateLooks() {
                this.setItem(
                    armorStand.isInvulnerable()
                        ? CMIMaterial.GREEN_WOOL.newItemStack()
                        : CMIMaterial.RED_WOOL.newItemStack());
                this.setName(
                    CMILC.getIM(
                        "armorstand",
                        ArmorStandManager.armorStandExtraActions.invulnerable.name()));
              }
            };
        cmiGui.addButton(cmiGuiButton);
      }
    } catch (NoSuchMethodError noSuchMethodError2) {
    }

    try {
      if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
          player, ArmorStandManager.armorStandExtraActions.interactable.name())) {
        cmiGuiButton =
            new CMIGuiButton(26, CMIMaterial.STONE) {
              public void click(GUIClickType clickType) {
                if (CMIArmorStandEditor.isOk(cmiGui)) {
                  Entity shownEntity = (Entity) cmiGui.getWhatShows();
                  ArmorStand armorStand = (ArmorStand) shownEntity;
                  armorStand.setMarker(!armorStand.isMarker());
                  this.updateLooks();
                  this.update();
                }
              }

              public void updateLooks() {
                this.setItem(
                    !armorStand.isMarker()
                        ? CMIMaterial.GREEN_WOOL.newItemStack()
                        : CMIMaterial.RED_WOOL.newItemStack());
                this.setName(
                    CMILC.getIM(
                        "armorstand",
                        ArmorStandManager.armorStandExtraActions.interactable.name()));
              }
            };
        cmiGui.addButton(cmiGuiButton);
      }
    } catch (NoSuchMethodError noSuchMethodError3) {
    }

    final double number = ArmorStandManager.getArmorStandsMaxScale();
    final double minScale = ArmorStandManager.getArmorStandsMinScale();

    try {
      if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)
          && PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
              player, ArmorStandManager.armorStandExtraActions.scale.name())) {
        cmiGuiButton =
            new CMIGuiButton(24, CMIMaterial.STONE) {
              public void click(GUIClickType clickType) {
                if (CMIArmorStandEditor.isOk(cmiGui)) {
                  Entity shownEntity = (Entity) cmiGui.getWhatShows();
                  double scale = CMIAttribute.SCALE.getValue(shownEntity);
                  switch (clickType) {
                    case Unknown:
                    case QControl:
                    default:
                      break;
                    case Left:
                      scale = CMINumber.sum(scale, 0.01, 3);
                      break;
                    case LeftShift:
                      scale = CMINumber.sum(scale, 0.1);
                      break;
                    case Right:
                      scale = CMINumber.sum(scale, -0.01, 3);
                      break;
                    case RightShift:
                      scale = CMINumber.sum(scale, -0.1);
                      break;
                    case MiddleMouse:
                    case Q:
                      scale = 1.0;
                  }

                  PermissionInfo permissionInfo =
                      PermissionsManager.CMIPerm.command_armorstand_scalemax_$1.getPermissionInfo(
                          player, "");
                  scale =
                      CMINumber.clamp(
                          scale,
                          0.0625,
                          permissionInfo == null
                              ? number
                              : Math.max(number, permissionInfo.getMaxValue(number)));
                  PermissionInfo scalePermission =
                      PermissionsManager.CMIPerm.command_armorstand_scalemin_$1.getPermissionInfo(
                          player, "");
                  scale =
                      CMINumber.clamp(
                          scale,
                          scalePermission == null
                              ? minScale
                              : Math.min(minScale, scalePermission.getMinValue(minScale)),
                          100);
                  scale = CMINumber.clamp(scale, 0.0625, 100);
                  CMIAttribute.SCALE.setBaseValue(shownEntity, scale);
                  this.updateLooks();
                  this.update();
                }
              }

              public void updateLooks() {
                double scaleValue = CMIAttribute.SCALE.getValue(entity);
                this.setItem(
                    scaleValue == 1.0
                        ? CMIMaterial.GREEN_WOOL.newItemStack()
                        : CMIMaterial.YELLOW_WOOL.newItemStack());
                this.setName(
                    CMILC.getIM(
                        armorstand.class,
                        ArmorStandManager.armorStandExtraActions.scale.name(),
                        "[amount]",
                        scaleValue));
                this.clearLore();
                PermissionInfo maxPermission =
                    PermissionsManager.CMIPerm.command_armorstand_scalemin_$1.getPermissionInfo(
                        player, "");
                double targetScale =
                    maxPermission == null
                        ? minScale
                        : Math.min(minScale, maxPermission.getMinValue(minScale));
                PermissionInfo minPermission =
                    PermissionsManager.CMIPerm.command_armorstand_scalemax_$1.getPermissionInfo(
                        player, "");
                double scaleDelta =
                    minPermission == null
                        ? number
                        : Math.max(number, minPermission.getMaxValue(number));
                this.addLore(
                    CMILC.getIM(
                        armorstand.class,
                        "scaleLimits",
                        "[min]",
                        targetScale,
                        "[max]",
                        scaleDelta));
              }
            };
        cmiGui.addButton(cmiGuiButton);
      }
    } catch (NoSuchMethodError noSuchMethodError4) {
    }

    boolean flag = hasActionAccess(player);
    boolean hasExtraAccess = hasExtraActionAccess(player);
    if (flag || hasExtraAccess) {
      cmiGuiButton =
          new CMIGuiButton(41, CMIMaterial.PAPER) {
            public void click(GUIClickType clickType) {
              CMIArmorStandEditor.openCopyWindow(player, armorStand);
            }

            public void updateLooks() {
              this.setName(CMILC.getIM(armorstand.class, "copyLabel"));
            }
          };
      cmiGui.addButton(cmiGuiButton);
    }

    if (flag) {
      cmiGuiButton =
          new CMIGuiButton(33, CMIMaterial.COMPASS) {
            public void click(GUIClickType clickType) {
              CMIArmorStandEditor.openPositionEditor(player, entity);
            }

            public void updateLooks() {
              this.setName(CMILC.getIM(armorstand.class, "positionLabel"));
            }
          };
      cmiGui.addButton(cmiGuiButton);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_admin.hasPermission(player)) {
      cmiGuiButton =
          new CMIGuiButton(43, CMIMaterial.CHEST) {
            public void click(GUIClickType clickType) {
              CMIArmorStandEditor.openSaveWindow(player, entity);
            }

            public void updateLooks() {
              this.setName(CMILC.getIM(armorstand.class, "saveLabel"));
            }
          };
      cmiGui.addButton(cmiGuiButton);
    }

    cmiGui.fillEmptyButtons();
    cmiGui.open();
  }

  private static double getAngle(
      ArmorStand armorStand,
      ArmorStandManager.armorStandActions armorStandActions,
      ArmorStandManager.armorStandPoseC armorStandPoseC) {
    EulerAngle eulerAngle = null;
    switch (armorStandActions) {
      case head:
        eulerAngle = armorStand.getHeadPose();
        break;
      case body:
        return armorStand.getLocation().getYaw();
      case torso:
        eulerAngle = armorStand.getBodyPose();
        break;
      case leftArm:
        eulerAngle = armorStand.getLeftArmPose();
        break;
      case rightArm:
        eulerAngle = armorStand.getRightArmPose();
        break;
      case leftLeg:
        eulerAngle = armorStand.getLeftLegPose();
        break;
      case rightLeg:
        eulerAngle = armorStand.getRightLegPose();
        break;
      case pos:
        Location location = armorStand.getLocation();
        switch (armorStandPoseC) {
          case x:
            return (int) (location.getX() * 100.0) / 100.0;
          case y:
            return (int) (location.getY() * 100.0) / 100.0;
          case z:
            return (int) (location.getZ() * 100.0) / 100.0;
        }
    }

    if (eulerAngle != null) {
      switch (armorStandPoseC) {
        case x:
          return (int) (Math.toDegrees(eulerAngle.getX()) * 100.0) / 100.0;
        case y:
          return (int) (Math.toDegrees(eulerAngle.getY()) * 100.0) / 100.0;
        case z:
          return (int) (Math.toDegrees(eulerAngle.getZ()) * 100.0) / 100.0;
      }
    }

    return 0.0;
  }

  private static EulerAngle getEulerAngle(
      ArmorStand armorStand,
      ArmorStandManager.armorStandActions armorStandActions,
      ArmorStandManager.armorStandPoseC armorStandPoseC) {
    EulerAngle eulerAngle = null;
    switch (armorStandActions) {
      case head:
        eulerAngle = armorStand.getHeadPose();
      case body:
      default:
        break;
      case torso:
        eulerAngle = armorStand.getBodyPose();
        break;
      case leftArm:
        eulerAngle = armorStand.getLeftArmPose();
        break;
      case rightArm:
        eulerAngle = armorStand.getRightArmPose();
        break;
      case leftLeg:
        eulerAngle = armorStand.getLeftLegPose();
        break;
      case rightLeg:
        eulerAngle = armorStand.getRightLegPose();
    }

    return eulerAngle;
  }

  private static void duplicate(
      ArmorStandManager.armorStandActions armorStandActions,
      ArmorStand armorStand,
      ArmorStand otherStand) {
    setAngle(
        otherStand,
        armorStandActions,
        ArmorStandManager.armorStandPoseC.x,
        getEulerAngle(armorStand, armorStandActions, ArmorStandManager.armorStandPoseC.x));
    setAngle(
        otherStand,
        armorStandActions,
        ArmorStandManager.armorStandPoseC.y,
        getEulerAngle(armorStand, armorStandActions, ArmorStandManager.armorStandPoseC.y));
    setAngle(
        otherStand,
        armorStandActions,
        ArmorStandManager.armorStandPoseC.z,
        getEulerAngle(armorStand, armorStandActions, ArmorStandManager.armorStandPoseC.z));
  }

  public static void duplicate(
      ArmorStand armorStand, ArmorStand otherStand, Set<ArmorStandManager.armorStandActions> set) {
    if (armorStand != null && !armorStand.isDead() && otherStand != null && !otherStand.isDead()) {
      if (set.contains(ArmorStandManager.armorStandActions.body)) {
        Location location = otherStand.getLocation();
        location.setYaw(armorStand.getLocation().getYaw());
        otherStand.teleport(location);
      }

      for (ArmorStandManager.armorStandActions armorStandActions : set) {
        duplicate(armorStandActions, armorStand, otherStand);
      }
    }
  }

  public static void duplicateExtra(
      ArmorStand armorStand,
      ArmorStand otherStand,
      Set<ArmorStandManager.armorStandExtraActions> set) {
    if (armorStand != null && !armorStand.isDead() && otherStand != null && !otherStand.isDead()) {
      for (ArmorStandManager.armorStandExtraActions armorStandExtraActions : set) {
        switch (armorStandExtraActions) {
          case plate:
            otherStand.setBasePlate(armorStand.hasBasePlate());
            break;
          case size:
            otherStand.setSmall(armorStand.isSmall());
            break;
          case visible:
            otherStand.setVisible(armorStand.isVisible());
            break;
          case arms:
            otherStand.setArms(armorStand.hasArms());
            break;
          case gravity:
            otherStand.setGravity(armorStand.hasGravity());
            break;
          case glow:
            otherStand.setGlowing(armorStand.isGlowing());
            break;
          case invulnerable:
            otherStand.setInvulnerable(armorStand.isInvulnerable());
            break;
          case name:
            otherStand.setCustomNameVisible(armorStand.isCustomNameVisible());
            break;
          case interactable:
            otherStand.setMarker(armorStand.isMarker());
            break;
          case scale:
            if (!Version.isCurrentLower(Version.v1_20_R4)) {
              adjustAttribute(armorStand, otherStand);
            }
        }
      }
    }
  }

  private static void adjustAttribute(ArmorStand armorStand, ArmorStand otherStand) {
    CMIScheduler.runAtEntity(
        CMI.getInstance(),
        otherStand,
        () -> CMIAttribute.SCALE.setBaseValue(otherStand, CMIAttribute.SCALE.getValue(armorStand)));
  }

  private static void setAngleButtons(
      final CMIGui cmiGui,
      final ArmorStand armorStand,
      final ArmorStandManager.armorStandActions armorStandActions,
      int value) {
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(value, CMIMaterial.BROWN_WOOL) {
          public void click(GUIClickType guiClickType) {
            CMIArmorStandEditor.changeAngle(
                cmiGui,
                armorStandActions,
                ArmorStandManager.armorStandPoseC.x,
                CMIArmorStandEditor.getValue(guiClickType));
            this.updateLooks();
            this.update();
          }

          public void updateLooks() {
            this.setName(
                CMILC.getIM(
                    "armorstand",
                    armorStandActions.name(),
                    "[coord]",
                    CMILC.getIM("armorstand", "coordX"),
                    "[amount]",
                    CMIArmorStandEditor.getAngle(
                        armorStand, armorStandActions, ArmorStandManager.armorStandPoseC.x)));
          }
        };
    cmiGuiButton.addLore(CMILC.getIM("armorstand", "explanation"));
    cmiGuiButton.addLore(CMILC.getIM("armorstand", "explanation2"));
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(value + 9, CMIMaterial.BLUE_WOOL) {
          public void click(GUIClickType clickType) {
            if (!armorStandActions.equals(ArmorStandManager.armorStandActions.pos)
                || armorStandActions.equals(ArmorStandManager.armorStandActions.pos)
                    && !armorStand.hasGravity()) {
              CMIArmorStandEditor.changeAngle(
                  cmiGui,
                  armorStandActions,
                  ArmorStandManager.armorStandPoseC.y,
                  CMIArmorStandEditor.getValue(clickType));
              this.updateLooks();
              this.update();
            }
          }

          public void updateLooks() {
            this.setName(
                CMILC.getIM(
                    "armorstand",
                    armorStandActions.name(),
                    "[coord]",
                    CMILC.getIM("armorstand", "coordY"),
                    "[amount]",
                    CMIArmorStandEditor.getAngle(
                        armorStand, armorStandActions, ArmorStandManager.armorStandPoseC.y)));
          }
        };
    if (armorStandActions.equals(ArmorStandManager.armorStandActions.pos)
        && armorStand.hasGravity()) {
      cmiGuiButton.addLore(CMILC.getIM("armorstand", "disableGravity"));
    }

    cmiGuiButton.addLore(CMILC.getIM("armorstand", "explanation"));
    cmiGuiButton.addLore(CMILC.getIM("armorstand", "explanation2"));
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(value + 18, CMIMaterial.PURPLE_WOOL) {
          public void click(GUIClickType clickType) {
            CMIArmorStandEditor.changeAngle(
                cmiGui,
                armorStandActions,
                ArmorStandManager.armorStandPoseC.z,
                CMIArmorStandEditor.getValue(clickType));
            this.updateLooks();
            this.update();
          }

          public void updateLooks() {
            this.setName(
                CMILC.getIM(
                    "armorstand",
                    armorStandActions.name(),
                    "[coord]",
                    CMILC.getIM("armorstand", "coordZ"),
                    "[amount]",
                    CMIArmorStandEditor.getAngle(
                        armorStand, armorStandActions, ArmorStandManager.armorStandPoseC.z)));
          }
        };
    cmiGuiButton.addLore(CMILC.getIM("armorstand", "explanation"));
    cmiGuiButton.addLore(CMILC.getIM("armorstand", "explanation2"));
    cmiGui.addButton(cmiGuiButton);
  }

  public static boolean openPositionEditor(final Player player, final Entity entity) {
    if (!Version.isCurrentHigher(Version.v1_7_R4)) {
      return false;
    }

    if (!entity.getType().equals(EntityType.ARMOR_STAND)) {
      return false;
    }

    if (entity.getCustomName() != null && entity.getCustomName().equals("CMIArmorStandForSit")) {
      return false;
    }

    Integer integer = new CMINBT(entity).getInt("DisabledSlots");
    if (integer != null && integer == 2096896) {
      return false;
    }

    if (entity.isDead()) {
      player.closeInventory();
      return false;
    }

    CMI.getInstance().getArmorStandManager().addEditor(player, entity);
    final ArmorStand armorStand = (ArmorStand) entity;
    final CMIGui cmiGui =
        new CMIGui(player) {
          public void onClose() {
            CMI.getInstance().getArmorStandManager().removeEditor(player);
          }
        };
    cmiGui.setWhatShows(entity);
    cmiGui.setCmiInventoryType(CmiInventoryType.ArmorStandEditor);
    cmiGui.setInvSize(GUIRows.r6);
    cmiGui.setTitle(CMILC.getIM(armorstand.class, "title"));
    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandActions.body.name())) {
      CMIGuiButton cmiGuiButton =
          new CMIGuiButton(40, CMIMaterial.BROWN_WOOL) {
            public void click(GUIClickType guiClickType) {
              CMIArmorStandEditor.changeAngle(
                  cmiGui,
                  ArmorStandManager.armorStandActions.body,
                  ArmorStandManager.armorStandPoseC.x,
                  CMIArmorStandEditor.getValue(guiClickType));
              this.updateLooks();
              this.update();
            }

            public void updateLooks() {
              this.setName(
                  CMILC.getIM(
                      "armorstand",
                      ArmorStandManager.armorStandActions.body.name(),
                      "[amount]",
                      CMIArmorStandEditor.getAngle(
                          armorStand,
                          ArmorStandManager.armorStandActions.body,
                          ArmorStandManager.armorStandPoseC.x)));
            }
          };
      cmiGuiButton.addLore(CMILC.getIM("armorstand", "explanation"));
      cmiGuiButton.addLore(CMILC.getIM("armorstand", "explanation2"));
      cmiGui.addButton(cmiGuiButton);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandActions.head.name())) {
      setAngleButtons(cmiGui, armorStand, ArmorStandManager.armorStandActions.head, 10);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandActions.torso.name())) {
      setAngleButtons(cmiGui, armorStand, ArmorStandManager.armorStandActions.torso, 11);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandActions.leftArm.name())) {
      setAngleButtons(cmiGui, armorStand, ArmorStandManager.armorStandActions.leftArm, 12);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandActions.rightArm.name())) {
      setAngleButtons(cmiGui, armorStand, ArmorStandManager.armorStandActions.rightArm, 13);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandActions.leftLeg.name())) {
      setAngleButtons(cmiGui, armorStand, ArmorStandManager.armorStandActions.leftLeg, 14);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandActions.rightLeg.name())) {
      setAngleButtons(cmiGui, armorStand, ArmorStandManager.armorStandActions.rightLeg, 15);
    }

    if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
        player, ArmorStandManager.armorStandActions.pos.name())) {
      setAngleButtons(cmiGui, armorStand, ArmorStandManager.armorStandActions.pos, 16);
    }

    CMIGuiButton lockButton =
        new CMIGuiButton(
            53,
            CMILib.getInstance()
                .getItemManager()
                .getItem(
                    "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNjYmY5ODgzZGQzNTlmZGYyMzg1YzkwYTQ1OWQ3Mzc3NjUzODJlYzQxMTdiMDQ4OTVhYzRkYzRiNjBmYyJ9fX0=")) {
          public void click(GUIClickType clickType) {
            CMIArmorStandEditor.openEditorInternal(player, entity, false);
          }
        };
    lockButton.setName(LC.info_Back.getLocale(new Object[0]));
    cmiGui.addButton(lockButton);
    cmiGui.fillEmptyButtons();
    cmiGui.open();
    return true;
  }

  private static boolean canBuild(Player player, Location location) {
    if (player == null) {
      return true;
    }

    if (PermissionsManager.CMIPerm.command_armorstand_movebypass.hasPermission(player)) {
      return true;
    }

    PlayerAnimationEvent playerAnimationEvent = new PlayerAnimationEvent(player);
    Bukkit.getPluginManager().callEvent(playerAnimationEvent);
    BlockPlaceEvent blockPlaceEvent = CMIEvent.placeBlockEvent(location.getBlock(), player);
    Bukkit.getPluginManager().callEvent(blockPlaceEvent);
    return !blockPlaceEvent.isCancelled();
  }

  public static void changeAngle(
      CMIGui cmiGui,
      ArmorStandManager.armorStandActions armorStandActions,
      ArmorStandManager.armorStandPoseC armorStandPoseC,
      int value) {
    if (isOk(cmiGui)) {
      Entity entity = (Entity) cmiGui.getWhatShows();
      changeAngle(
          (ArmorStand) entity, cmiGui.getPlayer(), armorStandActions, armorStandPoseC, value);
    }
  }

  public static void changeAngle(
      ArmorStand armorStand,
      Player player,
      ArmorStandManager.armorStandActions armorStandActions,
      ArmorStandManager.armorStandPoseC armorStandPoseC,
      int value) {
    double number = value / 100.0;
    EulerAngle eulerAngle = null;
    switch (armorStandActions) {
      case head:
        eulerAngle = armorStand.getHeadPose();
        break;
      case body:
        Float yawValue = armorStand.getLocation().getYaw() + value;
        yawValue = yawValue < 0.0F ? 360.0F + yawValue : yawValue;
        yawValue =
            yawValue >= 360.0F
                ? (360.0F - yawValue != 0.0F ? -(360.0F - yawValue) : 0.0F)
                : yawValue;
        Location location = armorStand.getLocation().clone();
        location.setYaw(yawValue);
        if (canBuild(player, location)) {
          if (Version.isFolia()) {
            CMITeleporter.teleportAsync(armorStand, location);
          } else {
            armorStand.teleport(location);
          }
        }

        return;
      case torso:
        eulerAngle = armorStand.getBodyPose();
        break;
      case leftArm:
        eulerAngle = armorStand.getLeftArmPose();
        break;
      case rightArm:
        eulerAngle = armorStand.getRightArmPose();
        break;
      case leftLeg:
        eulerAngle = armorStand.getLeftLegPose();
        break;
      case rightLeg:
        eulerAngle = armorStand.getRightLegPose();
        break;
      case pos:
        switch (armorStandPoseC) {
          case x:
            Location offsetEast = armorStand.getLocation().clone().add(number, 0.0, 0.0);
            if (canBuild(player, offsetEast)) {
              if (Version.isFolia()) {
                CMITeleporter.teleportAsync(armorStand, offsetEast);
              } else {
                armorStand.teleport(offsetEast);
              }
            }

            return;
          case y:
            Location offsetNorth = armorStand.getLocation().clone().add(0.0, number, 0.0);
            if (canBuild(player, offsetNorth)) {
              if (Version.isFolia()) {
                CMITeleporter.teleportAsync(armorStand, offsetNorth);
              } else {
                armorStand.teleport(offsetNorth);
              }
            }

            return;
          case z:
            Location offsetUp = armorStand.getLocation().clone().add(0.0, 0.0, number);
            if (canBuild(player, offsetUp)) {
              if (Version.isFolia()) {
                CMITeleporter.teleportAsync(armorStand, offsetUp);
              } else {
                armorStand.teleport(offsetUp);
              }
            }

            return;
        }
    }

    if (eulerAngle != null) {
      if (value == 0) {
        switch (armorStandPoseC) {
          case x:
            eulerAngle = eulerAngle.setX(0.0);
            break;
          case y:
            eulerAngle = eulerAngle.setY(0.0);
            break;
          case z:
            eulerAngle = eulerAngle.setZ(0.0);
        }
      } else {
        number = Math.PI / (360 / value);
        switch (armorStandPoseC) {
          case x:
            eulerAngle = eulerAngle.add(number, 0.0, 0.0);
            break;
          case y:
            eulerAngle = eulerAngle.add(0.0, number, 0.0);
            break;
          case z:
            eulerAngle = eulerAngle.add(0.0, 0.0, number);
        }
      }
    }

    switch (armorStandActions) {
      case head:
        armorStand.setHeadPose(eulerAngle);
      case body:
      default:
        break;
      case torso:
        armorStand.setBodyPose(eulerAngle);
        break;
      case leftArm:
        armorStand.setLeftArmPose(eulerAngle);
        break;
      case rightArm:
        armorStand.setRightArmPose(eulerAngle);
        break;
      case leftLeg:
        armorStand.setLeftLegPose(eulerAngle);
        break;
      case rightLeg:
        armorStand.setRightLegPose(eulerAngle);
    }
  }

  private static void setAngle(
      ArmorStand armorStand,
      ArmorStandManager.armorStandActions armorStandActions,
      ArmorStandManager.armorStandPoseC armorStandPoseC,
      EulerAngle eulerAngle) {
    switch (armorStandActions) {
      case head:
        armorStand.setHeadPose(eulerAngle);
      case body:
      default:
        break;
      case torso:
        armorStand.setBodyPose(eulerAngle);
        break;
      case leftArm:
        armorStand.setLeftArmPose(eulerAngle);
        break;
      case rightArm:
        armorStand.setRightArmPose(eulerAngle);
        break;
      case leftLeg:
        armorStand.setLeftLegPose(eulerAngle);
        break;
      case rightLeg:
        armorStand.setRightLegPose(eulerAngle);
    }
  }

  public static void switchArmorStandArms(CMIGui cmiGui) {
    if (isOk(cmiGui)) {
      Entity entity = (Entity) cmiGui.getWhatShows();
      ArmorStand armorStand = (ArmorStand) entity;
      armorStand.setArms(!armorStand.hasArms());
    }
  }

  private static int getValue(GUIClickType guiClickType) {
    switch (guiClickType) {
      case Left:
        return 1;
      case LeftShift:
        return 10;
      case Right:
        return -1;
      case RightShift:
        return -10;
      case MiddleMouse:
      case Q:
        return 0;
      case QControl:
      default:
        return 0;
    }
  }

  public static void updateArmorStandItems(CMIGui cmiGui) {
    if (isOk(cmiGui)) {
      Entity entity = (Entity) cmiGui.getWhatShows();
      ArmorStand armorStand = (ArmorStand) entity;
      ItemStack itemStack =
          cmiGui.getInv().getItem(ArmorStandManager.armorStandEditorSlots.helmet.getSlot());
      armorStand.setHelmet(itemStack);
      ItemStack itemCopy =
          cmiGui.getInv().getItem(ArmorStandManager.armorStandEditorSlots.chest.getSlot());
      armorStand.setChestplate(itemCopy);
      itemCopy =
          cmiGui.getInv().getItem(ArmorStandManager.armorStandEditorSlots.leggings.getSlot());
      armorStand.setLeggings(itemCopy);
      itemCopy = cmiGui.getInv().getItem(ArmorStandManager.armorStandEditorSlots.boots.getSlot());
      armorStand.setBoots(itemCopy);
      itemCopy =
          cmiGui.getInv().getItem(ArmorStandManager.armorStandEditorSlots.mainhand.getSlot());
      armorStand.setItemInHand(itemCopy);
      if (Version.isCurrentHigher(Version.v1_8_R3)) {
        LivingEntity livingEntity = (LivingEntity) entity;
        itemCopy =
            cmiGui.getInv().getItem(ArmorStandManager.armorStandEditorSlots.offhand.getSlot());
        livingEntity.getEquipment().setItemInOffHand(itemCopy);
      }
    }
  }

  private static boolean hasActionAccess(Player player) {
    ArmorStandManager.armorStandActions[] armorStandActionses;
    int value = (armorStandActionses = ArmorStandManager.armorStandActions.values()).length;

    for (int index = 0; index < value; index++) {
      ArmorStandManager.armorStandActions armorStandActions = armorStandActionses[index];
      if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
          player, armorStandActions.name())) {
        return true;
      }
    }

    return false;
  }

  private static boolean hasExtraActionAccess(Player player) {
    ArmorStandManager.armorStandExtraActions[] armorStandExtraActionses;
    int value =
        (armorStandExtraActionses = ArmorStandManager.armorStandExtraActions.values()).length;

    for (int index = 0; index < value; index++) {
      ArmorStandManager.armorStandExtraActions armorStandExtraActions =
          armorStandExtraActionses[index];
      if (PermissionsManager.CMIPerm.command_armorstand_$1.hasPermission(
          player, armorStandExtraActions.name())) {
        return true;
      }
    }

    return false;
  }
}
