package io.lumine.mythic.lib.skill.trigger;

import io.lumine.mythic.lib.UtilityMethods;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

public class TriggerType {
   @NotNull
   public static TriggerType KILL_ENTITY = new TriggerType("KILL_ENTITY");
   @NotNull
   public static TriggerType KILL_PLAYER = new TriggerType("KILL_PLAYER");
   @NotNull
   public static TriggerType ATTACK = new TriggerType("ATTACK", true, true, true);
   @NotNull
   public static TriggerType DAMAGED = new TriggerType("DAMAGED");
   @NotNull
   public static TriggerType DAMAGED_BY_ENTITY = new TriggerType("DAMAGED_BY_ENTITY");
   @NotNull
   public static TriggerType DEATH = new TriggerType("DEATH");
   @NotNull
   public static TriggerType PLACE_BLOCK = new TriggerType("PLACE_BLOCK");
   @NotNull
   public static TriggerType BREAK_BLOCK = new TriggerType("BREAK_BLOCK");
   @NotNull
   public static TriggerType SHOOT_BOW = new TriggerType("SHOOT_BOW", true, true, true);
   @NotNull
   public static TriggerType ARROW_TICK = new TriggerType("ARROW_TICK", true, true, true);
   @NotNull
   public static TriggerType ARROW_HIT = new TriggerType("ARROW_HIT", true, true, true);
   @NotNull
   public static TriggerType ARROW_LAND = new TriggerType("ARROW_LAND", true, true, true);
   @NotNull
   public static TriggerType SHOOT_TRIDENT = new TriggerType("SHOOT_TRIDENT", true, true, true);
   @NotNull
   public static TriggerType TRIDENT_TICK = new TriggerType("TRIDENT_TICK", true, true, true);
   @NotNull
   public static TriggerType TRIDENT_HIT = new TriggerType("TRIDENT_HIT", true, true, true);
   @NotNull
   public static TriggerType TRIDENT_LAND = new TriggerType("TRIDENT_LAND", true, true, true);
   @NotNull
   public static TriggerType RIGHT_CLICK = new TriggerType("RIGHT_CLICK", false, true, true);
   @NotNull
   public static TriggerType LEFT_CLICK = new TriggerType("LEFT_CLICK", false, true, true);
   @NotNull
   public static TriggerType SHIFT_RIGHT_CLICK = new TriggerType("SHIFT_RIGHT_CLICK", false, true, true);
   @NotNull
   public static TriggerType SHIFT_LEFT_CLICK = new TriggerType("SHIFT_LEFT_CLICK", false, true, true);
   @NotNull
   public static TriggerType DROP_ITEM = new TriggerType("DROP_ITEM", false);
   @NotNull
   public static TriggerType SHIFT_DROP_ITEM = new TriggerType("SHIFT_DROP_ITEM", false);
   @NotNull
   public static TriggerType SWAP_ITEMS = new TriggerType("SWAP_ITEMS", false);
   @NotNull
   public static TriggerType SHIFT_SWAP_ITEMS = new TriggerType("SHIFT_SWAP_ITEMS", false);
   @NotNull
   public static TriggerType LOGIN = new TriggerType("LOGIN");
   @NotNull
   public static TriggerType SNEAK = new TriggerType("SNEAK", false);
   @NotNull
   public static TriggerType TELEPORT = new TriggerType("TELEPORT");
   @NotNull
   public static TriggerType TIMER = new TriggerType("TIMER");
   @NotNull
   public static TriggerType CAST = new TriggerType("CAST", false, false);
   @NotNull
   public static TriggerType API = new TriggerType("API");
   public static final TriggerType EQUIP_ARMOR = new TriggerType("EQUIP_ARMOR", false);
   public static final TriggerType UNEQUIP_ARMOR = new TriggerType("UNEQUIP_ARMOR", false);
   private static final Map<String, TriggerType> BY_ID = new HashMap<>();
   private final String id;
   private final boolean silent;
   private final boolean passive;
   private final boolean actionHandSpecific;

   public TriggerType(String id) {
      this(id, true, true);
   }

   public TriggerType(String id, boolean silent) {
      this(id, silent, true);
   }

   private TriggerType(String id, boolean silent, boolean passive) {
      this(id, silent, passive, false);
   }

   private TriggerType(String id, boolean silent, boolean passive, boolean actionHandSpecific) {
      this.id = id;
      this.silent = silent;
      this.passive = passive;
      this.actionHandSpecific = actionHandSpecific;
   }

   @NotNull
   public String name() {
      return this.id;
   }

   public boolean isSilent() {
      return this.silent;
   }

   public boolean isActionHandSpecific() {
      return this.actionHandSpecific;
   }

   public boolean isPassive() {
      return this.passive;
   }

   public String getName() {
      return UtilityMethods.caseOnWords(this.name().toLowerCase().replace("_", " "));
   }

   public String getLowerCaseId() {
      return this.name().toLowerCase().replace("_", "-");
   }

   @Override
   public String toString() {
      return this.name();
   }

   @NotNull
   public static TriggerType valueOf(String id) {
      return Objects.requireNonNull(BY_ID.get(id), "Could not find trigger type with ID '" + id + "'");
   }

   public static void register(@NotNull TriggerType trigger) {
      Validate.notNull(trigger, "Trigger type cannot be null");
      BY_ID.put(trigger.name(), trigger);
   }

   public static Collection<TriggerType> values() {
      return BY_ID.values();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         TriggerType that = (TriggerType)o;
         return this.id.equals(that.id);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id);
   }

   static {
      register(KILL_ENTITY);
      register(KILL_PLAYER);
      register(ATTACK);
      register(DAMAGED);
      register(DAMAGED_BY_ENTITY);
      register(DEATH);
      register(PLACE_BLOCK);
      register(BREAK_BLOCK);
      register(SHOOT_BOW);
      register(ARROW_TICK);
      register(ARROW_HIT);
      register(ARROW_LAND);
      register(SHOOT_TRIDENT);
      register(TRIDENT_TICK);
      register(TRIDENT_HIT);
      register(TRIDENT_LAND);
      register(RIGHT_CLICK);
      register(LEFT_CLICK);
      register(DROP_ITEM);
      register(SWAP_ITEMS);
      register(SHIFT_RIGHT_CLICK);
      register(SHIFT_LEFT_CLICK);
      register(SHIFT_DROP_ITEM);
      register(SHIFT_SWAP_ITEMS);
      register(LOGIN);
      register(SNEAK);
      register(TELEPORT);
      register(TIMER);
      register(CAST);
      register(API);
   }
}
