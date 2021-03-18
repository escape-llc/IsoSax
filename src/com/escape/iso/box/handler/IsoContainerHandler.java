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
package com.escape.iso.box.handler;

import java.util.ArrayList;

import com.escape.iso.ParseCallback;
import com.escape.iso.ParseContext;
import com.escape.iso.box.Box;
import com.escape.iso.box.handler.TrackHandler.Selector;
import com.escape.iso.data.FileType;
import com.escape.iso.data.IsoMediaContainer;
import com.escape.iso.data.Meta;
import com.escape.iso.data.MovieHeader;
import com.escape.iso.data.Track;

/**
 * General-purpose handler that collects FTYP, MVHD, META (optional), and TRAKs (optional/filtered).
 * @author escape-llc
 *
 */
public class IsoContainerHandler implements ParseCallback, RenderInstance<IsoMediaContainer> {
	FileType ftyp;
	MovieHeader mvhd;
	ArrayList<Track> traks = new ArrayList<Track>();
	Meta meta;
	static final int IDX_TRAK = 0;
	static final int IDX_META = 1;
	final boolean flags[] = { true, true };
	final Selector ps;
	/**
	 * Default ctor.
	 * All collection flags are TRUE.
	 * All track data is collected.
	 */
	public IsoContainerHandler() {
		this.ps = null;
	}
	/**
	 * Default ctor.
	 * All collection flags are TRUE.
	 * Use given track selector.
	 */
	public IsoContainerHandler(Selector ps) {
		this.ps = ps;
	}
	/**
	 * Ctor.
	 * Set collection flags.
	 * @param trak true: collect tracks.
	 * @param meta true: collect top-level metadata.
	 */
	public IsoContainerHandler(boolean trak, boolean meta, Selector ps) {
		flags[IDX_TRAK] = trak;
		flags[IDX_META] = meta;
		this.ps = ps;
	}
	public IsoMediaContainer render() {
		return new IsoMediaContainer(ftyp, mvhd, traks.toArray(new Track[traks.size()]), meta);
	}
	public void start() {
	}
	public void box(ParseContext pc, Box box) throws Exception {
		pc.handler().message(String.format("%d %s %s", box.level(), box.path(), box));
		if(box.type.equals(Box.FTYP)) {
			// extract the data
			ftyp = pc.create(box);
			pc.handler().message("FTYP " + ftyp);
		}
		else if(box.type.equals(Box.MVHD)) {
			mvhd = pc.create(box);
			pc.handler().message("MVHD " + mvhd);
		}
		else if(box.type.equals(Box.TRAK) && flags[IDX_TRAK]) {
			final TrackHandler tkh = new TrackHandler(ps);
			tkh.start();
			pc.parseBox(box, tkh);
			tkh.end(pc);
			final Track trak = tkh.render();
			traks.add(trak);
			pc.handler().message("TRAK\n" + trak);
		}
		else if(Box.META.equals(box.type) && "/moov/udta/meta".equals(box.path()) && flags[IDX_META]) {
			final MetadataHandler ss = new MetadataHandler();
			ss.start();
			pc.parseBox(box, ss);
			ss.end(pc);
			meta = ss.render();
			pc.handler().message(String.format("META %d boxes %s", meta.map.size(), meta));
		}
		else if(box.type.equals(Box.FREE) || box.type.equals(Box.SKIP) || box.type.equals(Box.WIDE) || box.type.equals(Box.MDAT)) {
			// no action
		}
		else if(!Box.hasBoxes(box)) {
			pc.handler().warning(new IllegalArgumentException("ISO unhandled box: " + box.path()));
		}
		else {
			// decompose this box
			pc.parseBox(box, this);
		}
	}
	public void end(ParseContext pc) throws Exception {
	}
}
