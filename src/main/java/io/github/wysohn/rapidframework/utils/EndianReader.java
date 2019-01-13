/*
 * Copyright (C) 2015, 2017 wysohn.  All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation,  version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.wysohn.rapidframework.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EndianReader {
    private ByteBuffer bytebuffer;

    public EndianReader(byte[] data) {
        this(data, ByteOrder.LITTLE_ENDIAN);
    }

    public EndianReader(byte[] data, ByteOrder order) {
        bytebuffer = ByteBuffer.wrap(data);
        bytebuffer.order(order);
    }

    public byte readByte() {
        return bytebuffer.get();
    }

    public short readShort() {
        return bytebuffer.getShort();
    }

    public int readInt() {
        return bytebuffer.getInt();
    }

    public int readVarInt(){
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            read = readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    public long readLong() {
        return bytebuffer.getLong();
    }

    public long readVarLong() {
        int numRead = 0;
        long result = 0;
        byte read;
        do {
            read = readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 10) {
                throw new RuntimeException("VarLong is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    public void putByte(byte b) {
        bytebuffer.position(bytebuffer.position() - 1);
        bytebuffer.put(b);
    }

    public void putShort(short s) {
        bytebuffer.position(bytebuffer.position() - 2);
        bytebuffer.putShort(s);
    }

    public void putInt(int i) {
        bytebuffer.position(bytebuffer.position() - 4);
        bytebuffer.putInt(i);
    }

    public void putVarInt(int value) {
        do {
            byte temp = (byte)(value & 0b01111111);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            putByte(temp);
        } while (value != 0);
    }

    public void putLong(long l) {
        bytebuffer.position(bytebuffer.position() - 8);
        bytebuffer.putLong(l);
    }

    public void putVarLong(long value) {
        do {
            byte temp = (byte)(value & 0b01111111);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            putByte(temp);
        } while (value != 0);
    }

    public void writeByte(byte value) {
        bytebuffer.put(value);
    }

    public void writeShort(short value) {
        bytebuffer.putShort(value);
    }

    public void writeInt(int value) {
        bytebuffer.putInt(value);
    }

    public void writeLong(long value) {
        bytebuffer.putLong(value);
    }

    public void back(int byteNums) {
        bytebuffer.position(bytebuffer.position() - byteNums);
    }

    public void reset() {
        bytebuffer.rewind();
    }

    public int getRemaining() {
        return bytebuffer.remaining();
    }

    public byte[] toByteArray() {
        return bytebuffer.array();
    }
}
