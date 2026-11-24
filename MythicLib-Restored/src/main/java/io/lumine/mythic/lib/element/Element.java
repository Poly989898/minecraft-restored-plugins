package io.lumine.mythic.lib.element;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.skill.SimpleSkill;
import io.lumine.mythic.lib.skill.Skill;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import java.util.Collection;
import java.util.Objects;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class Element {
   private final String id;
   private final String name;
   private final String loreIcon;
   private final String color;
   private final Material icon;
   private final SkillHandler<?> criticalStrike;
   private final SkillHandler<?> regularAttack;
   private static final String DEFAULT_LORE_ICON = "❖";
   private static final String DEFAULT_MATERIAL = "DIRT";
   private static final String DEFAULT_COLOR = "&f";

   public Element(ConfigurationSection config) {
      Validate.notNull(config, "Config cannot be null");
      this.id = UtilityMethods.enumName(config.getName());
      this.icon = Material.valueOf(UtilityMethods.enumName(config.getString("icon", "DIRT")));
      this.name = Objects.requireNonNull(config.getString("name"), "Please specify an element name");
      this.loreIcon = config.getString("lore-icon", "❖");
      this.color = config.getString("color", "&f");
      this.regularAttack = MythicLib.plugin
         .getSkills()
         .loadSkillHandler(Objects.requireNonNull(config.get("regular-attack"), "Could not find skill for regular attacks"));
      this.criticalStrike = config.contains("crit-strike") ? MythicLib.plugin.getSkills().loadSkillHandler(config.get("crit-strike")) : null;
   }

   public String getId() {
      return UtilityMethods.enumName(this.id);
   }

   public String getName() {
      return this.name;
   }

   public Material getIcon() {
      return this.icon;
   }

   public String getLoreIcon() {
      return this.loreIcon;
   }

   public String getColor() {
      return this.color;
   }

   @NotNull
   public Skill getSkill(boolean criticalStrike) {
      SkillHandler<?> handler = criticalStrike && this.criticalStrike != null ? this.criticalStrike : this.regularAttack;
      return new SimpleSkill(TriggerType.API, handler);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Element element = (Element)o;
         return this.id.equals(element.id);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id);
   }

   public static Collection<Element> values() {
      return MythicLib.plugin.getElements().getAll();
   }

   @NotNull
   public static Element valueOf(String id) {
      return MythicLib.plugin.getElements().get(id);
   }
}
