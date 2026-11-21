package io.lumine.mythic.core.volatilecode.v1_20_R3.ai.navigators;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.navigation.NavigationGuardian;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.level.pathfinder.PathfinderWater;

public class StayInLavaNavigator extends NavigationGuardian {
   public StayInLavaNavigator(EntityInsentient entitystrider, World world) {
      super(entitystrider, world);
   }

   protected boolean a() {
      return true;
   }

   protected Pathfinder a(int var0) {
      this.o = new PathfinderWater(true);
      return new Pathfinder(this.o, var0);
   }

   public boolean a(BlockPosition blockposition) {
      return this.b.a_(blockposition).a(Blocks.H);
   }
}
