package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIMessageReplies;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.Arrays;
import java.util.stream.Collectors;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class reply implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("noOneToReply", "{gcp}There is no one to reply");
  }

  @CAnnotation(
      info = "Reply to last message sender",
      args = "[message]",
      regVar = -100,
      consoleVar = -100,
      customAlias = "r",
      ignoreHelpPage = true,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = CMIMessageReplies.getMessageReplyTo(commandSender.getName());
    if (string == null) {
      CMILC.info(this, commandSender, "noOneToReply");
      return null;
    }

    String message = Arrays.asList(strings).stream().collect(Collectors.joining(" "));
    if (cmi.getBungeeCordManager().isBungeeCord()
        && cmi.getBungeeCordManager().getPlayerServer(string) != null) {
      cmi.performCommand(
          commandSender,
          CommandsHandler.getLabel()
              + " "
              + msg.class.getSimpleName()
              + " "
              + string
              + " "
              + message);
    } else {
      CMIUser cmiUser = cmi.getPlayerManager().getUserByAproxName(string);
      Player player = cmiUser == null ? null : cmiUser.getPlayer(false);
      if (player == null
          && !string.equalsIgnoreCase("console")
          && !string.equalsIgnoreCase("server")) {
        LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
        return null;
      }

      cmi.performCommand(
          commandSender,
          CommandsHandler.getLabel()
              + " "
              + msg.class.getSimpleName()
              + " "
              + string
              + " "
              + message);
    }

    return true;
  }
}
