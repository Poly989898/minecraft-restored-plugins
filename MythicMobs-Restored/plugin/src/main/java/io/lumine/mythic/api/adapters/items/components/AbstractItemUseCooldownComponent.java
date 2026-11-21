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
import org.bukkit.NamespacedKey;

@Getter
@Setter
@ToString
public class AbstractItemUseCooldownComponent implements AbstractItemComponent {
   public static final String SECTION_KEY = "UseCooldown";
   public static final String KEY_COOLDOWN_GROUP = "UseCooldown.CooldownGroup";
   public static final String KEY_COOLDOWN_SECONDS = "UseCooldown.CooldownSeconds";
   private NamespacedKey cooldownGroup;
   private Float cooldownSeconds;

   public AbstractItemUseCooldownComponent(MythicItem mythicItem, MythicConfig config) {
      String configuredCooldownGroup = config.getString("UseCooldown.CooldownGroup");
      if (configuredCooldownGroup != null) {
         if (!configuredCooldownGroup.contains(":")) {
            configuredCooldownGroup = "minecraft:" + configuredCooldownGroup;
         }

         this.cooldownGroup = NamespacedKey.fromString(configuredCooldownGroup);
      }

      if (config.isSet("UseCooldown.CooldownSeconds")) {
         this.cooldownSeconds = config.getFloat("UseCooldown.CooldownSeconds");
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyUseCooldownComponent(this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemUseCooldownComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$cooldownSeconds = this.getCooldownSeconds();
         Object other$cooldownSeconds = other.getCooldownSeconds();
         if (this$cooldownSeconds == null ? other$cooldownSeconds == null : this$cooldownSeconds.equals(other$cooldownSeconds)) {
            Object this$cooldownGroup = this.getCooldownGroup();
            Object other$cooldownGroup = other.getCooldownGroup();
            return this$cooldownGroup == null ? other$cooldownGroup == null : this$cooldownGroup.equals(other$cooldownGroup);
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof AbstractItemUseCooldownComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $cooldownSeconds = this.getCooldownSeconds();
      result = result * 59 + ($cooldownSeconds == null ? 43 : $cooldownSeconds.hashCode());
      Object $cooldownGroup = this.getCooldownGroup();
      return result * 59 + ($cooldownGroup == null ? 43 : $cooldownGroup.hashCode());
   }
}
