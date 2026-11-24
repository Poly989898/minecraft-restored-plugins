package net.Indyuce.mmoitems.api.player;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.stat.StatInstance;
import io.lumine.mythic.lib.api.stat.StatMap;
import io.lumine.mythic.lib.api.stat.StatInstance.ModifierPacket;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.player.inventory.EquippedItem;
import net.Indyuce.mmoitems.stat.type.AttackWeaponStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.jetbrains.annotations.NotNull;

public class PlayerStats {
   private final PlayerData playerData;

   public PlayerStats(PlayerData var1) {
      this.playerData = var1;
   }

   public PlayerData getData() {
      return this.playerData;
   }

   @NotNull
   public StatMap getMap() {
      return this.playerData.getMMOPlayerData().getStatMap();
   }

   public double getStat(@NotNull ItemStat<?, ?> var1) {
      return this.getMap().getStat(var1.getId());
   }

   @NotNull
   public StatInstance getInstance(@NotNull ItemStat<?, ?> var1) {
      return this.getMap().getInstance(var1.getId());
   }

   @NotNull
   public PlayerMetadata newTemporary(@NotNull EquipmentSlot var1) {
      return this.playerData.getMMOPlayerData().getStatMap().cache(var1);
   }

   public void updateStats() {
      for (ItemStat var2 : MMOItems.plugin.getStats().getNumericStats()) {
         ModifierPacket var3 = this.getInstance(var2).newPacket();
         var3.removeIf(var0 -> var0.startsWith("MMOItem"));
         if (this.playerData.hasSetBonuses() && this.playerData.getSetBonuses().hasStat(var2)) {
            var3.addModifier(
               new StatModifier(
                  "MMOItemSetBonus", var2.getId(), this.playerData.getSetBonuses().getStat(var2), ModifierType.FLAT, EquipmentSlot.OTHER, ModifierSource.OTHER
               )
            );
         }

         int var4 = 0;

         for (EquippedItem var6 : this.playerData.getInventory().getEquipped()) {
            double var7 = var6.getNBT().getStat(var2.getId());
            if (var7 != 0.0) {
               ModifierSource var9 = var6.getCached().getType().getModifierSource();
               if (var9.isWeapon() && var2 instanceof AttackWeaponStat) {
                  var7 -= ((AttackWeaponStat)var2).getOffset(this.playerData);
               }

               var3.addModifier(new StatModifier("MMOItem-" + var4++, var2.getId(), var7, ModifierType.FLAT, var6.getSlot(), var9));
            }
         }

         var3.update();
      }
   }
}
