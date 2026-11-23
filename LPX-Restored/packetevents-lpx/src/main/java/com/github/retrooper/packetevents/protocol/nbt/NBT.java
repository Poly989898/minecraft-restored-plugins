/*
 * This file is part of ProtocolSupport - https://github.com/ProtocolSupport/ProtocolSupport
 * Copyright (C) 2021 ProtocolSupport
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.retrooper.packetevents.protocol.nbt;

import com.github.retrooper.packetevents.protocol.util.NbtCodecException;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public abstract class NBT {

    /**
     * Size in bytes this tag occupied on the wire, or -1 if unknown (LPX fork addition).
     */
    private int decodedSizeBytes = -1;

    public abstract NBTType<?> getType();

    public int getDecodedSizeBytes() {
        return this.decodedSizeBytes;
    }

    public void setDecodedSizeBytes(int decodedSizeBytes) {
        this.decodedSizeBytes = decodedSizeBytes;
    }

    @Override
    public abstract boolean equals(@Nullable Object other);

    @Override
    public abstract int hashCode();

    @Override
    public String toString() {
        return "nbt";
    }

    public abstract NBT copy();

    @SuppressWarnings("unchecked")
    public <T> T castOrThrow(Class<T> clazz) throws NbtCodecException {
        if (clazz.isInstance(this)) {
            return (T) this;
        }
        throw new NbtCodecException("expected: " + clazz.getName() + ", actual: " + this);
    }
}
