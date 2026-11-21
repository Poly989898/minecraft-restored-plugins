package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AbstractItemLodestoneTrackerComponent implements AbstractItemComponent {
   public static final String SECTION_KEY = "LodestoneTracker";
   public static final String SECTION_OPTIONS_KEY = "Options.LodestoneTracker";
   public static final String KEY_LOCATION = "Location";
   public static final String KEY_TRACKED = "Tracked";
   private String world;
   private Double x;
   private Double y;
   private Double z;
   private Boolean tracked;

   public AbstractItemLodestoneTrackerComponent(MythicItem mythicItem, MythicConfig config) {
      if (config.isSet("World") || config.isSet("X") || config.isSet("Y") || config.isSet("Z")) {
         MythicLogger.errorItemConfig(mythicItem, config, "LodestoneTracker legacy World/X/Y/Z keys are not supported. Use Location: x,y,z,world");
      }

      String locationInput = config.getString("Location", null);
      if (locationInput != null) {
         if (locationInput.isBlank()) {
            MythicLogger.errorItemConfig(mythicItem, config, "LodestoneTracker.Location must use format x,y,z,world");
         } else {
            String[] split = locationInput.split(",", 4);
            if (split.length != 4) {
               MythicLogger.errorItemConfig(mythicItem, config, "LodestoneTracker.Location must use format x,y,z,world");
            } else {
               try {
                  double parsedX = Double.parseDouble(split[0].trim());
                  double parsedY = Double.parseDouble(split[1].trim());
                  double parsedZ = Double.parseDouble(split[2].trim());
                  String parsedWorld = split[3].trim();
                  if (parsedWorld.isEmpty()) {
                     MythicLogger.errorItemConfig(mythicItem, config, "LodestoneTracker.Location world cannot be empty");
                  } else {
                     this.x = parsedX;
                     this.y = parsedY;
                     this.z = parsedZ;
                     this.world = parsedWorld;
                  }
               } catch (NumberFormatException e) {
                  MythicLogger.errorItemConfig(mythicItem, config, "LodestoneTracker.Location has invalid coordinates: " + locationInput);
               }
            }
         }
      }

      if (config.isSet("Tracked")) {
         this.tracked = config.getBoolean("Tracked");
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyLodestoneTrackerComponent(dropMetadata, this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemLodestoneTrackerComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$x = this.getX();
         Object other$x = other.getX();
         if (this$x == null ? other$x == null : this$x.equals(other$x)) {
            Object this$y = this.getY();
            Object other$y = other.getY();
            if (this$y == null ? other$y == null : this$y.equals(other$y)) {
               Object this$z = this.getZ();
               Object other$z = other.getZ();
               if (this$z == null ? other$z == null : this$z.equals(other$z)) {
                  Object this$tracked = this.getTracked();
                  Object other$tracked = other.getTracked();
                  if (this$tracked == null ? other$tracked == null : this$tracked.equals(other$tracked)) {
                     Object this$world = this.getWorld();
                     Object other$world = other.getWorld();
                     return this$world == null ? other$world == null : this$world.equals(other$world);
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof AbstractItemLodestoneTrackerComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $x = this.getX();
      result = result * 59 + ($x == null ? 43 : $x.hashCode());
      Object $y = this.getY();
      result = result * 59 + ($y == null ? 43 : $y.hashCode());
      Object $z = this.getZ();
      result = result * 59 + ($z == null ? 43 : $z.hashCode());
      Object $tracked = this.getTracked();
      result = result * 59 + ($tracked == null ? 43 : $tracked.hashCode());
      Object $world = this.getWorld();
      return result * 59 + ($world == null ? 43 : $world.hashCode());
   }
}
