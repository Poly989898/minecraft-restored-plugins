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
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;

@Getter
@Setter
@ToString
public class AbstractItemDamageTypeComponent implements AbstractItemComponent {
   public static final String KEY_DAMAGE_TYPE = "DamageType";
   public static final String KEY_DAMAGE_TYPE_OPTIONS = "Options.DamageType";
   private Key key;

   public AbstractItemDamageTypeComponent(MythicItem mythicItem, MythicConfig config) {
      String damageType = MythicConfig.normalizeNamespacedKey(config.getString(new String[]{"DamageType", "Options.DamageType"}, null));
      if (damageType != null) {
         try {
            this.key = Key.key(damageType);
         } catch (InvalidKeyException e) {
            MythicLogger.errorItemConfig(mythicItem, config, "Invalid DamageType '" + damageType + "'");
         }
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyDamageTypeComponent(dropMetadata, this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemDamageTypeComponent other)) {
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
      return other instanceof AbstractItemDamageTypeComponent;
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
