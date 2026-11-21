package io.lumine.mythic.core.menus.custom;

import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.config.properties.types.MenuProp;
import io.lumine.mythic.bukkit.utils.menu.EditableMenuBuilder;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.menu.MenuData;
import io.lumine.mythic.core.config.MythicConfigImpl;
import io.lumine.mythic.core.menus.MenuManager;
import io.lumine.mythic.core.menus.MythicMenu;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.SkillCondition;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.bukkit.entity.Player;

public abstract class CustomMenu extends MythicMenu<CustomMenuContext> {
   private final Pack pack;
   private final String key;
   private final File file;
   private final MythicConfig menuConfig;
   private CustomMenuBindingFunction dataBinding = null;

   public CustomMenu(MythicBukkit plugin, MenuManager manager, Pack pack, File file, String key, MenuProp menu) {
      super(plugin, manager, pack, menu, new MythicConfigImpl(file).getNestedConfig(key));
      this.pack = pack;
      this.key = key;
      this.file = file;
      this.menuConfig = super.menuConfig;
   }

   @Override
   public EditableMenuBuilder<CustomMenuContext> build(EditableMenuBuilder<CustomMenuContext> builder) {
      if (this.menuConfig.isConfigurationSection("Function")) {
         MythicConfig subConfig = this.menuConfig.getNestedConfig("Function");
         String bindDataType = subConfig.getString("Type");
         this.dataBinding = this.getMenuManager().getMenuBindingFunction(bindDataType, subConfig).orElse(null);
      } else if (this.menuConfig.isSet("Function")) {
         this.dataBinding = this.getMenuManager().getMenuBindingFunction(this.menuConfig.getString("Function"), null).orElse(null);
      }

      return this.applyCustomStuff(builder);
   }

   @Override
   protected void applyConfiguredIconContents(IconBuilder<CustomMenuContext> iconBuilder, MythicConfig config) {
      CustomMenuButtonFunction buttonFunction;
      if (config.isConfigurationSection("Function")) {
         MythicConfig subConfig = config.getNestedConfig("Function");
         String bindDataType = subConfig.getString("Type");
         buttonFunction = this.getMenuManager().getMenuButtonFunction(bindDataType, subConfig).orElse(null);
         if (buttonFunction != null) {
            buttonFunction.applyButtonPresets(iconBuilder);
         }
      } else if (config.isSet("Function")) {
         buttonFunction = null;
      } else {
         buttonFunction = null;
      }

      super.applyConfiguredIconContents(iconBuilder, config);
      List<SkillCondition> clickConditions;
      if (!config.contains("ClickConditions") && !config.contains("Conditions")) {
         clickConditions = null;
      } else {
         ArrayList<String> conditionLines = new ArrayList<>();
         if (config.contains("ClickConditions")) {
            conditionLines.addAll(config.getStringList("ClickConditions"));
            if (conditionLines.isEmpty()) {
               String single = config.getString("ClickConditions");
               if (single != null && !single.isBlank()) {
                  conditionLines.add(single);
               }
            }
         } else {
            conditionLines.addAll(config.getStringList("Conditions"));
            if (conditionLines.isEmpty()) {
               String single = config.getString("Conditions");
               if (single != null && !single.isBlank()) {
                  conditionLines.add(single);
               }
            }
         }

         clickConditions = this.getPlugin().getSkillManager().getConditions(conditionLines);
      }

      Predicate<Player> canClick = player -> {
         if (clickConditions != null && !clickConditions.isEmpty()) {
            AbstractPlayer adapted = BukkitAdapter.adapt(player);

            for (SkillCondition condition : clickConditions) {
               if (!condition.evaluateEntity(adapted)) {
                  return false;
               }
            }

            return true;
         } else {
            return true;
         }
      };
      if (config.contains("Skills")) {
         List<String> skills = config.getStringList("Skills");
         Optional<Skill> maybeSkill = this.getPlugin().getSkillManager().getSkill(this.file, skills);
         if (maybeSkill.isPresent()) {
            Skill skill = maybeSkill.get();
            iconBuilder.click((context, player) -> {
               if (canClick.test(player)) {
                  if (buttonFunction != null) {
                     buttonFunction.leftClick(context, player);
                  }

                  SkillMetadata meta = context.getSkillMetadata();
                  meta.getVariables().putInt("click-type", 0);
                  skill.execute(meta);
               }
            }).rightClick((context, player) -> {
               if (canClick.test(player)) {
                  if (buttonFunction != null) {
                     buttonFunction.rightClick(context, player);
                  }

                  SkillMetadata meta = context.getSkillMetadata();
                  meta.getVariables().putInt("click-type", 1);
                  skill.execute(meta);
               }
            });
         }
      } else if (buttonFunction != null) {
         iconBuilder.click((context, player) -> {
            if (canClick.test(player)) {
               buttonFunction.leftClick(context, player);
            }
         }).rightClick((context, player) -> {
            if (canClick.test(player)) {
               buttonFunction.rightClick(context, player);
            }
         });
      }
   }

   protected PlayerData resolvePlaceholderProfile(CustomMenuContext input) {
      return input != null ? input.getProfile() : null;
   }

   public void openMenu(Player player) {
      PlayerData profile = this.getPlugin().getPlayerManager().getProfile(player);
      CustomMenuContext context = new CustomMenuContext(profile);
      if (this.dataBinding != null) {
         List<MenuData<?>> data = this.dataBinding.getButtons(profile);
         this.open(player, context, data);
      } else {
         this.open(player, context);
      }
   }
}
