package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.utils.ServerLinksManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import net.Zrips.CMILib.Chat.ChatMessageListEdit;
import net.Zrips.CMILib.Chat.ChatMessageListEdit.ChatEditType;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.ServerLinks.ServerLink;
import org.bukkit.command.CommandSender;

public class serverlinks implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      info = "Set server links",
      explanation = "Separate display name and link with ;",
      regVar = 0,
      consoleVar = 0)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (Version.isCurrentLower(Version.v1_21_R1)) {
      LC.info_versionNotSupported.sendMessage(commandSender, new Object[0]);
      return null;
    }

    serverlinks.Action action = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String string = args[argIndex];
      if (action == null) {
        action = serverlinks.Action.getByName(string);
        if (action != null) {}
      }
    }

    if (action == null) {
      action = serverlinks.Action.list;
    }

    switch (action) {
      case add:
      case remove:
      default:
        return true;
      case list:
        final ArrayList<String> arrayList = new ArrayList();

        for (ServerLink serverLink : Bukkit.getServerLinks().getLinks()) {
          arrayList.add(serverLink.getDisplayName() + ";" + serverLink.getUrl().toString());
        }

        LC.info_Spliter.sendMessage(commandSender, new Object[0]);
        ChatMessageListEdit chatMessageListEdit =
            new ChatMessageListEdit(commandSender, arrayList, ChatEditType.String) {
              public void onUpdate() {
                Bukkit.getServerLinks()
                    .getLinks()
                    .forEach(link -> Bukkit.getServerLinks().removeLink(link));
                arrayList.forEach(
                    object -> {
                      String[] linkParts = object.split(";", 2);
                      if (linkParts.length >= 2) {
                        try {
                          String linkUrl = linkParts[1];
                          if (!linkUrl.startsWith("http://") && !linkUrl.startsWith("https://")) {
                            linkUrl = "http://" + linkUrl;
                          }

                          Bukkit.getServerLinks()
                              .addLink(CMIChatColor.translate(linkParts[0]), new URI(linkUrl));
                        } catch (URISyntaxException uriSyntaxException) {
                          uriSyntaxException.printStackTrace();
                        }
                      }
                    });
                Bukkit.getOnlinePlayers()
                    .forEach(manager -> manager.sendLinks(Bukkit.getServerLinks()));
                ServerLinksManager.save();
              }
            };
        chatMessageListEdit.print();
        return true;
    }
  }

  private enum Action {
    add,
    list,
    remove;

    public static serverlinks.Action getByName(String string) {
      serverlinks.Action[] actions;
      int value = (actions = values()).length;

      for (int index = 0; index < value; index++) {
        serverlinks.Action action = actions[index];
        if (action.name().equalsIgnoreCase(string)) {
          return action;
        }
      }

      return null;
    }
  }
}
