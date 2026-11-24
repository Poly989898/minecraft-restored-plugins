package net.Indyuce.mmoitems.comp.mythicmobs;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicReloadedEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.comp.mythicmobs.mechanics.MMOItemsArrowVolleyMechanic;
import net.Indyuce.mmoitems.comp.mythicmobs.mechanics.MMOItemsOnUseAura;
import net.Indyuce.mmoitems.comp.mythicmobs.stat.FactionDamage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MythicMobsCompatibility implements Listener {
   public MythicMobsCompatibility() {
      try {
         for (String faction : this.getFactions()) {
            MMOItems.plugin.getStats().register(new FactionDamage(faction));
         }
      } catch (Exception exception) {
         MMOItems.plugin.getLogger().log(Level.WARNING, "Exception while enabling MythicMobs compatibility: " + exception.getMessage());
      }

      Bukkit.getPluginManager().registerEvents(this, MMOItems.plugin);
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void b(MythicMechanicLoadEvent event) {
      switch (event.getMechanicName().toLowerCase()) {
         case "mmoitemsvolley":
            event.register(
               new MMOItemsArrowVolleyMechanic(event.getContainer().getManager(), event.getContainer().getFile(), event.getConfig().getLine(), event.getConfig())
            );
            break;
         case "onmmoitemuse":
            event.register(new MMOItemsOnUseAura(event.getContainer().getManager(), event.getContainer().getFile(), event.getConfig().getLine(), event.getConfig()));
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void a(MythicReloadedEvent event) {
      MMOItems.plugin.getSkills().initialize(true);

      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
         PlayerData playerData = PlayerData.get(onlinePlayer);
         playerData.updateInventory();
      }
   }

   private Set<String> getFactions() {
      HashSet set = new HashSet();
      ArrayList<MythicMob> mythicMobs = new ArrayList(MythicBukkit.inst().getMobManager().getVanillaTypes());
      mythicMobs.addAll(MythicBukkit.inst().getMobManager().getMobTypes());

      for (MythicMob mythicMob : mythicMobs) {
         if (mythicMob.hasFaction()) {
            set.add(mythicMob.getFaction());
         }
      }

      return set;
   }
}
