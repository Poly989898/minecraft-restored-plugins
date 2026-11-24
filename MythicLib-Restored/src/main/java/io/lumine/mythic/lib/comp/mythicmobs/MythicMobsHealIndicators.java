package io.lumine.mythic.lib.comp.mythicmobs;

import io.lumine.mythic.bukkit.events.MythicHealMechanicEvent;
import io.lumine.mythic.lib.listener.option.RegenIndicators;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

public class MythicMobsHealIndicators implements Listener {
   private final RegenIndicators indicators;

   public MythicMobsHealIndicators(RegenIndicators indicators) {
      this.indicators = indicators;
   }

   @EventHandler
   public void adaptMythicEvent(MythicHealMechanicEvent event) {
      EntityRegainHealthEvent called = new EntityRegainHealthEvent(event.getTarget(), event.getHealAmount(), RegainReason.CUSTOM);
      this.indicators.displayIndicators(called);
   }
}
