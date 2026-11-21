package com.nexomc.nexo.compatibilities.blocklocker;

import com.nexomc.nexo.compatibilities.CompatibilityProvider;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import nl.rutgerkok.blocklocker.BlockLockerAPIv2;
import nl.rutgerkok.blocklocker.ProtectableBlocksSettings;
import nl.rutgerkok.blocklocker.impl.BlockLockerPluginImpl;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007¢\u0006\u0004\b\u0003\u0010\u0004¨\u0006\u0005"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/blocklocker/BlockLockerCompatibility;", "Lcom/nexomc/nexo/compatibilities/CompatibilityProvider;", "Lnl/rutgerkok/blocklocker/impl/BlockLockerPluginImpl;", "<init>", "()V", "com.nexomc:core"}
)
public final class BlockLockerCompatibility extends CompatibilityProvider<BlockLockerPluginImpl> {
   public BlockLockerCompatibility() {
      BlockLockerAPIv2.getPlugin().getChestSettings().getExtraProtectables().removeIf(BlockLockerCompatibility::_init_$lambda$0);
      List var10000 = BlockLockerAPIv2.getPlugin().getChestSettings().getExtraProtectables();
      Intrinsics.checkNotNullExpressionValue(var10000, "getExtraProtectables(...)");
      Collection var1 = (Collection)var10000;
      var1.add(new BlockLockerProtection());
   }

   private static final boolean _init_$lambda$0(ProtectableBlocksSettings it) {
      return it instanceof BlockLockerProtection;
   }
}
