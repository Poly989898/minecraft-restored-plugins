package io.lumine.mythic.core.skills.cooldowns;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
final class CooldownState {
   private int maxCharges;
   private int missingCharges;
   private int overflowCharges;
   private CooldownMode mode;
   private final List<Long> rechargeCompleteTimes;

   CooldownState(int maxCharges, CooldownMode mode) {
      this.maxCharges = maxCharges;
      this.mode = mode;
      this.rechargeCompleteTimes = new ArrayList<>();
   }

   int getAvailableNormalCharges() {
      return Math.max(0, this.maxCharges - this.missingCharges);
   }

   int getAvailableCharges() {
      return this.overflowCharges + this.getAvailableNormalCharges();
   }

   void clearRechargeTimes() {
      this.rechargeCompleteTimes.clear();
   }

   boolean isAtBaseCapacity() {
      return this.missingCharges == 0 && this.overflowCharges == 0 && this.rechargeCompleteTimes.isEmpty();
   }
}
