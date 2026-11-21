package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicDialogClickEvent;
import io.lumine.mythic.bukkit.events.MythicDialogOpenEvent;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.dialogs.DialogDefinition;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.DialogBase.Builder;
import io.papermc.paper.registry.data.dialog.DialogBase.DialogAfterAction;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.BooleanDialogInput;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.NumberRangeDialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput.OptionEntry;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput.MultilineOptions;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickCallback.Options;
import net.kyori.adventure.text.format.StyleBuilderApplicable;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@MythicMechanic(
   name = "showDialog",
   aliases = {"openDialog", "dialog"},
   premium = true,
   description = "Shows a Paper dialog to the target player"
)
public class ShowDialogMechanic extends SkillMechanic implements ITargetedEntitySkill, INoTargetSkill {
   private static final int TEXT_CLICK = -2;
   private static final String SKILL_TAG = "skill";
   private final PlaceholderString dialogName;

   public ShowDialogMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.target_creative = true;
      this.dialogName = mlc.getPlaceholderString(new String[]{"dialog", "d", "name"}, "default");
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      return this.showDialogFor(data, data.getCaster().getEntity());
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      return this.showDialogFor(data, target);
   }

   private SkillResult showDialogFor(SkillMetadata data, AbstractEntity target) {
      if (!MythicBukkit.isVolatile()) {
         MythicLogger.errorMechanicConfig(this, this.config, "showDialog mechanic requires Mythic Premium");
         return SkillResult.INVALID_CONFIG;
      } else if (!ServerVersion.isPaper()) {
         MythicLogger.errorMechanicConfig(this, this.config, "showDialog mechanic requires Paper 1.21.6+");
         return SkillResult.INVALID_VERSION;
      } else if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         String name = this.dialogName.get(data, target);
         Optional<DialogDefinition> maybeDef = MythicBukkit.inst().getDialogManager().getDialog(name);
         if (maybeDef.isEmpty()) {
            MythicLogger.errorMechanicConfig(this, this.config, "Could not find dialog definition '" + name + "'");
            return SkillResult.INVALID_CONFIG;
         } else {
            DialogDefinition def = maybeDef.get();
            Player player = (Player)target.getBukkitEntity();
            Schedulers.runOnRegion(player, () -> this.show(def, data, target, player));
            return SkillResult.SUCCESS;
         }
      }
   }

   private void show(DialogDefinition def, SkillMetadata data, AbstractEntity target, Player player) {
      SkillMetadataImpl meta = data.deeperClone(false);
      meta.setEntityTarget(target);
      long token = DialogTracker.nextToken();
      Map<Object, List<SkillMetadata>> rows = new IdentityHashMap<>();
      int pages = 0;

      for (DialogDefinition.BodyElement el : def.getBody()) {
         pages = this.collectRows(el, el.getForEach(), el.getConditions(), rows, pages, meta, target);
      }

      for (DialogDefinition.ButtonElement el : def.getButtons()) {
         pages = this.collectRows(el, el.getForEach(), el.getConditions(), rows, pages, meta, target);
      }

      int page = Math.max(1, Math.min(pages, meta.getVariables().getInt("dialog.page|1")));
      if (pages > 0) {
         meta.getVariables().putInt("dialog.page", page);
         meta.getVariables().putInt("dialog.pages", pages);
      }

      ArrayList<DialogDefinition.InputElement> visibleInputs = new ArrayList<>();

      for (DialogDefinition.InputElement el : def.getInputs()) {
         if (this.passesConditions(el.getConditions(), meta, target)) {
            visibleInputs.add(el);
         }
      }

      String str = def.getAfterAction();

      DialogAfterAction afterAction = switch (str) {
         case "NONE" -> DialogAfterAction.NONE;
         case "WAIT_FOR_RESPONSE" -> DialogAfterAction.WAIT_FOR_RESPONSE;
         default -> DialogAfterAction.CLOSE;
      };

      try {
         ArrayList<DialogBody> bodyElements = new ArrayList<>();

         for (DialogDefinition.BodyElement elx : def.getBody()) {
            for (SkillMetadata rowMeta : this.instances(elx, elx.getForEach(), elx.getConditions(), rows, page, meta, target)) {
               bodyElements.add(this.buildBody(def, elx, token, rowMeta, target, afterAction));
            }
         }

         ArrayList<DialogInput> inputElements = new ArrayList<>();

         for (DialogDefinition.InputElement inputEl : visibleInputs) {
            inputElements.add(this.buildInput(def, inputEl, meta, target));
         }

         ArrayList<ActionButton> actionButtons = new ArrayList<>();
         boolean generated = false;

         for (DialogDefinition.ButtonElement elx : def.getButtons()) {
            generated |= elx.getForEach() != null;

            for (SkillMetadata rowMeta : this.instances(elx, elx.getForEach(), elx.getConditions(), rows, page, meta, target)) {
               actionButtons.add(this.buildButton(def, elx, actionButtons.size(), token, rowMeta, target, visibleInputs, afterAction, false));
            }
         }

         if (actionButtons.isEmpty()) {
            MythicLogger.errorMechanicConfig(this, this.config, "Dialog '" + def.getName() + "' has no visible buttons after condition filtering");
            return;
         }

         ActionButton exitButton = null;
         if (def.getExitButton() != null && this.passesConditions(def.getExitButton().getConditions(), meta, target)) {
            exitButton = this.buildButton(def, def.getExitButton(), -1, token, meta, target, visibleInputs, afterAction, true);
         }

         Component titleComponent = this.deserialize(def.getTitle().get(meta, target));
         Builder baseBuilder = DialogBase.builder(titleComponent)
            .canCloseWithEscape(def.isCanClose())
            .pause(def.isPause())
            .afterAction(afterAction)
            .body(bodyElements)
            .inputs(inputElements);
         if (def.getExternalTitle() != null) {
            baseBuilder.externalTitle(this.deserialize(def.getExternalTitle().get(meta, target)));
         }

         DialogBase base = baseBuilder.build();
         DialogType dialogType = this.buildDialogType(actionButtons, exitButton, def.getColumns(), generated);
         Dialog dialog = Dialog.create(
            factory -> ((io.papermc.paper.registry.data.dialog.DialogRegistryEntry.Builder)factory.empty()).base(base).type(dialogType)
         );
         MythicDialogOpenEvent openEvent = new MythicDialogOpenEvent(player, def.getName());
         Bukkit.getPluginManager().callEvent(openEvent);
         if (openEvent.isCancelled()) {
            return;
         }

         player.showDialog(dialog);
      } catch (RuntimeException e) {
         MythicLogger.errorMechanicConfig(this, this.config, "Dialog '" + def.getName() + "' could not be shown: " + e);
         return;
      }

      DialogTracker.markOpen(player.getUniqueId(), token);
   }

   private int collectRows(
      Object element,
      DialogDefinition.ForEach forEach,
      List<SkillCondition> conditions,
      Map<Object, List<SkillMetadata>> rows,
      int pages,
      SkillMetadata meta,
      AbstractEntity target
   ) {
      if (forEach == null) {
         return pages;
      } else {
         ArrayList<SkillMetadata> visible = new ArrayList<>();

         for (Map<String, String> row : this.readRows(forEach, meta, target)) {
            SkillMetadata rowMeta = meta.deepClone();
            rowMeta.getParameters().putAll(row);
            if (this.passesConditions(conditions, rowMeta, target)) {
               visible.add(rowMeta);
            }
         }

         rows.put(element, visible);
         return forEach.pageSize() <= 0 ? pages : Math.max(pages, Math.max(1, (visible.size() + forEach.pageSize() - 1) / forEach.pageSize()));
      }
   }

   private List<Map<String, String>> readRows(DialogDefinition.ForEach forEach, SkillMetadata meta, AbstractEntity target) {
      ArrayList<Map<String, String>> rows = new ArrayList<>();
      if (forEach.targeter() != null) {
         Collection<AbstractEntity> targets = MythicBukkit.inst()
            .getSkillManager()
            .processTargets(meta.deepClone(), forEach.targeter(), true, false)
            .getEntityTargets();
         if (targets != null) {
            for (AbstractEntity entity : targets) {
               rows.add(Map.of("value", entity.getName(), "uuid", entity.getUniqueId().toString(), "index", String.valueOf(rows.size())));
            }
         }

         return rows;
      } else {
         String values = forEach.values().get(meta, target);
         if (values.contains("=")) {
            for (String pair : values.split(";")) {
               String[] split = pair.split("=", 2);
               if (split.length == 2) {
                  rows.add(Map.of("key", split[0], "value", split[1], "index", String.valueOf(rows.size())));
               }
            }
         } else if (values.contains(",")) {
            for (String value : values.split(",")) {
               rows.add(Map.of("value", value, "index", String.valueOf(rows.size())));
            }
         } else if (!values.isEmpty()) {
            rows.add(Map.of("value", values, "index", "0"));
         }

         return rows;
      }
   }

   private List<SkillMetadata> instances(
      Object element,
      DialogDefinition.ForEach forEach,
      List<SkillCondition> conditions,
      Map<Object, List<SkillMetadata>> rows,
      int page,
      SkillMetadata meta,
      AbstractEntity target
   ) {
      if (forEach == null) {
         return this.passesConditions(conditions, meta, target) ? List.of(meta) : List.of();
      } else {
         List<SkillMetadata> visible = rows.get(element);
         if (forEach.pageSize() <= 0) {
            return visible;
         } else {
            int from = Math.min(visible.size(), (page - 1) * forEach.pageSize());
            return visible.subList(from, Math.min(visible.size(), from + forEach.pageSize()));
         }
      }
   }

   private DialogBody buildBody(
      DialogDefinition def, DialogDefinition.BodyElement el, long token, SkillMetadata data, AbstractEntity target, DialogAfterAction afterAction
   ) {
      if ("item".equals(el.getType())) {
         io.papermc.paper.registry.data.dialog.body.ItemDialogBody.Builder itemBuilder = DialogBody.item(this.buildItem(def, el, data, target))
            .width(Math.min(el.getWidth(), 256))
            .height(Math.min(el.getHeight(), 256))
            .showDecorations(el.isDecoration())
            .showTooltip(el.isShowTooltip());
         return itemBuilder.build();
      } else {
         String text = el.getMessage() != null ? el.getMessage().get(data, target) : "";
         Component component = MiniMessage.miniMessage().deserialize(text, this.skillTag(def, token, data, afterAction));
         return DialogBody.plainMessage(component, Math.min(el.getWidth(), 1024));
      }
   }

   private TagResolver skillTag(DialogDefinition def, long token, SkillMetadata data, DialogAfterAction afterAction) {
      HashMap<List<String>, Tag> tags = new HashMap<>();
      return TagResolver.resolver("skill", (args, context) -> {
         ArrayList<String> parts = new ArrayList<>();

         while (args.hasNext()) {
            parts.add(args.pop().value());
         }

         if (parts.isEmpty()) {
            throw context.newException("The skill tag needs a skill name", args);
         } else {
            return tags.computeIfAbsent(parts, key -> this.clickTag(def, token, data, afterAction, key));
         }
      });
   }

   private Tag clickTag(DialogDefinition def, long token, SkillMetadata data, DialogAfterAction afterAction, List<String> parts) {
      String name = parts.get(0);
      Optional<Skill> skill = MythicBukkit.inst().getSkillManager().getSkill(name);
      if (skill.isEmpty()) {
         MythicLogger.errorMechanicConfig(this, this.config, "Dialog '" + def.getName() + "' has clickable text for an unknown skill '" + name + "'");
         return Tag.styling(new StyleBuilderApplicable[0]);
      } else {
         LinkedHashMap<String, String> values = new LinkedHashMap<>();

         for (String part : parts.subList(1, parts.size())) {
            String[] pair = part.split("=", 2);
            values.put(pair[0], pair.length == 2 ? pair[1] : "");
         }

         return Tag.styling(new StyleBuilderApplicable[]{ClickEvent.callback(audience -> {
            if (audience instanceof Player p) {
               this.clickText(def, skill.get(), values, token, data, p, afterAction);
            }
         }, (Options)Options.builder().uses(-1).lifetime(Duration.ofMinutes(10L)).build())});
      }
   }

   private ItemStack buildItem(DialogDefinition def, DialogDefinition.BodyElement el, SkillMetadata data, AbstractEntity target) {
      String spec = el.getItem() != null ? el.getItem().get(data, target) : "STONE";
      Material material = Material.matchMaterial(spec);
      if (material != null) {
         return new ItemStack(material);
      } else {
         try {
            ItemStack itemStack = BukkitAdapter.adapt(MythicBukkit.inst().getBootstrap().createItem(spec));
            return itemStack != null ? itemStack : new ItemStack(Material.STONE);
         } catch (IllegalArgumentException e) {
            MythicLogger.errorMechanicConfig(this, this.config, "Dialog '" + def.getName() + "' has an unknown item '" + spec + "'");
            return new ItemStack(Material.STONE);
         }
      }
   }

   private DialogInput buildInput(DialogDefinition def, DialogDefinition.InputElement el, SkillMetadata data, AbstractEntity target) {
      Component labelComponent = this.deserialize(el.getLabel().get(data, target));
      String initial = this.initialValue(el, data, target);
      String type = el.getType();

      return (DialogInput)(switch (type) {
         case "bool" -> {
            io.papermc.paper.registry.data.dialog.input.BooleanDialogInput.Builder builder = DialogInput.bool(el.getKey(), labelComponent);
            if (initial != null) {
               builder.initial(Boolean.parseBoolean(initial));
            }

            BooleanDialogInput booleanDialogInput = builder.build();
            yield booleanDialogInput;
         }
         case "range" -> {
            io.papermc.paper.registry.data.dialog.input.NumberRangeDialogInput.Builder builder = DialogInput.numberRange(
                  el.getKey(), labelComponent, el.getStart(), el.getEnd()
               )
               .width(Math.min(el.getWidth(), 1024));
            if (initial != null) {
               try {
                  builder.initial(clamp(Float.parseFloat(initial), el));
               } catch (NumberFormatException e) {
                  MythicLogger.errorMechanicConfig(
                     this, this.config, "Dialog '" + def.getName() + "' input '" + el.getKey() + "' has a non-numeric initial value '" + initial + "'"
                  );
               }
            }

            if (el.getStep() != null) {
               builder.step(el.getStep());
            }

            if (el.getLabelFormat() != null) {
               builder.labelFormat(el.getLabelFormat());
            }

            NumberRangeDialogInput numberRangeDialogInput = builder.build();
            yield numberRangeDialogInput;
         }
         case "option" -> {
            String selected = initial != null && el.getOptions().containsKey(initial) ? initial : null;
            ArrayList<OptionEntry> entries = new ArrayList<>();
            boolean first = true;

            for (Entry<String, PlaceholderString> opt : el.getOptions().entrySet()) {
               boolean isInitial = selected != null ? opt.getKey().equals(selected) : first;
               entries.add(OptionEntry.create(opt.getKey(), this.deserialize(opt.getValue().get(data, target)), isInitial));
               first = false;
            }

            SingleOptionDialogInput singleOptionDialogInput = DialogInput.singleOption(el.getKey(), labelComponent, entries)
               .width(Math.min(el.getWidth(), 1024))
               .labelVisible(el.isLabelVisible())
               .build();
            yield singleOptionDialogInput;
         }
         default -> {
            io.papermc.paper.registry.data.dialog.input.TextDialogInput.Builder builder = DialogInput.text(el.getKey(), labelComponent)
               .width(Math.min(el.getWidth(), 1024))
               .labelVisible(el.isLabelVisible())
               .maxLength(el.getMaxLength());
            if (initial != null) {
               builder.initial(truncate(initial, el.getMaxLength()));
            }

            if (el.isMultiline()) {
               builder.multiline(MultilineOptions.create(el.getMaxLines(), el.getHeight()));
            }

            TextDialogInput textDialogInput = builder.build();
            yield textDialogInput;
         }
      });
   }

   private String initialValue(DialogDefinition.InputElement el, SkillMetadata data, AbstractEntity target) {
      String variable = "dialog." + el.getKey();
      if (data.getVariables().has(variable)) {
         return data.getVariables().getString(variable);
      } else {
         return el.getInitial() != null ? el.getInitial().get(data, target) : null;
      }
   }

   private ActionButton buildButton(
      DialogDefinition def,
      DialogDefinition.ButtonElement el,
      int index,
      long token,
      SkillMetadata data,
      AbstractEntity target,
      List<DialogDefinition.InputElement> inputs,
      DialogAfterAction afterAction,
      boolean exit
   ) {
      Component labelComponent = this.deserialize(el.getLabel().get(data, target));
      io.papermc.paper.registry.data.dialog.ActionButton.Builder builder = ActionButton.builder(labelComponent).width(Math.min(el.getWidth(), 1024));
      if (el.getTooltip() != null) {
         builder.tooltip(this.deserialize(el.getTooltip().get(data, target)));
      }

      builder.action(DialogAction.customClick((response, audience) -> {
         if (audience instanceof Player p) {
            this.click(def, el, index, token, data, p, inputs, response, afterAction, exit);
         }
      }, (Options)Options.builder().uses(-1).lifetime(Duration.ofMinutes(10L)).build()));
      return builder.build();
   }

   private void click(
      DialogDefinition def,
      DialogDefinition.ButtonElement el,
      int index,
      long token,
      SkillMetadata data,
      Player player,
      List<DialogDefinition.InputElement> inputs,
      DialogResponseView response,
      DialogAfterAction afterAction,
      boolean exit
   ) {
      if (DialogTracker.isCurrent(player.getUniqueId(), token)) {
         Map<String, String> values = this.readValues(inputs, response);
         ArrayList<Skill> skills = new ArrayList<>();
         el.getSkill().ifPresent(skills::add);
         el.getInlineSkill().ifPresent(skills::add);
         boolean ran = values != null && this.runSkills(def, skills, index, data, player, values);
         this.afterClick(player, token, afterAction, exit, ran);
      }
   }

   private void clickText(
      DialogDefinition def, Skill skill, Map<String, String> values, long token, SkillMetadata data, Player player, DialogAfterAction afterAction
   ) {
      if (DialogTracker.isCurrent(player.getUniqueId(), token)) {
         boolean ran = this.runSkills(def, List.of(skill), -2, data, player, new LinkedHashMap<>(values));
         this.afterClick(player, token, afterAction, false, ran);
      }
   }

   private void afterClick(Player player, long token, DialogAfterAction afterAction, boolean exit, boolean ran) {
      UUID uuid = player.getUniqueId();
      if (DialogTracker.isCurrent(uuid, token)) {
         if (afterAction == DialogAfterAction.CLOSE) {
            DialogTracker.markClosed(uuid);
         } else if (exit || afterAction == DialogAfterAction.WAIT_FOR_RESPONSE && !ran) {
            DialogTracker.close(player);
         }
      }
   }

   private Map<String, String> readValues(List<DialogDefinition.InputElement> inputs, DialogResponseView response) {
      LinkedHashMap<String, String> values = new LinkedHashMap<>();

      for (DialogDefinition.InputElement input : inputs) {
         String key = input.getKey();
         String type = input.getType();
         switch (type) {
            case "bool":
               Boolean valuexx = response.getBoolean(key);
               if (valuexx != null) {
                  values.put(key, valuexx.toString());
               }
               break;
            case "range":
               Float valuexxx = response.getFloat(key);
               if (valuexxx != null) {
                  if (valuexxx.isNaN() || valuexxx.isInfinite()) {
                     return null;
                  }

                  values.put(key, Float.toString(clamp(valuexxx, input)));
               }
               break;
            case "option":
               String valuex = response.getText(key);
               if (valuex != null) {
                  if (!input.getOptions().containsKey(valuex)) {
                     return null;
                  }

                  values.put(key, valuex);
               }
               break;
            default:
               String value = response.getText(key);
               if (value != null) {
                  values.put(
                     key,
                     MiniMessage.miniMessage()
                        .escapeTags(truncate(value, input.getMaxLength()), TagResolver.resolver("skill", Tag.styling(new StyleBuilderApplicable[0])))
                  );
               }
         }
      }

      return values;
   }

   private boolean runSkills(DialogDefinition def, List<Skill> skills, int index, SkillMetadata data, Player player, Map<String, String> values) {
      MythicDialogClickEvent clickEvent = new MythicDialogClickEvent(player, def.getName(), index, values);
      Bukkit.getPluginManager().callEvent(clickEvent);
      if (clickEvent.isCancelled()) {
         return false;
      } else {
         SkillMetadataImpl meta = data.deeperClone(false);
         meta.setIsAsync(false);
         meta.setEntityTarget(BukkitAdapter.adapt(player));
         meta.getVariables().putString("dialog.button", String.valueOf(index));
         clickEvent.getValues().forEach((key, value) -> meta.getVariables().putString("dialog." + key, value));
         boolean ran = false;

         for (Skill skill : skills) {
            ran |= skill.executeIfUsable(meta);
         }

         return ran;
      }
   }

   private DialogType buildDialogType(List<ActionButton> buttons, ActionButton exitButton, int columns, boolean generated) {
      if (exitButton != null || buttons.size() > 2 || generated) {
         io.papermc.paper.registry.data.dialog.type.MultiActionType.Builder builder = DialogType.multiAction(buttons);
         if (exitButton != null) {
            builder.exitAction(exitButton);
         }

         builder.columns(columns);
         return builder.build();
      } else {
         return (DialogType)(buttons.size() == 2 ? DialogType.confirmation(buttons.get(0), buttons.get(1)) : DialogType.notice(buttons.get(0)));
      }
   }

   private boolean passesConditions(List<SkillCondition> conditions, SkillMetadata meta, AbstractEntity viewer) {
      if (conditions != null && !conditions.isEmpty()) {
         SkillMetadata viewerMeta = meta.deepClone();
         viewerMeta.setCaster(MythicBukkit.inst().getSkillManager().getCaster(viewer));

         for (SkillCondition condition : conditions) {
            if (!condition.evaluateCaster(viewerMeta)) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   private static float clamp(float value, DialogDefinition.InputElement el) {
      return Math.max(Math.min(el.getStart(), el.getEnd()), Math.min(Math.max(el.getStart(), el.getEnd()), value));
   }

   private static String truncate(String value, int maxLength) {
      if (value.length() <= maxLength) {
         return value;
      } else {
         int end = maxLength;
         if (Character.isHighSurrogate(value.charAt(maxLength - 1))) {
            end = maxLength - 1;
         }

         return value.substring(0, end);
      }
   }

   private Component deserialize(String text) {
      return MiniMessage.miniMessage().deserialize(text != null ? text : "");
   }

   @Override
   public boolean getTargetsSpectators() {
      return true;
   }

   @Override
   public boolean getTargetsCreatives() {
      return true;
   }
}
