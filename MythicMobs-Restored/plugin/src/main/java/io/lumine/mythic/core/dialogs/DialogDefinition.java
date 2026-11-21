package io.lumine.mythic.core.dialogs;

import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillTargeter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;

@Getter
public class DialogDefinition {
   private final String name;
   private final PlaceholderString title;
   private final PlaceholderString externalTitle;
   private final boolean canClose;
   private final boolean pause;
   private final String afterAction;
   private final List<DialogDefinition.BodyElement> body;
   private final List<DialogDefinition.InputElement> inputs;
   private final List<DialogDefinition.ButtonElement> buttons;
   private final DialogDefinition.ButtonElement exitButton;
   private final int columns;

   public DialogDefinition(
      String name,
      PlaceholderString title,
      PlaceholderString externalTitle,
      boolean canClose,
      boolean pause,
      String afterAction,
      List<DialogDefinition.BodyElement> body,
      List<DialogDefinition.InputElement> inputs,
      List<DialogDefinition.ButtonElement> buttons,
      DialogDefinition.ButtonElement exitButton,
      int columns
   ) {
      this.name = name;
      this.title = title;
      this.externalTitle = externalTitle;
      this.canClose = canClose;
      this.pause = pause;
      this.afterAction = afterAction;
      this.body = body != null ? body : Collections.emptyList();
      this.inputs = inputs != null ? inputs : Collections.emptyList();
      this.buttons = buttons != null ? buttons : Collections.emptyList();
      this.exitButton = exitButton;
      this.columns = columns;
   }

   @Getter
   public static class BodyElement {
      private final String type;
      private final PlaceholderString message;
      private final PlaceholderString item;
      private final int width;
      private final int height;
      private final boolean decoration;
      private final boolean showTooltip;
      private final List<SkillCondition> conditions;
      private final DialogDefinition.ForEach forEach;

      public BodyElement(
         String type,
         PlaceholderString message,
         PlaceholderString item,
         int width,
         int height,
         boolean decoration,
         boolean showTooltip,
         List<SkillCondition> conditions,
         DialogDefinition.ForEach forEach
      ) {
         this.type = type;
         this.message = message;
         this.item = item;
         this.width = width;
         this.height = height;
         this.decoration = decoration;
         this.showTooltip = showTooltip;
         this.conditions = conditions != null ? conditions : Collections.emptyList();
         this.forEach = forEach;
      }
   }

   @Getter
   public static class ButtonElement {
      private final PlaceholderString label;
      private final PlaceholderString tooltip;
      private final int width;
      private final String skillName;
      private Optional<Skill> skill;
      private final Optional<Skill> inlineSkill;
      private final List<SkillCondition> conditions;
      private final DialogDefinition.ForEach forEach;

      public ButtonElement(
         PlaceholderString label,
         PlaceholderString tooltip,
         int width,
         String skillName,
         Optional<Skill> inlineSkill,
         List<SkillCondition> conditions,
         DialogDefinition.ForEach forEach
      ) {
         this.label = label;
         this.tooltip = tooltip;
         this.width = width;
         this.skillName = skillName;
         this.skill = Optional.empty();
         this.inlineSkill = inlineSkill;
         this.conditions = conditions != null ? conditions : Collections.emptyList();
         this.forEach = forEach;
      }

      public void setSkill(Optional<Skill> skill) {
         this.skill = skill;
      }
   }

   public record ForEach(SkillTargeter targeter, PlaceholderString values, int pageSize) {
   }

   @Getter
   public static class InputElement {
      private final String type;
      private final String key;
      private final PlaceholderString label;
      private final int width;
      private final boolean labelVisible;
      private final int maxLength;
      private final boolean multiline;
      private final Integer maxLines;
      private final Integer height;
      private final float start;
      private final float end;
      private final PlaceholderString initial;
      private final Float step;
      private final String labelFormat;
      private final Map<String, PlaceholderString> options;
      private final List<SkillCondition> conditions;

      public InputElement(
         String type,
         String key,
         PlaceholderString label,
         int width,
         boolean labelVisible,
         int maxLength,
         boolean multiline,
         Integer maxLines,
         Integer height,
         float start,
         float end,
         PlaceholderString initial,
         Float step,
         String labelFormat,
         Map<String, PlaceholderString> options,
         List<SkillCondition> conditions
      ) {
         this.type = type;
         this.key = key;
         this.label = label;
         this.width = width;
         this.labelVisible = labelVisible;
         this.maxLength = maxLength;
         this.multiline = multiline;
         this.maxLines = maxLines;
         this.height = height;
         this.start = start;
         this.end = end;
         this.initial = initial;
         this.step = step;
         this.labelFormat = labelFormat;
         this.options = options != null ? options : Collections.emptyMap();
         this.conditions = conditions != null ? conditions : Collections.emptyList();
      }
   }
}
