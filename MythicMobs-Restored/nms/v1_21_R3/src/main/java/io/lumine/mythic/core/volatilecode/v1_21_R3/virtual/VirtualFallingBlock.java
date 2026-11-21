package io.lumine.mythic.core.volatilecode.v1_21_R3.virtual;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.volatilecode.virtual.PacketFallingBlock;
import io.lumine.mythic.bukkit.BukkitAdapter;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.block.BlockBell;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.craftbukkit.v1_21_R3.block.data.CraftBlockData;
import org.bukkit.entity.Player;
import org.joml.Vector3dc;

public class VirtualFallingBlock extends VirtualEntity implements PacketFallingBlock.PacketFallingBlockEntityRenderer {
   public VirtualFallingBlock(PacketFallingBlock wrapper) {
      super(wrapper);
   }

   public PacketFallingBlock<VirtualFallingBlock> getWrapper() {
      return (PacketFallingBlock<VirtualFallingBlock>)super.getWrapper();
   }

   @Override
   public void spawn(AbstractPlayer target) {
      Player player = BukkitAdapter.adapt(target);
      PacketPlayOutSpawnEntity packetSpawn = this.createSpawnPacket();
      this.sendPacket(new Object[]{this.createSpawnPacket()});
   }

   @Override
   public void update() {
      if (this.getWrapper().getLocation().isDirty()) {
         AbstractLocation prev = this.getWrapper().getPreviousLocation();
         Vector3dc location = (Vector3dc)this.getWrapper().getLocation().get();
         double currentX = prev.getX();
         double currentY = prev.getY();
         double currentZ = prev.getZ();
         double newX = location.x();
         double newY = location.y();
         double newZ = location.z();
         double distance = Math.pow(newX - currentX, 2.0) + Math.pow(newY - currentY, 2.0) + Math.pow(newZ - currentZ, 2.0);
         if (distance < 1.0) {
            AbstractVector currentVector = new AbstractVector(currentX, currentY, currentZ);
            AbstractVector newVector = new AbstractVector(newX, newY, newZ);
            AbstractVector velocity = newVector.subtract(currentVector).multiply(1);
            PacketPlayOutEntityVelocity packetV = new PacketPlayOutEntityVelocity(this.entityId, new Vec3D(velocity.getX(), velocity.getY(), velocity.getZ()));
            this.sendPacket(new Object[]{packetV});
         } else {
            PacketPlayOutEntityTeleport packetTeleport = this.createTeleportPacket(newX, newY, newZ, 0.0F, 0.0F);
            this.sendPacket(new Object[]{packetTeleport});
         }
      }
   }

   @Override
   public PacketPlayOutSpawnEntity createSpawnPacket() {
      Vector3dc location = (Vector3dc)this.getWrapper().getLocation().get();
      IBlockData blockState = ((CraftBlockData)this.getWrapper().getBlockData().get()).getState();
      return new PacketPlayOutSpawnEntity(
         this.getEntityId(), this.getUniqueId(), location.x(), location.y(), location.z(), 0.0F, 0.0F, EntityTypes.X, BlockBell.j(blockState), Vec3D.c, 0.0
      );
   }

   @Override
   public PacketPlayOutEntityMetadata createDataPacket(boolean force) {
      return null;
   }
}
