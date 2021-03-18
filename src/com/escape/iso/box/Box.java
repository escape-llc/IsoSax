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
package com.escape.iso.box;

import java.util.HashSet;
import java.util.Stack;

/**
 * Represents the packaging unit of ISO Part 12 file data model.
 * For purposes of this framework, the version/flags are considered part of the header!
 * Basic box (header length == 8):
 * [0..3] length (N)
 * [4..7] type
 * [8..N-1] data
 * EOF box (length implied to EOF):
 * [0..3] 0000
 * [4..7] type
 * [8..EOF] data
 * Full box (header length == 12):
 * [0..3] length (N)
 * [4..7] type
 * [8..8] version
 * [9..11] flags
 * [12..N-1] data
 * 64-bit Extended-length box (header length == 16):
 * [0..3] 0001
 * [4..7] type
 * [8..15] extended length (N)
 * [16..N-1] data
 * 64-bit Extended-length Full Box (header length == 20):
 * [0..3] 0001
 * [4..7] type
 * [8..15] extended length (N)
 * [16..16] version
 * [17..19] flags
 * [20..N-1] data
 * User-defined box (header length == 24):
 * [0..3] length (N)
 * [4..7] "uuid"
 * [8..23] uuid bytes
 * [24..N-1] data
 * 64-bit Extended-length user-defined box (header length == 32):
 * [0..3] 0001
 * [4..7] "uuid"
 * [8..15] extended length (N)
 * [16..31] uuid bytes
 * [32..N-1] data
 * 
 * @author escape-llc
 *
 */
public class Box {
	// level 0
	public static final String FTYP = "ftyp";
	public static final String MDAT = "mdat";
	public static final String META = "meta";
	public static final String MFRA = "mfra";
	public static final String MOOF = "moof";
	public static final String MOOV = "moov";
	public static final String PDIN = "pdin";
	public static final String WIDE = "wide";
	// level agnostic
	public static final String FREE = "free";
	public static final String HDLR = "hdlr";
	public static final String SKIP = "skip";
	public static final String UDTA = "udta";
	public static final String UUID = "uuid";
	public static final String BXML = "bxml";
	public static final String XML  = "xml ";
	public static final String DREF_URL  = "url ";
	public static final String DREF_URN  = "urn ";
	public static final String DREF_ALIS = "alis";
	// DONT KNOW look it up
	public static final String ILOC = "iloc";
	public static final String ILST = "ilst";
	// level 1
	public static final String MVHD = "mvhd";
	public static final String MVEX = "mvex";
	public static final String TRAF = "traf";
	public static final String TRAK = "trak";
	// level 2
	public static final String TKHD = "tkhd";
	public static final String EDTS = "edts";
	public static final String MDIA = "mdia";
	public static final String TREF = "tref";
	// level 3
	public static final String MDHD = "mdhd";
	public static final String MINF = "minf";
	public static final String ELST = "elst";
	// level 4
	public static final String DINF = "dinf";
	public static final String GMHD = "gmhd";
	public static final String STBL = "stbl";
	// level 5
	public static final String DREF = "dref";
	public static final String STCO = "stco";
	public static final String STSC = "stsc";
	public static final String STSD = "stsd";
	public static final String STSS = "stss";
	public static final String STSZ = "stsz";
	public static final String STTS = "stts";
	
	static final HashSet<String> hasboxes;
	static final HashSet<String> toplevel;
	//static final HashSet<String> containment;
	static {
		//containment = new HashSet<String>();
		hasboxes = new HashSet<String>();
		toplevel = new HashSet<String>();
		
		// L0 and agnostic
		hasboxes.add(META);
		hasboxes.add(MFRA);
		hasboxes.add(MOOF);
		hasboxes.add(MOOV);
		hasboxes.add(UDTA);
		// L1
		hasboxes.add(TRAF);
		hasboxes.add(TRAK);
		hasboxes.add(MVEX);
		// L2
		hasboxes.add(EDTS);
		hasboxes.add(MDIA);
		// L3
		hasboxes.add(MINF);
		// L4
		hasboxes.add(DINF);
		hasboxes.add(GMHD);
		hasboxes.add(STBL);
		
		// init top-level tag map
		toplevel.add(FREE);
		toplevel.add(FTYP);
		toplevel.add(MDAT);
		toplevel.add(META);
		toplevel.add(MFRA);
		toplevel.add(MOOF);
		toplevel.add(MOOV);
		toplevel.add(PDIN);
		toplevel.add(SKIP);
		toplevel.add(UUID);
		toplevel.add(WIDE);
	}
	// box length; includes the length field itself.
	public final long length;
	// 4-byte type code as Unicode string
	public final String type;
	// absolute offset of this box
	public final long position;
	// number of bytes in the header portion
	public final int hdrsize;
	// the parent; may be NULL
	public final Box parent;
	/**
	 * Ctor.
	 * @param parent parent box; may be NULL for root box.
	 * @param type box type code in string format.
	 * @param position absolute file offset of this box.
	 * @param length the overall length of the box, independent of header encoding.
	 * @param hdrsize number of header bytes, used to skip to the data portion.
	 */
	public Box(Box parent, String type, long position, long length, int hdrsize) {
		if(type == null || type.length() == 0)
			throw new IllegalArgumentException("type");
		this.parent = parent;
		this.type = type;
		this.position = position;
		this.length = length;
		this.hdrsize = hdrsize;
	}
	/**
	 * Return position of the first data byte after the header.
	 * @return
	 */
	public long dataPosition() { return position + hdrsize; }
	/**
	 * Return the length of the data in this box.
	 * @return
	 */
	public long dataLength() { return length - hdrsize; }
	/**
	 * Return the tree level of this box.
	 * Root level is zero.
	 * @return
	 */
	public int level() {
		if(parent == null) return 0;
		int ct = 0;
		Box cx = parent;
		while(cx != null) {
			ct++;
			cx = cx.parent;
		}
		return ct;
	}
	/**
	 * Concatenate a string representing this box's Path.
	 * @return formatted path.
	 */
	public String path() {
		if(parent == null) {
			return "/" + type;
		}
		final Stack<String> names = new Stack<String>();
		Box cx = this;
		while(cx != null) {
			names.push(cx.type);
			cx = cx.parent;
		}
		final StringBuilder sb = new StringBuilder();
		while(!names.isEmpty()) {
			final String name = names.pop();
			sb.append("/");
			sb.append(name);
		}
		return sb.toString();
	}
	@Override public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(type);
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
		sb.append("]");
		return sb.toString();
	}
	/**
	 * Take two presumably numeric strings, and return only non-matching suffix characters from the second one.
	 * Used to format a "range" string like "123456-4000".
	 * 
	 * Example: val1 = "123456", val2 = "124000", return = "4000".
	 * 
	 * @param val1 First value.
	 * @param val2 Second value.
	 * @return  non-matching digits from val2.
	 */
	public static String shortestSuffix(String val1, String val2) {
		if(val1.length() != val2.length()) return val2;
		int ix = 0;
		for( ; val1.charAt(ix) == val2.charAt(ix) && ix < val2.length(); ix++) ;
		return val2.substring(ix, val2.length());
	}
	/**
	 * Return whether the given box contains boxes for decomposition.
	 * @param box
	 * @return true: can decompose; false: no more boxes.
	 */
	public static boolean hasBoxes(Box box) {
		return hasboxes.contains(box.type);
	}
	/**
	 * Return whether the given box is in the list of ISO Part 12 top-level boxes.
	 * @param type
	 * @return true: top-level; false: not.
	 */
	public static boolean isTopLevel(String type) {
		return toplevel.contains(type);
	}
}
