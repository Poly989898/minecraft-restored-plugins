package io.lumine.mythic.core.volatilecode.v1_21_R5.ai;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.mobs.ai.PathfinderAdapter;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftLivingEntity;

public interface PathfinderHolder extends PathfinderAdapter {
   static EntityCreature getNMSEntity(AbstractEntity entity) {
      return (EntityCreature)((CraftLivingEntity)entity.getBukkitEntity()).getHandle();
   }

   PathfinderGoal create();
}
