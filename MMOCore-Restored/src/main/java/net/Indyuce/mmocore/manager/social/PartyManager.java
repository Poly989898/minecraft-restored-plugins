package net.Indyuce.mmocore.manager.social;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.manager.MMOCoreManager;
import net.Indyuce.mmocore.party.AbstractParty;
import net.Indyuce.mmocore.party.PartyUtils;
import org.bukkit.configuration.ConfigurationSection;

public class PartyManager implements MMOCoreManager {
   private final Set<StatModifier> buffs = new HashSet<>();

   public Set<StatModifier> getBonuses() {
      return this.buffs;
   }

   @Override
   public void initialize(boolean clearBefore) {
      if (clearBefore) {
         PlayerData.getAll().forEach(PartyUtils::clearStatBonuses);
         this.buffs.clear();
      }

      ConfigurationSection config = MMOCore.plugin.getConfig().getConfigurationSection("party.buff");
      if (config != null) {
         for (String key : config.getKeys(false)) {
            try {
               this.buffs.add(new StatModifier("mmocoreParty", UtilityMethods.enumName(key), config.getString(key)));
            } catch (IllegalArgumentException exception) {
               MMOCore.log(Level.WARNING, "Could not load party buff '" + key + "': " + exception.getMessage());
            }
         }
      }

      if (clearBefore) {
         for (PlayerData playerData : PlayerData.getAll()) {
            AbstractParty party = MMOCore.plugin.partyModule.getParty(playerData);
            if (party != null) {
               PartyUtils.applyStatBonuses(playerData, party.countMembers());
            }
         }
      }
   }
}
