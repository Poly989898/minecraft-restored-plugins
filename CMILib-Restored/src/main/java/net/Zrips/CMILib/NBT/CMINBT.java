package net.Zrips.CMILib.NBT;

import java.util.List;
import java.util.Set;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class CMINBT {
  CMINBTInterface face;

  public CMINBT() {
    if (Version.isMojangMappings()) {
      this.face = new CMINBTMojang();
    } else {
      this.face = new CMINBTLegacy();
    }
  }

  public CMINBT(Object nbtTagCompound) {
    if (Version.isMojangMappings()) {
      this.face = new CMINBTMojang(nbtTagCompound);
    } else {
      this.face = new CMINBTLegacy(nbtTagCompound);
    }
  }

  public CMINBT(ItemStack item) {
    if (Version.isMojangMappings()) {
      this.face = new CMINBTMojang(item);
    } else {
      this.face = new CMINBTLegacy(item);
    }
  }

  public CMINBT(Block block) {
    if (Version.isMojangMappings()) {
      this.face = new CMINBTMojang(block);
    } else {
      this.face = new CMINBTLegacy(block);
    }
  }

  public CMINBT(Entity entity) {
    if (Version.isMojangMappings()) {
      this.face = new CMINBTMojang(entity);
    } else {
      this.face = new CMINBTLegacy(entity);
    }
  }

  public Integer getInt(String path) {
    return this.face.getInt(path);
  }

  public Byte getByte(String path) {
    return this.face.getByte(path);
  }

  public Long getLong(String path) {
    return this.face.getLong(path);
  }

  public Boolean getBoolean(String path) {
    return this.face.getBoolean(path);
  }

  public float getFloat(String path) {
    return this.face.getFloat(path);
  }

  public Short getShort(String path) {
    return this.face.getShort(path);
  }

  public double getDouble(String path) {
    return this.face.getDouble(path);
  }

  public byte[] getByteArray(String path) {
    return this.face.getByteArray(path);
  }

  public int[] getIntArray(String path) {
    return this.face.getIntArray(path);
  }

  public long[] getLongArray(String path) {
    return this.face.getLongArray(path);
  }

  public String getString(String path) {
    return this.face.getString(path);
  }

  public List<String> getList(String path) {
    return this.face.getList(path);
  }

  public List<String> getList(String path, int type) {
    return this.face.getList(path, type);
  }

  public Object getObjectList(String path, int type) {
    return this.face.getObjectList(path, type);
  }

  public Object setBoolean(String path, Boolean value) {
    return this.face.setBoolean(path, value);
  }

  public Object setByte(String path, Byte value) {
    return this.face.setByte(path, value);
  }

  public Object setShort(String path, Short value) {
    return this.face.setShort(path, value);
  }

  public Object setStringList(String path, List<String> value) {
    return this.face.setStringList(path, value);
  }

  public Object setString(String path, String value) {
    return this.face.setString(path, value);
  }

  public Object setInt(String path, Integer value) {
    return this.face.setInt(path, value);
  }

  public Object setIntArray(String path, int[] value) {
    return this.face.setIntArray(path, value);
  }

  public Object setByteArray(String path, byte[] value) {
    return this.face.setByteArray(path, value);
  }

  public Object setLongArray(String path, long[] value) {
    return this.face.setLongArray(path, value);
  }

  public Object setLong(String path, Long value) {
    return this.face.setLong(path, value);
  }

  public Object setDouble(String path, Double value) {
    return this.face.setDouble(path, value);
  }

  public Object remove(String path) {
    return this.face.remove(path);
  }

  public Object set(String path, Object nbtbase) {
    return this.face.set(path, nbtbase);
  }

  public boolean hasNBT() {
    return this.face.hasNBT();
  }

  public boolean hasNBT(String key) {
    return this.face.hasNBT(key);
  }

  public Object getNbt() {
    return this.face.getNbt();
  }

  public Set<String> getKeys() {
    return this.face.getKeys();
  }

  public Object get(String path) {
    return this.face.get(path);
  }

  public Object getCompound(String path) {
    return this.face.getCompound(path);
  }

  public byte getTypeId() {
    return this.face.getTypeId();
  }

  public static ItemStack setTag(ItemStack item, Object tag) {
    return Version.isMojangMappings()
        ? CMINBTMojang.setTag(item, tag)
        : CMINBTLegacy.setTag(item, tag);
  }

  public static Object getNbt(Entity entity) {
    return Version.isMojangMappings() ? CMINBTMojang.getNbt(entity) : CMINBTLegacy.getNbt(entity);
  }

  public static Object getNbt(ItemStack item) {
    return Version.isMojangMappings() ? CMINBTMojang.getNbt(item) : CMINBTLegacy.getNbt(item);
  }

  public static Object getNbt(Block block) {
    return Version.isMojangMappings() ? CMINBTMojang.getNbt(block) : CMINBTLegacy.getNbt(block);
  }

  public static Object asItemStackTemplate(ItemStack item) {
    return !Version.isMojangMappings() ? null : CMINBTMojang.asItemTemplate(item);
  }

  public static Object asNMSCopy(ItemStack item) {
    return Version.isMojangMappings() ? CMINBTMojang.asNMSCopy(item) : CMINBTLegacy.asNMSCopy(item);
  }

  public static Object asBukkitCopy(Object item) {
    return Version.isMojangMappings()
        ? CMINBTMojang.asBukkitCopy(item)
        : CMINBTLegacy.asBukkitCopy(item);
  }

  public static Object getEntityHandle(Entity ent) {
    return Version.isMojangMappings()
        ? CMINBTMojang.getEntityHandle(ent)
        : CMINBTLegacy.getEntityHandle(ent);
  }

  public static boolean isNBTSimilar(ItemStack is, ItemStack other) {
    return Version.isMojangMappings()
        ? CMINBTMojang.isNBTSimilar(is, other)
        : CMINBTLegacy.isNBTSimilar(is, other);
  }

  @Deprecated
  public static ItemStack HideFlag(ItemStack item, int state) {
    return Version.isMojangMappings()
        ? CMINBTMojang.HideFlag(item, state)
        : CMINBTLegacy.HideFlag(item, state);
  }

  public static ItemStack modifyItemStack(ItemStack stack, String arguments) {
    return Version.isMojangMappings()
        ? CMINBTMojang.modifyItemStack(stack, arguments)
        : CMINBTLegacy.modifyItemStack(stack, arguments);
  }

  public static String toJson(ItemStack item) {
    return Version.isMojangMappings() ? CMINBTMojang.toJson(item) : CMINBTLegacy.toJson(item);
  }

  public static EntityType getEggType(ItemStack item) {
    return Version.isMojangMappings()
        ? CMINBTMojang.getEggType(item)
        : CMINBTLegacy.getEggType(item);
  }

  public static ItemStack setEggType(ItemStack item, EntityType etype) {
    return Version.isMojangMappings()
        ? CMINBTMojang.setEggType(item, etype)
        : CMINBTLegacy.setEggType(item, etype);
  }

  public static Object newNBTTagList(int type) {
    return Version.isMojangMappings()
        ? CMINBTMojang.newNBTTagList(type)
        : CMINBTLegacy.newNBTTagList(type);
  }

  public static void addToList(Object list, int size, Object data) {
    if (Version.isMojangMappings()) {
      CMINBTMojang.addToList(list, size, data);
    } else {
      CMINBTLegacy.addToList(list, size, data);
    }
  }

  public CMINBT.nmbtType getType() {
    return this.face.getType();
  }

  public enum nmbtType {
    item,
    block,
    entity,
    custom;
  }
}
