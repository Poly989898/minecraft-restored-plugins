package team.unnamed.creative.model;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/** @deprecated */
@Deprecated
public class ItemPredicate {
   private final String name;
   private final Object value;

   private ItemPredicate(String name, Object value) {
      this.name = (String)Objects.requireNonNull(name, "name");
      this.value = Objects.requireNonNull(value, "value");
   }

   public String name() {
      return this.name;
   }

   public Object value() {
      return this.value;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{name=" + this.name + ", value=" + String.valueOf(this.value) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ItemPredicate that = (ItemPredicate)o;
         return this.name.equals(that.name) && this.value.equals(that.value);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.name, this.value});
   }

   public static ItemPredicate angle(float angle) {
      return new ItemPredicate("angle", angle);
   }

   public static ItemPredicate blocking() {
      return truePredicate("blocking");
   }

   public static ItemPredicate broken() {
      return truePredicate("broken");
   }

   public static ItemPredicate cast() {
      return truePredicate("cast");
   }

   public static ItemPredicate cooldown(float cooldown) {
      return new ItemPredicate("cooldown", cooldown);
   }

   public static ItemPredicate damage(float damage) {
      return new ItemPredicate("damage", damage);
   }

   public static ItemPredicate damaged() {
      return truePredicate("damaged");
   }

   public static ItemPredicate lefthanded() {
      return truePredicate("lefthanded");
   }

   public static ItemPredicate pull(float pull) {
      return new ItemPredicate("pull", pull);
   }

   public static ItemPredicate pulling() {
      return truePredicate("pulling");
   }

   public static ItemPredicate charged() {
      return truePredicate("charged");
   }

   public static ItemPredicate firework() {
      return truePredicate("firework");
   }

   public static ItemPredicate throwing() {
      return truePredicate("throwing");
   }

   public static ItemPredicate time(float time) {
      return new ItemPredicate("time", time);
   }

   public static ItemPredicate customModelData(int data) {
      return new ItemPredicate("custom_model_data", data);
   }

   public static ItemPredicate custom(String name, Object value) {
      return new ItemPredicate(name, value);
   }

   private static ItemPredicate truePredicate(String name) {
      return new ItemPredicate(name, 1.0F);
   }
}
