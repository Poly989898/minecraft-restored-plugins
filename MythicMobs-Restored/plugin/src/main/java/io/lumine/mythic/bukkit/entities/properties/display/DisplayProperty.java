package io.lumine.mythic.bukkit.entities.properties.display;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.entities.properties.EntityPropertySet;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class DisplayProperty implements EntityPropertySet {
   private float viewRange;
   private float width;
   private float height;
   private float shadowRadius;
   private float shadowStrength;
   private Billboard billboard;
   private int interpolationDelay;
   private int interpolationDuration;
   private int teleportDuration;
   private Transformation transformation;
   private Brightness brightness = null;
   private Color colorOverride;

   public DisplayProperty(MythicConfig config) {
      MythicConfig displayOption = config.getNestedConfig("DisplayOptions");
      this.viewRange = displayOption.getFloat("ViewRange", 1.0F);
      this.width = displayOption.getFloat("Width", 0.0F);
      this.height = displayOption.getFloat("Height", 0.0F);
      this.shadowRadius = displayOption.getFloat("ShadowRadius", 0.0F);
      this.shadowStrength = displayOption.getFloat("ShadowStrength", 1.0F);
      this.billboard = displayOption.getEnum("Billboard", Billboard.class, Billboard.FIXED);
      this.interpolationDelay = displayOption.getInt("InterpolationDelay", 0);
      this.interpolationDuration = displayOption.getInt("InterpolationDuration", 0);
      this.teleportDuration = displayOption.getInt("TeleportDuration", 0);
      Vector3f translation = new Vector3f();
      String[] trans = displayOption.getString("Translation", "0,0,0").split(",");

      try {
         translation.x = Float.parseFloat(trans[0]);
         translation.y = Float.parseFloat(trans[1]);
         translation.z = Float.parseFloat(trans[2]);
      } catch (Exception e) {
         MythicLogger.errorGenericConfig(config, "Invalid Translation. Format must be 'DisplayOptions.Translation: x,y,z");
      }

      Vector3f scale = new Vector3f();
      String[] s = displayOption.getString("Scale", "1,1,1").split(",");

      try {
         scale.x = Float.parseFloat(s[0]);
         scale.y = Float.parseFloat(s[1]);
         scale.z = Float.parseFloat(s[2]);
      } catch (Exception ex) {
         MythicLogger.errorGenericConfig(config, "Invalid Scale. Format must be 'DisplayOptions.Scale: x,y,z");
      }

      Quaternionf leftRotation = new Quaternionf();
      String[] leftRot = displayOption.getString("LeftRotation", "0,0,0,1").split(",");

      try {
         if (leftRot.length == 3) {
            float x = (float)(Float.parseFloat(leftRot[0]) * Math.PI) / 180.0F;
            float y = (float)(Float.parseFloat(leftRot[1]) * Math.PI) / 180.0F;
            float z = (float)(Float.parseFloat(leftRot[2]) * Math.PI) / 180.0F;
            leftRotation.rotationZYX(z, y, x);
         } else {
            leftRotation.x = Float.parseFloat(leftRot[0]);
            leftRotation.y = Float.parseFloat(leftRot[1]);
            leftRotation.z = Float.parseFloat(leftRot[2]);
            leftRotation.w = Float.parseFloat(leftRot[3]);
         }
      } catch (Exception ex2) {
         MythicLogger.errorGenericConfig(config, "Invalid LeftRotation. Format must be 'DisplayOptions.LeftRotation: x,y,z,w");
      }

      Quaternionf rightRotation = new Quaternionf();
      String[] rightRot = displayOption.getString("RightRotation", "0,0,0,1").split(",");

      try {
         if (rightRot.length == 3) {
            float x = (float)(Float.parseFloat(rightRot[0]) * Math.PI) / 180.0F;
            float y = (float)(Float.parseFloat(rightRot[1]) * Math.PI) / 180.0F;
            float z = (float)(Float.parseFloat(rightRot[2]) * Math.PI) / 180.0F;
            rightRotation.rotationZYX(z, y, x);
         } else {
            rightRotation.x = Float.parseFloat(rightRot[0]);
            rightRotation.y = Float.parseFloat(rightRot[1]);
            rightRotation.z = Float.parseFloat(rightRot[2]);
            rightRotation.w = Float.parseFloat(rightRot[3]);
         }
      } catch (Exception ex3) {
         MythicLogger.errorGenericConfig(config, "Invalid RightRotation. Format must be 'DisplayOptions.RightRotation: x,y,z,w");
      }

      this.transformation = new Transformation(translation, leftRotation, scale, rightRotation);
      int blockLight = displayOption.getInt("BlockLight", 15);
      int skyLight = displayOption.getInt("SkyLight", 15);
      if (blockLight >= 0 || skyLight >= 0) {
         this.brightness = new Brightness(org.joml.Math.clamp(0, 15, blockLight), org.joml.Math.clamp(0, 15, skyLight));
      }

      String colorStr = displayOption.getString("ColorOverride", "0");
      if (colorStr.contains(",")) {
         try {
            String[] splitStr = colorStr.split(",");
            int a = Integer.parseInt(splitStr[0]);
            int r = Integer.parseInt(splitStr[1]);
            int g = Integer.parseInt(splitStr[2]);
            int b = Integer.parseInt(splitStr[3]);
            this.colorOverride = Color.fromARGB(a, r, g, b);
         } catch (Throwable ex4) {
            this.colorOverride = Color.fromARGB(0);
            MythicLogger.errorGenericConfig(config, "Invalid ColorOverride (must be in 'a,r,g,b'): " + colorStr);
         }
      } else {
         try {
            int a = Integer.parseInt(colorStr);
            this.colorOverride = Color.fromARGB(a);
         } catch (NumberFormatException ex5) {
            this.colorOverride = Color.fromARGB(0);
            MythicLogger.errorGenericConfig(config, "Invalid ColorOverride (must be an integer): " + colorStr);
         }
      }
   }

   @Override
   public Entity applyProperties(Entity entity) {
      if (entity instanceof Display display) {
         if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_2)) {
            display.setTeleportDuration(this.teleportDuration);
         }

         display.setViewRange(this.viewRange);
         display.setDisplayWidth(this.width);
         display.setDisplayHeight(this.height);
         display.setShadowRadius(this.shadowRadius);
         display.setShadowStrength(this.shadowStrength);
         display.setBillboard(this.billboard);
         display.setInterpolationDelay(this.interpolationDelay);
         display.setInterpolationDuration(this.interpolationDuration);
         display.setTransformation(this.transformation);
         display.setGlowColorOverride(this.colorOverride);
         if (this.brightness != null) {
            display.setBrightness(this.brightness);
         }

         return entity;
      } else {
         return entity;
      }
   }
}
