package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Components.CMIComponentActionType;
import com.Zrips.CMI.Components.CMIComponentBase;
import com.Zrips.CMI.Components.CMIComponentItem;
import com.Zrips.CMI.Components.CMIComponentText;
import com.Zrips.CMI.Containers.CMIHitBox;
import com.Zrips.CMI.Containers.CMIJson;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Display.CMIBillboard;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Placeholders.Placeholder;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.Modules.Skin.SkinManager;
import com.Zrips.CMI.Modules.Statistics.StatsManager;
import com.Zrips.CMI.TabComplete;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.CMICommand;
import com.Zrips.CMI.commands.Cmd;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIBiome;
import net.Zrips.CMILib.Container.CMIBiomeType;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIVector3D;
import net.Zrips.CMILib.Effects.CMIEffectManager.CMIParticle;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Logs.CMIDebug;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.entity.WaterMob;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.json.simple.JSONObject;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

public class util implements Cmd {
  public static boolean imortaldebug = false;
  private static HashMap<Player, Long> targetingEnabled = new HashMap<>();
  private CMITask sched = null;
  private CMITask sched2 = null;
  private static HashMap<Player, Long> safeEnabled = new HashMap<>();
  private CMITask safeSched = null;
  public static UUID hologramTestUuid = null;
  ScheduledExecutorService executor = null;
  ScheduledFuture<?> scheduledFuture = null;
  CMITask clickEvent = null;

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("removedSits", "{gcp}Removed {gcs}[count] {gcp}empty sits from all worlds.");
  }

  private void createTestHolo(
      Player player,
      String string,
      CMIBillboard cmiBillboard,
      int value,
      int yaw,
      boolean flag,
      int offsetX) {
    CMIHologram cmiHologram = CMI.getInstance().getHologramManager().getByName(string);
    Location location = player.getEyeLocation();
    location.add(offsetX * 2, 0.0, 0.0);
    cmiHologram = new CMIHologram(string, location);
    cmiHologram.getSettings().setUpdateIntervalTicks(1);
    cmiHologram.getSettings().setUpdateRange(20);
    cmiHologram.getSettings().setVisibilityRange(24);
    cmiHologram.getTextSettings().setDoubleSided(flag);
    cmiHologram.getTextSettings().setBackgroundAlpha(255);
    cmiHologram.getSettings().setYaw(yaw);
    cmiHologram.getSettings().setPitch(value);
    cmiHologram.getIconSettings().setScale(0.5);
    cmiHologram.getBoardSettings().setEnabled(true);
    cmiHologram.getBoardSettings().getExtraDimensions().setZ(0.5);
    cmiHologram.getBoardSettings().getExtraDimensions().setY(0.1);
    cmiHologram.getBoardSettings().getExtraDimensions().setZ(0.15);
    cmiHologram.getBoardSettings().setMaterial(CMIMaterial.REINFORCED_DEEPSLATE);
    ArrayList<String> arrayList = new ArrayList();
    arrayList.add("<C>cmi heal [playerName]</C>");
    arrayList.add("<C>cmi feed [playerName]</C>");
    String holoText =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^*()-_=+[]{}|;:',.<>?/ "
            + "     ";
    new Random();

    for (int barCount = 0; barCount < CMINumber.random(10, 20); barCount++) {
      StringBuilder stringBuilder = new StringBuilder();
      if (new Random().nextDouble() > 0.8) {
        stringBuilder.append("ICON:%itemInHand%");
      } else {
        for (int columnIndex = 0; columnIndex < 9; columnIndex++) {
          byte b = 1;
          StringBuilder barBuilder = new StringBuilder();

          for (int barIndex = 0; barIndex < b; barIndex++) {
            barBuilder.append("⬛");
            columnIndex++;
          }

          stringBuilder.append(
              "<T>&7" + barBuilder.toString() + "</T><H>&5" + barBuilder.toString() + "</H>");
          stringBuilder.append((String) arrayList.get(CMINumber.random(0, arrayList.size())));
        }
      }

      cmiHologram.getPages().addLine(stringBuilder.toString());
    }

    cmiHologram.getSettings().setBillboard(cmiBillboard);
    cmiHologram.getInteractionSettings().setInteractable(true);
    cmiHologram.getInteractionSettings().setHoverEffect(CMIParticle.SOUL_FIRE_FLAME);
    cmiHologram.getInteractionSettings().setParticleCount(20);
    CMI.getInstance().getHologramManager().add(cmiHologram);
    cmiHologram.show(player);
  }

  public static List<CMIVector3D> fibonacciSphere(int value) {
    ArrayList<CMIVector3D> arrayList = new ArrayList(value);
    double number = Math.PI * (3.0 - Math.sqrt(5.0));

    for (int index = 0; index < value; index++) {
      double yPos = 1.0 - 2.0 * index / (value - 1);
      double ringRadius = Math.sqrt(1.0 - yPos * yPos);
      double angle = number * index;
      double ringX = Math.cos(angle) * ringRadius;
      double ringY = Math.sin(angle) * ringRadius;
      arrayList.add(new CMIVector3D(ringX, yPos, ringY));
    }

    return arrayList;
  }

  public static List<CMIVector3D> fibonacciSphere(
      int value, double number, CMIVector3D cmiVector3D) {
    var list = fibonacciSphere(value);
    ArrayList<CMIVector3D> arrayList = new ArrayList(value);

    for (CMIVector3D ringPoint : list) {
      arrayList.add(ringPoint.multiply(number).add(cmiVector3D));
    }

    return arrayList;
  }

  // $VF: Could not inline inconsistent finally blocks
  // Please report this to the Vineflower issue tracker, at
  // https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the
  // rights to distribute it!)
  @CAnnotation(
      priority = 48,
      info = "Administration tools",
      args = "(removeseats/testtarget/convertitems)",
      tab = "removeseats%%testtarget%%testsafe%%updatecommands%%convertitems",
      explanation = {},
      regVar = {1, 2},
      consoleVar = {1, 2},
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    util.actions action = util.actions.getByname(strings[0]);
    if (action == null) {
      return false;
    }

    switch (action) {
      case convertitems:
        if (Version.isCurrentLower(Version.v1_21_R1)
            || Version.isCurrentHigher(Version.v1_21_R3)
            || !Version.isPaperBranch()) {
          CMIMessages.sendMessage(
              commandSender,
              "&7This command is only for Paper type servers and version 1.21.0 to 1.21.4");
          return null;
        }

        File file = new File(CMI.getInstance().getDataFolder(), "Kits");
        for (File kitFile : file.listFiles()) {
          if (kitFile.getName().endsWith(".yml")) {
            try {
              new ConfigReader(kitFile).saveToBackup(false);
            } catch (Exception exception) {
              exception.printStackTrace();
            }
          }
        }

        cmi.getKitsManager().save();

        try {
          File savedItemsFile =
              new File(
                  cmi.getDataFolder(),
                  "Saves" + File.separator + cmi.getSavedItemManager().fileName);
          new ConfigReader(savedItemsFile).saveToBackup(false);
        } catch (Exception saveError2) {
          saveError2.printStackTrace();
        }

        cmi.getSavedItemManager().save();

        try {
          File warpsFile =
              new File(
                  cmi.getDataFolder(), "Saves" + File.separator + cmi.getWarpManager().fileName);
          new ConfigReader(warpsFile).saveToBackup(false);
        } catch (Exception saveError3) {
          saveError3.printStackTrace();
        }

        cmi.getWarpManager().save();

        try {
          File recipesFile =
              new File(
                  cmi.getDataFolder(), "Saves" + File.separator + cmi.getRecipeManager().fileName);
          new ConfigReader(recipesFile).saveToBackup(false);
        } catch (Exception saveError4) {
          saveError4.printStackTrace();
        }

        cmi.getRecipeManager().save();
        CMIMessages.sendMessage(
            commandSender,
            "&7Backup files saved into CMILib FileBackups folder. Item records converted.");
        break;
      case removeseats:
      case removechairs:
        int removedCount = 0;

        for (World world : Bukkit.getWorlds()) {
          for (Chunk chunk : world.getLoadedChunks()) {
            for (Entity entity : chunk.getEntities()) {
              if (entity.getType().equals(EntityType.ARMOR_STAND)
                  && entity.getCustomName() != null
                  && entity.getCustomName().equals("CMIArmorStandForSit")
                  && entity.getPassenger() == null) {
                entity.remove();
                removedCount++;
              }
            }
          }
        }

        CMILC.info(this, commandSender, "removedSits", "[count]", removedCount);
        break;
      case testtarget:
        if (!(commandSender instanceof Player)) {
          return false;
        }

        Player player = (Player) commandSender;
        if (targetingEnabled.containsKey(player)) {
          targetingEnabled.remove(player);
          LC.info_variables_Disabled.sendMessage(commandSender, new Object[0]);
          return true;
        }

        targetingEnabled.put(player, System.currentTimeMillis() + 60000L);
        LC.info_variables_Enabled.sendMessage(commandSender, new Object[0]);
        if (this.sched == null) {
          this.targetTasker();
        }

        return true;
      case testsafe:
        if (!(commandSender instanceof Player)) {
          return false;
        }

        Player safeSender = (Player) commandSender;
        if (safeEnabled.containsKey(safeSender)) {
          safeEnabled.remove(safeSender);
          LC.info_variables_Disabled.sendMessage(commandSender, new Object[0]);
          return true;
        }

        safeEnabled.put(safeSender, System.currentTimeMillis() + 60000L);
        LC.info_variables_Enabled.sendMessage(commandSender, new Object[0]);
        if (this.safeSched == null) {
          this.safeTasker();
        }

        return true;
      case allmobheads:
        CMIUser cmiUser = cmi.getUser(commandSender, null, this);
        if (cmiUser == null) {
          return false;
        }

        Location location = cmiUser.getLocation().clone();
        int headX = 0;
        int headY = 0;
        int headZ = 0;
        int headsPerRow = CMIEntityType.values().length / 6;
        Chunk chunkCopy = null;
        HashMap headsMap = new HashMap();
        for (CMIEntityType cmiEntityType : CMIEntityType.values()) {

          for (String string : cmiEntityType.getHeadTextures()) {
            ItemStack itemStack = cmiEntityType.getHead(string);
            if (itemStack != null) {
              if (headX >= headsPerRow) {
                headY++;
                headZ++;
                headX = 0;
              }

              Block block = location.clone().add(++headX, headY, headZ).getBlock();
              location
                  .clone()
                  .add(headX, headY, headZ + 1)
                  .getBlock()
                  .setType(CMIMaterial.GRAY_WOOL.getMaterial());
              location
                  .clone()
                  .add(headX, headY - 1, headZ)
                  .getBlock()
                  .setType(CMIMaterial.GRAY_WOOL.getMaterial());
              block.setType(CMIMaterial.PLAYER_HEAD.getMaterial());
              Skull skull = (Skull) block.getState();
              GameProfile gameProfile =
                  new GameProfile(UUID.nameUUIDFromBytes(string.getBytes()), "");
              SkinManager.getPropertyMap(gameProfile).removeAll("textures");
              SkinManager.getPropertyMap(gameProfile)
                  .put("textures", new Property("textures", string, null));
              Object headTile =
                  CMILib.getInstance()
                      .getReflectionManager()
                      .getTileEntityAt(skull.getLocation());
              headsMap.put(headTile, gameProfile);
              chunkCopy = skull.getChunk();
            }
          }
        }

        if (chunkCopy != null) {
          chunkCopy.getWorld().refreshChunk(chunkCopy.getX(), chunkCopy.getZ());
        }

        return true;
      case allcommands:
        if (!(commandSender instanceof Player)) {
          for (var entry : cmi.getCommandManager().getCommands().entrySet()) {
            CMILC.sendMessage(
                Bukkit.getConsoleSender(),
                (String) entry.getKey()
                    + " "
                    + ((CMICommand) entry.getValue()).getAnottation().args()
                    + " - "
                    + CMIChatColor.stripColor(
                        ((CMICommand) entry.getValue()).getAnottation().info()));
          }
        }

        return true;
      case allcommandsperm:
        if (!(commandSender instanceof Player)) {
          if (strings.length > 1 && strings[1].equalsIgnoreCase("formated")) {
            String htmlOutput = "<ul>";

            for (var commandEntry : cmi.getCommandManager().getCommands().entrySet()) {
              if (!((CMICommand) commandEntry.getValue()).getAnottation().hidden()) {
                htmlOutput =
                    htmlOutput
                        + "<li><strong>"
                        + (String) commandEntry.getKey()
                        + "</strong> -> "
                        + ((CMICommand) commandEntry.getValue()).getAnottation().info()
                        + "\n<ul>";
                htmlOutput =
                    htmlOutput
                        + "<li>  <strong>cmi.command."
                        + (String) commandEntry.getKey()
                        + "</strong> -> default: <strong>"
                        + (((CMICommand) commandEntry.getValue()).getAnottation().paccess()
                            ? "true"
                            : "op")
                        + "</strong></li>\n";
                if (((CMICommand) commandEntry.getValue()).getAnottation().others()) {
                  htmlOutput =
                      htmlOutput
                          + "<li>  <strong>cmi.command."
                          + (String) commandEntry.getKey()
                          + ".others</strong></li>\n";
                }

                htmlOutput = htmlOutput + "</ul></li>";
              }
            }

            htmlOutput = htmlOutput + "</ul>";
            CMI.getInstance().consoleMessage(htmlOutput);
          } else {
            String yamlOutput = "";

            for (var commandEntryPlain : cmi.getCommandManager().getCommands().entrySet()) {
              if (!((CMICommand) commandEntryPlain.getValue()).getAnottation().hidden()) {
                yamlOutput =
                    yamlOutput + "  cmi.command." + (String) commandEntryPlain.getKey() + ":\n";
                yamlOutput =
                    yamlOutput
                        + "    description: "
                        + ((CMICommand) commandEntryPlain.getValue()).getAnottation().info()
                        + "\n";
                yamlOutput =
                    yamlOutput
                        + "    default: "
                        + (((CMICommand) commandEntryPlain.getValue()).getAnottation().paccess()
                            ? "true"
                            : "op")
                        + "\n";
                if (((CMICommand) commandEntryPlain.getValue()).getAnottation().others()) {
                  yamlOutput =
                      yamlOutput
                          + "  cmi.command."
                          + (String) commandEntryPlain.getKey()
                          + ".others:\n";
                  yamlOutput = yamlOutput + "    default: op\n";
                }
              }
            }

            CMI.getInstance().consoleMessage(yamlOutput);
          }
        }

        return true;
      case allpermissions:
        if (commandSender instanceof Player) {
          return true;
        }

        if (strings.length > 1 && strings[1].equalsIgnoreCase("formated")) {
          Bukkit.getConsoleSender().sendMessage("<ul>");
          for (PermissionsManager.CMIPerm cmiPerm : PermissionsManager.CMIPerm.values()) {
            if (cmiPerm.getShow()) {
              StringBuilder stringBuilder = new StringBuilder();
              stringBuilder.append(
                  "<li><strong>"
                      + CMIChatColor.stripColor(
                          cmiPerm.getPermissionForShow() + "</strong> - " + cmiPerm.getDesc())
                      + "</li>");
              Bukkit.getConsoleSender().sendMessage(stringBuilder.toString());
            }
          }

          Bukkit.getConsoleSender().sendMessage("</ul>");
        } else {
          for (PermissionsManager.CMIPerm perm : PermissionsManager.CMIPerm.values()) {
            if (perm.getShow()) {
              CMIMessages.consoleMessage(
                  CMIChatColor.stripColor(perm.getPermissionForShow() + " - " + perm.getDesc()));
            }
          }
        }

        return true;
      case alltabcompletes:
        if (commandSender instanceof Player) {
          return true;
        }

        if (strings.length > 1 && strings[1].equalsIgnoreCase("formated")) {
          Bukkit.getConsoleSender().sendMessage("<ul>");
          for (TabComplete.TabAction tabAction : TabComplete.TabAction.values()) {
            if (tabAction.getDesc() != null) {
              Bukkit.getConsoleSender()
                  .sendMessage(
                      "<li><strong>"
                          + CMIChatColor.stripColor(
                              "[" + tabAction.name() + "]</strong> - " + tabAction.getDesc())
                          + "</li>");
            }
          }

          Bukkit.getConsoleSender().sendMessage("</ul>");
        } else {
          for (TabComplete.TabAction tabActionLoop : TabComplete.TabAction.values()) {
            if (tabActionLoop.getDesc() != null) {
              Bukkit.getConsoleSender()
                  .sendMessage(
                      CMIChatColor.stripColor(
                          "[" + tabActionLoop.name() + "] - " + tabActionLoop.getDesc()));
            }
          }
        }

        return true;
      case allplaceholders:
        if (commandSender instanceof Player) {
          return true;
        }

        if (strings.length > 1 && strings[1].equalsIgnoreCase("formated")) {
          Bukkit.getConsoleSender().sendMessage("<ul>");
          for (Placeholder.CMIPlaceHolders cmiPlaceHolders : Placeholder.CMIPlaceHolders.values()) {
            StringBuilder placeholderBuilder = new StringBuilder();
            placeholderBuilder.append(
                "<strong>" + CMIChatColor.stripColor(cmiPlaceHolders.getFull()) + "</strong>");
            if (cmiPlaceHolders.getDescription() != null
                && !cmiPlaceHolders.getDescription().isEmpty()) {
              placeholderBuilder.append(" - " + cmiPlaceHolders.getDescription());
            }

            Bukkit.getConsoleSender().sendMessage("<li>" + placeholderBuilder.toString() + "</li>");
          }

          Bukkit.getConsoleSender().sendMessage("</ul>");
        } else {
          for (Placeholder.CMIPlaceHolders placeholderEntry :
              Placeholder.CMIPlaceHolders.values()) {
            Bukkit.getConsoleSender()
                .sendMessage(CMIChatColor.stripColor(placeholderEntry.getFull()));
          }
        }

        return true;
      case allplayeroptions:
        StringBuilder optionsBuilder = new StringBuilder();
        for (PlayerOption playerOption : PlayerOption.values()) {
          if (!optionsBuilder.toString().isEmpty()) {
            optionsBuilder.append("|");
          }

          optionsBuilder.append(playerOption.getShortName().toLowerCase());
        }

        Bukkit.getConsoleSender().sendMessage(optionsBuilder.toString());
        return true;
      case updatecommands:
        String targetName = null;
        if (strings.length > 1) {
          targetName = strings[1];
        }

        Player targetPlayer = cmi.getTarget(commandSender, targetName, this);
        if (targetPlayer != null && targetPlayer.isOnline()) {
          targetPlayer.updateCommands();
        }

        PluginCommand pluginCommand = CMI.getInstance().getCommand("sethome");
        if (pluginCommand != null) {
          CMILC.sendMessage(
              commandSender,
              pluginCommand.getName()
                  + " -> "
                  + pluginCommand.getLabel()
                  + " -> "
                  + pluginCommand.getPermission()
                  + " -> "
                  + pluginCommand.isRegistered()
                  + " ");
        } else {
          CMILC.sendMessage(commandSender, "Null command");
        }
        break;
      case emptyspace:
        if (!(commandSender instanceof Player)) {
          return false;
        }

        Player senderPlayer = (Player) commandSender;
        CMIHitBox cmiHitBox = new CMIHitBox(senderPlayer);
        Block collidingBlock = cmiHitBox.getCollidingdBlock();
        CMILC.sendMessage(commandSender, "&7Free space: &f" + (collidingBlock == null));
        if (collidingBlock != null) {
          CMILC.sendMessage(
              commandSender,
              "&7Collides with: &f"
                  + collidingBlock.getType().toString()
                  + " &7at &f"
                  + collidingBlock.getLocation().toVector().toString());
        }
        break;
      case testmaterials:
        ArrayList<String> arrayList = new ArrayList();
        arrayList.add("52");
        arrayList.add("wool");
        arrayList.add("wool:15");
        arrayList.add("blackwool");
        arrayList.add("door");
        arrayList.add("oak_door");
        arrayList.add("black_wool");
        arrayList.add("MUSICDISC11");
        arrayList.add("35:15");
        arrayList.add("5:6");
        arrayList.add("fake");
        arrayList.add("stone");
        arrayList.add("redstone");
        arrayList.add("sugarcane");
        arrayList.add("24164");
        ArrayList<Material> materialList = new ArrayList();
        for (Material material : Material.values()) {
          if (material != null && !material.toString().startsWith("LEGACY_")) {
            materialList.add(material);
          }
        }

        int successCount = 0;
        ArrayList<String> failedStrings = new ArrayList();
        ArrayList<Material> failedMaterials = new ArrayList();
        Long startNanos = System.nanoTime();

        for (String materialName : arrayList) {
          CMIMaterial cmiMaterial = CMIMaterial.get(materialName);
          if (cmiMaterial != null && !cmiMaterial.equals(CMIMaterial.NONE)) {
            successCount++;
          } else {
            failedStrings.add(materialName);
          }
        }

        for (Material listedMaterial : materialList) {
          CMIMaterial listedCmiMaterial = CMIMaterial.get(listedMaterial);
          if (listedCmiMaterial != null && !listedCmiMaterial.equals(CMIMaterial.NONE)) {
            successCount++;
          } else {
            failedMaterials.add(listedMaterial);
          }
        }

        CMILC.sendMessage(
            commandSender,
            "New Checked &7"
                + (arrayList.size() + materialList.size())
                + " &fmaterials in &7"
                + (System.nanoTime() - startNanos) * 1000L / 1000000.0
                + "ms");
        CMILC.sendMessage(
            commandSender,
            "New &2Success: "
                + successCount
                + " &cFailed "
                + (failedStrings.size() + failedMaterials.size()));
        if (!failedStrings.isEmpty()) {
          CMILC.sendMessage(
              commandSender, "New String fails: " + CMIList.listToString(failedStrings, ", "));
        }

        if (!failedMaterials.isEmpty()) {
          failedStrings.clear();
          TreeMap<String, Material> treeMap = new TreeMap();

          for (Material failedMaterial : failedMaterials) {
            treeMap.put(failedMaterial.toString(), failedMaterial);
          }

          StringBuilder failsBuilder = new StringBuilder();

          for (Entry materialTreeEntry : treeMap.entrySet()) {
            failsBuilder.append("\n");
            failsBuilder.append(((Material) materialTreeEntry.getValue()).toString() + ",");
          }

          CMILC.sendMessage(commandSender, "New Material fails: " + failsBuilder.toString());
        }

        return true;
      case testentities:
        for (EntityType entityType : EntityType.values()) {
          CMIEntityType entityRecord = CMIEntityType.get(entityType);
          if (entityRecord == null) {
            CMILC.sendMessage(
                commandSender,
                "Missing entity record: " + entityType + " " + entityType.getTypeId());
          }
        }

        return true;
      case testbiomes:
        for (Biome biome : Biome.values()) {
          CMIBiomeType cmiBiomeType = CMIBiomeType.get(CMIBiome.getName(biome));
          if (cmiBiomeType == null) {
            CMILC.sendMessage(commandSender, "Missing biome record: " + biome);
          }
        }

        return true;
      case drivetest:
        LC.info_Spliter.sendMessage(commandSender, new Object[0]);

        try {
          NumberFormat numberFormat = NumberFormat.getNumberInstance();

          for (Path path : FileSystems.getDefault().getRootDirectories()) {
            try {
              FileStore fileStore = Files.getFileStore(path);
              CMILC.sendMessage(
                  commandSender,
                  "&c"
                      + fileStore.name()
                      + " &2"
                      + numberFormat.format(
                          (int) (fileStore.getUsableSpace() / 1024.0 / 1024.0 / 1024.0 * 100.0)
                              / 100.0)
                      + "&f/&7"
                      + numberFormat.format(
                          (int) (fileStore.getTotalSpace() / 1024.0 / 1024.0 / 1024.0 * 100.0)
                              / 100.0)
                      + "GB ");
            } catch (IOException ioException) {
              CMILC.sendMessage(
                  commandSender, "&fError while checking space: &7" + ioException.toString());
            }
          }
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }

        try {
          CMILC.sendMessage(
              commandSender,
              "&63# Free space: "
                  + convertToGB(new File(cmi.getDataFolder().getPath()).getFreeSpace() / 1024L));
          CMILC.sendMessage(
              commandSender,
              "&63# Total space: "
                  + convertToGB(new File(cmi.getDataFolder().getPath()).getTotalSpace() / 1024L));
          CMILC.sendMessage(
              commandSender,
              "&63# Usable space: "
                  + convertToGB(new File(cmi.getDataFolder().getPath()).getUsableSpace() / 1024L));
        } catch (Throwable throwable2) {
          throwable2.printStackTrace();
        }
        break;
      case testrandomsafe:
        Player senderPlayerB = (Player) commandSender;
        LC.info_Spliter.sendMessage(commandSender, new Object[0]);
        cmi.getTeleportManager().setSafeLocationDownThenUp(true);
        CMILC.sendMessage(commandSender, "&7-------------DownThenUp-----------");
        safeLocationTest(commandSender, senderPlayerB, senderPlayerB.getLocation());
        break;
      case placeholdertest:
        var list =
            Arrays.asList(
                "cmi_user_name",
                "cmi_user_playtime_days",
                "cmi_user_flightcharge",
                "cmi_user_prefix",
                "cmi_user_totalexp",
                "cmi_user_afk_for",
                "cmi_user_god",
                "cmi_user_op",
                "cmi_user_charges_cooldown",
                "cmi_user_charges_cooldown",
                "cmi_user_charges_cooldown",
                "cmi_user_muted",
                "cmi_user_nickname",
                "cmi_user_nick",
                "cmi_nickname",
                "cmi",
                "cmi_usernickname",
                "cmi_worth_buy_stone",
                "server_time_HH:mm:ss_GMT+5",
                "server_time_HH:mm:ss",
                "world_time12_user",
                "world_time24_user",
                "server_online_survival",
                "cmi_random_1_5",
                "cmi_lastrandom_user",
                "cmi_user_rank_percent_user",
                "cmi_user_meta_user",
                "cmi_user_metaint_user",
                "cmi_user_kitcd_user",
                "cmi_user_kit_available_user",
                "cmi_user_kit_hasaccess_user",
                "cmi_jail_time_user_user",
                "cmi_jail_username_user_user",
                "cmi_jail_reason_user_user",
                "cmi_tps_5_colored",
                "cmi_schedule_nextin_something",
                "cmi_schedule_endat_something",
                "cmi_baltop_name_1",
                "cmi_baltop_money_1",
                "cmi_votetop_1",
                "cmi_votetopcount_1",
                "cmi_worth_buy_stone",
                "cmi_bungee_total_creative",
                "cmi_worth_sell_stone",
                "cmi_bungee_current_creative",
                "cmi_bungee_motd_creative",
                "cmi_bungee_onlinestatus_creative");
        int foundCount = 0;
        CMIDebug.it();

        for (String checkName : list) {
          if (Placeholder.CMIPlaceHolders.getByName(checkName) != null) {
            foundCount++;
          }
        }

        CMILC.sendMessage(
            commandSender,
            "Checked by name "
                + list.size()
                + " Took: "
                + CMIDebug.getIT()
                + "ms &2found:&7"
                + foundCount);
        foundCount = 0;
        CMIDebug.it();

        for (String checkExact : list) {
          if (Placeholder.CMIPlaceHolders.getByNameExact(checkExact) != null) {
            foundCount++;
          }
        }

        CMILC.sendMessage(
            commandSender,
            "Checked by exact name "
                + list.size()
                + " Took: "
                + CMIDebug.getIT()
                + "ms &2found:&7"
                + foundCount);
        foundCount = 0;
        CMIDebug.it();

        for (String checkNewName : list) {
          Placeholder.CMIPlaceHolders placeholderByName =
              Placeholder.CMIPlaceHolders.getByName(checkNewName);
          if (placeholderByName != null) {
            foundCount++;
          }
        }

        CMILC.sendMessage(
            commandSender,
            "Checked by new name "
                + list.size()
                + " Took: "
                + CMIDebug.getIT()
                + "ms &2found:&7"
                + foundCount);
        foundCount = 0;
        CMIDebug.it();

        for (String checkNewExact : list) {
          if (Placeholder.CMIPlaceHolders.getByNameExact(checkNewExact) != null) {
            foundCount++;
          }
        }

        CMILC.sendMessage(
            commandSender,
            "Checked by new exact name "
                + list.size()
                + " Took: "
                + CMIDebug.getIT()
                + "ms &2found:&7"
                + foundCount);
        break;
      case statistictest:
        for (Statistic statistic : Statistic.values()) {
          if (StatsManager.CMIStatistic.getByName(statistic.toString()) == null) {
            CMIMessages.consoleMessage(statistic.toString());
          }
        }
        break;
      case colorstoweb:
        String colorsHtml = "<tr style=\"height: 20px;\">\n";
        int colorsPerRow = 0;

        for (var colorEntry : CMIChatColor.getByCustomName().entrySet()) {
          if (++colorsPerRow >= 4) {
            colorsHtml = colorsHtml + "</tr>\n";
            colorsHtml = colorsHtml + "<tr style=\"height: 20px;\">\n";
            colorsPerRow = 1;
          }

          colorsHtml =
              colorsHtml
                  + "<td style=\"width: 8%; height: 20px; text-align: right;\"><strong>"
                  + (String) colorEntry.getKey()
                  + "</strong></td>\n";
          colorsHtml =
              colorsHtml
                  + "<td style=\"width: 8%; height: 20px;\">"
                  + ((CMIChatColor) colorEntry.getValue()).getColorCode()
                  + "</td>\n";
          colorsHtml =
              colorsHtml
                  + "<td style=\"width: 20px; background-color: #"
                  + ((CMIChatColor) colorEntry.getValue()).getHex()
                  + "; height: 20px;\"></td>\n";
        }

        if (colorsPerRow != 4) {
          colorsHtml = colorsHtml + "</tr>\n";
        }

        Bukkit.getConsoleSender().sendMessage(colorsHtml);
        return true;
      case colortest:
        String colorInput = "{#blue} thing &2{#red} test";
        String colorLast = CMIChatColor.getLastColors(colorInput);
        CMILC.sendMessage(
            commandSender,
            "Check 1: "
                + (CMIChatColor.deColorize(colorLast).equalsIgnoreCase("{#red}")
                    ? "&2OK"
                    : "&cFail"));
        colorInput = "{#blue} thing &2{#red} &2test";
        colorLast = CMIChatColor.getLastColors(colorInput);
        CMILC.sendMessage(
            commandSender,
            "Check 2: "
                + (CMIChatColor.deColorize(colorLast).equalsIgnoreCase("&2") ? "&2OK" : "&cFail"));
        colorInput = "{#blue} thing &2{#6600cc} test";
        colorLast = CMIChatColor.getLastColors(colorInput);
        CMILC.sendMessage(
            commandSender,
            "Check 3: "
                + (CMIChatColor.deColorize(colorLast).equalsIgnoreCase("{#6600cc}")
                    ? "&2OK"
                    : "&cFail"));
        break;
      case mobtypes:
        if (!(commandSender instanceof Player)) {
          LC.info_Ingame.sendMessage(commandSender, new Object[0]);
          return null;
        }

        Player senderPlayerC = (Player) commandSender;
        Location spawnLocation = senderPlayerC.getLocation().clone().add(1.0, 0.0, 1.0);
        TreeMap<String, TreeMap> mobsByCategory = new TreeMap();
        for (EntityType mobType : EntityType.values()) {
          if (mobType.isAlive()) {
            Entity spawnedEntity = null;

            try {
              spawnedEntity = spawnLocation.getWorld().spawnEntity(spawnLocation, mobType);
            } catch (Exception exception5) {
              continue;
            }

            String mobCategory = "";
            if (spawnedEntity instanceof Animals) {
              mobCategory = "&2Animal";
            } else if (spawnedEntity instanceof Monster) {
              mobCategory = "&cMonster";
            } else if (spawnedEntity instanceof Ambient) {
              mobCategory = "&7Ambient";
            } else if (spawnedEntity instanceof NPC) {
              mobCategory = "&6NPC";
            } else if (spawnedEntity instanceof WaterMob) {
              mobCategory = "&eWaterMob";
            }

            TreeMap mobsOfCategory = mobsByCategory.getOrDefault(mobCategory, new TreeMap());
            mobsOfCategory.put(mobType.toString(), spawnedEntity);
            mobsByCategory.put(mobCategory, mobsOfCategory);
            spawnedEntity.remove();
          }
        }

        for (Entry categoryEntry : mobsByCategory.entrySet()) {
          for (Entry mobEntry : ((java.util.Map<?, ?>) categoryEntry.getValue()).entrySet()) {
            CMILC.sendMessage(
                commandSender, (String) categoryEntry.getKey() + " " + (String) mobEntry.getKey());
          }
        }
        break;
      case materials:
        StringBuilder materialsBuilder = new StringBuilder();
        for (CMIMaterial materialEntry : CMIMaterial.values()) {
          materialsBuilder.append("\n");
          materialsBuilder.append(materialEntry.toString());
        }

        cmi.consoleMessage(materialsBuilder.toString());
        break;
      case test:
        ItemStack heldItem = CMIItemStack.getItemInMainHand((Player) commandSender);
        ItemStack axeItem = CMIMaterial.DIAMOND_AXE.newItemStack();
        CMIComponentBase cmiComponentBase =
            CMIComponentBase.builder(true)
                .add(
                    new CMIComponentItem(
                            " item ",
                            heldItem != null && !heldItem.getType().equals(Material.AIR)
                                ? heldItem
                                : axeItem)
                        .setClickAction(CMIComponentActionType.open_url, "www.google.com"));
        cmiComponentBase.sendMessage(commandSender);
        cmiComponentBase =
            CMIComponentBase.builder(true)
                .add(new CMIComponentText("<shadow:black:0.5>Test</shadow>"))
                .add(new CMIComponentText(" <shadow:red:0.5>second title?</shadow>"))
                .add(
                    1,
                    new CMIComponentText("<shadow:yellow>Hello <shadow:aqua:0.5>World</shadow>!"));
        cmiComponentBase.showTitle(commandSender, 1000, 5000, 1000);
        cmiComponentBase =
            CMIComponentBase.builder(true)
                .add(new CMIComponentText("<shadow:black:0.5>Test"))
                .add(new CMIComponentText(" <shadow:red:0.5>second title?"));
        cmiComponentBase.sendActionBar(commandSender, 5000);
        cmiComponentBase =
            CMIComponentBase.builder(true)
                .add(
                    new CMIComponentText(
                        "<yellow>Woo:"
                            + " <gradient:#5e4fa2:#f79459:red>||||||||||||||||||||||||</gradient>!"))
                .add(new CMIComponentText(" <shadow:red:0.5>second title?\n</shadow>"))
                .add(new CMIComponentText(" <shadow:red:0.5>Second line?</shadow>"))
                .add(1, new CMIComponentText("<shadow:#5e4fa2:0.5><red>Footer</shadow>"))
                .add(1, new CMIComponentText(" <shadow:white:0.5><red>second?</shadow>"));
        cmiComponentBase.sendPlayerListHeaderAndFooter(commandSender);
        ItemStack bookItem =
            cmiComponentBase.convertIntoBook("Test title", commandSender.getName());
        ((Player) commandSender).getInventory().addItem(new ItemStack[] {bookItem});
        Block signBlock =
            CMI.getInstance().getUtilManager().getTargetBlock((Player) commandSender, 50);
        if (CMIMaterial.isSign(signBlock.getType())) {
          cmiComponentBase =
              CMIComponentBase.builder(true)
                  .add(0, new CMIComponentText("s1"))
                  .add(1, new CMIComponentText("{#red>}Headers{#brown<}"))
                  .add(
                      2, new CMIComponentText("<click:open_url:www.google.com> <red>URLs </click>"))
                  .add(3, new CMIComponentText(""))
                  .add(4, new CMIComponentText("5"))
                  .add(5, new CMIComponentText("6"))
                  .add(6, new CMIComponentText("7"))
                  .add(7, new CMIComponentText("8"));
          cmiComponentBase.applyOnSign(signBlock, (Player) commandSender);
        }
      case test2:
      case test3:
      case placetnt:
      default:
        break;
      case jsontest:
        HashMap jsonTestMap = new HashMap();
        jsonTestMap.put("intVal", 42);
        jsonTestMap.put("negInt", -123);
        jsonTestMap.put("longVal", Long.MAX_VALUE);
        jsonTestMap.put("zero", 0);
        jsonTestMap.put("doubleVal", 12345.6789);
        jsonTestMap.put("sciNotation", 123000.0);
        jsonTestMap.put("tinyDouble", 1.23E-7);
        jsonTestMap.put("bigNegativeDouble", -9.87654321E99);
        jsonTestMap.put("nanVal", Double.NaN);
        jsonTestMap.put("posInf", Double.POSITIVE_INFINITY);
        jsonTestMap.put("negInf", Double.NEGATIVE_INFINITY);
        jsonTestMap.put("bigDecimal", new BigDecimal("1234567890.1234567890123456789"));
        jsonTestMap.put("bigInteger", new BigInteger("999999999999999999999999999999999"));
        jsonTestMap.put("boolTrue", true);
        jsonTestMap.put("boolFalse", false);
        jsonTestMap.put("nullVal", null);
        jsonTestMap.put("emptyStr", "");
        jsonTestMap.put("escapedStr", "He said \"hi\" \\\\ test \n tab\t end");
        jsonTestMap.put("unicodeStr", "你好 \ud83d\ude00");
        jsonTestMap.put("emojiMix", "\ud83d\udd25\ud83d\udca7\ud83c\udf33\ud83c\udf0d");
        jsonTestMap.put("jsonLike", "{\"pretend\": [1,2,3]}");
        jsonTestMap.put("onlyBackslashes", "\\\\\\");
        jsonTestMap.put("quotesInside", "\"Quoted\" word");
        jsonTestMap.put("brokenSurrogate", "\ud83d");
        jsonTestMap.put("invalidLowSurrogate", "\ude0d");
        jsonTestMap.put("embeddedControl", "abc\u0001def\u001fghi");
        jsonTestMap.put("allEscapes", "\" \\ / \b \f \n \r \t");
        jsonTestMap.put("intList", Arrays.asList(1, "a", 3, null));
        jsonTestMap.put(
            "nestedList", Arrays.asList(Arrays.asList("a", "b"), Arrays.asList("c", "d")));
        jsonTestMap.put("emptyList", new ArrayList());
        jsonTestMap.put(
            "mixedList",
            Arrays.asList(123, "string", true, null, Arrays.asList(1, 2), new HashMap()));
        jsonTestMap.put(
            "deepNestedList",
            Arrays.asList(
                Arrays.asList(Arrays.asList(Arrays.asList("deep"))),
                Arrays.asList(Arrays.asList(Arrays.asList(999)))));
        HashMap innerMap = new HashMap();
        innerMap.put("innerKey1", "value1");
        innerMap.put("innerKey2", Arrays.asList(10, 20));
        jsonTestMap.put("mapInMap", innerMap);
        jsonTestMap.put("emptyMap", new HashMap());
        HashMap<String, String> weirdKeysMap = new HashMap();
        weirdKeysMap.put("123", "numKeyAsString");
        weirdKeysMap.put("spaced key", "with space");
        weirdKeysMap.put("specialChars{}[],\"", "tricky");
        weirdKeysMap.put("unicodeKey你好", "unicodeVal");
        weirdKeysMap.put("escapedKey\\\"quote", "escapedInKey");
        weirdKeysMap.put("", "emptyKeyVal");
        jsonTestMap.put("weirdKeysMap", weirdKeysMap);
        HashMap<String, String> level3Map = new HashMap();
        level3Map.put("level3", "deep");
        HashMap<String, HashMap> level2Map = new HashMap();
        level2Map.put("level2", level3Map);
        HashMap<String, HashMap> level1Map = new HashMap();
        level1Map.put("level1", level2Map);
        jsonTestMap.put("deepMap", level1Map);
        HashMap mixedMap = new HashMap();
        mixedMap.put("listInside", Arrays.asList("x", "y", "z"));
        mixedMap.put("mapInside", new HashMap<>(Map.of("a", 1, "b", 2)));
        mixedMap.put("boolInside", false);
        mixedMap.put("nullInside", null);
        jsonTestMap.put("mixedMap", mixedMap);
        jsonTestMap.put("veryLongString", "x".repeat(10000));
        jsonTestMap.put("stringWithAllControls", "\b\f\n\r\t");
        jsonTestMap.put("singleCharString", "A");
        jsonTestMap.put("singleElemList", Arrays.asList("onlyOne"));
        jsonTestMap.put("singleElemMap", new HashMap<>(Map.of("onlyKey", "onlyValue")));
        HashMap<Integer, String> intKeyMap = new HashMap();
        intKeyMap.put(123, "intKey");
        jsonTestMap.put("intKeyMap", intKeyMap);
        jsonTestMap.put("mixedNumbers", Arrays.asList(1, 1L, 1.0F, 1.0, new BigDecimal("1.1")));
        jsonTestMap.put("arrayOfEmpty", Arrays.asList(new ArrayList(), new ArrayList()));
        HashMap extremeRoot = new HashMap();
        HashMap extremeCursor = extremeRoot;

        for (int extremeIndex = 0; extremeIndex < 1000; extremeIndex++) {
          HashMap extremeNext = new HashMap();
          extremeCursor.put("d" + extremeIndex, extremeNext);
          extremeCursor = extremeNext;
        }

        jsonTestMap.put("extremeDepth", extremeRoot);
        HashMap nullKeyMap = new HashMap();
        nullKeyMap.put(null, "val");
        jsonTestMap.put("listWithNullKeyMap", Arrays.asList(nullKeyMap));
        String jsonOriginal = jsonTestMap.toString();
        String jsonSerialized = CMIJson.serialize(jsonTestMap);
        var deserializedMap = CMIJson.deserialize(jsonSerialized);
        String deserializedString = deserializedMap.toString();
        String reserializedJson = CMIJson.serialize(deserializedMap);
        String jsonLibString = new JSONObject(jsonTestMap).toString();
        if (jsonOriginal
            .replace("=NaN", "=null")
            .replace("=Infinity", "=null")
            .replace("=-Infinity", "=null")
            .equals(deserializedString.replace("E+", "E"))) {
          Bukkit.getConsoleSender().sendMessage(CMIChatColor.GREEN + "CORRECT deserialization");
        } else {
          Bukkit.getConsoleSender().sendMessage(CMIChatColor.RED + "FAIL deserialization");
          Bukkit.getConsoleSender()
              .sendMessage(
                  "Original: "
                      + jsonOriginal
                          .replace("=NaN", "=null")
                          .replace("=Infinity", "=null")
                          .replace("=-Infinity", "=null"));
          Bukkit.getConsoleSender()
              .sendMessage("Deserial: " + deserializedString.replace("E+", "E"));
        }

        if (jsonSerialized.equals(reserializedJson)) {
          Bukkit.getConsoleSender().sendMessage(CMIChatColor.GREEN + "CORRECT serialization");
        } else {
          Bukkit.getConsoleSender().sendMessage(CMIChatColor.RED + "FAIL serialization");
          Bukkit.getConsoleSender().sendMessage("Original:     " + jsonSerialized);
          Bukkit.getConsoleSender().sendMessage("Deserialized: " + reserializedJson);
        }

        if (jsonLibString.equals(reserializedJson)) {
          Bukkit.getConsoleSender()
              .sendMessage(CMIChatColor.GREEN + "CORRECT by json serialization");
        } else {
          Bukkit.getConsoleSender().sendMessage(CMIChatColor.RED + "FAIL by json serialization");
          Bukkit.getConsoleSender().sendMessage("Json: " + jsonLibString);
          Bukkit.getConsoleSender().sendMessage("CMI : " + reserializedJson);
        }
        break;
      case placeblock:
        if (!(commandSender instanceof Player)) {
          return false;
        }

        Player blockPlayer = (Player) commandSender;
        blockPlayer
            .getLocation()
            .clone()
            .add(0.0, -1.0, 0.0)
            .getBlock()
            .setType(Material.WHITE_WOOL);
        break;
      case placeplatform:
        if (!(commandSender instanceof Player)) {
          return false;
        }

        Player platformPlayer = (Player) commandSender;

        for (int platformX = -20; platformX < 20; platformX++) {
          for (int platformZ = -20; platformZ < 20; platformZ++) {
            Block platformBlock =
                platformPlayer.getLocation().clone().add(platformX, -1.0, platformZ).getBlock();
            if (CMINumber.abs(
                        platformBlock.getChunk().getX() >> 5 + platformBlock.getChunk().getZ() >> 5)
                    % 2
                == 0) {
              if (CMINumber.abs(platformBlock.getChunk().getX() + platformBlock.getChunk().getZ())
                      % 2
                  == 0) {
                platformBlock.setType(Material.GRAY_WOOL);
              } else {
                platformBlock.setType(Material.WHITE_WOOL);
              }
            } else if (CMINumber.abs(
                        platformBlock.getChunk().getX() + platformBlock.getChunk().getZ())
                    % 2
                == 0) {
              platformBlock.setType(Material.BLACK_WOOL);
            } else {
              platformBlock.setType(Material.LIGHT_GRAY_WOOL);
            }
          }
        }
        break;
      case cleararea:
        if (!(commandSender instanceof Player)) {
          return false;
        }

        Player clearPlayer = (Player) commandSender;

        for (int clearX = -10; clearX < 10; clearX++) {
          for (int clearZ = -10; clearZ < 10; clearZ++) {
            for (int clearY = 0; clearY < 10; clearY++) {
              Block clearBlock =
                  clearPlayer.getLocation().clone().add(clearX, clearY, clearZ).getBlock();
              clearBlock.setType(Material.AIR);
            }
          }
        }
        break;
      case imortaldebug:
        imortaldebug = !imortaldebug;
        if (imortaldebug) {
          LC.modify_enabled.sendMessage(commandSender, new Object[0]);
        } else {
          LC.modify_disabled.sendMessage(commandSender, new Object[0]);
        }
        break;
      case fakeclickevents:
        Player clickPlayer =
            commandSender instanceof Player ? (Player) commandSender : Bukkit.getPlayer("Zhax");
        if (this.scheduledFuture != null) {
          this.scheduledFuture.cancel(true);
          this.executor.shutdown();
          this.scheduledFuture = null;
          return true;
        }

        this.executor = Executors.newScheduledThreadPool(1);
        byte b = 0;
        this.scheduledFuture =
            this.executor.scheduleAtFixedRate(
                () -> {
                  Random random = new Random();
                  int sleepMs = random.nextInt(100) + 1;

                  try {
                    Thread.sleep(sleepMs);
                  } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                  }

                  try {
                    CMIScheduler.runTask(
                        cmi,
                        () -> {
                          if (clickPlayer.getItemOnCursor() != null
                              && clickPlayer.getItemOnCursor().getType() != Material.AIR) {
                            clickPlayer.getInventory().setItem(b, clickPlayer.getItemOnCursor());
                            clickPlayer.setItemOnCursor(null);
                            InventoryClickEvent inventoryClickEvent =
                                new InventoryClickEvent(
                                    clickPlayer.getOpenInventory(),
                                    SlotType.CONTAINER,
                                    b,
                                    ClickType.LEFT,
                                    InventoryAction.PLACE_ALL);
                            Bukkit.getPluginManager().callEvent(inventoryClickEvent);
                          } else {
                            PlayerInventory playerInventory = clickPlayer.getInventory();
                            clickPlayer.setItemOnCursor(playerInventory.getItem(b));
                            playerInventory.setItem(b, null);
                            clickPlayer.updateInventory();
                            InventoryClickEvent secondClickEvent =
                                new InventoryClickEvent(
                                    clickPlayer.getOpenInventory(),
                                    SlotType.CONTAINER,
                                    b,
                                    ClickType.LEFT,
                                    InventoryAction.PICKUP_ALL);
                            Bukkit.getPluginManager().callEvent(secondClickEvent);
                          }
                        });
                  } catch (Throwable throwable3) {
                    throwable3.printStackTrace();
                  }
                },
                0L,
                10L,
                TimeUnit.MILLISECONDS);
        break;
      case dialogs:
        if (!Version.isTestServer()) {
          return false;
        }

        File dialogsDir = new File("plugins/cmi/dialogs");
        String exampleName = "ExampleFirst.yml";
        short copies = 500;
        File exampleFile = new File(dialogsDir, exampleName);
        if (!exampleFile.exists()) {
          System.out.println("File not found: " + exampleFile.getAbsolutePath());
          return false;
        }

        Yaml yamlReader = new Yaml();
        Map yamlMap = null;

        try (FileReader fileReader = new FileReader(exampleFile)) {
          yamlMap = (Map) yamlReader.load(fileReader);
        } catch (FileNotFoundException fileNotFoundException) {
          fileNotFoundException.printStackTrace();
        } catch (IOException ioException2) {
          ioException2.printStackTrace();
        }

        if (yamlMap == null || yamlMap.isEmpty()) {
          System.out.println("Empty or invalid YAML file.");
          return false;
        }

        for (int yamlIndex = 1; yamlIndex <= copies; yamlIndex++) {
          LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap();

          for (Entry dialogEntry : ((java.util.Map<?, ?>) yamlMap).entrySet()) {
            linkedHashMap.put((String) dialogEntry.getKey() + yamlIndex, dialogEntry.getValue());
          }

          DumperOptions dumperOptions = new DumperOptions();
          dumperOptions.setDefaultFlowStyle(FlowStyle.BLOCK);
          dumperOptions.setIndent(2);
          dumperOptions.setPrettyFlow(true);
          Yaml yamlWriter = new Yaml(dumperOptions);
          File outputFile = new File(dialogsDir, exampleName.replace(".yml", yamlIndex + ".yml"));

          try (FileWriter fileWriter = new FileWriter(outputFile)) {
            yamlWriter.dump(linkedHashMap, fileWriter);
          } catch (IOException ioException3) {
            ioException3.printStackTrace();
          }

          System.out.println("Created: " + outputFile.getName());
        }
        break;
      case gc:
        System.gc();
    }

    return true;
  }

  private static void safeLocationTest(
      CommandSender commandSender, Player player, Location location) {
    byte b = 16;
    processSafe(b + " chunks by x", commandSender, player, location.clone().add(b * 16, 0.0, 0.0));
  }

  private static void processSafe(
      String string, CommandSender commandSender, Player player, Location location) {
    boolean flag =
        location.getWorld().isChunkGenerated(location.getBlockX() >> 4, location.getBlockZ() >> 4);
    Long startTime = System.nanoTime();
    var completableFuture =
        CMI.getInstance().getTeleportHandler().getSafeLocation(player, location, 1, 2);
    double number = (int) ((System.nanoTime() - startTime) / 1000000.0 * 100.0) / 100.0;
  }

  private static double convertToGB(Long bytes) {
    return (int) (bytes.longValue() / 1024.0 / 1024.0 * 100.0) / 100.0;
  }

  private void targetTasker() {
    if (this.sched == null) {
      this.sched =
          CMIScheduler.scheduleSyncRepeatingTask(
              CMI.getInstance(),
              () -> {
                if (targetingEnabled.isEmpty()) {
                  this.sched.cancel();
                  this.sched = null;
                } else {
                  for (var entry : targetingEnabled.entrySet()) {
                    if (!((Player) entry.getKey()).isOnline()
                        || (Long) entry.getValue() < System.currentTimeMillis()) {
                      targetingEnabled.remove(entry.getKey());
                      LC.info_variables_Disabled.sendMessage(entry.getKey(), new Object[0]);
                      break;
                    }

                    Player player = (Player) entry.getKey();
                    Block block = CMI.getInstance().getUtilManager().getTargetBlock(player, 50);
                    Entity entity =
                        CMI.getInstance().getUtilManager().getTargetEntity(player, 0.0, true, true);
                    CMIActionBar.send(
                        player,
                        block == null
                            ? ""
                            : block.getType().toString()
                                + (entity == null
                                    ? ""
                                    : (entity.getType().isAlive()
                                        ? " &2" + entity.getType()
                                        : " &e" + entity.getType())));
                  }
                }
              },
              1L,
              1L);
    }
  }

  private void safeTasker() {
    if (this.safeSched == null) {
      this.safeSched =
          CMIScheduler.scheduleSyncRepeatingTask(
              CMI.getInstance(),
              () -> {
                if (safeEnabled.isEmpty()) {
                  this.safeSched.cancel();
                  this.safeSched = null;
                } else {
                  for (var entry : safeEnabled.entrySet()) {
                    if (!((Player) entry.getKey()).isOnline()
                        || (Long) entry.getValue() < System.currentTimeMillis()) {
                      safeEnabled.remove(entry.getKey());
                      LC.info_variables_Disabled.sendMessage(entry.getKey(), new Object[0]);
                      break;
                    }

                    Player player = (Player) entry.getKey();
                  }
                }
              },
              1L,
              5L);
    }
  }

  private enum actions {
    convertitems,
    removeseats,
    removechairs,
    testtarget,
    testsafe,
    allmobheads,
    allcommands,
    allcommandsperm,
    allpermissions,
    alltabcompletes,
    allplaceholders,
    allplayeroptions,
    updatecommands,
    emptyspace,
    testmaterials,
    testentities,
    testbiomes,
    drivetest,
    testrandomsafe,
    placeholdertest,
    statistictest,
    colorstoweb,
    colortest,
    mobtypes,
    materials,
    test,
    test2,
    test3,
    jsontest,
    placeblock,
    placeplatform,
    placetnt,
    cleararea,
    imortaldebug,
    fakeclickevents,
    dialogs,
    gc;

    public static util.actions getByname(String string) {
      for (util.actions candidate : values()) {
        if (candidate.toString().equalsIgnoreCase(string)) {
          return candidate;
        }
      }

      return null;
    }
  }
}
