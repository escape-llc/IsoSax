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
package com.escape_technology_llc.iso.box;

import java.util.HashSet;

/**
 * ISO Full box with flags/version.
 * For purposes of this framework, the version/flags are considered part of the header!
 * Full box lays out like this (header length == 12):
 * [0..3] length (N)
 * [4..7] type
 * [8..8] version
 * [9..11] flags
 * [12..N-1] data
 * Extended-length Full Box lays out like this (header length == 20):
 * [0..3] 0001
 * [4..7] type
 * [8..15] 64-bit extended length (N)
 * [16..16] version
 * [17..19] flags
 * [20..N-1] data
 * 
 * @author escape-llc
 *
 */
public class FullBox extends Box {
	static final HashSet<String> fulltags;
	static {
		fulltags = new HashSet<String>();
		fulltags.add(Box.BXML);
		fulltags.add(Box.DREF);
		fulltags.add(Box.ELST);
		fulltags.add(Box.HDLR);
		fulltags.add(Box.ILOC);
		fulltags.add(Box.MDHD);
		fulltags.add(Box.META);
		fulltags.add(Box.MVHD);
		fulltags.add(Box.PDIN);
		fulltags.add(Box.STCO);
		fulltags.add(Box.STSC);
		fulltags.add(Box.STSD);
		fulltags.add(Box.STSS);
		fulltags.add(Box.STSZ);
		fulltags.add(Box.STTS);
		fulltags.add(Box.TKHD);
		fulltags.add(Box.XML);
	}
	// only low 8 bits populated
	public final int version;
	// only low 24 bits populated
	public final int flags;
	/**
	 * Ctor.
	 * @param parent Parent box; may be NULL.
	 * @param type Box type code.
	 * @param position Starting offset.
	 * @param length Length of entire box.
	 * @param hdrsize Length of header, including the version/flags.
	 * @param versionflags The version/flags bits [8/24].
	 */
	public FullBox(Box parent, String type, long position, long length, int hdrsize, int versionflags) {
		super(parent, type, position, length, hdrsize);
		this.version = (versionflags >> 24) & 0xff;
		this.flags = (versionflags & 0x00ffffff);
	}
	@Override public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(type);
		sb.append(".v");
		sb.append(version);
		sb.append(":");
		sb.append(Integer.toString(flags, 16));
		sb.append("@");
		final String px = Long.toString(position);
		sb.append(px);
		final long lastbyte = position + length - 1L;
		final String lbx = Long.toString(lastbyte);
		sb.append("-");
		sb.append(shortestSuffix(px, lbx));
		sb.append("[");
		sb.append(length);
		sb.append("/");
		sb.append(hdrsize);
		sb.append("] ");
		return sb.toString();
	}
	/**
	 * Return whether the given type requires a full box.
	 * @param type
	 * @return true: full box; false: not.
	 */
	public static boolean isFull(String type) {
		return fulltags.contains(type);
	}
}
