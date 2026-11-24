package net.Indyuce.mmocore.manager;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.util.FileUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigFile;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.util.input.ChatInput;
import net.Indyuce.mmocore.api.util.input.PlayerInput;
import net.Indyuce.mmocore.command.api.CommandVerbose;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigManager {
   public final CommandVerbose commandVerbose = new CommandVerbose();
   public boolean overrideVanillaExp;
   public boolean canCreativeCast;
   public boolean passiveSkillsNeedBinding;
   public boolean cobbleGeneratorXP;
   public boolean saveDefaultClassInfo;
   public boolean splitMainExp;
   public boolean splitProfessionExp;
   public boolean disableQuestBossBar;
   public boolean pvpModeEnabled;
   public boolean pvpModeInvulnerabilityCanDamage;
   public boolean forceClassSelection;
   public boolean enableGlobalSkillTreeGUI;
   public boolean enableSpecificSkillTreeGUI;
   public boolean waypointAutoPathCalculation;
   public boolean waypointLinkReciprocity;
   public String partyChatPrefix;
   public String noSkillBoundPlaceholder;
   public ChatColor staminaFull;
   public ChatColor staminaHalf;
   public ChatColor staminaEmpty;
   public long combatLogTimer;
   public long lootChestExpireTime;
   public long lootChestPlayerCooldown;
   public long globalSkillCooldown;
   public double lootChestsChanceWeight;
   public double dropItemsChanceWeight;
   public double fishingDropsChanceWeight;
   public double partyMaxExpSplitRange;
   public double pvpModeToggleOnCooldown;
   public double pvpModeToggleOffCooldown;
   public double pvpModeCombatCooldown;
   public double pvpModeCombatTimeout;
   public double pvpModeInvulnerabilityTimeRegionChange;
   public double pvpModeInvulnerabilityTimeCommand;
   public double pvpModeRegionEnterCooldown;
   public double pvpModeRegionLeaveCooldown;
   public int maxPartyLevelDifference;
   public int maxPartyPlayers;
   public int minCombatLevel;
   public int maxCombatLevelDifference;
   public int skillTreeScrollStepX;
   public int skillTreeScrollStepY;
   public int waypointWarpTime;
   public final List<DamageCause> combatLogDamageCauses = new ArrayList<>();
   private final FileConfiguration messages;

   public ConfigManager() {
      FileUtils.moveIfExists(MMOCore.plugin, "attributes.yml", "attributes");
      FileUtils.moveIfExists(MMOCore.plugin, "exp-tables.yml", "exp-tables");
      FileUtils.moveIfExists(MMOCore.plugin, "loot-chests.yml", "loot-chests");
      FileUtils.moveIfExists(MMOCore.plugin, "waypoints.yml", "waypoints");
      if (!FileUtils.getFile(MMOCore.plugin, "attributes").exists()) {
         this.copyDefaultFile("attributes/default_attributes.yml");
      }

      if (!FileUtils.getFile(MMOCore.plugin, "classes").exists()) {
         this.copyDefaultFile("classes/mage/arcane-mage.yml");
         this.copyDefaultFile("classes/mage/mage.yml");
         this.copyDefaultFile("classes/human.yml");
         this.copyDefaultFile("classes/marksman.yml");
         this.copyDefaultFile("classes/paladin.yml");
         this.copyDefaultFile("classes/rogue.yml");
         this.copyDefaultFile("classes/warrior.yml");
      }

      if (!FileUtils.getFile(MMOCore.plugin, "drop-tables").exists()) {
         this.copyDefaultFile("drop-tables/example_drop_tables.yml");
      }

      if (!FileUtils.getFile(MMOCore.plugin, "exp-tables").exists()) {
         this.copyDefaultFile("exp-tables/default_exp_tables.yml");
      }

      if (!FileUtils.getFile(MMOCore.plugin, "expcurves").exists()) {
         this.copyDefaultFile("expcurves/levels.txt");
         this.copyDefaultFile("expcurves/mining.txt");
         this.copyDefaultFile("expcurves/skill-tree-node.txt");
      }

      if (!FileUtils.getFile(MMOCore.plugin, "loot-chests").exists()) {
         this.copyDefaultFile("loot-chests/default_loot_chests.yml");
      }

      if (!FileUtils.getFile(MMOCore.plugin, "professions").exists()) {
         this.copyDefaultFile("professions/alchemy.yml");
         this.copyDefaultFile("professions/farming.yml");
         this.copyDefaultFile("professions/fishing.yml");
         this.copyDefaultFile("professions/mining.yml");
         this.copyDefaultFile("professions/smelting.yml");
         this.copyDefaultFile("professions/smithing.yml");
         this.copyDefaultFile("professions/woodcutting.yml");
         this.copyDefaultFile("professions/enchanting.yml");
      }

      if (!FileUtils.getFile(MMOCore.plugin, "quests").exists()) {
         this.copyDefaultFile("quests/adv-begins.yml");
         this.copyDefaultFile("quests/tutorial.yml");
         this.copyDefaultFile("quests/fetch-mango.yml");
      }

      if (!FileUtils.getFile(MMOCore.plugin, "skill-trees").exists()) {
         this.copyDefaultFile("skill-trees/combat.yml");
         this.copyDefaultFile("skill-trees/mage-arcane-mage.yml");
         this.copyDefaultFile("skill-trees/rogue-marksman.yml");
         this.copyDefaultFile("skill-trees/warrior-paladin.yml");
         this.copyDefaultFile("skill-trees/general.yml");
         this.copyDefaultFile("skill-trees/loop.yml");
      }

      if (!FileUtils.getFile(MMOCore.plugin, "waypoints").exists()) {
         this.copyDefaultFile("waypoints/default_waypoints.yml");
      }

      this.copyDefaultFile("conditions.yml");
      this.copyDefaultFile("exp-sources.yml");
      this.copyDefaultFile("guilds.yml");
      this.copyDefaultFile("items.yml");
      this.copyDefaultFile("messages.yml");
      this.copyDefaultFile("restrictions.yml");
      this.copyDefaultFile("sounds.yml");
      this.copyDefaultFile("stats.yml");
      ConfigurationSection config = MMOCore.plugin.getConfig();
      this.commandVerbose.reload(MMOCore.plugin.getConfig().getConfigurationSection("command-verbose"));
      this.messages = new ConfigFile("messages").getConfig();
      this.partyChatPrefix = MMOCore.plugin.getConfig().getString("party.chat-prefix");
      this.maxPartyPlayers = Math.max(2, MMOCore.plugin.getConfig().getInt("party.max-players", 8));
      this.combatLogTimer = MMOCore.plugin.getConfig().getInt("combat-log.timer") * 1000L;
      this.combatLogDamageCauses.clear();

      for (String key : MMOCore.plugin.getConfig().getStringList("combat-log.causes")) {
         try {
            this.combatLogDamageCauses.add(DamageCause.valueOf(UtilityMethods.enumName(key)));
         } catch (Exception exception) {
            MMOCore.plugin.getLogger().log(Level.WARNING, "Could not find damage cause called '" + key + "'");
         }
      }

      this.enableGlobalSkillTreeGUI = MMOCore.plugin.getConfig().getBoolean("enable-global-skill-tree-gui");
      this.enableSpecificSkillTreeGUI = MMOCore.plugin.getConfig().getBoolean("enable-specific-skill-tree-gui");
      this.lootChestExpireTime = Math.max(MMOCore.plugin.getConfig().getInt("loot-chests.chest-expire-time"), 1) * 20;
      this.lootChestPlayerCooldown = (long)MMOCore.plugin.getConfig().getDouble("player-cooldown") * 1000L;
      this.globalSkillCooldown = MMOCore.plugin.getConfig().getLong("global-skill-cooldown") * 50L;
      this.noSkillBoundPlaceholder = String.valueOf(this.messages.get("no-skill-placeholder"));
      this.lootChestsChanceWeight = MMOCore.plugin.getConfig().getDouble("chance-stat-weight.loot-chests");
      this.dropItemsChanceWeight = MMOCore.plugin.getConfig().getDouble("chance-stat-weight.drop-items");
      this.fishingDropsChanceWeight = MMOCore.plugin.getConfig().getDouble("chance-stat-weight.fishing-drops");
      this.maxPartyLevelDifference = MMOCore.plugin.getConfig().getInt("party.max-level-difference");
      this.partyMaxExpSplitRange = MMOCore.plugin.getConfig().getDouble("party.max-exp-split-range");
      this.splitMainExp = MMOCore.plugin.getConfig().getBoolean("party.main-exp-split");
      this.splitProfessionExp = MMOCore.plugin.getConfig().getBoolean("party.profession-exp-split");
      this.disableQuestBossBar = MMOCore.plugin.getConfig().getBoolean("mmocore-quests.disable-boss-bar");
      this.forceClassSelection = MMOCore.plugin.getConfig().getBoolean("force-class-selection");
      this.waypointWarpTime = MMOCore.plugin.getConfig().getInt("waypoints.default-warp-time");
      this.waypointAutoPathCalculation = MMOCore.plugin.getConfig().getBoolean("waypoints.auto_path_calculation");
      this.waypointLinkReciprocity = MMOCore.plugin.getConfig().getBoolean("waypoints.link_reciprocity");
      this.pvpModeEnabled = config.getBoolean("pvp_mode.enabled");
      this.pvpModeToggleOnCooldown = config.getDouble("pvp_mode.cooldown.toggle_on");
      this.pvpModeToggleOffCooldown = config.getDouble("pvp_mode.cooldown.toggle_off");
      this.pvpModeCombatCooldown = config.getDouble("pvp_mode.cooldown.combat");
      this.pvpModeRegionEnterCooldown = config.getDouble("pvp_mode.cooldown.region_enter");
      this.pvpModeRegionLeaveCooldown = config.getDouble("pvp_mode.cooldown.region_leave");
      this.pvpModeCombatTimeout = config.getDouble("pvp_mode.combat_timeout");
      this.pvpModeInvulnerabilityTimeCommand = config.getDouble("pvp_mode.invulnerability.time.command");
      this.pvpModeInvulnerabilityTimeRegionChange = config.getDouble("pvp_mode.invulnerability.time.region_change");
      this.pvpModeInvulnerabilityCanDamage = config.getBoolean("pvp_mode.invulnerability.can_damage");
      this.minCombatLevel = config.getInt("pvp_mode.min_level");
      this.maxCombatLevelDifference = config.getInt("pvp_mode.max_level_difference");
      this.skillTreeScrollStepX = config.getInt("skill-tree-scroll-step-x", 1);
      this.skillTreeScrollStepY = config.getInt("skill-tree-scroll-step-y", 1);
      this.staminaFull = this.getColorOrDefault("stamina-whole", ChatColor.GREEN);
      this.staminaHalf = this.getColorOrDefault("stamina-half", ChatColor.DARK_GRAY);
      this.staminaEmpty = this.getColorOrDefault("stamina-empty", ChatColor.WHITE);
      this.passiveSkillsNeedBinding = MMOCore.plugin.getConfig().getBoolean("passive-skill-need-bound");
      this.canCreativeCast = MMOCore.plugin.getConfig().getBoolean("can-creative-cast");
      this.cobbleGeneratorXP = MMOCore.plugin.getConfig().getBoolean("should-cobblestone-generators-give-exp");
      this.saveDefaultClassInfo = MMOCore.plugin.getConfig().getBoolean("save-default-class-info");
      this.overrideVanillaExp = MMOCore.plugin.getConfig().getBoolean("override-vanilla-exp");
   }

   @NotNull
   private ChatColor getColorOrDefault(String key, ChatColor defaultColor) {
      try {
         return ChatColor.valueOf(MMOCore.plugin.getConfig().getString("resource-bar-colors." + key).toUpperCase());
      } catch (IllegalArgumentException exception) {
         MMOCore.log(Level.WARNING, "Could not read resource bar color from '" + key + "': using default.");
         return defaultColor;
      }
   }

   @Deprecated
   public PlayerInput newPlayerInput(Player player, PlayerInput.InputType type, Consumer<String> output) {
      return new ChatInput(player, type, null, output);
   }

   public void copyDefaultFile(String path) {
      FileUtils.copyDefaultFile(MMOCore.plugin, path);
   }

   @Deprecated
   public void loadDefaultFile(String name) {
      this.copyDefaultFile(name);
   }

   @Deprecated
   public void copyDefaultFile(String path, String name) {
      if (path.isEmpty()) {
         this.copyDefaultFile(name);
      } else {
         this.copyDefaultFile(path + "/" + name);
      }
   }

   @Deprecated
   public List<String> getMessage(String key) {
      return this.messages.getStringList(key);
   }

   @Nullable
   public Object getMessageObject(String key) {
      return this.messages.get(key);
   }

   @Deprecated
   public ConfigManager.SimpleMessage getSimpleMessage(String key, String... placeholders) {
      ConfigManager.SimpleMessage wrapper = new ConfigManager.SimpleMessage(ConfigMessage.fromKey(key));
      wrapper.message.addPlaceholders(placeholders);
      return wrapper;
   }

   @Deprecated
   public static class SimpleMessage {
      private final ConfigMessage message;

      @Deprecated
      public SimpleMessage(ConfigMessage message) {
         this.message = message;
      }

      @Deprecated
      public String message() {
         return this.message.getLines().isEmpty() ? "" : this.message.getLines().get(0);
      }

      @Deprecated
      public boolean send(Player player) {
         this.message.send(player);
         return !this.message.getLines().isEmpty();
      }
   }
}
