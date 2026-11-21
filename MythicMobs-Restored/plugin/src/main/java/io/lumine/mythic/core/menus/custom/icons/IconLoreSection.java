package io.lumine.mythic.core.menus.custom.icons;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.menus.custom.CustomMenuContext;
import io.lumine.mythic.core.skills.SkillCondition;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

public class IconLoreSection {
   @Getter
   private IconLoreTemplate template;
   @Getter
   private Collection<SkillCondition> conditions;
   private Collection<IconLoreTemplateLine> lines = Lists.newArrayList();
   @Getter @Setter
   private boolean filler = false;

   public IconLoreSection(IconLoreTemplate template, List<String> input) {
      this.template = template;

      for (String line : input) {
         this.parseLine(line);
      }
   }

   public IconLoreSection(IconLoreTemplate template, Map<?, ?> in) {
      this.template = template;
      if (in.containsKey("Conditions")) {
         this.conditions = Lists.newArrayList();

         for (String line : (List<String>)in.get("Conditions")) {
            SkillCondition condition = MythicBukkit.inst().getSkillManager().getCondition(line);
            this.conditions.add(condition);
         }
      }

      Object line;
      if (in.containsKey("Line")) {
         line = in.get("Line");
      } else {
         if (!in.containsKey("Filler")) {
            return;
         }

         line = in.get("Filler");
      }

      if (line instanceof String stringLine) {
         this.parseLine(stringLine);
      } else if (line instanceof List) {
         for (String listLine : (List<String>)line) {
            this.parseLine(listLine);
         }
      }
   }

   private void parseLine(String line) {
      this.lines.add(this.template.parseLine(line));
   }

   public boolean hasConditions() {
      return this.conditions != null && !this.conditions.isEmpty();
   }

   public Collection<String> parse(CustomMenuContext context) {
      List<String> lore = Lists.newArrayList();

      for (IconLoreTemplateLine loreLine : this.lines) {
         if (loreLine.isApplicable(context)) {
            if (loreLine instanceof IconLoreTemplateMultiLine loreTemplateMultiLine) {
               List<String> lines = loreTemplateMultiLine.getLine(context);
               if (lines != null) {
                  for (String line : lines) {
                     lore.add(line);
                  }
               }
            } else if (loreLine instanceof IconLoreTemplateSingleLine loreTemplateSingleLine) {
               String line = loreTemplateSingleLine.getLine(context);
               if (line != null) {
                  lore.add(line);
               }
            }
         }
      }

      return lore;
   }
}
