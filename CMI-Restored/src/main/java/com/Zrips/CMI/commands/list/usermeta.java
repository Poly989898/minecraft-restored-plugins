/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.Chat.ChatEditorObject
 *  net.Zrips.CMILib.Chat.ChatMessageObjectEdit
 *  net.Zrips.CMILib.Container.PageInfo
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Locale.LC
 *  org.bukkit.command.CommandSender
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerMeta.PlayerMeta;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.math.BigDecimal;
import java.util.Map;
import net.Zrips.CMILib.Chat.ChatEditorObject;
import net.Zrips.CMILib.Chat.ChatMessageObjectEdit;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;

public class usermeta implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "added",
        "{gcp}Added meta for {gcs}[playerDisplayName] {gcp}with key as {gcs}[key] {gcp}and value as"
            + " {gcs}[value]");
    configReader.get("cleared", "{gcp}Cleared meta from {gcs}[playerDisplayName]");
    configReader.get("removed", "{gcp}Removed {gcs}[key] {gcp}meta from {gcs}[playerDisplayName]");
    configReader.get(
        "changed",
        "{gcp}Changed {gcs}[key] {gcp}meta from {gcs}[oldValue] {gcp}to new value of"
            + " {gcs}[newValue]");
    configReader.get("ls", "{gcs}[key]{gcp}: [value]");
  }

  private static boolean fractional(double d) {
    return d != Math.floor(d);
  }

  @Override
  @CAnnotation(
      info = "Manage players meta data",
      args = "[playerName] [add/remove/clear/list/increment] (key) (value) (-s)",
      tab = {"playername", "add%%remove%%clear%%list%%increment", "[usermeta]"},
      explanation = {
        "Any set meta can be displayed with %cmi_user_meta_[key]% placeholder around plugin"
      },
      regVar = {-100},
      consoleVar = {-100, -1},
      others = false)
  public Boolean perform(CMI cMI, final CommandSender commandSender, String[] args) {
    boolean bl = false;
    Action action = Action.List;
    String targetName = args[0];
    String key = null;
    if (args.length > 2) {
      key = args[2];
    }
    String value = null;
    for (int i = 3; i < args.length; i++) {
      if (!args[i].equalsIgnoreCase("-s")) {
        if (value == null) {
          value = "";
        }
        if (!value.isEmpty()) {
          value = value + " ";
        }
        value = value + args[i];
      }
    }
    if (args.length > 1) {
      switch (args[1].toLowerCase()) {
        case "add":
          {
            action = Action.Add;
            break;
          }
        case "remove":
          {
            action = Action.Remove;
            break;
          }
        case "clear":
          {
            action = Action.Clear;
            break;
          }
        case "list":
          {
            action = Action.List;
            break;
          }
        case "increment":
          {
            action = Action.Increment;
          }
      }
    }
    for (String arg : args) {
      switch (arg.toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            bl = true;
          }
      }
    }
    final CMIUser cMIUser = cMI.getPlayerManager().getUser(targetName);
    if (cMIUser == null) {
      LC.info_NoPlayer.sendMessage((Object) commandSender, new Object[0]);
      return null;
    }
    final Snd snd = new Snd().setSender(commandSender).setTarget(cMIUser);
    switch (action) {
      case Add:
        {
          if (key == null || value == null) {
            return false;
          }
          cMIUser.getMeta().add(key, value);
          if (bl) break;
          CMILC.info((Object) this, commandSender, "added", snd, "[key]", key, "[value]", value);
          break;
        }
      case Clear:
        {
          cMIUser.getMeta().getMap().clear();
          if (bl) break;
          CMILC.info((Object) this, commandSender, "cleared", snd, "[key]", key);
          break;
        }
      case Remove:
        {
          if (key == null) {
            return false;
          }
          cMIUser.getMeta().remove(key);
          if (bl) break;
          CMILC.info((Object) this, commandSender, "removed", snd, "[key]", key);
          break;
        }
      case Increment:
        {
          if (key == null || value == null) {
            return false;
          }
          String current = cMIUser.getMeta().getValue(key);
          current = current == null || current.isEmpty() ? "0" : current;
          Double amount = null;
          try {
            amount =
                value.startsWith("-") || value.startsWith("+")
                    ? Double.valueOf(Double.parseDouble(value.substring(1)))
                    : Double.valueOf(Double.parseDouble(value));
            if (value.startsWith("-")) {
              amount = -amount.doubleValue();
            }
          } catch (Exception exception) {
            // empty catch block
          }
          if (amount == null) {
            return false;
          }
          try {
            if (!usermeta.fractional(amount) && !usermeta.fractional(Double.parseDouble(current))) {
              cMIUser
                  .getMeta()
                  .add(key, String.valueOf(amount.intValue() + (int) Double.parseDouble(current)));
            } else {
              double d = Double.parseDouble(current);
              BigDecimal bigDecimal = BigDecimal.valueOf(d);
              bigDecimal = bigDecimal.add(BigDecimal.valueOf(amount));
              cMIUser.getMeta().add(key, String.valueOf(bigDecimal.toPlainString()));
            }
          } catch (Exception exception) {
            exception.printStackTrace();
            try {
              double d = Double.parseDouble(current);
              BigDecimal bigDecimal = BigDecimal.valueOf(d);
              bigDecimal = bigDecimal.add(BigDecimal.valueOf(amount));
              cMIUser.getMeta().add(key, String.valueOf(bigDecimal.toPlainString()));
            } catch (Exception exception2) {
              // empty catch block
            }
          }
          if (bl) break;
          CMILC.info(
              (Object) this,
              commandSender,
              "changed",
              snd,
              "[key]",
              key,
              "[oldValue]",
              current,
              "[newValue]",
              cMIUser.getMeta().getValue(key));
          break;
        }
      case List:
        {
          if (cMIUser.getMeta().getMap().isEmpty()) {
            LC.info_NoInformation.sendMessage((Object) commandSender, new Object[0]);
            return true;
          }
          LC.info_Spliter.sendMessage((Object) commandSender, new Object[0]);
          PageInfo pageInfo = new PageInfo(100, cMIUser.getMeta().getMap().size(), 1);
          ChatMessageObjectEdit chatMessageObjectEdit =
              new ChatMessageObjectEdit(commandSender, pageInfo) {

                public void newAdd(String string) {}
              };
          for (final Map.Entry<String, PlayerMeta.metaValues> entry :
              cMIUser.getMeta().getMap().entrySet()) {
            if (!pageInfo.isEntryOk()) continue;
            ChatEditorObject chatEditorObject =
                new ChatEditorObject(
                    CMILC.getIM(
                        this,
                        "ls",
                        snd,
                        "[key]",
                        entry.getKey(),
                        "[value]",
                        entry.getValue().getValue())) {

                  public void onDelete() {
                    cMIUser.getMeta().remove((String) entry.getKey());
                    CMILC.info(
                        usermeta.class.getSimpleName(),
                        commandSender,
                        "removed",
                        snd,
                        "[key]",
                        entry.getKey());
                  }

                  public void onClick() {}
                };
            chatMessageObjectEdit.addline(chatEditorObject);
          }
          chatMessageObjectEdit.setAddNew(false);
          chatMessageObjectEdit.print();
          break;
        }
      default:
        {
          return false;
        }
    }
    cMIUser.addForDelayedSave();
    return true;
  }

  private static enum Action {
    Remove,
    Add,
    Clear,
    List,
    Increment;
  }
}
