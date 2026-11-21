package io.lumine.mythic.core.dialogs;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.config.properties.Property;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.core.config.IOLoader;
import io.lumine.mythic.core.config.MythicConfigImpl;
import io.lumine.mythic.core.constants.ConfigFileType;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.packs.PackExecutor;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillTriggers;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.entity.Player;

public class DialogManager extends ReloadableModule<MythicBukkit> {
   private ConcurrentMap<String, DialogDefinition> dialogs;

   public DialogManager(MythicBukkit plugin) {
      super(plugin);
   }

   public void load(MythicBukkit plugin) {
      if (this.dialogs == null) {
         this.dialogs = new ConcurrentHashMap<>();
      }

      this.dialogs.clear();
      this.getPlugin().getClock().queueAfterLoad(this::loadDialogs);
   }

   private void loadDialogs() {
      this.dialogs.clear();
      new IOLoader<>(MythicBukkit.inst(), "ExampleDialogs.yml", "Dialogs");

      for (PackExecutor.ConfigFile configFile : this.getPlugin().getPackManager().getConfigFiles(ConfigFileType.DIALOG)) {
         for (String key : Property.NodeList(this.getPlugin(), configFile.file(), "").get()) {
            this.loadDialog(configFile.file(), key);
         }
      }

      MythicLogger.log("Loaded {0} dialog definition(s)", this.dialogs.size());
   }

   @Override
   public void unload() {
      this.dialogs.clear();
   }

   public Optional<DialogDefinition> getDialog(String name) {
      return Optional.ofNullable(this.dialogs.get(name));
   }

   public Collection<String> getDialogNames() {
      return this.dialogs.keySet();
   }

   public boolean open(Player player, String dialog, Map<String, String> values) {
      if (MythicBukkit.isVolatile() && this.dialogs.containsKey(dialog)) {
         Optional<Skill> skill = this.getPlugin().getSkillManager().getSkill("[ - showDialog{dialog=" + dialog + "} ]");
         if (skill.isEmpty()) {
            return false;
         } else {
            AbstractEntity entity = BukkitAdapter.adapt(player);
            SkillCaster caster = this.getPlugin().getSkillManager().getCaster(entity);
            SkillMetadata meta = this.getPlugin()
               .getSkillManager()
               .getEventBus()
               .buildSkillMetadata(SkillTriggers.API, caster, entity, entity.getLocation(), true);
            values.forEach((key, value) -> meta.getVariables().putString("dialog." + key, value));
            skill.get().execute(meta);
            return true;
         }
      } else {
         return false;
      }
   }

   private void loadDialog(File file, String key) {
      try {
         MythicConfigImpl config = new MythicConfigImpl(key, file);
         PlaceholderString title = config.getPlaceholderString("Title", "Dialog");
         PlaceholderString externalTitle = config.isSet("ExternalTitle") ? config.getPlaceholderString("ExternalTitle") : null;
         boolean canClose = config.getBoolean("CanClose", true);
         boolean pause = config.getBoolean("Pause", false);
         String afterAction = config.getString("AfterAction", "CLOSE").toUpperCase();
         int columns = config.getInteger("Columns", 1);
         List<DialogDefinition.BodyElement> body = this.parseBody(config);
         List<DialogDefinition.InputElement> inputs = this.parseInputs(config);
         List<DialogDefinition.ButtonElement> buttons = this.parseButtons(config, file, key);
         DialogDefinition.ButtonElement exitButton = this.parseExitButton(config, file, key);
         DialogDefinition definition = new DialogDefinition(key, title, externalTitle, canClose, pause, afterAction, body, inputs, buttons, exitButton, columns);
         this.dialogs.put(key, definition);
      } catch (Exception e) {
         MythicLogger.error("Failed to load dialog definition ''{0}'' from {1}: {2}", key, file.getName(), e.getMessage());
      }
   }

   private List<DialogDefinition.BodyElement> parseBody(MythicConfig config) {
      if (!config.contains("Body")) {
         return Collections.emptyList();
      } else {
         List<MythicConfig> bodyList = config.getConfigurationList("Body");
         if (bodyList != null && !bodyList.isEmpty()) {
            ArrayList<DialogDefinition.BodyElement> elements = new ArrayList<>();

            for (MythicConfig entry : bodyList) {
               String type = entry.getString("Type", "text").toLowerCase();
               PlaceholderString message = entry.isSet("Message") ? entry.getPlaceholderString("Message") : null;
               PlaceholderString item = entry.isSet("Item") ? entry.getPlaceholderString("Item") : null;
               int width = entry.getInteger("Width", type.equals("item") ? 64 : 256);
               int height = entry.getInteger("Height", 64);
               boolean decoration = entry.getBoolean("Decoration", false);
               boolean showTooltip = entry.getBoolean("ShowTooltip", true);
               List<SkillCondition> conditions = this.parseConditions(entry);
               elements.add(new DialogDefinition.BodyElement(type, message, item, width, height, decoration, showTooltip, conditions, this.parseForEach(entry)));
            }

            return elements;
         } else {
            return Collections.emptyList();
         }
      }
   }

