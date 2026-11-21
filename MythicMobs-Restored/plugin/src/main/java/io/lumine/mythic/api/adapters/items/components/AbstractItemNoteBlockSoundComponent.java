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
public class AbstractItemNoteBlockSoundComponent implements AbstractItemComponent {
   private Key key;

   public AbstractItemNoteBlockSoundComponent(MythicItem mythicItem, MythicConfig config) {
      String noteBlockSound = config.getString(new String[]{"NoteBlockSound", "Options.NoteBlockSound"}, null);
      if (noteBlockSound != null) {
         noteBlockSound = noteBlockSound.toLowerCase(Locale.ROOT);
         if (!noteBlockSound.contains(":")) {
            noteBlockSound = "minecraft:" + noteBlockSound;
         }

         try {
            this.key = Key.key(noteBlockSound);
         } catch (IllegalArgumentException e) {
            MythicLogger.errorItemConfig(mythicItem, config, "Invalid NoteBlockSound '" + noteBlockSound + "'");
         }
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyNoteBlockSoundComponent(dropMetadata, this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemNoteBlockSoundComponent other)) {
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
      return other instanceof AbstractItemNoteBlockSoundComponent;
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
