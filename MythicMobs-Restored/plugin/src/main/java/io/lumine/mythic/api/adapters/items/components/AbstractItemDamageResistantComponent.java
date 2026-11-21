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
import org.bukkit.NamespacedKey;

@Getter
@Setter
@ToString
public class AbstractItemDamageResistantComponent implements AbstractItemComponent {
   public static final String KEY_DAMAGE_RESISTANT = "DamageResistant";
   public static final String KEY_DAMAGE_RESISTANT_OPTIONS = "Options.DamageResistant";
   private NamespacedKey tag;

   public AbstractItemDamageResistantComponent(MythicItem mythicItem, MythicConfig config) {
      String tag = config.getString(new String[]{"DamageResistant", "Options.DamageResistant"}, null);
      if (tag != null && !tag.isEmpty()) {
         if (tag.startsWith("#")) {
            tag = tag.substring(1);
         }

         this.tag = NamespacedKey.fromString(MythicConfig.normalizeNamespacedKey(tag));
         if (this.tag == null) {
            MythicLogger.errorItemConfig(mythicItem, config, "Invalid DamageResistant tag '" + tag + "'");
         }
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyDamageResistantComponent(dropMetadata, this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemDamageResistantComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$tag = this.getTag();
         Object other$tag = other.getTag();
         return this$tag == null ? other$tag == null : this$tag.equals(other$tag);
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof AbstractItemDamageResistantComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $tag = this.getTag();
      return result * 59 + ($tag == null ? 43 : $tag.hashCode());
   }
}