   private List<DialogDefinition.InputElement> parseInputs(MythicConfig config) {
      if (!config.contains("Inputs")) {
         return Collections.emptyList();
      } else {
         List<MythicConfig> inputList = config.getConfigurationList("Inputs");
         if (inputList != null && !inputList.isEmpty()) {
            ArrayList<DialogDefinition.InputElement> elements = new ArrayList<>();

            for (MythicConfig entry : inputList) {
               String type = entry.getString("Type", "text").toLowerCase();
               String key = entry.getString("Key", "input");
               PlaceholderString label = entry.getPlaceholderString("Label", key);
               int width = entry.getInteger("Width", 200);
               boolean labelVisible = entry.getBoolean("LabelVisible", true);
               int maxLength = entry.getInteger("MaxLength", 256);
               boolean multiline = entry.getBoolean("Multiline", false);
               Integer maxLines = entry.isSet("MaxLines") ? entry.getInteger("MaxLines", 0) : null;
               Integer height = entry.isSet("Height") ? entry.getInteger("Height", 0) : null;
               float start = (float)entry.getDouble("Start", 0.0);
               float end = (float)entry.getDouble("End", 100.0);
               PlaceholderString initial = entry.isSet("Initial") ? entry.getPlaceholderString("Initial") : null;
               Float step = entry.isSet("Step") ? (float)entry.getDouble("Step", 1.0) : null;
               String labelFormat = entry.getString("LabelFormat", null);
               List<SkillCondition> conditions = this.parseConditions(entry);
               Map<String, PlaceholderString> options = null;
               if (type.equals("option") && entry.contains("Options")) {
                  options = new LinkedHashMap<>();

                  for (String optKey : entry.getKeys("Options")) {
                     MythicConfig nestedConfig = entry.getNestedConfig("Options");
                     options.put(optKey, nestedConfig.getPlaceholderString(optKey, optKey));
                  }
               }

               elements.add(
                  new DialogDefinition.InputElement(
                     type, key, label, width, labelVisible, maxLength, multiline, maxLines, height, start, end, initial, step, labelFormat, options, conditions
                  )
               );
            }

            return elements;
         } else {
            return Collections.emptyList();
         }
      }
   }

   private List<DialogDefinition.ButtonElement> parseButtons(MythicConfig config, File file, String dialogKey) {
      if (!config.contains("Buttons")) {
         return Collections.emptyList();
      } else {
         List<MythicConfig> buttonList = config.getConfigurationList("Buttons");
         if (buttonList != null && !buttonList.isEmpty()) {
            ArrayList<DialogDefinition.ButtonElement> elements = new ArrayList<>();

            for (MythicConfig entry : buttonList) {
               elements.add(this.parseButtonElement(entry, file, dialogKey));
            }

            return elements;
         } else {
            return Collections.emptyList();
         }
      }
   }

   private DialogDefinition.ButtonElement parseExitButton(MythicConfig config, File file, String dialogKey) {
      if (!config.contains("ExitButton")) {
         return null;
      } else {
         MythicConfig exitConfig = config.getNestedConfig("ExitButton");
         return this.parseButtonElement(exitConfig, file, dialogKey);
      }
   }

   private DialogDefinition.ButtonElement parseButtonElement(MythicConfig entry, File file, String dialogKey) {
      PlaceholderString label = entry.getPlaceholderString("Label", "Button");
      PlaceholderString tooltip = entry.isSet("Tooltip") ? entry.getPlaceholderString("Tooltip") : null;
      int width = entry.getInteger("Width", 150);
      String skillName = entry.getString("Skill", null);
      Optional<Skill> inlineSkill = entry.isSet("Skills") ? this.getPlugin().getSkillManager().getSkill(file, entry.getStringList("Skills")) : Optional.empty();
      List<SkillCondition> conditions = this.parseConditions(entry);
      DialogDefinition.ButtonElement button = new DialogDefinition.ButtonElement(
         label, tooltip, width, skillName, inlineSkill, conditions, this.parseForEach(entry)
      );
      if (skillName != null) {
         this.getPlugin().getSkillManager().queueSecondPass(() -> {
            Optional<Skill> skill = this.getPlugin().getSkillManager().getSkill(skillName);
            if (skill.isEmpty()) {
               MythicLogger.error("Dialog ''{0}'': could not find skill ''{1}''", dialogKey, skillName);
            }

            button.setSkill(skill);
         });
      }

      return button;
   }

   private DialogDefinition.ForEach parseForEach(MythicConfig config) {
      String source = config.getString("ForEach", null);
      if (source == null) {
         return null;
      } else {
         int pageSize = config.getInteger("PageSize", 0);
         return source.startsWith("@")
            ? new DialogDefinition.ForEach(this.getPlugin().getSkillManager().getTargeter(source), null, pageSize)
            : new DialogDefinition.ForEach(null, PlaceholderString.of(source), pageSize);
      }
   }

   private List<SkillCondition> parseConditions(MythicConfig config) {
      if (!config.contains("Conditions")) {
         return Collections.emptyList();
      } else {
         List<String> conditionLines = config.getStringList("Conditions");
         return conditionLines != null && !conditionLines.isEmpty()
            ? this.getPlugin().getSkillManager().getConditions(conditionLines)
            : Collections.emptyList();
      }
   }
}
