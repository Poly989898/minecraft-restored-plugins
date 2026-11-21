package io.lumine.mythic.core.drops.droppables;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IMultiDrop;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.drops.Drop;
import io.lumine.mythic.core.drops.DropTable;
import io.lumine.mythic.core.drops.LootBag;
import lombok.Getter;

public class DropTableDrop extends Drop implements IMultiDrop {
   @Getter
   private DropTable dropTable;
   private PlaceholderDouble level;

   public DropTableDrop(String line, String item, MythicLineConfig config) {
      super(line, config);
      this.dropTable = MythicBukkit.inst().getDropManager().getDropTable(item).get();
      this.level = config.getPlaceholderDouble(new String[]{"level", "lvl", "l"}, (PlaceholderDouble)null);
   }

   @Override
   public LootBag get(DropMetadata meta) {
      if (this.level != null) {
         meta = meta.clone();
         meta.setLevel(this.level.get(meta));
      }

      return this.dropTable.generate(meta);
   }
}
