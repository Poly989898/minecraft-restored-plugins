package it.ytnoos.lpx.utils;

import com.github.retrooper.packetevents.protocol.item.HashedStack;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import java.util.Optional;

public class ItemUtils {

   private ItemUtils() {
      throw new UnsupportedOperationException();
   }

   public static Optional getDisplayName(ItemStack itemStack) {
      NBTCompound nbtCompound = itemStack.getNBT();
      if (nbtCompound == null) {
         return Optional.empty();
      }

      NBTCompound nbtCompound2 = nbtCompound.getCompoundTagOrNull("display");
      return nbtCompound2 == null ? Optional.empty() : Optional.ofNullable(nbtCompound2.getStringTagValueOrNull("Name"));
   }

   public static Optional getLore(ItemStack itemStack) {
      NBTCompound nbtCompound = itemStack.getNBT();
      if (nbtCompound == null) {
         return Optional.empty();
      }

      NBTCompound nbtCompound2 = nbtCompound.getCompoundTagOrNull("display");
      return nbtCompound2 == null ? Optional.empty() : Optional.ofNullable(nbtCompound2.getStringListTagOrNull("Lore"));
   }

   public static boolean isEmpty(ItemStack itemStack) {
      boolean flag = itemStack.getComponents().getPatches().isEmpty();
      NBTCompound nbtCompound = itemStack.getNBT();
      boolean flag2 = nbtCompound == null || nbtCompound.isEmpty();
      return flag && flag2;
   }

   public static boolean isEmpty(HashedStack hashedStack) {
      return hashedStack.getComponents().getAddedComponents().isEmpty();
   }
}
