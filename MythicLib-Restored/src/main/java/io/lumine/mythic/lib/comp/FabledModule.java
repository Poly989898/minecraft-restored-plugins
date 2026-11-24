package io.lumine.mythic.lib.comp;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.event.fake.FakeEventCaller;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import studio.magemonkey.fabled.api.DefaultCombatProtection;
import studio.magemonkey.fabled.api.event.SkillDamageEvent;

public class FabledModule implements Listener, FakeEventCaller<EntityDamageEvent> {
   public FabledModule() {
      MythicLib.plugin.getFakeEvents().registerFakeEventCaller(EntityDamageEvent.class, this);
      Bukkit.getPluginManager().registerEvents(this, MythicLib.plugin);
   }

   public boolean isFake(EntityDamageEvent event) {
      return event instanceof EntityDamageByEntityEvent && DefaultCombatProtection.isFakeDamageEvent((EntityDamageByEntityEvent)event);
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void a(SkillDamageEvent event) {
      StatProvider damager = StatProvider.get(event.getDamager(), EquipmentSlot.MAIN_HAND, true);
      DamageMetadata damageMeta = new DamageMetadata(event.getDamage(), DamageType.SKILL, DamageType.MAGIC);
      AttackMetadata attackMeta = new AttackMetadata(damageMeta, event.getTarget(), damager);
      MythicLib.plugin.getDamage().markAsMetadata(attackMeta);
   }
}
