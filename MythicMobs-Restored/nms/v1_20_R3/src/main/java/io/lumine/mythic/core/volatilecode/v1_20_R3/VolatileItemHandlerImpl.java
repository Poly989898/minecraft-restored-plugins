package io.lumine.mythic.core.volatilecode.v1_20_R3;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileItemHandler;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.reflection.Reflector;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.Tag;
import java.util.HashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutCollect;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftTrident;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;

public class VolatileItemHandlerImpl implements VolatileItemHandler {
   private Reflector refItemStack = new Reflector<>(CraftItemStack.class, "handle");

   public VolatileItemHandlerImpl(VolatileCodeHandler handler) {
   }

   private ItemStack getNMSItemStack(org.bukkit.inventory.ItemStack itemStack) {
      ItemStack nmsItemStack;
      if (itemStack instanceof CraftItemStack craftItemStack) {
         nmsItemStack = (ItemStack)this.refItemStack.get((CraftItemStack)itemStack, "handle");
      } else {
         nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
      }

      return nmsItemStack;
   }

   @Override
   public void destroyItem(org.bukkit.inventory.ItemStack itemStack) {
      if (itemStack instanceof CraftItemStack craftItemStack) {
         ItemStack realStack = (ItemStack)this.refItemStack.get(craftItemStack, "handle");
         if (realStack != null) {
            realStack.f(0);
         }
      }
   }

   @Override
   public org.bukkit.inventory.ItemStack addNBTData(org.bukkit.inventory.ItemStack itemStack, String key, Tag value) {
      ItemStack nmsItemStack = this.getNMSItemStack(itemStack);
      NBTTagCompound tag = nmsItemStack.u() ? nmsItemStack.v() : new NBTTagCompound();
      CompoundTag compound = VolatileCompoundTag.fromNMSTag(tag).createBuilder().put(key, value).build();
      nmsItemStack.c(((VolatileCompoundTag)compound).toNMSTag());
      return itemStack instanceof CraftItemStack craftItemStack ? itemStack : CraftItemStack.asBukkitCopy(nmsItemStack);
   }

   @Override
   public CompoundTag getNBTData(org.bukkit.inventory.ItemStack itemStack) {
      ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
      return (CompoundTag)(nmsItemStack != null && nmsItemStack.u()
         ? VolatileCompoundTag.fromNMSTag(nmsItemStack.v())
         : new VolatileCompoundTag(new HashMap<>()));
   }

   @Override
   public org.bukkit.inventory.ItemStack setNBTData(org.bukkit.inventory.ItemStack itemStack, CompoundTag compoundTag) {
      ItemStack nmsItemStack = this.getNMSItemStack(itemStack);
      nmsItemStack.c(((VolatileCompoundTag)compoundTag).toNMSTag());
      return itemStack instanceof CraftItemStack craftItemStack ? itemStack : CraftItemStack.asBukkitCopy(nmsItemStack);
   }

   @Override
   public org.bukkit.inventory.ItemStack setNBTData(org.bukkit.inventory.ItemStack itemStack, CompoundTag compoundTag, PlaceholderMeta meta) {
      ItemStack nmsItemStack = this.getNMSItemStack(itemStack);
      nmsItemStack.c(((VolatileCompoundTag)compoundTag).toNMSTag(meta));
      return itemStack instanceof CraftItemStack craftItemStack ? itemStack : CraftItemStack.asBukkitCopy(nmsItemStack);
   }

   @Override
   public String dumpNBTData(org.bukkit.inventory.ItemStack itemStack) {
      ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
      return nmsItemStack.u() ? nmsItemStack.v().toString() : "";
   }

   @Override
   public int spawnFakeItem(Player player, org.bukkit.inventory.ItemStack itemStack, AbstractLocation location) {
      EntityItem item = new EntityItem(
         ((CraftWorld)BukkitAdapter.adapt(location.getWorld())).getHandle(),
         location.getX(),
         location.getY(),
         location.getZ(),
         CraftItemStack.asNMSCopy(itemStack)
      );
      item.a(location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F);
      PacketPlayOutSpawnEntity spawnItem = new PacketPlayOutSpawnEntity(item);
      PacketPlayOutEntityMetadata data = new PacketPlayOutEntityMetadata(item.aj(), item.an().c());
      Schedulers.async().run(() -> {
         ((CraftPlayer)player).getHandle().c.b(spawnItem);
         ((CraftPlayer)player).getHandle().c.b(data);
      });
      return item.aj();
   }

   @Override
   public void collectFakeItem(Player player, int id) {
      int playerID = ((CraftPlayer)player).getHandle().aj();
      PacketPlayOutCollect spawnItem = new PacketPlayOutCollect(id, playerID, 1);
      Schedulers.async().run(() -> ((CraftPlayer)player).getHandle().c.b(spawnItem));
   }

   public void updateFakeItem(Player player, int id, org.bukkit.inventory.ItemStack itemStack, AbstractLocation location) {
      EntityItem item = new EntityItem(
         ((CraftWorld)location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ(), CraftItemStack.asNMSCopy(itemStack)
      );
      item.a(location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F);
      PacketPlayOutEntityMetadata data = new PacketPlayOutEntityMetadata(item.aj(), item.an().c());
      Schedulers.async().run(() -> ((CraftPlayer)player).getHandle().c.b(data));
   }

   @Override
   public void destroyFakeItem(Player player, int id) {
      int playerID = ((CraftPlayer)player).getHandle().aj();
      PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(new int[]{id});
      Schedulers.async().run(() -> ((CraftPlayer)player).getHandle().c.b(destroyPacket));
   }

   public void playTotemEffect(AbstractPlayer player, int modelId) {
   }

   @Override
   public float getItemRecharge(Player player) {
      EntityPlayer ep = ((CraftPlayer)player).getHandle();
      return ep.B(0.0F);
   }

   @Override
   public boolean getItemRecharging(Player player) {
      EntityPlayer ep = ((CraftPlayer)player).getHandle();
      return ep.B(0.0F) < 1.0F;
   }

   @Override
   public void resetItemRecharge(Player player) {
      EntityPlayer ep = ((CraftPlayer)player).getHandle();
      ep.gm();
   }

   @Override
   public void setTridentItem(Trident entity, org.bukkit.inventory.ItemStack item) {
      ((CraftTrident)entity).setItem(item);
   }
}
