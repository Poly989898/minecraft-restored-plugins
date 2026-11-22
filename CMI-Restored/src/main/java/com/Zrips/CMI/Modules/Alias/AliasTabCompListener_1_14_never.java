package com.Zrips.CMI.Modules.Alias;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CommandAlias;
import com.Zrips.CMI.Containers.CommandAliasType;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.util.ArrayList;
import java.util.Collection;
import net.Zrips.CMILib.Container.CMIArray;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.TabCompleteEvent;

public class AliasTabCompListener_1_14_never implements Listener {
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerCommandSendEvent(PlayerCommandSendEvent playerCommandSendEvent) {}

  @EventHandler(priority = EventPriority.HIGHEST)
  public void PlayerChatTabCompleteEvent(TabCompleteEvent tabCompleteEvent) {
    String string =
        tabCompleteEvent.getBuffer().startsWith("/")
            ? tabCompleteEvent.getBuffer().substring(1)
            : tabCompleteEvent.getBuffer();
    var list =
        CMI.getInstance().getAliasManager().getAliasStartingWith(string, CommandAliasType.custom);
    if (!list.isEmpty()) {
      ArrayList<String> arrayList = new ArrayList(tabCompleteEvent.getCompletions());
      int value = string.split(" ").length;
      if (!string.endsWith(" ")) {
        value--;
      }

      for (CommandAlias commandAlias : list) {
        if (!commandAlias.isRequiresPerm()
            || PermissionsManager.CMIPerm.customalias_$1.hasPermission(
                tabCompleteEvent.getSender(), commandAlias.getAliasAsOneWordNS())) {
          String[] strings = commandAlias.getAlias().split(" ");
          if (strings.length > value) {
            arrayList.add(strings[value]);
          }
        }
      }

      tabCompleteEvent.setCompletions(arrayList);
    }

    CommandAlias matchedAlias = CMI.getInstance().getAliasManager().getAliasForCommand(string);
    if (matchedAlias != null && matchedAlias.getAlias() != null && string != null) {
      if ((matchedAlias.getType() != CommandAliasType.custom || matchedAlias.isOverrideTab())
          && matchedAlias.isTabComplete()
          && matchedAlias.getType() != CommandAliasType.base) {
        String remainder =
            string.substring(matchedAlias.getAliasBaseCommand().length(), string.length());
        remainder = remainder.startsWith(" ") ? remainder.substring(1) : remainder;
        String[] remainderParts = remainder.split(" ");
        ArrayList completions = new ArrayList();
        Collection collection = null;
        if (matchedAlias.getType().equals(CommandAliasType.custom)
            && matchedAlias.isTabComplete()) {
          if (remainder.endsWith(" ")) {
            remainderParts = CMIArray.addLast(remainderParts, "");
          }

          collection =
              CMI.getInstance()
                  .getTab()
                  .get(
                      tabCompleteEvent.getSender(), matchedAlias.getAlias(), "cmi", remainderParts);
          if (collection == null) {
            return;
          }

          if (matchedAlias.isAddTabs()) {
            completions.addAll(tabCompleteEvent.getCompletions());
          }

          completions.addAll(collection);
        } else {
          if (matchedAlias.getType().equals(CommandAliasType.subbase)) {
            collection =
                CMI.getInstance()
                    .getTab()
                    .get(
                        tabCompleteEvent.getSender(),
                        matchedAlias.getAlias(),
                        "CMICustomAlias",
                        remainderParts);
          } else {
            collection =
                CMI.getInstance()
                    .getTab()
                    .get(
                        tabCompleteEvent.getSender(),
                        matchedAlias.getCmiCommandName(),
                        "CMICustomAlias",
                        remainderParts);
          }

          if (collection == null) {
            return;
          }

          completions.addAll(collection);
        }

        tabCompleteEvent.setCompletions(completions);
      }
    }
  }
}
