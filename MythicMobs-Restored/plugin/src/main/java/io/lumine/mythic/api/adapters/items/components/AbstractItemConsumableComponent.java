package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.core.items.MythicItem;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AbstractItemConsumableComponent implements AbstractItemComponent {
   public static final String SECTION_KEY = "Consumable";
   public static final String KEY_CONSUME_SECONDS = "Consumable.ConsumeSeconds";
   public static final String KEY_HAS_PARTICLES = "Consumable.HasParticles";
   public static final String KEY_ANIMATION = "Consumable.Animation";
   public static final String KEY_SOUND = "Consumable.Sound";
   public static final String KEY_CONSUME_EFFECTS = "Consumable.ConsumeEffects";
   private Float consumeSeconds = null;
   private Boolean hasConsumeParticles = null;
   private String animation = null;
   private String sound = null;

   public AbstractItemConsumableComponent(MythicItem mythicItem, MythicConfig config) {
      if (config.isSet("Consumable.ConsumeSeconds")) {
         this.consumeSeconds = config.getFloat("Consumable.ConsumeSeconds", 1.6F);
      }

      if (config.isSet("Consumable.HasParticles")) {
         this.hasConsumeParticles = config.getBoolean("Consumable.HasParticles", true);
      }

      if (config.isSet("Consumable.Animation")) {
         this.animation = config.getString("Consumable.Animation", "");
      }

      if (config.isSet("Consumable.Sound")) {
         this.sound = MythicConfig.normalizeNamespacedKey(config.getString("Consumable.Sound", ""));
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyConsumableComponent(this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemConsumableComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$consumeSeconds = this.getConsumeSeconds();
         Object other$consumeSeconds = other.getConsumeSeconds();
         if (this$consumeSeconds == null ? other$consumeSeconds == null : this$consumeSeconds.equals(other$consumeSeconds)) {
            Object this$hasConsumeParticles = this.getHasConsumeParticles();
            Object other$hasConsumeParticles = other.getHasConsumeParticles();
            if (this$hasConsumeParticles == null ? other$hasConsumeParticles == null : this$hasConsumeParticles.equals(other$hasConsumeParticles)) {
               Object this$animation = this.getAnimation();
               Object other$animation = other.getAnimation();
               if (this$animation == null ? other$animation == null : this$animation.equals(other$animation)) {
                  Object this$sound = this.getSound();
                  Object other$sound = other.getSound();
                  return this$sound == null ? other$sound == null : this$sound.equals(other$sound);
               } else {
                  return false;
               }
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
      return other instanceof AbstractItemConsumableComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $consumeSeconds = this.getConsumeSeconds();
      result = result * 59 + ($consumeSeconds == null ? 43 : $consumeSeconds.hashCode());
      Object $hasConsumeParticles = this.getHasConsumeParticles();
      result = result * 59 + ($hasConsumeParticles == null ? 43 : $hasConsumeParticles.hashCode());
      Object $animation = this.getAnimation();
      result = result * 59 + ($animation == null ? 43 : $animation.hashCode());
      Object $sound = this.getSound();
      return result * 59 + ($sound == null ? 43 : $sound.hashCode());
   }
}
