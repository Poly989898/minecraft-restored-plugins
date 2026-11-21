package io.lumine.mythic.core.utils;

import java.util.ArrayList;
import java.util.List;

public final class TextOpacityFade {
   private static final int SIGN_BOUNDARY = 128;

   private TextOpacityFade() {
   }

   public static List<TextOpacityFade.Step> plan(int from, int to, int duration) {
      if (duration > 0 && from != to) {
         if (from >= 128 == to >= 128) {
            return List.of(new TextOpacityFade.Step(0, from, to, duration));
         } else {
            boolean down = from >= 128;
            int segmentEnd = down ? 128 : 127;
            int seam = down ? 127 : 128;
            int toSegmentEnd = Math.abs(from - segmentEnd);
            int fromSeam = Math.abs(to - seam);
            int segmentTicks = Math.max(toSegmentEnd > 0 ? 1 : 0, Math.round((float)duration * toSegmentEnd / Math.max(1, toSegmentEnd + fromSeam)));
            int remainderTicks = Math.max(fromSeam > 0 ? 1 : 0, duration - segmentTicks);
            List<TextOpacityFade.Step> steps = new ArrayList<>(3);
            if (toSegmentEnd > 0) {
               steps.add(new TextOpacityFade.Step(0, from, segmentEnd, segmentTicks));
            }

            steps.add(new TextOpacityFade.Step(segmentTicks, toSegmentEnd > 0 ? segmentEnd : from, seam, 0));
            if (fromSeam > 0) {
               steps.add(new TextOpacityFade.Step(segmentTicks + 1, seam, to, remainderTicks));
            }

            return List.copyOf(steps);
         }
      } else {
         return List.of(new TextOpacityFade.Step(0, from, to, Math.max(duration, 0)));
      }
   }

   public record Step(int delayTicks, int expectedOpacity, int opacity, int interpolationDuration) {
   }
}
