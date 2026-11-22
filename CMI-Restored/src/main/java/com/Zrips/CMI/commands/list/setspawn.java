package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.utils.SpawnUtil;
import java.util.Arrays;
import java.util.HashSet;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class setspawn implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "{gcp}Spawn point set ({gcs}[world],[x],[y],[z]{gcp})!");
    configReader.get("group", " {gcp}({gcs}[group]{gcp})");
    configReader.get("rng", " {gcp}(Rng: {gcs}[rng]{gcp})");
    configReader.get("respawn", "{gcn}Click to set this spawn point as possible respawn location");
  }

  @CAnnotation(
      priority = 240,
      info = "Sets spawn command teleport point",
      args =
          "(playerName) (true/false) (-g:[groupName]) (-rng:[range]) (-w:world,world_nether)"
              + " (-loc:[world;x;y;z;yaw;pitch])",
      explanation = "More info on command usage at https://www.zrips.net/cmi/commands/spawn/",
      regVar = {0, 1, 2, 3, 4},
      consoleVar = {1, 2, 3, 4},
      alias = true,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    boolean flag = false;
    String spawnName = null;
    Integer integer = null;
    HashSet<String> hashSet = new HashSet();
    Location object = null;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.equalsIgnoreCase("t") || arg.equalsIgnoreCase("true")) {
        flag = true;
      } else if (arg.equalsIgnoreCase("false") || arg.equalsIgnoreCase("f")) {
        flag = false;
      } else if (arg.startsWith("-g:")) {
        spawnName = arg.substring("-g:".length(), arg.length());
      } else {
        if (arg.startsWith("-rng:")) {
          try {
            integer = Integer.parseInt(arg.substring("-rng:".length(), arg.length()));
            continue;
          } catch (Exception exception) {
          }
        }

        if (arg.startsWith("loc:")) {
          try {
            object =
                cmi.getPlayerManager()
                    .convertStringToLocation(arg.substring("loc:".length(), arg.length()));
            continue;
          } catch (Exception exception2) {
          }
        }

        if (arg.startsWith("-loc:")) {
          try {
            object =
                cmi.getPlayerManager()
                    .convertStringToLocation(arg.substring("-loc:".length(), arg.length()));
            continue;
          } catch (Exception exception3) {
          }
        }

        if (arg.startsWith("-w:")) {
          arg = arg.substring("-w:".length());
          if (arg.contains(",")) {
            hashSet.addAll(Arrays.asList(arg.split(",")));
          } else {
            hashSet.add(arg);
          }
        } else {
          string = arg;
        }
      }
    }

    Player player = cmi.getTarget(commandSender, string, this, false);
    if (player == null && object == null) {
      return null;
    }

    if (object == null && player != null) {
      object = player.getLocation();
    }

    if (object == null) {
      return false;
    }

    try {
      if (spawnName == null) {
        object.getWorld().setSpawnLocation((Location) object);
      }
    } catch (Throwable throwable) {
    }

    SpawnUtil.addNew(spawnName, new CMILocation((Location) object), flag, integer, hashSet);
    SpawnUtil.save();
    if (!flag) {
      String target = "";

      for (String groupName : hashSet) {
        if (!target.isEmpty()) {
          target = target + ",";
        }

        target = target + groupName;
      }

      RawMessage rawMessage = new RawMessage();
      rawMessage
          .addText(CMILC.getIM(this, "respawn"))
          .addHover(LC.info_Click)
          .addCommand(
              CommandsHandler.getLabel()
                  + " "
                  + this.getClass().getSimpleName()
                  + (string != null ? " " + string : "")
                  + (target.isEmpty() ? "" : " -w:" + target)
                  + (spawnName == null ? "" : " -g:" + spawnName)
                  + (integer == null ? "" : " -rng:" + integer)
                  + " true");
      rawMessage.show(commandSender);
    }

    String feedback = CMILC.getIM(this, "feedback", object);
    if (spawnName != null) {
      feedback = feedback + CMILC.getIM(this, "group", "[group]", spawnName);
    }

    if (integer != null) {
      feedback = feedback + CMILC.getIM(this, "rng", "[rng]", integer);
    }

    CMILC.sendMessage(commandSender, feedback);
    return true;
  }
}
