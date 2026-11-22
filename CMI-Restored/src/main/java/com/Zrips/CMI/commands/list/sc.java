package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.AllListeners.PlayerListeners;
import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIColorTypes;
import com.Zrips.CMI.Containers.CMITextCleaner;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.utils.Util;
import java.util.ArrayList;
import java.util.Arrays;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class sc implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "punch",
        "{gcp}Punch sign to paste text into it, or punch any non-sign block to cancel process");
    configReader.get("canceled", "{gcp}Sign copy process cancelled");
    configReader.get("updated", "{gcp}Sign updated");
  }

  @CAnnotation(
      info = "Starts sign copy process",
      args = "(playerName) (oneside)",
      multiTab = "[playername],!oneside !oneside",
      regVar = {0, 1, 2},
      consoleVar = 666,
      customAlias = "!scopy")
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    Player player = (Player) commandSender;
    boolean flag = false;
    String[] lines = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = lines[argIndex];
      if (arg.equals("oneside")) {
        flag = true;
      } else if (string == null) {
        string = arg;
      }
    }

    Player targetPlayer = cmi.getTarget(commandSender, string, this);
    if (targetPlayer == null) {
      return null;
    }

    Block block = cmi.getUtilManager().getTargetBlock(player, 20);
    if (block != null && block.getState() instanceof Sign) {
      Sign sign = (Sign) block.getState();
      if (Version.isCurrentEqualOrLower(Version.v1_19_R3)) {
        lines = sign.getLines();
        PlayerListeners.scMap.put(targetPlayer.getUniqueId(), lines);
      } else {
        lines = new String[!flag ? 8 : 4];
        ArrayList arrayList = new ArrayList();
        Util.SignSide signSide = Util.getSignSide(block, targetPlayer.getLocation());
        if (signSide.equals(Util.SignSide.FRONT)) {
          SignSide frontSide = sign.getSide(Side.FRONT);
          SignSide backSide = sign.getSide(Side.BACK);
          arrayList.addAll(Arrays.asList(frontSide.getLines()));
          if (!flag) {
            arrayList.addAll(Arrays.asList(backSide.getLines()));
          }
        } else {
          SignSide backSideAlt = sign.getSide(Side.BACK);
          SignSide frontSideAlt = sign.getSide(Side.FRONT);
          arrayList.addAll(Arrays.asList(backSideAlt.getLines()));
          if (!flag) {
            arrayList.addAll(Arrays.asList(frontSideAlt.getLines()));
          }
        }

        for (int lineIndex = 0; lineIndex < arrayList.size(); lineIndex++) {
          if (arrayList.get(lineIndex) != null) {
            arrayList.set(
                lineIndex,
                CMITextCleaner.cleanFromColorCodes(
                    commandSender, (String) arrayList.get(lineIndex), CMIColorTypes.signs, true));
          }
        }

        arrayList.toArray(lines);
        PlayerListeners.scMap.put(targetPlayer.getUniqueId(), lines);
      }

      CMILC.info(this, targetPlayer, "punch");
      return true;
    } else {
      LC.info_lookAtSign.sendMessage(commandSender, new Object[0]);
      return null;
    }
  }
}
