package com.nexomc.nexo.utils;

import kotlin.Metadata;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u001b\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u001f\u001a\u00020\u00052\u0006\u0010 \u001a\u00020!J\b\u0010\"\u001a\u00020#H\u0016R\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0004\n\u0002\u0010\u0006R\u0012\u0010\u0007\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0004\n\u0002\u0010\u0006R\u0012\u0010\b\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0004\n\u0002\u0010\u0006R\u0012\u0010\t\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0004\n\u0002\u0010\u0006R\u0012\u0010\n\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0004\n\u0002\u0010\u0006R\u0012\u0010\u000b\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0004\n\u0002\u0010\u0006R\u0012\u0010\f\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0004\n\u0002\u0010\u0006R\u0012\u0010\r\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0004\n\u0002\u0010\u0006R\u0012\u0010\u000e\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0004\n\u0002\u0010\u0006R\u0012\u0010\u000f\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0004\n\u0002\u0010\u0006R\u0012\u0010\u0010\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0004\n\u0002\u0010\u0006R\u0012\u0010\u0011\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0004\n\u0002\u0010\u0006R\u0011\u0010\u0012\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0014\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u0014\u0010\u0013R\u0011\u0010\u0015\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u0015\u0010\u0013R\u0011\u0010\u0016\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u0016\u0010\u0013R\u0011\u0010\u0017\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u0017\u0010\u0013R\u0011\u0010\u0018\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u0018\u0010\u0013R\u0011\u0010\u0019\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u0019\u0010\u0013R\u0011\u0010\u001a\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u001a\u0010\u0013R\u0011\u0010\u001b\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u001b\u0010\u0013R\u0011\u0010\u001c\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u001c\u0010\u0013R\u0011\u0010\u001d\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u001d\u0010\u0013R\u0011\u0010\u001e\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u001e\u0010\u0013¨\u0006$"},
   d2 = {"Lcom/nexomc/nexo/utils/PluginUtils;", "Lcom/nexomc/nexo/utils/Reloadable;", "<init>", "()V", "_isModelEngineEnabled", "", "Ljava/lang/Boolean;", "_isViaVersionEnabled", "_isViaBackwardsEnabled", "_isMythicHUDEnabled", "_isBetterHUDEnabled", "_isTABEnabled", "_isMMOItemsEnabled", "_isMythicMobsEnabled", "_isMythicCrucibleEnabled", "_isVulcanEnabled", "_isSpartanEnabled", "_isVacanEnabled", "isModelEngineEnabled", "()Z", "isViaVersionEnabled", "isViaBackwardsEnabled", "isMythicHUDEnabled", "isBetterHUDEnabled", "isTABEnabled", "isMMOItemsEnabled", "isMythicMobsEnabled", "isMythicCrucibleEnabled", "isVulcanEnabled", "isSpartanEnabled", "isVacanEnabled", "isEnabled", "pluginName", "", "reload", "", "com.nexomc:core"}
)
public final class PluginUtils implements Reloadable {
   @NotNull
   public static final PluginUtils INSTANCE = new PluginUtils();
   @Nullable
   private static Boolean _isModelEngineEnabled;
   @Nullable
   private static Boolean _isViaVersionEnabled;
   @Nullable
   private static Boolean _isViaBackwardsEnabled;
   @Nullable
   private static Boolean _isMythicHUDEnabled;
   @Nullable
   private static Boolean _isBetterHUDEnabled;
   @Nullable
   private static Boolean _isTABEnabled;
   @Nullable
   private static Boolean _isMMOItemsEnabled;
   @Nullable
   private static Boolean _isMythicMobsEnabled;
   @Nullable
   private static Boolean _isMythicCrucibleEnabled;
   @Nullable
   private static Boolean _isVulcanEnabled;
   @Nullable
   private static Boolean _isSpartanEnabled;
   @Nullable
   private static Boolean _isVacanEnabled;

   private PluginUtils() {
   }

   public final boolean isModelEngineEnabled() {
      Boolean flag = _isModelEngineEnabled;
      boolean flag2;
      if (flag != null) {
         flag2 = flag;
      } else {
         boolean it = this.isEnabled("ModelEngine");
         int value = 0;
         _isModelEngineEnabled = it;
         flag2 = it;
      }

      return flag2;
   }

   public final boolean isViaVersionEnabled() {
      Boolean flag = _isViaVersionEnabled;
      boolean flag2;
      if (flag != null) {
         flag2 = flag;
      } else {
         boolean it = this.isEnabled("ViaVersion");
         int value = 0;
         _isViaVersionEnabled = it;
         flag2 = it;
      }

      return flag2;
   }

