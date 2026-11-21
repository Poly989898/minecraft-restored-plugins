package com.ticxo.modelengine.v1_21_R1.entity;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class EntityContainer extends Entity {
   public static EntityContainer of(int id) {
      return new EntityContainer(id);
   }

   public static Entity of(int id, int... passengerIds) {
      EntityContainer container = of(id);
      container.setPassengers(Arrays.stream(passengerIds).mapToObj(EntityContainer::new).toList());
      return container;
   }

   public static Entity of(int id, Collection<Integer> passengerIds) {
      EntityContainer container = of(id);
      container.setPassengers(passengerIds.stream().map(EntityContainer::new).toList());
      return container;
   }

   private EntityContainer(int id) {
      super(EntityType.INTERACTION, null);
      this.setId(id);
      this.setPosRaw(0.0, 0.0, 0.0);
      this.setRot(0.0F, 0.0F);
      this.setOnGround(false);
   }

   protected void setPassengers(List<? extends Entity> passengers) {
      this.passengers = ImmutableList.copyOf(passengers);
   }

   protected void defineSynchedData(@NotNull Builder builder) {
   }

   protected void readAdditionalSaveData(@NotNull CompoundTag nbt) {
   }

   protected void addAdditionalSaveData(@NotNull CompoundTag nbt) {
   }
}
