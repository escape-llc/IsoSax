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
package com.escape_technology_llc.iso.data;

public class DataRef {
	public static final int FLAG_SELFREFERENCE = 1;
	public static class Entry {
		public final int flags;
		public final String location;
		public final String value;
		public Entry(int flags, String type, String value) {
			this.flags = flags;
			this.location = type;
			this.value = value;
		}
		@Override public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append(location.trim());
			if((flags & FLAG_SELFREFERENCE) == 0) {
				sb.append("[");
				sb.append(value == null ? "null" : value);
				sb.append("]");
			}
			if(flags != 0) {
				sb.append(" ");
				if((flags & FLAG_SELFREFERENCE) != 0) {
					sb.append("$self");
				}
				else {
					sb.append("$");
					sb.append(Integer.toString(flags, 16));
				}
			}
			return sb.toString();
		}
	}
	public final int flags;
	public final Entry[] table;
	public DataRef(int flags, Entry[] entries) {
		this.flags = flags;
		this.table = entries;
	}
	@Override public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(table.length);
		sb.append("]");
		if(table.length > 0) sb.append(" ");
		for(int ix = 0; ix < table.length; ix++) {
			if(ix > 0) sb.append(",");
			sb.append("{");
			sb.append(table[ix]);
			sb.append("}");
		}
		if(flags != 0) {
			sb.append(" ");
			if((flags & FLAG_SELFREFERENCE) != 0) {
				sb.append("$self");
			}
		}
		return sb.toString();
	}
}
