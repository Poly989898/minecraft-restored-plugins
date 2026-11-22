package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import net.Zrips.CMILib.Container.CMINumber;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CMIViewDistance {
  public static void setView(Player player, int value) {
    value = CMINumber.clamp(value, -1, 32);
    player.setViewDistance(value);
  }

  public static void setSimulation(Player player, int value) {
    value = CMINumber.clamp(value, -1, 32);
    player.setSimulationDistance(value);
  }

  public static void setView(World world, int value) {
    value = CMINumber.clamp(value, 2, 32);
    world.setViewDistance(value);
  }

  public static void setSimulation(World world, int value) {
    value = CMINumber.clamp(value, 2, 32);
    world.setSimulationDistance(value);
  }

  public static void setView(int value) {
    value = CMINumber.clamp(value, 1, 32);
    CMI.getInstance().getNMS().setServerViewDistance(value);
  }

  public static void setSimulation(int value) {
    value = CMINumber.clamp(value, 1, 32);
    CMI.getInstance().getNMS().setServerSimulationDistance(value);
  }

  public static int getView(Player player) {
    return player.getViewDistance();
  }

  public static int getSimulation(Player player) {
    return player.getSimulationDistance();
  }

  public static int getView(World world) {
    return world.getViewDistance();
  }

  public static int getSimulation(World world) {
    return world.getSimulationDistance();
  }

  public static int getView() {
    return CMI.getInstance().getNMS().getServerViewDistance();
  }

  public static int getSimulation() {
    return CMI.getInstance().getNMS().getServerSimulationDistance();
  }
}
