package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import java.util.function.Consumer;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;

@Getter
public class BukkitPigZombieVillager extends BukkitEntityType {
   private boolean angry = false;

   @Override
   public EntityType getBukkitType() {
      return EntityType.ZOMBIFIED_PIGLIN;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.angry = mc.getBoolean("Options.Angry", false);
   }

   @Override
   public Entity spawn(Location location, SpawnReason reason, Consumer<Entity> prespawnFunc) {
      EntityType type;
      if (ServerVersion.isAfter(MinecraftVersions.v1_15)) {
         type = EntityType.valueOf("ZOMBIFIED_PIGLIN");
      } else {
         type = EntityType.valueOf("PIG_ZOMBIE");
      }

      PigZombie e = (PigZombie)this.spawnEntity(location, type, reason, prespawnFunc);
      e.setBaby(false);
      e.setVillager(true);
      return e;
   }

   @Override
   public Entity applyOptions(Entity entity) {
      PigZombie e = (PigZombie)entity;
      e.setAngry(this.angry);
      return e;
   }

   @Override
   public boolean compare(Entity e) {
      return isInstanceOf(e);
   }

   public static boolean isInstanceOf(Entity e) {
      return e instanceof PigZombie pigZombie ? pigZombie.isVillager() : false;
   }
}
