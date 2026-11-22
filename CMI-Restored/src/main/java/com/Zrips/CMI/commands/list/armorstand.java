package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.AStand.ArmorStandManager;
import com.Zrips.CMI.Modules.AStand.ArmorStandSave;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.UUID;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Chat.ChatEditorObject;
import net.Zrips.CMILib.Chat.ChatMessageObjectEdit;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.GUIManager.CmiInventoryType;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class armorstand implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("cantClick", "{gcw}Cant do this. Someone is already editing this armor stand");
    configReader.get("tooFar", "{gcw}Armor stand is too far");
    configReader.get("title", "Editor");
    configReader.get("coordX", "{gcw}X");
    configReader.get("coordY", "{gcu}Y");
    configReader.get("coordZ", "{gcp}Z");
    configReader.get("body", "{gcp}Body rotation: {gcs}[amount]");
    configReader.get("head", "{gcp}Head [coord]{gcp}: {gcs}[amount]");
    configReader.get("torso", "{gcp}Torso [coord]{gcp}: {gcs}[amount]");
    configReader.get("leftArm", "{gcp}Left arm [coord]{gcp}: {gcs}[amount]");
    configReader.get("rightArm", "{gcp}Right arm [coord]{gcp}: {gcs}[amount]");
    configReader.get("leftLeg", "{gcp}Left leg [coord]{gcp}: {gcs}[amount]");
    configReader.get("rightLeg", "{gcp}Right leg [coord]{gcp}: {gcs}[amount]");
    configReader.get("pos", "{gcp}Position [coord]{gcp}: {gcs}[amount]");
    configReader.get("list", "{gcp}[place]. {gcs}[distance] [items]");
    configReader.get("items", "{gcs}[displayName]{gcp}[type]");
    configReader.get("plate", "{gcp}Plate");
    configReader.get("size", "{gcp}Small");
    configReader.get("visible", "{gcp}Visible");
    configReader.get("arms", "{gcp}Visible arms");
    configReader.get("gravity", "{gcp}Gravity");
    configReader.get("glow", "{gcp}Glowing");
    configReader.get("invulnerable", "{gcp}Invulnerable");
    configReader.get("name", "{gcp}Visible name");
    configReader.get("nameEdit", "{gcp}Q click to edit name");
    configReader.get("interactable", "{gcp}Interactable");
    configReader.get("scale", "{gcp}Scale: {gcs}[amount]");
    configReader.get("scaleLimits", "{gcs}[min] {gcp}- {gcs}[max]");
    configReader.get("explanation", "{gcp}Left or right mouse button adjusts value");
    configReader.get("explanation", "{gcp}Hold shift to make it 10x faster");
    configReader.get("disableGravity", "{gcp}Disable gravity if you want to change this value");
    configReader.get("copyLabel", "{gcp}Open clone picker");
    configReader.get("copyTitle", "{gcp}Pick what you want to clone");
    configReader.get(
        "click", "{gcp}Click armor stand to duplicate it, or punch any block to cancel process");
    configReader.get("canceled", "{gcp}ArmorStand copy process cancelled");
    configReader.get("updated", "{gcp}ArmorStand updated");
    configReader.get("positionLabel", "{gcp}Open position options");
    configReader.get("saveLabel", "{gcp}Open template saver");
    configReader.get("saveButton", "{gcp}Save");
    configReader.get("saveTitle", "{gcp}Pick what you want to save");
    configReader.get("receivedItem", "{gcp}Received special armor stand into your inventory");
    configReader.get("armorStandItem", "{gcp}ArmorStand Template: {gcs}[name]");
    ArmorStandManager.armorStandSaveOptions[] armorStandSaveOptionses;
    int value = (armorStandSaveOptionses = ArmorStandManager.armorStandSaveOptions.values()).length;

    for (int count = 0; count < value; count++) {
      ArmorStandManager.armorStandSaveOptions armorStandSaveOptions =
          armorStandSaveOptionses[count];
      configReader.get(
          "action." + armorStandSaveOptions.toString(),
          "{gcp}" + CMIText.firstToUpperCase(armorStandSaveOptions.toString()));
    }
  }

  @CAnnotation(
      priority = 47,
      info = "Open armor stand editor",
      args = "(last/near)",
      tab = "last%%near%%list",
      explanation = {},
      regVar = {0, 1, 2},
      consoleVar = 666,
      modules = "armorstand",
      paccess = false,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Player player = (Player) commandSender;
    if (strings.length > 0) {
      String string;
      switch ((string = strings[0].toLowerCase()).hashCode()) {
        case 3314326:
          if (string.equals("last")) {
            return this.processLast(player);
          }
          break;
        case 3322014:
          if (string.equals("list")) {
            if (!PermissionsManager.CMIPerm.command_armorstand_admin.hasPermission(
                commandSender, true)) {
              return false;
            }

            Integer integer = null;
            String[] parts = strings;
            int value = strings.length;

            for (int count = 0; count < value; count++) {
              String text = parts[count];
              if (integer == null) {
                try {
                  integer = Integer.parseInt(text);
                } catch (Throwable throwable) {
                }
              }
            }

            if (integer == null) {
              integer = 1;
            }

            return this.processList(player, integer);
          }
          break;
        case 3377192:
          if (string.equals("near")) {
            return this.processNear(strings, player);
          }
      }

      if (!CMILib.getInstance().getGUIManager().isOpenedGui(player)) {
        return false;
      }

      CMIGui cmiGui = CMILib.getInstance().getGUIManager().getGui(player);
      if (strings[0].length() == 36) {
        for (Entity entity :
            player.getWorld().getNearbyEntities(player.getLocation(), 20.0, 20.0, 20.0)) {
          if (entity.getUniqueId().toString().equals(strings[0])) {
            if (cmi.getArmorStandManager().isTooFar(player, entity)) {
              CMILC.info(this, commandSender, "tooFar");
              return null;
            }

            if (cmiGui != null
                && cmiGui.getType().equals(CmiInventoryType.ArmorStandEditor)
                && !cmi.getArmorStandManager().openEditor(player, entity, false)) {
              return false;
            }

            return true;
          }
        }
      }
    } else {
      Entity entitySecond = cmi.getUtilManager().getTargetEntity(player, 0.94);
      if (entitySecond == null) {
        LC.info_lookAtEntity.sendMessage(commandSender, new Object[0]);
        return null;
      }

      if (!entitySecond.getType().equals(EntityType.ARMOR_STAND)) {
        return false;
      }

      if (entitySecond.hasMetadata("CMIArmorStandForSit")) {
        return false;
      }

      if (cmi.getArmorStandManager().isBeingEdited(entitySecond.getUniqueId())) {
        CMILC.info(this, commandSender, "cantClick");
        return null;
      }

      if (cmi.getArmorStandManager().isTooFar(player, entitySecond)) {
        CMILC.info(this, commandSender, "tooFar");
        return null;
      }

      if (!cmi.getArmorStandManager().openEditor(player, entitySecond, true)) {
        return false;
      }
    }

    return true;
  }

  private Boolean processLast(Player player) {
    Entity entity = CMI.getInstance().getArmorStandManager().getLast(player);
    if (entity == null) {
      return false;
    } else if (!entity.getType().equals(EntityType.ARMOR_STAND)) {
      return false;
    } else if (CMI.getInstance().getArmorStandManager().isBeingEdited(entity.getUniqueId())) {
      CMILC.info(this, player, "cantClick");
      return null;
    } else if (CMI.getInstance().getArmorStandManager().isTooFar(player, entity)) {
      CMILC.info(this, player, "tooFar");
      return null;
    } else {
      CMI.getInstance().getArmorStandManager().openEditor(player, entity, false);
      return true;
    }
  }

  private Boolean processList(final Player player, final int value) {
    var hashMap = CMI.getInstance().getArmorStandManager().getSavedArmorStands();
    PageInfo pageInfo =
        new PageInfo(10, hashMap.size(), value) {
          public Boolean pageChange(int count) {
            armorstand.this.processList(player, count);
            return true;
          }
        };
    LC.info_Spliter.sendMessage(player, new Object[0]);
    ArrayList arrayList = new ArrayList(hashMap.keySet());
    ChatMessageObjectEdit chatMessageObjectEdit = new ChatMessageObjectEdit(player, pageInfo);

    for (int countSecond = hashMap.size() - 1; countSecond >= 0; countSecond--) {
      if (pageInfo.isEntryOk()) {
        final ArmorStandSave armorStandSave =
            (ArmorStandSave) hashMap.get(arrayList.get(countSecond));
        ChatEditorObject chatEditorObject =
            new ChatEditorObject(armorStandSave.getName()) {
              public void onDelete() {
                CMI.getInstance()
                    .getArmorStandManager()
                    .removeSavedArmorStand(armorStandSave.getName());
                CMI.getInstance().getArmorStandManager().save();
                armorstand.this.processList(player, value);
              }

              public void onClick() {
                ItemStack itemStack = armorStandSave.createItem();
                player.getInventory().addItem(new ItemStack[] {itemStack});
              }
            };
        chatEditorObject.setHover(LC.info_Click.getLocale(new Object[] {armorStandSave.getName()}));
        chatMessageObjectEdit.addline(chatEditorObject);
      }
    }

    chatMessageObjectEdit.setAddNew(false);
    chatMessageObjectEdit.print();
    pageInfo.autoPagination(
        player, CommandsHandler.getLabel() + " " + armorstand.class.getSimpleName() + " list");
    return true;
  }

  private Boolean processNear(String[] strings, Player player) {
    Entity entity = null;
    if (strings.length != 1 && strings[1].length() == 36) {
      UUID uuid = UUID.fromString(strings[1]);

      for (Entity entitySecond :
          player.getWorld().getNearbyEntities(player.getLocation(), 20.0, 20.0, 20.0)) {
        if (entitySecond.getUniqueId().equals(uuid)) {
          entity = entitySecond;
          break;
        }
      }
    } else {
      var treeMap =
          CMI.getInstance()
              .getUtilManager()
              .getClosestEntities(player.getLocation(), 10, 2.0, EntityType.ARMOR_STAND);
      if (treeMap == null || treeMap.isEmpty()) {
        return false;
      }

      if (treeMap.size() > 1) {
        int value = 0;
        RawMessage rawMessage = new RawMessage();

        for (Entry entry : treeMap.entrySet()) {
          value++;
          if (rawMessage.getFinalLength() > 0) {
            rawMessage.addText("\n");
          }

          ArmorStand armorStand = (ArmorStand) entry.getValue();
          var arrayList =
              new ArrayList<>(Arrays.asList(armorStand.getEquipment().getArmorContents()));
          arrayList.add(armorStand.getItemInHand());
          if (Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
            arrayList.add(armorStand.getEquipment().getItemInOffHand());
          }

          StringBuilder stringBuilder = new StringBuilder();

          for (ItemStack itemStack : arrayList) {
            if (itemStack != null && !CMIMaterial.isAir(itemStack.getType())) {
              if (!stringBuilder.toString().isEmpty()) {
                stringBuilder.append(LC.info_ListSpliter.getLocale(new Object[0]));
              }

              String string = "";
              ItemMeta itemMeta = itemStack.getItemMeta();
              if (itemMeta != null) {
                String text = itemMeta.getDisplayName();
                if (text != null && !text.isEmpty()) {
                  string = text;
                }
              }

              stringBuilder.append(
                  CMILC.getIM(
                      this,
                      "items",
                      "[displayName]",
                      string,
                      "[type]",
                      CMIMaterial.get(itemStack).getName()));
            }
          }

          rawMessage.addText(
              CMILC.getIM(
                  this,
                  "list",
                  "[place]",
                  value,
                  "[distance]",
                  (int) ((Double) entry.getKey() * 100.0) / 100.0,
                  "[items]",
                  stringBuilder.toString()));
          rawMessage.addHover(LC.info_Click.getLocale(new Object[0]));
          rawMessage.addCommand(
              CommandsHandler.getLabel()
                  + " "
                  + armorstand.class.getSimpleName()
                  + " near "
                  + ((Entity) entry.getValue()).getUniqueId().toString());
        }

        rawMessage.show(player);
        return null;
      }

      entity = (Entity) treeMap.values().iterator().next();
    }

    if (entity == null || !entity.getType().equals(EntityType.ARMOR_STAND)) {
      return false;
    } else if (CMI.getInstance().getArmorStandManager().isBeingEdited(entity.getUniqueId())) {
      CMILC.info(this, player, "cantClick");
      return null;
    } else if (CMI.getInstance().getArmorStandManager().isTooFar(player, entity)) {
      CMILC.info(this, player, "tooFar");
      return null;
    } else {
      CMI.getInstance().getArmorStandManager().openEditor(player, entity, true);
      return true;
    }
  }
}
