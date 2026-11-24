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
   public MMOItemsBukkit(MMOItems mmoItems) {
      Bukkit.getPluginManager().registerEvents(new ItemUse(), mmoItems);
      Bukkit.getPluginManager().registerEvents(new ItemListener(), mmoItems);
      Bukkit.getPluginManager().registerEvents(new PlayerListener(), mmoItems);
      Bukkit.getPluginManager().registerEvents(new CustomSoundListener(), mmoItems);
      Bukkit.getPluginManager().registerEvents(new DurabilityListener(), mmoItems);
      Bukkit.getPluginManager().registerEvents(new DisableInteractions(), mmoItems);
      Bukkit.getPluginManager().registerEvents(new GuiListener(), mmoItems);
      Bukkit.getPluginManager().registerEvents(new BiomeChangeListener(), mmoItems);
      Bukkit.getPluginManager().registerEvents(new CustomBlockListener(), mmoItems);
      if (Bukkit.getPluginManager().getPlugin("PhatLoots") != null) {
         Bukkit.getPluginManager().registerEvents(new PhatLootsHook(), mmoItems);
      }

      if (mmoItems.getConfig().getBoolean("dropped-items.tier-glow") || mmoItems.getConfig().getBoolean("dropped-items.hints")) {
         Bukkit.getPluginManager().registerEvents(new DroppedItems(mmoItems.getConfig().getConfigurationSection("dropped-items")), mmoItems);
      }

      if (mmoItems.getLanguage().disableRemovedItems) {
         Bukkit.getPluginManager().registerEvents(new DisabledItemsListener(mmoItems), mmoItems);
      }

      if (!mmoItems.getConfig().getBoolean("soulbound.can-drop")) {
         Bukkit.getPluginManager().registerEvents(new SoulboundNoDrop(), mmoItems);
      }

      if (MythicLib.plugin.hasProfiles()) {
         Bukkit.getPluginManager().registerEvents(new ProfileSupportListener(), mmoItems);
      }

      Bukkit.getScheduler().runTaskTimer(mmoItems, () -> Bukkit.getOnlinePlayers().forEach(value -> PlayerData.get(value).timedRunnable()), 100L, 20L);
   }
}
