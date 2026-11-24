package io.lumine.mythic.lib.comp.flags;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import io.lumine.mythic.lib.MythicLib;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorldGuardFlags implements FlagPlugin {
   private final WorldGuard worldguard;
   private final WorldGuardPlugin worldguardPlugin;
   private final Map<CustomFlag, StateFlag> flags = new HashMap<>();

   public WorldGuardFlags() {
      this.worldguard = WorldGuard.getInstance();
      this.worldguardPlugin = (WorldGuardPlugin)Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
      FlagRegistry registry = this.worldguard.getFlagRegistry();

      for (CustomFlag customFlag : CustomFlag.values()) {
         try {
            StateFlag flag = new StateFlag(customFlag.getPath(), customFlag.getDefault());
            registry.register(flag);
            this.flags.put(customFlag, flag);
         } catch (Exception exception) {
            MythicLib.plugin.getLogger().log(Level.WARNING, "Could not register flag '" + customFlag.getPath() + "':");
            exception.printStackTrace();
         }
      }
   }

   @Nullable
   public StateFlag toWorldGuard(CustomFlag flag) {
      return this.flags.get(flag);
   }

   @Override
   public boolean isPvpAllowed(Location loc) {
      return this.getApplicableRegion(loc).queryState(null, new StateFlag[]{Flags.PVP}) != State.DENY;
   }

   @Override
   public boolean isFlagAllowed(Location loc, CustomFlag customFlag) {
      State state = (State)this.getApplicableRegion(loc).queryValue(null, (Flag)this.flags.get(customFlag));
      return state == null ? customFlag.getDefault() : state == State.ALLOW;
   }

   @Override
   public boolean isFlagAllowed(Player player, CustomFlag customFlag) {
      State state = (State)this.getApplicableRegion(player.getLocation())
         .queryValue(this.worldguardPlugin.wrapPlayer(player), (Flag)this.flags.get(customFlag));
      return state == null ? customFlag.getDefault() : state == State.ALLOW;
   }

   @NotNull
   private ApplicableRegionSet getApplicableRegion(Location loc) {
      return this.worldguard.getPlatform().getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt(loc));
   }
}
