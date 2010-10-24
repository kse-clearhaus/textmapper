/**
 * Copyright 2002-2010 Evgeny Gryaznov
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
package net.sf.lapg.templates.model.xml;

import net.sf.lapg.templates.model.xml.XmlTree.TextSource;

public class XmlModel {

	public static XmlNode load(String content) {
		XmlTree<XmlNode> tree = XmlTree.parse(new TextSource(".xml", content.toCharArray(), 1));
		return tree.getRoot();
	}
}