package io.lumine.mythic.core.skills.projectiles;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.utils.numbers.RandomDouble;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.Optional;

public class ArmorStandProjectileData {
   private boolean isMarker = false;
   private boolean has_arms = true;
   private boolean has_gravity = true;
   private boolean invisible = false;
   private boolean small = false;
   private String item_head = null;
   private String item_body = null;
   private String item_legs = null;
   private String item_feet = null;
   private String item_hand = null;
   private String item_offhand = null;
   private Optional<RandomDouble> headPoseP = Optional.empty();
   private Optional<RandomDouble> headPoseY = Optional.empty();
   private Optional<RandomDouble> headPoseA = Optional.empty();
   private Optional<RandomDouble> bodyPoseP = Optional.empty();
   private Optional<RandomDouble> bodyPoseY = Optional.empty();
   private Optional<RandomDouble> bodyPoseA = Optional.empty();
   private Optional<RandomDouble> lArmPoseP = Optional.empty();
   private Optional<RandomDouble> lArmPoseY = Optional.empty();
   private Optional<RandomDouble> lArmPoseA = Optional.empty();
   private Optional<RandomDouble> rArmPoseP = Optional.empty();
   private Optional<RandomDouble> rArmPoseY = Optional.empty();
   private Optional<RandomDouble> rArmPoseA = Optional.empty();
   private Optional<RandomDouble> lLegPoseP = Optional.empty();
   private Optional<RandomDouble> lLegPoseY = Optional.empty();
   private Optional<RandomDouble> lLegPoseA = Optional.empty();
   private Optional<RandomDouble> rLegPoseP = Optional.empty();
   private Optional<RandomDouble> rLegPoseY = Optional.empty();
   private Optional<RandomDouble> rLegPoseA = Optional.empty();

   public ArmorStandProjectileData(MythicLineConfig mc) {
      this.has_arms = mc.getBoolean("hasarms", false);
      this.small = mc.getBoolean("small", false);
      this.isMarker = mc.getBoolean("marker", false);
      this.item_head = mc.getString("head", null);
      this.item_body = mc.getString("body", null);
      this.item_legs = mc.getString("legs", null);
      this.item_feet = mc.getString("feet", null);
      this.item_hand = mc.getString("hand", null);
      this.item_offhand = mc.getString("offhand", null);
      String headPose = mc.getString("headpose", null);
      if (headPose != null) {
         try {
            String[] split = headPose.split(",");
            this.headPoseP = Optional.of(new RandomDouble(split[0]));
            this.headPoseY = Optional.of(new RandomDouble(split[1]));
            this.headPoseA = Optional.of(new RandomDouble(split[2]));
         } catch (Exception e) {
            MythicLogger.handleMinorError(e);
         }
      }

      String bodyPose = mc.getString("bodypose", null);
      if (bodyPose != null) {
         try {
            String[] split = bodyPose.split(",");
            this.bodyPoseP = Optional.of(new RandomDouble(split[0]));
            this.bodyPoseY = Optional.of(new RandomDouble(split[1]));
            this.bodyPoseA = Optional.of(new RandomDouble(split[2]));
         } catch (Exception ex) {
            MythicLogger.handleMinorError(ex);
         }
      }

      String lArmPose = mc.getString("leftarmpose", null);
      if (lArmPose != null) {
         try {
            String[] split = lArmPose.split(",");
            this.lArmPoseP = Optional.of(new RandomDouble(split[0]));
            this.lArmPoseY = Optional.of(new RandomDouble(split[1]));
            this.lArmPoseA = Optional.of(new RandomDouble(split[2]));
         } catch (Exception ex2) {
            MythicLogger.handleMinorError(ex2);
         }
      }

      String rArmPose = mc.getString("rightarmpose", null);
      if (rArmPose != null) {
         try {
            String[] split = rArmPose.split(",");
            this.rArmPoseP = Optional.of(new RandomDouble(split[0]));
            this.rArmPoseY = Optional.of(new RandomDouble(split[1]));
            this.rArmPoseA = Optional.of(new RandomDouble(split[2]));
         } catch (Exception ex3) {
            MythicLogger.handleMinorError(ex3);
         }
      }

      String lLegPose = mc.getString("leftlegpose", null);
      if (lLegPose != null) {
         try {
            String[] split = lLegPose.split(",");
            this.lLegPoseP = Optional.of(new RandomDouble(split[0]));
            this.lLegPoseY = Optional.of(new RandomDouble(split[1]));
            this.lLegPoseA = Optional.of(new RandomDouble(split[2]));
         } catch (Exception ex4) {
            MythicLogger.handleMinorError(ex4);
         }
      }

      String rLegPose = mc.getString("rightlegpose", null);
      if (rLegPose != null) {
         try {
            String[] split = rLegPose.split(",");
            this.rLegPoseP = Optional.of(new RandomDouble(split[0]));
            this.rLegPoseY = Optional.of(new RandomDouble(split[1]));
            this.rLegPoseA = Optional.of(new RandomDouble(split[2]));
         } catch (Exception ex5) {
            MythicLogger.handleMinorError(ex5);
         }
      }
   }

   public void spawn(AbstractLocation location) {
   }
}
