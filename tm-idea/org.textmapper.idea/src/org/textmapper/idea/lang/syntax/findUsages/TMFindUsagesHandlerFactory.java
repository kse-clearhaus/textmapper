/**
 * Copyright 2010-2017 Evgeny Gryaznov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package org.textmapper.idea.lang.syntax.findUsages;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesHandlerFactory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.textmapper.idea.lang.syntax.psi.TmGrammar;
import org.textmapper.idea.lang.syntax.psi.TmLexerState;
import org.textmapper.idea.lang.syntax.psi.TmLexerStateSelector;
import org.textmapper.idea.lang.syntax.psi.TmNamedElement;

import java.util.ArrayList;
import java.util.List;

/**
 * evgeny, 8/11/12
 */
public class TMFindUsagesHandlerFactory extends FindUsagesHandlerFactory {
	@Override
	public boolean canFindUsages(@NotNull PsiElement element) {
		return element instanceof TmNamedElement;
	}

	@Override
	public FindUsagesHandler createFindUsagesHandler(@NotNull PsiElement element, boolean forHighlightUsages) {
		if (element instanceof TmLexerState) {
			return new TMLexerStateFindUsagesHandler((TmLexerState) element);
		}
		if (element instanceof TmNamedElement) {
			return new FindUsagesHandler(element) {};
		}
		throw new IllegalArgumentException("unexpected element type: " + element);
	}

	private static class TMLexerStateFindUsagesHandler extends FindUsagesHandler {
		public TMLexerStateFindUsagesHandler(TmLexerState element) {
			super(element);
		}

		@NotNull
		@Override
		public PsiElement[] getPrimaryElements() {
			TmLexerState anchorState = (TmLexerState) getPsiElement();
			String name = anchorState.getName();
			TmGrammar grammar = PsiTreeUtil.getTopmostParentOfType(anchorState, TmGrammar.class);
			if (grammar != null && name != null) {
				List<TmLexerState> states = new ArrayList<>();
				for (TmLexerStateSelector stateSet : grammar.getStateSelectors()) {
					for (TmLexerState lexerState : stateSet.getStates()) {
						if (name.equals(lexerState.getName())) {
							states.add(lexerState);
						}
					}
				}
				return states.toArray(new PsiElement[states.size()]);
			}
			return super.getPrimaryElements();
		}
	}
}
