package com.nexomc.nexo.mechanics.furniture.states;

import com.nexomc.nexo.utils.NexoYamlKt;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.time.Duration;
import kotlin.time.DurationUnit;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \n2\u00020\u0001:\u0001\nB\u001f\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0005\u0010\u0006B\u0011\b\u0016\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\u0005\u0010\t¨\u0006\u000b"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/states/EmptyState;", "Lcom/nexomc/nexo/mechanics/furniture/states/FurnitureState;", "resetAfter", "Lkotlin/time/Duration;", "nextAfter", "<init>", "(Lkotlin/time/Duration;Lkotlin/time/Duration;Lkotlin/jvm/internal/DefaultConstructorMarker;)V", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "Companion", "com.nexomc:core"}
)
public final class EmptyState extends FurnitureState {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   public static final String ID = "";

   public EmptyState(Duration resetAfter, Duration nextAfter) {
      super("", FurnitureStateType.DEFAULT, CollectionsKt.emptyList(), resetAfter, nextAfter);
   }

   public EmptyState(@NotNull ConfigurationSection section) {
      this(NexoYamlKt.durationOrNull(section, "reset_after", (DurationUnit)null), NexoYamlKt.durationOrNull(section, "next_after", (DurationUnit)null));
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0006"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/states/EmptyState$Companion;", "", "<init>", "()V", "ID", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }
   }
}
