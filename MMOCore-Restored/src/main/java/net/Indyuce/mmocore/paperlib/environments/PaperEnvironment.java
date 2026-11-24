package net.Indyuce.mmocore.paperlib.environments;

import net.Indyuce.mmocore.paperlib.features.asyncchunks.AsyncChunksPaper_13;
import net.Indyuce.mmocore.paperlib.features.asyncchunks.AsyncChunksPaper_15;
import net.Indyuce.mmocore.paperlib.features.asyncchunks.AsyncChunksPaper_9_12;
import net.Indyuce.mmocore.paperlib.features.asyncteleport.AsyncTeleportPaper;
import net.Indyuce.mmocore.paperlib.features.asyncteleport.AsyncTeleportPaper_13;
import net.Indyuce.mmocore.paperlib.features.bedspawnlocation.BedSpawnLocationPaper;
import net.Indyuce.mmocore.paperlib.features.blockstatesnapshot.BlockStateSnapshotOptionalSnapshots;
import net.Indyuce.mmocore.paperlib.features.chunkisgenerated.ChunkIsGeneratedApiExists;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;

public class PaperEnvironment extends SpigotEnvironment {
   public PaperEnvironment() {
      if (this.isVersion(13, 1)) {
         this.asyncChunksHandler = new AsyncChunksPaper_13();
         this.asyncTeleportHandler = new AsyncTeleportPaper_13();
      } else if (this.isVersion(9) && !this.isVersion(13)) {
         this.asyncChunksHandler = new AsyncChunksPaper_9_12();
         this.asyncTeleportHandler = new AsyncTeleportPaper();
      }

      if (this.isVersion(12)) {
         this.isGeneratedHandler = new ChunkIsGeneratedApiExists();
         this.blockStateSnapshotHandler = new BlockStateSnapshotOptionalSnapshots();
      }

      if (this.isVersion(15, 2)) {
         try {
            World.class.getDeclaredMethod("getChunkAtAsyncUrgently", Location.class);
            this.asyncChunksHandler = new AsyncChunksPaper_15();
            HumanEntity.class.getDeclaredMethod("getPotentialBedLocation");
            this.bedSpawnLocationHandler = new BedSpawnLocationPaper();
         } catch (NoSuchMethodException exception) {
         }
      }
   }

   @Override
   public String getName() {
      return "Paper";
   }

   @Override
   public boolean isPaper() {
      return true;
   }
}
