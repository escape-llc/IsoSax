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
package com.escape.mp4.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.Test;

import com.escape.iso.ConsoleHandler;
import com.escape.iso.ISOParser;
import com.escape.iso.NullHandler;
import com.escape.iso.ParseCallback;
import com.escape.iso.ParseContext;
import com.escape.iso.box.Box;
import com.escape.iso.box.handler.BoxTreeDump;
import com.escape.iso.box.handler.IsoContainerHandler;
import com.escape.iso.box.handler.MetadataHandler;
import com.escape.iso.box.handler.TrackHandler;
import com.escape.iso.data.FileType;
import com.escape.iso.data.Handler_V0;
import com.escape.iso.data.IsoMediaContainer;
import com.escape.iso.data.Meta;
import com.escape.iso.data.MovieHeader;
import com.escape.iso.data.SoundTable;
import com.escape.iso.data.Track;
import com.escape.iso.data.Unmarshal;

public class MpegTests {
	private static String PATH;

	static {
		try {
			PATH = new File(".").getCanonicalPath() + "/src/com/escape/mp4/tests/";
		} catch (IOException e) {
		}
	}
	static final class Tester implements ParseCallback {
		int counter;
		boolean gotstart;
		boolean gotend;
		FileType ft;
		Meta meta;
		public void box(ParseContext pc, Box box) throws Exception {
			pc.handler().message(String.format("%d %s %s", box.level(), box.path(), box));
			counter++;
			if(box.type.equals(Box.FTYP)) {
				// extract the data
				ft = pc.create(box);
				pc.handler().message("FTYP " + ft);
			}
			else if(box.type.equals(Box.TRAK)) {
				final TrackHandler tkh = new TrackHandler();
				tkh.start();
				pc.parseBox(box, tkh);
				tkh.end(pc);
				final Track trak = tkh.render();
				pc.handler().message("TRAK\n" + trak);
			}
			else if(box.type.equals(Box.HDLR)) {
				final Handler_V0 hdlr = pc.create(box);
				pc.handler().message("HDLR " + hdlr);
			}
			else if(box.type.equals(Box.MVHD)) {
				final MovieHeader tr = pc.create(box);
				pc.handler().message("MVHD " + tr);
			}
			else if(Box.META.equals(box.type) && "/moov/udta/meta".equals(box.path())) {
				final MetadataHandler ss = new MetadataHandler();
				ss.start();
				pc.parseBox(box, ss);
				ss.end(pc);
				meta = ss.render();
				pc.handler().message(String.format("ILST %d boxes %s", meta.map.size(), meta.map));
			}
			else if(Box.hasBoxes(box)) {
				// decompose this box
				pc.parseBox(box, this);
			}
		}
		public void start() {
			gotstart = true;
		}
		public void end(ParseContext pc) {
			gotend = true;
		}
	}
	@Test
	public void boxTreeDump() throws Exception {
		final File fx = new File(PATH + "AdventuresOfTomSawyer-32kb-Part1_librivox.m4b");
		//final File fx = new File("c:/users/johng/downloads/AdventuresOfTomSawyer-32kb-Part1_librivox-pull.m4b");
		assertTrue("exists failed", fx.exists());
		final ParseCallback tx = new BoxTreeDump();
		ISOParser.parse(fx, new ConsoleHandler(), tx);
	}
	@Test
	public void accessTextTrackSamples() throws Exception {
		final File fx = new File(PATH + "AdventuresOfTomSawyer-32kb-Part1_librivox.m4b");
		assertTrue("exists failed", fx.exists());
		final IsoContainerHandler tx = new IsoContainerHandler();
		ISOParser.parse(fx, new NullHandler(), tx);
		final IsoMediaContainer imc = tx.render();
		assertNotNull("render failed", imc);
		assertNotNull("ftyp failed", imc.ftyp);
		assertNotNull("mvhd failed", imc.mvhd);
		assertNotNull("tracks failed", imc.tracks);
		assertTrue("tracks.length failed", imc.tracks.length == 2);
		final Track tk = imc.tracks[1];
		assertEquals("tracks[1].mtype failed", "text", tk.mhdlr.type);
		assertNull("tracks[1].dtype failed", tk.dhdlr);
		assertNotNull("tracks[1].mdhd failed", tk.mdhd);
		final SoundTable stbl2 = tk.stbl;
		//final DataRef dref = tk.dref;
		final RandomAccessFile raf = new RandomAccessFile(fx, "r");
		try {
			// IST media description "text" is scaled by the timescale
			int startsat = 0;
			final int ect = stbl2.stts.table.length;
			for(int ix = 1; ix <= ect; ix++) {
				final SoundTable.MediaCoordinate mc = stbl2.resolve(ix);
				//final DataRef.Entry drefe = dref.table[mc.sampleDesc - 1];
				final byte[] buf = new byte[mc.length];
				raf.seek(mc.position);
				raf.readFully(buf);
				final int leng = Unmarshal.getInt16(buf, 0);
				final String text = new String(buf, 2, leng);
				final long ttx = tk.mdhd.toMilliSeconds(startsat);
				System.out.println(String.format("leng %d text '%s' start %d %s", leng, text, startsat, ISOParser.formatDuration(ttx)));
				startsat += stbl2.stts.table[ix - 1].duration;
			}
		}
		finally {
			raf.close();
		}
	}
	@Test
	public void accessTextTrackSamplesAuphonic() throws Exception {
		final File fx = new File(PATH + "auphonic_chapters_demo.m4a");
		assertTrue("exists failed", fx.exists());
		final IsoContainerHandler tx = new IsoContainerHandler();
		ISOParser.parse(fx, new NullHandler(), tx);
		final IsoMediaContainer imc = tx.render();
		assertNotNull("render failed", imc);
		assertNotNull("ftyp failed", imc.ftyp);
		assertNotNull("mvhd failed", imc.mvhd);
		assertNotNull("tracks failed", imc.tracks);
		//assertTrue("tracks.length failed", imc.tracks.length == 3);
		final Track tk = imc.tracks[1];
		assertEquals("tracks[1].mtype failed", "text", tk.mhdlr.type);
		//assertEquals("tracks[1].dtype failed", "alis", imc.tracks[1].dhdlr.type);
		assertNotNull("tracks[1].mdhd failed", tk.mdhd);
		final SoundTable stbl1 = imc.tracks[1].stbl;
		//final DataRef dref = imc.tracks[1].dref;
		final RandomAccessFile raf = new RandomAccessFile(fx, "r");
		try {
			int startsat = 0;
			final int ect = stbl1.stts.table.length;
			for(int ix = 1; ix <= ect; ix++) {
				final SoundTable.MediaCoordinate mc = stbl1.resolve(ix);
				//final DataRef.Entry drefe = dref.table[mc.sampleDesc - 1];
				final byte[] buf = new byte[mc.length];
				raf.seek(mc.position);
				raf.readFully(buf);
				final int leng = Unmarshal.getInt16(buf, 0);
				final String text = new String(buf, 2, leng);
				final long ttx = tk.mdhd.toMilliSeconds(startsat);
				System.out.println(String.format("leng %d text '%s' start %d %s", leng, text, startsat, ISOParser.formatDuration(ttx, true)));
				startsat += stbl1.stts.table[ix - 1].duration;
			}
		}
		finally {
			raf.close();
		}
	}
	@Test
	public void chunkForSample() throws Exception {
		final File fx = new File(PATH + "AdventuresOfTomSawyer-32kb-Part1_librivox.m4b");
		assertTrue("exists failed", fx.exists());
		final IsoContainerHandler tx = new IsoContainerHandler();
		ISOParser.parse(fx, new NullHandler(), tx);
		final IsoMediaContainer imc = tx.render();
		assertNotNull("render failed", imc);
		assertNotNull("ftyp failed", imc.ftyp);
		assertNotNull("mvhd failed", imc.mvhd);
		assertNotNull("tracks failed", imc.tracks);
		assertTrue("tracks.length failed", imc.tracks.length == 2);
		assertEquals("tracks[1].mtype failed", "text", imc.tracks[1].mhdlr.type);
		assertNull("tracks[1].dtype failed", imc.tracks[1].dhdlr);
		final SoundTable stbl0 = imc.tracks[0].stbl;
		SoundTable.SampleCoordinate chunk0 = stbl0.chunkForSample(1);
		System.out.println(chunk0);
		assertEquals("tracks[0].chunkForSample(1) failed", 1, chunk0.chunk);
		SoundTable.MediaCoordinate media0 = stbl0.resolve(chunk0);
		System.out.println(media0);
		chunk0 = stbl0.chunkForSample(2);
		System.out.println(chunk0);
		assertEquals("tracks[0].chunkForSample(2) failed", 1, chunk0.chunk);
		media0 = stbl0.resolve(chunk0);
		System.out.println(media0);
		chunk0 = stbl0.chunkForSample(11);
		System.out.println(chunk0);
		assertEquals("tracks[0].chunkForSample(11) failed", 1, chunk0.chunk);
		media0 = stbl0.resolve(chunk0);
		System.out.println(media0);
		chunk0 = stbl0.chunkForSample(21);
		System.out.println(chunk0);
		assertEquals("tracks[0].chunkForSample(21) failed", 2, chunk0.chunk);
		media0 = stbl0.resolve(chunk0);
		System.out.println(media0);
		chunk0 = stbl0.chunkForSample(22);
		System.out.println(chunk0);
		assertEquals("tracks[0].chunkForSample(22) failed", 2, chunk0.chunk);
		media0 = stbl0.resolve(chunk0);
		System.out.println(media0);
		chunk0 = stbl0.chunkForSample(23);
		System.out.println(chunk0);
		assertEquals("tracks[0].chunkForSample(23) failed", 2, chunk0.chunk);
		media0 = stbl0.resolve(chunk0);
		System.out.println(media0);
		chunk0 = stbl0.chunkForSample(24);
		System.out.println(chunk0);
		assertEquals("tracks[0].chunkForSample(24) failed", 2, chunk0.chunk);
		media0 = stbl0.resolve(chunk0);
		System.out.println(media0);
		chunk0 = stbl0.chunkForSample(25);
		System.out.println(chunk0);
		assertEquals("tracks[0].chunkForSample(25) failed", 2, chunk0.chunk);
		media0 = stbl0.resolve(chunk0);
		System.out.println(media0);
		chunk0 = stbl0.chunkForSample(26);
		System.out.println(chunk0);
		assertEquals("tracks[0].chunkForSample(26) failed", 2, chunk0.chunk);
		media0 = stbl0.resolve(chunk0);
		System.out.println(media0);
		final SoundTable stbl2 = imc.tracks[1].stbl;
		SoundTable.SampleCoordinate chunk2 = stbl2.chunkForSample(1);
		System.out.println(chunk2);
		assertEquals("tracks[1].chunkForSample(1) failed", 1, chunk2.chunk);
		SoundTable.MediaCoordinate media2 = stbl2.resolve(chunk2);
		System.out.println(media2);
		chunk2 = stbl2.chunkForSample(2);
		System.out.println(chunk2);
		assertEquals("tracks[1].chunkForSample(2) failed", 2, chunk2.chunk);
		media2 = stbl2.resolve(chunk2);
		System.out.println(media2);
		chunk2 = stbl2.chunkForSample(3);
		System.out.println(chunk2);
		assertEquals("tracks[1].chunkForSample(3) failed", 3, chunk2.chunk);
		media2 = stbl2.resolve(chunk2);
		System.out.println(media2);
	}
	@Test
	public void flameTestM4B() throws Exception {
		final File fx = new File(PATH + "AdventuresOfTomSawyer-32kb-Part1_librivox.m4b");
		assertTrue("exists failed", fx.exists());
		final Tester tx = new Tester();
		ISOParser.parse(fx, new ConsoleHandler(), tx);
		assertTrue("cb.start failed", tx.gotstart);
		assertTrue("cb.box failed", tx.counter > 0);
		assertTrue("cb.end failed", tx.gotend);
		assertNotNull("FileType failed", tx.ft);
		assertNotNull("Metadata failed", tx.meta);
		assertTrue("meta.size failed", tx.meta.map.size() > 0);
	}
	@Test
	public void flameTestM4B2() throws Exception {
		//final File fx = new File("c:/users/johng/downloads/AdventuresOfTomSawyer-32kb-Part1_librivox.m4b");
		final File fx = new File("c:/users/johng/downloads/AdventuresOfTomSawyer-32kb-Part1_librivox-pull.m4b");
		assertTrue("exists failed", fx.exists());
		final Tester tx = new Tester();
		ISOParser.parse(fx, new ConsoleHandler(), tx);
		assertTrue("cb.start failed", tx.gotstart);
		assertTrue("cb.box failed", tx.counter > 0);
		assertTrue("cb.end failed", tx.gotend);
		assertNotNull("FileType failed", tx.ft);
		assertNotNull("Metadata failed", tx.meta);
		assertTrue("meta.size failed", tx.meta.map.size() > 0);
	}
	@Test
	public void flameTestAuphonic() throws Exception {
		final File fx = new File(PATH + "auphonic_chapters_demo.m4a");
		assertTrue("exists failed", fx.exists());
		final Tester tx = new Tester();
		ISOParser.parse(fx, new ConsoleHandler(), tx);
		assertTrue("cb.start failed", tx.gotstart);
		assertTrue("cb.box failed", tx.counter > 0);
		assertTrue("cb.end failed", tx.gotend);
		assertNotNull("FileType failed", tx.ft);
		assertNotNull("Metadata failed", tx.meta);
		assertTrue("meta.size failed", tx.meta.map.size() > 0);
	}
	@Test
	public void flameTestVideo2() throws Exception {
		final File fx = new File("c:/users/johng/videos/Indulgence.mp4");
		assertTrue("exists failed", fx.exists());
		final Tester tx = new Tester();
		ISOParser.parse(fx, new ConsoleHandler(), tx);
		assertTrue("cb.start failed", tx.gotstart);
		assertTrue("cb.box failed", tx.counter > 0);
		assertTrue("cb.end failed", tx.gotend);
		assertNotNull("FileType failed", tx.ft);
		assertNotNull("Metadata failed", tx.meta);
		assertTrue("meta.size failed", tx.meta.map.size() > 0);
	}
}