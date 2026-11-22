package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.io.File;
import java.io.IOException;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class setfirstspawn implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback",
        "{gcp}First spawn point set"
            + " ({gcs}[world]{gcp},{gcs}[x]{gcp},{gcs}[y]{gcp},{gcs}[z]{gcp})!");
  }

  @CAnnotation(
      priority = 240,
      info = "Sets first spawn point",
      args = "(playerName)",
      tab = "playername",
      explanation = {},
      regVar = {0, 1},
      consoleVar = 1,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String[] commandArgs = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = commandArgs[argIndex];
      string = arg;
    }

    Player player = cmi.getTarget(commandSender, string, this, false);
    if (player == null) {
      return null;
    }

    Location location = player.getLocation();
    File file = new File(cmi.getDataFolder(), "config.yml");
    YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    yamlConfiguration.options().copyDefaults(true);
    yamlConfiguration.set("Spawn.FirstSpawn.Location", CMILocation.toString(location));
    if (yamlConfiguration.isString("Spawn.FirstSpawn.World")) {
      yamlConfiguration.set("Spawn.FirstSpawn.World", null);
      yamlConfiguration.set("Spawn.FirstSpawn.X", null);
      yamlConfiguration.set("Spawn.FirstSpawn.Y", null);
      yamlConfiguration.set("Spawn.FirstSpawn.Z", null);
      yamlConfiguration.set("Spawn.FirstSpawn.Pitch", null);
      yamlConfiguration.set("Spawn.FirstSpawn.Yaw", null);
    }

    try {
      yamlConfiguration.save(file);
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }

    cmi.getConfigManager().load();
    CMILC.info(this, commandSender, "feedback", location);
    return true;
  }
}
