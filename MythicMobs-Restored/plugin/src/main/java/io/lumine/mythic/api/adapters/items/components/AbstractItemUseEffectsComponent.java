package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AbstractItemUseEffectsComponent implements AbstractItemComponent {
   private Boolean canSprint;
   private Boolean interactVibrations;
   private Float speedMultiplier;

   public AbstractItemUseEffectsComponent(MythicItem mythicItem, MythicConfig config) {
      if (config.isSet("CanSprint")) {
         this.canSprint = config.getBoolean("CanSprint");
      }

      if (config.isSet("InteractVibrations")) {
         this.interactVibrations = config.getBoolean("InteractVibrations");
      }

      if (config.isSet("SpeedMultiplier")) {
         this.speedMultiplier = config.getFloat("SpeedMultiplier");
         if (this.speedMultiplier < 0.0F || this.speedMultiplier > 1.0F) {
            MythicLogger.errorGenericConfig(config, "UseEffects.SpeedMultiplier must be between 0 and 1, clamping");
            this.speedMultiplier = Math.max(0.0F, Math.min(1.0F, this.speedMultiplier));
         }
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyUseEffectsComponent(dropMetadata, this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemUseEffectsComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$canSprint = this.getCanSprint();
         Object other$canSprint = other.getCanSprint();
         if (this$canSprint == null ? other$canSprint == null : this$canSprint.equals(other$canSprint)) {
            Object this$interactVibrations = this.getInteractVibrations();
            Object other$interactVibrations = other.getInteractVibrations();
            if (this$interactVibrations == null ? other$interactVibrations == null : this$interactVibrations.equals(other$interactVibrations)) {
               Object this$speedMultiplier = this.getSpeedMultiplier();
               Object other$speedMultiplier = other.getSpeedMultiplier();
               return this$speedMultiplier == null ? other$speedMultiplier == null : this$speedMultiplier.equals(other$speedMultiplier);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof AbstractItemUseEffectsComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $canSprint = this.getCanSprint();
      result = result * 59 + ($canSprint == null ? 43 : $canSprint.hashCode());
      Object $interactVibrations = this.getInteractVibrations();
      result = result * 59 + ($interactVibrations == null ? 43 : $interactVibrations.hashCode());
      Object $speedMultiplier = this.getSpeedMultiplier();
      return result * 59 + ($speedMultiplier == null ? 43 : $speedMultiplier.hashCode());
   }
}
