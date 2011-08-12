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
package org.textway.lapg.test.cases.bootstrap.b.ast;

import java.util.List;
import org.textway.lapg.test.cases.bootstrap.b.SampleBTree.TextSource;

public class AstClassdef extends AstNode implements IAstClassdefNoEoi {

	private AstID ID;
	private List<AstClassdeflistItem> classdeflistopt;
	private String Lextends;
	private String identifier;

	public AstClassdef(AstID ID, List<AstClassdeflistItem> classdeflistopt, String Lextends, String identifier, TextSource input, int start, int end) {
		super(input, start, end);
		this.ID = ID;
		this.classdeflistopt = classdeflistopt;
		this.Lextends = Lextends;
		this.identifier = identifier;
	}

	public AstID getID() {
		return ID;
	}
	public List<AstClassdeflistItem> getClassdeflistopt() {
		return classdeflistopt;
	}
	public String getLextends() {
		return Lextends;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void accept(AstVisitor v) {
		if (!v.visit(this)) {
			return;
		}

		if (ID != null) {
			ID.accept(v);
		}
		if (classdeflistopt != null) {
			for (AstClassdeflistItem it : classdeflistopt) {
				it.accept(v);
			}
		}
		// TODO for Lextends
		// TODO for identifier
	}
}