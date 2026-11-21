package io.lumine.mythic.bukkit.utils.version.wrappers.v1_19_R2;

import io.lumine.mythic.bukkit.utils.items.nbt.jnbt.CompoundTag;
import io.lumine.mythic.bukkit.utils.items.nbt.jnbt.Tag;
import io.lumine.mythic.bukkit.utils.reflection.Reflector;
import io.lumine.mythic.bukkit.utils.version.wrappers.VersionItemHandler;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class VolatileItemHandler extends VersionItemHandler {
   private Reflector refItemStack = new Reflector<>(CraftItemStack.class, "handle");

   @Override
   public CompoundTag createCompoundTag(Map<String, Tag> value) {
      return new VolatileCompoundTag(value);
   }

   @Override
   public CompoundTag getCompoundTag(ItemStack itemStack) {
      net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
      return (CompoundTag)(nmsItemStack != null && nmsItemStack.t()
         ? VolatileCompoundTag.fromNMSTag(nmsItemStack.u())
         : new VolatileCompoundTag(new HashMap<>()));
   }

   @Override
   public ItemStack addCompoundTag(ItemStack itemStack, String key, Tag value) {
      net.minecraft.world.item.ItemStack nmsItemStack = (net.minecraft.world.item.ItemStack)this.refItemStack.get((CraftItemStack)itemStack, "handle");
      NBTTagCompound tag = nmsItemStack.t() ? nmsItemStack.u() : new NBTTagCompound();
      CompoundTag compound = VolatileCompoundTag.fromNMSTag(tag).createBuilder().put(key, value).build();
      nmsItemStack.c(((VolatileCompoundTag)compound).toNMSTag());
      return itemStack;
   }

   @Override
   public ItemStack setCompoundTag(ItemStack itemStack, CompoundTag compoundTag) {
      net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
      nmsItemStack.c(((VolatileCompoundTag)compoundTag).toNMSTag());
      return CraftItemStack.asBukkitCopy(nmsItemStack);
   }
}
