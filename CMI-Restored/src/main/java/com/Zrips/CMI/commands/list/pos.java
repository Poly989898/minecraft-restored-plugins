package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Container.CMIBiome;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class pos implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback",
        "{gcp}[playerDisplayName] {gcp}([world]{gcp},{gcs}[x]{gcp}:{gcs}[y]{gcp}:{gcs}[z]{gcp})");
    configReader.get(
        "feedbackRegion",
        "{gcp}Chunk {gcs}[chunkx]{gcp}:{gcs}[chunkz]{gcp} Region"
            + " {gcs}[regionx]{gcp}:{gcs}[regionz]");
    configReader.get(
        "feedbackDirection", "{gcp}Direction {gcs}[direction] {gcp}({gcs}[degrees] {gcp}degrees)");
    configReader.get("feedbackBiome", "{gcp}Biome {gcs}[biome]");
    configReader.get("feedbackLight", "{gcp}Light level {gcs}[lightLevel]");
    configReader.get(
        "netherCoord", "{gcp}Nether World coord: {gcp}({gcs}[x]{gcp}:{gcs}[y]{gcp}:{gcs}[z]{gcp})");
    configReader.get(
        "normalCoord", "{gcp}Normal world coord: {gcp}({gcs}[x]{gcp}:{gcs}[y]{gcp}:{gcs}[z]{gcp})");
  }

  @CAnnotation(
      priority = 40,
      info = "Show current position of a player",
      args = "(playerName)",
      tab = "playername",
      explanation = {},
      regVar = {0, 1},
      consoleVar = 1,
      alias = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (string == null) {
        string = arg;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
    Location location = player.getLocation();
    Snd snd = new Snd().setSender(commandSender).setTarget(player);
    String feedbackMessage = CMILC.getIM(this, "feedback", snd, location);
    String hoverText = "";
    hoverText =
        hoverText
            + CMILC.getIM(
                this,
                "feedbackRegion",
                "[chunkx]",
                location.getChunk().getX(),
                "[chunkz]",
                location.getChunk().getZ(),
                "[regionx]",
                location.getChunk().getX() >> 5,
                "[regionz]",
                location.getChunk().getZ() >> 5);
    hoverText =
        hoverText
            + " \n"
            + CMILC.getIM(
                this,
                "feedbackDirection",
                "[direction]",
                cmiUser.getLookDirection().getName().replace("_", " "),
                "[degrees]",
                cmiUser.getLookDirectionInDegrees());

    try {
      hoverText =
          hoverText
              + " \n"
              + CMILC.getIM(
                  this,
                  "feedbackBiome",
                  "[biome]",
                  CMIBiome.getName(location.getBlock().getBiome()));
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

    hoverText =
        hoverText
            + " \n"
            + CMILC.getIM(
                this, "feedbackLight", "[lightLevel]", location.getBlock().getLightLevel());
    Object object = null;
    if (location.getWorld().getEnvironment() == Environment.NETHER) {
      object =
          new Location(
              location.getWorld(), location.getX() * 8.0, location.getY(), location.getZ() * 8.0);
      hoverText = hoverText + " \n" + CMILC.getIM(this, "normalCoord", snd, object);
    } else if (location.getWorld().getEnvironment() == Environment.NORMAL) {
      object =
          new Location(
              location.getWorld(), location.getX() / 8.0, location.getY(), location.getZ() / 8.0);
      hoverText = hoverText + " \n" + CMILC.getIM(this, "netherCoord", snd, object);
    }

    if (commandSender instanceof Player) {
      RawMessage rawMessage = new RawMessage();
      rawMessage.addText(feedbackMessage);
      rawMessage.addHover(hoverText);
      rawMessage.addSuggestion(CMILocation.toString(location));
      rawMessage.show(commandSender);
    } else {
      commandSender.sendMessage(feedbackMessage);
      commandSender.sendMessage(hoverText.replace("\\n", ""));
    }

    return true;
  }
}
