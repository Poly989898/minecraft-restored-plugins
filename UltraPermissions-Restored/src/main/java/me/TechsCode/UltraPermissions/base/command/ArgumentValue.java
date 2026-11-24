package me.TechsCode.UltraPermissions.base.command;

public class ArgumentValue<T> {
   private final String raw;
   private final T t;
   private final Validity validity;

   public static <T> ArgumentValue<T> empty(String text, EmptyReason emptyReason) {
      return new ArgumentValue<>(text, null, Validity.valueOf(emptyReason.name()));
   }

   public static <T> ArgumentValue<T> of(String text, T value) {
      return new ArgumentValue<>(text, (T)value, Validity.VALID);
   }

   private ArgumentValue(String text, T value, Validity validity) {
      this.raw = text;
      this.t = (T)value;
      this.validity = validity;
   }

   public String getRaw() {
      return this.raw;
   }

   public T get() {
      return this.t;
   }

   public Validity getValidity() {
      return this.validity;
   }

   public boolean isValid() {
      return this.validity == Validity.VALID;
   }

   public boolean isMatch() {
      return this.validity != Validity.NO_MATCH;
   }
}
