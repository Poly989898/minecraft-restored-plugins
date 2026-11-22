package net.Zrips.CMILib.NBT;

import java.util.List;
import java.util.Set;

public interface CMINBTInterface {
  Integer getInt(String key);

  Byte getByte(String key);

  Long getLong(String key);

  Boolean getBoolean(String key);

  float getFloat(String key);

  Short getShort(String key);

  double getDouble(String key);

  byte[] getByteArray(String key);

  int[] getIntArray(String key);

  long[] getLongArray(String key);

  String getString(String key);

  List<String> getList(String key);

  List<String> getList(String key, int index);

  Object getObjectList(String key, int index);

  Object setBoolean(String key, Boolean value);

  Object setByte(String key, Byte value);

  Object setShort(String key, Short value);

  Object setStringList(String key, List<String> value);

  Object setString(String key, String value);

  Object setInt(String key, Integer value);

  Object setIntArray(String key, int[] value);

  Object setByteArray(String key, byte[] value);

  Object setLongArray(String key, long[] value);

  Object setLong(String key, Long value);

  Object setDouble(String key, Double value);

  Object remove(String key);

  Object set(String key, Object value);

  boolean hasNBT();

  boolean hasNBT(String key);

  Object getNbt();

  Set<String> getKeys();

  Object get(String key);

  Object getCompound(String key);

  byte getTypeId();

  CMINBT.nmbtType getType();
}
