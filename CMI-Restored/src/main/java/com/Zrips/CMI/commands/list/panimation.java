package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class panimation implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      priority = 115,
      info = "Play custom particle animation",
      args = "(variable/playerName/stopAll) (stop)",
      tab = {"circle;effect:flying_glyph%%playername%%stopAll", "stop"},
      explanation =
          "Information on usage can be found at https://www.zrips.net/cmi/extra/particles/",
      regVar = {1, 2},
      consoleVar = {1, 2},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String animationName = null;
    panimation.Action action = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (action == null) {
        action = panimation.Action.getByName(arg);
        if (action != null) {
          continue;
        }
      }

      if (!arg.contains(":") && !arg.contains(";")) {
        string = arg;
      } else {
        animationName = arg;
      }

      if (arg.contains("target:")) {
        try {
          string = arg.split("target:")[1].split(";")[0];
        } catch (Exception exception) {
        }
      }
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this, false, true);
    if (action != null) {
      switch (action) {
        case stop:
          cmi.getParticleManager().removePlayerEffects(cmiUser.getUniqueId());
          return true;
        case stopall:
          cmi.getParticleManager().removeAllPlayerEffects();
          return true;
      }
    }

    Player player = cmiUser != null ? cmiUser.getPlayer(false) : null;
    cmi.getParticleManager().show(animationName, player);
    return true;
  }

  private enum Action {
    stop,
    stopall;

    public static panimation.Action getByName(String string) {
      panimation.Action[] actions;
      int value = (actions = values()).length;

      for (int index = 0; index < value; index++) {
        panimation.Action action = actions[index];
        if (action.name().equalsIgnoreCase(string)) {
          return action;
        }
      }

      return null;
    }
  }
}
