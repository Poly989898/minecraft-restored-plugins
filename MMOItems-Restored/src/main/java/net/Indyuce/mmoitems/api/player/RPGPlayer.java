package net.Indyuce.mmoitems.api.player;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.stat.type.ItemRestriction;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class RPGPlayer {
   private final PlayerData playerData;
   private final Player player;

   @Deprecated
   public RPGPlayer(Player player) {
      this(PlayerData.get(player));
   }

   public RPGPlayer(@NotNull PlayerData playerData) {
      this.player = playerData.getPlayer();
      this.playerData = playerData;
   }

   public PlayerData getPlayerData() {
      return this.playerData;
   }

   public Player getPlayer() {
      return this.playerData.getPlayer();
   }

   public abstract int getLevel();

   public abstract String getClassName();

   public abstract double getMana();

   public abstract double getStamina();

   public abstract void setMana(double value);

   public abstract void setStamina(double value);

   public void giveMana(double value) {
      this.setMana(this.getMana() + value);
   }

   public void giveStamina(double value) {
      this.setStamina(this.getStamina() + value);
   }

   public boolean canUse(NBTItem nbtItem, boolean flag) {
      return this.canUse(nbtItem, flag, false);
   }

   public boolean canUse(@NotNull NBTItem nbtItem, boolean flag, boolean flag2) {
      if (nbtItem.hasTag("MMOITEMS_UNIDENTIFIED_ITEM")) {
         if (flag) {
            Message.UNIDENTIFIED_ITEM.format(ChatColor.RED).send(this.player.getPlayer());
            this.player.getPlayer().playSound(this.player.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.5F);
         }

         return false;
      } else {
         if (MMOItems.plugin.getLanguage().disableRemovedItems && MMOUtils.hasBeenRemoved(nbtItem)) {
            return false;
         }

         for (ItemRestriction itemRestrictionStat : MMOItems.plugin.getStats().getItemRestrictionStats()) {
            if ((!itemRestrictionStat.isDynamic() || !flag2) && !itemRestrictionStat.canUse(this, nbtItem, flag)) {
               return false;
            }
         }

         return true;
      }
   }
}
