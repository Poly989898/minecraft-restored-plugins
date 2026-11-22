/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.Container.CMINumber
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Locale.LC
 *  org.bukkit.command.CommandSender
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
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class exp implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "current",
        "{gcs}[playerDisplayName] {gcp}is at {gcs}[lvl]{gcp}lvl with {gcs}[currentExp]{gcp}exp or"
            + " with {gcs}[exp]{gcp}exp in total.");
    configReader.get(
        "selfFeedback", "{gcp}Exp was set to {gcs}[exp] {gcp}for {gcs}[playerDisplayName]{gcp}.");
    configReader.get(
        "targetFeedback",
        "{gcp}Your exp was set to {gcs}[exp]{gcp} by {gcs}[senderDisplayName]{gcp}.");
  }

  @Override
  @CAnnotation(
      priority = 180,
      info = "Set players exp. Use L to set levels",
      args =
          "[playerName] [add/set/take/clear] [amount][%rand/10-20%][1%[min-max][[playerName]]]"
              + " (-s)",
      tab = {"playername"},
      explanation = {
        "Example:",
        "/exp 10",
        "/exp add 10",
        "/exp set 10L",
        "/exp take 10",
        "/exp Zrips clear",
        "/exp Zrips add 10",
        "/exp Zrips add 10%[10-50]"
      },
      regVar = {0, 1, 2, 3, 4},
      consoleVar = {1, 2, 3, 4},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    boolean bl = false;
    Action action = Action.Add;
    long l = 0L;
    boolean blValue = false;
    String string = null;
    boolean blSecond = false;
    Integer n = null;
    Integer count = null;
    String text = null;
    Pattern pattern = Pattern.compile("%rand\\/(\\d+|\\d+.?\\d+?)-(\\d+|\\d+.?\\d+?)%");
    Pattern patternSecond =
        Pattern.compile(
            "(^(\\d+(\\.\\d+)?)(%)((\\[(\\d+(\\.\\d+)?)\\-(\\d+(\\.\\d+)?)\\])?(\\[([a-zA-z\\d]+)\\]+)?)$)");
    String[] args = stringArray;
    int countSecond = stringArray.length;
    int countThird = 0;
    while (countThird < countSecond) {
      String textSecond = args[countThird];
      breakSecond:
      switch (textSecond.toLowerCase()) {
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
        case "clear":
          {
            action = Action.Clear;
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
        default:
          {
            String textThird;
            String textFourth;
            Object value;
            Object valueSecond;
            if (textSecond.toLowerCase().contains("l")) {
              try {
                l = Long.valueOf(textSecond.toLowerCase().replace("l", ""));
                blValue = true;
                break;
              } catch (NumberFormatException numberFormatException) {
                // empty catch block
              }
            }
            if (((Matcher) (valueSecond = pattern.matcher(textSecond))).find()) {
              if (!((Matcher) valueSecond).group(0).contains("-")) break;
              value = new Random(System.nanoTime());
              try {
                textFourth = ((Matcher) valueSecond).group(1);
                textThird = ((Matcher) valueSecond).group(2);
                int countFourth = Integer.parseInt(textFourth);
                int countFifth = Integer.parseInt(textThird) + 1;
                if (countFourth >= countFifth) break;
                l = ((Random) value).nextInt(countFifth - countFourth) + countFourth;
              } catch (Exception exception) {
              }
              break;
            }
            value = patternSecond.matcher(textSecond);
            while (((Matcher) value).find()) {
              try {
                l = Long.valueOf(((Matcher) value).group(2));
                l = l < 0L ? 0L : l;
                blSecond = true;
                textFourth = ((Matcher) value).group(7);
                textThird = ((Matcher) value).group(9);
                text = ((Matcher) value).group(12);
                if (textFourth != null) {
                  n = Integer.parseInt(textFourth);
                }
                if (textThird == null) break breakSecond;
                count = Integer.parseInt(textThird);
                break breakSecond;
              } catch (Exception exception) {
                exception.printStackTrace();
              }
            }
            if (string == null && cMI.getPlayerManager().getUserByAproxName(textSecond) != null) {
              string = textSecond;
              break;
            }
            try {
              l = Long.valueOf(textSecond);
              break;
            } catch (NumberFormatException numberFormatException) {
              // empty catch block
            }
          }
      }
      ++countThird;
    }
    int countSixth = 0;
    if (l < (long) countSixth) {
      l = countSixth;
      LC.info_NoLessThan.sendMessage((Object) commandSender, new Object[] {"[amount]", countSixth});
    }
    Player player = cMI.getTarget(commandSender, string, this);
    Snd snd = new Snd().setSender(commandSender).setTarget(player);
    if (player == null) {
      return null;
    }
    Object object = cMI.getPlayerManager().getUser(player);
    if (stringArray.length == 0 || stringArray.length == 1 && string != null) {
      CMILC.info(
          (Object) this,
          commandSender,
          "current",
          snd,
          "[currentExp]",
          ((CMIUser) object).getExpForCurrentLevel());
      return true;
    }
    if (blSecond) {
      l = exp.processValue((CMIUser) object, l, n, count, text, blValue);
    }
    if (!blValue) {
      switch (action) {
        case Add:
          {
            l = ((CMIUser) object).getTotalExp() + l;
            break;
          }
        case Take:
          {
            l = ((CMIUser) object).getTotalExp() - l;
            break;
          }
        case Clear:
          {
            l = 0L;
            break;
          }
      }
    } else {
      switch (action) {
        case Add:
          {
            l = (long) player.getLevel() + l;
            break;
          }
        case Take:
          {
            l = (long) player.getLevel() - l;
            break;
          }
        case Clear:
          {
            l = 0L;
            break;
          }
      }
    }
    if (l < 0L) {
      l = 0L;
    }
    if (!blValue) {
      ((CMIUser) object).setExp(l);
    } else {
      ((CMIUser) object)
          .setLevel((int) CMINumber.clamp((long) l, (long) 0L, (long) Integer.MAX_VALUE));
    }
    cMI.save(player);
    if (!bl) {
      CMILC.info((Object) this, commandSender, "selfFeedback", snd);
      if (!player.getName().equalsIgnoreCase(commandSender.getName())) {
        CMILC.info((Object) this, (CommandSender) player, "targetFeedback", snd);
      }
    }
    return true;
  }

  private static long processValue(
      CMIUser cMIUser, long l, Integer n, Integer count, String string, boolean bl) {
    long lValue = 0L;
    if (string != null) {
      CMIUser cMIUserSecond = CMI.getInstance().getPlayerManager().getUser(string);
      lValue =
          cMIUserSecond != null
              ? (bl ? (long) cMIUserSecond.getLevel() : cMIUserSecond.getTotalExp())
              : lValue;
    }
    long lSecond = l;
    lSecond = string != null ? lValue : (bl ? (long) cMIUser.getLevel() : cMIUser.getTotalExp());
    lSecond = lSecond * l / 100L;
    if (n != null && lSecond < (long) n.intValue()) {
      lSecond = n.intValue();
    }
    if (count != null && lSecond > (long) count.intValue()) {
      lSecond = count.intValue();
    }
    return lSecond;
  }

  private static enum Action {
    Set,
    Add,
    Clear,
    Take;
  }
}
