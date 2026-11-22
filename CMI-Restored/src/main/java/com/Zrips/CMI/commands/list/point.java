package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import java.util.List;
import net.Zrips.CMILib.Effects.CMIEffect;
import net.Zrips.CMILib.Effects.CMIEffectManager.CMIParticle;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Time.timeModifier;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class point implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      priority = 125,
      info = "Point to block",
      args = "(particleName) (playerName) (-self) (time) (-s:[speed])",
      tab = {"effect", "playername", "%%-self"},
      explanation = {},
      regVar = {0, 1, 2, 3, 4},
      consoleVar = 666,
      alias = true,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Player player = (Player) commandSender;
    boolean flag = false;
    String string = null;
    Location object = null;
    CMIEffect cmiEffect = null;
    Long time = null;
    Double amount = 0.3;
    String[] parts = strings;
    int value = strings.length;

    for (int count = 0; count < value; count++) {
      String text = parts[count];
      object = cmi.getPlayerManager().convertStringToLocation(strings[0]);
      if (object == null || !object.getWorld().equals(player.getWorld())) {
        if (CMIParticle.getCMIEffect(text) != null) {
          cmiEffect = CMIParticle.getCMIEffect(text);
        } else if (text.equalsIgnoreCase("-self")) {
          flag = true;
        } else {
          if (time == null) {
            try {
              time = timeModifier.getTimeRangeFromString(text);
              if (time != null) {
                continue;
              }
            } catch (Exception exception) {
            }
          }

          try {
            if (text.startsWith("-s:")) {
              amount = Double.parseDouble(text.substring("-s:".length()));
              continue;
            }
          } catch (Exception exception2) {
          }

          string = text;
        }
      }
    }

    if (object == null) {
      if (string == null) {
        Block block = cmi.getUtilManager().getTargetBlock(player, 50, true);
        if (block == null) {
          return null;
        }

        object = block.getLocation().clone().add(0.5, 0.5, 0.5);
      } else {
        Player playerSecond = CMIUser.getOnlinePlayer(string, true);
        if (playerSecond != null && player.canSee(playerSecond)) {
          if (!playerSecond.getWorld().equals(player.getWorld())) {
            return false;
          }

          object = playerSecond.getLocation().clone();
          object.add(0.0, 0.5, 0.0);
        }
      }
    }

    if (object == null) {
      return false;
    }

    if (cmiEffect == null) {
      cmiEffect = cmi.getConfigManager().getPointDefaultEffect();
    }

    if (cmiEffect.getParticle() == null) {
      cmiEffect = new CMIEffect(CMIParticle.DUST);
    }

    if (!cmiEffect
            .getParticle()
            .equals(cmi.getConfigManager().getPointDefaultEffect().getParticle())
        && !PermissionsManager.CMIPerm.command_point_$1.hasPermission(
            commandSender, true, cmiEffect.getParticle().name().toLowerCase())) {
      return null;
    }

    List<Player> list = new ArrayList();
    if (!flag) {
      list = cmi.getUtilManager().getPlayersFromRange(player, player.getLocation(), 32);
    } else {
      list.add(player);
    }

    if (time == null) {
      time = 5000L;
    } else {
      time = time * 1000L;
    }

    if (time > 60000L) {
      time = 60000L;
    }

    cmi.getParticleManager()
        .drawLine(
            player.getEyeLocation().clone().add(0.0, -0.2, 0.0),
            (Location) object,
            cmiEffect,
            false,
            list,
            time,
            amount);
    return true;
  }
}
