package io.lumine.mythic.lib.comp.mythicmobs;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicReloadedEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.comp.mythicmobs.condition.CanTargetCondition;
import io.lumine.mythic.lib.comp.mythicmobs.condition.HasDamageTypeCondition;
import io.lumine.mythic.lib.comp.mythicmobs.condition.IsMMODamageCondition;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MythicMobsHook implements Listener {
   public MythicMobsHook() {
      MythicBukkit.inst().getCompatibility().setupMMOBridge(new MythicLibSupportImpl());
   }

   @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
   public void a(PlayerAttackEvent event) {
      String faction = this.getFaction(event.getEntity());
      if (faction != null) {
         event.getDamage().additiveModifier(event.getAttacker().getData().getStatMap().getStat("FACTION_DAMAGE_" + faction.toUpperCase()) / 100.0);
      }
   }

   @EventHandler(priority = EventPriority.NORMAL)
   public void b(MythicReloadedEvent event) {
      MythicLib.plugin.getSkills().initialize(true);
   }

   @EventHandler
   public void c(MythicConditionLoadEvent event) {
      String conditionName = event.getConditionName().toLowerCase();
      int s = event.getConditionName().indexOf(" ");
      if (s > 0) {
         conditionName = conditionName.substring(0, s);
      }

      switch (conditionName) {
         case "mmodamagetype":
            event.register(new HasDamageTypeCondition(event.getConfig()));
            break;
         case "ismmodamage":
            event.register(new IsMMODamageCondition(event.getConfig()));
            break;
         case "mmocantarget":
            event.register(new CanTargetCondition(event.getConfig().getLine(), event.getConfig()));
      }
   }

   private String getFaction(Entity entity) {
      ActiveMob mob = MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);
      return mob != null && mob.hasFaction() ? mob.getFaction() : null;
   }
}
