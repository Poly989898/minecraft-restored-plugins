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
public class AbstractItemOminousBottleAmplifierComponent implements AbstractItemComponent {
   private Integer amplifier;

   public AbstractItemOminousBottleAmplifierComponent(MythicItem mythicItem, MythicConfig config) {
      if (config.isSet("OminousBottleAmplifier")) {
         this.amplifier = config.getInteger("OminousBottleAmplifier");
      } else if (config.isSet("OminousBottle.Amplifier")) {
         this.amplifier = config.getInteger("OminousBottle.Amplifier");
      }

      if (this.amplifier != null && (this.amplifier < 0 || this.amplifier > 4)) {
         MythicLogger.errorGenericConfig(config, "OminousBottleAmplifier must be between 0 and 4, using 0 instead");
         this.amplifier = 0;
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyOminousBottleAmplifierComponent(dropMetadata, this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemOminousBottleAmplifierComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$amplifier = this.getAmplifier();
         Object other$amplifier = other.getAmplifier();
         return this$amplifier == null ? other$amplifier == null : this$amplifier.equals(other$amplifier);
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof AbstractItemOminousBottleAmplifierComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $amplifier = this.getAmplifier();
      return result * 59 + ($amplifier == null ? 43 : $amplifier.hashCode());
   }
}
