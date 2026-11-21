package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IMetaSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.conditions.InvalidCondition;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

@MythicMechanic(
   author = "Seyarada",
   name = "switch",
   description = "Acts as a switch/case"
)
public class SwitchMechanic extends SkillMechanic implements IMetaSkill {
   final Map<String, Skill> caseSkills = new HashMap<>();
   boolean uniqueResult;
   PlaceholderString baseCondition;
   Skill defaultSkill;

   public SwitchMechanic(SkillExecutor manager, File file, String mlcLine, MythicLineConfig mlc) {
      super(manager, file, mlcLine, mlc);
      this.uniqueResult = mlc.getBoolean(new String[]{"uniqueresult", "unique", "first"}, true);
      this.baseCondition = mlc.getPlaceholderString("condition", "");
      List<String[]> cases = parseCases(mlc.getString("cases"));
      this.getManager().queueSecondPass(() -> {
         for (String[] pair : cases) {
            String condition = pair[0];
            String skillString = pair[1];
            Optional<Skill> skill = this.getManager().getSkill(file, this, skillString);
            skill.ifPresent(metaSkill -> {
               if (metaSkill.isInlineSkill()) {
                  metaSkill.addParent(this);
               }

               if (condition.equalsIgnoreCase("default")) {
                  this.defaultSkill = metaSkill;
               } else {
                  this.caseSkills.put(condition, metaSkill);
               }
            });
         }
      });
   }

   @Override
   public SkillResult cast(SkillMetadata meta) {
      String parsedCondition = this.baseCondition.get(meta);
      boolean hasExecuted = false;

      for (Entry<String, Skill> e : this.caseSkills.entrySet()) {
         String conditionCase = PlaceholderString.of(e.getKey()).get(meta);
         Skill skill = e.getValue();
         String caseCondition = parsedCondition.replace("<case>", conditionCase);
         SkillCondition condition = this.getPlugin().getSkillManager().getCondition(caseCondition);
         if (!(condition instanceof InvalidCondition)) {
            SkillMetadataImpl data = meta.deeperClone();
            if (condition instanceof ISkillMetaCondition metaCondition) {
               if (metaCondition.check(data)) {
                  skill.execute(data);
                  hasExecuted = true;
               }
            } else if (condition.evaluateTargets(data)) {
               skill.execute(data);
               hasExecuted = true;
            }

            if (hasExecuted && this.uniqueResult) {
               break;
            }
         }
      }

      if (!hasExecuted && this.defaultSkill != null) {
         this.defaultSkill.execute(meta);
      }

      return SkillResult.SUCCESS;
   }

   private static List<String[]> parseCases(String casesString) {
      if (casesString != null && !casesString.isEmpty()) {
         List<Integer> caseStarts = new ArrayList<>();
         int squareDepth = 0;
         int curlyDepth = 0;
         int parenDepth = 0;
         boolean inQuotes = false;
         char quoteChar = 0;

         for (int i = 0; i < casesString.length(); i++) {
            char c = casesString.charAt(i);
            if (inQuotes) {
               if (c == quoteChar) {
                  inQuotes = false;
               }
            } else if (c != '"' && c != '\'') {
               switch (c) {
                  case '(':
                     parenDepth++;
                     break;
                  case ')':
                     parenDepth = Math.max(0, parenDepth - 1);
                     break;
                  case '[':
                     squareDepth++;
                     break;
                  case ']':
                     squareDepth = Math.max(0, squareDepth - 1);
                     break;
                  case '{':
                     curlyDepth++;
                     break;
                  case '}':
                     curlyDepth = Math.max(0, curlyDepth - 1);
               }

               if (squareDepth == 0 && curlyDepth == 0 && parenDepth == 0 && matchesCaseToken(casesString, i)) {
                  caseStarts.add(i);
                  i += 3;
               }
            } else {
               inQuotes = true;
               quoteChar = c;
            }
         }

         if (caseStarts.isEmpty()) {
            return List.of();
         } else {
            List<String[]> results = new ArrayList<>();

            for (int ix = 0; ix < caseStarts.size(); ix++) {
               int start = caseStarts.get(ix) + 4;
               int end = ix + 1 < caseStarts.size() ? caseStarts.get(ix + 1) : casesString.length();
               String segment = casesString.substring(start, end).trim();
               if (!segment.isEmpty()) {
                  String[] pair = segment.split("=", 2);
                  if (pair.length == 2) {
                     results.add(new String[]{pair[0].trim(), pair[1].trim()});
                  }
               }
            }

            return results;
         }
      } else {
         return List.of();
      }
   }

   private static boolean matchesCaseToken(String text, int index) {
      if (index < 0 || index + 4 > text.length()) {
         return false;
      } else if (!text.regionMatches(true, index, "case", 0, 4)) {
         return false;
      } else {
         char before = index == 0 ? 32 : text.charAt(index - 1);
         char after = index + 4 >= text.length() ? 32 : text.charAt(index + 4);
         return Character.isWhitespace(before) && Character.isWhitespace(after);
      }
   }
}
