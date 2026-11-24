package net.Indyuce.mmocore.api.player.profess;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.skill.PassiveSkill;
import io.lumine.mythic.lib.script.Script;
import io.lumine.mythic.lib.skill.SimpleSkill;
import io.lumine.mythic.lib.skill.Skill;
import io.lumine.mythic.lib.skill.handler.MythicLibSkillHandler;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import io.lumine.mythic.lib.util.FileUtils;
import io.lumine.mythic.lib.util.PostLoadAction;
import io.lumine.mythic.lib.util.PreloadedObject;
import io.lumine.mythic.lib.version.VParticle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.profess.event.EventTrigger;
import net.Indyuce.mmocore.api.player.profess.resource.ManaDisplayOptions;
import net.Indyuce.mmocore.api.player.profess.resource.PlayerResource;
import net.Indyuce.mmocore.api.player.profess.resource.ResourceRegeneration;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import net.Indyuce.mmocore.api.util.math.formula.LinearValue;
import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmocore.experience.ExpCurve;
import net.Indyuce.mmocore.experience.ExperienceObject;
import net.Indyuce.mmocore.experience.droptable.ExperienceTable;
import net.Indyuce.mmocore.loot.chest.particle.CastingParticle;
import net.Indyuce.mmocore.player.stats.StatInfo;
import net.Indyuce.mmocore.skill.ClassSkill;
import net.Indyuce.mmocore.skill.RegisteredSkill;
import net.Indyuce.mmocore.skill.binding.SkillSlot;
import net.Indyuce.mmocore.skill.cast.ComboMap;
import net.Indyuce.mmocore.skilltree.tree.SkillTree;
import net.Indyuce.mmocore.util.Icon;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerClass implements ExperienceObject, PreloadedObject {
   private final String name;
   private final String id;
   private final String actionBarFormat;
   private final List<String> description = new ArrayList<>();
   private final List<String> attrDescription = new ArrayList<>();
   private final ItemStack icon;
   private final Map<ClassOption, Boolean> options = new HashMap<>();
   private final int maxLevel;
   private final int displayOrder;
   @NotNull
   private final ManaDisplayOptions manaDisplay;
   @NotNull
   private final ExpCurve expCurve;
   @Nullable
   private final ExperienceTable expTable;
   @Nullable
   private final CastingParticle castParticle;
   private final List<SkillSlot> skillSlots = new ArrayList<>();
   private final List<SkillTree> skillTrees = new ArrayList<>();
   private final List<PassiveSkill> classScripts = new ArrayList<>();
   private final Map<String, LinearValue> stats = new HashMap<>();
   private final Map<String, ClassSkill> skills = new LinkedHashMap<>();
   private final List<Subclass> subclasses = new ArrayList<>();
   private final Map<PlayerResource, ResourceRegeneration> resourceHandlers = new HashMap<>();
   @Nullable
   private final ComboMap comboMap;
   @Deprecated
   private final Map<String, EventTrigger> eventTriggers = new HashMap<>();
   private final PostLoadAction postLoadAction = new PostLoadAction(
      configx -> {
         if (configx.contains("subclasses")) {
            for (String keyx : configx.getConfigurationSection("subclasses").getKeys(false)) {
               try {
                  this.subclasses
                     .add(
                        new Subclass(
                           MMOCore.plugin.classManager.getOrThrow(keyx.toUpperCase().replace("-", "_").replace(" ", "_")), configx.getInt("subclasses." + keyx)
                        )
                     );
               } catch (IllegalArgumentException exception) {
                  MMOCore.plugin
                     .getLogger()
                     .log(Level.WARNING, "Could not load subclass '" + keyx + "' from class '" + this.getId() + "': " + exception.getMessage());
               }
            }
         }
      }
   );

   public PlayerClass(String id, ConfigurationSection config) {
      this.postLoadAction.cacheConfig(config);
      this.id = UtilityMethods.enumName(id);
      this.name = MythicLib.plugin.parseColors(config.getString("display.name", "INVALID DISPLAY NAME"));
      this.icon = Icon.from(config.get("display.item", "BARRIER")).toItem();
      if (config.contains("display.texture") && this.icon.getType() == Material.PLAYER_HEAD) {
         ItemMeta meta = this.icon.getItemMeta();
         UtilityMethods.setTextureValue((SkullMeta)meta, config.getString("display.texture"));
         this.icon.setItemMeta(meta);
      }

      for (String string : config.getStringList("display.lore")) {
         this.description.add(ChatColor.GRAY + MythicLib.plugin.parseColors(string));
      }

      for (String string : config.getStringList("display.attribute-lore")) {
         this.attrDescription.add(ChatColor.GRAY + MythicLib.plugin.parseColors(string));
      }

      this.manaDisplay = config.contains("mana") ? new ManaDisplayOptions(config.getConfigurationSection("mana")) : ManaDisplayOptions.DEFAULT;
      this.maxLevel = config.getInt("max-level");
      this.displayOrder = config.getInt("display.order");
      this.actionBarFormat = config.contains("action-bar", true) ? config.getString("action-bar") : null;
      this.expCurve = config.contains("exp-curve")
         ? MMOCore.plugin.experience.getCurveOrThrow(config.get("exp-curve").toString().toLowerCase().replace("_", "-").replace(" ", "-"))
         : ExpCurve.DEFAULT;
      ExperienceTable expTable = null;
      if (config.contains("exp-table")) {
         try {
            expTable = MMOCore.plugin.experience.loadExperienceTable(config.get("exp-table"));
         } catch (RuntimeException exception) {
            MMOCore.plugin.getLogger().log(Level.WARNING, "Could not load exp table from class '" + id + "': " + exception.getMessage());
         }
      }

      this.expTable = expTable;
      if (config.contains("skill-trees")) {
         for (String str : config.getStringList("skill-trees")) {
            try {
               this.skillTrees.add(MMOCore.plugin.skillTreeManager.get(str));
            } catch (Exception e) {
               MMOCore.log(Level.WARNING, "Could not find skill tree with ID: " + str);
            }
         }
      }

      if (config.contains("scripts")) {
         for (String key : config.getConfigurationSection("scripts").getKeys(false)) {
            try {
               TriggerType trigger = TriggerType.valueOf(UtilityMethods.enumName(key));
               Script script = MythicLib.plugin.getSkills().loadScript(config.getConfigurationSection("scripts." + key));
               Skill castSkill = new SimpleSkill(trigger, new MythicLibSkillHandler(script));
               PassiveSkill skill = new PassiveSkill("MMOCoreClassScript", castSkill, EquipmentSlot.OTHER, ModifierSource.OTHER);
               this.classScripts.add(skill);
            } catch (IllegalArgumentException exception) {
               MMOCore.plugin.getLogger().log(Level.WARNING, "Could not load script '" + key + "' from class '" + id + "': " + exception.getMessage());
            }
         }
      }

      ComboMap comboMap = null;
      if (config.contains("key-combos")) {
         try {
            comboMap = new ComboMap(config.getConfigurationSection("key-combos"));
         } catch (Exception exception) {
            MMOCore.plugin.getLogger().log(Level.WARNING, "Could not load combo map from class '" + id + "': " + exception.getMessage());
         }
      }

      this.comboMap = comboMap;
      if (config.contains("triggers")) {
         for (String key : config.getConfigurationSection("triggers").getKeys(false)) {
            try {
               String format = key.toLowerCase().replace("_", "-").replace(" ", "-");
               this.eventTriggers.put(format, new EventTrigger(format, config.getStringList("triggers." + key)));
            } catch (IllegalArgumentException exception) {
               MMOCore.log(Level.WARNING, "Could not load trigger '" + key + "' from class '" + id + "':" + exception.getMessage());
            }
         }
      }

      if (config.contains("attributes")) {
         for (String key : config.getConfigurationSection("attributes").getKeys(false)) {
            try {
               this.stats.put(UtilityMethods.enumName(key), new LinearValue(config.getConfigurationSection("attributes." + key)));
            } catch (IllegalArgumentException exception) {
               MMOCore.plugin.getLogger().log(Level.WARNING, "Could not load stat info '" + key + "' from class '" + id + "': " + exception.getMessage());
            }
         }
      }

      if (config.isConfigurationSection("skill-slots")) {
         FileUtils.iterateConfigSectionList(
            config.getConfigurationSection("skill-slots"),
            this.skillSlots,
            SkillSlot::new,
            index -> new SkillSlot(
               index, 0, "true", "&eUnconfigured Skill Slot " + MMOCoreUtils.intToRoman(index), new ArrayList<>(), false, true, new ArrayList<>()
            ),
            (keyx, exceptionx) -> MMOCore.plugin
               .getLogger()
               .log(Level.WARNING, "Could not load skill slot '" + keyx + "' from class '" + this.getId() + "': " + exceptionx.getMessage())
         );
      }

      for (RegisteredSkill registered : MMOCore.plugin.skillManager.getAll()) {
         String key = registered.getHandler().getId();
         if (config.contains("skills." + key)) {
            this.skills.put(key, new ClassSkill(registered, config.getConfigurationSection("skills." + key)));
         } else {
            this.skills.put(key, new ClassSkill(registered, 1, 1, false));
         }
      }

      CastingParticle castingParticle;
      try {
         castingParticle = config.contains("cast-particle") ? new CastingParticle(config.getConfigurationSection("cast-particle")) : null;
      } catch (RuntimeException exception) {
         MMOCore.plugin.getLogger().log(Level.WARNING, "Could not load casting mode particle of class '" + this.getId() + "': " + exception.getMessage());
         castingParticle = null;
      }

      this.castParticle = castingParticle;
      if (config.contains("options")) {
         for (String key : config.getConfigurationSection("options").getKeys(false)) {
            try {
               this.setOption(ClassOption.valueOf(key.toUpperCase().replace("-", "_").replace(" ", "_")), config.getBoolean("options." + key));
            } catch (IllegalArgumentException exception) {
               MMOCore.plugin
                  .getLogger()
                  .log(Level.WARNING, "Could not load option '" + key + "' from class '" + this.getId() + "': " + exception.getMessage());
            }
         }
      }

      if (config.contains("main-exp-sources")) {
         for (String key : config.getStringList("main-exp-sources")) {
            try {
               MMOCore.plugin.experience.registerSource(MMOCore.plugin.loadManager.loadExperienceSource(new MMOLineConfig(key), this));
            } catch (RuntimeException exception) {
               MMOCore.plugin.getLogger().log(Level.WARNING, "Could not load exp source '" + key + "' from class '" + id + "': " + exception.getMessage());
            }
         }
      }

      for (PlayerResource resource : PlayerResource.values()) {
         if (config.isConfigurationSection("resource." + resource.name().toLowerCase())) {
            try {
               this.resourceHandlers
                  .put(resource, new ResourceRegeneration(resource, config.getConfigurationSection("resource." + resource.name().toLowerCase())));
            } catch (IllegalArgumentException exception) {
               MMOCore.log(
                  Level.WARNING, "Could not load special " + resource.name().toLowerCase() + " regen from class '" + id + "': " + exception.getMessage()
               );
               this.resourceHandlers.put(resource, new ResourceRegeneration(resource));
            }
         } else {
            this.resourceHandlers.put(resource, new ResourceRegeneration(resource));
         }
      }
   }

   public PlayerClass(String id, String name, Material material) {
      this.id = id;
      this.name = name;
      this.manaDisplay = ManaDisplayOptions.DEFAULT;
      this.maxLevel = 0;
      this.displayOrder = 0;
      this.expCurve = ExpCurve.DEFAULT;
      this.expTable = null;
      this.comboMap = null;
      this.castParticle = new CastingParticle(VParticle.INSTANT_EFFECT.get());
      this.actionBarFormat = "";
      this.icon = new ItemStack(material);
      this.setOption(ClassOption.DISPLAY, false);
      this.setOption(ClassOption.DEFAULT, false);

      for (PlayerResource resource : PlayerResource.values()) {
         this.resourceHandlers.put(resource, new ResourceRegeneration(resource));
      }
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   @Override
   public String getKey() {
      return "class_" + this.getId();
   }

   @NotNull
   public ManaDisplayOptions getManaDisplay() {
      return this.manaDisplay;
   }

   @NotNull
   public ResourceRegeneration getHandler(PlayerResource resource) {
      return this.resourceHandlers.get(resource);
   }

   public int getMaxLevel() {
      return this.maxLevel;
   }

   public int getDisplayOrder() {
      return this.displayOrder;
   }

   @NotNull
   public PostLoadAction getPostLoadAction() {
      return this.postLoadAction;
   }

   @Override
   public ExpCurve getExpCurve() {
      return this.expCurve;
   }

   @NotNull
   @Override
   public ExperienceTable getExperienceTable() {
      return Objects.requireNonNull(this.expTable, "Class has no exp table");
   }

   @Override
   public boolean hasExperienceTable() {
      return this.expTable != null;
   }

   public ItemStack getIcon() {
      return this.icon.clone();
   }

   @Nullable
   public CastingParticle getCastParticle() {
      return this.castParticle;
   }

   public List<String> getDescription() {
      return this.description;
   }

   public List<String> getAttributeDescription() {
      return this.attrDescription;
   }

   public void setOption(ClassOption option, boolean value) {
      this.options.put(option, value);
   }

   public boolean hasOption(ClassOption option) {
      return this.options.getOrDefault(option, option.getDefault());
   }

   @Override
   public void giveExperience(PlayerData playerData, double experience, @Nullable Location hologramLocation, EXPSource source) {
      hologramLocation = !MMOCore.plugin.getConfig().getBoolean("display-main-class-exp-holograms") ? null : hologramLocation;
      playerData.giveExperience(experience, source, hologramLocation, true);
   }

   @Override
   public boolean shouldHandle(PlayerData playerData) {
      return this.equals(playerData.getProfess());
   }

   @NotNull
   public List<PassiveSkill> getScripts() {
      return this.classScripts;
   }

   @Deprecated
   public Set<String> getEventTriggers() {
      return this.eventTriggers.keySet();
   }

   @Deprecated
   public boolean hasEventTriggers(String name) {
      return this.eventTriggers.containsKey(name);
   }

   @Deprecated
   public EventTrigger getEventTriggers(String name) {
      return this.eventTriggers.get(name);
   }

   public void setDefaultStatFormula(String type, LinearValue value) {
      this.stats.put(UtilityMethods.enumName(type), value);
   }

   public double calculateStat(String stat, int level) {
      return this.getStatInfo(stat).calculate(level);
   }

   public List<Subclass> getSubclasses() {
      return this.subclasses;
   }

   public boolean hasSubclass(PlayerClass profess) {
      for (Subclass sub : this.subclasses) {
         if (sub.getProfess().equals(profess) || sub.getProfess().hasSubclass(profess)) {
            return true;
         }
      }

      return false;
   }

   @Deprecated
   public boolean hasSkill(RegisteredSkill skill) {
      return this.hasSkill(skill.getHandler().getId());
   }

   @Deprecated
   public boolean hasSkill(String id) {
      return this.skills.containsKey(id);
   }

   public boolean hasSlot(int slot) {
      return 1 <= slot && slot <= this.skillSlots.size();
   }

   public List<SkillTree> getSkillTrees() {
      return this.skillTrees;
   }

   @Nullable
   public SkillSlot getSkillSlot(int slot) {
      return this.hasSlot(slot) ? this.skillSlots.get(slot - 1) : null;
   }

   @NotNull
   public List<SkillSlot> getSlots() {
      return this.skillSlots;
   }

   @NotNull
   public ClassSkill getSkill(RegisteredSkill skill) {
      return this.getSkill(skill.getHandler().getId());
   }

   @Nullable
   public ClassSkill getSkill(String id) {
      return this.skills.get(id);
   }

   @Deprecated
   public Collection<ClassSkill> getSkills() {
      return this.skills.values();
   }

   public Set<String> getStats() {
      return this.stats.keySet();
   }

   @Nullable
   public ComboMap getComboMap() {
      return this.comboMap;
   }

   @NotNull
   private LinearValue getStatInfo(String text) {
      LinearValue linearValue = this.stats.get(text);
      return linearValue == null ? StatInfo.valueOf(text).getDefaultFormula() : linearValue;
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof PlayerClass && ((PlayerClass)obj).id.equals(this.id);
   }

   @Nullable
   public String getActionBar() {
      return this.actionBarFormat;
   }

   public boolean hasActionBar() {
      return this.actionBarFormat != null;
   }
}
