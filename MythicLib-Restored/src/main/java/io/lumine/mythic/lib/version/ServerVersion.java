package io.lumine.mythic.lib.version;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.version.wrapper.VersionWrapper;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class ServerVersion {
   private final String craftBukkitVersion;
   private final int revNumber;
   private final int[] bukkitVersion;
   private final VersionWrapper versionWrapper;
   private final boolean paper;
   private static final int MAXIMUM_INDEX = 3;
   private static final int MAXIMUM_REVISION_NUMBER = 10;
   private static final String CLASS_NAME_USED = "CraftServer";

   @Deprecated
   public ServerVersion(Class<?> ignored) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
      this();
   }

   public ServerVersion() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
      String[] bukkitSplit = Bukkit.getServer().getBukkitVersion().split("\\-")[0].split("\\.");
      // Paper 26.x reports "26.3.build.253-stable": only the leading numeric parts are the version.
      int numeric = 0;
      while (numeric < Math.min(3, bukkitSplit.length) && bukkitSplit[numeric].matches("\\d+")) {
         numeric++;
      }

      this.bukkitVersion = new int[numeric];

      for (int i = 0; i < this.bukkitVersion.length; i++) {
         this.bukkitVersion[i] = Integer.parseInt(bukkitSplit[i]);
      }

      this.revNumber = this.findRevisionNumber();
      this.craftBukkitVersion = this.craftBukkitVersion(this.revNumber);

      VersionWrapper found;
      try {
         found = (VersionWrapper)Class.forName("io.lumine.mythic.lib.version.wrapper.VersionWrapper_" + this.wrapperSuffix())
            .getDeclaredConstructor()
            .newInstance();
      } catch (Exception exception) {
         throw new IllegalStateException(
            "Unsupported server version " + Bukkit.getServer().getBukkitVersion() + ": this MythicLib build only supports Paper 26.3 (no wrapper for '"
               + this.wrapperSuffix() + "')",
            exception
         );
      }

      this.versionWrapper = found;
      boolean isPaper = false;

      try {
         Class.forName("com.destroystokyo.paper.ParticleBuilder");
         isPaper = true;
      } catch (ClassNotFoundException var6) {
      }

      this.paper = isPaper;

      try {
         VEnchantment.values();
         VEntityType.values();
         VMaterial.values();
         VParticle.values();
         VPotionEffectType.values();
         VSound.values();
      } catch (Throwable throwable) {
         throw new RuntimeException("Compatibility error: " + throwable.getMessage());
      }
   }

   @NotNull
   private String craftBukkitVersion(int revNumber) {
      return "v" + this.bukkitVersion[0] + "_" + this.bukkitVersion[1] + "_R" + revNumber;
   }

   /** Paper 26.x has no versioned CraftBukkit package: the wrapper is named after the Minecraft version (26_3). */
   private String wrapperSuffix() {
      if (this.bukkitVersion[0] >= 26) {
         return this.bukkitVersion[0] + "_" + (this.bukkitVersion.length > 1 ? this.bukkitVersion[1] : 0);
      }

      return this.craftBukkitVersion.substring(1);
   }

   private int findRevisionNumber() {
      try {
         Class<?> bukkitServerClass = Bukkit.getServer().getClass();
         String rev = bukkitServerClass.getPackage().getName().replace(".", ",").split(",")[3];
         return Integer.parseInt(rev.split("_")[2].replaceAll("[^0-9]", ""));
      } catch (Throwable var4) {
         for (int revNumber = 1; revNumber < 10; revNumber++) {
            try {
               String candidate = this.craftBukkitVersion(revNumber);
               Class.forName("org.bukkit.craftbukkit." + candidate + "." + "CraftServer");
               return revNumber;
            } catch (Throwable var3) {
            }
         }

         return 0;
      }
   }

   public boolean isPaper() {
      return this.paper;
   }

   public boolean isAbove(int... version) {
      Validate.isTrue(version.length >= 1 && version.length <= 3, "Provide at least 1 integer and at most 3");
      int maxLength = Math.min(3, Math.max(version.length, this.bukkitVersion.length));

      for (int i = 0; i < maxLength; i++) {
         int server = i >= this.bukkitVersion.length ? 0 : this.bukkitVersion[i];
         int provided = i >= version.length ? 0 : version[i];
         if (server != provided) {
            return server > provided;
         }
      }

      return true;
   }

   public boolean isUnder(int... version) {
      return !this.isAbove(version);
   }

   @NotNull
   public String getCraftBukkitVersion() {
      return this.craftBukkitVersion;
   }

   public int getRevisionNumber() {
      return this.revNumber;
   }

   public int[] getBukkitVersion() {
      return this.bukkitVersion;
   }

   @NotNull
   public VersionWrapper getWrapper() {
      return this.versionWrapper;
   }

   @Override
   public String toString() {
      return "ServerVersion{revision='"
         + this.craftBukkitVersion
         + '\''
         + ", revisionNumber="
         + this.revNumber
         + ", integers="
         + Arrays.toString(this.bukkitVersion)
         + ", paper="
         + this.paper
         + '}';
   }

   @Deprecated
   public String getRevision() {
      return this.getCraftBukkitVersion();
   }

   @Deprecated
   public int[] toNumbers() {
      return this.bukkitVersion;
   }

   @Deprecated
   public int[] getIntegers() {
      return this.getBukkitVersion();
   }

   @Deprecated
   public boolean isStrictlyHigher(int... version) {
      Validate.isTrue(version.length >= 1 && version.length <= 3, "Provide at least 1 integer and at most 3");
      int maxLength = Math.min(3, Math.max(version.length, this.bukkitVersion.length));

      for (int i = 0; i < maxLength; i++) {
         int server = i >= this.bukkitVersion.length ? 0 : this.bukkitVersion[i];
         int provided = i >= version.length ? 0 : version[i];
         if (server != provided) {
            return server > provided;
         }
      }

      return false;
   }

   @Deprecated
   public boolean isBelowOrEqual(int... version) {
      return !this.isStrictlyHigher(version);
   }
}
