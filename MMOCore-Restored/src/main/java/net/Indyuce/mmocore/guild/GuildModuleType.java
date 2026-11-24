package net.Indyuce.mmocore.guild;

import javax.inject.Provider;
import net.Indyuce.mmocore.guild.compat.GuildsGuildModule;
import net.Indyuce.mmocore.guild.compat.KingdomsXGuildModule;
import net.Indyuce.mmocore.guild.compat.UltimateClansGuildModule;
import net.Indyuce.mmocore.guild.provided.MMOCoreGuildModule;
import net.Indyuce.mmocore.guild.provided.NoneGuildModule;
import org.bukkit.Bukkit;

public enum GuildModuleType {
   MMOCORE("MMOCore", MMOCoreGuildModule::new),
   NONE("MMOCore", NoneGuildModule::new),
   GUILDS("Guilds", GuildsGuildModule::new),
   KINGDOMSX("Kingdoms", KingdomsXGuildModule::new),
   ULTIMATE_CLANS("UltimateClans", UltimateClansGuildModule::new);

   private final String pluginName;
   private final Provider<GuildModule> provider;

   GuildModuleType(String pluginName, Provider<GuildModule> provider) {
      this.pluginName = pluginName;
      this.provider = provider;
   }

   public String getPluginName() {
      return this.pluginName;
   }

   public boolean isValid() {
      return Bukkit.getPluginManager().getPlugin(this.pluginName) != null;
   }

   public GuildModule provideModule() {
      return (GuildModule)this.provider.get();
   }
}
