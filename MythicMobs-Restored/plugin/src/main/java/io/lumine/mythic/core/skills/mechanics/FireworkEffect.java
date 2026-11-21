package io.lumine.mythic.core.skills.mechanics;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractFirework;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicFields;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.awt.Color;
import java.io.File;
import java.util.Collection;
import org.bukkit.DyeColor;

@MythicMechanic(
   author = "Ashijin",
   name = "firework",
   aliases = {"fireworks", "effect:firework", "effect:fireworks", "e:firework"},
   description = "Shoots a firework"
)
public class FireworkEffect extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicFields({@MythicField(
         name = "type",
         aliases = {"t"},
         description = "The type of fireball to use.\nAvailable Types:\n    - BALL\n    - BALL_LARGE\n    - BURST\n    - CREEPER\n    - STAR\n",
         defValue = "BALL"
      ), @MythicField(
         name = "power",
         aliases = {"p", "duration", "d"},
         description = "The duration of the firework effect",
         defValue = "2"
      ), @MythicField(
         name = "flicker",
         aliases = {"f"},
         description = "Sets whether the firework should flicker",
         defValue = "false"
      ), @MythicField(
         name = "trail",
         aliases = {"tr"},
         description = "Sets whether the firework should have a trail",
         defValue = "false"
      ), @MythicField(
         name = "colors",
         aliases = {"color", "c"},
         description = "The primary colors of the firework",
         defValue = "#FFFFFF"
      ), @MythicField(
         name = "fadedColors",
         aliases = {"fcolors", "fc"},
         description = "The faded colors of the firework",
         defValue = "#FFFFFF"
      )})
   protected AbstractFirework firework;

   public FireworkEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      String type = mlc.getString(new String[]{"type", "t"}, "BALL");
      int power = mlc.getInteger(new String[]{"power", "p", "duration", "d"}, 2);
      boolean flicker = mlc.getBoolean(new String[]{"flicker", "f"}, false);
      boolean trail = mlc.getBoolean(new String[]{"trail", "tr"}, false);
      String strColors = mlc.getString(new String[]{"colors", "color", "c"}, "#FFFFFF");
      String strFadeColors = mlc.getString(new String[]{"fadecolors", "fcolors", "fc"}, "#FFFFFF");
      Collection<Color> colors = Lists.newArrayList();
      Collection<Color> fadeColors = Lists.newArrayList();

      for (String colorCode : strColors.split(",")) {
         if (colorCode.startsWith("#")) {
            Color color = Color.decode(colorCode);
            colors.add(color);
         } else {
            DyeColor dColor = DyeColor.valueOf(colorCode.toUpperCase());
            org.bukkit.Color color = dColor.getFireworkColor();
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();
            colors.add(new Color(r, g, b));
         }
      }

      for (String colorCodex : strFadeColors.split(",")) {
         if (colorCodex.startsWith("#")) {
            Color color = Color.decode(colorCodex);
            fadeColors.add(color);
         } else {
            DyeColor dColor = DyeColor.valueOf(colorCodex.toUpperCase());
            org.bukkit.Color color = dColor.getFireworkColor();
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();
            fadeColors.add(new Color(r, g, b));
         }
      }

      this.firework = this.getPlugin().getBootstrap().createFirework(type, power, colors, fadeColors, flicker, trail);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      this.firework.launch(data, target);
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      this.firework.launch(data, target.getLocation());
      return SkillResult.SUCCESS;
   }
}
