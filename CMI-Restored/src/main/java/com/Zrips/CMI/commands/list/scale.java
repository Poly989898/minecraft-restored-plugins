/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.Container.CMIAttribute
 *  net.Zrips.CMILib.Container.CMINumber
 *  net.Zrips.CMILib.Entities.CMIEntityType
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Locale.LC
 *  net.Zrips.CMILib.Version.Version
 *  org.bukkit.attribute.Attributable
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Container.CMIAttribute;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.attribute.Attributable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class scale implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback", "{gcp}You have set {gcs}[scale] {gcp}scale for {gcs}[entityName]{gcp}.");
    configReader.get("scale", "{gcp}Scale: {gcs}[scale]");
    configReader.get(
        "targetfeedback",
        "{gcp}Your scale was set to {gcs}[scale] {gcp}by {gcs}[senderDisplayName]{gcp}.");
  }

  @Override
  @CAnnotation(
      info = "Set entities scale",
      args = "[set/add/take/clear] (playerName) [amount] (-s)",
      tab = {"set%%add%%take%%clear", "playername"},
      explanation = {},
      regVar = {0, 1, 2, 3, 4},
      consoleVar = {2, 3, 4},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    String string;
    Entity text;
    if (Version.isCurrentLower((Version) Version.v1_20_R4)) {
      LC.info_versionNotSupported.sendMessage((Object) commandSender, new Object[0]);
      return null;
    }
    boolean bl = false;
    Action action = Action.Info;
    double d = 0.0;
    String textSecond = null;
    String textThird = null;
    String[] stringArrayValue = stringArray;
    int n = stringArray.length;
    int count = 0;
    while (count < n) {
      String token = stringArrayValue[count];
      switch (token.toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            bl = true;
            break;
          }
        case "add":
          {
            action = Action.Add;
            break;
          }
        case "set":
          {
            action = Action.Set;
            break;
          }
        case "take":
          {
            action = Action.Take;
            break;
          }
        case "clear":
          {
            action = Action.Clear;
            break;
          }
        default:
          {
            Player textFourth;
            if (textThird == null
                && (textFourth = CMIUser.getOnlinePlayer(commandSender, token, true)) != null) {
              textThird = token;
              break;
            }
            try {
              d = Double.parseDouble(token);
              break;
            } catch (NumberFormatException numberFormatException) {
              if (cMI.getPlaceholderAPIManager().containsPlaceHolder(token)) {
                textSecond = token;
                break;
              }
              textThird = token;
            }
          }
      }
      ++count;
    }
    text = null;
    if (textThird != null) {
      text = cMI.getTarget(commandSender, textThird, this);
    }
    if (text == null && commandSender instanceof Player) {
      text = cMI.getUtilManager().getTargetEntity((Player) commandSender);
    }
    if (!(text instanceof Attributable)) {
      return false;
    }
    if (action == Action.Info) {
      CMILC.info(
          (Object) this,
          commandSender,
          "scale",
          "[scale]",
          CMIAttribute.SCALE.getValue((Entity) text));
      return true;
    }
    if (d == 0.0 && textSecond != null && text instanceof Player) {
      textSecond = cMI.getPlaceholderAPIManager().updatePlaceHolders((Player) text, textSecond);
      try {
        d = (int) Double.parseDouble(textSecond);
      } catch (NumberFormatException numberFormatException) {
        // empty catch block
      }
    }
    if (d <= 0.0 && action != Action.Clear) {
      LC.info_NoLessThan.sendMessage((Object) commandSender, new Object[] {"[amount]", 0});
      return null;
    }
    double amount = 20.0;
    int countSecond = 100;
    double amountSecond = CMIAttribute.SCALE.getValue((Entity) text);
    switch (action) {
      case Add:
        {
          amount = CMINumber.clamp((double) (amountSecond + d), (double) 0.0625, (int) countSecond);
          break;
        }
      case Set:
        {
          amount = CMINumber.clamp((double) d, (double) 0.0625, (int) countSecond);
          break;
        }
      case Take:
        {
          amount = CMINumber.clamp((double) (amountSecond - d), (double) 0.0625, (int) countSecond);
          break;
        }
      case Clear:
        {
          amount = 1.0;
        }
    }
    CMIAttribute.SCALE.setBaseValue((Entity) text, amount);
    Snd snd = new Snd().setSender(commandSender);
    if (text instanceof Player) {
      cMI.save((Player) text);
      snd = snd.setTarget((Player) text);
      if (!((Player) text).getName().equalsIgnoreCase(commandSender.getName()) && !bl) {
        CMILC.info((Object) this, (CommandSender) text, "targetfeedback", snd, "[scale]", d);
      }
    }
    string =
        text instanceof Player
            ? ((Player) text).getDisplayName()
            : (text.getCustomName() == null
                ? CMIEntityType.get((Entity) text).getTranslatedName()
                : text.getCustomName());
    if (!bl) {
      CMILC.info(
          (Object) this, commandSender, "feedback", snd, "[scale]", d, "[entityName]", string);
    }
    return true;
  }

  private static enum Action {
    Set,
    Add,
    Take,
    Clear,
    Info;
  }
}
