package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.cinematic.CameraPath;
import io.lumine.mythic.core.skills.cinematic.CameraPoint;
import io.lumine.mythic.core.skills.cinematic.CinematicManager;
import io.lumine.mythic.core.skills.cinematic.CinematicMode;
import io.lumine.mythic.core.skills.cinematic.Easing;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.entity.Player;

@MythicMechanic(
   author = "Taiyou06",
   name = "cinematicCamera",
   aliases = {"cinecam", "camerapath"},
   description = "Plays a cinematic camera path for the target player."
)
public class CinematicCameraMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "path",
      aliases = {"p", "name"},
      description = "Name of a camera path defined under /cutscenes/."
   )
   protected String pathName;
   @MythicField(
      name = "points",
      aliases = {"keyframes"},
      description = "Inline keyframes: world,x,y,z,yaw,pitch[,hold[,easing]] separated by ;"
   )
   protected String pointsRaw;
   @MythicField(
      name = "duration",
      aliases = {"d"},
      description = "Total duration in ticks (used when inline keyframes have no per-leg hold).",
      defValue = "100"
   )
   protected int duration;
   @MythicField(
      name = "easing",
      aliases = {"e", "curve"},
      description = "Default easing curve for legs.",
      defValue = "EASE_IN_OUT_CUBIC"
   )
   protected Easing easing;
   @MythicField(
      name = "loop",
      description = "Whether the path loops until cancelled.",
      defValue = "false"
   )
   protected boolean loop;
   @MythicField(
      name = "maxDuration",
      aliases = {"max", "maxd"},
      description = "Safety cap in ticks for looping paths.",
      defValue = "6000"
   )
   protected PlaceholderInt maxDuration;
   @MythicField(
      name = "freezePlayer",
      aliases = {"freeze"},
      description = "Lock the player's movement and damage during the cinematic.",
      defValue = "true"
   )
   protected boolean freezePlayer;
   @MythicField(
      name = "mode",
      aliases = {"m"},
      description = "DISPLAY (smooth, packet-only camera entity) or TELEPORT (per-tick player teleport, no interpolation).",
      defValue = "DISPLAY"
   )
   protected CinematicMode mode;
   @MythicField(
      name = "onStart",
      aliases = {"onstartskill", "os"},
      description = "Optional skill to execute when the cinematic begins."
   )
   protected Optional<Skill> onStartSkill = Optional.empty();
   @MythicField(
      name = "onEnd",
      aliases = {"onendskill", "oe"},
      description = "Optional skill to execute when the cinematic ends."
   )
   protected Optional<Skill> onEndSkill = Optional.empty();
   protected String onStartSkillName;
   protected String onEndSkillName;
   protected CameraPath inlinePath;
   protected PlaceholderString inlinePoints;

   public CinematicCameraMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.pathName = mlc.getString(new String[]{"path", "p", "name"}, null);
      this.pointsRaw = mlc.getString(new String[]{"points", "keyframes"}, null);
      this.duration = mlc.getInteger(new String[]{"duration", "d"}, 100);
      this.easing = Easing.fromString(mlc.getString(new String[]{"easing", "e", "curve"}), Easing.EASE_IN_OUT_CUBIC);
      this.loop = mlc.getBoolean(new String[]{"loop"}, false);
      this.maxDuration = mlc.getPlaceholderInteger(new String[]{"maxduration", "maxd", "max"}, 6000);
      this.freezePlayer = mlc.getBoolean(new String[]{"freezeplayer", "freeze"}, true);
      this.mode = CinematicMode.fromString(mlc.getString(new String[]{"mode", "m"}), CinematicMode.DISPLAY);
      this.onStartSkillName = mlc.getString(new String[]{"onstart", "onstartskill", "os"});
      this.onEndSkillName = mlc.getString(new String[]{"onend", "onendskill", "oe"});
      if (this.pointsRaw != null && !this.pointsRaw.isEmpty()) {
         if (this.pointsRaw.indexOf(60) >= 0 && this.pointsRaw.indexOf(62) > this.pointsRaw.indexOf(60)) {
            this.inlinePoints = PlaceholderString.of(this.pointsRaw);
         } else {
            try {
               this.inlinePath = this.parsePoints(this.pointsRaw);
            } catch (Exception e) {
               MythicLogger.errorMechanicConfig(this, mlc, "Failed to parse inline 'points': " + e.getMessage());
            }
         }
      }

      if (this.inlinePath == null && this.inlinePoints == null && (this.pathName == null || this.pathName.isEmpty())) {
         MythicLogger.errorMechanicConfig(this, mlc, "cinematicCamera requires either 'path' or 'points'.");
      }

      this.getManager().queueSecondPass(() -> {
         if (this.onStartSkillName != null) {
            this.onStartSkill = this.getManager().getSkill(file, this, this.onStartSkillName);
         }

         if (this.onEndSkillName != null) {
            this.onEndSkill = this.getManager().getSkill(file, this, this.onEndSkillName);
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target != null && target.getBukkitEntity() != null) {
         if (target.getBukkitEntity() instanceof Player player) {
            CinematicManager mgr = MythicBukkit.inst().getCinematicManager();
            if (mgr == null) {
               MythicLogger.errorMechanic(this, "CinematicManager is not initialized.");
               return SkillResult.ERROR;
            } else {
               CameraPath path = this.inlinePath;
               if (path == null && this.inlinePoints != null) {
                  String resolved = this.inlinePoints.get(PlaceholderContext.of(data, target, null, null, null));

                  try {
                     path = this.parsePoints(resolved);
                  } catch (Exception e) {
                     MythicLogger.errorMechanic(this, "Failed to parse inline 'points' (" + resolved + "): " + e.getMessage());
                     return SkillResult.INVALID_CONFIG;
                  }
               }

               if (path == null && this.pathName != null) {
                  path = mgr.getRegistry().get(this.pathName).orElse(null);
                  if (path == null) {
                     MythicLogger.errorMechanic(this, "Camera path '" + this.pathName + "' not found.");
                     return SkillResult.INVALID_CONFIG;
                  }
               }

               if (path == null) {
                  return SkillResult.INVALID_CONFIG;
               } else {
                  mgr.start(player, this.pathName, path, this.maxDuration.get(data), this.freezePlayer, this.mode, this.onStartSkill, this.onEndSkill, data);
                  return SkillResult.SUCCESS;
               }
            }
         } else {
            return SkillResult.INVALID_TARGET;
         }
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }

   private CameraPath parsePoints(String raw) {
      List<CameraPoint> parsed = new ArrayList<>();

      for (String part : raw.split(";")) {
         String trimmed = part.trim();
         if (!trimmed.isEmpty()) {
            parsed.add(CameraPoint.fromString(trimmed));
         }
      }

      if (parsed.isEmpty()) {
         throw new IllegalArgumentException("no valid keyframes");
      } else {
         return new CameraPath(parsed, this.duration, this.loop, this.easing);
      }
   }
}
