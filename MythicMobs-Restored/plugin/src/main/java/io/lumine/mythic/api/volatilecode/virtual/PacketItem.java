package io.lumine.mythic.api.volatilecode.virtual;

import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.bukkit.utils.cache.DataTracker;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class PacketItem<T extends PacketEntityRenderer & PacketItem.PacketItemEntityRenderer> extends PacketEntity {
   public DataTracker<ItemStack> item = DataTracker.of(new ItemStack(Material.AIR));

   public PacketItem(AbstractLocation location) {
      this(MythicProvider.get(), location);
   }

   public PacketItem(MythicPlugin plugin, AbstractLocation location) {
      super(location);
      this.setRenderer((T)plugin.getVolatileCodeHandler().getWorldHandler().createVirtualItemRenderer(this));
   }

   public void setItem(ItemStack item) {
      if (this.item.set(item)) {
         this.setDirty();
      }
   }

   public T getRenderer() {
      return (T)super.getRenderer();
   }

   public void collect(AbstractPlayer player) {
      this.getRenderer().collect(player);
   }

   public interface PacketItemEntityRenderer extends IPacketEntityRenderer {
      void collect(AbstractPlayer abstractPlayer);
   }
}
