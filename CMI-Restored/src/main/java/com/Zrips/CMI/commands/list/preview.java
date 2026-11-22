package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.ChunkInfo;
import com.Zrips.CMI.Containers.ChunkPreviewInfo;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class preview implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "NoMore", "{gcp}Range can't be more than 32 chunks. Changed to maximum allowed.");
    configReader.get("startSending", "{gcp}Found {gcs}[amount] {gcp}chunks to send. Starting!");
    configReader.get(
        "InfoShow",
        "{gcp}TPS: {gcs}[tps] {gcp}Left {gcs}[left] {gcp}({gcs}[min]m:[sec]s{gcp}) Speed:"
            + " {gcs}[speed]");
    configReader.get(
        "Finished", "{gcp}Finished. It can take some extra time to load all chunks on your side.");
  }

  @CAnnotation(
      priority = 285,
      info = "Load chunk for given range",
      args = "[range] (innerrange)",
      tab = {"doubleViewRange", "ViewRange"},
      explanation = {},
      regVar = {1, 2},
      consoleVar = 666,
      alias = true,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (Version.isCurrentEqualOrHigher(Version.v1_18_R1)) {
      LC.info_versionNotSupported.sendMessage(commandSender, new Object[0]);
      return null;
    }

    Player player = (Player) commandSender;
    ChunkPreviewInfo chunkPreviewInfo = new ChunkPreviewInfo();
    int value = -1;

    try {
      value = Integer.parseInt(strings[0]);
    } catch (NumberFormatException numberFormatException) {
      LC.info_UseInteger.sendMessage(commandSender, new Object[0]);
      return null;
    }

    if (value > 32) {
      value = 32;
      CMILC.info(this, commandSender, "NoMore");
    }

    if (value < 0) {
      return false;
    }

    value++;
    int radius = -1;
    if (strings.length == 2) {
      try {
        radius = Integer.parseInt(strings[1]);
      } catch (NumberFormatException numberFormatException2) {
        LC.info_UseInteger.sendMessage(commandSender, new Object[0]);
        return null;
      }
    }

    if (radius < 0 || radius > 32) {
      radius = Bukkit.getViewDistance();
    }

    Chunk chunk = player.getLocation().getChunk();
    chunkPreviewInfo.setRange(value);
    int centerX = chunk.getX();
    int centerZ = chunk.getZ();

    for (int ring = radius; ring < value; ring++) {
      for (int xStart = centerX - ring; xStart <= centerX + ring; xStart++) {
        chunkPreviewInfo.addChunk(new ChunkInfo(xStart, centerZ + ring));
      }

      for (int zStart = centerZ + ring - 1; zStart >= centerZ - ring; zStart--) {
        chunkPreviewInfo.addChunk(new ChunkInfo(centerX + ring, zStart));
      }

      for (int xEnd = centerX + ring - 1; xEnd >= centerX - ring; xEnd--) {
        chunkPreviewInfo.addChunk(new ChunkInfo(xEnd, centerZ - ring));
      }

      for (int zEnd = centerZ - ring + 1; zEnd <= centerZ + ring - 1; zEnd++) {
        chunkPreviewInfo.addChunk(new ChunkInfo(centerX - ring, zEnd));
      }
    }

    int chunkCount = chunkPreviewInfo.getChunks().size();
    CMIActionBar.send(player, CMILC.getIM(this, "startSending", "[amount]", chunkCount));
    chunkPreviewInfo.setPlayer(player);
    chunkPreviewInfo.setWorld(player.getWorld());
    chunkPreviewInfo.setStartTime();
    chunkPreviewInfo.setRunning(true);
    chunkPreviewInfo.setShowInfo(System.currentTimeMillis());
    if (cmi.getChunkPreviewManager().Info.containsKey(player.getUniqueId())
        && cmi.getChunkPreviewManager().Info.get(player.getUniqueId()).getScheduleTask() != null) {
      cmi.getChunkPreviewManager().Info.get(player.getUniqueId()).getScheduleTask().cancel();
    }

    cmi.getChunkPreviewManager().Info.put(player.getUniqueId(), chunkPreviewInfo);
    cmi.getChunkPreviewManager().loadRegionFile(chunkPreviewInfo);
    return true;
  }
}
