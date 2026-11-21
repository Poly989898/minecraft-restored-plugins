package io.lumine.mythic.core.volatilecode.v26_3.ai;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.mobs.ai.PathfinderAdapter;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;

public interface PathfinderHolder extends PathfinderAdapter {
   static PathfinderMob getNMSEntity(AbstractEntity entity) {
      return (PathfinderMob)((CraftLivingEntity)entity.getBukkitEntity()).getHandle();
   }

   Goal create();
}
