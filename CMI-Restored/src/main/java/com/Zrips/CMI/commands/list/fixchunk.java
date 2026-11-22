package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.ChunkInfo;
import com.Zrips.CMI.Containers.FixChunkInfo;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.utils.ChunkFix;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class fixchunk implements Cmd {
  public void getExtra(ConfigReader configReader) {
    configReader.get("startScanning", "{gcp}Found {gcs}[amount] {gcp}chunks to check. Starting!");
    configReader.get(
        "paused", "{gcp}Scanning paused, to continue use {gcs}/cmi fixchunk continue.");
    configReader.get("continuing", "{gcp}Scanning continuing");
    configReader.get(
        "stoped", "{gcp}Scanning stopped. You can check the results with /cmi fixchunk info");
    configReader.get("speedChange", "{gcp}Speed changed to [amount]");
    configReader.get("autospeedChange", "{gcp}AutoSpeed changed to [state]");
    configReader.get("messagesChange", "{gcp}Message state changed to [state]");
    configReader.get("NoInfo", "{gcp}There is no info");
    configReader.get(
        "InfoShow",
        "{gcs}TPS: {gcs}[tps] {gcs}Left {gcp}[left] {gcs}({gcp}[min]m:[sec]s{gcs}) {gcs}Speed:"
            + " {gcp}[speed] {gcs}Found: {gcp}[found]");
    configReader.get("scanList", "{gcs}[number]. {gcp}X:{gcs}[x] {gcp}Z:{gcs}[z]");
    configReader.get(
        "finished",
        "{gcp}Scanning finished, use {gcs}/cmi fixchunk fix {gcp}to fix found issues. {gcn}Chunks"
            + " will be regenerated!");
    configReader.get("fixed", "{gcs}[number] {gcp}chunks fixed");
    configReader.get("total", "{gcp}Checking from [current]/[until] from total of [total]");
    configReader.get(
        "stats",
        Arrays.asList(
            "{gcp}************************************************************",
            "{gcp}* {gcs}Chunks: {gcp}[checked]/[total]",
            "{gcp}* {gcs}Scan range: {gcp}[range]",
            "{gcp}* {gcs}Scan speed: {gcp}[scanspeed] {gcs}({gcp}[autospeed]{gcs})",
            "{gcp}* {gcs}Real speed: {gcp}[cps] {gcs}CPS ({gcp}[avgcps] avg{gcs})",
            "{gcp}* {gcs}World: {gcp}[world]",
            "{gcp}* {gcs}Found: {gcp}[found]",
            "{gcp}* {gcs}Time: {gcp}[min]min [sec]sec",
            "{gcp}************************************************************"));
  }

  @CAnnotation(
      info = "Scans for damaged chunks",
      args = "w [worldName] r [range in chunks] c [x:z]",
      explanation = {
        "SubCommands:",
        "- stats - show current scanning stats",
        "- pause - pause scanning",
        "- continue - continue scanning",
        "- stop - stop scanning",
        "- stopall - stop all scanning",
        "- speed [amount] - set current scan speed",
        "- autospeed [true/false] - set autospeed turned off or on",
        "- messages [true/false] - set message output to off or on",
        "Example:",
        "/fixchunk w LT_Craft",
        "/fixchunk w LT_Craft r 50 c 1024:-2048",
        "/fixchunk w LT_Craft r g",
        "/fixchunk fix"
      },
      regVar = -100,
      consoleVar = -100,
      alias = true,
      others = false)
  public Boolean perform(CMI plugin, CommandSender sender, String[] args) {
    if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
      LC.info_versionNotSupported.sendMessage(sender);
      return null;
    }

    ChunkFix fixManager = plugin.getChunkFixManager();
    String senderName = sender.getName();

    if (args.length == 1) {
      String command = args[0];
      if (command.equalsIgnoreCase("pause") || command.equalsIgnoreCase("p")) {
        if (fixManager.ChunkFix.containsKey(senderName)
            && fixManager.ChunkFix.get(senderName).isRunning()) {
          CMIActionBar.send(sender, CMILC.getIM(this, "paused"));
          fixManager.ChunkFix.get(senderName).setRunning(false);
          return true;
        }
      } else if (command.equalsIgnoreCase("continue") || command.equalsIgnoreCase("c")) {
        if (fixManager.ChunkFix.containsKey(senderName)
            && !fixManager.ChunkFix.get(senderName).isRunning()) {
          CMIActionBar.send(sender, CMILC.getIM(this, "continuing"));
          FixChunkInfo info = fixManager.ChunkFix.get(senderName);
          info.setRunning(true);
          info.setStartTime();
          plugin.getChunkFixManager().loadRegionFile(info);
          return true;
        }
      } else if (command.equalsIgnoreCase("stop") || command.equalsIgnoreCase("s")) {
        if (fixManager.ChunkFix.containsKey(senderName)
            && fixManager.ChunkFix.get(senderName).isRunning()) {
          CMILC.info(this, sender, "stoped");
          fixManager.ChunkFix.get(senderName).setRunning(false);
          return true;
        }
      } else if (command.equalsIgnoreCase("stopall")) {
        for (FixChunkInfo other : fixManager.ChunkFix.values()) {
          if (!other.isRunning()) {
            continue;
          }
          CMILC.info(this, other.getSender(), "stoped");
          other.setRunning(false);
          return true;
        }
      }
    }

    if (args.length >= 1) {
      String command = args[0];
      if (command.equalsIgnoreCase("info") || command.equalsIgnoreCase("i")) {
        return showScanList(plugin, sender, args);
      }
      if (command.equalsIgnoreCase("fix")) {
        return fixChunks(plugin, sender, args);
      }
      if (command.equalsIgnoreCase("stats")) {
        return showStats(plugin, sender, args);
      }
    }

    if (args.length == 2) {
      String command = args[0];
      if (command.equalsIgnoreCase("speed")
          && PermissionsManager.CMIPerm.command_fixchunk_speed.hasPermission(
              sender, true, new String[0])) {
        int speed;
        try {
          speed = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
          LC.info_UseInteger.sendMessage(sender);
          return true;
        }
        if (fixManager.ChunkFix.containsKey(senderName)) {
          FixChunkInfo info = fixManager.ChunkFix.get(senderName);
          // Same checks as the original bytecode: they test the stored speed, not the requested
          // one.
          if (info.getSpeed() < 1) {
            info.setSpeed(1);
          } else if (info.getSpeed() > 100) {
            info.setSpeed(100);
          } else {
            info.setSpeed(speed);
          }
          CMILC.info(this, sender, "speedChange", "[amount]", speed);
        }
        return true;
      } else if (command.equalsIgnoreCase("autospeed")) {
        boolean autoSpeed;
        if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("yes")) {
          autoSpeed = true;
        } else if (args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("no")) {
          autoSpeed = false;
        } else {
          LC.info_UseBoolean.sendMessage(sender);
          return true;
        }
        if (fixManager.ChunkFix.containsKey(senderName)) {
          FixChunkInfo info = fixManager.ChunkFix.get(senderName);
          info.setAutoSpeed(autoSpeed);
          CMILC.info(this, sender, "autospeedChange", "[state]", autoSpeed);
        }
        return true;
      } else if (command.equalsIgnoreCase("messages")) {
        boolean messages;
        if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("yes")) {
          messages = true;
        } else if (args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("no")) {
          messages = false;
        } else {
          LC.info_UseBoolean.sendMessage(sender);
          return true;
        }
        if (fixManager.ChunkFix.containsKey(senderName)) {
          FixChunkInfo info = fixManager.ChunkFix.get(senderName);
          info.setMessages(messages);
          CMILC.info(this, sender, "messagesChange", "[state]", messages);
        }
        return true;
      }
    }

    return startScan(plugin, sender, args);
  }

  private Boolean showScanList(CMI plugin, CommandSender sender, String[] args) {
    String senderName = sender.getName();
    boolean hasInfo = true;

    if (plugin.getChunkFixManager().ChunkFix.containsKey(senderName)) {
      int page = 1;
      if (args.length == 2) {
        try {
          page = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
          LC.info_UseInteger.sendMessage(sender);
          plugin.getCommandManager().sendUsage(sender, getClass().getSimpleName());
          return true;
        }
      }
      if (page < 1) {
        page = 1;
      }

      List<ChunkInfo> chunks =
          plugin.getChunkFixManager().ChunkFix.get(senderName).getChunksResult();
      if (chunks.isEmpty()) {
        hasInfo = false;
      } else {
        int total = chunks.size();
        if (page * 10 - 10 >= total) {
          return true;
        }

        CMILC.info(this, sender, "finished");
        CMILC.info(
            this,
            sender,
            "total",
            "[current]",
            page * 10 - 9,
            "[until]",
            Math.min(page * 10, total),
            "[total]",
            total);

        int firstPosition = page * 10 - 10;
        int lastPosition = firstPosition + 10;
        int position = 0;
        for (ChunkInfo chunk : chunks) {
          position++;
          if (position <= firstPosition) {
            continue;
          }
          if (position > lastPosition) {
            break;
          }
          String line = CMILC.getIM(this, "scanList");
          line =
              line.replace("[number]", String.valueOf(position))
                  .replace("[x]", String.valueOf(chunk.getX()))
                  .replace("[z]", String.valueOf(chunk.getZ()));
          sender.sendMessage(CMIChatColor.translate(line));
        }
      }
    } else {
      hasInfo = false;
    }

    if (!hasInfo) {
      CMILC.info(this, sender, "NoInfo");
    }
    return true;
  }

  private Boolean fixChunks(CMI plugin, CommandSender sender, String[] args) {
    String senderName = sender.getName();
    boolean hasInfo = true;

    if (plugin.getChunkFixManager().ChunkFix.containsKey(senderName)) {
      // The optional amount is only validated; the original never uses its value.
      if (args.length == 2) {
        try {
          Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
          LC.info_UseInteger.sendMessage(sender);
          return true;
        }
      }

      List<ChunkInfo> chunks =
          plugin.getChunkFixManager().ChunkFix.get(senderName).getChunksResult();
      if (chunks.isEmpty()) {
        hasInfo = false;
      } else {
        int fixed = 0;
        for (ChunkInfo chunk : chunks) {
          try {
            int x = chunk.getX();
            int z = chunk.getZ();
            World world = chunk.getW();
            world.regenerateChunk(x, z);
            world.refreshChunk(x, z);
            world.unloadChunkRequest(x, z);
            world.loadChunk(x, z);
            fixed++;
          } catch (Exception e) {
            // Failed chunks are skipped, as in the original.
          }
        }
        CMILC.info(this, sender, "fixed", "[number]", fixed);
        plugin.getChunkFixManager().ChunkFix.remove(senderName);
      }
    } else {
      hasInfo = false;
    }

    if (!hasInfo) {
      CMILC.info(this, sender, "NoInfo");
    }
    return true;
  }

  private Boolean showStats(CMI plugin, CommandSender sender, String[] args) {
    if (args.length > 1) {
      plugin.getCommandManager().sendUsage(sender, getClass().getSimpleName());
      return true;
    }

    String senderName = sender.getName();
    if (!plugin.getChunkFixManager().ChunkFix.containsKey(senderName)) {
      CMILC.info(this, sender, "NoInfo");
      return true;
    }

    FixChunkInfo info = plugin.getChunkFixManager().ChunkFix.get(senderName);
    double total = info.getTotal();
    double checked = 0;
    if (info.isOnlyChunks()) {
      checked = info.getChunkChecked();
    } else {
      checked = info.getRegionChecked() * 1024.0;
    }
    if (checked == 0) {
      checked = 1;
    }
    double remaining = total - checked;

    long elapsed = System.currentTimeMillis() - info.getStartTime();
    long msPerChunk = (long) (elapsed / checked);
    int secondsLeft = (int) (remaining * msPerChunk / 1000.0);
    int minutes = secondsLeft / 60;
    int seconds = secondsLeft - minutes * 60;

    double checkedSinceLast = checked - info.getChunksCkeckedLast();
    info.setChunksCkeckedLast(checked);

    double sinceShow = System.currentTimeMillis() - info.getShowInfo();
    double cps = (sinceShow / checkedSinceLast) * 100.0;
    if (!info.isRunning()) {
      cps = 0;
    }

    double sinceStart = System.currentTimeMillis() - info.getStartTime();
    double avgCps = (sinceStart / checked) * 100.0;
    if (!info.isRunning()) {
      avgCps = 0;
    }

    info.setShowInfo(System.currentTimeMillis());

    List<String> lines = CMILC.getIML(this, "stats");
    for (String line : lines) {
      line =
          line.replace("[checked]", String.valueOf(checked))
              .replace("[total]", String.valueOf(total))
              .replace("[range]", info.getRange() == -1 ? "G" : String.valueOf(info.getRange()))
              .replace("[scanspeed]", String.valueOf(info.getSpeed()))
              .replace("[autospeed]", String.valueOf(info.isAutoSpeed()))
              .replace("[cps]", String.format("%.2f", cps))
              .replace("[avgcps]", String.format("%.2f", avgCps))
              .replace("[world]", info.getWorld().getName())
              .replace("[found]", String.valueOf(info.getFound()))
              .replace("[min]", String.valueOf(minutes))
              .replace("[sec]", String.valueOf(seconds));
      sender.sendMessage(line);
    }
    return true;
  }

  private Boolean startScan(CMI plugin, CommandSender sender, String[] args) {
    ChunkFix fixManager = plugin.getChunkFixManager();
    String senderName = sender.getName();
    FixChunkInfo info = new FixChunkInfo();

    if (args.length >= 1) {
      // argIndex points at the value that follows a keyword. It advances on every
      // argument except the ones where the original skipped the increment (no colon, no world).
      int argIndex = 0;
      for (int position = 0; position < args.length; position++) {
        String arg = args[position];
        switch (arg.toLowerCase()) {
          case "center":
          case "c":
            {
              if (argIndex + 1 >= args.length) {
                break;
              }
              if (!args[argIndex + 1].contains(":")) {
                continue;
              }
              int x;
              int z;
              try {
                x = Integer.parseInt(args[argIndex + 1].split(":")[0]);
                z = Integer.parseInt(args[argIndex + 1].split(":")[1]);
              } catch (NumberFormatException e) {
                LC.info_UseInteger.sendMessage(sender);
                return true;
              }
              Vector center = new Vector();
              center.setX(x / 16);
              center.setZ(z / 16);
              info.setCenter(center);
              break;
            }
          case "w":
          case "world":
            {
              if (argIndex + 1 >= args.length) {
                break;
              }
              World found = null;
              for (World candidate : Bukkit.getWorlds()) {
                if (candidate.getName().equalsIgnoreCase(args[argIndex + 1])) {
                  found = candidate;
                  break;
                }
              }
              if (found == null) {
                continue;
              }
              info.setWorld(found);
              break;
            }
          case "r":
          case "range":
            {
              if (argIndex + 1 >= args.length) {
                break;
              }
              if (args[argIndex + 1].equalsIgnoreCase("g")) {
                break;
              }
              int range;
              try {
                range = Integer.parseInt(args[argIndex + 1]);
              } catch (NumberFormatException e) {
                LC.info_UseInteger.sendMessage(sender);
                return true;
              }
              info.setRange(range);
              info.setOnlyChunks(true);
              break;
            }
          default:
            break;
        }
        argIndex++;
      }
    }

    if (info.isOnlyChunks() && info.getCenter() == null && sender instanceof Player) {
      Player player = (Player) sender;
      Vector center = new Vector();
      center.setX(player.getLocation().getChunk().getX());
      center.setZ(player.getLocation().getChunk().getZ());
      info.setCenter(center);
    }

    if (info.getWorld() == null && sender instanceof Player) {
      info.setWorld(((Player) sender).getWorld());
    }

    if (info.getWorld() == null || (info.isOnlyChunks() && info.getCenter() == null)) {
      plugin.getCommandManager().sendUsage(sender, getClass().getSimpleName());
      return true;
    }

    List<File> regionFiles = new ArrayList<>();
    File worldFolder = new File(Bukkit.getWorldContainer(), info.getWorld().getName());
    File regionFolder = new File(worldFolder, "region");
    if (!new File(worldFolder, "region").isDirectory()) {
      if (new File(worldFolder, "DIM-1").isDirectory()) {
        regionFolder = new File(worldFolder, "DIM-1" + File.separator + "region");
      } else if (new File(worldFolder, "DIM1").isDirectory()) {
        regionFolder = new File(worldFolder, "DIM1" + File.separator + "region");
      }
    }

    File[] files =
        regionFolder.listFiles(
            new FilenameFilter() {
              @Override
              public boolean accept(File dir, String name) {
                return plugin.getChunkFixManager().regionPattern.matcher(name).matches();
              }
            });
    if (files == null || files.length == 0) {
      return false;
    }

    regionFiles.addAll(Arrays.asList(files));
    if (info.isOnlyChunks()) {
      info.setTotal((info.getRange() * 2.0 - 1) * (info.getRange() * 2.0 - 1));
    } else {
      regionFiles.clear();
      regionFiles.add(new File(regionFolder, "r.-1.-2.mca"));
      info.setRegionFiles(regionFiles);
      info.setTotal(info.getRegionFiles().size() * 1024);
    }

    CMIActionBar.send(sender, CMILC.getIM(this, "startScanning", "[amount]", info.getTotal()));

    if (sender instanceof Player) {
      info.setSender(((Player) sender).getUniqueId());
    } else {
      info.setSender(plugin.getServerUUID());
    }
    info.setStartTime();
    info.setRunning(true);
    info.setShowInfo(System.currentTimeMillis());

    if (fixManager.ChunkFix.containsKey(senderName)) {
      FixChunkInfo previous = fixManager.ChunkFix.get(senderName);
      if (previous.getScheduleTask() != null) {
        previous.getScheduleTask().cancel();
      }
    }
    fixManager.ChunkFix.put(senderName, info);
    plugin.getChunkFixManager().loadRegionFile(info);
    return true;
  }
}
