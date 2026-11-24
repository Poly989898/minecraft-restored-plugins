package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.ItemRestriction;
import net.Indyuce.mmoitems.stat.type.PlayerConsumable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ManaCost extends DoubleStat implements ItemRestriction, PlayerConsumable {
   public ManaCost() {
      super("MANA_COST", Material.LAPIS_LAZULI, "Mana Cost", new String[]{"Mana spent by your weapon to be used."}, new String[]{"weapon"});
   }

   @Override
   public boolean canUse(RPGPlayer var1, NBTItem var2, boolean var3) {
      if (!var2.hasTag(ItemStats.MANA_COST.getNBTPath())) {
         return true;
      }

      double var4 = var2.getDouble(ItemStats.MANA_COST.getNBTPath());
      boolean var6 = var4 > 0.0 && var1.getMana() >= var4;
      if (!var6) {
         Message.NOT_ENOUGH_MANA.format(ChatColor.RED).send(var1.getPlayer());
      }

      return var6;
   }

   @Override
   public void onConsume(@NotNull VolatileMMOItem var1, @NotNull Player var2, boolean var3) {
      if (var1.hasData(ItemStats.MANA_COST)) {
         DoubleData var4 = (DoubleData)var1.getData(ItemStats.MANA_COST);
         if (var4.getValue() > 0.0) {
            RPGPlayer var5 = PlayerData.get(var2).getRPG();
            var5.setMana(var5.getMana() - var4.getValue());
         }
      }
   }
}
