package io.lumine.mythic.core.volatilecode.v1_21_R5;

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
import io.lumine.mythic.core.volatilecode.VolatileCodeEnabled_v1_21_R5;
import it.unimi.dsi.fastutil.shorts.ShortArraySet;
import java.lang.reflect.Constructor;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.SectionPosition;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayOutBlockAction;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity;
import net.minecraft.network.protocol.game.PacketPlayOutMultiBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockBell;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.piston.BlockPiston;
import net.minecraft.world.level.block.piston.BlockPistonMoving;
import net.minecraft.world.level.block.piston.PistonExtendsChecker;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyPistonType;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Piston;
import org.bukkit.craftbukkit.v1_21_R5.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class VolatileBlockHandlerImpl implements VolatileBlockHandler {
   private final VolatileCodeEnabled_v1_21_R5 handler;

   public VolatileBlockHandlerImpl(VolatileCodeEnabled_v1_21_R5 handler) {
      this.handler = handler;
   }

   @Override
   public void sendBlockChange(Collection<AbstractPlayer> audience, AbstractLocation location, AbstractBlock block) {
      BlockPosition blockPosition = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
      IBlockData blockState;
      if (block == null) {
         WorldServer nmsWorld = ((CraftWorld)BukkitAdapter.adapt(location).getWorld()).getHandle();
         blockState = nmsWorld.a_(blockPosition);
      } else {
         if (!(block instanceof BukkitBlock bukkitBlock)) {
            return;
         }

         blockState = ((CraftBlockData)bukkitBlock.getBlockData()).getState();
      }

      PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(blockPosition, blockState);

      for (AbstractPlayer player : audience) {
         EntityPlayer nmsPlayer = ((CraftPlayer)BukkitAdapter.adapt(player)).getHandle();
         nmsPlayer.g.b(packet);
      }
   }

   @Override
   public void sendMultiBlockChange(Collection<AbstractPlayer> audience, Map<AbstractLocation, AbstractBlock> changeMap) {
      if (!changeMap.isEmpty()) {
         for (PacketPlayOutMultiBlockChange packet : getSectionUpdatePackets(changeMap)) {
            for (AbstractPlayer player : audience) {
               EntityPlayer nmsPlayer = ((CraftPlayer)BukkitAdapter.adapt(player)).getHandle();
               nmsPlayer.g.b(packet);
            }
         }
      }
   }

   public static Collection<PacketPlayOutMultiBlockChange> getSectionUpdatePackets(Map<AbstractLocation, AbstractBlock> changeMap) {
      Map<SectionPosition, Map<Short, IBlockData>> blocksMap = Maps.newLinkedHashMap();

      for (Entry<AbstractLocation, AbstractBlock> entry : changeMap.entrySet()) {
         AbstractLocation location = entry.getKey();
         WorldServer nmsWorld = ((CraftWorld)BukkitAdapter.adapt(location.getWorld())).getHandle();
         AbstractBlock aBlockData = entry.getValue();
         BlockPosition blockPosition = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
         Map<Short, IBlockData> sectionBlocks = blocksMap.computeIfAbsent(SectionPosition.a(blockPosition), sectionx -> Maps.newLinkedHashMap());
         IBlockData blockState;
         if (aBlockData == null) {
            blockState = nmsWorld.a_(blockPosition);
         } else {
            if (!(aBlockData instanceof BukkitBlock bukkitBlock)) {
               continue;
            }

            blockState = ((CraftBlockData)bukkitBlock.getBlockData()).getState();
         }

         sectionBlocks.put(SectionPosition.b(blockPosition), blockState);
      }

      Collection<PacketPlayOutMultiBlockChange> packets = Lists.newArrayList();

      for (Entry<SectionPosition, Map<Short, IBlockData>> entry : blocksMap.entrySet()) {
         SectionPosition section = entry.getKey();
         ShortArraySet shortArraySet = new ShortArraySet(entry.getValue().keySet());
         IBlockData[] blockData = entry.getValue().values().toArray(new IBlockData[0]);
         packets.add(new PacketPlayOutMultiBlockChange(section, shortArraySet, blockData));
      }

      return packets;
   }

   @Override
   public void applyPhysics(Block target) {
      Location location = target.getLocation();
      WorldServer world = ((CraftWorld)location.getWorld()).getHandle();
      BlockPosition blockposition = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
      IBlockData iblockdata = world.a_(blockposition);
      net.minecraft.world.level.block.Block block = iblockdata.b();
      world.a(blockposition, block);
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
         WorldServer world = ((CraftWorld)location.getWorld()).getHandle();
         BlockPosition blockposition = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
         IBlockData iblockdata = world.a_(blockposition);
         boolean bold = BlockPiston.a(iblockdata, world, blockposition, EnumDirection.c, true, EnumDirection.d);
         if (!bold) {
            redstone.setExtended(false);
            block.setBlockData(redstone);
         }
      }
   }

   @Override
   public void throwBlock(AbstractLocation target, Material material, AbstractVector velocity, int duration, boolean hideSourceBlock) {
      Location location = BukkitAdapter.adapt(target);
      BlockPosition blockPosition = new BlockPosition(target.getBlockX(), target.getBlockY(), target.getBlockZ());
      CraftWorld bukkitWorld = (CraftWorld)location.getWorld();
      WorldServer nmsWorld = bukkitWorld.getHandle();
      IBlockData blockState;
      if (material == null) {
         blockState = nmsWorld.a_(blockPosition);
      } else {
         blockState = ((CraftBlockData)Bukkit.createBlockData(material)).getState();
      }

      try {
         Constructor<?> ref = Reflector.getConstructor(EntityFallingBlock.class, World.class, Double.class, Double.class, Double.class, IBlockData.class);
         ref.setAccessible(true);
         EntityFallingBlock block = (EntityFallingBlock)ref.newInstance(
            nmsWorld, target.getBlockX() + 0.5, target.getBlockY() + 1, target.getBlockZ() + 0.5, blockState
         );
         List<Packet<? super PacketListenerPlayOut>> sendPackets = new ArrayList<>();
         List<Packet<? super PacketListenerPlayOut>> removePackets = new ArrayList<>();
         sendPackets.add(new PacketPlayOutSpawnEntity(block, BlockBell.j(blockState), blockPosition));
         sendPackets.add(new PacketPlayOutEntityVelocity(block.ar(), new Vec3D(velocity.getX(), velocity.getY(), velocity.getZ())));
         removePackets.add(new PacketPlayOutEntityDestroy(new int[]{block.ar()}));
         if (hideSourceBlock) {
            IBlockData airBlockState = ((CraftBlockData)Material.BARRIER.createBlockData()).getState();
            sendPackets.add(new PacketPlayOutBlockChange(blockPosition, airBlockState));
            removePackets.add(new PacketPlayOutBlockChange(blockPosition, nmsWorld.a_(blockPosition)));
            TileEntity blockEntity = nmsWorld.c_(blockPosition);
            Packet<PacketListenerPlayOut> blockEntityPacket = blockEntity == null ? null : blockEntity.ax_();
            if (blockEntityPacket != null) {
               removePackets.add(blockEntityPacket);
            }
         }

         ClientboundBundlePacket addBundle = new ClientboundBundlePacket(sendPackets);
         ClientboundBundlePacket removeBundle = new ClientboundBundlePacket(removePackets);

         for (EntityPlayer p : nmsWorld.a(sp -> sp.y().equals(nmsWorld))) {
            p.g.b(addBundle);
            Schedulers.async().runLater(() -> p.g.b(removeBundle), duration);
         }
      } catch (Error | Exception var19) {
         var19.printStackTrace();
      }
   }

   @Override
   public boolean pushBlock(AbstractLocation location, AbstractDirection direction) {
      WorldServer world = ((CraftWorld)BukkitAdapter.adapt(location).getWorld()).getHandle();
      EnumDirection movingDirection = EnumDirection.valueOf(direction.toString());
      BlockPosition blockMovedPosition = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
      BlockPosition blockMovingPosition = blockMovedPosition.a(movingDirection.g());
      BlockPosition blockFakeHeadPosition = blockMovingPosition.a(EnumDirection.c);
      IBlockData blockMovedState = world.a_(blockMovedPosition);
      IBlockData blockMovingState = world.a_(blockMovingPosition);
      IBlockData fakeHeadState = world.a_(blockFakeHeadPosition);
      IBlockData pistonState = (IBlockData)Blocks.bI.m().b(BlockProperties.R, movingDirection);
      IBlockData airState = Blocks.a.m();
      PacketPlayOutBlockChange packetBlock = new PacketPlayOutBlockChange(blockMovingPosition, pistonState);
      PacketPlayOutBlockAction packetPush = new PacketPlayOutBlockAction(blockMovingPosition, blockMovingState.b(), 0, movingDirection.ordinal());
      new PacketPlayOutBlockAction(blockMovingPosition, blockMovingState.b(), 2, movingDirection.ordinal());
      PacketPlayOutBlockChange packetBlockBye1 = new PacketPlayOutBlockChange(blockMovingPosition, blockMovingState);
      PacketPlayOutBlockChange packetBlockBye2 = new PacketPlayOutBlockChange(blockMovedPosition, airState);
      PacketPlayOutBlockChange packetBlockBye3 = new PacketPlayOutBlockChange(blockFakeHeadPosition, fakeHeadState);
      ClientboundBundlePacket bundle = new ClientboundBundlePacket(List.of(packetBlock, packetPush, packetBlockBye1, packetBlockBye2, packetBlockBye3));
      Schedulers.ensureAsync(() -> {
         for (Player player : Players.all()) {
            ((CraftPlayer)player).getHandle().g.b(bundle);
         }
      });
      return this.moveBlocks(world, blockMovingPosition, movingDirection, true);
   }

   private boolean moveBlocks(World world, BlockPosition blockposition, EnumDirection direction, boolean isExtending) {
      BlockPosition newBlockPosition = blockposition.a(direction);
      PistonExtendsChecker pistonStructureResolver = new PistonExtendsChecker(world, blockposition, direction, isExtending);
      if (!pistonStructureResolver.a()) {
         return false;
      } else {
         Map<BlockPosition, IBlockData> blocksToPushStates = Maps.newHashMap();
         List<BlockPosition> blocksToPush = pistonStructureResolver.c();
         List<IBlockData> blocksToPushStatesList = Lists.newArrayList();

         for (BlockPosition blockPos : blocksToPush) {
            IBlockData blockState = world.a_(blockPos);
            blocksToPushStates.put(blockPos, blockState);
            blocksToPushStatesList.add(blockState);
         }

         List<BlockPosition> blocksToDestroy = pistonStructureResolver.d();
         IBlockData[] aiblockdata = new IBlockData[blocksToPush.size() + blocksToDestroy.size()];
         EnumDirection directionMovement = isExtending ? direction : direction.g();
         final Block baseBlock = world.getWorld().getBlockAt(blockposition.u(), blockposition.v(), blockposition.w());
         final List<BlockPosition> moved = pistonStructureResolver.c();
         final List<BlockPosition> broken = pistonStructureResolver.d();
         IBlockData blockStateAir = Blocks.a.m();
         new AbstractList<Block>() {
            @Override
            public int size() {
               return moved.size() + broken.size();
            }

            public Block get(int index) {
               if (index < this.size() && index >= 0) {
                  BlockPosition pos = index < moved.size() ? moved.get(index) : broken.get(index - moved.size());
                  return baseBlock.getWorld().getBlockAt(pos.u(), pos.v(), pos.w());
               } else {
                  throw new ArrayIndexOutOfBoundsException(index);
               }
            }
         };
         int j = 0;

         for (int i = blocksToDestroy.size() - 1; i >= 0; i--) {
            BlockPosition blockPos = blocksToDestroy.get(i);
            IBlockData blockState = world.a_(blockPos);
            world.a(blockPos, Blocks.a.m(), 18);
            if (!blockState.a(TagsBlock.aQ)) {
               world.b(blockPos, blockState);
            }

            aiblockdata[j++] = blockState;
         }

         for (int i = blocksToPush.size() - 1; i >= 0; i--) {
            BlockPosition blockPos = blocksToPush.get(i);
            BlockPosition blockPosNew = blockPos.a(directionMovement);
            IBlockData blockState = world.a_(blockPos);
            IBlockData movingPistonState = (IBlockData)Blocks.ca.m().b(BlockProperties.R, direction);
            blocksToPushStates.remove(blockPosNew);
            world.a(blockPosNew, movingPistonState, 68);
            world.a(BlockPistonMoving.a(blockPosNew, movingPistonState, blocksToPushStatesList.get(i), direction, isExtending, false));
            aiblockdata[j++] = blockState;
         }

         if (isExtending) {
            BlockPropertyPistonType blockpropertypistontype = BlockPropertyPistonType.a;
            IBlockData iblockdata1 = (IBlockData)((IBlockData)Blocks.ca.m().b(BlockPistonMoving.b, direction))
               .b(BlockPistonMoving.c, BlockPropertyPistonType.a);
            blocksToPushStates.remove(newBlockPosition);
            world.a(newBlockPosition, iblockdata1, 68);
            world.a(BlockPistonMoving.a(newBlockPosition, iblockdata1, blockStateAir, direction, true, true));
         }

         Iterator<BlockPosition> iterator = blocksToPushStates.keySet().iterator();

         while (iterator.hasNext()) {
            world.a(iterator.next(), blockStateAir, 82);
         }

         for (Entry<BlockPosition, IBlockData> entry : blocksToPushStates.entrySet()) {
            BlockPosition blockPos = entry.getKey();
            IBlockData blockState = entry.getValue();
            blockState.b(world, blockPos, 2);
            blockStateAir.a(world, blockPos, 2);
            blockStateAir.b(world, blockPos, 2);
         }

         j = 0;

         for (int i = blocksToDestroy.size() - 1; i >= 0; i--) {
            IBlockData blockState = aiblockdata[j++];
            BlockPosition blockPos = blocksToDestroy.get(i);
            blockState.b(world, blockPos, 2);
            world.a(blockPos, blockState.b());
         }

         for (int i = blocksToPush.size() - 1; i >= 0; i--) {
            world.a(blocksToPush.get(i), aiblockdata[j++].b());
         }

         return true;
      }
   }

   @Override
   public void sendBlockDamaged(Collection<AbstractPlayer> audience, AbstractLocation location, int damage) {
      BlockPosition block = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
      int id = (block.u() & 4095) << 20 | (block.w() & 4095) << 8 | block.v() & 0xFF;
      VolatileBlockHandlerImpl.MythicClientboundBlockDestructionPacket packet = new VolatileBlockHandlerImpl.MythicClientboundBlockDestructionPacket(
         id, block, damage
      );

      for (AbstractPlayer player : audience) {
         EntityPlayer nmsPlayer = ((CraftPlayer)BukkitAdapter.adapt(player)).getHandle();
         nmsPlayer.g.b(packet);
      }
   }

   public static class MythicClientboundBlockDestructionPacket extends PacketPlayOutBlockBreakAnimation {
      public MythicClientboundBlockDestructionPacket(int id, BlockPosition pos, int damage) {
         super(id, pos, damage);
      }
   }
}
