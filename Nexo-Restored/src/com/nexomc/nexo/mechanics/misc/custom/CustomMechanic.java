package com.nexomc.nexo.mechanics.misc.custom;

import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.mechanics.misc.custom.fields.CustomEvent;
import com.nexomc.nexo.mechanics.misc.custom.listeners.CustomListener;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.actions.ClickAction;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.time.DurationUnit;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 \b2\u00020\u0001:\u0001\bB\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007¨\u0006\t"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/custom/CustomMechanic;", "Lcom/nexomc/nexo/mechanics/Mechanic;", "factory", "Lcom/nexomc/nexo/mechanics/misc/custom/CustomMechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lcom/nexomc/nexo/mechanics/misc/custom/CustomMechanicFactory;Lorg/bukkit/configuration/ConfigurationSection;)V", "Companion", "com.nexomc:core"}
)
public final class CustomMechanic extends Mechanic {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private static final Map<String, CustomListener> LOADED_VARIANTS = (Map)(new LinkedHashMap());

   public CustomMechanic(@NotNull CustomMechanicFactory factory, @NotNull ConfigurationSection section) {
      super(factory, section);
      Iterable iterable = (Iterable)NexoYamlKt.childSections(section).values();

      for(Object element : iterable) {
         ConfigurationSection subSection = (ConfigurationSection)element;
         int value = 0;
         String currentPath = subSection.getCurrentPath();
         if (currentPath != null) {
            String key = currentPath;
            CustomListener customListener = (CustomListener)LOADED_VARIANTS.get(key);
            if (customListener != null) {
               CustomListener p0 = customListener;
               int value2 = 0;
               p0.unregister();
            }

            ClickAction clickAction2 = ClickAction.Companion.from(subSection);
            if (clickAction2 != null) {
               ClickAction clickAction = clickAction2;
               CustomListener p0 = (new CustomEvent(NexoYamlKt.stringOrEmpty(subSection, "event"), subSection.getBoolean("one_usage", false), subSection.getBoolean("cancel_event", false))).getListener_Kx4hsE0(this.getItemID(), NexoYamlKt.durationOrNull(subSection, "cooldown", (DurationUnit)null), clickAction);
               int value3 = 0;
               p0.register();
               LOADED_VARIANTS.put(key, p0);
            }
         }
      }

   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u001a\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/mechanics/misc/custom/CustomMechanic$Companion;", "", "<init>", "()V", "LOADED_VARIANTS", "", "", "Lcom/nexomc/nexo/mechanics/misc/custom/listeners/CustomListener;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }
   }
}
