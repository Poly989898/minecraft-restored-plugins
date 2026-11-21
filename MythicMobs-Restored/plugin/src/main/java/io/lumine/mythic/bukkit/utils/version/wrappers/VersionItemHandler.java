package io.lumine.mythic.bukkit.utils.version.wrappers;

import io.lumine.mythic.bukkit.utils.items.nbt.jnbt.CompoundTag;
import io.lumine.mythic.bukkit.utils.items.nbt.jnbt.Tag;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public abstract class VersionItemHandler {
   public abstract CompoundTag createCompoundTag(Map<String, Tag> map);

   public abstract CompoundTag getCompoundTag(ItemStack itemStack);

   public abstract ItemStack addCompoundTag(ItemStack itemStack, String str, Tag tag);

   public abstract ItemStack setCompoundTag(ItemStack itemStack, CompoundTag compoundTag);
}
