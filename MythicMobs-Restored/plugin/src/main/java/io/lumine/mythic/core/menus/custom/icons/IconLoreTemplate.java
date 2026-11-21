package io.lumine.mythic.core.menus.custom.icons;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.core.menus.custom.CustomMenuContext;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;

@Getter
public class IconLoreTemplate {
   private final IconLoreParser parser;
   private List<IconLoreSection> sections = Lists.newArrayList();

   public IconLoreTemplate(IconLoreParser parser, MythicConfig config) {
      this.parser = parser;
      List<String> lines = config.getStringList("Lines");
      if (lines != null && !lines.isEmpty()) {
         this.sections.add(new IconLoreSection(this, lines));
      }
   }

   public IconLoreTemplate(IconLoreParser parser, List<?> list) {
      this.parser = parser;
      if (list != null && !list.isEmpty()) {
         if (list.get(0) instanceof String) {
            this.sections.add(new IconLoreSection(this, (List<String>)list));
         } else if (list.get(0) instanceof Map) {
            for (Object in : list) {
               Map<?, ?> inMap = (Map<?, ?>)in;
               IconLoreSection section = new IconLoreSection(this, inMap);
               if (inMap.containsKey("Filler")) {
                  section.setFiller(true);
               }

               this.sections.add(section);
            }
         }
      }
   }

   public IconBuilder<CustomMenuContext> apply(CustomMenuContext context, IconBuilder<CustomMenuContext> icon) {
      icon.lore(this.parseLore(context));
      return icon;
   }

   public List<String> parseLore(CustomMenuContext context) {
      List<String> lore = Lists.newArrayList();
      Collection<String> lastFiller = null;

      for (IconLoreSection section : this.sections) {
         if (!section.hasConditions() || this.checkConditions(section, context)) {
            Collection<String> loreLine = section.parse(context);
            if (section.isFiller()) {
               lastFiller = loreLine;
            } else if (!loreLine.isEmpty()) {
               if (lastFiller != null) {
                  lore.addAll(lastFiller);
                  lastFiller = null;
               }

               lore.addAll(loreLine);
            }
         }
      }

      return lore;
   }

   private boolean checkConditions(IconLoreSection section, PlaceholderMeta meta) {
      if (meta instanceof DropMetadata dropMetadata) {
         for (SkillCondition condition : section.getConditions()) {
            if (!condition.evaluateDropper(dropMetadata)) {
               return false;
            }
         }
      } else if (meta instanceof SkillMetadata skillMetadata) {
         for (SkillCondition conditionx : section.getConditions()) {
            if (!conditionx.evaluateCaster(skillMetadata)) {
               return false;
            }
         }
      }

      return true;
   }

   protected IconLoreTemplateLine parseLine(String line) {
      if (line != null && !line.isEmpty()) {
         if (this.parser != null) {
            Optional<IconLoreTemplateLine> parsed = this.parser.parseLine(line);
            if (parsed.isPresent()) {
               return parsed.get();
            }
         }

         return new IconStaticLoreLine(line);
      } else {
         return new IconStaticLoreLine("<white>");
      }
   }
}
