package com.Zrips.CMI.Containers;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class CMIPaperItemSerializator {
  static Method serializeAsBytes = null;
  static Method deserializeBytes = null;

  public static Object serializeItemStack(ItemStack itemStack) {
    if (itemStack == null) {
      return null;
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_21_R1) && Version.isPaperBranch()) {
      try {
        if (itemStack.isEmpty()) {
          return null;
        }
      } catch (Exception exception) {
      }

      try {
        if (serializeAsBytes == null) {
          serializeAsBytes = ItemStack.class.getDeclaredMethod("serializeAsBytes");
        }

        serializeAsBytes.setAccessible(true);
        return serializeAsBytes.invoke(itemStack);
      } catch (Exception exception2) {
        exception2.printStackTrace();
        return null;
      }
    } else {
      return itemStack.serialize();
    }
  }

  public static ItemStack deserializeItemStack(byte[] bs) {
    if (Version.isCurrentEqualOrHigher(Version.v1_21_R1) && Version.isPaperBranch()) {
      try {
        if (deserializeBytes == null) {
          deserializeBytes = ItemStack.class.getDeclaredMethod("deserializeBytes", byte[].class);
        }

        return (ItemStack) deserializeBytes.invoke(null, bs);
      } catch (Exception exception) {
        return null;
      }
    } else {
      return null;
    }
  }

  public static void setTo(
      YamlConfiguration yamlConfiguration, String string, ItemStack itemStack) {
    if (Version.isCurrentEqualOrHigher(Version.v1_21_R1) && Version.isPaperBranch()) {
      yamlConfiguration.set(string, serializeItemStack(itemStack));
    } else {
      yamlConfiguration.set(string, itemStack.serialize());
    }
  }

  public static ItemStack getItem(Object object) {
    ItemStack itemStack = getItemFromObject(object);
    if (itemStack != null) {
      return itemStack;
    }

    try {
      if (!(object instanceof MemorySection)) {
        return null;
      }

      LinkedHashMap<String, String> linkedHashMap =
          new LinkedHashMap(((MemorySection) object).getValues(false));
      linkedHashMap.put("==", "org.bukkit.inventory.ItemStack");
      YamlConfiguration yamlConfiguration = new YamlConfiguration();
      yamlConfiguration.set("Item", linkedHashMap);
      itemStack = getItem(yamlConfiguration.get("Item"));
      if (itemStack != null) {
        CMIMessages.consoleMessage("Success!");
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

    return itemStack;
  }

  public static ItemStack getItemFromObject(Object object) {
    if (object instanceof ItemStack) {
      try {
        return (ItemStack) object;
      } catch (Throwable throwable) {
      }
    }

    if (object instanceof MemorySection) {
      try {
        var map = ((MemorySection) object).getValues(true);
        if (map == null) {
          return null;
        }

        return ItemStack.deserialize(map);
      } catch (Throwable throwable2) {
      }
    }

    if (object instanceof Map) {
      try {
        return ItemStack.deserialize((Map) object);
      } catch (Throwable throwable3) {
      }
    }

    if (object instanceof byte[]) {
      ItemStack itemStack = deserializeItemStack((byte[]) object);
      if (itemStack == null && !Version.isTestServer()) {
        CMIMessages.consoleMessage(
            "&cFailed to deserialize itemstack from byte array! Make sure to run Paper type server"
                + " or update item records");
      }

      return itemStack;
    } else {
      CMIMessages.consoleMessage("Failed to read itemStack entry, trying alternative approach");
      return null;
    }
  }
}
