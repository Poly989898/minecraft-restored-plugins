package io.lumine.mythic.core.volatilecode.v1_21_R3.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R3.ai.PathfinderHolder;
import java.util.EnumSet;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalGotoTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoal.Type;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathMode;

@MythicAIGoal(
   name = "moveToLava",
   aliases = {"gotolava"},
   description = "Path to nearby lava"
)
public class MoveToLavaGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   private double speed;
   private int searchRange = 8;
   private int verticalSearchRange = 2;

   public MoveToLavaGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.speed = mlc.getDouble(new String[]{"speed", "s"}, 0.9);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public PathfinderGoal create() {
      return new MoveToLavaGoal.StriderGoToLavaGoal(PathfinderHolder.getNMSEntity(this.entity));
   }

   private class StriderGoToLavaGoal extends PathfinderGoalGotoTarget {
      private final EntityInsentient mob;

      public StriderGoToLavaGoal(EntityInsentient mob) {
         super((EntityCreature)mob, MoveToLavaGoal.this.speed, MoveToLavaGoal.this.searchRange, MoveToLavaGoal.this.verticalSearchRange);
         this.mob = mob;
         this.a(EnumSet.of(Type.c, Type.a));
      }

      public BlockPosition k() {
         return this.e;
      }

      public boolean c() {
         return !this.mob.bx() && this.a(this.mob.dV(), this.e);
      }

      public boolean b() {
         return !this.mob.bx() && super.b();
      }

      public boolean l() {
         return this.d % 20 == 0;
      }

      protected boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
         return iworldreader.a_(blockposition).a(Blocks.K) && iworldreader.a_(blockposition.d()).a(PathMode.a);
      }
   }
}
