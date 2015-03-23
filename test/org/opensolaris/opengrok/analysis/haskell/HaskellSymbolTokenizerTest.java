/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright (c) 2015, Oracle and/or its affiliates. All rights reserved.
 */

package org.opensolaris.opengrok.analysis.haskell;

import java.io.Reader;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.opensolaris.opengrok.analysis.JFlexTokenizer;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;
import org.opensolaris.opengrok.analysis.FileAnalyzer;
import static org.junit.Assert.*;

/**
 * Tests the {@link HaskellSymbolTokenizer} class.
 *
 * @author Harry Pan
 */
public class HaskellSymbolTokenizerTest {

    private final FileAnalyzer analyzer;

    public HaskellSymbolTokenizerTest() {
        this.analyzer = new HaskellAnalyzerFactory().getAnalyzer();
    }

    private String[] getTermsFor(Reader r) {
        List<String> l = new LinkedList<>();
        JFlexTokenizer ts = (JFlexTokenizer) this.analyzer.createComponents("refs").getTokenStream();
        try {
            ts.setReader(r);
        } catch (IOException ex) {
            Logger.getLogger(HaskellSymbolTokenizerTest.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
        ts.yyreset(r);
        CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
        try {
            while (ts.yylex()) {
                l.add(term.toString());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return l.toArray(new String[l.size()]);
    }

    @Test
    public void sampleTest() throws UnsupportedEncodingException {
        InputStream res = getClass().getClassLoader().getResourceAsStream(
            "org/opensolaris/opengrok/analysis/haskell/sample.hs");
        InputStreamReader r = new InputStreamReader(res, "UTF-8");
        String[] termsFor = getTermsFor(r);
        Logger.getLogger(HaskellSymbolTokenizerTest.class.getName()).log(Level.SEVERE, null, termsFor);
        assertArrayEquals(
            new String[]{
                "qsort", // line 2
                "qsort", "x", "xs", "qsort", "x'", "x'", "xs", "x'", "x", "x", "qsort", "x'", "x'", "xs", "x'", "x", //line 3
                "x'y'", "f'", "g'h", "f'", "g'h" // line 6
            },
            termsFor);
    }
}
