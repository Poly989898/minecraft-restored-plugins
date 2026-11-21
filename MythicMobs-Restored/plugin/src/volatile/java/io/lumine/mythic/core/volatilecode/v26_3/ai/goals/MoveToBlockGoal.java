package io.lumine.mythic.core.volatilecode.v26_3.ai.goals;

import io.lumine.mythic.api.adapters.AbstractBlock;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.adapters.BukkitBlock;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_3.ai.PathfinderHolder;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

@MythicAIGoal(
   name = "moveToBlock",
   aliases = {"gotoblock"},
   description = "Path to a nearby block type"
)
public class MoveToBlockGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   private final AbstractBlock block;
   private final BlockState state;
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
   public Goal create() {
      return new MoveToBlockGoal.GoToBlockGoal(PathfinderHolder.getNMSEntity(this.entity));
   }

   private class GoToBlockGoal extends net.minecraft.world.entity.ai.goal.MoveToBlockGoal {
      private final Mob searchingMob;

      public GoToBlockGoal(Mob mob) {
         super((PathfinderMob)mob, MoveToBlockGoal.this.speed, MoveToBlockGoal.this.searchRange, MoveToBlockGoal.this.verticalSearchRange);
         this.searchingMob = mob;
         this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
      }

      public boolean canUse() {
         if (this.nextStartTick > 0) {
            this.nextStartTick--;
            return false;
         } else if (this.tryFindBlock()) {
            this.nextStartTick = reducedTickDelay(20);
            return true;
         } else {
            this.nextStartTick = this.nextStartTick(this.mob);
            return false;
         }
      }

      private boolean tryFindBlock() {
         return this.blockPos != null && this.isValidTarget(this.mob.level(), this.blockPos) ? true : this.findNearestBlock();
      }

      protected boolean isValidTarget(LevelReader iworldreader, BlockPos blockposition) {
         ChunkAccess ichunkaccess = iworldreader.getChunk(
            SectionPos.blockToSectionCoord(blockposition.getX()), SectionPos.blockToSectionCoord(blockposition.getZ()), ChunkStatus.FULL, false
         );
         return ichunkaccess == null
            ? false
            : ichunkaccess.getBlockState(blockposition).equals(MoveToBlockGoal.this.state)
               && ichunkaccess.getBlockState(blockposition.above()).isAir()
               && ichunkaccess.getBlockState(blockposition.above(2)).isAir();
      }
   }
}
