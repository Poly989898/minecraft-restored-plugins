package com.nexomc.nexo.compatibilities.placeholderapi;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.compatibilities.CompatibilityProvider;
import kotlin.Metadata;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007¢\u0006\u0004\b\u0003\u0010\u0004J\b\u0010\u0007\u001a\u00020\bH\u0016R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\t"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/placeholderapi/PlaceholderAPICompatibility;", "Lcom/nexomc/nexo/compatibilities/CompatibilityProvider;", "Lme/clip/placeholderapi/PlaceholderAPIPlugin;", "<init>", "()V", "expansion", "Lcom/nexomc/nexo/compatibilities/placeholderapi/NexoExpansion;", "disable", "", "com.nexomc:core"}
)
public final class PlaceholderAPICompatibility extends CompatibilityProvider<PlaceholderAPIPlugin> {
   @NotNull
   private final NexoExpansion expansion;

   public PlaceholderAPICompatibility() {
      this.expansion = new NexoExpansion(NexoPlugin.Companion.instance());
      this.expansion.register();
   }

   public void disable() {
      super.disable();
      this.expansion.unregister();
   }
}
