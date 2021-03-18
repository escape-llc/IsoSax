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

public class TrackHeader {
	public static final int FLAGS_ENABLED = 1;
	public static final int FLAGS_INMOVIE = 2;
	public static final int FLAGS_INPREVIEW = 4;
	public static final int FLAGS_INPOSTER = 8;
	
	public static class V0 extends TrackHeader {
		public final int creation;
		public final int modification;
		public final int duration;
		public V0(int flags, int creation, int modification, int track, int duration) {
			super(flags, track);
			this.creation = creation;
			this.modification = modification;
			this.duration = duration;
		}
		@Override public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append("track: ");
			sb.append(trackId);
			sb.append(",duration: " );
			sb.append(duration);
			if(flags != 0) {
				sb.append(" ");
				if((flags & FLAGS_ENABLED) != 0) {
					sb.append("$enb");
				}
				if((flags & FLAGS_INMOVIE) != 0) {
					sb.append("$inmovie");
				}
				if((flags & FLAGS_INPREVIEW) != 0) {
					sb.append("$inpreview");
				}
				if((flags & FLAGS_INPOSTER) != 0) {
					sb.append("$inposter");
				}
			}
			return sb.toString();
		}
	}
	public static class V1 extends TrackHeader {
		public final long creation;
		public final long modification;
		public final long duration;
		public V1(int flags, long creation, long modification, int track, long duration) {
			super(flags, track);
			this.creation = creation;
			this.modification = modification;
			this.duration = duration;
		}
		@Override public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append("track: ");
			sb.append(trackId);
			sb.append(",duration: " );
			sb.append(duration);
			if(flags != 0) {
				sb.append(" ");
				if((flags & FLAGS_ENABLED) != 0) {
					sb.append("$enb");
				}
				if((flags & FLAGS_INMOVIE) != 0) {
					sb.append("$inmovie");
				}
				if((flags & FLAGS_INPREVIEW) != 0) {
					sb.append("$inpreview");
				}
				if((flags & FLAGS_INPOSTER) != 0) {
					sb.append("$inposter");
				}
			}
			return sb.toString();
		}
	}
	public final int flags;
	public final int trackId;
	protected TrackHeader(int flags, int track) {
		this.flags = flags;
		this.trackId = track;
	}
}
