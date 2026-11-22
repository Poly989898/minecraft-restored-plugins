package net.Zrips.CMILib.Container;

import java.lang.reflect.Method;
import javax.annotation.Nullable;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CMIPlayerInventory {
  private static Method topInventory = null;

  @NotNull
  public static ItemStack[] getTopInventoryContents(Player player) {
    Inventory inv = getTopInventory(player);
    return inv == null ? new ItemStack[54] : inv.getContents();
  }

  @Nullable
  public static Inventory getTopInventory(Player player) {
    if (Version.isCurrentEqualOrHigher(Version.v1_21_R1)) {
      return player.getOpenInventory().getTopInventory();
    }

    try {
      InventoryView inv = player.getOpenInventory();
      if (topInventory == null) {
        topInventory = InventoryView.class.getMethod("getTopInventory");
      }

      return (Inventory) topInventory.invoke(inv);
    } catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }
}
