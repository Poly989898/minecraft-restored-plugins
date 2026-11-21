package io.lumine.mythic.core.volatilecode.v1_20_R3.virtual;

import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.volatilecode.virtual.PacketItem;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.PacketPlayOutCollect;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;

public class VirtualItem extends VirtualEntity implements PacketItem.PacketItemEntityRenderer {
   public VirtualItem(PacketItem wrapper) {
      super(wrapper);
   }

   public PacketItem<VirtualItem> getWrapper() {
      return (PacketItem<VirtualItem>)super.getWrapper();
   }

   @Override
   public void collect(AbstractPlayer target) {
      Player player = BukkitAdapter.adapt(target);
      int playerId = ((CraftPlayer)player).getHandle().aj();
      PacketPlayOutCollect spawnItem = new PacketPlayOutCollect(this.getEntityId(), playerId, 1);
      Schedulers.async().run(() -> ((CraftPlayer)player).getHandle().c.b(spawnItem));
   }

   @Override
   public PacketPlayOutSpawnEntity createSpawnPacket() {
      return this.createSpawnPacket(EntityTypes.ae);
   }

   @Override
   public PacketPlayOutEntityMetadata createDataPacket(boolean force) {
      if (!this.getWrapper().isDirty() && !force) {
         return null;
      } else {
         this.getWrapper().clearDirty();
         PacketDataSerializer buf = new PacketDataSerializer(Unpooled.buffer());
         buf.c(this.getEntityId());
         if (force) {
            writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
            writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
         }

         this.getWrapper().getHasGravity().ifDirty(bool -> writeData(buf, 5, DataWatcherRegistry.k, !((Boolean)bool)), force);
         this.getWrapper().getItem().ifDirty(itemStack -> writeData(buf, 8, DataWatcherRegistry.h, CraftItemStack.asNMSCopy(itemStack)), force);
         buf.k(255);
         return new PacketPlayOutEntityMetadata(buf);
      }
   }
}
