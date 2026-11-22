package net.Zrips.CMILib.PersistentData;

import net.Zrips.CMILib.CMILib;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CMIBlockPersistentDataContainer extends CMIPersistentDataContainer {
  private final Chunk chunk;
  private final NamespacedKey key;

  public CMIBlockPersistentDataContainer(Block block) {
    this(block.getLocation());
  }

  public CMIBlockPersistentDataContainer(Location loc) {
    this.chunk = loc == null ? null : loc.getChunk();
    this.key = loc == null ? null : getNamespacedKey(loc);
    this.persistentDataContainer = loc == null ? null : this.getDataContainer();
  }

  public boolean hasBlockData() {
    return this.chunk.getPersistentDataContainer().has(this.key, PersistentDataType.TAG_CONTAINER);
  }

  private PersistentDataContainer getDataContainer() {
    if (!this.chunk.isLoaded()) {
      return null;
    }

    PersistentDataContainer chunkData = this.chunk.getPersistentDataContainer();
    PersistentDataContainer blockPDC =
        (PersistentDataContainer) chunkData.get(this.key, PersistentDataType.TAG_CONTAINER);
    if (blockPDC != null) {
      return blockPDC;
    }

    blockPDC = chunkData.getAdapterContext().newPersistentDataContainer();
    this.chunk
        .getPersistentDataContainer()
        .set(this.key, PersistentDataType.TAG_CONTAINER, blockPDC);
    return blockPDC;
  }

  private static NamespacedKey getNamespacedKey(Location loc) {
    return new NamespacedKey(CMILib.getInstance(), getKey(loc));
  }

  private static String getKey(Location loc) {
    return (loc.getBlockX() & 15) + "." + loc.getBlockY() + "." + (loc.getBlockZ() & 15);
  }

  @Override
  public void save() {
    if (this.persistentDataContainer != null) {
      if (this.persistentDataContainer.isEmpty()) {
        this.chunk.getPersistentDataContainer().remove(this.key);
      } else {
        this.chunk
            .getPersistentDataContainer()
            .set(this.key, PersistentDataType.TAG_CONTAINER, this.persistentDataContainer);
      }
    }
  }
}
