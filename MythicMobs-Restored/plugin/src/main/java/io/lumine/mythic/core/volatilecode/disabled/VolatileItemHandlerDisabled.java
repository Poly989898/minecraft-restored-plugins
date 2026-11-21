package io.lumine.mythic.core.volatilecode.disabled;

import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.Tag;
import org.bukkit.inventory.ItemStack;

public interface VolatileItemHandlerDisabled {
   ItemStack addNBTData(ItemStack itemStack, String str, Tag tag);

   CompoundTag getNBTData(ItemStack itemStack);

   ItemStack setNBTData(ItemStack itemStack, CompoundTag compoundTag);

   void destroyItem(ItemStack itemStack);
}
