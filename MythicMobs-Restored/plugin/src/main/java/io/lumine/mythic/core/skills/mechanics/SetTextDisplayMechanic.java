package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.projectiles.bullet.ITextBulletTracker;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.TextDisplay;

@MythicMechanic(
   name = "setTextDisplay",
   aliases = {"setText"},
   description = "Sets text on a Text Display entity or TEXT projectile"
)
public class SetTextDisplayMechanic extends SkillMechanic implements ITargetedEntitySkill, INoTargetSkill {
   @MythicField(
      name = "text",
      aliases = {"t"},
      description = "The text to use. If not set, the mechanic will remove the text from the entity instead.",
      defValue = ""
   )
   private final PlaceholderString text;

   public SetTextDisplayMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.text = mlc.getPlaceholderString(new String[]{"text", "t"}, null);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.getBukkitEntity() instanceof TextDisplay textDisplay) {
         PlaceholderContext placeholderContext = PlaceholderContext.builder().meta(data).entity(target).build();
         String raw = this.text == null ? null : this.text.get(placeholderContext);
         Runnable applyRunnable;
         if (ServerVersion.isPaper()) {
            Component component = MiniMessage.miniMessage().deserializeOrNull(raw);
            applyRunnable = () -> textDisplay.text(component);
         } else {
            applyRunnable = () -> textDisplay.setText(raw);
         }

         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(textDisplay)) {
            Schedulers.of(textDisplay).run(applyRunnable);
         } else {
            applyRunnable.run();
         }

         return SkillResult.SUCCESS;
      } else {
         return this.cast(data);
      }
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      ITextBulletTracker bullet = ITextBulletTracker.of(data);
      if (bullet == null) {
         return SkillResult.INVALID_TARGET;
      } else {
         bullet.setBulletText(this.text == null ? null : this.text.get(PlaceholderContext.builder().meta(data).build()));
         return SkillResult.SUCCESS;
      }
   }
}
