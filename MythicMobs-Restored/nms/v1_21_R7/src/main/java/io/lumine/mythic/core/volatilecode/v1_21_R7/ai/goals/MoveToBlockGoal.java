package io.lumine.mythic.core.volatilecode.v1_21_R7.ai.goals;

import io.lumine.mythic.api.adapters.AbstractBlock;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.adapters.BukkitBlock;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R7.ai.PathfinderHolder;
import java.util.EnumSet;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalGotoTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoal.Type;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.bukkit.craftbukkit.v1_21_R7.block.data.CraftBlockData;

@MythicAIGoal(
   name = "moveToBlock",
   aliases = {"gotoblock"},
   description = "Path to a nearby block type"
)
public class MoveToBlockGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   private final AbstractBlock block;
   private final IBlockData state;
   private double speed;
   private int searchRange = 8;
   private int verticalSearchRange = 2;

   public MoveToBlockGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      String b = mlc.getString(new String[]{"material", "mat", "m"}, "IRON_ORE");
      this.block = (BukkitBlock)getPlugin().getBootstrap().getBlock(b);
      this.state = ((CraftBlockData)((BukkitBlock)this.block).getBlockData()).getState();
      this.searchRange = mlc.getInteger(new String[]{"searchrange", "search", "radius", "r"}, 8);
      this.verticalSearchRange = mlc.getInteger(new String[]{"verticalsearchrange", "vsearch", "radiusy", "ry", "yradius", "yr"}, 2);
      this.speed = mlc.getDouble(new String[]{"speed", "s"}, 0.9);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public PathfinderGoal create() {
      return new MoveToBlockGoal.GoToBlockGoal(PathfinderHolder.getNMSEntity(this.entity));
   }

   private class GoToBlockGoal extends PathfinderGoalGotoTarget {
      private final EntityInsentient searchingMob;

      public GoToBlockGoal(EntityInsentient mob) {
         super((EntityCreature)mob, MoveToBlockGoal.this.speed, MoveToBlockGoal.this.searchRange, MoveToBlockGoal.this.verticalSearchRange);
         this.searchingMob = mob;
         this.a(EnumSet.of(Type.c, Type.a));
      }

      public boolean b() {
         if (this.c > 0) {
            this.c--;
            return false;
         } else if (this.tryFindBlock()) {
            this.c = b(20);
            return true;
         } else {
            this.c = this.a(this.a);
            return false;
         }
      }

      private boolean tryFindBlock() {
         return this.e != null && this.a(this.a.ao(), this.e) ? true : this.n();
      }

      protected boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
         IChunkAccess ichunkaccess = iworldreader.a(SectionPosition.a(blockposition.u()), SectionPosition.a(blockposition.w()), ChunkStatus.n, false);
         return ichunkaccess == null
            ? false
            : ichunkaccess.a_(blockposition).equals(MoveToBlockGoal.this.state)
               && ichunkaccess.a_(blockposition.d()).l()
               && ichunkaccess.a_(blockposition.b(2)).l();
      }
   }
}
