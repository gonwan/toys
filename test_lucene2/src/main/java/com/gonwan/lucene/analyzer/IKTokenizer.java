package com.gonwan.lucene.analyzer;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;

public class IKTokenizer extends Tokenizer {

    private IKSegmenter ikSegmenter;

    private CharTermAttribute charTermAttribute;

    private OffsetAttribute offsetAttribute;

    private TypeAttribute typeAttribute;

    private int endPosition;

    public IKTokenizer(boolean useSmart) {
        charTermAttribute = addAttribute(CharTermAttribute.class);
        offsetAttribute = addAttribute(OffsetAttribute.class);
        typeAttribute = addAttribute(TypeAttribute.class);
        ikSegmenter = new IKSegmenter(input, useSmart);
    }

    @Override
    public boolean incrementToken() throws IOException {
        clearAttributes();
        Lexeme nextLexeme = ikSegmenter.next();
        if (nextLexeme == null) {
            return false;
        } else {
            charTermAttribute.append(nextLexeme.getLexemeText());
            charTermAttribute.setLength(nextLexeme.getLength());
            offsetAttribute.setOffset(nextLexeme.getBeginPosition(), nextLexeme.getEndPosition());
            typeAttribute.setType(nextLexeme.getLexemeText());
            endPosition = nextLexeme.getEndPosition();
            return true;
        }
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        ikSegmenter.reset(input);
    }

    @Override
    public void end() throws IOException {
        int finalOffset = correctOffset(endPosition);
        offsetAttribute.setOffset(finalOffset, finalOffset);
    }

}
