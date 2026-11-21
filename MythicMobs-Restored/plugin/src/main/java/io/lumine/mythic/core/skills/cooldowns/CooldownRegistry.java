package io.lumine.mythic.core.skills.cooldowns;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.LongSupplier;
import lombok.Generated;
import lombok.NonNull;

public final class CooldownRegistry {
   public static final long UNAVAILABLE = Long.MAX_VALUE;
   private static final int DEFAULT_MAX_CHARGES = 1;
   private static final CooldownMode DEFAULT_MODE = CooldownMode.SEQUENTIAL;
   private final Map<UUID, CooldownState> states = new HashMap<>();
   @NonNull
   private final LongSupplier currentTimeMillis;

   public CooldownRegistry() {
      this(System::currentTimeMillis);
   }

   public synchronized boolean test(UUID skillId, long cooldownMillis) {
      return this.test(skillId, 1, DEFAULT_MODE, cooldownMillis);
   }

   public synchronized boolean test(UUID skillId, int maxCharges, long cooldownMillis) {
      return this.test(skillId, maxCharges, DEFAULT_MODE, cooldownMillis);
   }

   public synchronized boolean test(UUID skillId, int maxCharges, CooldownMode mode, long cooldownMillis) {
      return this.consumeCharge(skillId, maxCharges, mode, cooldownMillis);
   }

   public synchronized boolean consumeCharge(UUID skillId, long cooldownMillis) {
      return this.consumeCharge(skillId, 1, DEFAULT_MODE, cooldownMillis);
   }

   public synchronized boolean consumeCharge(UUID skillId, int maxCharges, long cooldownMillis) {
      return this.consumeCharge(skillId, maxCharges, DEFAULT_MODE, cooldownMillis);
   }

   public synchronized boolean consumeCharge(UUID skillId, int maxCharges, CooldownMode mode, long cooldownMillis) {
      validate(skillId, maxCharges, mode);
      long now = this.currentTimeMillis.getAsLong();
      CooldownState state = this.loadState(skillId, maxCharges, mode, true);
      if (state.getOverflowCharges() > 0) {
         state.setOverflowCharges(state.getOverflowCharges() - 1);
         this.pruneIfIdle(skillId, state);
         return true;
      } else if (state.getAvailableNormalCharges() <= 0) {
         this.pruneIfIdle(skillId, state);
         return false;
      } else {
         state.setMissingCharges(state.getMissingCharges() + 1);
         this.scheduleSpend(state, normalizeCooldownMillis(cooldownMillis), now);
         this.expireCompleted(state, now);
         this.pruneIfIdle(skillId, state);
         return true;
      }
   }

   public synchronized boolean hasAvailableCharge(UUID skillId, int maxCharges, CooldownMode mode) {
      return this.getAvailableCharges(skillId, maxCharges, mode) > 0;
   }

   public synchronized boolean hasAvailableCharge(UUID skillId) {
      return this.hasAvailableCharge(skillId, 1, DEFAULT_MODE);
   }

   public synchronized boolean hasAvailableCharge(UUID skillId, int maxCharges) {
      return this.hasAvailableCharge(skillId, maxCharges, DEFAULT_MODE);
   }

   public synchronized boolean isOnCooldown(UUID skillId, int maxCharges, CooldownMode mode) {
      return this.getAvailableCharges(skillId, maxCharges, mode) == 0;
   }

   public synchronized boolean isOnCooldown(UUID skillId) {
      return this.isOnCooldown(skillId, 1, DEFAULT_MODE);
   }

   public synchronized boolean isOnCooldown(UUID skillId, int maxCharges) {
      return this.isOnCooldown(skillId, maxCharges, DEFAULT_MODE);
   }

   public synchronized boolean isRecharging(UUID skillId, int maxCharges, CooldownMode mode) {
      validate(skillId, maxCharges, mode);
      CooldownState state = this.loadState(skillId, maxCharges, mode, false);
      return state != null && !state.getRechargeCompleteTimes().isEmpty();
   }

   public synchronized boolean isRecharging(UUID skillId) {
      return this.isRecharging(skillId, 1, DEFAULT_MODE);
   }

   public synchronized boolean isRecharging(UUID skillId, int maxCharges) {
      return this.isRecharging(skillId, maxCharges, DEFAULT_MODE);
   }

