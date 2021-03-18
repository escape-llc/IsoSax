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
package com.escape.iso.data;

public class Track {
	public final TrackHeader tkhd;
	public final MediaHeader mdhd;
	public final Handler_V0 mhdlr;
	public final Handler_V0 dhdlr;
	public final EditList_V0 elst;
	public final TrackRef tref;
	public final DataRef dref;
	public final SoundTable stbl;
	public Track(TrackHeader thdr, MediaHeader mdhd, Handler_V0 mhdlr, Handler_V0 dhdlr, EditList_V0 elst, TrackRef tref, DataRef dref, SoundTable stbl) {
		this.tkhd = thdr;
		this.mdhd = mdhd;
		this.mhdlr = mhdlr;
		this.dhdlr = dhdlr;
		this.elst = elst;
		this.tref = tref;
		this.dref = dref;
		this.stbl = stbl;
	}
	@Override public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("TKHD ");
		sb.append(tkhd);
		sb.append("\nMDHD ");
		sb.append(mdhd);
		sb.append("\nHDLR.media ");
		sb.append(mhdlr);
		sb.append("\nHDLR.data ");
		sb.append(dhdlr);
		sb.append("\nTREF ");
		sb.append(tref);
		sb.append("\nDREF ");
		sb.append(dref);
		sb.append("\nELST ");
		sb.append(elst);
		sb.append("\nSTBL\n");
		sb.append(stbl);
		return sb.toString();
	}
}
