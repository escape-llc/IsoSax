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
 * Callback interface for ISO parser.
 * @author escape-llc
 *
 */
public interface ParseCallback {
	/**
	 * Signal start of parsing.
	 * Called before any other callback.
	 */
	void start();
	/**
	 * Signal a box has been detected.
	 * @param pc parse context.
	 * @param box source box.
	 * @throws Exception on errors.
	 */
	void box(ParseContext pc, Box box) throws Exception;
	/**
	 * Signal end of processing.
	 * No other callbacks will occur after this returns.
	 * @param pc parse context.
	 * @throws Exception on errors.
	 */
	void end(ParseContext pc) throws Exception;
}
