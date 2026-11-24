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
   public boolean canUse(RPGPlayer rpgPlayer, NBTItem nbtItem, boolean flag) {
      if (!nbtItem.hasTag(this.getNBTPath())) {
         return true;
      }

      ArrayList list = new ArrayList();
      if (nbtItem.hasTag(this.getNBTPath())) {
         list.add(ItemTag.getTagAtPath(this.getNBTPath(), nbtItem, SupportedNBTTagValues.STRING));
      }

      StringListData loadedNBT = this.getLoadedNBT(list);
      boolean hasBiome = false;
      if (loadedNBT != null) {
         for (String text : loadedNBT.getList()) {
            String biomeName = text.toLowerCase().replace(" ", "_").replace("-", "_");
            if (biomeName.startsWith("!")) {
               hasBiome = true;
               biomeName = biomeName.substring(1);
            }

            String key = rpgPlayer.getPlayer().getLocation().getBlock().getBiome().getKey().getKey();
            if (key.contains(biomeName)) {
               return !hasBiome;
            }
         }

         return hasBiome;
      } else {
         return true;
      }
   }

   @Override
   public boolean isDynamic() {
      return true;
   }
}
