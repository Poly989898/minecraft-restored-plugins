package com.ticxo.modelengine.api.entity;

import com.ticxo.modelengine.api.entity.data.BukkitEntityData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class BukkitPlayer extends BukkitEntity {
   public BukkitPlayer(Player original) {
      super(original);
   }

   @Override
   protected BukkitEntityData createEntityData(Entity original) {
      return new BukkitPlayer.BukkitPlayerData(original, this);
   }

   @Override
   public boolean isWalking() {
      return ((BukkitPlayer.BukkitPlayerData)this.getData()).getWalkTick() > 0;
   }

   @Override
   public boolean isJumping() {
      return ((BukkitPlayer.BukkitPlayerData)this.getData()).getJumpTick() > 0;
   }

   @Override
   public boolean isFlying() {
      return ((BukkitPlayer.BukkitPlayerData)this.getData()).isFlying;
   }

   public static class BukkitPlayerData extends BukkitEntityData {
      @Getter @Setter
      private int walkTick;
      @Getter @Setter
      private int jumpTick;
      private boolean isFlying;

      public BukkitPlayerData(Entity entity, BukkitPlayer player) {
         super(entity, player);
      }

      @Override
      protected void safeSyncUpdate() {
         super.safeSyncUpdate();
         if (this.walkTick > 0) {
            this.walkTick--;
         }

         if (this.jumpTick > 0 && this.entity.isOnGround()) {
            this.jumpTick--;
         }

         this.isFlying = ((Player)this.entity).isFlying();
      }
   }
}
