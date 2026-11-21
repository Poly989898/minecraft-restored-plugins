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
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;

@Getter
@Setter
@ToString
public class AbstractItemBreakSoundComponent implements AbstractItemComponent {
   public static final String KEY_BREAK_SOUND = "BreakSound";
   public static final String KEY_BREAK_SOUND_OPTIONS = "Options.BreakSound";
   private Key key;

   public AbstractItemBreakSoundComponent(MythicItem mythicItem, MythicConfig config) {
      String breakSound = config.getString(new String[]{"BreakSound", "Options.BreakSound"}, null);
      if (breakSound != null) {
         breakSound = breakSound.toLowerCase(Locale.ROOT);
         if (!breakSound.contains(":")) {
            breakSound = "minecraft:" + breakSound;
         }

         try {
            this.key = Key.key(breakSound);
         } catch (InvalidKeyException e) {
            MythicLogger.errorItemConfig(mythicItem, config, "Invalid BreakSound '" + breakSound + "'");
         }
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyBreakSoundComponent(dropMetadata, this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemBreakSoundComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$key = this.getKey();
         Object other$key = other.getKey();
         return this$key == null ? other$key == null : this$key.equals(other$key);
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof AbstractItemBreakSoundComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $key = this.getKey();
      return result * 59 + ($key == null ? 43 : $key.hashCode());
   }
}
