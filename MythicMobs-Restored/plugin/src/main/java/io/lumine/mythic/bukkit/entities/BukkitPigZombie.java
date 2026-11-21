package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.inventory.ItemStack;

public class BukkitPigZombie extends BukkitEntityType {
   private final String headString = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRlOWM2ZTk4NTgyZmZkOGZmOGZlYjMzMjJjZDE4NDljNDNmYjE2YjE1OGFiYjExY2E3YjQyZWRhNzc0M2ViIn19fQ";
   @Getter
   private final ItemStack head = BukkitAdapter.adapt(
      MythicBukkit.inst()
         .getBootstrap()
         .createItem("PLAYER_HEAD")
         .skullTexture(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRlOWM2ZTk4NTgyZmZkOGZmOGZlYjMzMjJjZDE4NDljNDNmYjE2YjE1OGFiYjExY2E3YjQyZWRhNzc0M2ViIn19fQ"
         )
   );
   @Getter
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
   public Entity applyOptions(Entity entity) {
      PigZombie e = (PigZombie)entity;
      e.setBaby(false);
      e.setAngry(this.angry);
      return e;
   }

   @Override
   public boolean compare(Entity e) {
      return isInstanceOf(e);
   }

   public static boolean isInstanceOf(Entity e) {
      return e instanceof PigZombie ? !((PigZombie)e).isBaby() : false;
   }

   @Generated
   @Override
   public String getHeadString() {
      return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRlOWM2ZTk4NTgyZmZkOGZmOGZlYjMzMjJjZDE4NDljNDNmYjE2YjE1OGFiYjExY2E3YjQyZWRhNzc0M2ViIn19fQ";
   }
}
