package net.Zrips.CMILib;

import java.lang.reflect.Method;
import org.bukkit.World;

public enum ServerType {
  Bukkit,
  Spigot,
  Paper,
  Other;

  static ServerType type = Bukkit;

  static {
    try {
      for (Method one : World.class.getMethods()) {
        if (one.getName().equalsIgnoreCase("spigot")) {
          type = Spigot;
          break;
        }
      }
    } catch (Throwable var5) {
    }

    try {
      for (Method one : World.class.getMethods()) {
        if (one.getName().equalsIgnoreCase("getChunkAtAsync")) {
          type = Paper;
          break;
        }
      }
    } catch (Throwable var4) {
    }
  }
}
