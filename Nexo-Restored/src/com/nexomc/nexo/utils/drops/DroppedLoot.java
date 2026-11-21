package com.nexomc.nexo.utils.drops;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\n\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000b\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÆ\u0001J\u0014\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010HÖ\u0083\u0004J\n\u0010\u0011\u001a\u00020\u0005HÖ\u0081\u0004J\n\u0010\u0012\u001a\u00020\u0013HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\t¨\u0006\u0014"},
   d2 = {"Lcom/nexomc/nexo/utils/drops/DroppedLoot;", "Ljava/lang/Record;", "loot", "Lcom/nexomc/nexo/utils/drops/Loot;", "amount", "", "<init>", "(Lcom/nexomc/nexo/utils/drops/Loot;I)V", "()Lcom/nexomc/nexo/utils/drops/Loot;", "()I", "component1", "component2", "copy", "equals", "", "other", "", "hashCode", "toString", "", "com.nexomc:core"}
)
public record DroppedLoot(Loot loot, int amount) {
   public DroppedLoot {
   }

   @NotNull
   public final Loot loot() {
      return this.loot;
   }

   @NotNull
   public final Loot component1() {
      return this.loot;
   }

   public final int component2() {
      return this.amount;
   }

   @NotNull
   public final DroppedLoot copy(@NotNull Loot loot, int amount) {
      return new DroppedLoot(loot, amount);
   }

   @NotNull
   public String toString() {
      return "DroppedLoot(loot=" + this.loot + ", amount=" + this.amount + ")";
   }

   public int hashCode() {
      int result = this.loot.hashCode();
      result = result * 31 + Integer.hashCode(this.amount);
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof DroppedLoot)) {
         return false;
      } else {
         DroppedLoot droppedLoot = (DroppedLoot)other;
         if (!Intrinsics.areEqual(this.loot, droppedLoot.loot)) {
            return false;
         } else {
            return this.amount == droppedLoot.amount;
         }
      }
   }
}
