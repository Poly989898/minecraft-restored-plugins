package net.Zrips.CMILib.NBT;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.PersistentData.CMIPersistentDataContainer;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CMINBTMojang implements CMINBTInterface {
  private static Object CODEC;
  private static Object serializator;
  private static Method encodeMethod;
  private static Class<?> CRAFT_ITEM_STACK;
  private static Class<?> NMS_ITEM_STACK;
  private static Class<?> DATA_COMPONENTS;
  private static Class<?> CUSTOM_DATA;
  private static Class<?> COMPOUND_TAG;
  private static Class<?> COMPOUND_TAGLIST;
  private static Constructor<?> ITEM_TEMPLATE;
  private static Method AS_NMS_COPY;
  private static Method AS_BUKKIT_COPY;
  private static Method GET_ITEM;
  private static Method NMS_GET_COMPONENT;
  private static Method NMS_SET_COMPONENT;
  private static Field CUSTOM_DATA_COMPONENT;
  private static Method CUSTOMDATA_OF;
  private static Method CUSTOMDATA_COPYTAG;
  private static Method TAG_CONTAINS;
  private static Method TAG_GET;
  private static Method TAG_GET_STRING;
  private static Method TAG_PUT_STRING;
  private static Method TAG_EQUALS;
  private static Method TAG_GET_BOOLEAN;
  private static Method TAG_GET_BYTE;
  private static Method TAG_GET_SHORT;
  private static Method TAG_GET_INT;
  private static Method TAG_GET_LONG;
  private static Method TAG_GET_FLOAT;
  private static Method TAG_GET_DOUBLE;
  private static Method TAG_GET_BYTE_ARRAY;
  private static Method TAG_GET_INT_ARRAY;
  private static Method TAG_GET_LONG_ARRAY;
  private static Method TAG_GET_LIST;
  private static Method TAG_GET_COMPOUND;
  private static Method TAG_PUT_COMPOUND;
  private static Method TAG_REMOVE;
  private static Object registry;
  private static Method nbtMethod;
  Object tag;
  Object object;
  private CMINBT.nmbtType type;
  private static Method met_getOrThrow;

  private static Object getRegistry() {
    if (registry == null) {
      try {
        if (Version.isMojangMappings()) {
          registry =
              Class.forName("org.bukkit.craftbukkit.CraftRegistry")
                  .getMethod("getMinecraftRegistry")
                  .invoke(null);
        } else if (Version.isCurrentEqualOrHigher(Version.v1_21_R4)) {
          registry = getBukkitClass("CraftRegistry").getMethod("getMinecraftRegistry").invoke(null);
        } else {
          Object server = CMILib.getInstance().getReflectionManager().getDedicatedServer();
          registry = server.getClass().getMethod("getDefaultRegistryAccess").invoke(server);
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }

    return registry;
  }

  private static Class<?> getBukkitClass(String nmsClassString) {
    try {
      return Class.forName(
          "org.bukkit.craftbukkit." + Version.getCurrent().toString() + "." + nmsClassString);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public CMINBTMojang() {
    this.type = CMINBT.nmbtType.custom;
  }

  public CMINBTMojang(Object nbtTagCompound) {
    this.tag = nbtTagCompound;
    this.type = CMINBT.nmbtType.custom;
  }

  public CMINBTMojang(ItemStack item) {
    this.object = item;
    this.tag = getNbt(item);
    this.type = CMINBT.nmbtType.item;
  }

  public CMINBTMojang(Block block) {
    this.tag = getNbt(block);
    this.object = block;
    this.type = CMINBT.nmbtType.block;
  }

  public CMINBTMojang(Entity entity) {
    this.tag = getNbt(entity);
    this.object = entity;
    this.type = CMINBT.nmbtType.entity;
  }

  private Object get(String path, Method getMethod) {
    if (this.getNbt() == null) {
      return null;
    }

    try {
      Object t = this.updateLegacyTag(this.getNbt());
      if (!path.contains(".")) {
        return getMethod.invoke(t, path);
      }

      List<String> keys = new ArrayList<>();
      keys.addAll(Arrays.asList(path.split("\\.")));

      try {
        Object nbtbase = TAG_GET.invoke(t, keys.get(0));

        for (int i = 1; i < keys.size(); i++) {
          if (i + 1 >= keys.size()) {
            if (nbtbase != null) {
              return getMethod.invoke(nbtbase, keys.get(i));
            }
            break;
          }

          nbtbase = TAG_GET.invoke(nbtbase, keys.get(i));
        }
      } catch (Throwable var7) {
      }

      return getMethod.invoke(t, path);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public Integer getInt(String path) {
    if (!this.hasNBT(path)) {
      return null;
    }

    CMIPersistentDataContainer persistentDataContainer =
        CMIPersistentDataContainer.get(this.object);
    if (persistentDataContainer != null) {
      Integer v = persistentDataContainer.getInt(path);
      if (v != null) {
        return v;
      }
    }

    try {
      Object res = getFromOptional(this.get(path, TAG_GET_INT));
      return res == null ? null : (Integer) res;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public Byte getByte(String path) {
    if (!this.hasNBT(path)) {
      return null;
    }

    CMIPersistentDataContainer persistentDataContainer =
        CMIPersistentDataContainer.get(this.object);
    if (persistentDataContainer != null) {
      Byte v = CMIPersistentDataContainer.get(this.object).getByte(path);
      if (v != null) {
        return v;
      }
    }

    try {
      Object res = getFromOptional(this.get(path, TAG_GET_BYTE));
      return res == null ? null : (Byte) res;
    } catch (Exception var4) {
      return null;
    }
  }

  @Override
  public Long getLong(String path) {
    if (!this.hasNBT(path)) {
      return null;
    }

    CMIPersistentDataContainer persistentDataContainer =
        CMIPersistentDataContainer.get(this.object);
    if (persistentDataContainer != null) {
      Long v = persistentDataContainer.getLong(path);
      if (v != null) {
        return v;
      }
    }

    try {
      Object res = getFromOptional(this.get(path, TAG_GET_LONG));
      return res == null ? null : (Long) res;
    } catch (Exception var4) {
      return null;
    }
  }

  @Override
  public Boolean getBoolean(String path) {
    if (!this.hasNBT(path)) {
      return null;
    }

    CMIPersistentDataContainer persistentDataContainer =
        CMIPersistentDataContainer.get(this.object);
    if (persistentDataContainer != null) {
      Boolean v = persistentDataContainer.getBoolean(path);
      if (v != null) {
        return v;
      }
    }

    try {
      Object res = getFromOptional(this.get(path, TAG_GET_BOOLEAN));
      return res == null ? null : (Boolean) res;
    } catch (Exception var4) {
      return null;
    }
  }

  @Override
  public float getFloat(String path) {
    if (!this.hasNBT(path)) {
      return 0.0F;
    }

    CMIPersistentDataContainer persistentDataContainer =
        CMIPersistentDataContainer.get(this.object);
    if (persistentDataContainer != null) {
      Float v = persistentDataContainer.getFloat(path);
      if (v != null) {
        return v;
      }
    }

    try {
      Object res = getFromOptional(this.get(path, TAG_GET_FLOAT));
      return res == null ? 0.0F : (Float) res;
    } catch (Exception var4) {
      return 0.0F;
    }
  }

  @Override
  public Short getShort(String path) {
    CMIPersistentDataContainer persistentDataContainer =
        CMIPersistentDataContainer.get(this.object);
    if (persistentDataContainer != null) {
      Short v = persistentDataContainer.getShort(path);
      if (v != null) {
        return v;
      }
    }

    if (this.tag == null) {
      return null;
    }

    try {
      Object res = getFromOptional(this.get(path, TAG_GET_SHORT));
      return res == null ? null : (Short) res;
    } catch (Exception var4) {
      return null;
    }
  }

  @Override
  public double getDouble(String path) {
    if (!this.hasNBT(path)) {
      return 0.0;
    }

    CMIPersistentDataContainer persistentDataContainer =
        CMIPersistentDataContainer.get(this.object);
    if (persistentDataContainer != null) {
      Double v = persistentDataContainer.getDouble(path);
      if (v != null) {
        return v;
      }
    }

    try {
      Object res = getFromOptional(this.get(path, TAG_GET_DOUBLE));
      return res == null ? 0.0 : (Double) res;
    } catch (Exception var4) {
      return 0.0;
    }
  }

  @Override
  public byte[] getByteArray(String path) {
    if (!this.hasNBT(path)) {
      return new byte[0];
    }

    CMIPersistentDataContainer persistentDataContainer =
        CMIPersistentDataContainer.get(this.object);
    if (persistentDataContainer != null) {
      byte[] v = persistentDataContainer.getByteArray(path);
      if (v != null) {
        return v;
      }
    }

    try {
      Object res = getFromOptional(this.get(path, TAG_GET_BYTE_ARRAY));
      return res == null ? new byte[0] : (byte[]) res;
    } catch (Exception var4) {
      return new byte[0];
    }
  }

  @Override
  public int[] getIntArray(String path) {
    if (!this.hasNBT(path)) {
      return new int[0];
    }

    try {
      Object res = getFromOptional(this.get(path, TAG_GET_INT_ARRAY));
      return res == null ? new int[0] : (int[]) res;
    } catch (Exception var3) {
      return new int[0];
    }
  }

  @Override
  public long[] getLongArray(String path) {
    if (!this.hasNBT(path)) {
      return new long[0];
    }

    try {
      Object res = getFromOptional(this.get(path, TAG_GET_LONG_ARRAY));
      return res == null ? new long[0] : (long[]) res;
    } catch (Exception var3) {
      return new long[0];
    }
  }

  @Override
  public String getString(String path) {
    if (!this.hasNBT(path)) {
      return null;
    }

    CMIPersistentDataContainer persistentDataContainer =
        CMIPersistentDataContainer.get(this.object);
    if (persistentDataContainer != null) {
      String v = persistentDataContainer.getString(path);
      if (v != null) {
        return v;
      }
    }

    if (this.getNbt() == null) {
      return null;
    }

    try {
      Object res = getFromOptional(this.get(path, TAG_GET_STRING));
      return res == null ? null : (String) res;
    } catch (Exception var4) {
      return null;
    }
  }

  @Override
  public List<String> getList(String path) {
    return this.getList(path, -1);
  }

  private Object updateLegacyTag(Object t) {
    if (this.getType().equals(CMINBT.nmbtType.item)) {
      CMINBTMojang mt = new CMINBTMojang(t);
      if (mt.hasNBT("components")) {
        t = getFromOptional(new CMINBTMojang(t).get("components"));
        mt = new CMINBTMojang(t);
        if (mt.hasNBT("minecraft:custom_data")) {
          t = getFromOptional(new CMINBTMojang(t).get("minecraft:custom_data"));
        }
      }
    }

    return t;
  }

  private static Object getFromOptional(Object optional) {
    return optional instanceof Optional ? ((Optional) optional).orElse(null) : optional;
  }

  @Override
  public List<String> getList(String path, int type) {
    if (!this.hasNBT(path)) {
      return null;
    }

    CMIPersistentDataContainer persistentDataContainer =
        CMIPersistentDataContainer.get(this.object);
    if (persistentDataContainer != null) {
      List<String> v = persistentDataContainer.getListString(path);
      if (v != null) {
        return new ArrayList<>(v);
      }
    }

    if (this.getNbt() == null) {
      return null;
    }

    List<String> list = new ArrayList<>();

    try {
      Object t = this.updateLegacyTag(this.getNbt());
      if (t == null) {
        return list;
      }

      if (path.contains(".")) {
        List<String> keys = new ArrayList<>();
        keys.addAll(Arrays.asList(path.split("\\.")));

        try {
          Object nbtbase = TAG_GET.invoke(t, keys.get(0));

          for (int i = 1; i < keys.size(); i++) {
            if (i + 1 >= keys.size()) {
              t = nbtbase;
              path = keys.get(i);
              break;
            }

            nbtbase = TAG_GET.invoke(nbtbase, keys.get(i));
          }
        } catch (Throwable var11) {
        }
      }

      Object ls = getFromOptional(TAG_GET_LIST.invoke(t, path));
      int size = (Integer) ls.getClass().getMethod("size").invoke(ls);
      if (size == 0 && type < 0) {
        ls = getFromOptional(TAG_GET_LIST.invoke(t, path));
        size = (Integer) ls.getClass().getMethod("size").invoke(ls);
      }

      Method method = ls.getClass().getMethod("getString", int.class);

      for (int i = 0; i < size; i++) {
        Object ress = getFromOptional(method.invoke(ls, i));
        list.add(ress == null ? "" : ress.toString());
      }

      return list;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public Object getObjectList(String path, int type) {
    if (!this.hasNBT()) {
      return null;
    }

    if (!this.hasNBT(path)) {
      return null;
    }

    try {
      Object t = this.updateLegacyTag(this.getNbt());
      if (t != null && path.contains(".")) {
        List<String> keys = new ArrayList<>();
        keys.addAll(Arrays.asList(path.split("\\.")));

        try {
          Object nbtbase = TAG_GET.invoke(t, keys.get(0));

          for (int i = 1; i < keys.size(); i++) {
            if (i + 1 >= keys.size()) {
              t = nbtbase;
              path = keys.get(i);
              break;
            }

            nbtbase = TAG_GET.invoke(nbtbase, keys.get(i));
          }
        } catch (Throwable var7) {
        }
      }

      if (t == null) {
        return null;
      }

      Object ls = getFromOptional(TAG_GET_LIST.invoke(t, path));
      int size = (Integer) ls.getClass().getMethod("size").invoke(ls);
      if (size == 0 && type < 0) {
        ls = getFromOptional(TAG_GET_LIST.invoke(t, path));
        size = (Integer) ls.getClass().getMethod("size").invoke(ls);
      }

      return ls;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private Object removeItemTag(String path) {
    if (!this.type.equals(CMINBT.nmbtType.item)) {
      return this.object;
    }

    if (!this.hasNBT(path)) {
      return this.object;
    }

    try {
      Object t = this.updateLegacyTag(this.getNbt());
      TAG_REMOVE.invoke(t, path);
      return setTag((ItemStack) this.object, t);
    } catch (Throwable e) {
      e.printStackTrace();
      return this.object;
    }
  }

  @Override
  public Object setBoolean(String path, Boolean value) {
    if (value == null) {
      return this.remove(path);
    }

    CMIPersistentDataContainer.get(this.object).set(path, value).save();
    return this.object;
  }

  @Override
  public Object setByte(String path, Byte value) {
    if (value == null) {
      return this.remove(path);
    }

    CMIPersistentDataContainer.get(this.object).set(path, value).save();
    return this.object;
  }

  @Override
  public Object setShort(String path, Short value) {
    if (value == null) {
      return this.remove(path);
    }

    CMIPersistentDataContainer.get(this.object).set(path, value).save();
    return this.object;
  }

  @Override
  public Object setStringList(String path, List<String> value) {
    if (value == null) {
      return this.remove(path);
    }

    CMIPersistentDataContainer.get(this.object).set(path, value).save();
    return this.object;
  }

  @Override
  public Object setString(String path, String value) {
    if (value == null) {
      return this.remove(path);
    }

    CMIPersistentDataContainer.get(this.object).set(path, value).save();
    return this.object;
  }

  @Override
  public Object setInt(String path, Integer value) {
    if (value == null) {
      return this.remove(path);
    }

    CMIPersistentDataContainer.get(this.object).set(path, value).save();
    return this.object;
  }

  @Override
  public Object setIntArray(String path, int[] value) {
    if (value == null) {
      return this.remove(path);
    }

    CMIPersistentDataContainer.get(this.object).set(path, value).save();
    return this.object;
  }

  @Override
  public Object setByteArray(String path, byte[] value) {
    if (value == null) {
      return this.remove(path);
    }

    CMIPersistentDataContainer.get(this.object).set(path, value).save();
    return this.object;
  }

  @Override
  public Object setLongArray(String path, long[] value) {
    if (value == null) {
      return this.remove(path);
    }

    CMIPersistentDataContainer.get(this.object).set(path, value).save();
    return this.object;
  }

  @Override
  public Object setLong(String path, Long value) {
    if (value == null) {
      return this.remove(path);
    }

    CMIPersistentDataContainer.get(this.object).set(path, value).save();
    return this.object;
  }

  @Override
  public Object setDouble(String path, Double value) {
    if (value == null) {
      return this.remove(path);
    }

    CMIPersistentDataContainer.get(this.object).set(path, value).save();
    return this.object;
  }

  @Override
  public Object remove(String path) {
    CMIPersistentDataContainer it = CMIPersistentDataContainer.get(this.object);
    it.remove(path).save();
    if (this.getType().equals(CMINBT.nmbtType.item)) {
      this.tag = getNbt((ItemStack) this.object);
    }

    return this.removeItemTag(path);
  }

  @Override
  public Object set(String path, Object nbtbase) {
    try {
      TAG_PUT_COMPOUND.invoke(this.getNbt(), path, nbtbase);
    } catch (Throwable e) {
      if (Version.isCurrentEqualOrHigher(Version.v1_7_R4)) {
        e.printStackTrace();
      }
    }

    switch (this.type) {
      case block:
      case entity:
      default:
        return this.object;
      case custom:
        return this.getNbt();
      case item:
        return setTag((ItemStack) this.object, this.updateLegacyTag(this.getNbt()));
    }
  }

  @Override
  public boolean hasNBT() {
    return this.tag != null;
  }

  @Override
  public boolean hasNBT(String key) {
    CMIPersistentDataContainer persistent = CMIPersistentDataContainer.get(this.object);
    if (persistent != null && persistent.hasKey(key)) {
      return true;
    }

    if (!this.hasNBT()) {
      return false;
    }

    boolean contains = false;

    try {
      contains = (Boolean) TAG_CONTAINS.invoke(this.updateLegacyTag(this.getNbt()), key);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return contains;
  }

  @Override
  public Object getNbt() {
    return this.tag;
  }

  @Override
  public Set<String> getKeys() {
    if (!this.hasNBT()) {
      return new HashSet<>();
    }

    try {
      return (Set<String>) this.getNbt().getClass().getMethod("keySet").invoke(this.getNbt());
    } catch (Throwable e) {
      e.printStackTrace();
      return new HashSet<>();
    }
  }

  @Override
  public Object get(String path) {
    Object nbt = this.getNbt();
    if (nbt == null) {
      return null;
    }

    try {
      return TAG_GET.invoke(this.updateLegacyTag(this.getNbt()), path);
    } catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public Object getCompound(String path) {
    Object nbt = this.getNbt();
    if (nbt == null) {
      return null;
    }

    try {
      return getFromOptional(TAG_GET_COMPOUND.invoke(this.updateLegacyTag(this.getNbt()), path));
    } catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public byte getTypeId() {
    try {
      return (Byte) this.getNbt().getClass().getMethod("getId").invoke(this.getNbt());
    } catch (Throwable e) {
      e.printStackTrace();
      return 0;
    }
  }

  public static ItemStack setTag(ItemStack item, Object tag) {
    if (item == null) {
      return item;
    }

    Object nms = asNMSCopy(item);

    try {
      Object newCustom = CUSTOMDATA_OF.invoke(null, tag);
      NMS_SET_COMPONENT.invoke(nms, CUSTOM_DATA_COMPONENT.get(null), newCustom);
      return (ItemStack) asBukkitCopy(nms);
    } catch (Throwable e) {
      e.printStackTrace();
      return item;
    }
  }

  public static Object getNbt(Entity entity) {
    return null;
  }

  public static Object getNbt(ItemStack item) {
    if (item != null && !item.getType().equals(Material.AIR)) {
      Object nmsStack = asNMSCopy(item);
      if (nmsStack == null) {
        return null;
      }

      try {
        Object res = encodeMethod.invoke(CODEC, serializator, nmsStack);
        if (met_getOrThrow == null) {
          met_getOrThrow = res.getClass().getMethod("getOrThrow");
        }

        return met_getOrThrow.invoke(res);
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    } else {
      return null;
    }
  }

  public static Object getNbt(Block block) {
    return null;
  }

  public static Object asNMSCopy(ItemStack item) {
    if (item == null) {
      return null;
    }

    try {
      return AS_NMS_COPY.invoke(CRAFT_ITEM_STACK, item);
    } catch (Exception e) {
      return null;
    }
  }

  public static Object asItemTemplate(ItemStack item) {
    if (item == null) {
      return null;
    }

    Object nms = asNMSCopy(item);
    Object baseItem = null;

    try {
      baseItem = GET_ITEM.invoke(nms);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

    try {
      return ITEM_TEMPLATE.newInstance(baseItem);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static Object asBukkitCopy(Object item) {
    try {
      return AS_BUKKIT_COPY.invoke(CRAFT_ITEM_STACK, item);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static Object getEntityHandle(Entity ent) {
    return null;
  }

  public static boolean isNBTSimilar(ItemStack is, ItemStack other) {
    try {
      Object firstNms = asNMSCopy(is);
      Object otherNms = asNMSCopy(other);
      return (Boolean) TAG_EQUALS.invoke(NMS_ITEM_STACK, firstNms, otherNms);
    } catch (Throwable e) {
      e.printStackTrace();
      return false;
    }
  }

  @Deprecated
  public static ItemStack HideFlag(ItemStack item, int state) {
    ItemMeta meta = item.getItemMeta();
    if (Version.isPaperBranch()) {
      try {
        Multimap<Attribute, AttributeModifier> modifiers = meta.getAttributeModifiers();
        if (modifiers == null) {
          Multimap<Attribute, AttributeModifier> emptyModifiers = HashMultimap.create();
          meta.setAttributeModifiers(emptyModifiers);
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }

    for (ItemFlag one : ItemFlag.values()) {
      if ((state & 1 << one.ordinal()) != 0) {
        meta.addItemFlags(new ItemFlag[] {one});
      }
    }

    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack modifyItemStack(ItemStack stack, String arguments) {
    return stack;
  }

  public static String toJson(ItemStack item) {
    return item == null ? null : getNbt(item).toString();
  }

  public static EntityType getEggType(ItemStack item) {
    if (!CMIMaterial.isMonsterEgg(item.getType())) {
      return null;
    }

    CMIEntityType type = CMIEntityType.get(item.getType().toString().replace("_SPAWN_EGG", ""));
    return type == null ? null : type.getType();
  }

  public static ItemStack setEggType(ItemStack item, EntityType etype) {
    if (!item.getType().toString().contains("_EGG")) {
      return null;
    }

    try {
      Object tag = getNbt(item);
      Object ttag = TAG_GET_COMPOUND.invoke(tag, "EntityTag");
      if (ttag == null) {
        ttag = COMPOUND_TAG.getDeclaredConstructor().newInstance();
      }

      CMIEntityType ce = CMIEntityType.get(etype);
      if (ce == null) {
        return item;
      }

      TAG_PUT_STRING.invoke(ttag, "id", ce.getName());
      TAG_PUT_COMPOUND.invoke(tag, "EntityTag", ttag);
      setTag(item, tag);
      return (ItemStack) asBukkitCopy(asNMSCopy(item));
    } catch (Exception e) {
      return null;
    }
  }

  public static Object newNBTTagList(int type) {
    try {
      return COMPOUND_TAGLIST.getDeclaredConstructor().newInstance();
    } catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void addToList(Object list, int size, Object data) {}

  @Override
  public CMINBT.nmbtType getType() {
    return this.type;
  }

  static {
    try {
      if (Version.isCurrentEqualOrHigher(Version.v26_1_0)) {
        ITEM_TEMPLATE =
            Class.forName("net.minecraft.world.item.ItemStackTemplate")
                .getConstructor(Class.forName("net.minecraft.world.item.Item"));
      }

      CRAFT_ITEM_STACK = Class.forName("org.bukkit.craftbukkit.inventory.CraftItemStack");
      NMS_ITEM_STACK = Class.forName("net.minecraft.world.item.ItemStack");
      DATA_COMPONENTS = Class.forName("net.minecraft.core.component.DataComponents");
      CUSTOM_DATA = Class.forName("net.minecraft.world.item.component.CustomData");
      COMPOUND_TAG = Class.forName("net.minecraft.nbt.CompoundTag");
      COMPOUND_TAGLIST = Class.forName("net.minecraft.nbt.ListTag");
      AS_NMS_COPY = CRAFT_ITEM_STACK.getMethod("asNMSCopy", ItemStack.class);
      if (Version.isCurrentEqualOrHigher(Version.v26_3_0)) {
        AS_BUKKIT_COPY = CRAFT_ITEM_STACK.getMethod("asBukkitMirror", NMS_ITEM_STACK);
      } else if (Version.isCurrentEqualOrHigher(Version.v26_2_0)) {
        AS_BUKKIT_COPY = CRAFT_ITEM_STACK.getMethod("asCraftMirror", NMS_ITEM_STACK);
      } else {
        AS_BUKKIT_COPY = CRAFT_ITEM_STACK.getMethod("asBukkitCopy", NMS_ITEM_STACK);
      }

      NMS_GET_COMPONENT =
          NMS_ITEM_STACK.getMethod(
              "get", Class.forName("net.minecraft.core.component.DataComponentType"));
      NMS_SET_COMPONENT =
          NMS_ITEM_STACK.getMethod(
              "set", Class.forName("net.minecraft.core.component.DataComponentType"), Object.class);
      CUSTOM_DATA_COMPONENT = DATA_COMPONENTS.getField("CUSTOM_DATA");
      CUSTOMDATA_OF = CUSTOM_DATA.getMethod("of", COMPOUND_TAG);
      CUSTOMDATA_COPYTAG = CUSTOM_DATA.getMethod("copyTag");
      TAG_CONTAINS = COMPOUND_TAG.getMethod("contains", String.class);
      TAG_GET = COMPOUND_TAG.getMethod("get", String.class);
      GET_ITEM = NMS_ITEM_STACK.getMethod("getItem");
      TAG_EQUALS = NMS_ITEM_STACK.getMethod("matches", NMS_ITEM_STACK, NMS_ITEM_STACK);
      TAG_GET_STRING = COMPOUND_TAG.getMethod("getString", String.class);
      TAG_GET_BOOLEAN = COMPOUND_TAG.getMethod("getBoolean", String.class);
      TAG_GET_BYTE = COMPOUND_TAG.getMethod("getByte", String.class);
      TAG_GET_SHORT = COMPOUND_TAG.getMethod("getShort", String.class);
      TAG_GET_INT = COMPOUND_TAG.getMethod("getInt", String.class);
      TAG_GET_LONG = COMPOUND_TAG.getMethod("getLong", String.class);
      TAG_GET_FLOAT = COMPOUND_TAG.getMethod("getFloat", String.class);
      TAG_GET_DOUBLE = COMPOUND_TAG.getMethod("getDouble", String.class);
      Class<?> dynamicOps = Class.forName("com.mojang.serialization.DynamicOps");
      Method met_createSerializationContext =
          getRegistry().getClass().getMethod("createSerializationContext", dynamicOps);
      CODEC = NMS_ITEM_STACK.getField("CODEC").get(null);
      Class<?> dops = Class.forName("net.minecraft.nbt.NbtOps");
      Object dops_Instance = dops.getDeclaredField("INSTANCE").get(dops);
      serializator = met_createSerializationContext.invoke(getRegistry(), dops_Instance);
      encodeMethod = CODEC.getClass().getMethod("encodeStart", dynamicOps, Object.class);
      TAG_GET_LIST = COMPOUND_TAG.getMethod("getList", String.class);
      TAG_GET_INT_ARRAY = COMPOUND_TAG.getMethod("getIntArray", String.class);
      TAG_GET_BYTE_ARRAY = COMPOUND_TAG.getMethod("getByteArray", String.class);
      TAG_GET_LONG_ARRAY = COMPOUND_TAG.getMethod("getLongArray", String.class);
      TAG_GET_COMPOUND = COMPOUND_TAG.getMethod("getCompound", String.class);
      TAG_PUT_COMPOUND =
          COMPOUND_TAG.getMethod("put", String.class, Class.forName("net.minecraft.nbt.Tag"));
      TAG_REMOVE = COMPOUND_TAG.getMethod("remove", String.class);
    } catch (Throwable e) {
      e.printStackTrace();
    }

    registry = null;
    nbtMethod = null;
    met_getOrThrow = null;
  }
}
