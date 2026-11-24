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
   public boolean canUse(RPGPlayer var1, NBTItem var2, boolean var3) {
      if (!var2.hasTag(this.getNBTPath())) {
         return true;
      }

      ArrayList var4 = new ArrayList();
      if (var2.hasTag(this.getNBTPath())) {
         var4.add(ItemTag.getTagAtPath(this.getNBTPath(), var2, SupportedNBTTagValues.STRING));
      }

      String var5 = this.getLoadedNBT(var4).toString();
      if (var5 != null) {
         switch (var5) {
            case "NORMAL":
            default:
               return true;
            case "DRY":
               for (Block var21 : this.blocksTouchedByPlayer(var1.getPlayer())) {
                  if (var21.isLiquid()) {
                     return false;
                  }
               }

               return true;
            case "WET":
               for (Block var20 : this.blocksTouchedByPlayer(var1.getPlayer())) {
                  if (var20.getType().equals(Material.WATER)) {
                     return true;
                  }
               }

               return false;
            case "DAMP":
               for (Block var19 : this.blocksTouchedByPlayer(var1.getPlayer())) {
                  if (!var19.getType().equals(Material.WATER)) {
                     return false;
                  }
               }

               return true;
            case "LAVA":
               for (Block var18 : this.blocksTouchedByPlayer(var1.getPlayer())) {
                  if (var18.getType().equals(Material.LAVA)) {
                     return true;
                  }
               }

               return false;
            case "MOLTEN":
               for (Block var17 : this.blocksTouchedByPlayer(var1.getPlayer())) {
                  if (!var17.getType().equals(Material.LAVA)) {
                     return false;
                  }
               }

               return true;
            case "LIQUID":
               for (Block var16 : this.blocksTouchedByPlayer(var1.getPlayer())) {
                  if (var16.isLiquid()) {
                     return true;
                  }
               }

               return false;
            case "SUBMERGED":
               for (Block var9 : this.blocksTouchedByPlayer(var1.getPlayer())) {
                  if (!var9.isLiquid()) {
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

   ArrayList<Block> blocksTouchedByPlayer(@NotNull Player var1) {
      BoundingBox var2 = var1.getBoundingBox();
      ArrayList var3 = new ArrayList();

      for (double var4 = var2.getMinX(); var4 <= var2.getMaxX(); var4 += Math.min(1.0, Math.max(var2.getWidthX() - (var4 - var2.getMinX()), 0.001))) {
         for (double var6 = var2.getMinY(); var6 <= var2.getMaxY(); var6 += Math.min(1.0, Math.max(var2.getHeight() - (var6 - var2.getMinY()), 0.001))) {
            for (double var8 = var2.getMinZ(); var8 <= var2.getMaxZ(); var8 += Math.min(1.0, Math.max(var2.getWidthZ() - (var8 - var2.getMinZ()), 0.001))) {
               int var10 = SilentNumbers.floor(var4);
               int var11 = SilentNumbers.floor(var6);
               int var12 = SilentNumbers.floor(var8);
               Block var13 = var1.getLocation().getWorld().getBlockAt(var10, var11, var12);
               if (!var3.contains(var13)) {
                  var3.add(var13);
               }
            }
         }
      }

      return var3;
   }
}
