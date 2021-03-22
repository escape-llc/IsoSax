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

public class TrackRef {
	public final String type;
	public final int[] trackIDs;
	public TrackRef(String type, int[] trackIDs) {
		this.type = type;
		this.trackIDs = trackIDs;
	}
	@Override public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(type);
		sb.append(" [");
		sb.append(trackIDs.length);
		sb.append("] ");
		for(int ix = 0; ix < trackIDs.length; ix++) {
			if(ix > 0) sb.append(",");
			sb.append(trackIDs[ix]);
		}
		return sb.toString();
	}
}
