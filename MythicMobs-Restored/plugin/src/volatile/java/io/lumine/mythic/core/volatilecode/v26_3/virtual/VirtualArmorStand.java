package io.lumine.mythic.core.volatilecode.v26_3.virtual;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.volatilecode.virtual.PacketArmorStand;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.Rotations;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.util.EulerAngle;

public class VirtualArmorStand extends VirtualEntity implements PacketArmorStand.PacketArmorStandEntityRenderer {
   public VirtualArmorStand(PacketArmorStand wrapper) {
      super(wrapper);
   }

   public PacketArmorStand<VirtualArmorStand> getWrapper() {
      return (PacketArmorStand<VirtualArmorStand>)super.getWrapper();
   }

   @Override
   public Collection<Object> getSpawnPackets(AbstractPlayer target) {
      ClientboundAddEntityPacket packetSpawn = this.createSpawnPacket();
      ClientboundSetEntityDataPacket packetData = this.createDataPacket(true);
      List<Pair<EquipmentSlot, ItemStack>> equipmentList = Lists.newArrayList();
      equipmentList.add(new Pair(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(this.getWrapper().getHeadItem().get())));
      ClientboundSetEquipmentPacket packetEquipment = new ClientboundSetEquipmentPacket(this.getEntityId(), equipmentList);
      return Lists.newArrayList(new Object[]{packetSpawn, packetData, packetEquipment});
   }

   @Override
   public ClientboundAddEntityPacket createSpawnPacket() {
      return this.createSpawnPacket(EntityTypes.ARMOR_STAND);
   }

   @Override
   public ClientboundSetEntityDataPacket createDataPacket(boolean force) {
      if (!this.getWrapper().isDirty() && !force) {
         return null;
      } else {
         this.getWrapper().clearDirty();
         List<DataValue<?>> buf = Lists.newArrayList();
         if (force) {
            boolean isSmall = this.getWrapper().isSmall();
            writeData(buf, 0, EntityDataSerializers.BYTE, (byte)32);
            writeData(buf, 1, EntityDataSerializers.INT, Integer.MAX_VALUE);
            writeData(buf, 15, EntityDataSerializers.BYTE, Byte.valueOf((byte)(isSmall ? 23 : 16)));
         }

         this.getWrapper().getRotation().ifDirty(value -> {
            EulerAngle rot = this.getWrapper().getRotation().get();
            if (rot != null) {
               Rotations rotation = new Rotations((float)Math.toDegrees(rot.getX()), (float)Math.toDegrees(rot.getY()), (float)Math.toDegrees(rot.getZ()));
               writeData(buf, 16, EntityDataSerializers.ROTATIONS, rotation);
            }
         }, force);
         return new ClientboundSetEntityDataPacket(this.getEntityId(), buf);
      }
   }

   public Collection<Packet<ClientGamePacketListener>> spawnAsMount() {
      ClientboundAddEntityPacket packetSpawn = this.createSpawnPacket();
      ClientboundSetEntityDataPacket packetData = this.createDataPacket(true);
      List<Pair<EquipmentSlot, ItemStack>> equipmentList = Lists.newArrayList();
      equipmentList.add(new Pair(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(this.getWrapper().getHeadItem().get())));
      ClientboundSetEquipmentPacket packetEquipment = new ClientboundSetEquipmentPacket(this.getEntityId(), equipmentList);
      return Lists.newArrayList(new Packet[]{packetSpawn, packetData, packetEquipment});
   }
}
