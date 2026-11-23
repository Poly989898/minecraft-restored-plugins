/*
 * This file is part of packetevents - https://github.com/retrooper/packetevents
 * Copyright (C) 2024 retrooper and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.retrooper.packetevents.protocol.nbt;

import com.github.retrooper.packetevents.exception.PacketLimitException;
import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Limits applied while reading NBT. LPX fork version: limit violations throw
 * {@link PacketLimitException} (NBT_SIZE, NBT_DEPTH, NBT_ELEMENTS) instead of IllegalArgumentException,
 * and an optional per-packet element counter is supported.
 */
@NullMarked
@ApiStatus.NonExtendable
public interface NBTLimiter {

    int DEFAULT_MAX_SIZE = Integer.getInteger("packetevents.nbt.default-max-size", 2 << 20); // 2MiB
    int DEFAULT_MAX_DEPTH = Integer.getInteger("packetevents.nbt.default-max-depth", 512);

    static NBTLimiter noop() {
        return new NBTLimiter() {
            @Override
            public void increment(int amount) {
            }

            @Override
            public void checkReadability(int length) {
            }

            @Override
            public void enterDepth() {
            }

            @Override
            public void exitDepth() {
            }

            @Override
            public void countElements(int amount) {
            }
        };
    }

    static NBTLimiter forBuffer(Object byteBuf) {
        return forBuffer(byteBuf, DEFAULT_MAX_SIZE);
    }

    static NBTLimiter forBuffer(Object byteBuf, int maxBytes) {
        return forBuffer(byteBuf, maxBytes, DEFAULT_MAX_DEPTH);
    }

    static NBTLimiter forBuffer(Object byteBuf, int maxBytes, int maxDepth) {
        return forBuffer(byteBuf, maxBytes, maxDepth, null);
    }

    static NBTLimiter forBuffer(Object byteBuf, int maxBytes, int maxDepth, @Nullable NBTElementCounter elementCounter) {
        return new NBTLimiter() {
            private int bytes;
            private int depth;

            @Override
            public void increment(int amount) {
                if (amount < 0) {
                    throw new IllegalArgumentException("Can't increment NBT limiter by negative amount: " + amount);
                }
                if (this.bytes + amount > maxBytes) {
                    throw new PacketLimitException(PacketLimitException.Limit.NBT_SIZE, (long) this.bytes + (long) amount, maxBytes);
                }
                this.bytes += amount;
            }

            @Override
            public void checkReadability(int length) {
                int readableBytes = ByteBufHelper.readableBytes(byteBuf);
                if (length > readableBytes) {
                    throw new IllegalArgumentException("Can't read more than possible: " + length + " > " + readableBytes);
                }
            }

            @Override
            public void enterDepth() {
                if (this.depth >= maxDepth) {
                    throw new PacketLimitException(PacketLimitException.Limit.NBT_DEPTH, (long) this.depth + 1L, maxDepth);
                }
                this.depth++;
            }

            @Override
            public void exitDepth() {
                if (this.depth <= 0) {
                    throw new IllegalArgumentException("Can't exit top-level depth");
                }
                this.depth--;
            }

            @Override
            public void countElements(int amount) {
                if (elementCounter != null) {
                    elementCounter.add(amount);
                }
            }
        };
    }

    void increment(int amount);

    void checkReadability(int length);

    void enterDepth();

    void exitDepth();

    /**
     * Counts NBT elements against the per-packet limit (LPX fork addition). No-op without a counter.
     */
    void countElements(int amount);
}
