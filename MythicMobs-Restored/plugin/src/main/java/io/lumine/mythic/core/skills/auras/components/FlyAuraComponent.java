package io.lumine.mythic.core.skills.auras.components;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.mechanics.AuraMechanic;
import io.lumine.mythic.core.utils.annotations.MythicAuraComponent;
import java.util.Optional;

public class FlyAuraComponent implements AuraComponent {
   private final Aura.AuraTracker tracker;
   private AbstractPlayer target;
   private boolean couldFly;

   public FlyAuraComponent(Aura.AuraTracker tracker) {
      this.tracker = tracker;
   }

   @Override
   public void auraStart() {
      Optional<AbstractEntity> maybeEntity = this.tracker.getEntity();
      if (!maybeEntity.isEmpty() && maybeEntity.get().isPlayer()) {
         this.target = maybeEntity.get().asPlayer();
         this.couldFly = this.target.getAllowFlight();
         this.target.setAllowFlight(true);
         this.target.setFlying(true);
      }
   }

   @Override
   public void auraStop() {
      if (this.target != null) {
         this.target.setFlying(false);
         this.target.setAllowFlight(this.couldFly);
      }
   }

   @MythicAuraComponent(
      name = "fly",
      aliases = {"flight"},
      description = "Enables flight while the aura is active"
   )
   public static class Factory implements AuraComponentFactory {
      public Factory(AuraMechanic mechanic, MythicLineConfig mlc) {
      }

      @Override
      public AuraComponent create(Aura.AuraTracker tracker) {
         return new FlyAuraComponent(tracker);
      }
   }
}
