package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.Arrays;
import java.util.Iterator;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.TitleMessages.CMITitleMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class titlemsg implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("message", "&f[message]");
  }

  @CAnnotation(
      info = "Sends title message to player",
      args = "[playerName/all] [title \n subtitle] (-in:[ticks]) (-out:[ticks]) (-keep:[ticks])",
      tab = "playername",
      explanation = {},
      regVar = {-100, -1},
      consoleVar = {-100, -1},
      ignoreHelpPage = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = strings[0];
    Player player = null;
    int value = 0;
    int fadeInTicks = 100;
    int fadeOutTicks = 20;
    if (!string.equalsIgnoreCase("all")) {
      player = CMIUser.getOnlinePlayer(string, true);
      if (player == null) {
        LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
        return true;
      }
    }

    var list = Arrays.asList(Arrays.copyOfRange(strings, 1, strings.length));
    String title = "";
    Iterator iterator = list.iterator();

    while (true) {
      String subtitle;
      while (true) {
        while (true) {
          while (true) {
            if (!iterator.hasNext()) {
              if (PermissionsManager.CMIPerm.title_colors.hasPermission(commandSender)) {
                title = CMIChatColor.translate(title);
              }

              title = cmi.getPlaceholderAPIManager().updatePlaceHolders(player, title);
              title = title.replace("\\n", "%subtitle%");
              title = title.replace("\n", "%subtitle%");
              subtitle = null;
              if (title.endsWith("%subtitle%")) {
                title = title.substring(0, title.length() - "%subtitle%".length());
              }

              if (title.startsWith("%subtitle%")) {
                subtitle = title.substring("%subtitle%".length(), title.length());
              }

              if (title.contains("%subtitle%") && title.split("%subtitle%").length > 1) {
                subtitle = title.split("%subtitle%")[1];
                title = title.split("%subtitle%")[0];
              }

              if (title != null) {
                title = CMILC.getIM(this, "message", "[messsage]", title, "[message]", title);
              }

              if (subtitle != null) {
                subtitle =
                    CMILC.getIM(this, "message", "[messsage]", subtitle, "[message]", subtitle);
              }

              if (player != null) {
                Snd snd = new Snd().setSender(commandSender).setTarget(player);
                CMITitleMessage.send(
                    player,
                    cmi.getLM().updateSnd(snd, title),
                    cmi.getLM().updateSnd(snd, subtitle),
                    value,
                    fadeInTicks,
                    fadeOutTicks);
              } else {
                for (Player target : Bukkit.getOnlinePlayers()) {
                  Snd titleSound = new Snd().setSender(commandSender).setTarget(target);
                  CMITitleMessage.send(
                      target,
                      cmi.getLM().updateSnd(titleSound, title),
                      cmi.getLM().updateSnd(titleSound, subtitle),
                      value,
                      fadeInTicks,
                      fadeOutTicks);
                }
              }

              return true;
            }

            subtitle = (String) iterator.next();
            if (!subtitle.startsWith("-in:")) {
              break;
            }

            try {
              value = Integer.parseInt(subtitle.substring("-in:".length()));
            } catch (Exception exception) {
              break;
            }
          }

          if (!subtitle.startsWith("-out:")) {
            break;
          }

          try {
            fadeOutTicks = Integer.parseInt(subtitle.substring("-out:".length()));
          } catch (Exception exception2) {
            break;
          }
        }

        if (!subtitle.startsWith("-keep:")) {
          break;
        }

        try {
          fadeInTicks = Integer.parseInt(subtitle.substring("-keep:".length()));
        } catch (Exception exception3) {
          break;
        }
      }

      if (!title.isEmpty()) {
        title = title + " ";
      }

      title = title + subtitle;
    }
  }
}
