package com.Zrips.CMI.Modules.Alias;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CommandAlias;
import com.Zrips.CMI.Containers.Snd;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class onServerPreprocessCommand implements Listener {
  @EventHandler(priority = EventPriority.NORMAL)
  public void onConsoleCommand(ServerCommandEvent serverCommandEvent) {
    if (!serverCommandEvent.isCancelled()) {
      String string =
          serverCommandEvent.getCommand().startsWith("/")
              ? serverCommandEvent.getCommand().substring(1)
              : serverCommandEvent.getCommand();
      String commandText = string;
      CommandAlias commandAlias = CMI.getInstance().getAliasManager().getAliasForCommand(string);
      if (commandAlias != null && commandAlias.getAlias() != null && string != null) {
        int value = commandAlias.getAlias().length();
        if (string.length() >= value) {
          string = string.substring(value);
        } else {
          string = "";
        }

        ArrayList<String> arrayList = new ArrayList();
        if (!string.isEmpty()) {
          if (string.contains(" ")) {
            arrayList.addAll(Arrays.asList(string.split(" ")));
          } else {
            arrayList.add(string);
          }
        }

        List<String> list = new ArrayList<>(commandAlias.getCommands());
        list = CMI.getInstance().getAliasManager().updateCommands(commandAlias, arrayList, list);
        Snd snd =
            new Snd()
                .setSender(serverCommandEvent.getSender())
                .setTarget(serverCommandEvent.getSender());

        for (int index = 0; index < list.size(); index++) {
          String arg = (String) list.get(index);
          arg = CMI.getInstance().getLM().updateSnd(snd, arg);
          list.set(index, arg);
        }

        Object object = null;

        for (String playerName : arrayList) {
          object = Bukkit.getPlayerExact(playerName);
          if (object != null) {
            break;
          }
        }

        if (!list.isEmpty()
            && list.size() == 1
            && !CMI.getInstance()
                .getSpecializedCommandManager()
                .isSpecializedCommand((String) list.get(0))) {
          String commandName = (String) list.get(0);
          if (commandAlias.getAlias().equalsIgnoreCase(commandText)
              || commandText.startsWith(commandAlias.getAlias())) {
            serverCommandEvent.setCommand(commandName);
          }
        } else if (commandAlias.getAlias().equalsIgnoreCase(commandText)
            || commandText.startsWith(commandAlias.getAlias())) {
          serverCommandEvent.setCancelled(true);
          CMI.getInstance().getSpecializedCommandManager().processCmds(list);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void RemoteServerCommandEvent(RemoteServerCommandEvent remoteServerCommandEvent) {
    if (!remoteServerCommandEvent.isCancelled()) {
      String string =
          remoteServerCommandEvent.getCommand().startsWith("/")
              ? remoteServerCommandEvent.getCommand().substring(1)
              : remoteServerCommandEvent.getCommand();
      String commandText = string;
      CommandAlias commandAlias = CMI.getInstance().getAliasManager().getAliasForCommand(string);
      if (commandAlias != null && commandAlias.getAlias() != null && string != null) {
        string = string.substring(commandAlias.getAlias().length());
        ArrayList<String> arrayList = new ArrayList();
        if (string.contains(" ")) {
          arrayList.addAll(Arrays.asList(string.split(" ")));
        } else {
          arrayList.add(string);
        }

        List<String> list = new ArrayList<>(commandAlias.getCommands());
        list = CMI.getInstance().getAliasManager().updateCommands(commandAlias, arrayList, list);
        Snd snd =
            new Snd()
                .setSender(remoteServerCommandEvent.getSender())
                .setTarget(remoteServerCommandEvent.getSender());

        for (int value = 0; value < list.size(); value++) {
          String arg = (String) list.get(value);
          arg = CMI.getInstance().getLM().updateSnd(snd, arg);
          list.set(value, arg);
        }

        if (!list.isEmpty()
            && list.size() == 1
            && !CMI.getInstance()
                .getSpecializedCommandManager()
                .isSpecializedCommand((String) list.get(0))) {
          String commandName = (String) list.get(0);
          if (commandAlias.getAlias().equalsIgnoreCase(commandText)) {
            remoteServerCommandEvent.setCommand(commandName);
          }
        } else if (commandAlias.getCmiCommandName() == null
            && commandText.toLowerCase().startsWith(commandAlias.getAlias())) {
          remoteServerCommandEvent.setCancelled(true);
          CMI.getInstance()
              .getSpecializedCommandManager()
              .processCmds(list, remoteServerCommandEvent.getSender());
        }
      }
    }
  }
}
