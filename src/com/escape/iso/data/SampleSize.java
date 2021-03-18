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

public class SampleSize {
	public final int sampleSize;
	// if table == null use sampleSize instead
	public final int[] table;
	public SampleSize(int ss, int[] entries) {
		this.sampleSize = ss;
		if(ss == 0 && entries == null)
			throw new IllegalArgumentException("ss == 0 requires the ss table");
		this.table = entries;
	}
	/**
	 * Return either the value from TABLE or SampleSize if TABLE is NULL.
	 * @param idx Requested index.
	 * @return sample size for index.
	 */
	public int get(int idx) {
		return table == null ? sampleSize : table[idx];
	}
	@Override public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("[");
		if(table != null) {
			sb.append(table.length);
		}
		else {
			sb.append(sampleSize);
		}
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