   public synchronized int getAvailableCharges(UUID skillId, int maxCharges, CooldownMode mode) {
      validate(skillId, maxCharges, mode);
      CooldownState state = this.loadState(skillId, maxCharges, mode, false);
      return state == null ? maxCharges : state.getAvailableCharges();
   }

   public synchronized int getAvailableCharges(UUID skillId) {
      return this.getAvailableCharges(skillId, 1, DEFAULT_MODE);
   }

   public synchronized int getAvailableCharges(UUID skillId, int maxCharges) {
      return this.getAvailableCharges(skillId, maxCharges, DEFAULT_MODE);
   }

   public synchronized int getOverflowCharges(UUID skillId, int maxCharges, CooldownMode mode) {
      validate(skillId, maxCharges, mode);
      CooldownState state = this.loadState(skillId, maxCharges, mode, false);
      return state == null ? 0 : state.getOverflowCharges();
   }

   public synchronized int getOverflowCharges(UUID skillId) {
      return this.getOverflowCharges(skillId, 1, DEFAULT_MODE);
   }

   public synchronized int getOverflowCharges(UUID skillId, int maxCharges) {
      return this.getOverflowCharges(skillId, maxCharges, DEFAULT_MODE);
   }

   public synchronized int getMissingCharges(UUID skillId, int maxCharges, CooldownMode mode) {
      validate(skillId, maxCharges, mode);
      CooldownState state = this.loadState(skillId, maxCharges, mode, false);
      return state == null ? 0 : state.getMissingCharges();
   }

   public synchronized int getMissingCharges(UUID skillId) {
      return this.getMissingCharges(skillId, 1, DEFAULT_MODE);
   }

   public synchronized int getMissingCharges(UUID skillId, int maxCharges) {
      return this.getMissingCharges(skillId, maxCharges, DEFAULT_MODE);
   }

   public synchronized long getRemainingCooldownMillis(UUID skillId, int maxCharges, CooldownMode mode) {
      validate(skillId, maxCharges, mode);
      CooldownState state = this.loadState(skillId, maxCharges, mode, false);
      if (state == null) {
         return 0L;
      } else {
         List<Long> rechargeTimes = state.getRechargeCompleteTimes();
         if (!rechargeTimes.isEmpty()) {
            return Math.max(0L, rechargeTimes.getFirst() - this.currentTimeMillis.getAsLong());
         } else {
            return state.getAvailableCharges() > 0 ? 0L : Long.MAX_VALUE;
         }
      }
   }

   public synchronized long getRemainingCooldownMillis(UUID skillId) {
      return this.getRemainingCooldownMillis(skillId, 1, DEFAULT_MODE);
   }

   public synchronized long getRemainingCooldownMillis(UUID skillId, int maxCharges) {
      return this.getRemainingCooldownMillis(skillId, maxCharges, DEFAULT_MODE);
   }

   public synchronized boolean testAndSetCooldown(UUID skillId, long cooldownMillis) {
      return this.testAndSetCooldown(skillId, 1, DEFAULT_MODE, cooldownMillis);
   }

   public synchronized boolean testAndSetCooldown(UUID skillId, int maxCharges, long cooldownMillis) {
      return this.testAndSetCooldown(skillId, maxCharges, DEFAULT_MODE, cooldownMillis);
   }

   public synchronized boolean testAndSetCooldown(UUID skillId, int maxCharges, CooldownMode mode, long cooldownMillis) {
      if (!this.hasAvailableCharge(skillId, maxCharges, mode)) {
         return false;
      } else {
         this.setCooldown(skillId, maxCharges, mode, cooldownMillis);
         return true;
      }
   }

   public synchronized boolean addCharges(UUID skillId, int amount) {
      return this.addCharges(skillId, 1, DEFAULT_MODE, amount, ChargeRestoreOrder.LATEST, false);
   }

   public synchronized boolean addCharges(UUID skillId, int maxCharges, int amount) {
      return this.addCharges(skillId, maxCharges, DEFAULT_MODE, amount, ChargeRestoreOrder.LATEST, false);
   }

   public synchronized boolean addCharges(UUID skillId, int maxCharges, CooldownMode mode, int amount) {
      return this.addCharges(skillId, maxCharges, mode, amount, ChargeRestoreOrder.LATEST, false);
   }

