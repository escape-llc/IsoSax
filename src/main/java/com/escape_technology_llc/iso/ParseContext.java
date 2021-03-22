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
package com.escape_technology_llc.iso;

import com.escape_technology_llc.iso.box.Box;

/**
 * Control interface for callbacks to direct the recursive descent into boxes.
 * @author escape-llc
 *
 */
public interface ParseContext {
	/**
	 * Skip given box header and recursively parse the contents of the box.
	 * @param box Source box to parse.
	 * @param pc Callback to use for parsing.
	 * @throws Exception
	 */
	void parseBox(Box box, ParseCallback pc) throws Exception;
	/**
	 * Materialize the bytes of data as indicated.
	 * @param position
	 * @param length
	 * @return
	 * @throws Exception
	 */
	byte[] materialize(long position, int length) throws Exception;
	/**
	 * Attempt to create an object from the box contents.
	 * @param box
	 * @return !NULL: instance; NULL: no instance.
	 * @throws Exception
	 */
	<T> T create(Box box) throws Exception;
	/**
	 * Get the exception handler for the context.
	 * @return
	 */
	ParseHandler handler();
}
