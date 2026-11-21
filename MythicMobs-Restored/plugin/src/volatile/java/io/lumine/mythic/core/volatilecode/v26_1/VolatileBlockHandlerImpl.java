package io.lumine.mythic.core.volatilecode.v26_1;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractBlock;
import io.lumine.mythic.api.adapters.AbstractDirection;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.volatilecode.handlers.VolatileBlockHandler;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitBlock;
import io.lumine.mythic.bukkit.utils.Players;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.reflection.Reflector;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.volatilecode.VolatileCodeEnabled_v26_1;
import it.unimi.dsi.fastutil.shorts.ShortArraySet;
import java.lang.reflect.Constructor;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Piston;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class VolatileBlockHandlerImpl implements VolatileBlockHandler {
   private final VolatileCodeEnabled_v26_1 handler;

   public VolatileBlockHandlerImpl(VolatileCodeEnabled_v26_1 handler) {
      this.handler = handler;
   }

   @Override
   public void sendBlockChange(Collection<AbstractPlayer> audience, AbstractLocation location, AbstractBlock block) {
      BlockPos blockPosition = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
      BlockState blockState;
      if (block == null) {
         ServerLevel nmsWorld = ((CraftWorld)BukkitAdapter.adapt(location).getWorld()).getHandle();
         blockState = nmsWorld.getBlockState(blockPosition);
      } else {
         if (!(block instanceof BukkitBlock bukkitBlock)) {
            return;
         }

         blockState = ((CraftBlockData)bukkitBlock.getBlockData()).getState();
      }

      ClientboundBlockUpdatePacket packet = new ClientboundBlockUpdatePacket(blockPosition, blockState);

      for (AbstractPlayer player : audience) {
         ServerPlayer nmsPlayer = ((CraftPlayer)BukkitAdapter.adapt(player)).getHandle();
         nmsPlayer.connection.send(packet);
      }
   }

   @Override
   public void sendMultiBlockChange(Collection<AbstractPlayer> audience, Map<AbstractLocation, AbstractBlock> changeMap) {
      if (!changeMap.isEmpty()) {
         for (ClientboundSectionBlocksUpdatePacket packet : getSectionUpdatePackets(changeMap)) {
            for (AbstractPlayer player : audience) {
               ServerPlayer nmsPlayer = ((CraftPlayer)BukkitAdapter.adapt(player)).getHandle();
               nmsPlayer.connection.send(packet);
            }
         }
      }
   }

   public static Collection<ClientboundSectionBlocksUpdatePacket> getSectionUpdatePackets(Map<AbstractLocation, AbstractBlock> changeMap) {
      Map<SectionPos, Map<Short, BlockState>> blocksMap = Maps.newLinkedHashMap();

      for (Entry<AbstractLocation, AbstractBlock> entry : changeMap.entrySet()) {
         AbstractLocation location = entry.getKey();
         ServerLevel nmsWorld = ((CraftWorld)BukkitAdapter.adapt(location.getWorld())).getHandle();
         AbstractBlock aBlockData = entry.getValue();
         BlockPos blockPosition = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
         Map<Short, BlockState> sectionBlocks = blocksMap.computeIfAbsent(SectionPos.of(blockPosition), sectionx -> Maps.newLinkedHashMap());
         BlockState blockState;
         if (aBlockData == null) {
            blockState = nmsWorld.getBlockState(blockPosition);
         } else {
            if (!(aBlockData instanceof BukkitBlock bukkitBlock)) {
               continue;
            }

            blockState = ((CraftBlockData)bukkitBlock.getBlockData()).getState();
         }

         sectionBlocks.put(SectionPos.sectionRelativePos(blockPosition), blockState);
      }

      Collection<ClientboundSectionBlocksUpdatePacket> packets = Lists.newArrayList();

      for (Entry<SectionPos, Map<Short, BlockState>> entry : blocksMap.entrySet()) {
         SectionPos section = entry.getKey();
         ShortArraySet shortArraySet = new ShortArraySet(entry.getValue().keySet());
         BlockState[] blockData = entry.getValue().values().toArray(new BlockState[0]);
         packets.add(new ClientboundSectionBlocksUpdatePacket(section, shortArraySet, blockData));
      }

      return packets;
   }

   @Override
   public void applyPhysics(Block target) {
      Location location = target.getLocation();
      ServerLevel world = ((CraftWorld)location.getWorld()).getHandle();
      BlockPos blockposition = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
      BlockState iblockdata = world.getBlockState(blockposition);
      net.minecraft.world.level.block.Block block = iblockdata.getBlock();
      world.updateNeighborsAt(blockposition, block);
   }

   @Override
   public void togglePowerable(AbstractLocation location) {
      this.togglePowerable(location, 0L);
   }

   @Override
   public void togglePowerable(AbstractLocation location, long duration) {
      Location l = BukkitAdapter.adapt(location);
      Block block = l.getWorld().getBlockAt(l);
      if (block.getBlockData() instanceof Powerable pbd) {
         pbd.setPowered(true);
         block.setBlockData(pbd);
         this.applyPhysics(block);
         Schedulers.of(l).runLater(() -> {
            pbd.setPowered(false);
            block.setBlockData(pbd);
            this.applyPhysics(block);
         }, duration);
      }
   }

   @Override
   public void togglePiston(AbstractLocation target) {
      Location location = BukkitAdapter.adapt(target);
      Block block = location.getBlock();
      if (block.getType() != Material.PISTON) {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Location was not piston");
      } else {
         BlockData bd = block.getBlockData();
         Piston redstone = (Piston)bd;
         boolean extended = redstone.isExtended();
         BlockFace bf = ((Directional)bd).getFacing();
         if (extended) {
            redstone.setExtended(false);
            block.setBlockData(redstone);
         }

         redstone.setExtended(true);
         block.setBlockData(redstone);
         ServerLevel world = ((CraftWorld)location.getWorld()).getHandle();
         BlockPos blockposition = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
         BlockState iblockdata = world.getBlockState(blockposition);
         boolean bold = PistonBaseBlock.isPushable(iblockdata, world, blockposition, Direction.NORTH, true, Direction.SOUTH);
         if (!bold) {
            redstone.setExtended(false);
            block.setBlockData(redstone);
         }
      }
   }

   @Override
   public void throwBlock(AbstractLocation target, Material material, AbstractVector velocity, int duration, boolean hideSourceBlock) {
      this.throwBlock(target, Bukkit.createBlockData(material).createBlockState(), velocity, duration, hideSourceBlock);
   }

   @Override
   public void throwBlock(AbstractLocation target, org.bukkit.block.BlockState blockState, AbstractVector velocity, int duration, boolean hideSourceBlock) {
      Location location = BukkitAdapter.adapt(target);
      BlockPos blockPosition = new BlockPos(target.getBlockX(), target.getBlockY(), target.getBlockZ());
      CraftWorld bukkitWorld = (CraftWorld)location.getWorld();
      ServerLevel nmsWorld = bukkitWorld.getHandle();
      BlockState nmsBlockState = ((CraftBlockState)blockState).getHandle();

      try {
         Constructor<?> ref = Reflector.getConstructor(FallingBlockEntity.class, Level.class, Double.class, Double.class, Double.class, BlockState.class);
         ref.setAccessible(true);
         FallingBlockEntity block = (FallingBlockEntity)ref.newInstance(
            nmsWorld, target.getBlockX() + 0.5, target.getBlockY() + 1, target.getBlockZ() + 0.5, nmsBlockState
         );
         List<Packet<? super ClientGamePacketListener>> sendPackets = new ArrayList<>();
         List<Packet<? super ClientGamePacketListener>> removePackets = new ArrayList<>();
         sendPackets.add(new ClientboundAddEntityPacket(block, BellBlock.getId(nmsBlockState), blockPosition));
         sendPackets.add(new ClientboundSetEntityMotionPacket(block.getId(), new Vec3(velocity.getX(), velocity.getY(), velocity.getZ())));
         removePackets.add(new ClientboundRemoveEntitiesPacket(new int[]{block.getId()}));
         if (hideSourceBlock) {
            BlockState airBlockState = ((CraftBlockData)Material.BARRIER.createBlockData()).getState();
            sendPackets.add(new ClientboundBlockUpdatePacket(blockPosition, airBlockState));
            removePackets.add(new ClientboundBlockUpdatePacket(blockPosition, nmsWorld.getBlockState(blockPosition)));
            BlockEntity blockEntity = nmsWorld.getBlockEntity(blockPosition);
            Packet<ClientGamePacketListener> blockEntityPacket = blockEntity == null ? null : blockEntity.getUpdatePacket();
            if (blockEntityPacket != null) {
               removePackets.add(blockEntityPacket);
            }
         }

         ClientboundBundlePacket addBundle = new ClientboundBundlePacket(sendPackets);
         ClientboundBundlePacket removeBundle = new ClientboundBundlePacket(removePackets);

         for (ServerPlayer p : nmsWorld.getPlayers(sp -> sp.level().equals(nmsWorld))) {
            p.connection.send(addBundle);
            Schedulers.async().runLater(() -> p.connection.send(removeBundle), duration);
         }
      } catch (Error | Exception var19) {
         var19.printStackTrace();
      }
   }

   @Override
   public boolean pushBlock(AbstractLocation location, AbstractDirection direction) {
      ServerLevel world = ((CraftWorld)BukkitAdapter.adapt(location).getWorld()).getHandle();
      Direction movingDirection = Direction.valueOf(direction.toString());
      BlockPos blockMovedPosition = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
      BlockPos blockMovingPosition = blockMovedPosition.relative(movingDirection.getOpposite());
      BlockPos blockFakeHeadPosition = blockMovingPosition.relative(Direction.NORTH);
      BlockState blockMovedState = world.getBlockState(blockMovedPosition);
      BlockState blockMovingState = world.getBlockState(blockMovingPosition);
      BlockState fakeHeadState = world.getBlockState(blockFakeHeadPosition);
      BlockState pistonState = (BlockState)Blocks.PISTON.defaultBlockState().setValue(BlockStateProperties.FACING, movingDirection);
      BlockState airState = Blocks.AIR.defaultBlockState();
      ClientboundBlockUpdatePacket packetBlock = new ClientboundBlockUpdatePacket(blockMovingPosition, pistonState);
      ClientboundBlockEventPacket packetPush = new ClientboundBlockEventPacket(blockMovingPosition, blockMovingState.getBlock(), 0, movingDirection.ordinal());
      new ClientboundBlockEventPacket(blockMovingPosition, blockMovingState.getBlock(), 2, movingDirection.ordinal());
      ClientboundBlockUpdatePacket packetBlockBye1 = new ClientboundBlockUpdatePacket(blockMovingPosition, blockMovingState);
      ClientboundBlockUpdatePacket packetBlockBye2 = new ClientboundBlockUpdatePacket(blockMovedPosition, airState);
      ClientboundBlockUpdatePacket packetBlockBye3 = new ClientboundBlockUpdatePacket(blockFakeHeadPosition, fakeHeadState);
      ClientboundBundlePacket bundle = new ClientboundBundlePacket(List.of(packetBlock, packetPush, packetBlockBye1, packetBlockBye2, packetBlockBye3));
      Schedulers.ensureAsync(() -> {
         for (Player player : Players.all()) {
            ((CraftPlayer)player).getHandle().connection.send(bundle);
         }
      });
      return this.moveBlocks(world, blockMovingPosition, movingDirection, true);
   }

   private boolean moveBlocks(Level world, BlockPos blockposition, Direction direction, boolean isExtending) {
      BlockPos newBlockPosition = blockposition.relative(direction);
      PistonStructureResolver pistonStructureResolver = new PistonStructureResolver(world, blockposition, direction, isExtending);
      if (!pistonStructureResolver.resolve()) {
         return false;
      } else {
         Map<BlockPos, BlockState> blocksToPushStates = Maps.newHashMap();
         List<BlockPos> blocksToPush = pistonStructureResolver.getToPush();
         List<BlockState> blocksToPushStatesList = Lists.newArrayList();

         for (BlockPos blockPos : blocksToPush) {
            BlockState blockState = world.getBlockState(blockPos);
            blocksToPushStates.put(blockPos, blockState);
            blocksToPushStatesList.add(blockState);
         }

         List<BlockPos> blocksToDestroy = pistonStructureResolver.getToDestroy();
         BlockState[] aiblockdata = new BlockState[blocksToPush.size() + blocksToDestroy.size()];
         Direction directionMovement = isExtending ? direction : direction.getOpposite();
         final Block baseBlock = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
         final List<BlockPos> moved = pistonStructureResolver.getToPush();
         final List<BlockPos> broken = pistonStructureResolver.getToDestroy();
         BlockState blockStateAir = Blocks.AIR.defaultBlockState();
         new AbstractList<Block>() {
            @Override
            public int size() {
               return moved.size() + broken.size();
            }

            public Block get(int index) {
               if (index < this.size() && index >= 0) {
                  BlockPos pos = index < moved.size() ? moved.get(index) : broken.get(index - moved.size());
                  return baseBlock.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
               } else {
                  throw new ArrayIndexOutOfBoundsException(index);
               }
            }
         };
         int j = 0;

         for (int i = blocksToDestroy.size() - 1; i >= 0; i--) {
            BlockPos blockPos = blocksToDestroy.get(i);
            BlockState blockState = world.getBlockState(blockPos);
            world.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 18);
            if (!blockState.is(BlockTags.FIRE)) {
               world.addDestroyBlockEffect(blockPos, blockState);
            }

            aiblockdata[j++] = blockState;
         }

         for (int i = blocksToPush.size() - 1; i >= 0; i--) {
            BlockPos blockPos = blocksToPush.get(i);
            BlockPos blockPosNew = blockPos.relative(directionMovement);
            BlockState blockState = world.getBlockState(blockPos);
            BlockState movingPistonState = (BlockState)Blocks.MOVING_PISTON.defaultBlockState().setValue(BlockStateProperties.FACING, direction);
            blocksToPushStates.remove(blockPosNew);
            world.setBlock(blockPosNew, movingPistonState, 68);
            world.setBlockEntity(
               MovingPistonBlock.newMovingBlockEntity(blockPosNew, movingPistonState, blocksToPushStatesList.get(i), direction, isExtending, false)
            );
            aiblockdata[j++] = blockState;
         }

         if (isExtending) {
            PistonType blockpropertypistontype = PistonType.DEFAULT;
            BlockState iblockdata1 = (BlockState)((BlockState)Blocks.MOVING_PISTON.defaultBlockState().setValue(MovingPistonBlock.FACING, direction))
               .setValue(MovingPistonBlock.TYPE, PistonType.DEFAULT);
            blocksToPushStates.remove(newBlockPosition);
            world.setBlock(newBlockPosition, iblockdata1, 68);
            world.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(newBlockPosition, iblockdata1, blockStateAir, direction, true, true));
         }

         Iterator<BlockPos> iterator = blocksToPushStates.keySet().iterator();

         while (iterator.hasNext()) {
            world.setBlock(iterator.next(), blockStateAir, 82);
         }

         for (Entry<BlockPos, BlockState> entry : blocksToPushStates.entrySet()) {
            BlockPos blockPos = entry.getKey();
            BlockState blockState = entry.getValue();
            blockState.updateIndirectNeighbourShapes(world, blockPos, 2);
            blockStateAir.updateNeighbourShapes(world, blockPos, 2);
            blockStateAir.updateIndirectNeighbourShapes(world, blockPos, 2);
         }

         j = 0;

         for (int i = blocksToDestroy.size() - 1; i >= 0; i--) {
            BlockState blockState = aiblockdata[j++];
            BlockPos blockPos = blocksToDestroy.get(i);
            blockState.updateIndirectNeighbourShapes(world, blockPos, 2);
            world.updateNeighborsAt(blockPos, blockState.getBlock());
         }

         for (int i = blocksToPush.size() - 1; i >= 0; i--) {
            world.updateNeighborsAt(blocksToPush.get(i), aiblockdata[j++].getBlock());
         }

         return true;
      }
   }

   @Override
   public void sendBlockDamaged(Collection<AbstractPlayer> audience, AbstractLocation location, int damage) {
      BlockPos block = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
      int id = (block.getX() & 4095) << 20 | (block.getZ() & 4095) << 8 | block.getY() & 0xFF;
      VolatileBlockHandlerImpl.MythicClientboundBlockDestructionPacket packet = new VolatileBlockHandlerImpl.MythicClientboundBlockDestructionPacket(
         id, block, damage
      );

      for (AbstractPlayer player : audience) {
         ServerPlayer nmsPlayer = ((CraftPlayer)BukkitAdapter.adapt(player)).getHandle();
         nmsPlayer.connection.send(packet);
      }
   }

   public static class MythicClientboundBlockDestructionPacket extends ClientboundBlockDestructionPacket {
      public MythicClientboundBlockDestructionPacket(int id, BlockPos pos, int damage) {
         super(id, pos, damage);
      }
   }
}
