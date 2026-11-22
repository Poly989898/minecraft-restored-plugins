package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class blocknbt implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("noNbt", "{gcp}Block does not contain any NBT data");
  }

  @CAnnotation(info = "Show block NBT information", regVar = 0, consoleVar = 666, others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (Version.isCurrentEqualOrLower(Version.v1_8_R3)) {
      LC.info_versionNotSupported.sendMessage(commandSender, new Object[0]);
      return null;
    }

    Player player = (Player) commandSender;
    Block block = cmi.getUtilManager().getTargetBlock(player, 20);
    if (block != null && !block.getType().equals(Material.AIR)) {
      Object object = CMINBT.getNbt(block);
      if (object == null) {
        CMILC.info(this, commandSender, "noNbt");
        return null;
      } else {
        LC.info_Spliter.sendMessage(commandSender, new Object[0]);
        cmi.getReflectionManager().printNBT(object, 0, commandSender, null);
        LC.info_Spliter.sendMessage(commandSender, new Object[0]);
        return true;
      }
    } else {
      return false;
    }
  }
}
