package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import io.lumine.mythic.bukkit.entities.properties.MerchantProperty;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WanderingTrader;

@Getter
public class BukkitWanderingTrader extends BukkitEntityType {
   private AgeableProperty ageableProperty;
   private MerchantProperty merchantProperty;

   @Override
   public EntityType getBukkitType() {
      return EntityType.WANDERING_TRADER;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.ageableProperty = new AgeableProperty(mc);
      this.merchantProperty = new MerchantProperty(mc);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      WanderingTrader e = (WanderingTrader)entity;
      this.ageableProperty.applyProperties(entity);
      this.merchantProperty.applyProperties(entity);
      return e;
   }
}
