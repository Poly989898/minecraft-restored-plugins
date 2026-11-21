package io.lumine.mythic.core.volatilecode.v26_2.virtual;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.volatilecode.virtual.PacketItem;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import java.util.List;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
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
      int playerId = ((CraftPlayer)player).getHandle().getId();
      ClientboundTakeItemEntityPacket spawnItem = new ClientboundTakeItemEntityPacket(this.getEntityId(), playerId, 1);
      Schedulers.async().run(() -> ((CraftPlayer)player).getHandle().connection.send(spawnItem));
   }

   @Override
   public ClientboundAddEntityPacket createSpawnPacket() {
      return this.createSpawnPacket(EntityTypes.ITEM);
   }

   @Override
   public ClientboundSetEntityDataPacket createDataPacket(boolean force) {
      if (!this.getWrapper().isDirty() && !force) {
         return null;
      } else {
         this.getWrapper().clearDirty();
         List<DataValue<?>> buf = Lists.newArrayList();
         if (force) {
            writeData(buf, 0, EntityDataSerializers.BYTE, (byte)32);
            writeData(buf, 1, EntityDataSerializers.INT, Integer.MAX_VALUE);
         }

         this.getWrapper().getHasGravity().ifDirty(bool -> writeData(buf, 5, EntityDataSerializers.BOOLEAN, !((Boolean)bool)), force);
         this.getWrapper().getItem().ifDirty(itemStack -> writeData(buf, 8, EntityDataSerializers.ITEM_STACK, CraftItemStack.asNMSCopy(itemStack)), force);
         return new ClientboundSetEntityDataPacket(this.getEntityId(), buf);
      }
   }
}
