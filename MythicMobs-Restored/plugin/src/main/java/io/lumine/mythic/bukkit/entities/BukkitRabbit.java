package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Rabbit.Type;

@Getter
public class BukkitRabbit extends BukkitEntityType {
   private AgeableProperty ageableProperty;
   private boolean isAngry = false;
   private Type type;

   @Override
   public EntityType getBukkitType() {
      return EntityType.RABBIT;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.ageableProperty = new AgeableProperty(mc);
      this.isAngry = mc.getBoolean("Options.IsKillerBunny", false);
      this.isAngry = mc.getBoolean("Options.Angry", this.isAngry);
      String t = mc.getString("Options.RabbitType", null);
      if (t != null) {
         this.type = Type.valueOf(t.toUpperCase());
      }
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Rabbit e = (Rabbit)entity;
      this.ageableProperty.applyProperties(entity);
      if (this.type != null) {
         e.setRabbitType(this.type);
      }

      if (this.isAngry) {
         e.setRabbitType(Type.THE_KILLER_BUNNY);
      }

      return e;
   }
}
