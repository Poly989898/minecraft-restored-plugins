package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Mirror.CMIMirrorType;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class mirror implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("started", "{gcp}Mirroring started");
    configReader.get("stopped", "{gcn}Mirroring stopped");
    configReader.get(
        "tooFar",
        "{gcd}You are too far from mirror center (max {gcs}[blocks] {gcd}blocks). Feature is"
            + " paused!");
    configReader.get("noBlocks", "{gcd}Not enough blocks");
    configReader.get("used", "{gcp}Used: {gcs}[blocks]");
    configReader.get("title", "{gcp}Mirror");
    configReader.get("location", "{gcp}Location");
    configReader.get("offset", "{gcp}Offset");
    configReader.get(
        "offsetPos", "{gcp}X:{gcs}[x] {gcp}Y:{gcs}[y] {gcp}Z:{gcs}[z] {gcp}- {gcs}[id]");
    configReader.get("sections", "{gcp}Sections: {gcs}[sections]");
    configReader.get("flipType", "{gcp}Flip type: {gcs}[type]");
    configReader.get("sectionsAdjust", "{gcp}Shift + Mouse click to adjust");
    configReader.get("blocksToPlace", "{gcp}Blocks to be placed: {gcs}[blocks]{gcp}/{gcs}[total]");
    CMIMirrorType[] cmiMirrorTypes;
    int value = (cmiMirrorTypes = CMIMirrorType.values()).length;

    for (int index = 0; index < value; index++) {
      CMIMirrorType cmiMirrorType = cmiMirrorTypes[index];
      configReader.get("Action." + cmiMirrorType.toString() + ".Name", cmiMirrorType.toString());
      configReader.get("Action." + cmiMirrorType.toString() + ".Desc", cmiMirrorType.getDesc());
    }
  }

  @CAnnotation(
      info = "Starts block place/break mirroring.",
      args = "(start/stop)",
      regVar = {0, 1},
      consoleVar = 666,
      modules = "mirror")
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (strings.length == 0 || strings[0].equals("start")) {
      cmi.getMirrorManager().openGui((Player) commandSender);
      CMILC.info(this, commandSender, "started");
    } else if (strings[0].equals("stop")) {
      cmi.getMirrorManager().removeMirroring((Player) commandSender);
      CMILC.info(this, commandSender, "stopped");
    }

    return true;
  }
}
