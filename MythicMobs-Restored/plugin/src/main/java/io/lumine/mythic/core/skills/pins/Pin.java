package io.lumine.mythic.core.skills.pins;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.bukkit.utils.config.properties.PropertyHolder;
import java.io.File;
import java.util.List;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public abstract class Pin implements PropertyHolder {
   protected final Pack pack;
   protected final File file;
   protected final String propertyNode;
   protected final List<AbstractLocation> locations = Lists.newArrayList();

   public Pin(Pack pack, File file, String node) {
      this.pack = pack;
      this.file = file;
      this.propertyNode = node;
      this.loadLocations();
   }

   public Pin(Pack pack, File file, String node, AbstractLocation location) {
      this.pack = pack;
      this.file = file;
      this.propertyNode = node;
      this.locations.add(location);
   }

   public abstract void loadLocations();

   public abstract void save();

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Pin other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$pack = this.getPack();
         Object other$pack = other.getPack();
         if (this$pack == null ? other$pack == null : this$pack.equals(other$pack)) {
            Object this$file = this.getFile();
            Object other$file = other.getFile();
            if (this$file == null ? other$file == null : this$file.equals(other$file)) {
               Object this$propertyNode = this.getPropertyNode();
               Object other$propertyNode = other.getPropertyNode();
               if (this$propertyNode == null ? other$propertyNode == null : this$propertyNode.equals(other$propertyNode)) {
                  Object this$locations = this.getLocations();
                  Object other$locations = other.getLocations();
                  return this$locations == null ? other$locations == null : this$locations.equals(other$locations);
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
      return other instanceof Pin;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $pack = this.getPack();
      result = result * 59 + ($pack == null ? 43 : $pack.hashCode());
      Object $file = this.getFile();
      result = result * 59 + ($file == null ? 43 : $file.hashCode());
      Object $propertyNode = this.getPropertyNode();
      result = result * 59 + ($propertyNode == null ? 43 : $propertyNode.hashCode());
      Object $locations = this.getLocations();
      return result * 59 + ($locations == null ? 43 : $locations.hashCode());
   }
}
