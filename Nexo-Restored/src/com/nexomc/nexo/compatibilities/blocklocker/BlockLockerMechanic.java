package com.nexomc.nexo.compatibilities.blocklocker;

import com.nexomc.nexo.utils.EnumUtils;
import com.nexomc.nexo.utils.logs.Logs;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import nl.rutgerkok.blocklocker.ProtectionType;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u000e"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/blocklocker/BlockLockerMechanic;", "", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "canProtect", "", "getCanProtect", "()Z", "protectionType", "Lnl/rutgerkok/blocklocker/ProtectionType;", "getProtectionType", "()Lnl/rutgerkok/blocklocker/ProtectionType;", "com.nexomc:core"}
)
public final class BlockLockerMechanic {
   private final boolean canProtect;
   @NotNull
   private final ProtectionType protectionType;

   public BlockLockerMechanic(@NotNull ConfigurationSection section) {
      Intrinsics.checkNotNullParameter(section, "section");
      this.canProtect = section.getBoolean("can_protect", true);
      ProtectionType type = null;
      String name = section.getString("protection_type");
      if (name != null) {
         for (ProtectionType candidate : ProtectionType.values()) {
            if (StringsKt.equals(candidate.name(), name, true)) {
               type = candidate;
               break;
            }
         }

         if (type == null) {
            ConfigurationSection parent = section.getParent();
            Logs.logError("Invalid protection type " + name + " for BlockLocker mechanic in item " + (parent != null ? parent.getParent() : null) + ", defaulting to CONTAINER", false);
            type = ProtectionType.CONTAINER;
         }
      }

      this.protectionType = type != null ? type : ProtectionType.CONTAINER;
   }

   public final boolean getCanProtect() {
      return this.canProtect;
   }

   @NotNull
   public final ProtectionType getProtectionType() {
      return this.protectionType;
   }
}
