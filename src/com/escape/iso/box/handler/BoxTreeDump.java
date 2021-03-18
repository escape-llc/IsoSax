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

/**
 * Handler that simply dumps the box hierarchy to the parse context exception-handler.
 * @author escape-llc
 *
 */
public class BoxTreeDump implements ParseCallback {
	public void box(ParseContext pc, Box box) throws Exception {
		pc.handler().message(String.format("%d %s %s", box.level(), box.path(), box));
		if(Box.hasBoxes(box)) {
			// decompose this box
			pc.parseBox(box, this);
		}
	}
	public void start() {
	}
	public void end(ParseContext pc) {
	}
}
