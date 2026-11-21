package io.lumine.mythic.api.volatilecode.virtual;

import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.utils.cache.DataTracker;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

@Getter
public class PacketArmorStand<T extends PacketEntityRenderer & PacketArmorStand.PacketArmorStandEntityRenderer> extends PacketEntity {
   private DataTracker<ItemStack> headItem = DataTracker.of(null, false);
   private DataTracker<EulerAngle> rotation = DataTracker.of(null);
   private boolean small = false;
   private boolean upsideDown = false;

   public static PacketArmorStand.PacketArmorStandBuilder create() {
      return new PacketArmorStand.PacketArmorStandBuilder();
   }

   public PacketArmorStand(AbstractLocation location) {
      this(MythicProvider.get(), location);
   }

   public PacketArmorStand(AbstractLocation location, PacketArmorStand.PacketArmorStandBuilder builder) {
      this(MythicProvider.get(), location);
      if (builder.headItem != null) {
         this.headItem.set(builder.headItem);
      }

      if (builder.small != null) {
         this.small = this.small;
      }

      if (builder.upsideDown != null) {
         this.upsideDown = this.upsideDown;
      }
   }

   public PacketArmorStand(MythicPlugin plugin, AbstractLocation location) {
      super(location);
      this.setRenderer((T)plugin.getVolatileCodeHandler().getWorldHandler().createVirtualArmorStandRenderer(this));
   }

   public void setHeadPose(EulerAngle rot) {
      if (this.rotation.set(rot)) {
         this.setDirty();
      }
   }

   public void setItem(ItemStack item) {
      if (this.headItem.set(item)) {
         this.setDirty();
      }
   }

   public static class PacketArmorStandBuilder {
      private ItemStack headItem;
      private Boolean small;
      private Boolean upsideDown;

      public PacketArmorStand build(AbstractLocation location) {
         return new PacketArmorStand(location, this);
      }

      @Generated
      public ItemStack headItem() {
         return this.headItem;
      }

      @Generated
      public Boolean small() {
         return this.small;
      }

      @Generated
      public Boolean upsideDown() {
         return this.upsideDown;
      }

      @Generated
      public PacketArmorStand.PacketArmorStandBuilder headItem(ItemStack headItem) {
         this.headItem = headItem;
         return this;
      }

      @Generated
      public PacketArmorStand.PacketArmorStandBuilder small(Boolean small) {
         this.small = small;
         return this;
      }

      @Generated
      public PacketArmorStand.PacketArmorStandBuilder upsideDown(Boolean upsideDown) {
         this.upsideDown = upsideDown;
         return this;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof PacketArmorStand.PacketArmorStandBuilder other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$small = this.small();
            Object other$small = other.small();
            if (this$small == null ? other$small == null : this$small.equals(other$small)) {
               Object this$upsideDown = this.upsideDown();
               Object other$upsideDown = other.upsideDown();
               if (this$upsideDown == null ? other$upsideDown == null : this$upsideDown.equals(other$upsideDown)) {
                  Object this$headItem = this.headItem();
                  Object other$headItem = other.headItem();
                  return this$headItem == null ? other$headItem == null : this$headItem.equals(other$headItem);
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
         return other instanceof PacketArmorStand.PacketArmorStandBuilder;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         Object $small = this.small();
         result = result * 59 + ($small == null ? 43 : $small.hashCode());
         Object $upsideDown = this.upsideDown();
         result = result * 59 + ($upsideDown == null ? 43 : $upsideDown.hashCode());
         Object $headItem = this.headItem();
         return result * 59 + ($headItem == null ? 43 : $headItem.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "PacketArmorStand.PacketArmorStandBuilder(headItem=" + this.headItem() + ", small=" + this.small() + ", upsideDown=" + this.upsideDown() + ")";
      }
   }

   public interface PacketArmorStandEntityRenderer extends IPacketEntityRenderer {
   }
}
