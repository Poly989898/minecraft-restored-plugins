package com.nexomc.libs.morepersistentdatatypes.datatypes;

import com.nexomc.libs.morepersistentdatatypes.DataType;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class BlockDataArrayDataType implements PersistentDataType<byte[], BlockData[]> {
   @NotNull
   public Class<byte[]> getPrimitiveType() {
      return byte[].class;
   }

   @NotNull
   public Class<BlockData[]> getComplexType() {
      return BlockData[].class;
   }

   @NotNull
   public byte[] toPrimitive(@NotNull BlockData[] blockData, @NotNull PersistentDataAdapterContext context) {
      return (byte[])DataType.STRING_ARRAY.toPrimitive((String[])Arrays.stream(blockData).map(BlockData::getAsString).toArray((x$0) -> new String[x$0]), context);
   }

   @NotNull
   public BlockData[] fromPrimitive(@NotNull byte[] bytes, @NotNull PersistentDataAdapterContext context) {
      return (BlockData[])Arrays.stream((String[])DataType.STRING_ARRAY.fromPrimitive(bytes, context)).map(Bukkit::createBlockData).toArray((x$0) -> new BlockData[x$0]);
   }
}
