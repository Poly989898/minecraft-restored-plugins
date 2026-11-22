package net.Zrips.CMILib.PersistentData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Version.MinecraftPlatform;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CMIPersistentDataContainer {
  PersistentDataContainer persistentDataContainer = null;
  private static Set<PersistentDataType<?, ?>> DATA_TYPES = new HashSet<>();

  public static CMIPersistentDataContainer get(ItemStack item) {
    return new CMIItemPersistentDataContainer(item);
  }

  public static CMIPersistentDataContainer get(Entity entity) {
    return new CMIEntityPersistentDataContainer(entity);
  }

  public static CMIPersistentDataContainer get(Block block) {
    return new CMIBlockPersistentDataContainer(block);
  }

  @Nullable
  public static CMIPersistentDataContainer get(Object object) {
    if (object instanceof ItemStack) {
      return new CMIItemPersistentDataContainer((ItemStack) object);
    } else if (object instanceof Block) {
      return new CMIBlockPersistentDataContainer((Block) object);
    } else {
      return object instanceof Entity
          ? new CMIEntityPersistentDataContainer((Entity) object)
          : null;
    }
  }

  public void save() {}

  public PersistentDataType<?, ?> getType(String key) {
    return this.getType(getKey(key));
  }

  public PersistentDataType<?, ?> getType(NamespacedKey key) {
    for (PersistentDataType<?, ?> dataType : DATA_TYPES) {
      if (this.persistentDataContainer.has(key, dataType)) {
        return dataType;
      }
    }

    return null;
  }

  private static NamespacedKey getKey(String key) {
    return key.contains(":")
        ? new NamespacedKey(
            key.split(":", 2)[0].toLowerCase(), key.split(":", 2)[1].replace(" ", "_"))
        : new NamespacedKey(CMILib.getInstance(), key.replace(" ", "_"));
  }

  public boolean hasKey(String key) {
    return this.persistentDataContainer == null
        ? false
        : this.persistentDataContainer.has(getKey(key));
  }

  @NotNull
  public Set<NamespacedKey> getKeys() {
    return this.persistentDataContainer == null
        ? new HashSet<>()
        : this.persistentDataContainer.getKeys();
  }

  public CMIPersistentDataContainer set(String key, String value) {
    if (this.persistentDataContainer == null) {
      return this;
    }

    this.persistentDataContainer.set(getKey(key), PersistentDataType.STRING, value);
    return this;
  }

  public CMIPersistentDataContainer remove(String key) {
    if (this.persistentDataContainer == null) {
      return this;
    }

    this.persistentDataContainer.remove(getKey(key));
    return this;
  }

  @Nullable
  public String getString(String key) {
    return (String) this.persistentDataContainer.get(getKey(key), PersistentDataType.STRING);
  }

  public CMIPersistentDataContainer set(String key, int value) {
    if (this.persistentDataContainer == null) {
      return this;
    }

    this.persistentDataContainer.set(getKey(key), PersistentDataType.INTEGER, value);
    return this;
  }

  @Nullable
  public Integer getInt(String key) {
    return this.persistentDataContainer == null
        ? null
        : (Integer) this.persistentDataContainer.get(getKey(key), PersistentDataType.INTEGER);
  }

  public CMIPersistentDataContainer set(String key, long value) {
    if (this.persistentDataContainer == null) {
      return this;
    }

    this.persistentDataContainer.set(getKey(key), PersistentDataType.LONG, value);
    return this;
  }

  @Nullable
  public Long getLong(String key) {
    return this.persistentDataContainer == null
        ? null
        : (Long) this.persistentDataContainer.get(getKey(key), PersistentDataType.LONG);
  }

  public CMIPersistentDataContainer set(String key, long... value) {
    if (this.persistentDataContainer == null) {
      return this;
    }

    this.persistentDataContainer.set(getKey(key), PersistentDataType.LONG_ARRAY, value);
    return this;
  }

  public long @Nullable [] getLongArray(String key) {
    return this.persistentDataContainer == null
        ? null
        : (long[]) this.persistentDataContainer.get(getKey(key), PersistentDataType.LONG_ARRAY);
  }

  public CMIPersistentDataContainer set(String key, boolean value) {
    if (this.persistentDataContainer == null) {
      return this;
    }

    this.persistentDataContainer.set(getKey(key), PersistentDataType.BOOLEAN, value);
    return this;
  }

  @Nullable
  public Boolean getBoolean(String key) {
    return this.persistentDataContainer == null
        ? null
        : (Boolean) this.persistentDataContainer.get(getKey(key), PersistentDataType.BOOLEAN);
  }

  public CMIPersistentDataContainer set(String key, float value) {
    if (this.persistentDataContainer == null) {
      return this;
    }

    this.persistentDataContainer.set(getKey(key), PersistentDataType.FLOAT, value);
    return this;
  }

  @Nullable
  public Float getFloat(String key) {
    return this.persistentDataContainer == null
        ? null
        : (Float) this.persistentDataContainer.get(getKey(key), PersistentDataType.FLOAT);
  }

  public CMIPersistentDataContainer set(String key, short value) {
    if (this.persistentDataContainer == null) {
      return this;
    }

    this.persistentDataContainer.set(getKey(key), PersistentDataType.SHORT, value);
    return this;
  }

  @Nullable
  public Short getShort(String key) {
    return this.persistentDataContainer == null
        ? null
        : (Short) this.persistentDataContainer.get(getKey(key), PersistentDataType.SHORT);
  }

  public CMIPersistentDataContainer set(String key, double value) {
    if (this.persistentDataContainer == null) {
      return this;
    }

    this.persistentDataContainer.set(getKey(key), PersistentDataType.DOUBLE, value);
    return this;
  }

  @Nullable
  public Double getDouble(String key) {
    return this.persistentDataContainer == null
        ? null
        : (Double) this.persistentDataContainer.get(getKey(key), PersistentDataType.DOUBLE);
  }

  public CMIPersistentDataContainer set(String key, byte... value) {
    if (this.persistentDataContainer == null) {
      return this;
    }

    this.persistentDataContainer.set(getKey(key), PersistentDataType.BYTE_ARRAY, value);
    return this;
  }

  public byte @Nullable [] getByteArray(String key) {
    return this.persistentDataContainer == null
        ? null
        : (byte[]) this.persistentDataContainer.get(getKey(key), PersistentDataType.BYTE_ARRAY);
  }

  public CMIPersistentDataContainer set(String key, int... value) {
    if (this.persistentDataContainer == null) {
      return this;
    }

    this.persistentDataContainer.set(getKey(key), PersistentDataType.INTEGER_ARRAY, value);
    return this;
  }

  public int @Nullable [] getIntArray(String key) {
    if (this.persistentDataContainer == null) {
      return null;
    }

    try {
      return (int[])
          this.persistentDataContainer.get(getKey(key), PersistentDataType.INTEGER_ARRAY);
    } catch (Exception var4) {
      try {
        return ((List<Integer>)
                this.persistentDataContainer.get(getKey(key), PersistentDataType.LIST.integers()))
            .stream().mapToInt(i -> i).toArray();
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    }
  }

  public CMIPersistentDataContainer set(String key, byte value) {
    if (this.persistentDataContainer == null) {
      return this;
    }

    this.persistentDataContainer.set(getKey(key), PersistentDataType.BYTE, value);
    return this;
  }

  @Nullable
  public Byte getByte(String key) {
    return this.persistentDataContainer == null
        ? null
        : (Byte) this.persistentDataContainer.get(getKey(key), PersistentDataType.BYTE);
  }

  public CMIPersistentDataContainer set(String key, List<String> value) {
    if (this.persistentDataContainer == null) {
      return this;
    }

    this.persistentDataContainer.set(getKey(key), PersistentDataType.LIST.strings(), value);
    return this;
  }

  public CMIPersistentDataContainer setIntList(String key, List<Integer> value) {
    if (this.persistentDataContainer == null) {
      return this;
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)
        && !Version.getPlatform().equals(MinecraftPlatform.arclight)) {
      this.persistentDataContainer.set(getKey(key), PersistentDataType.LIST.integers(), value);
      return this;
    }

    int[] array = new int[value.size()];

    for (int i = 0; i < value.size(); i++) {
      array[i] = value.get(i);
    }

    this.persistentDataContainer.set(getKey(key), PersistentDataType.INTEGER_ARRAY, array);
    return this;
  }

  @Nullable
  public List<String> getListString(String key) {
    return this.persistentDataContainer == null
        ? null
        : (List) this.persistentDataContainer.get(getKey(key), PersistentDataType.LIST.strings());
  }

  @Nullable
  public List<Integer> getListInt(String key) {
    if (this.persistentDataContainer == null) {
      return null;
    }

    try {
      if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
        return (List<Integer>)
            this.persistentDataContainer.get(getKey(key), PersistentDataType.LIST.integers());
      }
    } catch (Throwable var8) {
    }

    int[] array = this.getIntArray(key);
    if (array == null) {
      return null;
    }

    List<Integer> list = new ArrayList<>();

    for (int i : array) {
      list.add(i);
    }

    return list;
  }

  @Nullable
  public Object get(String key, PersistentDataType<?, ?> type) {
    return this.get(getKey(key), type);
  }

  @Nullable
  public Object get(NamespacedKey key, PersistentDataType<?, ?> type) {
    return this.persistentDataContainer == null
        ? null
        : this.persistentDataContainer.get(key, type);
  }

  private PersistentDataContainer getSubContainer(String key) {
    PersistentDataContainer container = this.getSubContainerIfExists(key);
    if (container == null) {
      container = this.persistentDataContainer.getAdapterContext().newPersistentDataContainer();
    }

    return container;
  }

  private PersistentDataContainer getSubContainerIfExists(String key) {
    if (this.persistentDataContainer == null) {
      return null;
    } else {
      return !this.persistentDataContainer.has(getKey(key), PersistentDataType.TAG_CONTAINER)
          ? null
          : (PersistentDataContainer)
              this.persistentDataContainer.get(getKey(key), PersistentDataType.TAG_CONTAINER);
    }
  }

  public CMIPersistentDataContainer set(String key, String subKey, List<String> value) {
    PersistentDataContainer container = this.getSubContainer(key);
    if (container == null) {
      return this;
    }

    container.set(getKey(subKey), PersistentDataType.LIST.strings(), value);
    this.persistentDataContainer.set(getKey(key), PersistentDataType.TAG_CONTAINER, container);
    return this;
  }

  public CMIPersistentDataContainer set(String key, String subKey, long value) {
    PersistentDataContainer container = this.getSubContainer(key);
    if (container == null) {
      return this;
    }

    container.set(getKey(subKey), PersistentDataType.LONG, value);
    this.persistentDataContainer.set(getKey(key), PersistentDataType.TAG_CONTAINER, container);
    return this;
  }

  public CMIPersistentDataContainer set(String key, String subKey, boolean value) {
    PersistentDataContainer container = this.getSubContainer(key);
    if (container == null) {
      return this;
    } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
      container.set(getKey(subKey), PersistentDataType.BOOLEAN, value);
      this.persistentDataContainer.set(getKey(key), PersistentDataType.TAG_CONTAINER, container);
      return this;
    } else {
      container.set(getKey(subKey), PersistentDataType.BYTE, (byte) (value ? 1 : 0));
      this.persistentDataContainer.set(getKey(key), PersistentDataType.TAG_CONTAINER, container);
      return this;
    }
  }

  public CMIPersistentDataContainer set(String key, String subKey, int value) {
    PersistentDataContainer container = this.getSubContainer(key);
    if (container == null) {
      return this;
    }

    container.set(getKey(subKey), PersistentDataType.INTEGER, value);
    this.persistentDataContainer.set(getKey(key), PersistentDataType.TAG_CONTAINER, container);
    return this;
  }

  public CMIPersistentDataContainer setIntList(String key, String subKey, List<Integer> value) {
    PersistentDataContainer container = this.getSubContainer(key);
    if (container == null) {
      return this;
    }

    container.set(getKey(subKey), PersistentDataType.LIST.integers(), value);
    this.persistentDataContainer.set(getKey(key), PersistentDataType.TAG_CONTAINER, container);
    return this;
  }

  public CMIPersistentDataContainer remove(String key, String subKey) {
    PersistentDataContainer container = this.getSubContainerIfExists(key);
    if (container == null) {
      return this;
    }

    container.remove(getKey(subKey));
    this.persistentDataContainer.set(getKey(key), PersistentDataType.TAG_CONTAINER, container);
    return this;
  }

  @Nullable
  public List<String> getListString(String key, String subKey) {
    if (this.persistentDataContainer == null) {
      return null;
    }

    PersistentDataContainer container = this.getSubContainerIfExists(key);
    return container == null
        ? null
        : (List) container.get(getKey(subKey), PersistentDataType.LIST.strings());
  }

  @Nullable
  public Boolean getBoolean(String key, String subKey) {
    if (this.persistentDataContainer == null) {
      return null;
    }

    PersistentDataContainer container = this.getSubContainerIfExists(key);
    if (container == null) {
      return null;
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
      return (Boolean) container.get(getKey(subKey), PersistentDataType.BOOLEAN);
    }

    Byte b = (Byte) container.get(getKey(subKey), PersistentDataType.BYTE);
    return b == null ? null : b == 1;
  }

  @Nullable
  public Long getLong(String key, String subKey) {
    if (this.persistentDataContainer == null) {
      return null;
    }

    PersistentDataContainer container = this.getSubContainerIfExists(key);
    return container == null ? null : (Long) container.get(getKey(subKey), PersistentDataType.LONG);
  }

  @Nullable
  public Integer getInt(String key, String subKey) {
    if (this.persistentDataContainer == null) {
      return null;
    }

    PersistentDataContainer container = this.getSubContainerIfExists(key);
    return container == null
        ? null
        : (Integer) container.get(getKey(subKey), PersistentDataType.INTEGER);
  }

  @Nullable
  public Object get(String key) {
    return this.get(getKey(key));
  }

  @Nullable
  public Object get(NamespacedKey key) {
    PersistentDataType<?, ?> type = this.getType(key);
    return type != null && this.persistentDataContainer != null
        ? this.persistentDataContainer.get(key, type)
        : null;
  }

  static {
    DATA_TYPES.add(PersistentDataType.BYTE);
    DATA_TYPES.add(PersistentDataType.SHORT);
    DATA_TYPES.add(PersistentDataType.INTEGER);
    DATA_TYPES.add(PersistentDataType.LONG);
    DATA_TYPES.add(PersistentDataType.FLOAT);
    DATA_TYPES.add(PersistentDataType.DOUBLE);
    DATA_TYPES.add(PersistentDataType.STRING);
    DATA_TYPES.add(PersistentDataType.BYTE_ARRAY);
    DATA_TYPES.add(PersistentDataType.INTEGER_ARRAY);
    DATA_TYPES.add(PersistentDataType.LONG_ARRAY);

    try {
      DATA_TYPES.add(PersistentDataType.BOOLEAN);
    } catch (Throwable var9) {
    }

    try {
      DATA_TYPES.add(PersistentDataType.TAG_CONTAINER);
    } catch (Throwable var8) {
    }

    try {
      DATA_TYPES.add(PersistentDataType.LIST.bytes());
    } catch (Throwable var7) {
    }

    try {
      DATA_TYPES.add(PersistentDataType.LIST.booleans());
    } catch (Throwable var6) {
    }

    try {
      DATA_TYPES.add(PersistentDataType.LIST.doubles());
    } catch (Throwable var5) {
    }

    try {
      DATA_TYPES.add(PersistentDataType.LIST.floats());
    } catch (Throwable var4) {
    }

    try {
      DATA_TYPES.add(PersistentDataType.LIST.integers());
    } catch (Throwable var3) {
    }

    try {
      DATA_TYPES.add(PersistentDataType.LIST.strings());
    } catch (Throwable var2) {
    }

    try {
      DATA_TYPES.add(PersistentDataType.LIST.shorts());
    } catch (Throwable var1) {
    }
  }
}
