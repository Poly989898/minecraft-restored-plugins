package com.nexomc.nexo.decomp;

import kotlin.Result;
import kotlin.ResultKt;

/**
 * Replacement for Kotlin inline value-class calls (Result.constructor-impl and similar), which
 * the decompiler emitted with hyphens in the names and which are not valid Java identifiers.
 * Kotlin's Result is represented at runtime by the value itself or by a Result.Failure instance.
 */
public final class ResultShim {
   private ResultShim() {
   }

   public static Object ok(Object value) {
      return value;
   }

   public static boolean isFailure(Object result) {
      return result instanceof Result.Failure;
   }

   public static boolean isSuccess(Object result) {
      return !(result instanceof Result.Failure);
   }

   public static Throwable exceptionOrNull(Object result) {
      return result instanceof Result.Failure ? ((Result.Failure)result).exception : null;
   }

   public static Object getOrNull(Object result) {
      return isFailure(result) ? null : result;
   }

   public static Object failure(Throwable t) {
      return ResultKt.createFailure(t);
   }
}
