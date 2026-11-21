package com.nexomc.nexo.mechanics.custom_block.stringblock;

import java.util.Collection;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.collections.IntIterator;
import kotlin.ranges.IntRange;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ\u000e\u0010\f\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ\u000e\u0010\r\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\tJ\u000e\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012R\u0016\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0007¨\u0006\u0013"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringMechanicHelpers;", "", "<init>", "()V", "BLOCK_FACES", "", "Lorg/bukkit/block/BlockFace;", "[Lorg/bukkit/block/BlockFace;", "legacyBlockData", "Lorg/bukkit/block/data/type/Tripwire;", "customVariation", "", "modernBlockData", "modernCustomVariation", "tripwire", "fixClientsideUpdate", "", "loc", "Lorg/bukkit/Location;", "com.nexomc:core"}
)
public final class StringMechanicHelpers {
   @NotNull
   public static final StringMechanicHelpers INSTANCE = new StringMechanicHelpers();
   @NotNull
   private static final BlockFace[] BLOCK_FACES;

   private StringMechanicHelpers() {
   }

   @NotNull
   public final Tripwire legacyBlockData(int customVariation) {
      BlockData blockData = Bukkit.createBlockData(Material.TRIPWIRE);
      Tripwire tripwire2 = (Tripwire)blockData;
      Tripwire tripwire = tripwire2;
      int value = 0;
      int i = 0;
      Object[] objs = BLOCK_FACES;
      int value2 = 0;

      for(int j = objs.length; value2 < j; ++value2) {
         Object element = objs[value2];
         int value3 = 0;
         int value4 = i++;
         tripwire.setFace((BlockFace)element, (customVariation & 1 << value4) != 0);
      }

      int value5 = i++;
      tripwire.setAttached((customVariation & 1 << value5) != 0);
      value5 = i++;
      tripwire.setDisarmed((customVariation & 1 << value5) != 0);
      tripwire.setPowered((customVariation & 1 << i) != 0);
      return tripwire2;
   }

   @NotNull
   public final Tripwire modernBlockData(int customVariation) {
      BlockData blockData = Bukkit.createBlockData(Material.TRIPWIRE);
      Tripwire tripwire = (Tripwire)blockData;
      int value = 0;
      tripwire.setFace(BlockFace.NORTH, (customVariation & 1) != 0);
      tripwire.setFace(BlockFace.SOUTH, (customVariation & 2) != 0);
      tripwire.setFace(BlockFace.EAST, (customVariation & 4) != 0);
      tripwire.setFace(BlockFace.WEST, (customVariation & 8) != 0);
      tripwire.setAttached((customVariation & 16) != 0);
      tripwire.setDisarmed((customVariation & 32) != 0);
      tripwire.setPowered((customVariation & 64) != 0);
      return tripwire;
   }

   public final int modernCustomVariation(@NotNull Tripwire tripwire) {
      int customVariation = 0;
      if (tripwire.hasFace(BlockFace.NORTH)) {
         customVariation |= 1;
      }

      if (tripwire.hasFace(BlockFace.SOUTH)) {
         customVariation |= 2;
      }

      if (tripwire.hasFace(BlockFace.EAST)) {
         customVariation |= 4;
      }

      if (tripwire.hasFace(BlockFace.WEST)) {
         customVariation |= 8;
      }

      if (tripwire.isAttached()) {
         customVariation |= 16;
      }

      if (tripwire.isDisarmed()) {
         customVariation |= 32;
      }

      if (tripwire.isPowered()) {
         customVariation |= 64;
      }

      return customVariation;
   }

   public final void fixClientsideUpdate(@NotNull Location loc) {
      Collection nearbyEntitiesByType = loc.getWorld().getNearbyEntitiesByType(Player.class, loc, (double)20.0F);
      Collection players = nearbyEntitiesByType;
      Iterable iterable = (Iterable)(new IntRange(loc.getBlockX() - 10, loc.getBlockX() + 10));
      Iterator iterator = iterable.iterator();

      while(iterator.hasNext()) {
         int element = ((IntIterator)iterator).nextInt();
         int x = element;
         int value = 0;
         Iterable iterable2 = (Iterable)(new IntRange(loc.getBlockY() - 4, loc.getBlockY() + 4));
         Iterator iterator2 = iterable2.iterator();

         while(iterator2.hasNext()) {
            int nextInt = ((IntIterator)iterator2).nextInt();
            int y = nextInt;
            int value2 = 0;
            Iterable iterable3 = (Iterable)(new IntRange(loc.getBlockZ() - 10, loc.getBlockZ() + 10));
            Iterator iterator3 = iterable3.iterator();

            while(iterator3.hasNext()) {
               int value6 = ((IntIterator)iterator3).nextInt();
               int value3 = 0;
               Location location = new Location(loc.getWorld(), (double)x, (double)y, (double)value6);
               BlockData it = location.getBlock().getBlockData();
               int value4 = 0;
               BlockData blockData2 = it instanceof Tripwire ? it : null;
               if ((it instanceof Tripwire ? it : null) != null) {
                  BlockData blockData = blockData2;
                  Iterable iterable4 = (Iterable)players;

                  for(Object obj : iterable4) {
                     Player it_1 = (Player)obj;
                     int value5 = 0;
                     it_1.sendBlockChange(location, blockData);
                  }
               }
            }
         }
      }

   }

   static {
      BlockFace[] blockFaces = new BlockFace[]{BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH};
      BLOCK_FACES = blockFaces;
   }
}
