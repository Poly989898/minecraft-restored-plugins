package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Time.timeModifier;
import org.bukkit.command.CommandSender;

public class mutechat implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("mutedFor", "{gcp}Public chat muted for {gcs}[time]");
    configReader.get("isMuted", "!actionbar!{gcp}Public chat is muted for {gcs}[time]");
    configReader.get("muteReason", "{gcp}Mute reason: {gcs}[reason]");
  }

  @CAnnotation(
      info = "Prevent public messages",
      args = "(time) (-s) (reason)",
      tab = {},
      explanation = {"Examples: ", "/cmi mutechat 1m", "/cmi mutechat 1h "},
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Long muteDuration = null;
    boolean flag = false;
    String string = "";
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (muteDuration == null) {
        try {
          Long parsedDuration = timeModifier.getTimeRangeFromString(arg);
          if (parsedDuration != null) {
            muteDuration = parsedDuration;
            continue;
          }
        } catch (Exception exception) {
        }
      }

      if (arg.equalsIgnoreCase("-s")
          && PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender, false)) {
        flag = true;
      } else {
        if (!string.isEmpty()) {
          string = string + " ";
        }

        string = string + arg;
      }
    }

    if (muteDuration == null) {
      muteDuration = 3600L;
    }

    muteDuration = muteDuration > 0L ? muteDuration : 0L;
    cmi.getChatFormatManager().setChatMutedUntil(System.currentTimeMillis() + muteDuration * 1000L);
    cmi.getChatFormatManager().setChatMutedReason(string);
    if (!flag) {
      CMILC.info(
          this,
          commandSender,
          "mutedFor",
          "[time]",
          CMITimeManager.to24hourShort(muteDuration * 1000L));
      CMILC.broadcastMessage(
          commandSender,
          CMILC.getIM(
              this, "mutedFor", "[time]", CMITimeManager.to24hourShort(muteDuration * 1000L)),
          false);
      if (!string.isEmpty()) {
        CMILC.broadcastMessage(
            commandSender, CMILC.getIM(this, "muteReason", "[reason]", string), true);
      }
    }

    return true;
  }
}
