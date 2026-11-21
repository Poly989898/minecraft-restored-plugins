package io.lumine.mythic.core.constants;

import org.bukkit.entity.Player;

public class UIEffects {
   public static final void playErrorSound(Player player) {
      player.playSound(player.getLocation(), "block.note_block.didgeridoo", 1.0F, 1.0F);
   }

   public static final void playSuccessSound(Player player) {
      player.playSound(player.getLocation(), "block.note_block.bell", 1.0F, 1.0F);
   }

   public static final void playDataUpdatedSound(Player player) {
      player.playSound(player.getLocation(), "ui.cartography_table.take_result", 1.0F, 1.0F);
   }

   public static final void playClickSound(Player player) {
      player.playSound(player.getLocation(), "ui.button.click", 1.0F, 1.0F);
   }

   public static final void playPageSound(Player player) {
      player.playSound(player.getLocation(), "ui.loom.select_pattern", 1.0F, 1.0F);
   }
}
