package io.lumine.mythic.bukkit.compatibility;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;

public class ShopGUIPlusSupport {
   public ShopGUIPlusSupport(MythicBukkit plugin) {
      try {
         Class.forName("net.brcdev.shopgui.provider.item.MythicMobsItemsItemProvider");
      } catch (Throwable e) {
         MythicLogger.log("Found ShopGUIPlus but version not supported");
      }
   }
}
