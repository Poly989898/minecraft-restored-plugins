package net.Indyuce.mmocore.api.block;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.MMOCore;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

public class RegenInfo {
   private final BlockType temporary;
   private final int regenTime;

   public RegenInfo(ConfigurationSection config) {
      Validate.notNull(config, "Could not read regen info config");
      this.temporary = config.contains("temp-block") ? MMOCore.plugin.loadManager.loadBlockType(new MMOLineConfig(config.getString("temp-block"))) : null;
      this.regenTime = config.getInt("time", 2400);
   }

   public int getTime() {
      return this.regenTime;
   }

   public boolean hasTemporaryBlock() {
      return this.temporary != null;
   }

   public BlockType getTemporaryBlock() {
      return this.temporary;
   }
}
