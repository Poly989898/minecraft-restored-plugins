package io.lumine.mythic.core.skills.pins;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.bukkit.utils.config.properties.Property;
import io.lumine.mythic.bukkit.utils.config.properties.PropertyHolder;
import io.lumine.mythic.bukkit.utils.config.properties.types.RegionProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.StringListProp;
import io.lumine.mythic.bukkit.utils.serialize.Region;
import io.lumine.mythic.core.config.Scope;
import java.io.File;
import java.util.Collection;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PinRegion implements PropertyHolder {
   private static final RegionProp REGION = Property.Region(Scope.CONFIG, "Region");
   private static final StringListProp TAGS = Property.StringList(Scope.CONFIG, "Tags");
   protected final Pack pack;
   protected final File file;
   protected final String propertyNode;
   protected Region region;
   protected Collection<String> tags;

   public PinRegion(Pack pack, File file, String node) {
      this.pack = pack;
      this.file = file;
      this.propertyNode = node;
      this.region = REGION.fget(file, this);
      this.tags = TAGS.fget(file, this);
   }

   public PinRegion(Pack pack, File file, String node, Region region, Collection<String> tags) {
      this.pack = pack;
      this.file = file;
      this.propertyNode = node;
      this.region = region;
      this.tags = tags;
   }

   public void save() {
      REGION.fset(this.file, this, this.region);
      TAGS.fset(this.file, this, Lists.newArrayList(this.tags));
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof PinRegion other)) {
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
                  Object this$region = this.getRegion();
                  Object other$region = other.getRegion();
                  if (this$region == null ? other$region == null : this$region.equals(other$region)) {
                     Object this$tags = this.getTags();
                     Object other$tags = other.getTags();
                     return this$tags == null ? other$tags == null : this$tags.equals(other$tags);
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
      return other instanceof PinRegion;
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
      Object $region = this.getRegion();
      result = result * 59 + ($region == null ? 43 : $region.hashCode());
      Object $tags = this.getTags();
      return result * 59 + ($tags == null ? 43 : $tags.hashCode());
   }
}
