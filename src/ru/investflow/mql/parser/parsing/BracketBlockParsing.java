package ru.investflow.mql.parser.parsing;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ru.investflow.mql.parser.parsing.util.ParsingErrors;
import ru.investflow.mql.parser.parsing.util.ParsingUtils;
import ru.investflow.mql.psi.MQL4Elements;
import ru.investflow.mql.psi.MQL4TokenSets;

import java.util.Stack;

import static ru.investflow.mql.parser.parsing.CommentParsing.parseComment;
import static ru.investflow.mql.parser.parsing.statement.StatementParsing.parseEmptyStatement;
import static ru.investflow.mql.parser.parsing.util.ParsingErrors.NO_MATCHING_CLOSING_BRACKET;

public class BracketBlockParsing implements MQL4Elements {

    //TODO: Bracket blocks do not support #ifdef / #define parsing -> need global solution to fix this problem

    public static boolean parseBracketsBlock(PsiBuilder b, int l) {
        if (!ParsingUtils.nextTokenIn(b, l, "parseBracketBlock", MQL4TokenSets.BRACKETS)) {
            return false;
        }
        IElementType lBracket = b.getTokenType();
        assert lBracket != null;
        if (MQL4TokenSets.RIGHT_BRACKETS.contains(lBracket)) {
            ParsingUtils.advanceWithError(b, ParsingErrors.UNEXPECTED_TOKEN);
            return false;
        }

        PsiBuilder.Marker block = b.mark(); // starting new bracket section
        b.advanceLexer(); // left bracket
        if (!hasMatchingClosingBracket(b, lBracket)) { // must check it here to set error on the correct position.
            b.error(NO_MATCHING_CLOSING_BRACKET);
        }
        try {
            IElementType rBracket = MQL4TokenSets.getRightBracketFor(lBracket);
            while (!b.eof()) { // now parse sub-blocks one by one. Stop on closing bracket
                IElementType t = b.rawLookup(0);
                assert t != null;
                if (t == rBracket) {
                    b.advanceLexer(); // right bracket
                    return true;
                }
                if (MQL4TokenSets.RIGHT_BRACKETS.contains(t)) { // different kind of right bracket
                    ParsingUtils.advanceWithError(b, ParsingErrors.UNEXPECTED_TOKEN);
                    continue;
                }
                boolean res = parseBracketsBlock(b, l + 1)
                        || parseEmptyStatement(b)
                        || parseComment(b);

                if (!res) { // if was not parsed -> just consume this element
                    b.advanceLexer();
                }
            }
            return true;
        } finally {
            block.done(BRACKETS_BLOCK);
        }
    }

    private static boolean hasMatchingClosingBracket(@NotNull PsiBuilder b, @NotNull IElementType lBracket) {
        IElementType rBracket = MQL4TokenSets.getRightBracketFor(lBracket);
        Stack<IElementType> lStack = new Stack<>();
        for (int i = 0; ; i++) {
            IElementType e = b.rawLookup(i);
            if (e == null) { // right bracket not found.
                return false;
            }
            if (!MQL4TokenSets.BRACKETS.contains(e)) { // ignore non-bracket elements
                continue;
            }
            if (MQL4TokenSets.LEFT_BRACKETS.contains(e)) { // new bracket block
                lStack.push(e);
                continue;
            }
            // e is a right bracket here.
            if (e == rBracket && lStack.isEmpty()) { // the bracket we need
                return true;
            }
            // pop bracket for well-formed nested block
            if (!lStack.isEmpty() && lStack.peek() == MQL4TokenSets.getLeftBracketFor(e)) {
                lStack.pop();
            } // else the error will be handled when corresponding nested block is parsed
        }
    }

}
