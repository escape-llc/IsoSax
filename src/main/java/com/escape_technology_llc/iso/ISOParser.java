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
package com.escape_technology_llc.iso;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

import com.escape_technology_llc.iso.box.Box;
import com.escape_technology_llc.iso.box.FullBox;
import com.escape_technology_llc.iso.box.UserType;
import com.escape_technology_llc.iso.data.BoxDataFactory;
import com.escape_technology_llc.iso.data.Unmarshal;

/**
 * ISO Part 12 file parser.
 * @author escape-llc
 *
 */
public class ISOParser {
	/**
	 * Implementation of parse context.
	 * @author escape-llc
	 *
	 */
	static final class MPParseContext implements ParseContext {
		final RandomAccessFile raf;
		final ParseHandler eh;
		public MPParseContext(ParseHandler eh, RandomAccessFile raf) {
			if(eh == null)
				throw new IllegalArgumentException("eh");
			if(raf == null)
				throw new IllegalArgumentException("raf");
			this.raf = raf;
			this.eh = eh;
		}
		public void parseBox(Box box, ParseCallback pc) throws Exception {
			parseBoxes(raf, box, box.dataPosition(), box.dataLength(), this, pc);
		}
		@SuppressWarnings("unchecked")
		public <T> T create(Box box) throws Exception {
			return (T) BoxDataFactory.create(box, raf);
		}
		public byte[] materialize(long position, int length) throws Exception {
			final byte[] buf = new byte[length];
			raf.seek(position);
			raf.readFully(buf);
			return buf;
		}
		public ParseHandler handler() { return eh; }
	}
	/**
	 * Parse the given byte range for boxes.
	 * Assumes RAF position already done.
	 * Does not save or restore file pointer.
	 * @param raf source of bytes; should be positioned at box boundary.
	 * @param parent !NULL: parent box; NULL: top-level box.
	 * @param position initial offset (from start of file).  must be positioned at a box boundary.
	 * @param totalBytes number of bytes in box.
	 * @param ctx parse context.
	 * @param pc callback for boxes.
	 * @throws Exception
	 */
	static void parseBoxes(RandomAccessFile raf, Box parent, long position, long totalBytes, MPParseContext ctx, ParseCallback pc) throws Exception {
		//ctx.handler().message(String.format("parseBoxes position=%d totalBytes=%d", position, totalBytes));
		long current = 0;
		final byte[] buf32 = new byte[4];
		final byte[] buf64 = new byte[8];
		final byte[] lengthandtype = new byte[8];
		while(current < totalBytes) {
			if(current + lengthandtype.length > totalBytes) {
				ctx.handler().error(new IllegalStateException(String.format("%d leftover bytes cannot make a box", totalBytes - current)));
				break;
			}
			final long boxpos = position + current;
			//ctx.handler().message(String.format("BOX starts seek to %d", boxpos));
			raf.seek(boxpos);
			// TODO in udta list it can end with length==0 and nothing else!
			// that will cause this to overshoot reading by 4 bytes
			// also must kick out when that condition is detected!
			raf.readFully(lengthandtype);
			final int length = Unmarshal.getInt32(lengthandtype, 0);
			final String type = Unmarshal.getType(lengthandtype, 4);
			if(parent == null && !Box.isTopLevel(type)) {
				ctx.handler().error(new IllegalArgumentException(String.format("invalid top-level tag '%s' @%d", type, boxpos)));
				break;
			}
			if(length == 0 && parent != null) {
				ctx.handler().warning(new IllegalArgumentException(String.format("zero-size tag '%s' level %d @%d", type, parent == null ? 0 : parent.level() + 1, boxpos)));
				// not sure what to do here yet
			}
			int hdrsize = lengthandtype.length;
			// if length == 1 get extended length
			long full = (long)length;
			if(length == 1) {
				raf.readFully(buf64);
				full = Unmarshal.getInt64(buf64, 0);
				hdrsize += buf64.length;
			}
			if(current + full > totalBytes) {
				// cannot overshoot
				ctx.handler().warning(new IllegalStateException(String.format("overshooting limit %d current %d full %d diff %d", totalBytes, current, full, (current + full) - totalBytes)));
			}
			byte[] uuid = null;
			if(Box.UUID.equals(type)) {
				// extended UUID 16 bytes
				uuid = new byte[16];
				raf.readFully(uuid);
				hdrsize += uuid.length;
			}
			Box box = null;
			if(FullBox.isFull(type)) {
				// it's a full box get the version+flags
				raf.readFully(buf32);
				final int vf = Unmarshal.getInt32(buf32, 0);
				hdrsize += buf32.length;
				box = new FullBox(parent, type, boxpos, full, hdrsize, vf);
			}
			else if (uuid != null) {
				box = new UserType(parent, type, boxpos, full, hdrsize, uuid);
			}
			else {
				box = new Box(parent, type, boxpos, full, hdrsize);
			}
			if(parent == null) {
				//ctx.handler().message(String.format("BOX type='%s' position=%d current=%d length=%d full=%d hdrsize=%d boxpos=%d", type, position, current, length, full, hdrsize, boxpos));
			}
			pc.box(ctx, box);
			// length zero means box extends to end of file
			if(length == 0) break;
			current += full;
		}
		if(current > totalBytes) {
			ctx.handler().error(new IllegalStateException(String.format("Overshot current %d limit %d diff %d", current, totalBytes, current - totalBytes)));
		}
		else if(parent == null && current < totalBytes) {
			ctx.handler().error(new IllegalStateException(String.format("%d Extra bytes not in a box", totalBytes - current)));
		}
	}
	/**
	 * Parse the ISO container file, pass each top-level box to given callback.
	 * @param fx Source ISO container file.
	 * @param eh Handler to use.
	 * @param pc Parse callback.
	 * @throws Exception Invalid arguments.
	 */
	public static void parse(File fx, ParseHandler eh, ParseCallback pc) throws Exception {
		if(fx == null)
			throw new IllegalArgumentException("fx");
		if(eh == null)
			throw new IllegalArgumentException("eh");
		if(pc == null)
			throw new IllegalArgumentException("pc");
		final RandomAccessFile raf = new RandomAccessFile(fx, "r");
		try {
			final MPParseContext ctx = new MPParseContext(eh, raf);
			pc.start();
			parseBoxes(raf, null, 0, raf.length(), ctx, pc);
			pc.end(ctx);
		}
		finally {
			raf.close();
		}
	}
	/**
	 * Convert 16.16 fixed-point to float.
	 * @param val value to convert.
	 * @return converted to float.
	 */
	public static float toFloat_1616(int val) {
		return ((float) val) / 65536.0f;
	}
	/**
	 * Format duration in MS into hr:mn:sc
	 * @param millis number of MS.
	 * @return formatted value.
	 */
	public static String formatDuration(final long millis) {
	    final long hours = TimeUnit.MILLISECONDS.toHours(millis);
	    final long tominutes = TimeUnit.MILLISECONDS.toMinutes(millis);
	    final long minutes = tominutes - TimeUnit.HOURS.toMinutes(hours);
	    final long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(tominutes);
	    return String.format("%02d:%02d:%02d", hours, minutes, seconds); 
	}
	/**
	 * Format duration in MS into hr:mn:sc.xxx
	 * @param millis number of MS.
	 * @param forcems true: force MS display; false: leave off if zero.
	 * @return formatted value.
	 */
	public static String formatDuration(final long millis, boolean forcems) {
		final StringBuilder sb = new StringBuilder();
		sb.append(formatDuration(millis));
		final long mss = millis % 1000L;
		if(mss != 0L || forcems) {
			sb.append(".");
			sb.append(String.format("%03d", mss));
		}
		return sb.toString();
	}
}
