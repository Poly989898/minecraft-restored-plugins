package me.TechsCode.UltraPermissions.base.item;

import me.TechsCode.UltraPermissions.base.item.annotations.XChange;
import me.TechsCode.UltraPermissions.base.item.annotations.XInfo;
import me.TechsCode.UltraPermissions.base.item.annotations.XMerge;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class XModuleMetadata {
   private final boolean wasRemoved;
   private final XChange[] changes;
   private final XMerge[] merges;
   private final XInfo info;

   public XModuleMetadata(boolean flag, XChange[] xChanges, XMerge[] xMerges, XInfo xInfo) {
      this.wasRemoved = flag;
      this.changes = xChanges;
      this.merges = xMerges;
      this.info = xInfo;
   }

   public boolean wasRemoved() {
      return this.wasRemoved;
   }

   public XChange[] getChanges() {
      return this.changes;
   }

   public XMerge[] getMerges() {
      return this.merges;
   }

   public XInfo getInfo() {
      return this.info;
   }
}