   public final boolean isViaBackwardsEnabled() {
      Boolean flag = _isViaBackwardsEnabled;
      boolean flag2;
      if (flag != null) {
         flag2 = flag;
      } else {
         boolean it = this.isEnabled("ViaBackwards");
         int value = 0;
         _isViaBackwardsEnabled = it;
         flag2 = it;
      }

      return flag2;
   }

   public final boolean isMythicHUDEnabled() {
      Boolean flag = _isMythicHUDEnabled;
      boolean flag2;
      if (flag != null) {
         flag2 = flag;
      } else {
         boolean it = this.isEnabled("MythicHUD");
         int value = 0;
         _isMythicHUDEnabled = it;
         flag2 = it;
      }

      return flag2;
   }

   public final boolean isBetterHUDEnabled() {
      Boolean flag = _isBetterHUDEnabled;
      boolean flag2;
      if (flag != null) {
         flag2 = flag;
      } else {
         boolean it = this.isEnabled("BetterHUD");
         int value = 0;
         _isBetterHUDEnabled = it;
         flag2 = it;
      }

      return flag2;
   }

   public final boolean isTABEnabled() {
      Boolean flag = _isTABEnabled;
      boolean flag2;
      if (flag != null) {
         flag2 = flag;
      } else {
         boolean it = this.isEnabled("TAB") || this.isEnabled("TAB-Bridge");
         int value = 0;
         _isTABEnabled = it;
         flag2 = it;
      }

      return flag2;
   }

   public final boolean isMMOItemsEnabled() {
      Boolean flag = _isMMOItemsEnabled;
      boolean flag2;
      if (flag != null) {
         flag2 = flag;
      } else {
         boolean it = this.isEnabled("MMOItems");
         int value = 0;
         _isMMOItemsEnabled = it;
         flag2 = it;
      }

      return flag2;
   }

   public final boolean isMythicMobsEnabled() {
      Boolean flag = _isMythicMobsEnabled;
      boolean flag2;
      if (flag != null) {
         flag2 = flag;
      } else {
         boolean it = this.isEnabled("MythicMobs");
         int value = 0;
         _isMythicMobsEnabled = it;
         flag2 = it;
      }

      return flag2;
   }

   public final boolean isMythicCrucibleEnabled() {
      Boolean flag = _isMythicCrucibleEnabled;
      boolean flag2;
      if (flag != null) {
         flag2 = flag;
      } else {
         boolean it = this.isEnabled("MythicCrucible");
         int value = 0;
         _isMythicCrucibleEnabled = it;
         flag2 = it;
      }

      return flag2;
   }

   public final boolean isVulcanEnabled() {
      Boolean flag = _isVulcanEnabled;
      boolean flag2;
      if (flag != null) {
         flag2 = flag;
      } else {
         boolean it = this.isEnabled("Vulcan");
         int value = 0;
         _isVulcanEnabled = it;
         flag2 = it;
      }

      return flag2;
   }

   public final boolean isSpartanEnabled() {
      Boolean flag = _isSpartanEnabled;
      boolean flag2;
      if (flag != null) {
         flag2 = flag;
      } else {
         boolean it = this.isEnabled("Spartan");
         int value = 0;
         _isSpartanEnabled = it;
         flag2 = it;
      }

      return flag2;
   }

   public final boolean isVacanEnabled() {
      Boolean flag = _isVacanEnabled;
      boolean flag2;
      if (flag != null) {
         flag2 = flag;
      } else {
         boolean it = this.isEnabled("Vacan");
         int value = 0;
         _isVacanEnabled = it;
         flag2 = it;
      }

      return flag2;
   }

   public final boolean isEnabled(@NotNull String pluginName) {
      return Bukkit.getPluginManager().isPluginEnabled(pluginName);
   }

   public void reload() {
      _isModelEngineEnabled = null;
      _isViaVersionEnabled = null;
      _isViaBackwardsEnabled = null;
      _isMythicHUDEnabled = null;
      _isBetterHUDEnabled = null;
      _isTABEnabled = null;
      _isMMOItemsEnabled = null;
      _isMythicMobsEnabled = null;
      _isMythicCrucibleEnabled = null;
      _isVulcanEnabled = null;
      _isSpartanEnabled = null;
      _isVacanEnabled = null;
   }
}
