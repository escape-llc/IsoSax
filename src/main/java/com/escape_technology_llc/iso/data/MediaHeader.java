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

public class MediaHeader {
	public static class V0 extends MediaHeader {
		// sec since epoch
		public final int creation;
		// sec since epoch
		public final int modification;
		// in units of timescale
		public final int duration;
		public V0(int flags, int creation, int modification, int timescale, int duration, int language) {
			super(flags, timescale, language);
			this.creation = creation;
			this.modification = modification;
			this.duration = duration;
		}
	}
	public static class V1 extends MediaHeader {
		// sec since epoch
		public final long creation;
		// sec since epoch
		public final long modification;
		// in units of timescale
		public final long duration;
		public V1(int flags, long creation, long modification, int timescale, long duration, int language) {
			super(flags, timescale, language);
			this.creation = creation;
			this.modification = modification;
			this.duration = duration;
		}
	}
	public final int flags;
	// units/sec
	public final int timescale;
	// 3x5bit ISO-639-2/T language code in lower 16 bits
	public final int language;
	protected MediaHeader(int flags, int timescale, int language) {
		this.flags = flags;
		this.timescale = timescale;
		this.language = language;
	}
	/**
	 * Convert the given media time to milliseconds.
	 * @param time Media time.
	 * @return Number of milliseconds.
	 */
	public long toMilliSeconds(long time) {
		return (time*1000L)/(long)timescale;
	}
	@Override public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("ts=");
		sb.append(timescale);
		sb.append(",lang=");
		sb.append(Integer.toString(language, 16));
		return sb.toString();
	}
}
