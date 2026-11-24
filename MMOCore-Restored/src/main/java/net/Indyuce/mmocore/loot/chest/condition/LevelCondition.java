package net.Indyuce.mmocore.loot.chest.condition;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.entity.Player;

public class LevelCondition extends Condition {
   private final int amount;
   private final String profession;

   public LevelCondition(MMOLineConfig config) {
      super(config);
      config.validate(new String[]{"amount"});
      this.amount = config.getInt("amount");
      this.profession = config.contains("profession") ? config.getString("profession") : null;
   }

   @Override
   public boolean isMet(ConditionInstance entity) {
      if (entity.getEntity() instanceof Player) {
         int level = this.profession != null
            ? PlayerData.get(entity.getEntity().getUniqueId()).getCollectionSkills().getLevel(this.profession)
            : PlayerData.get(entity.getEntity().getUniqueId()).getLevel();
         return level >= this.amount;
      } else {
         return false;
      }
   }
}
