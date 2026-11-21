package com.ticxo.modelengine.api.utils.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** Queued values are double buffered: writers set, readers only see the value after pass(). */
class QueuedValuesTest {
   @Test
   void intIsVisibleToReaderOnlyAfterPass() {
      QueuedInt value = new QueuedInt(1);
      value.set(5);
      assertEquals(1, value.get());
      assertEquals(5, value.getUnsafe());
      value.pass();
      assertEquals(5, value.get());
   }

   @Test
   void setUnsafeWritesTheReaderSideDirectly() {
      QueuedInt value = new QueuedInt(1);
      value.setUnsafe(9);
      assertEquals(9, value.get());
      assertEquals(1, value.getUnsafe());
   }

   @Test
   void doubleAndFloatFollowTheSameProtocol() {
      QueuedDouble d = new QueuedDouble(0.5);
      d.set(2.5);
      assertEquals(0.5, d.get());
      d.pass();
      assertEquals(2.5, d.get());

      QueuedFloat f = new QueuedFloat(0.5F);
      f.set(2.5F);
      assertEquals(0.5F, f.get());
      f.pass();
      assertEquals(2.5F, f.get());
   }

   @Test
   void booleanFollowsTheSameProtocol() {
      QueuedBoolean b = new QueuedBoolean(false);
      b.set(true);
      assertFalse(b.get());
      b.pass();
      assertTrue(b.get());
   }
}
