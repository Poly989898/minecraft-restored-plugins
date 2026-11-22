package com.Zrips.CMI.Modules.Worlds;

import com.Zrips.CMI.CMI;
import java.util.regex.Pattern;
import org.bukkit.scheduler.BukkitTask;

public class WorldManager {
  private CMI plugin;
  public static Pattern regionPattern = Pattern.compile("r\\.(.+)\\.(.+)\\.mca");
  BukkitTask task = null;

  public WorldManager(CMI cmi) {
    this.plugin = cmi;
  }
}
