package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.FindBiome.FindBiomeInfo;
import com.Zrips.CMI.Modules.Region.CMIRegion;
import com.Zrips.CMI.Modules.Region.WorldInfo;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Container.CMIBiome;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class findbiome implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "infoShow",
        "{gcp}TPS: {gcs}[tps] {gcp}Left {gcs}[left] {gcp}({gcs}[time]{gcp}) Speed: {gcs}[speed]");
    configReader.get("stopped", "{gcp}Stopped!");
    configReader.get(
        "cantFindBiome", "{gcp}Could not find biome you wanted. Checked {gcs}[amount] {gcp}chunks");
    configReader.get("teleportedTo", "{gcp}Teleported to {gcs}[biome] {gcp}biome");
  }

  @CAnnotation(
      info = "Finds nearest biome by name",
      args = "(biomeName/stop/stopall)",
      tab = "biome",
      regVar = 1,
      consoleVar = 666,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Player player = (Player) commandSender;
    if (strings[0].equalsIgnoreCase("stop")) {
      cmi.getFindBiomeManager().stop(player);
      CMILC.info(this, player, "stopped");
      return true;
    }

    if (!strings[0].equalsIgnoreCase("stopall")) {
      String string = null;
      if (strings.length == 1) {
        CMIBiome cmiBiome = CMIBiome.get(strings[0]);
        if (cmiBiome != null) {
          string = cmiBiome.getName();
        }
      }

      if (string == null) {
        return false;
      }

      FindBiomeInfo findBiomeInfo = new FindBiomeInfo(player, string);
      Chunk chunk = player.getLocation().getChunk();
      WorldInfo worldInfo =
          cmi.getRegionManager()
              .getWorldInfoInRange(player.getWorld(), new CMIRegion(chunk), Integer.valueOf(312));
      findBiomeInfo.setRI(worldInfo);
      findBiomeInfo.setStartTime();
      findBiomeInfo.setShowInfo(System.currentTimeMillis());
      cmi.getFindBiomeManager().start(findBiomeInfo);
      return true;
    } else {
      for (var entry : cmi.getFindBiomeManager().FindBiomeInfo.entrySet()) {
        player = ((FindBiomeInfo) entry.getValue()).getPlayer();
        cmi.getFindBiomeManager().stop(player);
        CMILC.info(this, player, "stopped");
      }

      return true;
    }
  }
}
