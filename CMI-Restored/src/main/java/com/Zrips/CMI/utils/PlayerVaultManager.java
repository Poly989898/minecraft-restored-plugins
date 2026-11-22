package com.Zrips.CMI.utils;

import com.Zrips.CMI.Modules.Hooks.CMIHookSubType;
import com.Zrips.CMI.Modules.Hooks.CMIHookType;
import com.drtshock.playervaults.vaultmanagement.VaultManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import org.bukkit.inventory.Inventory;

public class PlayerVaultManager {
  static Method vaultExistsMeth = null;
  static Method loadOtherVaultMeth = null;
  static Class<?> c = null;

  private static void setVaultExistsMeth() {
    if (CMIHookType.PlayerVaults.getSubType().equals(CMIHookSubType.PlayerVaults)) {
      try {
        vaultExistsMeth = c.getMethod("vaultExists", UUID.class, int.class);
      } catch (NoSuchMethodException | SecurityException value) {
        value.printStackTrace();
      }
    } else if (CMIHookType.PlayerVaults.getSubType().equals(CMIHookSubType.PlayerVaultsNBT)) {
      try {
        vaultExistsMeth = c.getMethod("vaultExists", String.class, int.class);
      } catch (NoSuchMethodException | SecurityException value2) {
        value2.printStackTrace();
      }
    }
  }

  private static void setLoadOtherVaultMeth() {
    if (CMIHookType.PlayerVaults.getSubType().equals(CMIHookSubType.PlayerVaults)) {
      try {
        loadOtherVaultMeth = c.getMethod("loadOtherVault", UUID.class, int.class, int.class);
      } catch (NoSuchMethodException | SecurityException value) {
        value.printStackTrace();
      }
    } else if (CMIHookType.PlayerVaults.getSubType().equals(CMIHookSubType.PlayerVaultsNBT)) {
      try {
        loadOtherVaultMeth = c.getMethod("loadOtherVault", String.class, int.class, int.class);
      } catch (NoSuchMethodException | SecurityException value2) {
        value2.printStackTrace();
      }
    }
  }

  private static void setClass() {
    try {
      c = Class.forName("com.drtshock.playervaults.vaultmanagement.UUIDVaultManager");
    } catch (ClassNotFoundException classNotFoundException) {
    }
  }

  public static boolean vaultExists(UUID uuid, int value) {
    if (!CMIHookType.PlayerVaults.isEnabled()) {
      return false;
    }

    if (CMIHookType.PlayerVaults.getSubType().equals(CMIHookSubType.PlayerVaultsX)) {
      return VaultManager.getInstance().vaultExists(uuid, value);
    }

    if (c == null) {
      setClass();
    }

    if (c == null) {
      return false;
    }

    if (vaultExistsMeth == null) {
      setVaultExistsMeth();
    }

    if (vaultExistsMeth == null) {
      return false;
    }

    Object object = null;
    if (CMIHookType.PlayerVaults.getSubType().equals(CMIHookSubType.PlayerVaults)) {
      try {
        object = vaultExistsMeth.invoke(c.newInstance(), uuid, value);
      } catch (IllegalAccessException
          | IllegalArgumentException
          | InvocationTargetException
          | InstantiationException value2) {
        value2.printStackTrace();
      }
    } else if (CMIHookType.PlayerVaults.getSubType().equals(CMIHookSubType.PlayerVaultsNBT)) {
      try {
        object = vaultExistsMeth.invoke(c.newInstance(), uuid.toString(), value);
      } catch (IllegalAccessException
          | IllegalArgumentException
          | InvocationTargetException
          | InstantiationException value3) {
        value3.printStackTrace();
      }
    }

    return object == null ? false : (Boolean) object;
  }

  public static Inventory loadOtherVault(UUID uuid, int value, int vaultNumber) {
    if (CMIHookType.PlayerVaults.getSubType().equals(CMIHookSubType.PlayerVaultsX)) {
      return VaultManager.getInstance().loadOtherVault(uuid, value, vaultNumber);
    }

    if (c == null) {
      setClass();
    }

    if (c == null) {
      return null;
    }

    if (loadOtherVaultMeth == null) {
      setLoadOtherVaultMeth();
    }

    if (loadOtherVaultMeth == null) {
      return null;
    }

    Object object = null;
    if (CMIHookType.PlayerVaults.getSubType().equals(CMIHookSubType.PlayerVaults)) {
      try {
        object = loadOtherVaultMeth.invoke(c.newInstance(), uuid, value, vaultNumber);
      } catch (IllegalAccessException
          | IllegalArgumentException
          | InvocationTargetException
          | InstantiationException value3) {
        value3.printStackTrace();
      }
    } else if (CMIHookType.PlayerVaults.getSubType().equals(CMIHookSubType.PlayerVaultsNBT)) {
      try {
        object = loadOtherVaultMeth.invoke(c.newInstance(), uuid.toString(), value, vaultNumber);
      } catch (IllegalAccessException
          | IllegalArgumentException
          | InvocationTargetException
          | InstantiationException value4) {
        value4.printStackTrace();
      }
    }

    return object == null ? null : (Inventory) object;
  }
}
