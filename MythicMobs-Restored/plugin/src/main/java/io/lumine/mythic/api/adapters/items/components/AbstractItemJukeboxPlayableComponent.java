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
public class AbstractItemJukeboxPlayableComponent implements AbstractItemComponent {
   private Key songKey;

   public AbstractItemJukeboxPlayableComponent(MythicItem mythicItem, MythicConfig config) {
      String songKeyName = config.getString(new String[]{"JukeboxPlayable", "Options.JukeboxPlayable"}, null);
      if (songKeyName != null) {
         songKeyName = songKeyName.toLowerCase(Locale.ROOT);
         if (!songKeyName.contains(":")) {
            songKeyName = "minecraft:" + songKeyName;
         }

         try {
            this.songKey = Key.key(songKeyName);
         } catch (IllegalArgumentException e) {
            MythicLogger.errorItemConfig(mythicItem, config, "Invalid JukeboxPlayable song key '" + songKeyName + "'");
         }
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyJukeboxPlayableComponent(dropMetadata, this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemJukeboxPlayableComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$songKey = this.getSongKey();
         Object other$songKey = other.getSongKey();
         return this$songKey == null ? other$songKey == null : this$songKey.equals(other$songKey);
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof AbstractItemJukeboxPlayableComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $songKey = this.getSongKey();
      return result * 59 + ($songKey == null ? 43 : $songKey.hashCode());
   }
}
