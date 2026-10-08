/*
 * Copyright 2022 The Embulk project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.embulk.spi.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.msgpack.value.Value;
import org.msgpack.value.impl.ImmutableDoubleValueImpl;

public final class FakeJsonDouble implements JsonValue {
    private FakeJsonDouble(final double value) {
        this.value = value;
        this.msgpackDoubleCache = null;
    }

    public static FakeJsonDouble of(final double value) {
        return new FakeJsonDouble(value);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.DOUBLE;
    }

    @Override
    public int presumeReferenceSizeInBytes() {
        return 8;
    }

    public boolean isIntegral() {
        return !Double.isInfinite(this.value) && this.value == Math.rint(this.value);
    }

    public boolean isByteValue() {
        return this.isIntegral() && ((double) Byte.MIN_VALUE) <= this.value && this.value <= ((double) Byte.MAX_VALUE);
    }

    public boolean isShortValue() {
        return this.isIntegral() && ((double) Short.MIN_VALUE) <= this.value && this.value <= ((double) Short.MAX_VALUE);
    }

    public boolean isIntValue() {
        return this.isIntegral() && ((double) Integer.MIN_VALUE) <= this.value && this.value <= ((double) Integer.MAX_VALUE);
    }

    public boolean isLongValue() {
        return this.isIntegral() && -0x1p63 <= this.value && this.value < 0x1p63;
    }

    public byte byteValue() {
        return (byte) this.value;
    }

    public byte byteValueExact() {
        if (!this.isByteValue()) {
            throw new ArithmeticException("Out of the range of byte, or not integral: " + this.value);
        }
        return (byte) this.value;
    }

    public short shortValue() {
        return (short) this.value;
    }

    public short shortValueExact() {
        if (!this.isShortValue()) {
            throw new ArithmeticException("Out of the range of short, or not integral: " + this.value);
        }
        return (short) this.value;
    }

    public int intValue() {
        return (int) this.value;
    }

    public int intValueExact() {
        if (!this.isIntValue()) {
            throw new ArithmeticException("Out of the range of int, or not integral: " + this.value);
        }
        return (int) this.value;
    }

    public long longValue() {
        return (long) this.value;
    }

    public long longValueExact() {
        if (!this.isLongValue()) {
            throw new ArithmeticException("Out of the range of long, or not integral: " + this.value);
        }
        return (long) this.value;
    }

    public BigInteger bigIntegerValue() {
        return this.bigDecimalValueInternal().toBigInteger();
    }

    public BigInteger bigIntegerValueExact() {
        return this.bigDecimalValueInternal().toBigIntegerExact();
    }

    public float floatValue() {
        return (float) this.value;
    }

    public double doubleValue() {
        return this.value;
    }

    public BigDecimal bigDecimalValue() {
        return this.bigDecimalValueInternal();
    }

    @Override
    public String toJson() {
        return Double.toString(this.value);
    }

    @Deprecated
    public Value toMsgpack() {
        if (this.msgpackDoubleCache != null) {
            return this.msgpackDoubleCache;
        }

        this.msgpackDoubleCache = new ImmutableDoubleValueImpl(this.value);
        return this.msgpackDoubleCache;
    }

    @Override
    public String toString() {
        return Double.toString(this.value);
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (otherObject == this) {
            return true;
        }

        // Check by `instanceof` in case against unexpected arbitrary extension of JsonValue.
        if (otherObject instanceof FakeJsonDouble) {
            final FakeJsonDouble other = (FakeJsonDouble) otherObject;
            return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(other.value);
        }

        // Fake!
        if (otherObject instanceof JsonDouble) {
            final JsonDouble other = (JsonDouble) otherObject;
            return Double.doubleToLongBits(this.doubleValue()) == Double.doubleToLongBits(other.doubleValue());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(this.value);
    }

    private BigDecimal bigDecimalValueInternal() {
        if (Double.isNaN(this.value) || Double.isInfinite(this.value)) {
            throw new ArithmeticException("Not a finite number: " + this.value);
        }
        return BigDecimal.valueOf(this.value);
    }

    private final double value;

    private ImmutableDoubleValueImpl msgpackDoubleCache;
}
