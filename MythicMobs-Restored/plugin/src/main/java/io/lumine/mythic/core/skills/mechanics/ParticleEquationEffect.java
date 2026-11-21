package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.lumine.mythic.core.utils.math.MathExpressions;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:particleEquation",
   aliases = {"e:peq", "peq", "particleEquation"}
)
public class ParticleEquationEffect extends ParticleEffect implements ITargetedEntitySkill, ITargetedLocationSkill {
   protected final String equationStr;
   protected final PlaceholderDouble precision;
   protected final PlaceholderDouble tolerance;
   protected final double extentX;
   protected final double extentY;
   protected final double extentZ;
   protected final PlaceholderInt maxSamples;
   protected final Map<String, Double> constVariables;
   private volatile Expression equationTemplate;
   protected final boolean relative;
   protected final PlaceholderDouble yawDeg;
   protected final PlaceholderDouble pitchDeg;

   public ParticleEquationEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.ASYNC_ONLY;
      this.equationStr = mlc.getString("equation", "(x*x + y*y + z*z) - r*r");
      this.precision = mlc.getPlaceholderDouble("precision", 0.25);
      this.tolerance = mlc.getPlaceholderDouble("tolerance", 0.1);
      double radius = Math.max(mlc.getDouble("radius", 4.0), 0.01);
      double ex = mlc.getDouble("extentx", -1.0);
      double ey = mlc.getDouble("extenty", -1.0);
      double ez = mlc.getDouble("extentz", -1.0);
      this.extentX = ex > 0.0 ? ex : radius;
      this.extentY = ey > 0.0 ? ey : radius;
      this.extentZ = ez > 0.0 ? ez : radius;
      this.maxSamples = mlc.getPlaceholderInteger("maxsamples", 250000);
      this.constVariables = this.parseVariables(mlc.getString("variables", ""));
      this.relative = mlc.getBoolean("relative", false);
      this.yawDeg = mlc.getPlaceholderDouble("yaw", 0.0);
      this.pitchDeg = mlc.getPlaceholderDouble("pitch", 0.0);
   }

   private Map<String, Double> parseVariables(String variablesStr) {
      if (variablesStr != null && !variablesStr.trim().isEmpty()) {
         Map<String, Double> out = new LinkedHashMap<>();

         for (String part : variablesStr.split(";")) {
            String p = part.trim();
            if (!p.isEmpty()) {
               String[] kv = p.split("=");
               if (kv.length != 2) {
                  Log.warn("[peq] Ignoring variable '{}': expected key=value", p);
               } else {
                  String key = kv[0].trim();
                  String val = kv[1].trim();
                  if (key.isEmpty()) {
                     Log.warn("[peq] Ignoring variable with empty name in '{}'", part);
                  } else {
                     try {
                        out.put(key, Double.parseDouble(val));
                     } catch (NumberFormatException e) {
                        Log.warn("[peq] Variable '{}' value '{}' is not a number; skipping.", key, val);
                     }
                  }
               }
            }
         }

         return out;
      } else {
         return Collections.emptyMap();
      }
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      Collection<AbstractPlayer> audience = SkillAudience.getEffective(this.audience, data, null);
      this.playParticleEquationEffect(data, target, audience);
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      Collection<AbstractPlayer> audience = SkillAudience.getEffective(this.audience, data, target);
      this.playParticleEquationEffect(data, target.getLocation(), audience);
      return SkillResult.SUCCESS;
   }

   protected void playParticleEquationEffect(SkillMetadata data, AbstractLocation center, Collection<AbstractPlayer> audience) {
      Expression template = this.equationTemplate;
      if (template == null) {
         try {
            Set<String> varNames = new LinkedHashSet<>();
            varNames.add("x");
            varNames.add("y");
            varNames.add("z");
            varNames.addAll(this.constVariables.keySet());
            template = new ExpressionBuilder(this.equationStr).variables(varNames).build();
            this.equationTemplate = template;
         } catch (Throwable ex) {
            Log.error("[peq] Failed to build expression '{}': {}", this.equationStr, ex.getMessage());
            return;
         }
      }

      Expression expression = MathExpressions.instantiate(template);

      for (Entry<String, Double> e : this.constVariables.entrySet()) {
         expression.setVariable(e.getKey(), e.getValue());
      }

      double effYawDeg = this.yawDeg.get(data);
      double effPitchDeg = this.pitchDeg.get(data);
      if (this.relative) {
         try {
            AbstractLocation loc = data.getCaster().getEntity().getLocation();
            effYawDeg += loc.getYaw();
            effPitchDeg += loc.getPitch();
         } catch (Throwable ex2) {
         }
      }

      double yawRad = Math.toRadians(effYawDeg);
      double pitchRad = Math.toRadians(effPitchDeg);
      double minX = -this.extentX;
      double maxX = this.extentX;
      double minY = -this.extentY;
      double maxY = this.extentY;
      double minZ = -this.extentZ;
      double maxZ = this.extentZ;
      double precision = Math.max(this.precision.get(data), 0.01);
      int maxSamples = Math.max(this.maxSamples.get(data), 1);
      int nx0 = Math.max(1, (int)Math.floor((maxX - minX) / precision) + 1);
      int ny0 = Math.max(1, (int)Math.floor((maxY - minY) / precision) + 1);
      int nz0 = Math.max(1, (int)Math.floor((maxZ - minZ) / precision) + 1);
      long planned = 1L * nx0 * ny0 * nz0;
      double step = precision;
      if (planned > maxSamples) {
         double scale = Math.cbrt((double)planned / maxSamples);
         step = precision * Math.max(1.0, scale);
      }

      this.renderGrid(data, center, audience, expression, minX, minY, minZ, step, yawRad, pitchRad);
   }

   private void renderGrid(
      SkillMetadata data,
      AbstractLocation center,
      Collection<AbstractPlayer> audience,
      Expression expression,
      double minX,
      double minY,
      double minZ,
      double step,
      double yawRad,
      double pitchRad
   ) {
      double cx = center.getX();
      double cy = center.getY();
      double cz = center.getZ();
      AbstractLocation particleLocation = center.clone();
      int nx = Math.max(1, (int)Math.floor(this.extentX * 2.0 / step) + 1);
      int ny = Math.max(1, (int)Math.floor(this.extentY * 2.0 / step) + 1);
      int nz = Math.max(1, (int)Math.floor(this.extentZ * 2.0 / step) + 1);
      double tolerance = Math.max(this.tolerance.get(data), 0.0);
      double cosY = Math.cos(yawRad);
      double sinY = Math.sin(yawRad);
      double cosP = Math.cos(pitchRad);
      double sinP = Math.sin(pitchRad);

      for (int ix = 0; ix < nx; ix++) {
         double xLocal = minX + ix * step;

         for (int iy = 0; iy < ny; iy++) {
            double yLocal = minY + iy * step;

            for (int iz = 0; iz < nz; iz++) {
               double zLocal = minZ + iz * step;
               double x1 = xLocal * cosY - zLocal * sinY;
               double z1 = xLocal * sinY + zLocal * cosY;
               double y2 = yLocal * cosP - z1 * sinP;
               double z2 = yLocal * sinP + z1 * cosP;
               expression.setVariable("x", x1);
               expression.setVariable("y", y2);
               expression.setVariable("z", z2);

               double result;
               try {
                  result = expression.evaluate();
               } catch (Throwable e) {
                  continue;
               }

               if (Math.abs(result) <= tolerance) {
                  particleLocation.setX(cx + x1);
                  particleLocation.setY(cy + y2);
                  particleLocation.setZ(cz + z2);
                  this.playParticleEffect(data, particleLocation, audience);
               }
            }
         }
      }
   }
}
