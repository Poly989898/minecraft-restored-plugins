package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.CMIViewDistance;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class viewdistance implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "output", "{gcp}View: {gcs}[view] {gcp}Simulation: {gcs}[simulation] {gcn}[type]");
  }

  @CAnnotation(
      info = "Change view distance",
      args = "[range] (playerName/worldName/reset) (view/simulation)",
      multiTab = "[ViewRange],reset [playername],[worlds] view,simulation",
      regVar = {0, 1, 2, 3},
      consoleVar = {0, 1, 2, 3},
      alias = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (Version.isCurrentEqualOrHigher(Version.v1_20_5) && Version.isPaperBranch()) {
      int value = -2;
      String string = null;
      viewdistance.Type type = null;
      boolean flag = false;
      String[] args = strings;
      int argCount = strings.length;

      for (int argIndex = 0; argIndex < argCount; argIndex++) {
        String arg = args[argIndex];
        if (arg.equalsIgnoreCase("reset")) {
          flag = true;
        } else {
          if (value <= -2) {
            try {
              value = Integer.parseInt(arg);
              continue;
            } catch (Exception exception) {
            }
          }

          if (type == null) {
            type = viewdistance.Type.getType(arg);
            if (type != null) {
              continue;
            }
          }

          if (string == null) {
            string = arg;
          }
        }
      }

      if (flag) {
        value = -1;
      }

      if (value > -2) {
        if (type == null) {
          type = viewdistance.Type.view;
        }

        if (string == null) {
          switch (type) {
            case view:
              CMIViewDistance.setView(value);
              break;
            case simulation:
              CMIViewDistance.setSimulation(value);
          }

          return true;
        } else {
          World world = CMIWorld.getWorld(string);
          if (world != null) {
            switch (type) {
              case view:
                CMIViewDistance.setView(world, value);
                break;
              case simulation:
                CMIViewDistance.setSimulation(world, value);
            }

            return true;
          } else {
            Player player = CMIUser.getOnlinePlayer(string);
            if (player != null) {
              switch (type) {
                case view:
                  CMIViewDistance.setView(player, value);
                  break;
                case simulation:
                  CMIViewDistance.setSimulation(player, value);
              }
            }

            return true;
          }
        }
      } else {
        LC.info_Spliter.sendMessage(commandSender, new Object[0]);
        CMILC.info(
            this,
            commandSender,
            "output",
            "[view]",
            CMIViewDistance.getView(),
            "[simulation]",
            CMIViewDistance.getSimulation(),
            "[type]",
            "");
        Player targetPlayer = CMIUser.getOnlinePlayer(string);
        if (targetPlayer == null && commandSender instanceof Player) {
          targetPlayer = (Player) commandSender;
        }

        if (targetPlayer != null) {
          CMILC.info(
              this,
              commandSender,
              "output",
              "[view]",
              CMIViewDistance.getView(targetPlayer),
              "[simulation]",
              CMIViewDistance.getSimulation(targetPlayer),
              "[type]",
              targetPlayer.getName());
        }

        for (World bukkitWorld : Bukkit.getWorlds()) {
          CMILC.info(
              this,
              commandSender,
              "output",
              "[view]",
              CMIViewDistance.getView(bukkitWorld),
              "[simulation]",
              CMIViewDistance.getSimulation(bukkitWorld),
              "[type]",
              bukkitWorld.getName());
        }

        return true;
      }
    } else {
      LC.info_versionNotSupported.sendMessage(commandSender, new Object[0]);
      return null;
    }
  }

  private enum Type {
    view,
    simulation;

    public static viewdistance.Type getType(String string) {
      viewdistance.Type[] types;
      int value = (types = values()).length;

      for (int index = 0; index < value; index++) {
        viewdistance.Type type = types[index];
        if (type.name().equalsIgnoreCase(string)) {
          return type;
        }
      }

      return null;
    }
  }
}
