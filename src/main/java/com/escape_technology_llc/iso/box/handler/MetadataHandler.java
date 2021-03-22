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

import java.util.ArrayList;
import java.util.HashMap;

import com.escape_technology_llc.iso.ParseCallback;
import com.escape_technology_llc.iso.ParseContext;
import com.escape_technology_llc.iso.box.Box;
import com.escape_technology_llc.iso.data.Handler_V0;
import com.escape_technology_llc.iso.data.Meta;
import com.escape_technology_llc.iso.data.MetadataValue;

public class MetadataHandler implements ParseCallback, RenderInstance<Meta> {
	Handler_V0 hdlr;
	HashMap<String, ArrayList<MetadataValue>> meta = new HashMap<String, ArrayList<MetadataValue>>();
	public Meta render() {
		return new Meta(hdlr, meta);
	}
	public void start() {
	}
	void put(String type, MetadataValue mx) {
		if(meta.containsKey(type)) {
			final ArrayList<MetadataValue> mvs = meta.get(type);
			mvs.add(mx);
		}
		else {
			final ArrayList<MetadataValue> mvs = new ArrayList<MetadataValue>();
			mvs.add(mx);
			meta.put(type, mvs);
		}
	}
	public void box(ParseContext pc, Box box) throws Exception {
		//pc.handler().message(String.format("\t%d %s %s", box.level(), box.path(), box));
		if(Box.HDLR.equals(box.type)) {
			hdlr = pc.create(box);
			if(!"mdir".equals(hdlr.type)) {
				pc.handler().error(new IllegalStateException("Unknown handler type: " + hdlr.type));
			}
		}
		else if(Box.ILST.equals(box.type)) {
			if("mdir".equals(hdlr.type)) {
				pc.parseBox(box, this);
			}
			else {
				pc.handler().warning(new IllegalStateException("Skipping data box, unknown handler type: " + hdlr.type));
			}
		}
		else {
			final MetadataValue mx = pc.create(box);
			if(mx != null) {
				put(mx.type, mx);
			}
			else {
				// saw it but couldn't parse
				put(box.type, new MetadataValue.Unknown(-1, -1, box));
			}
		}
	}
	public void end(ParseContext pc) throws Exception {
	}
}
