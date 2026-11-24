package fr.phoenixdevt.mmoprofiles.bukkit.profile.data.inventory;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import java.util.Map;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class InventoryWrapper extends ItemStackArray {
   public static final int SIZE = 41;

   public InventoryWrapper() {
      super(41);
   }

   @Deprecated
   public InventoryWrapper(Map<String, Object> map) {
      super(41, map);
   }

   public InventoryWrapper(@NotNull String serializedData) {
      super(41, serializedData);
   }

   public InventoryWrapper(@NotNull Player player) {
      super(41);

      for (int i = 0; i < 36; i++) {
         if (!MMOProfiles.plugin.configManager.isBlacklisted(i)) {
            this.content[i] = player.getInventory().getItem(i);
         }
      }

      this.content[36] = player.getEquipment().getBoots();
      this.content[37] = player.getEquipment().getLeggings();
      this.content[38] = player.getEquipment().getChestplate();
      this.content[39] = player.getEquipment().getHelmet();
      this.content[40] = player.getEquipment().getItemInOffHand();
   }

   public void apply(Player player) {
      for (int i = 0; i < 36; i++) {
         if (!MMOProfiles.plugin.configManager.isBlacklisted(i)) {
            player.getInventory().setItem(i, this.content[i]);
         }
      }

      player.getEquipment().setBoots(this.content[36]);
      player.getEquipment().setLeggings(this.content[37]);
      player.getEquipment().setChestplate(this.content[38]);
      player.getEquipment().setHelmet(this.content[39]);
      player.getEquipment().setItemInOffHand(this.content[40]);
   }
}
