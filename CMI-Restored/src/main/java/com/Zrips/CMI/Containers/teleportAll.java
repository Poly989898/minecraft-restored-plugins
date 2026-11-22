package com.Zrips.CMI.Containers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class teleportAll {
  private String targetFolder = null;
  private String targetWorld = null;
  private Location destination = null;
  private CompletableFuture<Void> id = null;
  private int teleported = 0;
  private List<String> names = new ArrayList<>();
  private Player player;

  public void setTargetFolder(String string) {
    this.targetFolder = string;
  }

  public String getTargetFolder() {
    return this.targetFolder == null
        ? ((World) Bukkit.getWorlds().get(0)).getName()
        : this.targetFolder;
  }

  public void setTargetWorld(String string) {
    this.targetWorld = string;
  }

  public String getTargetWorld() {
    return this.targetWorld;
  }

  public void setDestination(Location location) {
    this.destination = location;
  }

  public Location getDestination() {
    return this.destination;
  }

  public void setTask(CompletableFuture<Void> completableFuture) {
    this.id = completableFuture;
  }

  public Player getPlayer() {
    return this.player;
  }

  public void setPlayer(Player newPlayer) {
    this.player = newPlayer;
  }

  public CompletableFuture<Void> getTask() {
    return this.id;
  }

  public void addName(String string) {
    this.names.add(string);
  }

  public List<String> getNames() {
    return this.names;
  }

  public int getTeleportedCount() {
    return this.teleported;
  }

  public void addTeleported() {
    this.teleported++;
  }
}
