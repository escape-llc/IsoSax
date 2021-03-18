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

public class EditList_V0 {
	public static class Entry {
		public final int trackDuration;
		public final int mediaTime;
		public final int mediaRate;
		public Entry(int trackDuration, int mediaTime, int mediaRate) {
			this.trackDuration = trackDuration;
			this.mediaTime = mediaTime;
			this.mediaRate = mediaRate;
		}
	}
	public final int flags;
	public final Entry[] table;
	public EditList_V0(int flags, Entry[] table) {
		this.flags = flags;
		this.table = table;
	}
	@Override public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(table.length);
		sb.append("] ");
		for(int ix = 0; ix < table.length; ix++) {
			if(ix > 0) sb.append(", ");
			sb.append("{dur=");
			sb.append(table[ix].trackDuration);
			sb.append(",mt=");
			sb.append(table[ix].mediaTime);
			sb.append(",rate=");
			sb.append(Integer.toString(table[ix].mediaRate, 16));
			sb.append("}");
		}
		return sb.toString();
	}
}
