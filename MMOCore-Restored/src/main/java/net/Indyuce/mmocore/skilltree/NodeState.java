package net.Indyuce.mmocore.skilltree;

public enum NodeState {
   UNLOCKED,
   UNLOCKABLE,
   LOCKED,
   MAXED_OUT,
   FULLY_LOCKED;

   public boolean isUnlocked() {
      return this == UNLOCKED || this == MAXED_OUT;
   }
}
