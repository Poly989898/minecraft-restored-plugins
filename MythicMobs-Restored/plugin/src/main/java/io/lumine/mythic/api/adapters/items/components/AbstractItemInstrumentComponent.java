package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.Locale;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.kyori.adventure.key.Key;

@Getter
@Setter
@ToString
public class AbstractItemInstrumentComponent implements AbstractItemComponent {
   public static final String KEY_INSTRUMENT = "Instrument";
   public static final String KEY_INSTRUMENT_OPTIONS = "Options.Instrument";
   private Key instrumentKey;

   public AbstractItemInstrumentComponent(MythicItem mythicItem, MythicConfig config) {
      String instrument = config.getString(new String[]{"Instrument", "Options.Instrument"}, null);
      if (instrument != null) {
         instrument = instrument.toLowerCase(Locale.ROOT);
         if (!instrument.contains(":")) {
            instrument = "minecraft:" + instrument;
         }

         try {
            this.instrumentKey = Key.key(instrument);
         } catch (IllegalArgumentException e) {
            MythicLogger.errorItemConfig(mythicItem, config, "Invalid Instrument '" + instrument + "'");
         }
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyInstrumentComponent(dropMetadata, this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemInstrumentComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$instrumentKey = this.getInstrumentKey();
         Object other$instrumentKey = other.getInstrumentKey();
         return this$instrumentKey == null ? other$instrumentKey == null : this$instrumentKey.equals(other$instrumentKey);
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof AbstractItemInstrumentComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $instrumentKey = this.getInstrumentKey();
      return result * 59 + ($instrumentKey == null ? 43 : $instrumentKey.hashCode());
   }
}
