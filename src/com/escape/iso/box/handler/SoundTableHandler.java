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

import com.escape.iso.ParseCallback;
import com.escape.iso.ParseContext;
import com.escape.iso.box.Box;
import com.escape.iso.data.ChunkOffset;
import com.escape.iso.data.SampleDescription;
import com.escape.iso.data.SampleSize;
import com.escape.iso.data.SampleToChunk;
import com.escape.iso.data.SoundTable;
import com.escape.iso.data.SyncSample;
import com.escape.iso.data.TimeToSample;

public class SoundTableHandler implements ParseCallback, RenderInstance<SoundTable> {
	SampleDescription stsd;
	SampleToChunk stsc;
	TimeToSample stts;
	SyncSample stss;
	SampleSize stsz;
	ChunkOffset stco;
	/**
	 * Produce the domain object for this handler.
	 * @return
	 */
	public SoundTable render() {
		return new SoundTable(stsd, stsc, stts, stss, stsz, stco);
	}
	public void start() {
	}
	public void box(ParseContext pc, Box box) throws Exception {
		if(box.type.equals(Box.STSC)) {
			stsc = pc.create(box);
		}
		else if(box.type.equals(Box.STSD)) {
			stsd = pc.create(box);
		}
		else if(box.type.equals(Box.STSS)) {
			stss = pc.create(box);
		}
		else if(box.type.equals(Box.STTS)) {
			stts = pc.create(box);
		}
		else if(box.type.equals(Box.STSZ)) {
			stsz = pc.create(box);
		}
		else if(box.type.equals(Box.STCO)) {
			stco = pc.create(box);
		}
		else {
			pc.handler().warning(new IllegalArgumentException("stbl unhandled box: " + box.path()));
		}
	}
	public void end(ParseContext pc) throws Exception {
	}
}
