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

public class ChunkOffset {
	public final int[] table;
	public ChunkOffset(int[] entries) {
		this.table = entries;
	}
	@Override public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(table.length);
		sb.append("] ");
		if(table != null) {
			for(int ix = 0; ix < table.length; ix++) {
				if(ix > 0) sb.append(", ");
				sb.append(table[ix]);
				if(table.length > 10 && ix == 10) {
					sb.append("...");
					break;
				}
			}
		}
		return sb.toString();
	}
}
