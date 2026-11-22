package com.Zrips.CMI.Containers;

import java.util.List;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class CMISpawner {
  private Block block;
  private CreatureSpawner spawner = null;

  public CMISpawner(Block spawnerBlock) {
    this.block = spawnerBlock;
    this.spawner = (CreatureSpawner) spawnerBlock.getState();
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

  @Deprecated
  public String getCreatureTypeName() {
    return this.spawner.getCreatureTypeName();
  }

  public int getDelay() {
    return Version.isCurrentEqualOrHigher(Version.v1_13_R1)
        ? this.spawner.getDelay()
        : this.getValue("Delay");
  }

  public int getMaxNearbyEntities() {
    return Version.isCurrentEqualOrHigher(Version.v1_13_R1)
        ? this.spawner.getMaxNearbyEntities()
        : this.getValue("MaxNearbyEntities");
  }

  public int getMaxSpawnDelay() {
    return Version.isCurrentEqualOrHigher(Version.v1_13_R1)
        ? this.spawner.getMaxSpawnDelay()
        : this.getValue("MaxSpawnDelay");
  }

  public int getMinSpawnDelay() {
    return Version.isCurrentEqualOrHigher(Version.v1_13_R1)
        ? this.spawner.getMinSpawnDelay()
        : this.getValue("MinSpawnDelay");
  }

  public int getRequiredPlayerRange() {
    return Version.isCurrentEqualOrHigher(Version.v1_13_R1)
        ? this.spawner.getRequiredPlayerRange()
        : this.getValue("RequiredPlayerRange");
  }

  public int getSpawnCount() {
    return Version.isCurrentEqualOrHigher(Version.v1_13_R1)
        ? this.spawner.getSpawnCount()
        : this.getValue("SpawnCount");
  }

  public int getSpawnRange() {
    return Version.isCurrentEqualOrHigher(Version.v1_13_R1)
        ? this.spawner.getSpawnRange()
        : this.getValue("SpawnRange");
  }

  private int getValue(String string) {
    Short storedShort = new CMINBT(this.block).getShort(string);
    return storedShort instanceof Short ? storedShort : 0;
  }

  public EntityType getSpawnedType() {
    return this.spawner.getSpawnedType();
  }

  @Deprecated
  public void setCreatureTypeByName(String string) {
    this.spawner.setCreatureTypeByName(string);
  }

  public void setDelay(int value) {
    if (Version.isCurrentEqualOrHigher(Version.v1_12_R1)) {
      this.spawner.setDelay(value);
    } else {
      this.updateValue("Delay", value);
    }
  }

  public void setMaxNearbyEntities(int value) {
    if (Version.isCurrentEqualOrHigher(Version.v1_12_R1)) {
      this.spawner.setMaxNearbyEntities(value);
    } else {
      this.updateValue("MaxNearbyEntities", value);
    }
  }

  public void setMaxSpawnDelay(int value) {
    if (Version.isCurrentEqualOrHigher(Version.v1_12_R1)) {
      this.spawner.setMaxSpawnDelay(value);
    } else {
      this.updateValue("MaxSpawnDelay", value);
    }
  }

  public void setMinSpawnDelay(int value) {
    if (Version.isCurrentEqualOrHigher(Version.v1_12_R1)) {
      this.spawner.setMinSpawnDelay(value);
    } else {
      this.updateValue("MinSpawnDelay", value);
    }
  }

  public void setRequiredPlayerRange(int value) {
    if (Version.isCurrentEqualOrHigher(Version.v1_12_R1)) {
      this.spawner.setRequiredPlayerRange(value);
    } else {
      this.updateValue("RequiredPlayerRange", value);
    }
  }

  public void setSpawnCount(int value) {
    if (Version.isCurrentEqualOrHigher(Version.v1_12_R1)) {
      this.spawner.setSpawnCount(value);
    } else {
      this.updateValue("SpawnCount", value);
    }
  }

  public void setSpawnRange(int value) {
    if (Version.isCurrentEqualOrHigher(Version.v1_12_R1)) {
      this.spawner.setSpawnRange(value);
    } else {
      this.updateValue("SpawnRange", value);
    }
  }

  private void updateValue(String string, int value) {
    Object object = new CMINBT(this.block).setShort(string, (short) value);
    CMILib.getInstance().getReflectionManager().updateTileEntity(this.getLocation(), object);
    this.spawner.update();
  }

  public void setSpawnedType(EntityType entityType) {
    this.spawner.setSpawnedType(entityType);
  }
}
