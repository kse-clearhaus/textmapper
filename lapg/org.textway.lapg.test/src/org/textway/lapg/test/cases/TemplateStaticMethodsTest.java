/**
 * Copyright 2002-2011 Evgeny Gryaznov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.textway.lapg.test.cases;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.textway.lapg.gen.TemplateStaticMethods;

/**
 * Gryaznov Evgeny, 5/19/11
 */
public class TemplateStaticMethodsTest extends TestCase {


	public void testShiftLeft() {
		Assert.assertEquals("AA\nBB\n", new TemplateStaticMethods().shiftLeft("\t\tAA\n\t\tBB\n"));
		Assert.assertEquals("AA\n\tBB\n", new TemplateStaticMethods().shiftLeft("\t\tAA\n\t\t\tBB\n"));
		Assert.assertEquals("\tAA\nBB\n", new TemplateStaticMethods().shiftLeft("\t\t\tAA\n\t\tBB\n"));
		Assert.assertEquals(" AA\nBB\n", new TemplateStaticMethods().shiftLeft("\t\t AA\n\t\tBB\n"));
		Assert.assertEquals(
				" AA\n" +
				"\n" +
				"BB\n", new TemplateStaticMethods().shiftLeft(
						"\t\t AA\n" +
						"\n" +
						"\t\tBB\n"));
		Assert.assertEquals(
				" AA\n" +
				"\n" +
				"BB\n", new TemplateStaticMethods().shiftLeft(
						"\t\t AA\r\n" +
						"\r\n" +
						"\t\tBB\r\n"));
	}

	public void testShiftRight() {
		Assert.assertEquals("\t\t\tAA\n\n\t\t\tBB\n", new TemplateStaticMethods().shiftRight("\t\tAA\n\n\t\tBB\n", 1));
	}
}