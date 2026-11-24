package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import java.util.ArrayList;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.stat.data.StringListData;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import net.Indyuce.mmoitems.stat.type.ItemRestriction;
import net.Indyuce.mmoitems.stat.type.StringListStat;
import org.bukkit.Material;

public class RequiredBiomes extends StringListStat implements ItemRestriction, GemStoneStat {
   public RequiredBiomes() {
      super(
         "REQUIRED_BIOMES",
         Material.JUNGLE_SAPLING,
         "Required Biomes",
         new String[]{"The biome the player must be within", "for this item to activate."},
         new String[]{"!block", "all"}
      );
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

      StringListData var5 = this.getLoadedNBT(var4);
      boolean var6 = false;
      if (var5 != null) {
         for (String var8 : var5.getList()) {
            String var9 = var8.toLowerCase().replace(" ", "_").replace("-", "_");
            if (var9.startsWith("!")) {
               var6 = true;
               var9 = var9.substring(1);
            }

            String var10 = var1.getPlayer().getLocation().getBlock().getBiome().getKey().getKey();
            if (var10.contains(var9)) {
               return !var6;
            }
         }

         return var6;
      } else {
         return true;
      }
   }

   @Override
   public boolean isDynamic() {
      return true;
   }
}
