package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.block.Block;
import org.bukkit.block.EndGateway;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class endgateway implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("off", "{gcp}Beam was toggled {gcd}Off");
    configReader.get("on", "{gcp}Beam was toggled {gcu}On");
  }

  @CAnnotation(info = "Toggle end gateway beam", regVar = 0, consoleVar = 666, others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (Version.isCurrentLower(Version.v1_9_R1)) {
      LC.info_versionNotSupported.sendMessage(commandSender, new Object[0]);
      return null;
    }

    Player player = (Player) commandSender;
    var arrayList = cmi.getUtilManager().getLastTwoTargetBlock(player, 50);
    Block block = arrayList.isEmpty() ? null : (Block) arrayList.get(arrayList.size() - 1);
    CMIMaterial cmiMaterial = CMIMaterial.get(block);
    if (block != null && cmiMaterial == CMIMaterial.END_GATEWAY) {
      if (block.getState() instanceof EndGateway) {
        EndGateway endGateway = (EndGateway) block.getState();
        if (endGateway.getAge() > 0L) {
          endGateway.setAge(-4611686018427387903L);
          CMILC.info(this, commandSender, "off");
        } else {
          endGateway.setAge(0L);
          CMILC.info(this, commandSender, "on");
        }

        endGateway.update();
      }

      return true;
    } else {
      LC.info_lookAtBlock.sendMessage(commandSender, new Object[0]);
      return null;
    }
  }
}
