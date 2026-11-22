package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.SilentChest.silentChestListener;
import com.Zrips.CMI.Modules.Vanish.VanishAction;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class silentchest implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "{gcp}You toggled silent chest mode to {gcs}[offon].");
    configReader.get("onOpen", "{gcp}Opening chest, silently.");
    configReader.get("cantOpen", "{gcw}Unable to open. Loot not generated yet.");
  }

  @CAnnotation(
      priority = 10,
      info = "Toggles silent chest",
      args = "",
      tab = {},
      explanation = {
        "Players arround you wont see chest opening animation when you opening chest",
        "Protocollib required for this feature to work."
      },
      regVar = 0,
      consoleVar = 666,
      modules = "silentChest",
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Player player = (Player) commandSender;
    boolean flag =
        silentChestListener.toggledSilentChest.containsKey(player.getUniqueId())
            && silentChestListener.toggledSilentChest.get(player.getUniqueId());
    if (!flag) {
      silentChestListener.toggledSilentChest.put(player.getUniqueId(), !flag);
    } else {
      silentChestListener.toggledSilentChest.remove(player.getUniqueId());
    }

    CMIUser.getUser(player).getVanish().set(VanishAction.noisyChest, flag);
    CMILC.info(this, commandSender, "feedback", "[offon]", !flag);
    return true;
  }
}
