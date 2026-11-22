package net.Zrips.CMILib.PersistentData;

import net.Zrips.CMILib.CMILib;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CMIChunkPersistentDataContainer extends CMIPersistentDataContainer {
  private final Chunk chunk;
  private final NamespacedKey key;

  public CMIChunkPersistentDataContainer(String key, Chunk chunk) {
    this.chunk = chunk;
    this.key = getNamespacedKey(key);
    this.persistentDataContainer = chunk == null ? null : this.getDataContainer();
  }

  public boolean hasData() {
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

  private static NamespacedKey getNamespacedKey(String key) {
    return new NamespacedKey(CMILib.getInstance(), key);
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
