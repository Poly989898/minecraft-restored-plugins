package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import java.util.function.Consumer;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PiglinBrute;

@Getter
public class BukkitPiglinBrute extends BukkitEntityType {
   private boolean immuneToZombification = false;

   @Override
   public EntityType getBukkitType() {
      return EntityType.PIGLIN_BRUTE;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.immuneToZombification = mc.getBoolean("Options.ImmuneToZombification", true);
   }

   @Override
   public Entity spawn(Location location, SpawnReason reason, Consumer<Entity> prespawnFunc) {
      PiglinBrute e = (PiglinBrute)this.spawnEntity(location, EntityType.PIGLIN_BRUTE, reason, prespawnFunc);
      e.setBaby(false);
      return e;
   }

   @Override
   public Entity applyOptions(Entity entity) {
      PiglinBrute z = (PiglinBrute)entity;
      z.setBaby(false);
      z.setImmuneToZombification(this.immuneToZombification);
      return z;
   }
}
