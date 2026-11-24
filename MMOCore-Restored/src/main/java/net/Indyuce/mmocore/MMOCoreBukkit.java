package net.Indyuce.mmocore;

import io.lumine.mythic.lib.MythicLib;
import net.Indyuce.mmocore.comp.profile.ForceClassProfileDataModule;
import net.Indyuce.mmocore.listener.BlockListener;
import net.Indyuce.mmocore.listener.GoldPouchesListener;
import net.Indyuce.mmocore.listener.GuildListener;
import net.Indyuce.mmocore.listener.LootableChestsListener;
import net.Indyuce.mmocore.listener.MMOCoreSkillTriggers;
import net.Indyuce.mmocore.listener.PlayerListener;
import net.Indyuce.mmocore.listener.WaypointsListener;
import net.Indyuce.mmocore.listener.event.PlayerPressKeyListener;
import net.Indyuce.mmocore.listener.option.DeathExperienceLoss;
import net.Indyuce.mmocore.listener.option.NoSpawnerEXP;
import net.Indyuce.mmocore.listener.option.PlayerProfileCheck;
import net.Indyuce.mmocore.listener.option.RedirectVanillaExp;
import net.Indyuce.mmocore.listener.option.VanillaExperienceOverride;
import net.Indyuce.mmocore.listener.profession.FishingListener;
import net.Indyuce.mmocore.listener.profession.PlayerCollectStats;
import org.bukkit.Bukkit;

public class MMOCoreBukkit {
   public MMOCoreBukkit(MMOCore plugin) {
      if (plugin.configManager.overrideVanillaExp) {
         Bukkit.getPluginManager().registerEvents(new VanillaExperienceOverride(), plugin);
      }

      if (plugin.getConfig().getBoolean("prevent-spawner-xp")) {
         Bukkit.getPluginManager().registerEvents(new NoSpawnerEXP(), plugin);
      }

      if (plugin.getConfig().getBoolean("death-exp-loss.enabled")) {
         Bukkit.getPluginManager().registerEvents(new DeathExperienceLoss(), plugin);
      }

      if (plugin.getConfig().getBoolean("shift-click-player-profile-check")) {
         Bukkit.getPluginManager().registerEvents(new PlayerProfileCheck(), plugin);
      }

      if (plugin.getConfig().getBoolean("vanilla-exp-redirection.enabled")) {
         Bukkit.getPluginManager().registerEvents(new RedirectVanillaExp(plugin.getConfig().getDouble("vanilla-exp-redirection.ratio")), plugin);
      }

      if (plugin.getConfig().getBoolean("force-class-selection") && MythicLib.plugin.hasProfiles()) {
         new ForceClassProfileDataModule();
      }

      Bukkit.getPluginManager().registerEvents(new WaypointsListener(), plugin);
      Bukkit.getPluginManager().registerEvents(new PlayerListener(), plugin);
      Bukkit.getPluginManager().registerEvents(new GoldPouchesListener(), plugin);
      Bukkit.getPluginManager().registerEvents(new BlockListener(), plugin);
      Bukkit.getPluginManager().registerEvents(new LootableChestsListener(), plugin);
      Bukkit.getPluginManager().registerEvents(new GuildListener(), plugin);
      Bukkit.getPluginManager().registerEvents(new FishingListener(), plugin);
      Bukkit.getPluginManager().registerEvents(new PlayerCollectStats(), plugin);
      Bukkit.getPluginManager().registerEvents(new PlayerPressKeyListener(), plugin);
      Bukkit.getPluginManager().registerEvents(new MMOCoreSkillTriggers(), plugin);
   }
}
