package com.escape.mp4.tests;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.escape.iso.ISOParser;
import com.escape.iso.NullHandler;
import com.escape.iso.box.handler.IsoContainerHandler;
import com.escape.iso.box.handler.TrackHandler;
import com.escape.iso.data.Handler_V0;
import com.escape.iso.data.IsoMediaContainer;

public class HandlerTests {
	@Test
	public void isoContainerHandler_Default() throws Exception {
		final File fx = new File("c:/users/johng/downloads/AdventuresOfTomSawyer-32kb-Part1_librivox.m4b");
		assertTrue("exists failed", fx.exists());
		final IsoContainerHandler tx = new IsoContainerHandler();
		ISOParser.parse(fx, new NullHandler(), tx);
		final IsoMediaContainer imc = tx.render();
		assertNotNull("render failed", imc);
		assertNotNull("ftyp failed", imc.ftyp);
		assertNotNull("mvhd failed", imc.mvhd);
		assertNotNull("tracks failed", imc.tracks);
		assertNotNull("meta failed", imc.meta);
		assertNotNull("meta.hdlr failed", imc.meta.hdlr);
		assertTrue("meta.hdlr.type failed", imc.meta.hdlr.type.equals("mdir"));
		assertTrue("meta.size failed", imc.meta.map.size() > 0);
		assertTrue("tracks.length failed", imc.tracks.length == 2);
		if(imc.tracks.length > 0) {
			assertEquals("tracks[0].mtype failed", "soun", imc.tracks[0].mhdlr.type);
			assertEquals("tracks[0].dtype failed", null, imc.tracks[0].dhdlr);
		}
		if(imc.tracks.length > 1) {
			assertEquals("tracks[1].mtype failed", "text", imc.tracks[1].mhdlr.type);
			assertNull("tracks[1].dtype failed", imc.tracks[1].dhdlr);
		}
	}
	@Test
	public void isoContainerHandler_Meta() throws Exception {
		final File fx = new File("c:/users/johng/downloads/AdventuresOfTomSawyer-32kb-Part1_librivox.m4b");
		assertTrue("exists failed", fx.exists());
		final IsoContainerHandler tx = new IsoContainerHandler(false, true, null);
		ISOParser.parse(fx, new NullHandler(), tx);
		final IsoMediaContainer imc = tx.render();
		assertNotNull("render failed", imc);
		assertNotNull("ftyp failed", imc.ftyp);
		assertNotNull("mvhd failed", imc.mvhd);
		assertNotNull("tracks failed", imc.tracks);
		assertNotNull("meta failed", imc.meta);
		assertNotNull("meta.hdlr failed", imc.meta.hdlr);
		assertTrue("meta.hdlr.type failed", imc.meta.hdlr.type.equals("mdir"));
		assertTrue("meta.size failed", imc.meta.map.size() > 0);
		assertTrue("tracks.length failed", imc.tracks.length == 0);
	}
	@Test
	public void isoContainerHandler_Tracks() throws Exception {
		final File fx = new File("c:/users/johng/downloads/AdventuresOfTomSawyer-32kb-Part1_librivox.m4b");
		assertTrue("exists failed", fx.exists());
		final IsoContainerHandler tx = new IsoContainerHandler(true, false, null);
		ISOParser.parse(fx, new NullHandler(), tx);
		final IsoMediaContainer imc = tx.render();
		assertNotNull("render failed", imc);
		assertNotNull("ftyp failed", imc.ftyp);
		assertNotNull("mvhd failed", imc.mvhd);
		assertNotNull("tracks failed", imc.tracks);
		assertNull("meta failed", imc.meta);
		assertTrue("tracks.length failed", imc.tracks.length == 2);
		if(imc.tracks.length > 0) {
			assertEquals("tracks[0].mtype failed", "soun", imc.tracks[0].mhdlr.type);
			assertEquals("tracks[0].dtype failed", null, imc.tracks[0].dhdlr);
			assertNotNull("tracks[0].stbl failed", imc.tracks[0].stbl);
		}
		if(imc.tracks.length > 1) {
			assertEquals("tracks[1].mtype failed", "text", imc.tracks[1].mhdlr.type);
			assertNull("tracks[1].dtype failed", imc.tracks[1].dhdlr);
			assertNotNull("tracks[1].stbl failed", imc.tracks[1].stbl);
		}
	}
	@Test
	public void isoContainerHandler_Tracks_Selector() throws Exception {
		final File fx = new File("c:/users/johng/downloads/AdventuresOfTomSawyer-32kb-Part1_librivox.m4b");
		assertTrue("exists failed", fx.exists());
		// only materialize text tracks
		final TrackHandler.Selector text = new TrackHandler.Selector() {
			public boolean stbl(Handler_V0 mhdlr, Handler_V0 dhdlr) {
				return mhdlr != null && "text".equals(mhdlr.type);
			}
		};
		final IsoContainerHandler tx = new IsoContainerHandler(true, false, text);
		ISOParser.parse(fx, new NullHandler(), tx);
		final IsoMediaContainer imc = tx.render();
		assertNotNull("render failed", imc);
		assertNotNull("ftyp failed", imc.ftyp);
		assertNotNull("mvhd failed", imc.mvhd);
		assertNotNull("tracks failed", imc.tracks);
		assertNull("meta failed", imc.meta);
		assertTrue("tracks.length failed", imc.tracks.length == 2);
		if(imc.tracks.length > 0) {
			assertEquals("tracks[0].mtype failed", "soun", imc.tracks[0].mhdlr.type);
			assertEquals("tracks[0].dtype failed", null, imc.tracks[0].dhdlr);
			assertNull("tracks[0].stbl failed", imc.tracks[0].stbl);
		}
		if(imc.tracks.length > 1) {
			assertEquals("tracks[1].mtype failed", "text", imc.tracks[1].mhdlr.type);
			assertNull("tracks[1].dtype failed", imc.tracks[1].dhdlr);
			assertNotNull("tracks[1].stbl failed", imc.tracks[1].stbl);
		}
	}
}
