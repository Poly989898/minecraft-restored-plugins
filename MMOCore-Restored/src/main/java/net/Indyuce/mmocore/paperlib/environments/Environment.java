package net.Indyuce.mmocore.paperlib.environments;

import java.util.concurrent.CompletableFuture;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Indyuce.mmocore.paperlib.features.asyncchunks.AsyncChunks;
import net.Indyuce.mmocore.paperlib.features.asyncchunks.AsyncChunksSync;
import net.Indyuce.mmocore.paperlib.features.asyncteleport.AsyncTeleport;
import net.Indyuce.mmocore.paperlib.features.asyncteleport.AsyncTeleportSync;
import net.Indyuce.mmocore.paperlib.features.bedspawnlocation.BedSpawnLocation;
import net.Indyuce.mmocore.paperlib.features.bedspawnlocation.BedSpawnLocationSync;
import net.Indyuce.mmocore.paperlib.features.blockstatesnapshot.BlockStateSnapshot;
import net.Indyuce.mmocore.paperlib.features.blockstatesnapshot.BlockStateSnapshotBeforeSnapshots;
import net.Indyuce.mmocore.paperlib.features.blockstatesnapshot.BlockStateSnapshotNoOption;
import net.Indyuce.mmocore.paperlib.features.blockstatesnapshot.BlockStateSnapshotResult;
import net.Indyuce.mmocore.paperlib.features.chunkisgenerated.ChunkIsGenerated;
import net.Indyuce.mmocore.paperlib.features.chunkisgenerated.ChunkIsGeneratedApiExists;
import net.Indyuce.mmocore.paperlib.features.chunkisgenerated.ChunkIsGeneratedUnknown;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public abstract class Environment {
   private int minecraftVersion;
   private int minecraftPatchVersion;
   protected AsyncChunks asyncChunksHandler = new AsyncChunksSync();
   protected AsyncTeleport asyncTeleportHandler = new AsyncTeleportSync();
   protected ChunkIsGenerated isGeneratedHandler = new ChunkIsGeneratedUnknown();
   protected BlockStateSnapshot blockStateSnapshotHandler;
   protected BedSpawnLocation bedSpawnLocationHandler = new BedSpawnLocationSync();

   public Environment() {
      Pattern versionPattern = Pattern.compile("\\(MC: (\\d)\\.(\\d+)\\.?(\\d+?)?\\)");
      Matcher matcher = versionPattern.matcher(Bukkit.getVersion());
      int version = 0;
      int patchVersion = 0;
      if (matcher.find()) {
         MatchResult matchResult = matcher.toMatchResult();

         try {
            version = Integer.parseInt(matchResult.group(2), 10);
         } catch (Exception exception) {
         }

         if (matchResult.groupCount() >= 3) {
            try {
               patchVersion = Integer.parseInt(matchResult.group(3), 10);
            } catch (Exception exception) {
            }
         }
      }

      this.minecraftVersion = version;
      this.minecraftPatchVersion = patchVersion;
      if (this.isVersion(13, 1)) {
         this.isGeneratedHandler = new ChunkIsGeneratedApiExists();
      }

      if (!this.isVersion(12)) {
         this.blockStateSnapshotHandler = new BlockStateSnapshotBeforeSnapshots();
      } else {
         this.blockStateSnapshotHandler = new BlockStateSnapshotNoOption();
      }
   }

   public abstract String getName();

   public CompletableFuture<Chunk> getChunkAtAsync(World world, int x, int z, boolean gen) {
      return this.asyncChunksHandler.getChunkAtAsync(world, x, z, gen, false);
   }

   public CompletableFuture<Chunk> getChunkAtAsync(World world, int x, int z, boolean gen, boolean isUrgent) {
      return this.asyncChunksHandler.getChunkAtAsync(world, x, z, gen, isUrgent);
   }

   public CompletableFuture<Chunk> getChunkAtAsyncUrgently(World world, int x, int z, boolean gen) {
      return this.asyncChunksHandler.getChunkAtAsync(world, x, z, gen, true);
   }

   public CompletableFuture<Boolean> teleport(Entity entity, Location location, TeleportCause cause) {
      return this.asyncTeleportHandler.teleportAsync(entity, location, cause);
   }

   public boolean isChunkGenerated(World world, int x, int z) {
      return this.isGeneratedHandler.isChunkGenerated(world, x, z);
   }

   public BlockStateSnapshotResult getBlockState(Block block, boolean useSnapshot) {
      return this.blockStateSnapshotHandler.getBlockState(block, useSnapshot);
   }

   public CompletableFuture<Location> getBedSpawnLocationAsync(Player player, boolean isUrgent) {
      return this.bedSpawnLocationHandler.getBedSpawnLocationAsync(player, isUrgent);
   }

   public boolean isVersion(int minor) {
      return this.isVersion(minor, 0);
   }

   public boolean isVersion(int minor, int patch) {
      return this.minecraftVersion > minor || this.minecraftVersion >= minor && this.minecraftPatchVersion >= patch;
   }

   public int getMinecraftVersion() {
      return this.minecraftVersion;
   }

   public int getMinecraftPatchVersion() {
      return this.minecraftPatchVersion;
   }

   public boolean isSpigot() {
      return false;
   }

   public boolean isPaper() {
      return false;
   }
}
