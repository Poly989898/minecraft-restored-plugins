package io.lumine.mythic.core.skills.variables;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Nullable;

public class VariableUnstableUtils {
   @Nullable
   public static PersistentDataContainer getContainer(ItemMeta meta) {
      return meta == null ? null : meta.getPersistentDataContainer();
   }
}
