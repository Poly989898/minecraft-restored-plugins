package fr.phoenixdevt.mmoprofiles.bukkit.profile.data.inventory;

import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class EnderChestWrapper extends ItemStackArray {
   public static final int SIZE = 27;

   public EnderChestWrapper() {
      super(27);
   }

   @Deprecated
   public EnderChestWrapper(Map<String, Object> map) {
      super(27, map);
   }

   public EnderChestWrapper(@NotNull String serializedData) {
      super(27, serializedData);
   }

   public EnderChestWrapper(@NotNull Player player) {
      super(27);

      for (int i = 0; i < 27; i++) {
         this.content[i] = player.getEnderChest().getItem(i);
      }
   }

   public void apply(Player player) {
      Inventory chest = player.getEnderChest();

      for (int i = 0; i < 27; i++) {
         chest.setItem(i, this.content[i]);
      }
   }
}
