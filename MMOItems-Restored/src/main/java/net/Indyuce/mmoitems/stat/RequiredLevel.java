package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.stat.data.RequiredLevelData;
import net.Indyuce.mmoitems.stat.type.RequiredLevelStat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public class RequiredLevel extends RequiredLevelStat {
   public RequiredLevel() {
      super("LEVEL", Material.EXPERIENCE_BOTTLE, "Level", new String[]{"The level your item needs", "in order to be used."});
   }

   @Override
   public boolean canUse(RPGPlayer var1, NBTItem var2, boolean var3) {
      int var4 = var2.getInteger(this.getNBTPath());
      if (var4 <= 0) {
         return true;
      }

      if (var1.getLevel() < var4 && !var1.getPlayer().hasPermission("mmoitems.bypass.level")) {
         if (var3) {
            Message.NOT_ENOUGH_LEVELS.format(ChatColor.RED).send(var1.getPlayer());
            var1.getPlayer().playSound(var1.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.5F);
         }

         return false;
      } else {
         return true;
      }
   }

   @NotNull
   @Override
   public RequiredLevelData getClearStatData() {
      return new RequiredLevelData(0.0);
   }
}
