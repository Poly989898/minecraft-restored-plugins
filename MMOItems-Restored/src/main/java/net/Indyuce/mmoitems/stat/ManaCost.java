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
   public boolean canUse(RPGPlayer rpgPlayer, NBTItem nbtItem, boolean flag) {
      if (!nbtItem.hasTag(ItemStats.MANA_COST.getNBTPath())) {
         return true;
      }

      double value = nbtItem.getDouble(ItemStats.MANA_COST.getNBTPath());
      boolean hasMana = value > 0.0 && rpgPlayer.getMana() >= value;
      if (!hasMana) {
         Message.NOT_ENOUGH_MANA.format(ChatColor.RED).send(rpgPlayer.getPlayer());
      }

      return hasMana;
   }

   @Override
   public void onConsume(@NotNull VolatileMMOItem volatileMMOItem, @NotNull Player player, boolean flag) {
      if (volatileMMOItem.hasData(ItemStats.MANA_COST)) {
         DoubleData data = (DoubleData)volatileMMOItem.getData(ItemStats.MANA_COST);
         if (data.getValue() > 0.0) {
            RPGPlayer rPG = PlayerData.get(player).getRPG();
            rPG.setMana(rPG.getMana() - data.getValue());
         }
      }
   }
}
