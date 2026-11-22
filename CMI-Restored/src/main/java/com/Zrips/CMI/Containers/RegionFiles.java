package com.Zrips.CMI.Containers;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RegionFiles {
  private static final int SECTOR_BYTES = 4096;
  private static final int SECTOR_INTS = 1024;
  private RandomAccessFile file;
  private final int[] offsets = new int[1024];

  public RegionFiles(File regionFile) {
    try {
      this.file = new RandomAccessFile(regionFile, "r");
      this.file.seek(0L);

      for (int value = 0; value < 1024; value++) {
        int offset = this.file.readInt();
        this.offsets[value] = offset;
      }
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }

  private int getOffset(int value, int localZ) {
    return this.offsets[value + localZ * 32];
  }

  public boolean hasChunk(int value, int localZ) {
    return this.getOffset(value, localZ) != 0;
  }

  public void close() throws IOException {
    this.file.close();
  }
}
