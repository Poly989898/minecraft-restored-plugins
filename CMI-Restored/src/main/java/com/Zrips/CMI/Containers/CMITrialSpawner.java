package com.Zrips.CMI.Containers;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.TrialSpawner;
import org.bukkit.block.data.BlockData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class CMITrialSpawner {
  private Block block;
  private TrialSpawner spawner = null;

  public CMITrialSpawner(Block spawnerBlock) {
    this.block = spawnerBlock;
    this.spawner = (TrialSpawner) spawnerBlock.getState();
  }

  public Block getBlock() {
    return this.block;
  }

  public BlockData getBlockData() {
    return this.block.getBlockData();
  }

  public byte getLightLevel() {
    return this.spawner.getLightLevel();
  }

  public Location getLocation() {
    return this.block.getLocation();
  }

  public Material getType() {
    return this.block.getType();
  }

  public World getWorld() {
    return this.block.getWorld();
  }

  public int getX() {
    return this.block.getX();
  }

  public int getY() {
    return this.block.getY();
  }

  public int getZ() {
    return this.block.getZ();
  }

  public void setBlockData(BlockData blockData) {
    this.block.setBlockData(blockData);
  }

  public boolean update() {
    return this.spawner.update();
  }

  public boolean update(boolean flag) {
    return this.spawner.update(flag);
  }

  public boolean update(boolean flag, boolean secondFlag) {
    return this.spawner.update(flag, secondFlag);
  }

  public List<MetadataValue> getMetadata(String string) {
    return this.spawner.getMetadata(string);
  }

  public boolean hasMetadata(String string) {
    return this.spawner.hasMetadata(string);
  }

  public void removeMetadata(String string, Plugin plugin) {
    this.spawner.removeMetadata(string, plugin);
  }

  public void setMetadata(String string, MetadataValue metadataValue) {
    this.spawner.setMetadata(string, metadataValue);
  }
}
