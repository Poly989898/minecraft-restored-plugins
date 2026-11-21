package com.nexomc.libs.gui.components.nbt;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LegacyNbt implements NbtWrapper {
   public static final String PACKAGE_NAME = Bukkit.getServer().getClass().getPackage().getName();
   public static final String NMS_VERSION;
   private static Method getStringMethod;
   private static Method setStringMethod;
   private static Method setBooleanMethod;
   private static Method hasTagMethod;
   private static Method getTagMethod;
   private static Method setTagMethod;
   private static Method removeTagMethod;
   private static Method asNMSCopyMethod;
   private static Method asBukkitCopyMethod;
   private static Constructor<?> nbtCompoundConstructor;

   public ItemStack setString(@NotNull ItemStack itemStack, String key, String value) {
      if (itemStack.getType() == Material.AIR) {
         return itemStack;
      } else {
         Object nmsItemStack = asNMSCopy(itemStack);
         Object itemCompound = hasTag(nmsItemStack) ? getTag(nmsItemStack) : newNBTTagCompound();
         setString(itemCompound, key, value);
         setTag(nmsItemStack, itemCompound);
         return asBukkitCopy(nmsItemStack);
      }
   }

   public ItemStack removeTag(@NotNull ItemStack itemStack, String key) {
      if (itemStack.getType() == Material.AIR) {
         return itemStack;
      } else {
         Object nmsItemStack = asNMSCopy(itemStack);
         Object itemCompound = hasTag(nmsItemStack) ? getTag(nmsItemStack) : newNBTTagCompound();
         remove(itemCompound, key);
         setTag(nmsItemStack, itemCompound);
         return asBukkitCopy(nmsItemStack);
      }
   }

   public ItemStack setBoolean(@NotNull ItemStack itemStack, String key, boolean value) {
      if (itemStack.getType() == Material.AIR) {
         return itemStack;
      } else {
         Object nmsItemStack = asNMSCopy(itemStack);
         Object itemCompound = hasTag(nmsItemStack) ? getTag(nmsItemStack) : newNBTTagCompound();
         setBoolean(itemCompound, key, value);
         setTag(nmsItemStack, itemCompound);
         return asBukkitCopy(nmsItemStack);
      }
   }

   public @Nullable String getString(@NotNull ItemStack itemStack, String key) {
      if (itemStack.getType() == Material.AIR) {
         return null;
      } else {
         Object nmsItemStack = asNMSCopy(itemStack);
         Object itemCompound = hasTag(nmsItemStack) ? getTag(nmsItemStack) : newNBTTagCompound();
         return getString(itemCompound, key);
      }
   }

   private static void setString(Object itemCompound, String key, String value) {
      try {
         setStringMethod.invoke(itemCompound, key, value);
      } catch (InvocationTargetException | IllegalAccessException var4) {
      }

   }

   private static void setBoolean(Object itemCompound, String key, boolean value) {
      try {
         setBooleanMethod.invoke(itemCompound, key, value);
      } catch (InvocationTargetException | IllegalAccessException var4) {
      }

   }

   private static void remove(Object itemCompound, String key) {
      try {
         removeTagMethod.invoke(itemCompound, key);
      } catch (InvocationTargetException | IllegalAccessException var3) {
      }

   }

   private static String getString(Object itemCompound, String key) {
      try {
         return (String)getStringMethod.invoke(itemCompound, key);
      } catch (InvocationTargetException | IllegalAccessException var3) {
         return null;
      }
   }

   private static boolean hasTag(Object nmsItemStack) {
      try {
         return (Boolean)hasTagMethod.invoke(nmsItemStack);
      } catch (InvocationTargetException | IllegalAccessException var2) {
         return false;
      }
   }

   public static Object getTag(Object nmsItemStack) {
      try {
         return getTagMethod.invoke(nmsItemStack);
      } catch (InvocationTargetException | IllegalAccessException var2) {
         return null;
      }
   }

   private static void setTag(Object nmsItemStack, Object itemCompound) {
      try {
         setTagMethod.invoke(nmsItemStack, itemCompound);
      } catch (InvocationTargetException | IllegalAccessException var3) {
      }

   }

   private static Object newNBTTagCompound() {
      try {
         return nbtCompoundConstructor.newInstance();
      } catch (InstantiationException | InvocationTargetException | IllegalAccessException var1) {
         return null;
      }
   }

   public static Object asNMSCopy(ItemStack itemStack) {
      try {
         return asNMSCopyMethod.invoke((Object)null, itemStack);
      } catch (InvocationTargetException | IllegalAccessException var2) {
         return null;
      }
   }

   public static ItemStack asBukkitCopy(Object nmsItemStack) {
      try {
         return (ItemStack)asBukkitCopyMethod.invoke((Object)null, nmsItemStack);
      } catch (InvocationTargetException | IllegalAccessException var2) {
         return null;
      }
   }

   private static Class<?> getNMSClass(String className) {
      try {
         return Class.forName("net.minecraft.server." + NMS_VERSION + "." + className);
      } catch (ClassNotFoundException var2) {
         return null;
      }
   }

   private static Class<?> getCraftItemStackClass() {
      try {
         return Class.forName("org.bukkit.craftbukkit." + NMS_VERSION + ".inventory.CraftItemStack");
      } catch (ClassNotFoundException var1) {
         return null;
      }
   }

   static {
      NMS_VERSION = PACKAGE_NAME.substring(PACKAGE_NAME.lastIndexOf(46) + 1);

      try {
         getStringMethod = ((Class)Objects.requireNonNull(getNMSClass("NBTTagCompound"))).getMethod("getString", String.class);
         removeTagMethod = ((Class)Objects.requireNonNull(getNMSClass("NBTTagCompound"))).getMethod("remove", String.class);
         setStringMethod = ((Class)Objects.requireNonNull(getNMSClass("NBTTagCompound"))).getMethod("setString", String.class, String.class);
         setBooleanMethod = ((Class)Objects.requireNonNull(getNMSClass("NBTTagCompound"))).getMethod("setBoolean", String.class, Boolean.TYPE);
         hasTagMethod = ((Class)Objects.requireNonNull(getNMSClass("ItemStack"))).getMethod("hasTag");
         getTagMethod = ((Class)Objects.requireNonNull(getNMSClass("ItemStack"))).getMethod("getTag");
         setTagMethod = ((Class)Objects.requireNonNull(getNMSClass("ItemStack"))).getMethod("setTag", getNMSClass("NBTTagCompound"));
         nbtCompoundConstructor = ((Class)Objects.requireNonNull(getNMSClass("NBTTagCompound"))).getDeclaredConstructor();
         asNMSCopyMethod = ((Class)Objects.requireNonNull(getCraftItemStackClass())).getMethod("asNMSCopy", ItemStack.class);
         asBukkitCopyMethod = ((Class)Objects.requireNonNull(getCraftItemStackClass())).getMethod("asBukkitCopy", getNMSClass("ItemStack"));
      } catch (NoSuchMethodException e) {
         e.printStackTrace();
      }

   }
}
