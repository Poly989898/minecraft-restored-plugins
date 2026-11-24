package net.Indyuce.mmoitems.comp.inventory;

import java.util.List;
import net.Indyuce.mmoitems.api.player.inventory.EquippedItem;
import org.bukkit.entity.Player;

public interface PlayerInventory {
   List<EquippedItem> getInventory(Player player);
}
