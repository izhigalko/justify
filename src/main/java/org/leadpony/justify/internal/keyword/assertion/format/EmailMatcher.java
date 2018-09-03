/*
 * Copyright 2018 the Justify authors.
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

package org.leadpony.justify.internal.keyword.assertion.format;

import static org.leadpony.justify.internal.keyword.assertion.format.Characters.isAsciiAlphanumeric;

import java.util.BitSet;

/**
 * Matcher for email addresses.
 * 
 * @author leadpony
 */
class EmailMatcher extends AbstractFormatMatcher {
    
    static final int MAX_LOCAL_PART_CHARS = 64;
    static final String ATOM_TEXT_CHARS = "!#$%&'*+-/=?^_`{|}~";
  
    @SuppressWarnings("serial")
    static final BitSet atomTextCharset = new BitSet(128) {{
        for (int i = 0; i < ATOM_TEXT_CHARS.length(); i++) {
            set(ATOM_TEXT_CHARS.charAt(i));
        }
    }};
    
    EmailMatcher(CharSequence input) {
        super(input);
    }
    
    @Override
    public void all() {
        localPart();
        if (next() == '@') {
            domainPart();
        } else {
            fail();
        }
    }
    
    private void localPart() {
        final int start = pos();
        comments();
        if (peek() == '\"') {
            quotedString();
        } else {
            dotAtom();
        }
        comments();
        int length = pos() - start;
        if (length > MAX_LOCAL_PART_CHARS) {
            fail();
        }
    }
    
    private void dotAtom() {
        atomText();
        while (peek() == '.') {
            next();
            atomText();
        }
    }
    
    private void atomText() {
        int length = 0;
        for (;;) {
            char c = peek();
            if (c == '@' || c == '.' || c == '(') {
                break;
            }
            next();
            if (checkAtomLetter(c)) {
                length++;
            } else {
                fail();
            }                
        }
        if (length == 0) {
            fail();
        }
    }
    
    private void quotedString() {
        // Skips opening quote.
        char c = next();
        while ((c = next()) != '\"') {
            if (c == '\\') {
                if (!checkQuotedLetter(next())) {
                    fail();
                }
            } else if (checkQuotedLetter(c)) {
                // good
            } else {
                fail();
            }
        }
    }
    
    private void domainPart() {
        final int start = pos();
        comments();
        if (peek() == '[') {
            domainLiteral();
        } else {
            hostname();
        }
        comments();
        int length = pos() - start;
        if (length > HostnameMatcher.MAX_DOMAIN_CHARS) {
            fail();
        }
    }
    
    private void domainLiteral() {
        // Opening bracket.
        char c = next();
        while ((c = next()) != ']') {
            if (!checkDomainLiteralLetter(c)) {
                fail();
            }
        }
    }
    
    private void hostname() {
        final int start = pos();
        while (hasNext() && peek() != '(') {
            next();
        }
        createHostnameMatcher(start, pos()).all();
    }
    
    private void comments() {
        if (!hasNext() || peek() != '(') {
            return;
        }
        comment();
        while (hasNext() && peek() == '(') {
            comment();
        }
    }
    
    private void comment() {
        next();
        for (;;) {
            char c = peek();
            if (c == '(') {
                comment();
            } else {
                next();
                if (c == ')') {
                    break;
                } else if (c == '\\') {
                    if (!checkQuotedLetter(next())) {
                        fail();
                    }
                } else if (!checkCommentLetter(c)) {
                    fail();
                }
            }
        }
    }
    
    protected boolean checkQuotedLetter(char c) {
        return c >= 32 && c < 127;
    }
    
    protected boolean checkAtomLetter(char c) {
        return isAsciiAlphanumeric(c) || atomTextCharset.get(c);
    }
    
    protected boolean checkDomainLiteralLetter(char c) {
        return c >= 32 && c < 127 &&
               c != '[' && c != '\\';
    }
    
    protected boolean checkCommentLetter(char c) {
        return isNonWhiteSpaceControl(c) ||
                (c >= 33 && c <= 39) ||
                (c >= 42 && c <= 91) ||
                (c >= 93 && c <= 126);
    }
    
    protected FormatMatcher createHostnameMatcher(int start, int end) {
        return new HostnameMatcher(input(), start, end);
    }
    
    private static boolean isNonWhiteSpaceControl(char c) {
        return (c >= 1 && c <= 9) ||
               (c == 11) ||
               (c == 12) ||
               (c >= 14 && c <= 31) ||
               (c == 127);
    }
}