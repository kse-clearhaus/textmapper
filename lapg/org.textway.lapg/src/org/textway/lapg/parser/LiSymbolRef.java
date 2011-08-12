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
package org.textway.lapg.parser;

import org.textway.lapg.api.Symbol;
import org.textway.lapg.api.SymbolRef;
import org.textway.lapg.parser.ast.IAstNode;

import java.util.Map;

public class LiSymbolRef extends LiAnnotated implements SymbolRef {

	private final Symbol target;
	private final String alias;

	public LiSymbolRef(Symbol target, String alias, Map<String,Object> annotations, IAstNode node) {
		super(annotations, node);
		this.target = target;
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	public Symbol getTarget() {
		return target;
	}

}