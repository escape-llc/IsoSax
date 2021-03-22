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
package com.escape_technology_llc.iso.box.handler;

import com.escape_technology_llc.iso.ParseCallback;
import com.escape_technology_llc.iso.ParseContext;
import com.escape_technology_llc.iso.box.Box;
import com.escape_technology_llc.iso.data.DataRef;
import com.escape_technology_llc.iso.data.EditList_V0;
import com.escape_technology_llc.iso.data.Handler_V0;
import com.escape_technology_llc.iso.data.MediaHeader;
import com.escape_technology_llc.iso.data.SoundTable;
import com.escape_technology_llc.iso.data.Track;
import com.escape_technology_llc.iso.data.TrackHeader;
import com.escape_technology_llc.iso.data.TrackRef;

public class TrackHandler implements ParseCallback, RenderInstance<Track> {
	/**
	 * Ability to filter for tracks.
	 * @author escape-llc
	 *
	 */
	public interface Selector {
		/**
		 * Return whether to select this track.
		 * @param mhdlr media handler; may be NULL.
		 * @param dhdlr data handler; may be NULL.
		 * @return true: select; false: ignore.
		 */
		boolean stbl(Handler_V0 mhdlr, Handler_V0 dhdlr);
	}
	TrackHeader tkhd;
	MediaHeader mdhd;
	Handler_V0 mhdlr;
	Handler_V0 dhdlr;
	EditList_V0 elst;
	TrackRef tref;
	DataRef dref;
	SoundTable stbl;
	final Selector ps;
	/**
	 * Ctor.
	 * No stbl predicate; collects everything.
	 */
	public TrackHandler() {
		ps = null;
	}
	/**
	 * Ctor.
	 * @param ps !NULL: sample table predicate.
	 */
	public TrackHandler(Selector ps) {
		this.ps = ps;
	}
	public Track render() {
		return new Track(tkhd, mdhd, mhdlr, dhdlr, elst, tref, dref, stbl);
	}
	public void start() {
	}
	public void box(ParseContext pc, Box box) throws Exception {
		pc.handler().message(String.format("\t%d %s %s", box.level(), box.path(), box));
		if(box.type.equals(Box.TKHD)) {
			tkhd = pc.create(box);
		}
		else if(box.type.equals(Box.HDLR)) {
			if(box.parent.type.equals(Box.MDIA)) {
				mhdlr = pc.create(box);
			}
			else if(box.parent.type.equals(Box.MINF)) {
				dhdlr = pc.create(box);
			}
		}
		else if(box.type.equals(Box.TREF)) {
			tref = pc.create(box);
		}
		else if(box.type.equals(Box.DREF)) {
			dref = pc.create(box);
		}
		else if(box.type.equals(Box.ELST)) {
			elst = pc.create(box);
		}
		else if(box.type.equals(Box.MDHD)) {
			mdhd = pc.create(box);
		}
		else if(box.type.equals(Box.STBL) && (ps == null || ps.stbl(mhdlr, dhdlr))) {
			final SoundTableHandler ss = new SoundTableHandler();
			ss.start();
			pc.parseBox(box, ss);
			ss.end(pc);
			stbl = ss.render();
		}
		else if(box.type.equals(Box.FREE) || box.type.equals(Box.SKIP)) {
			// no action
		}
		else if(!Box.hasBoxes(box)) {
			pc.handler().warning(new IllegalArgumentException("trak unhandled box: " + box.path()));
		}
		else {
			// decompose this box
			pc.parseBox(box, this);
		}
	}
	public void end(ParseContext pc) throws Exception {
	}
}