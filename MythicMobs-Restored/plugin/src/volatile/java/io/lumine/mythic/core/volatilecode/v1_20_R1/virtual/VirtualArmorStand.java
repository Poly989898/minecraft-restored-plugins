package io.lumine.mythic.core.volatilecode.v1_20_R1.virtual;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.volatilecode.virtual.PacketArmorStand;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.netty.buffer.Unpooled;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.Vector3f;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
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
      Player player = BukkitAdapter.adapt(target);
      PacketPlayOutSpawnEntity packetSpawn = this.createSpawnPacket();
      PacketPlayOutEntityMetadata packetData = this.createDataPacket(true);
      List<Pair<EnumItemSlot, ItemStack>> equipmentList = Lists.newArrayList();
      equipmentList.add(new Pair(EnumItemSlot.f, CraftItemStack.asNMSCopy(this.getWrapper().getHeadItem().get())));
      PacketPlayOutEntityEquipment packetEquipment = new PacketPlayOutEntityEquipment(this.getEntityId(), equipmentList);
      return Lists.newArrayList(new Object[]{packetSpawn, packetData, packetEquipment});
   }

   @Override
   public PacketPlayOutSpawnEntity createSpawnPacket() {
      return this.createSpawnPacket(EntityTypes.d);
   }

   @Override
   public PacketPlayOutEntityMetadata createDataPacket(boolean force) {
      if (!this.getWrapper().isDirty() && !force) {
         return null;
      } else {
         this.getWrapper().clearDirty();
         PacketDataSerializer buf = new PacketDataSerializer(Unpooled.buffer());
         buf.d(this.getEntityId());
         if (force) {
            boolean isSmall = this.getWrapper().isSmall();
            writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
            writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
            writeData(buf, 15, DataWatcherRegistry.a, Byte.valueOf((byte)(isSmall ? 23 : 16)));
         }

         this.getWrapper().getRotation().ifDirty(value -> {
            EulerAngle rot = this.getWrapper().getRotation().get();
            if (rot != null) {
               Vector3f rotation = new Vector3f((float)Math.toDegrees(rot.getX()), (float)Math.toDegrees(rot.getY()), (float)Math.toDegrees(rot.getZ()));
               writeData(buf, 16, DataWatcherRegistry.m, rotation);
            }
         }, force);
         buf.writeByte(255);
         return new PacketPlayOutEntityMetadata(buf);
      }
   }

   public Collection<Packet<PacketListenerPlayOut>> spawnAsMount() {
      PacketPlayOutSpawnEntity packetSpawn = this.createSpawnPacket();
      PacketPlayOutEntityMetadata packetData = this.createDataPacket(true);
      List<Pair<EnumItemSlot, ItemStack>> equipmentList = Lists.newArrayList();
      equipmentList.add(new Pair(EnumItemSlot.f, CraftItemStack.asNMSCopy(this.getWrapper().getHeadItem().get())));
      PacketPlayOutEntityEquipment packetEquipment = new PacketPlayOutEntityEquipment(this.getEntityId(), equipmentList);
      return Lists.newArrayList(new Packet[]{packetSpawn, packetData, packetEquipment});
   }
}