   public synchronized boolean addCharges(UUID skillId, int maxCharges, CooldownMode mode, int amount, ChargeRestoreOrder order, boolean overflow) {
      validate(skillId, maxCharges, mode);
      Objects.requireNonNull(order, "order");
      if (amount <= 0) {
         return false;
      } else {
         CooldownState state = this.loadState(skillId, maxCharges, mode, overflow);
         if (state == null) {
            return false;
         } else {
            int restored = this.restoreMissingCharges(state, amount, order);
            int remaining = amount - restored;
            if (overflow && remaining > 0) {
               state.setOverflowCharges(state.getOverflowCharges() + remaining);
            }

            this.pruneIfIdle(skillId, state);
            return restored > 0 || overflow && remaining > 0;
         }
      }
   }

   public synchronized void setCooldown(UUID skillId, long cooldownMillis) {
      this.setCooldown(skillId, 1, DEFAULT_MODE, cooldownMillis);
   }

   public synchronized void setCooldown(UUID skillId, int maxCharges, long cooldownMillis) {
      this.setCooldown(skillId, maxCharges, DEFAULT_MODE, cooldownMillis);
   }

   public synchronized void setCooldown(UUID skillId, int maxCharges, CooldownMode mode, long cooldownMillis) {
      validate(skillId, maxCharges, mode);
      CooldownState state = this.loadState(skillId, maxCharges, mode, true);
      long now = this.currentTimeMillis.getAsLong();
      state.clearRechargeTimes();
      state.setOverflowCharges(0);
      state.setMissingCharges(maxCharges);
      this.scheduleHardCooldown(state, normalizeCooldownMillis(cooldownMillis), now);
      this.expireCompleted(state, now);
      this.pruneIfIdle(skillId, state);
   }

   public synchronized void clearCooldown(UUID skillId) {
      Objects.requireNonNull(skillId, "skillId");
      this.states.remove(skillId);
   }

   public synchronized void purgeExpired() {
      long now = this.currentTimeMillis.getAsLong();
      Iterator<Entry<UUID, CooldownState>> iterator = this.states.entrySet().iterator();

      while (iterator.hasNext()) {
         Entry<UUID, CooldownState> entry = iterator.next();
         this.expireCompleted(entry.getValue(), now);
         if (entry.getValue().isAtBaseCapacity()) {
            iterator.remove();
         }
      }
   }

   public synchronized boolean isEmpty() {
      this.purgeExpired();
      return this.states.isEmpty();
   }

   private CooldownState loadState(UUID skillId, int maxCharges, CooldownMode mode, boolean create) {
      CooldownState state = this.states.get(skillId);
      if (state == null) {
         if (!create) {
            return null;
         } else {
            state = new CooldownState(maxCharges, mode);
            this.states.put(skillId, state);
            return state;
         }
      } else {
         long now = this.currentTimeMillis.getAsLong();
         if (state.getMaxCharges() != maxCharges || state.getMode() != mode) {
            state.setMaxCharges(maxCharges);
            state.setMode(mode);
            state.setMissingCharges(0);
            state.setOverflowCharges(0);
            state.clearRechargeTimes();
         }

         this.expireCompleted(state, now);
         if (state.isAtBaseCapacity()) {
            this.states.remove(skillId);
            return create ? this.states.computeIfAbsent(skillId, id -> new CooldownState(maxCharges, mode)) : null;
         } else {
            return state;
         }
      }
   }

   private void scheduleSpend(CooldownState state, long cooldownMillis, long now) {
      switch (state.getMode()) {
         case SEQUENTIAL:
            List<Long> rechargeTimes = state.getRechargeCompleteTimes();
            long lastRechargeAt = rechargeTimes.isEmpty() ? now : rechargeTimes.getLast();
            rechargeTimes.add(Math.max(now, lastRechargeAt) + cooldownMillis);
            break;
         case PARALLEL:
            insertSorted(state.getRechargeCompleteTimes(), now + cooldownMillis);
            break;
         case REFILL:
            if (state.getRechargeCompleteTimes().isEmpty()) {
               state.getRechargeCompleteTimes().add(now + cooldownMillis);
            }
            break;
         case REFILL_ON_EMPTY:
            if (state.getAvailableNormalCharges() == 0 && state.getRechargeCompleteTimes().isEmpty()) {
               state.getRechargeCompleteTimes().add(now + cooldownMillis);
            }
         case NONE:
      }
   }

