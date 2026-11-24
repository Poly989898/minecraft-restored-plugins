package io.lumine.mythic.lib.api.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Ref<E> {
   @Nullable
   E ref;

   public Ref(@Nullable E e) {
      this.ref = e;
   }

   public Ref() {
      this.ref = null;
   }

   @Nullable
   public E getValue() {
      return this.ref;
   }

   @NotNull
   public E getValue(@NotNull E def) {
      return this.ref == null ? def : this.ref;
   }

   public void setValue(@Nullable E e) {
      this.ref = e;
   }

   public static <S> void setValue(@Nullable Ref<S> ref, @Nullable S value) {
      if (ref != null) {
         ref.setValue(value);
      }
   }

   void RefExample() {
      double d_primitive = 20.0;
      Double d_class = 20.0;
      Ref<Double> d_ref = new Ref<>(20.0);
      this.RefExample_Process(d_primitive, d_class, d_ref);
      this.Print("Primitive: " + d_primitive);
      this.Print("Class: " + d_class);
      this.Print("Ref: " + d_ref.getValue());
   }

   void RefExample_Process(double d_primitive, Double d_class, Ref<Double> d_ref) {
      d_primitive += 10.0;
      d_class = d_class + 10.0;
      d_ref.setValue(d_ref.getValue() + 10.0);
   }

   void Print(String log) {
   }
}
