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
   public RPGPlayer(Player var1) {
      this(PlayerData.get(var1));
   }

   public RPGPlayer(@NotNull PlayerData var1) {
      this.player = var1.getPlayer();
      this.playerData = var1;
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

   public abstract void setMana(double var1);

   public abstract void setStamina(double var1);

   public void giveMana(double var1) {
      this.setMana(this.getMana() + var1);
   }

   public void giveStamina(double var1) {
      this.setStamina(this.getStamina() + var1);
   }

   public boolean canUse(NBTItem var1, boolean var2) {
      return this.canUse(var1, var2, false);
   }

   public boolean canUse(@NotNull NBTItem var1, boolean var2, boolean var3) {
      if (var1.hasTag("MMOITEMS_UNIDENTIFIED_ITEM")) {
         if (var2) {
            Message.UNIDENTIFIED_ITEM.format(ChatColor.RED).send(this.player.getPlayer());
            this.player.getPlayer().playSound(this.player.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.5F);
         }

         return false;
      } else {
         if (MMOItems.plugin.getLanguage().disableRemovedItems && MMOUtils.hasBeenRemoved(var1)) {
            return false;
         }

         for (ItemRestriction var5 : MMOItems.plugin.getStats().getItemRestrictionStats()) {
            if ((!var5.isDynamic() || !var3) && !var5.canUse(this, var1, var2)) {
               return false;
            }
         }

         return true;
      }
   }
}
