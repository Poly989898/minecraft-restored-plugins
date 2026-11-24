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

   public PlayerStats(PlayerData playerData) {
      this.playerData = playerData;
   }

   public PlayerData getData() {
      return this.playerData;
   }

   @NotNull
   public StatMap getMap() {
      return this.playerData.getMMOPlayerData().getStatMap();
   }

   public double getStat(@NotNull ItemStat<?, ?> itemStat) {
      return this.getMap().getStat(itemStat.getId());
   }

   @NotNull
   public StatInstance getInstance(@NotNull ItemStat<?, ?> itemStat) {
      return this.getMap().getInstance(itemStat.getId());
   }

   @NotNull
   public PlayerMetadata newTemporary(@NotNull EquipmentSlot equipmentSlot) {
      return this.playerData.getMMOPlayerData().getStatMap().cache(equipmentSlot);
   }

   public void updateStats() {
      for (ItemStat numericStat : MMOItems.plugin.getStats().getNumericStats()) {
         ModifierPacket packet = this.getInstance(numericStat).newPacket();
         packet.removeIf(text -> text.startsWith("MMOItem"));
         if (this.playerData.hasSetBonuses() && this.playerData.getSetBonuses().hasStat(numericStat)) {
            packet.addModifier(
               new StatModifier(
                  "MMOItemSetBonus", numericStat.getId(), this.playerData.getSetBonuses().getStat(numericStat), ModifierType.FLAT, EquipmentSlot.OTHER, ModifierSource.OTHER
               )
            );
         }

         int count = 0;

         for (EquippedItem equippedItem : this.playerData.getInventory().getEquipped()) {
            double stat = equippedItem.getNBT().getStat(numericStat.getId());
            if (stat != 0.0) {
               ModifierSource modifierSource = equippedItem.getCached().getType().getModifierSource();
               if (modifierSource.isWeapon() && numericStat instanceof AttackWeaponStat) {
                  stat -= ((AttackWeaponStat)numericStat).getOffset(this.playerData);
               }

               packet.addModifier(new StatModifier("MMOItem-" + count++, numericStat.getId(), stat, ModifierType.FLAT, equippedItem.getSlot(), modifierSource));
            }
         }

         packet.update();
      }
   }
}
