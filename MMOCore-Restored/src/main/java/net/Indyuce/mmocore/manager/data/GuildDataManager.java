package net.Indyuce.mmocore.manager.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.Indyuce.mmocore.api.ConfigFile;
import net.Indyuce.mmocore.guild.provided.Guild;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

public abstract class GuildDataManager {
   protected final Map<String, Guild> guilds = new HashMap<>();
   private GuildDataManager.GuildConfiguration config;

   public Guild newRegisteredGuild(UUID owner, String name, String tag) {
      Guild guild = new Guild(owner, name, tag);
      this.registerGuild(guild);
      return guild;
   }

   public void registerGuild(Guild guild) {
      this.guilds.put(guild.getId(), guild);
   }

   public boolean isRegistered(Guild guild) {
      return this.guilds.containsValue(guild);
   }

   public boolean isRegistered(String tag) {
      return this.guilds.containsKey(tag);
   }

   public void unregisterGuild(Guild guild) {
      guild.forEachMember(member -> guild.removeMember(member, true));
      this.guilds.remove(guild.getId());
      this.delete(guild);
   }

   public void saveAll() {
      for (Guild guild : this.guilds.values()) {
         this.save(guild);
      }
   }

   @Nullable
   public Guild getGuild(String guild) {
      return this.guilds.get(guild);
   }

   public Collection<Guild> getAll() {
      return this.guilds.values();
   }

   @Deprecated
   public void reload() {
      for (Guild guild : this.guilds.values()) {
         this.save(guild);
      }

      this.guilds.clear();
      this.load();
      this.config = new GuildDataManager.GuildConfiguration();
   }

   public abstract void save(Guild var1);

   public abstract void load();

   public abstract void delete(Guild var1);

   public GuildDataManager.GuildConfiguration getConfig() {
      return this.config == null ? (this.config = new GuildDataManager.GuildConfiguration()) : this.config;
   }

   public static class GuildConfiguration {
      private final String prefix;
      private final boolean uppercaseTags;
      private final GuildDataManager.GuildConfiguration.NamingRules tagRules;
      private final GuildDataManager.GuildConfiguration.NamingRules nameRules;

      public GuildConfiguration() {
         FileConfiguration config = new ConfigFile("guilds").getConfig();
         this.prefix = config.getString("chat-prefix", "*");
         this.uppercaseTags = config.getBoolean("uppercase-tags", true);
         this.tagRules = new GuildDataManager.GuildConfiguration.NamingRules(config.getConfigurationSection("rules.tag"));
         this.nameRules = new GuildDataManager.GuildConfiguration.NamingRules(config.getConfigurationSection("rules.name"));
      }

      public String getPrefix() {
         return this.prefix;
      }

      public boolean shouldUppercaseTags() {
         return this.uppercaseTags;
      }

      public GuildDataManager.GuildConfiguration.NamingRules getTagRules() {
         return this.tagRules;
      }

      public GuildDataManager.GuildConfiguration.NamingRules getNameRules() {
         return this.nameRules;
      }

      public static class NamingRules {
         private final String regex;
         private final int min;
         private final int max;

         public NamingRules(ConfigurationSection config) {
            this.regex = config.getString("matches", "[a-zA-Z-_!?]+");
            this.min = config.getInt("min-length", 3);
            this.max = config.getInt("max-length", 5);
         }

         public String getRegex() {
            return this.regex;
         }

         public int getMin() {
            return this.min;
         }

         public int getMax() {
            return this.max;
         }
      }
   }
}
