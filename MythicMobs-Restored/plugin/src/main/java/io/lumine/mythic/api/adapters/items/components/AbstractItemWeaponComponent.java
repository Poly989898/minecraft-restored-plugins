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
public class AbstractItemWeaponComponent implements AbstractItemComponent {
   public static final String SECTION_KEY = "Weapon";
   public static final String KEY_ITEM_DAMAGE_PER_ATTACK = "Weapon.ItemDamagePerAttack";
   public static final String KEY_DISABLE_BLOCKING_FOR_SECONDS = "Weapon.DisableBlockingForSeconds";
   private Integer itemDamagePerAttack;
   private Float disableBlockingForSeconds;

   public AbstractItemWeaponComponent(MythicItem mythicItem, MythicConfig config) {
      if (config.isSet("Weapon.ItemDamagePerAttack")) {
         this.itemDamagePerAttack = config.getInteger("Weapon.ItemDamagePerAttack");
      }

      if (config.isSet("Weapon.DisableBlockingForSeconds")) {
         this.disableBlockingForSeconds = config.getFloat("Weapon.DisableBlockingForSeconds");
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyWeaponComponent(dropMetadata, this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemWeaponComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$itemDamagePerAttack = this.getItemDamagePerAttack();
         Object other$itemDamagePerAttack = other.getItemDamagePerAttack();
         if (this$itemDamagePerAttack == null ? other$itemDamagePerAttack == null : this$itemDamagePerAttack.equals(other$itemDamagePerAttack)) {
            Object this$disableBlockingForSeconds = this.getDisableBlockingForSeconds();
            Object other$disableBlockingForSeconds = other.getDisableBlockingForSeconds();
            return this$disableBlockingForSeconds == null
               ? other$disableBlockingForSeconds == null
               : this$disableBlockingForSeconds.equals(other$disableBlockingForSeconds);
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof AbstractItemWeaponComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $itemDamagePerAttack = this.getItemDamagePerAttack();
      result = result * 59 + ($itemDamagePerAttack == null ? 43 : $itemDamagePerAttack.hashCode());
      Object $disableBlockingForSeconds = this.getDisableBlockingForSeconds();
      return result * 59 + ($disableBlockingForSeconds == null ? 43 : $disableBlockingForSeconds.hashCode());
   }
}
