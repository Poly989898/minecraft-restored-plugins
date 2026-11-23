package com.nexomc.protectionlib;

import com.bekvon.bukkit.residence.Residence;
import com.massivecraft.factions.FactionsPlugin;
import com.nexomc.protectionlib.compatibilities.BentoBoxCompat;
import com.nexomc.protectionlib.compatibilities.CrashClaimCompat;
import com.nexomc.protectionlib.compatibilities.FactionsUuidCompat;
import com.nexomc.protectionlib.compatibilities.GriefPreventionCompat;
import com.nexomc.protectionlib.compatibilities.HuskClaimCompat;
import com.nexomc.protectionlib.compatibilities.HuskTownsCompat;
import com.nexomc.protectionlib.compatibilities.LandsCompat;
import com.nexomc.protectionlib.compatibilities.NoBuildPlusCompat;
import com.nexomc.protectionlib.compatibilities.PlotSquaredCompat;
import com.nexomc.protectionlib.compatibilities.ResidenceCompat;
import com.nexomc.protectionlib.compatibilities.TownyCompat;
import com.nexomc.protectionlib.compatibilities.WorldGuardCompat;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import java.util.function.Predicate;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.crashcraft.crashclaim.CrashClaim;
import net.william278.huskclaims.BukkitHuskClaims;
import net.william278.husktowns.BukkitHuskTowns;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import p1xel.nobuildplus.NoBuildPlus;
import world.bentobox.bentobox.BentoBox;

/**
 * Hooks into third-party protection plugins and answers build/break/interact/use checks
 * by asking every loaded compatibility layer. Rewritten by hand from a decompiled Kotlin
 * original; behaviour is unchanged.
 */
public final class ProtectionLib {
   public static final ProtectionLib INSTANCE = new ProtectionLib();
   private static final Set<ProtectionCompatibility<? extends Plugin>> compatibilities = new ObjectOpenHashSet<>();
   public static boolean debug;

   private ProtectionLib() {
   }

   public static void init(@NotNull JavaPlugin plugin) {
      handleCompatibility("WorldGuard", plugin, "com.sk89q.worldguard.bukkit.WorldGuardPlugin", "WorldGuardCompat");
      handleCompatibility("Towny", plugin, "org.bukkit.plugin.Plugin", "TownyCompat");
      handleCompatibility("Factions", plugin, "com.massivecraft.factions.FactionsPlugin", "FactionsUuidCompat");
      handleCompatibility("Lands", plugin, "org.bukkit.plugin.Plugin", "LandsCompat");
      handleCompatibility("PlotSquared", plugin, "org.bukkit.plugin.Plugin", "PlotSquaredCompat");
      handleCompatibility("CrashClaim", plugin, "net.crashcraft.crashclaim.CrashClaim", "CrashClaimCompat");
      handleCompatibility("GriefPrevention", plugin, "me.ryanhamshire.GriefPrevention.GriefPrevention", "GriefPreventionCompat");
      handleCompatibility("HuskClaims", plugin, "net.william278.huskclaims.BukkitHuskClaims", "HuskClaimCompat");
      handleCompatibility("BentoBox", plugin, "world.bentobox.bentobox.BentoBox", "BentoBoxCompat");
      handleCompatibility("HuskTowns", plugin, "net.william278.husktowns.BukkitHuskTowns", "HuskTownsCompat");
      handleCompatibility("Residence", plugin, "com.bekvon.bukkit.residence.Residence", "ResidenceCompat");
      handleCompatibility("NoBuildPlus", plugin, "p1xel.nobuildplus.NoBuildPlus", "NoBuildPlusCompat");
   }

   public static boolean canBuild(@NotNull Player player, @NotNull Location target) {
      return allCompatibilities(compat -> compat.canBuild(player, target));
   }

   public static boolean canBreak(@NotNull Player player, @NotNull Location target) {
      return allCompatibilities(compat -> compat.canBreak(player, target));
   }

   public static boolean canInteract(@NotNull Player player, @NotNull Location target) {
      return allCompatibilities(compat -> compat.canInteract(player, target));
   }

   public static boolean canUse(@NotNull Player player, @NotNull Location target) {
      return allCompatibilities(compat -> compat.canUse(player, target));
   }

   /** Registers the compatibility layer for {@code pluginName} if that plugin is loaded and of the expected type. */
   /**
    * Registers one compatibility layer. Everything is resolved by name, inside the try: when the
    * third-party plugin (and so its API classes) is absent, the layer is skipped and nothing else fails.
    */
   private static void handleCompatibility(@NotNull String pluginName, @NotNull JavaPlugin mainPlugin, @NotNull String typeName, @NotNull String compatName) {
      try {
         Class<?> type = Class.forName(typeName);
         Plugin found = Bukkit.getPluginManager().getPlugin(pluginName);
         if (!type.isInstance(found)) return;
         if (pluginName.equals("Factions") && !checkFactionsCompat()) return;

         Class<?> compatClass = Class.forName("com.nexomc.protectionlib.compatibilities." + compatName);
         java.lang.reflect.Constructor<?> constructor = compatClass.getConstructor(JavaPlugin.class, type);
         @SuppressWarnings("unchecked")
         ProtectionCompatibility<? extends Plugin> compatibility = (ProtectionCompatibility<? extends Plugin>) constructor.newInstance(mainPlugin, found);
         if (compatibility != null) compatibilities.add(compatibility);
      } catch (Throwable throwable) {
         if (debug) throwable.printStackTrace();
      }
   }
   /** True only if every registered compatibility allows the action. A failing check counts as allowed. */
   private static boolean allCompatibilities(@NotNull Predicate<ProtectionCompatibility<? extends Plugin>> check) {
      try {
         for (ProtectionCompatibility<? extends Plugin> compatibility : compatibilities) {
            if (!check.test(compatibility)) return false;
         }
         return true;
      } catch (Throwable throwable) {
         if (debug) throwable.printStackTrace();
         return true;
      }
   }

   private static boolean checkFactionsCompat() {
      try {
         Class.forName("com.massivecraft.factions.perms.PermissibleActions");
         return true;
      } catch (Throwable throwable) {
         Bukkit.getLogger().warning("It seems a Factions plugin is installed, but it is not FactionsUUID.");
         Bukkit.getLogger().warning("ProtectionLib will not be able to handle Factions protection.");
         return false;
      }
   }

   private interface CompatibilityConstructor<T extends Plugin> {
      @Nullable
      ProtectionCompatibility<T> create(@NotNull JavaPlugin mainPlugin, @NotNull T plugin);
   }
}
