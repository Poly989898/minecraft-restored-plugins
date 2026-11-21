package com.ticxo.modelengine.api.utils.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PositionCodecTest {
   @Test
   void firstUpdateIsAbsoluteAndRepeatIsUnchanged() {
      PositionCodec codec = new PositionCodec();
      assertEquals(PositionCodec.Result.ABSOLUTE, codec.update(1.0, 2.0, 3.0));
      assertEquals(PositionCodec.Result.UNCHANGED, codec.update(1.0, 2.0, 3.0));
   }

   @Test
   void smallMoveIsEncodedAsDeltaInFourThousandNinetySixths() {
      PositionCodec codec = new PositionCodec();
      codec.update(0.0, 0.0, 0.0);
      assertEquals(PositionCodec.Result.MOVE, codec.update(1.0, -1.0, 0.5));
      assertEquals(4096, codec.getDeltaX());
      assertEquals(-4096, codec.getDeltaY());
      assertEquals(2048, codec.getDeltaZ());
   }

   @Test
   void moveTooLargeForShortFallsBackToAbsolute() {
      PositionCodec codec = new PositionCodec();
      codec.update(0.0, 0.0, 0.0);
      assertEquals(PositionCodec.Result.ABSOLUTE, codec.update(20.0, 0.0, 0.0));
   }

   @Test
   void absoluteResyncHappensEveryFourHundredMoves() {
      PositionCodec codec = new PositionCodec();
      codec.update(0.0, 0.0, 0.0);
      for (int i = 1; i < PositionCodec.RESYNC_INTERVAL; i++) {
         assertEquals(PositionCodec.Result.MOVE, codec.update(i * 0.01, 0.0, 0.0), "move " + i);
      }
      assertEquals(PositionCodec.Result.ABSOLUTE, codec.update(PositionCodec.RESYNC_INTERVAL * 0.01, 0.0, 0.0));
      assertEquals(PositionCodec.Result.MOVE, codec.update((PositionCodec.RESYNC_INTERVAL + 1) * 0.01, 0.0, 0.0));
   }
}
