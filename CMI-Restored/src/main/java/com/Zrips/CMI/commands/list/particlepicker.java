package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Effects.CMIEffect;
import net.Zrips.CMILib.Effects.CMIEffectPicker;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class particlepicker implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      info = "Pick particle",
      args = "(particleName)",
      regVar = {0, 1},
      consoleVar = 666,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    CMIEffect cmiEffect = null;
    if (strings.length == 1) {
      cmiEffect = CMIEffect.deserialize(strings[1]);
    }

    CMIEffectPicker cmiEffectPicker = new CMIEffectPicker((Player) commandSender, cmiEffect);
    cmiEffectPicker.setShowSerializeButton(true);
    cmiEffectPicker.open();
    return true;
  }
}
