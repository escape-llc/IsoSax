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
import java.util.Arrays;
import java.util.HashMap;

import com.escape_technology_llc.iso.box.Box;
import com.escape_technology_llc.iso.box.FullBox;
import com.escape_technology_llc.iso.box.UserType;

/**
 * Facility for converting box contents into domain objects.
 * @author escape-llc
 *
 */
public class BoxDataFactory {
	static final int V0 = 20;
	static final int V1 = 32;
	static final int I32 = 4;
	static final int I64 = 8;
	static final int OFS_0 = 0*4;
	static final int OFS_1 = 1*4;
	static final int OFS_2 = 2*4;
	static final int OFS_3 = 3*4;
	static final int OFS_4 = 4*4;
	static final int OFS_5 = 5*4;
	/**
	 * Ability to unmarshal a box into a domain object.
	 * @author escape-llc
	 *
	 */
	public interface BoxUnmarshaler {
		Object unmarshal(Box box, RandomAccessFile raf) throws Exception;
	}
	static final HashMap<String, BoxUnmarshaler> createmap;
	/**
	 * Allocate a buffer to hold the box data.
	 * @param box Source box.
	 * @return new instance.
	 */
	static byte[] allocate(Box box) {
		final int lg = (int) box.dataLength();
		return new byte[lg];
	}
	static final BoxUnmarshaler fileType = new BoxUnmarshaler() {
		public Object unmarshal(Box box, RandomAccessFile raf) throws Exception {
			final byte[] buf = allocate(box);
			raf.readFully(buf);
			final String brand = Unmarshal.getType(buf, OFS_0);
			final int mv = Unmarshal.getInt32(buf, OFS_1);
			// get the list of compatible brands
			final int cbsz = (buf.length - I64)/I32;
			final String[] cbs = new String[cbsz];
			for(int ix = OFS_2, ct = 0; ix < buf.length; ix += I32, ct++) {
				final String cb = Unmarshal.getType(buf, ix);
				cbs[ct] = cb;
			}
			final FileType ft = new FileType(brand, mv, cbs);
			return ft;
		}
	};
	static final BoxUnmarshaler movieHeader = new BoxUnmarshaler() {
		final byte[] bufv0 = new byte[V0];
		final byte[] bufv1 = new byte[V1];
		public Object unmarshal(Box box, RandomAccessFile raf) throws Exception {
			final FullBox fb = (FullBox)box;
			switch(fb.version) {
			case 0:
				raf.readFully(bufv0);
				final int creation = Unmarshal.getInt32(bufv0, OFS_0);
				final int modification = Unmarshal.getInt32(bufv0, OFS_1);
				final int timescale = Unmarshal.getInt32(bufv0, OFS_2);
				final int duration = Unmarshal.getInt32(bufv0, OFS_3);
				final int rate = Unmarshal.getInt32(bufv0, OFS_4);
				final MovieHeader mh = new MovieHeader.V0(fb.flags, creation, modification, timescale, duration, rate, 0, 0);
				return mh;
			case 1:
				raf.readFully(bufv1);
				final long creation1 = Unmarshal.getInt64(bufv0, 0);
				final long modification1 = Unmarshal.getInt64(bufv0, 8);
				final int timescale1 = Unmarshal.getInt32(bufv0, 16);
				final long duration1 = Unmarshal.getInt64(bufv0, 20);
				final int rate1 = Unmarshal.getInt32(bufv0, 28);
				final MovieHeader mh1 = new MovieHeader.V1(fb.flags, creation1, modification1, timescale1, duration1, rate1, 0, 0);
				return mh1;
			}
			throw new IllegalArgumentException("Could not create movie header");
		}
	};
	static final BoxUnmarshaler mdhd = new BoxUnmarshaler() {
		final byte[] bufv0 = new byte[V0];
		final byte[] bufv1 = new byte[V1];
		public Object unmarshal(Box box, RandomAccessFile raf) throws Exception {
			final FullBox fb = (FullBox)box;
			switch(fb.version) {
			case 0:
				raf.readFully(bufv0);
				final int creation = Unmarshal.getInt32(bufv0, OFS_0);
				final int modification = Unmarshal.getInt32(bufv0, OFS_1);
				final int timescale = Unmarshal.getInt32(bufv0, OFS_2);
				final int duration = Unmarshal.getInt32(bufv0, OFS_3);
				final int lang = Unmarshal.getInt16(bufv0, OFS_4);
				final MediaHeader mh = new MediaHeader.V0(fb.flags, creation, modification, timescale, duration, lang);
				return mh;
			case 1:
				raf.readFully(bufv1);
				final long creation1 = Unmarshal.getInt64(bufv0, 0);
				final long modification1 = Unmarshal.getInt64(bufv0, 8);
				final int timescale1 = Unmarshal.getInt32(bufv0, 16);
				final long duration1 = Unmarshal.getInt64(bufv0, 20);
				final int lang1 = Unmarshal.getInt16(bufv0, 28);
				final MediaHeader mh1 = new MediaHeader.V1(fb.flags, creation1, modification1, timescale1, duration1, lang1);
				return mh1;
			}
			throw new IllegalArgumentException("Could not create media header");
		}
	};
	static final BoxUnmarshaler trakHeader = new BoxUnmarshaler() {
		final byte[] bufv0 = new byte[V0];
		final byte[] bufv1 = new byte[V1];
		public Object unmarshal(Box box, RandomAccessFile raf) throws Exception {
			final FullBox fb = (FullBox)box;
			switch(fb.version) {
			case 0:
				raf.readFully(bufv0);
				final int creation = Unmarshal.getInt32(bufv0, OFS_0);
				final int modification = Unmarshal.getInt32(bufv0, OFS_1);
				final int trackid = Unmarshal.getInt32(bufv0, OFS_2);
				// reserved int32 (12)
				final int duration = Unmarshal.getInt32(bufv0, OFS_4);
				final TrackHeader mh = new TrackHeader.V0(fb.flags, creation, modification, trackid, duration);
				return mh;
			case 1:
				raf.readFully(bufv1);
				final long creation1 = Unmarshal.getInt64(bufv0, 0);
				final long modification1 = Unmarshal.getInt64(bufv0, 8);
				final int trackid1 = Unmarshal.getInt32(bufv0, 16);
				// reserved int32 (20)
				final long duration1 = Unmarshal.getInt64(bufv0, 24);
				final TrackHeader mh1 = new TrackHeader.V1(fb.flags, creation1, modification1, trackid1, duration1);
				return mh1;
			}
			throw new IllegalArgumentException("Could not create track header");
		}
	};
	static final BoxUnmarshaler handler =  new BoxUnmarshaler() {
		public Object unmarshal(Box box, RandomAccessFile raf) throws Exception {
			final FullBox fb = (FullBox)box;
			switch(fb.version) {
			case 0:
				final byte[] buf = allocate(box);
				raf.readFully(buf);
				final int ck_htype = Unmarshal.getInt32(buf, OFS_0);
				//final String htype = ck_htype == 0 ? null : Unmarshal.getType(buf, OFS_0);
				final String type = Unmarshal.getType(buf, OFS_1);
				if(ck_htype != 0) {
					final String name = Unmarshal.getPascalString(buf, 20);
					return new Handler_V0(fb.flags, type, name);
				}
				else {
					final String name = Unmarshal.getString(buf, 20);
					return new Handler_V0(fb.flags, type, name);
				}
			}
			return null;
		}
	};
	static final BoxUnmarshaler editList = new BoxUnmarshaler() {
		public Object unmarshal(Box box, RandomAccessFile raf) throws Exception {
			final FullBox fb = (FullBox)box;
			switch(fb.version) {
			case 0:
				final byte[] buf = allocate(box);
				raf.readFully(buf);
				final int ct = Unmarshal.getInt32(buf,  OFS_0);
				final EditList_V0.Entry[] edts = new EditList_V0.Entry[ct];
				for(int ix = 0, ofs = OFS_1; ix < ct; ix++, ofs += 12) {
					// get the entry
					edts[ix] = new EditList_V0.Entry(Unmarshal.getInt32(buf, ofs), Unmarshal.getInt32(buf, ofs + I32), Unmarshal.getInt32(buf, ofs + I64));
				}
				return new EditList_V0(fb.flags, edts);
			}
			return null;
		}
	};
	static final BoxUnmarshaler tref = new BoxUnmarshaler() {
		public Object unmarshal(Box box, RandomAccessFile raf) throws Exception {
			// the reference is in a box
			final Box ref = extractBox(box, box.dataPosition(), raf);
			final byte[] buf = allocate(ref);
			raf.readFully(buf);
			// get the list of Track IDs
			final int cbsz = buf.length/I32;
			final int[] cbs = new int[cbsz];
			for(int ix = 0, ct = 0; ix < buf.length; ix += I32, ct++) {
				final int cb = Unmarshal.getInt32(buf, ix);
				cbs[ct] = cb;
			}
			return new TrackRef(ref.type, cbs);
		}
	};
	static final BoxUnmarshaler dref = new BoxUnmarshaler() {
		public Object unmarshal(Box box, RandomAccessFile raf) throws Exception {
			final FullBox fb = (FullBox)box;
			final byte[] buf = allocate(box);
			raf.readFully(buf);
			final int ct = Unmarshal.getInt32(buf,  OFS_0);
			final DataRef.Entry[] entries = new DataRef.Entry[ct];
			int offset = I32;
			for(int ix = 0; ix < ct; ix++) {
				// accumulate
				final int ileng = Unmarshal.getInt32(buf, offset);
				final String itype = Unmarshal.getType(buf, offset + I32);
				final int iflags = Unmarshal.getInt32(buf,  offset + I64);
				String location = null;
				// if $self then entry is terminated, else ILENG-12 bytes follow
				if((iflags & DataRef.FLAG_SELFREFERENCE) == 0) {
					// not self: accumulate
					if(Box.DREF_URL.equals(itype)) {
						// url: location
					}
					else if(Box.DREF_URN.equals(itype)) {
						// urn: name, location-optional
					}
				}
				entries[ix] = new DataRef.Entry(iflags, itype, location);
				offset += ileng;
			}
			return new DataRef(fb.flags, entries);
		}
	};
	static final BoxUnmarshaler ilst = new BoxUnmarshaler() {
		public Object unmarshal(Box box, RandomAccessFile raf) throws Exception {
			final byte[] buf = allocate(box);
			raf.readFully(buf);
			//final int leng = Unmarshal.getInt32(buf, OFS_0);
			final String type = Unmarshal.getType(buf,  OFS_1);
			if("data".equals(type)) {
				// for now assume UTF-8; check data type stuff
				final int dtype = Unmarshal.getInt32(buf, OFS_2);
				final int dlocale = Unmarshal.getInt32(buf, OFS_3);
				switch(dtype) {
				case 1: // UTF-8
					final String value = new String(buf, OFS_4, buf.length - OFS_4);
					return new MetadataValue.StringValue(box.type, dtype, dlocale, value);
				case 21: // BE signed int
				case 22: // BE unsigned int
					final int ileng = buf.length - OFS_4;
					int value3 = Integer.MIN_VALUE;
					switch(ileng) {
					case 1:
						value3 = (int)buf[OFS_4];
						break;
					case 2:
						value3 = Unmarshal.getInt16(buf, OFS_4);
						break;
					case 3:
						value3 = Unmarshal.getInt24(buf, OFS_4);
						break;
					case 4:
						value3 = Unmarshal.getInt32(buf, OFS_4);
						break;
					}
					return new MetadataValue.IntegerValue(box.type, dtype, dlocale, value3);
				case 13: // JPEG
				case 14: // PNG
				case 27: // BMP
					final byte[] value2 = Arrays.copyOfRange(buf, OFS_4, buf.length - OFS_4);
					return new MetadataValue.BinaryValue(box.type, dtype, dlocale, value2);
				case 65: // 8-bit
				case 75: // 8-bit unsigned
					return new MetadataValue.IntegerValue(box.type, dtype, dlocale, (int)buf[OFS_4]);
				case 66: // BE 16-bit
				case 76: // BE unsigned 16-bit
					return new MetadataValue.IntegerValue(box.type, dtype, dlocale, Unmarshal.getInt16(buf, OFS_4));
				case 67: // BE 32-bit
				case 77: // BE unsigned 32-bit
					return new MetadataValue.IntegerValue(box.type, dtype, dlocale, Unmarshal.getInt32(buf, OFS_4));
				case 74: // BE 64-bit
				case 78: // BE unsigned 64-bit
					return new MetadataValue.LongValue(box.type, dtype, dlocale, Unmarshal.getInt64(buf, OFS_4));
				default:
					return new MetadataValue.Unknown(dtype, dlocale, box);
				}
			}
			return null;
		}
	};
	static final BoxUnmarshaler stts = new BoxUnmarshaler() {
		public Object unmarshal(Box box, RandomAccessFile raf) throws Exception {
			final byte[] buf = allocate(box);
			raf.readFully(buf);
			final int count = Unmarshal.getInt32(buf, OFS_0);
			final TimeToSample.Entry[] ttss = new TimeToSample.Entry[count];
			for(int ix = 0, ofs = OFS_1; ix < count; ix ++) {
				ttss[ix] = new TimeToSample.Entry(Unmarshal.getInt32(buf, ofs), Unmarshal.getInt32(buf, ofs + I32));
				ofs += I64;
			}
			return new TimeToSample(ttss);
		}
	};
	static final BoxUnmarshaler stsc = new BoxUnmarshaler() {
		public Object unmarshal(Box box, RandomAccessFile raf) throws Exception {
			final byte[] buf = allocate(box);
			raf.readFully(buf);
			final int count = Unmarshal.getInt32(buf, OFS_0);
			final SampleToChunk.Entry[] ttss = new SampleToChunk.Entry[count];
			for(int ix = 0, ofs = OFS_1; ix < count; ix ++) {
				ttss[ix] = new SampleToChunk.Entry(Unmarshal.getInt32(buf, ofs), Unmarshal.getInt32(buf, ofs + I32), Unmarshal.getInt32(buf, ofs + I64));
				ofs += 12;
			}
			return new SampleToChunk(ttss);
		}
	};
	static final BoxUnmarshaler stsz = new BoxUnmarshaler() {
		public Object unmarshal(Box box, RandomAccessFile raf) throws Exception {
			final byte[] buf = allocate(box);
			raf.readFully(buf);
			final int ss = Unmarshal.getInt32(buf,  OFS_0);
			final int count = Unmarshal.getInt32(buf, OFS_1);
			final int[] ttss = ss == 0 ? new int[count] : null;
			if(ttss != null) {
				for(int ix = 0, ofs = OFS_2; ix < count; ix ++) {
					ttss[ix] = Unmarshal.getInt32(buf, ofs);
					ofs += I32;
				}
			}
			return new SampleSize(ss, ttss);
		}
	};
	static final BoxUnmarshaler stco = new BoxUnmarshaler() {
		public Object unmarshal(Box box, RandomAccessFile raf) throws Exception {
			final byte[] buf = allocate(box);
			raf.readFully(buf);
			final int count = Unmarshal.getInt32(buf, OFS_0);
			final int[] ttss = new int[count];
				for(int ix = 0, ofs = OFS_1; ix < count; ix ++) {
					ttss[ix] = Unmarshal.getInt32(buf, ofs);
					ofs += I32;
				}
			return new ChunkOffset(ttss);
		}
	};
	static final BoxUnmarshaler stsd = new BoxUnmarshaler() {
		public Object unmarshal(Box box, RandomAccessFile raf) throws Exception {
			final byte[] buf = allocate(box);
			raf.readFully(buf);
			final int count = Unmarshal.getInt32(buf, OFS_0);
			final SampleDescription.Entry[] ttss = new SampleDescription.Entry[count];
				for(int ix = 0, ofs = OFS_1; ix < count; ix ++) {
					final int esize = Unmarshal.getInt32(buf, ofs);
					final String dformat = Unmarshal.getType(buf, ofs + I32);
					// mbz (8..13)
					final int drindex = Unmarshal.getInt16(buf,  ofs + 14);
					// TODO dformat determines format of extra bytes [16..esize] and the entry class
					ttss[ix] = new SampleDescription.Entry(dformat, drindex);
					ofs += esize;
				}
			return new SampleDescription(ttss);
		}
	};
	static final BoxUnmarshaler stss = new BoxUnmarshaler() {
		public Object unmarshal(Box box, RandomAccessFile raf) throws Exception {
			final byte[] buf = allocate(box);
			raf.readFully(buf);
			final int count = Unmarshal.getInt32(buf, OFS_0);
			final int[] ttss = new int[count];
				for(int ix = 0, ofs = OFS_1; ix < count; ix ++) {
					ttss[ix] = Unmarshal.getInt32(buf, ofs);
					ofs += I32;
				}
			return new SyncSample(ttss);
		}
	};
	static {
		createmap = new HashMap<String, BoxUnmarshaler>();
		createmap.put(Box.DREF, dref);
		createmap.put(Box.ELST, editList);
		createmap.put(Box.FTYP, fileType);
		createmap.put(Box.HDLR, handler);
		createmap.put(Box.MVHD, movieHeader);
		createmap.put(Box.MDHD, mdhd);
		createmap.put(Box.STCO, stco);
		createmap.put(Box.STSC, stsc);
		createmap.put(Box.STSD, stsd);
		createmap.put(Box.STSS, stss);
		createmap.put(Box.STSZ, stsz);
		createmap.put(Box.STTS, stts);
		createmap.put(Box.TKHD, trakHeader);
		createmap.put(Box.TREF, tref);
		createmap.put("ilst/aART", ilst);
		createmap.put("ilst/covr", ilst);
		createmap.put("ilst/cprt", ilst);
		createmap.put("ilst/desc", ilst);
		createmap.put("ilst/ldes", ilst);
		createmap.put("ilst/purl", ilst);
		createmap.put("ilst/stik", ilst);
		createmap.put("ilst/trkn", ilst);
		createmap.put("ilst/�alb", ilst);
		createmap.put("ilst/�art", ilst);
		createmap.put("ilst/�cmt", ilst);
		createmap.put("ilst/�day", ilst);
		createmap.put("ilst/�gen", ilst);
		createmap.put("ilst/�lyr", ilst);
		createmap.put("ilst/�nam", ilst);
		createmap.put("ilst/�too", ilst);
		createmap.put("ilst/�ART", ilst);
	}
	/**
	 * Factory method to unmarshal boxes.
	 * Moves file pointer no restore.
	 * @param box  Box to unmarshal.
	 * @param raf Source of bytes.
	 * @return !NULL: unmarshalled; NULL: nothing registered.
	 * @throws Exception on errors.
	 */
	public static Object create(Box box, RandomAccessFile raf) throws Exception {
		String key = box.type;
		if(box.parent != null && "ilst".equals(box.parent.type)) {
			key = new StringBuilder(box.parent.type).append("/").append(box.type).toString();
		}
		if(createmap.containsKey(key)) {
			raf.seek(box.position + box.hdrsize);
			final BoxUnmarshaler dfb = createmap.get(key);
			return dfb.unmarshal(box, raf);
		}
		return null;
	}
	/**
	 * Extract the box information from given starting position.
	 * This is used when boxes contain data fields that are themselves boxes, but not part of the box hierarchy.
	 * Assumes file pointer is positioned correctly.
	 * Advances the file pointer past all header bytes.
	 * @param parent parent box.
	 * @param boxpos position of this box.
	 * @param raf source of bytes.
	 * @return new instance.
	 * @throws Exception on errors.
	 */
	public static Box extractBox(Box parent, long boxpos, RandomAccessFile raf) throws Exception {
		final byte[] lengthandtype = new byte[8];
		raf.readFully(lengthandtype);
		final int length = Unmarshal.getInt32(lengthandtype, OFS_0);
		final String type = Unmarshal.getType(lengthandtype, OFS_1);
		int hdrsize = lengthandtype.length;
		// if length == 1 get extended length
		long full = (long)length;
		if(length == 1) {
			final byte[] buf64 = new byte[I64];
			raf.readFully(buf64);
			full = Unmarshal.getInt64(buf64, OFS_0);
			hdrsize += buf64.length;
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
			final byte[] buf32 = new byte[I32];
			raf.readFully(buf32);
			final int vf = Unmarshal.getInt32(buf32, OFS_0);
			hdrsize += buf32.length;
			box = new FullBox(parent, type, boxpos, full, hdrsize, vf);
		}
		else if (uuid != null) {
			box = new UserType(parent, type, boxpos, full, hdrsize, uuid);
		}
		else {
			box = new Box(parent, type, boxpos, full, hdrsize);
		}
		return box;
	}
}