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

import com.escape_technology_llc.iso.ISOParser;

public class MovieHeader {
	public static class V0 extends MovieHeader {
		// sec since epoch
		public final int creation;
		// sec since epoch
		public final int modification;
		// in units of timescale
		public final int duration;
		public V0(int flags, int creation, int modification, int timescale, int duration, int rate, int volume, int nextTrackId) {
			super(flags, timescale, rate, volume, nextTrackId);
			this.creation = creation;
			this.modification = modification;
			this.duration = duration;
		}
	}
	public static class V1 extends MovieHeader {
		// sec since epoch
		public final long creation;
		// sec since epoch
		public final long modification;
		// in units of timescale
		public final long duration;
		public V1(int flags, long creation, long modification, int timescale, long duration, int rate, int volume, int nextTrackId) {
			super(flags, timescale, rate, volume, nextTrackId);
			this.creation = creation;
			this.modification = modification;
			this.duration = duration;
		}
	}
	public final int flags;
	// units/sec
	public final int timescale;
	// 16.16 fixpoint
	public final int rate;
	// 8.8 fixpoint
	public final int volume;
	public final int nextTrackId;
	protected MovieHeader(int flags, int timescale, int rate, int volume, int nextTrackId) {
		this.flags = flags;
		this.timescale = timescale;
		this.rate = rate;
		this.volume = volume;
		this.nextTrackId = nextTrackId;
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
		sb.append(",rate=");
		sb.append(Integer.toString(rate, 16));
		sb.append("(");
		sb.append(ISOParser.toFloat_1616(rate));
		sb.append(")");
		sb.append(",vol=");
		sb.append(Integer.toString(volume, 16));
		return sb.toString();
	}
}
