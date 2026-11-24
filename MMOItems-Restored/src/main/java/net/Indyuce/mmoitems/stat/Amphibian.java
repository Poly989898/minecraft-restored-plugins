package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.type.ChooseStat;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import net.Indyuce.mmoitems.stat.type.ItemRestriction;
import net.Indyuce.mmoitems.util.StatChoice;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

public class Amphibian extends ChooseStat implements ItemRestriction, GemStoneStat {
   public static final StatChoice NORMAL = new StatChoice("NORMAL", "No liquids dependency");
   public static final StatChoice DRY = new StatChoice("DRY", "The item does not work if the player is touching a liquid block.");
   public static final StatChoice WET = new StatChoice("WET", "The only works if the player is touching water (rain does not count).");
   public static final StatChoice DAMP = new StatChoice("DAMP", "The only works if the player is completely submerged in water.");
   public static final StatChoice LAVA = new StatChoice("LAVA", "The only works if the player is touching lava.");
   public static final StatChoice MOLTEN = new StatChoice("MOLTEN", "The only works if the player is completely submerged in lava.");
   public static final StatChoice LIQUID = new StatChoice("LIQUID", "The only works if the player is touching any liquid.");
   public static final StatChoice SUBMERGED = new StatChoice("SUBMERGED", "The only works if the player is completely submerged in any liquid.");

   public Amphibian() {
      super(
         "AMPHIBIAN",
         Material.WATER_BUCKET,
         "Amphibian",
         new String[]{"May this item only be used in specific", "environments regarding liquids?"},
         new String[]{"!block", "all"}
      );
      this.addChoices(NORMAL, DRY, WET, DAMP, LAVA, MOLTEN, LIQUID, SUBMERGED);
   }

   @NotNull
   @Override
   public StringData getClearStatData() {
      return new StringData(NORMAL.getId());
   }

   @Override
   public boolean canUse(RPGPlayer rpgPlayer, NBTItem nbtItem, boolean flag) {
      if (!nbtItem.hasTag(this.getNBTPath())) {
         return true;
      }

      ArrayList list = new ArrayList();
      if (nbtItem.hasTag(this.getNBTPath())) {
         list.add(ItemTag.getTagAtPath(this.getNBTPath(), nbtItem, SupportedNBTTagValues.STRING));
      }

      String text = this.getLoadedNBT(list).toString();
      if (text != null) {
         switch (text) {
            case "NORMAL":
            default:
               return true;
            case "DRY":
               for (Block block : this.blocksTouchedByPlayer(rpgPlayer.getPlayer())) {
                  if (block.isLiquid()) {
                     return false;
                  }
               }

               return true;
            case "WET":
               for (Block block : this.blocksTouchedByPlayer(rpgPlayer.getPlayer())) {
                  if (block.getType().equals(Material.WATER)) {
                     return true;
                  }
               }

               return false;
            case "DAMP":
               for (Block block : this.blocksTouchedByPlayer(rpgPlayer.getPlayer())) {
                  if (!block.getType().equals(Material.WATER)) {
                     return false;
                  }
               }

               return true;
            case "LAVA":
               for (Block block : this.blocksTouchedByPlayer(rpgPlayer.getPlayer())) {
                  if (block.getType().equals(Material.LAVA)) {
                     return true;
                  }
               }

               return false;
            case "MOLTEN":
               for (Block block : this.blocksTouchedByPlayer(rpgPlayer.getPlayer())) {
                  if (!block.getType().equals(Material.LAVA)) {
                     return false;
                  }
               }

               return true;
            case "LIQUID":
               for (Block block : this.blocksTouchedByPlayer(rpgPlayer.getPlayer())) {
                  if (block.isLiquid()) {
                     return true;
                  }
               }

               return false;
            case "SUBMERGED":
               for (Block block : this.blocksTouchedByPlayer(rpgPlayer.getPlayer())) {
                  if (!block.isLiquid()) {
                     return false;
                  }
               }

               return true;
         }
      } else {
         return true;
      }
   }

   @Override
   public boolean isDynamic() {
      return true;
   }

   ArrayList<Block> blocksTouchedByPlayer(@NotNull Player player) {
      BoundingBox boundingBox = player.getBoundingBox();
      ArrayList list = new ArrayList();

      for (double minX = boundingBox.getMinX(); minX <= boundingBox.getMaxX(); minX += Math.min(1.0, Math.max(boundingBox.getWidthX() - (minX - boundingBox.getMinX()), 0.001))) {
         for (double minY = boundingBox.getMinY(); minY <= boundingBox.getMaxY(); minY += Math.min(1.0, Math.max(boundingBox.getHeight() - (minY - boundingBox.getMinY()), 0.001))) {
            for (double minZ = boundingBox.getMinZ(); minZ <= boundingBox.getMaxZ(); minZ += Math.min(1.0, Math.max(boundingBox.getWidthZ() - (minZ - boundingBox.getMinZ()), 0.001))) {
               int count = SilentNumbers.floor(minX);
               int count2 = SilentNumbers.floor(minY);
               int count3 = SilentNumbers.floor(minZ);
               Block blockAt = player.getLocation().getWorld().getBlockAt(count, count2, count3);
               if (!list.contains(blockAt)) {
                  list.add(blockAt);
               }
            }
         }
      }

      return list;
   }
}
