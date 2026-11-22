package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMISpawner;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.GUI.GUIManager.GUIRows;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class spawnereditor implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("type", "{gcp}Change type: {gcs}[type]");
    configReader.get("nextIn", "{gcp}Next spawn in: {gcs}[time]");
    configReader.get("nearby", "{gcp}MaxNearBy entities: {gcs}[count]");
    configReader.get("maxdelay", "{gcp}Max Spawn Delay: {gcs}[count]");
    configReader.get("mindelay", "{gcp}Min Spawn Delay: {gcs}[count]");
    configReader.get("prange", "{gcp}Required player range: {gcs}[range]");
    configReader.get("spawncount", "{gcp}Spawn Count: {gcs}[count]");
    configReader.get("spawnrange", "{gcp}Spawn Range: {gcs}[range]");
  }

  @CAnnotation(
      info = "Edit spawner",
      regVar = {0, 1},
      consoleVar = 666)
  @Override
  public Boolean perform(final CMI cmi, CommandSender commandSender, String[] strings) {
    if (Version.isCurrentEqualOrLower(Version.v1_8_R3)) {
      LC.info_versionNotSupported.sendMessage(commandSender, new Object[0]);
      return null;
    }

    final Player player = (Player) commandSender;
    final Block block = cmi.getUtilManager().getTargetBlock(player, 16);
    if (block != null && CMIMaterial.SPAWNER.equals(block.getType())) {
      final CMISpawner cmiSpawner = new CMISpawner(block);
      final CMIGui cmiGui = new CMIGui(player);
      cmiGui.setInvSize(GUIRows.r3);
      if (cmiSpawner.getSpawnedType() != null) {
        cmiGui.setTitle(CMIEntityType.getByType(cmiSpawner.getSpawnedType()).getTranslatedName());
      }

      CMIGuiButton cmiGuiButton =
          new CMIGuiButton(4, CMIMaterial.SPAWNER.newItemStack()) {
            public void click(GUIClickType guiClickType) {
              cmi.getSpawnerChargesManager().openSpawnerPickerUI(player, 1);
            }

            public void updateLooks() {
              if (cmiSpawner.getSpawnedType() != null) {
                this.setName(
                    CMILC.getIM(
                        spawnereditor.class.getSimpleName(),
                        "type",
                        "[type]",
                        CMIEntityType.getByType(cmiSpawner.getSpawnedType()).getTranslatedName()));
              }
            }
          };
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(10, CMIMaterial.RED_WOOL.newItemStack()) {
            public void click(GUIClickType clickType) {
              Block targetBlock = block.getWorld().getBlockAt(block.getLocation());
              if (targetBlock != null && CMIMaterial.SPAWNER.equals(targetBlock.getType())) {
                CMISpawner spawner = new CMISpawner(targetBlock);
                switch (clickType) {
                  case Left:
                    spawner.setDelay(spawner.getDelay() > 20 ? spawner.getDelay() - 20 : 20);
                    break;
                  case LeftShift:
                    spawner.setDelay(spawner.getDelay() > 200 ? spawner.getDelay() - 200 : 20);
                    break;
                  case Right:
                    spawner.setDelay(spawner.getDelay() + 20);
                    break;
                  case RightShift:
                    spawner.setDelay(spawner.getDelay() + 200);
                }

                spawner.update();
                this.updateLooks();
                this.update(cmiGui);
              } else {
                CMIUser.closeInventory(player);
              }
            }

            public void updateLooks() {
              Block targetBlock = block.getWorld().getBlockAt(block.getLocation());
              if (targetBlock != null
                  && CMIMaterial.SPAWNER.equals(targetBlock.getState().getType())) {
                CMISpawner spawner = new CMISpawner(targetBlock);
                this.setName(
                    CMILC.getIM(
                        spawnereditor.class.getSimpleName(),
                        "nextIn",
                        "[time]",
                        CMITimeManager.to24hourShort(spawner.getDelay() / 20 * 1000L)));
              }
            }
          };
      cmiGuiButton.startAutoUpdate(cmiGui, 20);
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(11, CMIMaterial.GREEN_WOOL.newItemStack()) {
            public void click(GUIClickType clickType) {
              Block targetBlock = block.getWorld().getBlockAt(block.getLocation());
              if (targetBlock != null
                  && CMIMaterial.SPAWNER.equals(targetBlock.getState().getType())) {
                int value = cmiSpawner.getMaxNearbyEntities();
                switch (clickType) {
                  case Left:
                    value--;
                    break;
                  case LeftShift:
                    value -= 10;
                    break;
                  case Right:
                    value++;
                    break;
                  case RightShift:
                    value += 10;
                }

                if (value < 1) {
                  value = 1;
                }

                cmiSpawner.setMaxNearbyEntities(value);
                cmiSpawner.update();
                this.updateLooks();
                this.update(cmiGui);
              } else {
                CMIUser.closeInventory(player);
              }
            }

            public void updateLooks() {
              Block targetBlock = block.getWorld().getBlockAt(block.getLocation());
              if (targetBlock != null
                  && CMIMaterial.SPAWNER.equals(targetBlock.getState().getType())) {
                this.setName(
                    CMILC.getIM(
                        spawnereditor.class.getSimpleName(),
                        "nearby",
                        "[count]",
                        cmiSpawner.getMaxNearbyEntities()));
              }
            }
          };
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(12, CMIMaterial.BLUE_WOOL.newItemStack()) {
            public void click(GUIClickType clickType) {
              Block targetBlock = block.getWorld().getBlockAt(block.getLocation());
              if (targetBlock != null
                  && CMIMaterial.SPAWNER.equals(targetBlock.getState().getType())) {
                int maxDelay = cmiSpawner.getMaxSpawnDelay();
                switch (clickType) {
                  case Left:
                    maxDelay -= 20;
                    break;
                  case LeftShift:
                    maxDelay -= 200;
                    break;
                  case Right:
                    maxDelay += 20;
                    break;
                  case RightShift:
                    maxDelay += 200;
                }

                int minDelay = cmiSpawner.getMinSpawnDelay();
                if (minDelay > maxDelay) {
                  maxDelay = minDelay;
                }

                if (maxDelay < 1) {
                  maxDelay = 1;
                }

                cmiSpawner.setMaxSpawnDelay(maxDelay);
                cmiSpawner.update();
                this.updateLooks();
                this.update(cmiGui);
              } else {
                CMIUser.closeInventory(player);
              }
            }

            public void updateLooks() {
              Block targetBlock = block.getWorld().getBlockAt(block.getLocation());
              if (targetBlock != null
                  && CMIMaterial.SPAWNER.equals(targetBlock.getState().getType())) {
                this.setName(
                    CMILC.getIM(
                        spawnereditor.class.getSimpleName(),
                        "maxdelay",
                        "[count]",
                        CMITimeManager.to24hourShort(cmiSpawner.getMaxSpawnDelay() / 20 * 1000L)));
              }
            }
          };
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(13, CMIMaterial.BROWN_WOOL.newItemStack()) {
            public void click(GUIClickType clickType) {
              Block targetBlock = block.getWorld().getBlockAt(block.getLocation());
              if (targetBlock != null
                  && CMIMaterial.SPAWNER.equals(targetBlock.getState().getType())) {
                int minDelay = cmiSpawner.getMinSpawnDelay();
                switch (clickType) {
                  case Left:
                    minDelay -= 20;
                    break;
                  case LeftShift:
                    minDelay -= 200;
                    break;
                  case Right:
                    minDelay += 20;
                    break;
                  case RightShift:
                    minDelay += 200;
                }

                int maxDelay = cmiSpawner.getMaxSpawnDelay();
                if (maxDelay < minDelay) {
                  minDelay = maxDelay;
                }

                if (minDelay < 1) {
                  minDelay = 1;
                }

                cmiSpawner.setMinSpawnDelay(minDelay);
                cmiSpawner.update();
                this.updateLooks();
                this.update(cmiGui);
              } else {
                CMIUser.closeInventory(player);
              }
            }

            public void updateLooks() {
              Block targetBlock = block.getWorld().getBlockAt(block.getLocation());
              if (targetBlock != null
                  && CMIMaterial.SPAWNER.equals(targetBlock.getState().getType())) {
                this.setName(
                    CMILC.getIM(
                        spawnereditor.class.getSimpleName(),
                        "mindelay",
                        "[count]",
                        CMITimeManager.to24hourShort(cmiSpawner.getMinSpawnDelay() / 20 * 1000L)));
              }
            }
          };
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(14, CMIMaterial.GRAY_WOOL.newItemStack()) {
            public void click(GUIClickType clickType) {
              Block targetBlock = block.getWorld().getBlockAt(block.getLocation());
              if (targetBlock != null
                  && CMIMaterial.SPAWNER.equals(targetBlock.getState().getType())) {
                int range = cmiSpawner.getRequiredPlayerRange();
                switch (clickType) {
                  case Left:
                    range--;
                    break;
                  case LeftShift:
                    range -= 10;
                    break;
                  case Right:
                    range++;
                    break;
                  case RightShift:
                    range += 10;
                }

                if (range < 1) {
                  range = 1;
                }

                if (range > 32) {
                  range = 32;
                }

                cmiSpawner.setRequiredPlayerRange(range);
                cmiSpawner.update();
                this.updateLooks();
                this.update(cmiGui);
              } else {
                CMIUser.closeInventory(player);
              }
            }

            public void updateLooks() {
              Block targetBlock = block.getWorld().getBlockAt(block.getLocation());
              if (targetBlock != null
                  && CMIMaterial.SPAWNER.equals(targetBlock.getState().getType())) {
                this.setName(
                    CMILC.getIM(
                        spawnereditor.class.getSimpleName(),
                        "prange",
                        "[range]",
                        cmiSpawner.getRequiredPlayerRange()));
              }
            }
          };
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(15, CMIMaterial.PURPLE_WOOL.newItemStack()) {
            public void click(GUIClickType clickType) {
              Block targetBlock = block.getWorld().getBlockAt(block.getLocation());
              if (targetBlock != null
                  && CMIMaterial.SPAWNER.equals(targetBlock.getState().getType())) {
                int count = cmiSpawner.getSpawnCount();
                switch (clickType) {
                  case Left:
                    count--;
                    break;
                  case LeftShift:
                    count -= 10;
                    break;
                  case Right:
                    count++;
                    break;
                  case RightShift:
                    count += 10;
                }

                if (count < 1) {
                  count = 1;
                }

                if (count > 32) {
                  count = 32;
                }

                cmiSpawner.setSpawnCount(count);
                cmiSpawner.update();
                this.updateLooks();
                this.update(cmiGui);
              } else {
                CMIUser.closeInventory(player);
              }
            }

            public void updateLooks() {
              Block targetBlock = block.getWorld().getBlockAt(block.getLocation());
              if (targetBlock != null
                  && CMIMaterial.SPAWNER.equals(targetBlock.getState().getType())) {
                this.setName(
                    CMILC.getIM(
                        spawnereditor.class.getSimpleName(),
                        "spawncount",
                        "[count]",
                        cmiSpawner.getSpawnCount()));
              }
            }
          };
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(16, CMIMaterial.PINK_WOOL.newItemStack()) {
            public void click(GUIClickType clickType) {
              Block targetBlock = block.getWorld().getBlockAt(block.getLocation());
              if (targetBlock != null
                  && CMIMaterial.SPAWNER.equals(targetBlock.getState().getType())) {
                int spawnRange = cmiSpawner.getSpawnRange();
                switch (clickType) {
                  case Left:
                    spawnRange--;
                    break;
                  case LeftShift:
                    spawnRange -= 10;
                    break;
                  case Right:
                    spawnRange++;
                    break;
                  case RightShift:
                    spawnRange += 10;
                }

                if (spawnRange < 1) {
                  spawnRange = 1;
                }

                if (spawnRange > 32) {
                  spawnRange = 32;
                }

                cmiSpawner.setSpawnRange(spawnRange);
                cmiSpawner.update();
                this.updateLooks();
                this.update(cmiGui);
              } else {
                CMIUser.closeInventory(player);
              }
            }

            public void updateLooks() {
              Block targetBlock = block.getWorld().getBlockAt(block.getLocation());
              if (targetBlock != null
                  && CMIMaterial.SPAWNER.equals(targetBlock.getState().getType())) {
                this.setName(
                    CMILC.getIM(
                        spawnereditor.class.getSimpleName(),
                        "spawnrange",
                        "[range]",
                        cmiSpawner.getSpawnRange()));
              }
            }
          };
      cmiGui.addButton(cmiGuiButton);
      cmiGui.fillEmptyButtons();
      cmiGui.open();
      return true;
    } else {
      CMILC.info("spawner", commandSender, "cantDetect");
      return true;
    }
  }
}
