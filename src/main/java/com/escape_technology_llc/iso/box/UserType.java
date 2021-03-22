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
package com.escape_technology_llc.iso.box;


/**
 * Box with a user-defined type.
 * Major box type is UUID.
 * @author escape-llc
 *
 */
public class UserType extends Box {
	final byte[] ext;
	public UserType(Box parent, String type, long position, long length, int hdrsize, byte[] ext) {
		super(parent, type, position, length, hdrsize);
		this.ext = ext;
	}
	@Override public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(type);
		sb.append(":");
		/*
		for(int ix = 0; ix < ext.length; ix++) {
			if(ix > 0) sb.append(".");
			sb.append(Byte.toString(ext[ix]));
		}
		*/
		sb.append(new String(ext));
		sb.append("@");
		final String px = Long.toString(position);
		sb.append(px);
		final long lastbyte = position + length - 1L;
		final String lbx = Long.toString(lastbyte);
		sb.append("-");
		sb.append(shortestSuffix(px, lbx));
		sb.append("[");
		sb.append(length);
		sb.append("/");
		sb.append(hdrsize);
		sb.append("]");
		return sb.toString();
	}
}