   private void scheduleHardCooldown(CooldownState state, long cooldownMillis, long now) {
      switch (state.getMode()) {
         case SEQUENTIAL:
            long rechargeAt = now;

            for (int i = 0; i < state.getMaxCharges(); i++) {
               rechargeAt = Math.max(now, rechargeAt) + cooldownMillis;
               state.getRechargeCompleteTimes().add(rechargeAt);
            }
            break;
         case PARALLEL:
            long rechargeAtParallel = now + cooldownMillis;

            for (int i = 0; i < state.getMaxCharges(); i++) {
               state.getRechargeCompleteTimes().add(rechargeAtParallel);
            }
            break;
         case REFILL:
            if (state.getMaxCharges() > 0) {
               state.getRechargeCompleteTimes().add(now + cooldownMillis);
            }
            break;
         case REFILL_ON_EMPTY:
            if (state.getMaxCharges() > 0) {
               state.getRechargeCompleteTimes().add(now + cooldownMillis);
            }
         case NONE:
      }
   }

   private void expireCompleted(CooldownState state, long now) {
      List<Long> rechargeTimes = state.getRechargeCompleteTimes();
      if (!rechargeTimes.isEmpty()) {
         switch (state.getMode()) {
            case SEQUENTIAL:
            case PARALLEL:
               int completedCharges = 0;

               while (completedCharges < rechargeTimes.size() && rechargeTimes.get(completedCharges) <= now) {
                  completedCharges++;
               }

               if (completedCharges > 0) {
                  rechargeTimes.subList(0, completedCharges).clear();
                  state.setMissingCharges(Math.max(0, state.getMissingCharges() - completedCharges));
               }
               break;
            case REFILL:
            case REFILL_ON_EMPTY:
               if (rechargeTimes.getFirst() <= now) {
                  rechargeTimes.clear();
                  state.setMissingCharges(0);
               }
               break;
            case NONE:
               rechargeTimes.clear();
         }
      }
   }

   private int restoreMissingCharges(CooldownState state, int amount, ChargeRestoreOrder order) {
      int restored = Math.min(amount, state.getMissingCharges());
      if (restored <= 0) {
         return 0;
      } else {
         state.setMissingCharges(state.getMissingCharges() - restored);
         List<Long> rechargeTimes = state.getRechargeCompleteTimes();
         switch (state.getMode()) {
            case SEQUENTIAL:
            case PARALLEL:
               removeScheduledRecharges(rechargeTimes, restored, order);
               break;
            case REFILL:
            case REFILL_ON_EMPTY:
               if (state.getMissingCharges() == 0) {
                  rechargeTimes.clear();
               }
               break;
            case NONE:
               rechargeTimes.clear();
         }

         return restored;
      }
   }

   private static void removeScheduledRecharges(List<Long> rechargeTimes, int amount, ChargeRestoreOrder order) {
      int removable = Math.min(amount, rechargeTimes.size());
      if (removable > 0) {
         if (order == ChargeRestoreOrder.EARLIEST) {
            rechargeTimes.subList(0, removable).clear();
         } else {
            rechargeTimes.subList(rechargeTimes.size() - removable, rechargeTimes.size()).clear();
         }
      }
   }

   private void pruneIfIdle(UUID skillId, CooldownState state) {
      if (state.isAtBaseCapacity()) {
         this.states.remove(skillId);
      }
   }

   private static long normalizeCooldownMillis(long cooldownMillis) {
      return Math.max(0L, cooldownMillis);
   }

   private static void insertSorted(List<Long> values, long value) {
      int index = Collections.binarySearch(values, value);
      if (index < 0) {
         index = -index - 1;
      }

      values.add(index, value);
   }

   private static void validate(UUID skillId, int maxCharges, CooldownMode mode) {
      Objects.requireNonNull(skillId, "skillId");
      Objects.requireNonNull(mode, "mode");
      if (maxCharges < 1) {
         throw new IllegalArgumentException("maxCharges must be at least 1");
      }
   }

   @Generated
   CooldownRegistry(@NonNull LongSupplier currentTimeMillis) {
      if (currentTimeMillis == null) {
         throw new NullPointerException("currentTimeMillis is marked non-null but is null");
      } else {
         this.currentTimeMillis = currentTimeMillis;
      }
   }
}
