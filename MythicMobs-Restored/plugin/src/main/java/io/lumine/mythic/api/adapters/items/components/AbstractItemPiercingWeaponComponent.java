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
public class AbstractItemPiercingWeaponComponent implements AbstractItemComponent {
   public static final String SECTION_KEY = "PiercingWeapon";
   public static final String KEY_DEALS_KNOCKBACK = "PiercingWeapon.DealsKnockback";
   public static final String KEY_KNOCKBACK = "PiercingWeapon.Knockback";
   public static final String KEY_DISMOUNTS = "PiercingWeapon.Dismounts";
   public static final String KEY_SOUND = "PiercingWeapon.Sound";
   public static final String KEY_HIT_SOUND = "PiercingWeapon.HitSound";
   private Boolean dealsKnockback;
   private Boolean dismounts;
   private String sound;
   private String hitSound;

   public AbstractItemPiercingWeaponComponent(MythicItem mythicItem, MythicConfig config) {
      if (config.isSet("PiercingWeapon.DealsKnockback")) {
         this.dealsKnockback = config.getBoolean("PiercingWeapon.DealsKnockback");
      } else if (config.isSet("PiercingWeapon.Knockback")) {
         this.dealsKnockback = config.getBoolean("PiercingWeapon.Knockback");
      }

      if (config.isSet("PiercingWeapon.Dismounts")) {
         this.dismounts = config.getBoolean("PiercingWeapon.Dismounts");
      }

      if (config.isSet("PiercingWeapon.Sound")) {
         this.sound = MythicConfig.normalizeNamespacedKey(config.getString("PiercingWeapon.Sound"));
      }

      if (config.isSet("PiercingWeapon.HitSound")) {
         this.hitSound = MythicConfig.normalizeNamespacedKey(config.getString("PiercingWeapon.HitSound"));
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyPiercingWeaponComponent(dropMetadata, this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemPiercingWeaponComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$dealsKnockback = this.getDealsKnockback();
         Object other$dealsKnockback = other.getDealsKnockback();
         if (this$dealsKnockback == null ? other$dealsKnockback == null : this$dealsKnockback.equals(other$dealsKnockback)) {
            Object this$dismounts = this.getDismounts();
            Object other$dismounts = other.getDismounts();
            if (this$dismounts == null ? other$dismounts == null : this$dismounts.equals(other$dismounts)) {
               Object this$sound = this.getSound();
               Object other$sound = other.getSound();
               if (this$sound == null ? other$sound == null : this$sound.equals(other$sound)) {
                  Object this$hitSound = this.getHitSound();
                  Object other$hitSound = other.getHitSound();
                  return this$hitSound == null ? other$hitSound == null : this$hitSound.equals(other$hitSound);
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
      return other instanceof AbstractItemPiercingWeaponComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $dealsKnockback = this.getDealsKnockback();
      result = result * 59 + ($dealsKnockback == null ? 43 : $dealsKnockback.hashCode());
      Object $dismounts = this.getDismounts();
      result = result * 59 + ($dismounts == null ? 43 : $dismounts.hashCode());
      Object $sound = this.getSound();
      result = result * 59 + ($sound == null ? 43 : $sound.hashCode());
      Object $hitSound = this.getHitSound();
      return result * 59 + ($hitSound == null ? 43 : $hitSound.hashCode());
   }
}
