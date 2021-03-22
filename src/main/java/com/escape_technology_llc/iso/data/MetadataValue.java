/*
Copyright 2016 eScape Technology LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.escape_technology_llc.iso.data;

import com.escape_technology_llc.iso.box.Box;

/**
 * Represent QT metadata values.
 * @author escape-llc
 *
 */
public class MetadataValue {
	/**
	 * String value.
	 * @author escape-llc
	 *
	 */
	public static final class StringValue extends MetadataValue {
		public final String value;
		public StringValue(String type, int dtype, int locale, String value) {
			super(type, dtype, locale);
			this.value = value;
		}
		@Override public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append(type);
			sb.append(":");
			sb.append(dtype);
			sb.append("[");
			sb.append(value);
			sb.append("]");
			return sb.toString();
		}
	}
	/**
	 * Integer value (8 to 32 bit values).
	 * @author escape-llc
	 *
	 */
	public static final class IntegerValue extends MetadataValue {
		public final int value;
		public IntegerValue(String type, int dtype, int locale, int value) {
			super(type, dtype, locale);
			this.value = value;
		}
		@Override public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append(type);
			sb.append(":");
			sb.append(dtype);
			sb.append("[");
			sb.append(value);
			sb.append("]");
			return sb.toString();
		}
	}
	/**
	 * Long value (64 bit values).
	 * @author escape-llc
	 *
	 */
	public static final class LongValue extends MetadataValue {
		public final long value;
		public LongValue(String type, int dtype, int locale, long value) {
			super(type, dtype, locale);
			this.value = value;
		}
		@Override public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append(type);
			sb.append(":");
			sb.append(dtype);
			sb.append("[");
			sb.append(value);
			sb.append("]");
			return sb.toString();
		}
	}
	/**
	 * Binary value (e.g. image data).
	 * @author escape-llc
	 *
	 */
	public static final class BinaryValue extends MetadataValue {
		public final byte[] value;
		public BinaryValue(String type, int dtype, int locale, byte[] value) {
			super(type, dtype, locale);
			this.value = value;
		}
		@Override public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append(type);
			sb.append(":");
			sb.append(dtype);
			sb.append("[");
			sb.append(value.length);
			sb.append(" bytes of data");
			sb.append("]");
			return sb.toString();
		}
	}
	/**
	 * Unknown value (not interpreted).
	 * @author escape-llc
	 *
	 */
	public static final class Unknown extends MetadataValue {
		public final Box source;
		public Unknown(int dtype, int locale, Box source) {
			super(source.type, dtype, locale);
			this.source = source;
		}
		@Override public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append(type);
			sb.append(":");
			sb.append(dtype);
			sb.append("[");
			sb.append(source.toString());
			sb.append("]");
			return sb.toString();
		}
	}
	// metadata item name
	public final String type;
	// data type code
	public final int dtype;
	// locale indicator (country:16, language:16)
	public final int locale;
	protected MetadataValue(String type, int dtype, int locale) {
		this.type = type;
		this.dtype = dtype;
		this.locale = locale;
	}
}
