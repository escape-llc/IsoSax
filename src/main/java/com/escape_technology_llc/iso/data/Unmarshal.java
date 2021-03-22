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

import java.io.RandomAccessFile;

public final class Unmarshal {
	// important to use this encoding, otherwise the meta tags will not parse correctly, due to using the copyright character!
	// DO NOT use UTF-8
	public static final String ENCODING = "ISO-8859-1";
	/**
	 * extract big-endian Int32 value from buffer.
	 * @param encodedValue source buffer.
	 * @param offset starting offset.
	 * @return value.
	 */
	public static int getInt32(byte[] encodedValue, int offset) {
	    int value = encodedValue[offset] << Byte.SIZE * 3;
	    value ^= (encodedValue[offset + 1] & 0xFF) << Byte.SIZE * 2;
	    value ^= (encodedValue[offset + 2] & 0xFF) << Byte.SIZE * 1;
	    value ^= (encodedValue[offset + 3] & 0xFF);
	    return value;
	}
	/**
	 * extract bit-endian Int32 value from buffer.
	 * @param encodedValue source buffer.
	 * @param offset starting offset.
	 * @return value.
	 */
	public static int getInt24(byte[] encodedValue, int offset) {
	    int value = encodedValue[offset] << Byte.SIZE * 2;
	    value ^= (encodedValue[offset + 1] & 0xFF) << Byte.SIZE * 1;
	    value ^= (encodedValue[offset + 2] & 0xFF) << Byte.SIZE * 0;
	    return value;
	}
	/**
	 * extract big-endian Int16 value from buffer.
	 * @param encodedValue source buffer.
	 * @param offset starting offset.
	 * @return value.
	 */
	public static int getInt16(byte[] encodedValue, int offset) {
	    int value = encodedValue[offset] << Byte.SIZE * 1;
	    value ^= (encodedValue[offset + 1] & 0xFF) << Byte.SIZE * 0;
	    return value;
	}
	/**
	 * extract big-endian Int64 value from buffer.
	 * @param encodedValue source buffer.
	 * @param offset starting offset.
	 * @return value.
	 */
	public static long getInt64(byte[] encodedValue, int offset) {
	    long value = (long)encodedValue[offset + 0] << Byte.SIZE * 7;
	    value ^= (long)(encodedValue[offset + 1] & 0xFF) << Byte.SIZE * 6;
	    value ^= (long)(encodedValue[offset + 2] & 0xFF) << Byte.SIZE * 5;
	    value ^= (long)(encodedValue[offset + 3] & 0xFF) << Byte.SIZE * 4;
	    value ^= (long)(encodedValue[offset + 4] & 0xFF) << Byte.SIZE * 3;
	    value ^= (long)(encodedValue[offset + 5] & 0xFF) << Byte.SIZE * 2;
	    value ^= (long)(encodedValue[offset + 6] & 0xFF) << Byte.SIZE * 1;
	    value ^= (long)(encodedValue[offset + 7] & 0xFF);
	    return value;
	}
	/**
	 * Extract bytes in network order to String[4].
	 * @param buf input buffer.
	 * @param offset buffer offset.
	 * @return new instance.
	 * @throws Exception on errors.
	 */
	public static String getType(byte[] buf, int offset) throws Exception {
		return new String(buf, offset, 4, ENCODING);
	}
	/**
	 * NUL-terminated string ala C.
	 * @param buf input buffer.
	 * @param offset buffer offset.
	 * @return new instance.
	 * @throws Exception on errors.
	 */
	public static String getString(byte[] buf, int offset) throws Exception {
		for(int ix = offset; ix < buf.length; ix++) {
			if(buf[ix] == '\0') {
				return new String(buf, offset, ix - offset, ENCODING);
			}
		}
		// just call it all a string
		return new String(buf, offset, buf.length - offset, ENCODING);
		//throw new IndexOutOfBoundsException("getString ran out of buffer, no NUL detected");
	}
	/**
	 * Length-prefixed string ala Pascal.
	 * @param buf input buffer.
	 * @param offset buffer offset.
	 * @return new instance.
	 * @throws Exception on errors.
	 */
	public static String getPascalString(byte[] buf, int offset) throws Exception {
		final int lg = (int)buf[offset];
		if(offset + 1 + lg > buf.length)
			throw new IndexOutOfBoundsException("getString ran out of buffer, no NUL detected");
		return new String(buf, offset + 1, lg, ENCODING);
	}
	/**
	 * Extract bytes in network order to String[4].
	 * @param raf source file.
	 * @param buf target buffer.
	 * @return new instance.
	 * @throws Exception on errors.
	 */
	public static String getType(RandomAccessFile raf, byte[] buf) throws Exception {
		raf.readFully(buf);
		return new String(buf, ENCODING);
	}
}
