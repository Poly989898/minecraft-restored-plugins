package net.Indyuce.mmoitems;

import io.lumine.mythic.lib.MythicLib;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.comp.PhatLootsHook;
import net.Indyuce.mmoitems.gui.listener.GuiListener;
import net.Indyuce.mmoitems.listener.BiomeChangeListener;
import net.Indyuce.mmoitems.listener.CustomBlockListener;
import net.Indyuce.mmoitems.listener.CustomSoundListener;
import net.Indyuce.mmoitems.listener.DisableInteractions;
import net.Indyuce.mmoitems.listener.DisabledItemsListener;
import net.Indyuce.mmoitems.listener.DurabilityListener;
import net.Indyuce.mmoitems.listener.ItemListener;
import net.Indyuce.mmoitems.listener.ItemUse;
import net.Indyuce.mmoitems.listener.PlayerListener;
import net.Indyuce.mmoitems.listener.ProfileSupportListener;
import net.Indyuce.mmoitems.listener.option.DroppedItems;
import net.Indyuce.mmoitems.listener.option.SoulboundNoDrop;
import org.bukkit.Bukkit;

public class MMOItemsBukkit {
   public MMOItemsBukkit(MMOItems var1) {
      Bukkit.getPluginManager().registerEvents(new ItemUse(), var1);
      Bukkit.getPluginManager().registerEvents(new ItemListener(), var1);
      Bukkit.getPluginManager().registerEvents(new PlayerListener(), var1);
      Bukkit.getPluginManager().registerEvents(new CustomSoundListener(), var1);
      Bukkit.getPluginManager().registerEvents(new DurabilityListener(), var1);
      Bukkit.getPluginManager().registerEvents(new DisableInteractions(), var1);
      Bukkit.getPluginManager().registerEvents(new GuiListener(), var1);
      Bukkit.getPluginManager().registerEvents(new BiomeChangeListener(), var1);
      Bukkit.getPluginManager().registerEvents(new CustomBlockListener(), var1);
      if (Bukkit.getPluginManager().getPlugin("PhatLoots") != null) {
         Bukkit.getPluginManager().registerEvents(new PhatLootsHook(), var1);
      }

      if (var1.getConfig().getBoolean("dropped-items.tier-glow") || var1.getConfig().getBoolean("dropped-items.hints")) {
         Bukkit.getPluginManager().registerEvents(new DroppedItems(var1.getConfig().getConfigurationSection("dropped-items")), var1);
      }

      if (var1.getLanguage().disableRemovedItems) {
         Bukkit.getPluginManager().registerEvents(new DisabledItemsListener(var1), var1);
      }

      if (!var1.getConfig().getBoolean("soulbound.can-drop")) {
         Bukkit.getPluginManager().registerEvents(new SoulboundNoDrop(), var1);
      }

      if (MythicLib.plugin.hasProfiles()) {
         Bukkit.getPluginManager().registerEvents(new ProfileSupportListener(), var1);
      }

      Bukkit.getScheduler().runTaskTimer(var1, () -> Bukkit.getOnlinePlayers().forEach(var0 -> PlayerData.get(var0).timedRunnable()), 100L, 20L);
   }
}
