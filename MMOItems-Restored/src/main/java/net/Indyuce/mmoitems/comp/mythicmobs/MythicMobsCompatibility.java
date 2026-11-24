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
         for (String var2 : this.getFactions()) {
            MMOItems.plugin.getStats().register(new FactionDamage(var2));
         }
      } catch (Exception var3) {
         MMOItems.plugin.getLogger().log(Level.WARNING, "Exception while enabling MythicMobs compatibility: " + var3.getMessage());
      }

      Bukkit.getPluginManager().registerEvents(this, MMOItems.plugin);
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void b(MythicMechanicLoadEvent var1) {
      switch (var1.getMechanicName().toLowerCase()) {
         case "mmoitemsvolley":
            var1.register(
               new MMOItemsArrowVolleyMechanic(var1.getContainer().getManager(), var1.getContainer().getFile(), var1.getConfig().getLine(), var1.getConfig())
            );
            break;
         case "onmmoitemuse":
            var1.register(new MMOItemsOnUseAura(var1.getContainer().getManager(), var1.getContainer().getFile(), var1.getConfig().getLine(), var1.getConfig()));
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void a(MythicReloadedEvent var1) {
      MMOItems.plugin.getSkills().initialize(true);

      for (Player var3 : Bukkit.getOnlinePlayers()) {
         PlayerData var4 = PlayerData.get(var3);
         var4.updateInventory();
      }
   }

   private Set<String> getFactions() {
      HashSet var1 = new HashSet();
      ArrayList<MythicMob> var2 = new ArrayList(MythicBukkit.inst().getMobManager().getVanillaTypes());
      var2.addAll(MythicBukkit.inst().getMobManager().getMobTypes());

      for (MythicMob var4 : var2) {
         if (var4.hasFaction()) {
            var1.add(var4.getFaction());
         }
      }

      return var1;
   }
}
