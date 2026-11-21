package io.lumine.mythic.bukkit.utils.serialize;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Optl<T> {
   private static final Optl<?> EMPTY = new Optl();
   private T value;

   @Nonnull
   public static <T> Optl<T> empty() {
      return (Optl<T>)EMPTY;
   }

   @Nonnull
   public static <T> Optl<T> of(@Nullable T value) {
      return new Optl<>(value);
   }

   @Nonnull
   public static <T> Optl<T> fromOptional(@Nonnull Optl<T> value) {
      return value.isPresent() ? of(value.get()) : empty();
   }

   private Optl() {
      this.value = null;
   }

   private Optl(@Nullable T value) {
      this.value = value;
   }

   @Nullable
   public T get() {
      if (!this.isPresent()) {
         throw new NoSuchElementException("No value present");
      } else {
         return this.value;
      }
   }

   @Nonnull
   public Optional<T> asOptional() {
      return this.isPresent() ? Optional.ofNullable(this.value) : Optional.empty();
   }

   public boolean isPresent() {
      return this.value != null;
   }

   public void ifPresent(@Nonnull Consumer<? super T> consumer) {
      if (this.isPresent()) {
         consumer.accept(this.value);
      }
   }

   @Nonnull
   public Optl<T> filter(@Nonnull Predicate<? super T> predicate) {
      Objects.requireNonNull(predicate);
      if (!this.isPresent()) {
         return this;
      } else {
         return predicate.test(this.value) ? this : empty();
      }
   }

   @Nonnull
   public <U> Optl<U> map(@Nonnull Function<? super T, ? extends U> mapper) {
      Objects.requireNonNull(mapper);
      return !this.isPresent() ? empty() : of(mapper.apply(this.value));
   }

   @Nonnull
   public <U> Optl<U> flatMap(@Nonnull Function<? super T, Optl<U>> mapper) {
      Objects.requireNonNull(mapper);
      return !this.isPresent() ? empty() : Objects.requireNonNull(mapper.apply(this.value));
   }

   @Nullable
   public T orElse(@Nullable T other) {
      return this.isPresent() ? this.value : other;
   }

   @Nullable
   public T orElseGet(@Nonnull Supplier<? extends T> other) {
      return this.isPresent() ? this.value : other.get();
   }

   @Nullable
   public <X extends Throwable> T orElseThrow(@Nonnull Supplier<? extends X> exceptionSupplier) throws X {
      if (this.isPresent()) {
         return this.value;
      } else {
         throw exceptionSupplier.get();
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof Optl<?> other) ? false : this.isPresent() == other.isPresent() && Objects.equals(this.value, other.value);
      }
   }

   @Override
   public int hashCode() {
      return !this.isPresent() ? -1 : Objects.hashCode(this.value);
   }

   @Override
   public String toString() {
      return this.isPresent() ? String.format("Optl[%s]", this.value) : "Optl.empty";
   }
}
