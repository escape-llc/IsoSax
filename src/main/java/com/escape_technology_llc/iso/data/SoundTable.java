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

import com.escape_technology_llc.iso.box.Box;

public class SoundTable {
	/**
	 * Information needed to resolve a sample into its chunk location.
	 * @author escape-llc
	 *
	 */
	public static class SampleCoordinate {
		// chunk number (1-relative)
		public final int chunk;
		// first sample of this chunk (1-relative)
		public final int firstSampleInChunk;
		// target sample (1-relative)
		public final int sample;
		// sample description (1-relative)
		public final int sampleDesc;
		public SampleCoordinate(int chunk, int firstSampleInChunk, int sample, int sampleDesc) {
			if(chunk == 0)
				throw new IllegalArgumentException("chunk");
			if(firstSampleInChunk == 0)
				throw new IllegalArgumentException("firstSampleInChunk");
			if(sample == 0)
				throw new IllegalArgumentException("sample");
			if(firstSampleInChunk > sample)
				throw new IllegalArgumentException("firstSampleInChunk GT sample");
			if(sampleDesc == 0)
				throw new IllegalArgumentException("sampleDesc");
			this.chunk = chunk;
			this.firstSampleInChunk = firstSampleInChunk;
			this.sample = sample;
			this.sampleDesc = sampleDesc;
		}
		@Override public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append("c=");
			sb.append(chunk);
			sb.append(",f=");
			sb.append(firstSampleInChunk);
			sb.append(",s=");
			sb.append(sample);
			sb.append(",sd=");
			sb.append(sampleDesc);
			return sb.toString();
		}
	}
	/**
	 * Information needed to locate bytes within a data file.
	 * @author escape-llc
	 *
	 */
	public static class MediaCoordinate {
		// offset from beginning of file
		public final long position;
		// number of bytes
		public final int length;
		// sample description (1-relative)
		public final int sampleDesc;
		public MediaCoordinate(long position, int length, int sd) {
			this.position = position;
			this.length = length;
			this.sampleDesc = sd;
		}
		@Override public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append(sampleDesc);
			sb.append("@");
			final String px = Long.toString(position);
			sb.append(px);
			final long lastbyte = position + length - 1L;
			final String lbx = Long.toString(lastbyte);
			sb.append("-");
			sb.append(Box.shortestSuffix(px, lbx));
			sb.append("[");
			sb.append(length);
			sb.append("]");
			return sb.toString();
		}
	}
	public final SampleDescription stsd;
	public final SampleToChunk stsc;
	public final TimeToSample stts;
	public final SyncSample stss;
	public final SampleSize stsz;
	public final ChunkOffset stco;
	public SoundTable(SampleDescription stsd, SampleToChunk stsc, TimeToSample stts, SyncSample stss, SampleSize stsz, ChunkOffset stco) {
		this.stsd = stsd;
		this.stsc = stsc;
		this.stts = stts;
		this.stss = stss;
		this.stsz = stsz;
		this.stco = stco;
	}
	/**
	 * Scan the sample-to-chunk table for the chunk matching given sample.
	 * @param sample sample number (1-relative)
	 * @return Sample coordinate.
	 */
	public SampleCoordinate chunkForSample(int sample) {
		if(sample < 1)
			throw new IllegalArgumentException ("sample");
		if(stsc == null)
			throw new IllegalStateException("stsc");
		if(stsc.table == null || stsc.table.length == 0)
			throw new IllegalStateException("stsc.table");
		if(stsc.table.length == 1) {
			// all chunks are the same size; shortcut
			final int spc = stsc.table[0].samplesPerChunk;
			final int chunk = (sample - 1)/spc;
			return new SampleCoordinate(chunk + stsc.table[0].firstChunk, (chunk*spc) + 1, sample, stsc.table[0].sampleDescID);
		}
		if(sample == 1) {
			// another shortcut
			return new SampleCoordinate(stsc.table[0].firstChunk, 1, 1, stsc.table[0].sampleDescID);
		}
		int samp = 1;
		int cidx = 0;
		int chunk = stsc.table[cidx].firstChunk;
		int spc = stsc.table[cidx].samplesPerChunk;
		int sdid = stsc.table[cidx].sampleDescID;
		int chunk_bpt = stsc.table[cidx + 1].firstChunk;
		// look ahead to see if we can skip entire range of chunks
		int spe = (chunk_bpt - chunk)*spc;
		while(samp + spe < sample) {
			// advance to the next table entry
			samp += spe;
			chunk = chunk_bpt;
			if(cidx + 1 < stsc.table.length) {
				cidx++;
				spc = stsc.table[cidx].samplesPerChunk;
				sdid = stsc.table[cidx].sampleDescID;
				if(cidx + 1 < stsc.table.length) {
					chunk_bpt = stsc.table[cidx + 1].firstChunk;
					spe = (chunk_bpt - chunk)*spc;
				}
				else {
					// all chunks here on out are the current size
					break;
				}
			}
		}
		// at this point chunk == table[cidx].firstChunk && spc == table[cidx].samplesPerChunk
		final int achunks = (sample - samp)/spc;
		return new SampleCoordinate(chunk + achunks, samp + achunks*spc, sample, sdid);
	}
	/**
	 * Take the sample coordinate and compute the media coordinate.
	 * @param sc
	 * @return
	 */
	public MediaCoordinate resolve(SampleCoordinate sc) {
		if(stco == null)
			throw new IllegalArgumentException("stco");
		if(stsz == null)
			throw new IllegalArgumentException("stsz");
		int cofs = 0;
		for(int ix = sc.firstSampleInChunk; ix < sc.sample; ix++) {
			cofs += stsz.get(ix - 1);
		}
		return new MediaCoordinate(stco.table[sc.chunk - 1] + cofs, stsz.get(sc.sample - 1), sc.sampleDesc);
	}
	/**
	 * Convenience method to go from sample to media coordinate.
	 * @param sample sample (1-relative)
	 * @return
	 */
	public MediaCoordinate resolve(int sample) {
		final SampleCoordinate sc = chunkForSample(sample);
		return resolve(sc);
	}
	@Override public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("STSC ");
		sb.append(stsc);
		sb.append("\n");
		sb.append("STSD ");
		sb.append(stsd);
		sb.append("\n");
		sb.append("STSS ");
		sb.append(stss);
		sb.append("\n");
		sb.append("STTS ");
		sb.append(stts);
		sb.append("\n");
		sb.append("STSZ ");
		sb.append(stsz);
		sb.append("\n");
		sb.append("STCO ");
		sb.append(stco);
		return sb.toString();
	}
}