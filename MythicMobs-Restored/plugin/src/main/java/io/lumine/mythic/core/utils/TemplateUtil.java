package io.lumine.mythic.core.utils;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.ItemExecutor;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.MobExecutor;
import io.lumine.mythic.core.mobs.MobType;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TemplateUtil {
   private static final ItemExecutor ITEM_EXECUTOR = MythicBukkit.inst().getItemManager();
   private static final MobExecutor MOB_EXECUTOR = MythicBukkit.inst().getMobManager();

   public static void handleTemplates(List<? extends TemplateUtil.Template> templates, Map<String, ?> registry) {
      Map<String, TemplateUtil.NodeState> stateMap = new HashMap<>();
      Map<String, TemplateUtil.Template> templateMap = templates.stream()
         .collect(Collectors.toMap(TemplateUtil.Template::id, template -> (TemplateUtil.Template)template, (existing, replacement) -> existing));
      templates.forEach(template -> processTemplate(template, templateMap, stateMap, registry));
   }

   private static void processTemplate(
      TemplateUtil.Template template, Map<String, TemplateUtil.Template> templates, Map<String, TemplateUtil.NodeState> state, Map<String, ?> registry
   ) {
      TemplateUtil.NodeState mergeState = state.getOrDefault(template.id(), TemplateUtil.NodeState.IDLE);
      if (mergeState == TemplateUtil.NodeState.BUSY) {
         MythicLogger.error("Cyclic template dependency found between {0} and {1}", template.id(), template.parentId());
      } else if (mergeState != TemplateUtil.NodeState.FINISHED) {
         state.put(template.id(), TemplateUtil.NodeState.BUSY);
         if (template.parentId() != null) {
            String[] parentIds = template.parentId().split(",");
            List<String> parentIdList = Arrays.asList(parentIds);
            Collections.reverse(parentIdList);

            for (String parentID : parentIdList) {
               parentID = parentID.trim();
               TemplateUtil.Template parentTemplate = templates.get(parentID);
               if (parentTemplate == null && registry.get(parentID) == null) {
                  MythicLogger.error("Invalid template used as parent in {0}: {1}", template.id(), parentID);
                  return;
               }

               if (parentTemplate != null) {
                  processTemplate(parentTemplate, templates, state, registry);
               }

               MythicConfig parentConfig = null;
               if (parentTemplate != null) {
                  parentConfig = parentTemplate.config();
               } else {
                  Object registryObj = registry.get(parentID);
                  if (registryObj instanceof MythicMob mob) {
                     parentConfig = mob.getConfig();
                  } else if (registryObj instanceof MythicItem item) {
                     parentConfig = item.getConfig();
                  } else if (registryObj instanceof TemplateUtil.TemplateMob tmpl) {
                     parentConfig = tmpl.config();
                  } else if (registryObj instanceof TemplateUtil.TemplateItem tmpl) {
                     parentConfig = tmpl.config();
                  }
               }

               if (parentConfig != null) {
                  List<String> excludedFields = template.config().getStringList("Exclude");
                  excludedFields.add("Hidden");
                  template.config().merge(parentConfig, excludedFields);
                  List<String> templateParents = template.config().getStringList("TemplateParents", new ArrayList<>());
                  templateParents.add(parentID);
                  template.config().set("TemplateParents", templateParents);
               }
            }
         }

         registerTemplate(template, registry);
         state.put(template.id(), TemplateUtil.NodeState.FINISHED);
      }
   }

   private static void registerTemplate(TemplateUtil.Template template, Map<String, ?> registry) {
      if (template instanceof TemplateUtil.TemplateMob mob) {
         MythicMob mm = new MobType(MOB_EXECUTOR, mob.pack(), mob.file(), mob.id(), mob.config());
         ((Map<String, MythicMob>)registry).put(mob.id(), mm);
      } else if (template instanceof TemplateUtil.TemplateItem item) {
         ITEM_EXECUTOR.registerItem(item.id(), new MythicItem(item.pack(), item.file(), item.id(), item.config()));
         item.pack().addItem();
      }
   }

   public static enum NodeState {
      IDLE,
      BUSY,
      FINISHED;
   }

   public interface Template {
      String id();

      String parentId();

      MythicConfig config();

      Pack pack();

      File file();
   }

   public record TemplateItem(File file, Pack pack, String id, MythicConfig config, String parentId) implements TemplateUtil.Template {
   }

   public record TemplateMob(File file, Pack pack, String id, MythicConfig config, String parentId) implements TemplateUtil.Template {
   }
}
