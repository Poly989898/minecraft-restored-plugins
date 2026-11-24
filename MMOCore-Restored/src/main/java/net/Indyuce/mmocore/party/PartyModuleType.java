package net.Indyuce.mmocore.party;

import javax.inject.Provider;
import net.Indyuce.mmocore.party.compat.DungeonsXLPartyModule;
import net.Indyuce.mmocore.party.compat.HeroesPartyModule;
import net.Indyuce.mmocore.party.compat.McMMOPartyModule;
import net.Indyuce.mmocore.party.compat.MythicDungeonsPartyInjector;
import net.Indyuce.mmocore.party.compat.MythicDungeonsPartyModule;
import net.Indyuce.mmocore.party.compat.OBTeamPartyModule;
import net.Indyuce.mmocore.party.compat.PAFPartyModule;
import net.Indyuce.mmocore.party.compat.PAFProxyPartyModule;
import net.Indyuce.mmocore.party.compat.PartiesPartyModule;
import net.Indyuce.mmocore.party.provided.MMOCorePartyModule;
import net.Indyuce.mmocore.party.provided.NonePartyModule;
import org.bukkit.Bukkit;

public enum PartyModuleType {
   MMOCORE("MMOCore", MMOCorePartyModule::new),
   NONE("MMOCore", NonePartyModule::new),
   DUNGEONSXL("DungeonsXL", DungeonsXLPartyModule::new),
   HEROES("Heroes", HeroesPartyModule::new),
   MCMMO("mcMMO", McMMOPartyModule::new),
   MYTHICDUNGEONS_INJECT("MythicDungeons", () -> {
      try {
         return MythicDungeonsPartyInjector.class.getConstructor().newInstance();
      } catch (Exception exception) {
         throw new RuntimeException(exception);
      }
   }),
   MYTHICDUNGEONS("MythicDungeons", MythicDungeonsPartyModule::new),
   OBTEAM("OBTeam", OBTeamPartyModule::new),
   PARTY_AND_FRIENDS("PartyAndFriends", PAFPartyModule::new),
   PARTY_AND_FRIENDS_BUNGEECORD_VELOCITY("Spigot-Party-API-PAF", PAFProxyPartyModule::new),
   PARTIES("Parties", PartiesPartyModule::new);

   private final String pluginName;
   private final Provider<PartyModule> provider;

   PartyModuleType(String pluginName, Provider<PartyModule> provider) {
      this.pluginName = pluginName;
      this.provider = provider;
   }

   public String getPluginName() {
      return this.pluginName;
   }

   public boolean isValid() {
      return this.pluginName == null || Bukkit.getPluginManager().getPlugin(this.pluginName) != null;
   }

   public PartyModule provideModule() {
      return (PartyModule)this.provider.get();
   }
}
