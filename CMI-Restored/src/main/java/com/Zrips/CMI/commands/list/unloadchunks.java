package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.UCInfo;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import java.util.Arrays;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class unloadchunks implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("removed", "{gcp}Removed -{gcs}[list]");
    configReader.get("startScanning", "{gcp}Found &6[amount] {gcp}chunks to unload. Starting!");
    configReader.get(
        "InfoShow",
        "{gcp}TPS: {gcs}[tps] {gcp}Left {gcs}[left] {gcp}({gcs}[min]m:[sec]s{gcp}) Speed:"
            + " {gcs}[speed] {gcp}Unloaded: {gcs}[found]");
    configReader.get(
        "Finished",
        "{gcp}Finished!!! Unloaded: {gcs}[amount] {gcp}chunks! In {gcs}[minutes] {gcp}min"
            + " {gcs}[seconds] {gcp}sec.");
  }

  @CAnnotation(
      info = "Unloads chunks from server memory",
      args = "(-f)",
      explanation = "-f variable will force for all chunks to be unloaded in one go.",
      regVar = -66,
      consoleVar = -66,
      alias = true,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    UCInfo ucInfo = new UCInfo();
    ArrayList arrayList = new ArrayList();

    for (World world : Bukkit.getWorlds()) {
      arrayList.addAll(Arrays.asList(world.getLoadedChunks()));
    }

    ucInfo.setChunks(arrayList);
    int value = ucInfo.getChunks().size();
    CMIActionBar.send(commandSender, CMILC.getIM(this, "startScanning", "[amount]", value));
    ucInfo.setSender(commandSender);
    ucInfo.setStartTime();
    ucInfo.setRunning(true);
    ucInfo.setShowInfo(System.currentTimeMillis());
    if (strings.length > 0 && strings[0].equalsIgnoreCase("-f")) {
      ucInfo.setSpeed(ucInfo.getChunks().size());
    }

    if (cmi.getUnloadChunksManager().ucinfo.containsKey(commandSender.getName())
        && cmi.getUnloadChunksManager().ucinfo.get(commandSender.getName()).getScheduleTask()
            != null) {
      cmi.getUnloadChunksManager().ucinfo.get(commandSender.getName()).getScheduleTask().cancel();
    }

    cmi.getUnloadChunksManager().ucinfo.put(commandSender.getName(), ucInfo);
    cmi.getUnloadChunksManager().loadRegionFile(ucInfo);
    return true;
  }
}
