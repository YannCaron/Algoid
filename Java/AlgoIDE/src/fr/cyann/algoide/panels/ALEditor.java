/*
 * Copyright (C) 2014 cyann
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.cyann.algoide.panels;

import fr.cyann.algoide.AlgoIDEConstant;
import fr.cyann.tools.ColorTools;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BraceCompletion;
import javax.swing.CodeEditorPane;
import javax.swing.LineNumbersTextPane;
import javax.swing.SyntaxColorizer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;

/**
 * The ALEditor class. Creation date: 20 mai 2013.
 * <p>
 * @author CyaNn
 * @version v0.1
 */
public class ALEditor extends CodeEditorPane implements FocusListener {

    public static final String POSITION_CARET = "§";
    private final List<Integer> breakpoints;
    private final Speechs speechs;

    public HashMap<String, Color> getALColor() {
        final HashMap<String, Color> syntax = new HashMap<String, Color>();
        syntax.put("return", AlgoIDEConstant.SyntaxColors.KEYWORD.getColor());
        syntax.put("loop", AlgoIDEConstant.SyntaxColors.KEYWORD.getColor());
        syntax.put("for", AlgoIDEConstant.SyntaxColors.KEYWORD.getColor());
        syntax.put("break", AlgoIDEConstant.SyntaxColors.KEYWORD.getColor());
        syntax.put("if", AlgoIDEConstant.SyntaxColors.KEYWORD.getColor());
        syntax.put("elseif", AlgoIDEConstant.SyntaxColors.KEYWORD.getColor());
        syntax.put("else", AlgoIDEConstant.SyntaxColors.KEYWORD.getColor());
        syntax.put("while", AlgoIDEConstant.SyntaxColors.KEYWORD.getColor());
        syntax.put("do", AlgoIDEConstant.SyntaxColors.KEYWORD.getColor());
        syntax.put("until", AlgoIDEConstant.SyntaxColors.KEYWORD.getColor());
        syntax.put("set", AlgoIDEConstant.SyntaxColors.KEYWORD.getColor());
        syntax.put("array", AlgoIDEConstant.SyntaxColors.KEYWORD.getColor());
        syntax.put("function", AlgoIDEConstant.SyntaxColors.KEYWORD.getColor());
        syntax.put("object", AlgoIDEConstant.SyntaxColors.KEYWORD.getColor());
        syntax.put("new", AlgoIDEConstant.SyntaxColors.KEYWORD.getColor());
        syntax.put("supers", AlgoIDEConstant.SyntaxColors.KEYWORD.getColor());

        syntax.put("nil", AlgoIDEConstant.SyntaxColors.NIL.getColor());

        syntax.put("true", AlgoIDEConstant.SyntaxColors.BOOLEAN.getColor());
        syntax.put("false", AlgoIDEConstant.SyntaxColors.BOOLEAN.getColor());

        syntax.put("nan", AlgoIDEConstant.SyntaxColors.NUMBER.getColor());
        syntax.put("infinity", AlgoIDEConstant.SyntaxColors.NUMBER.getColor());

        syntax.put(".", AlgoIDEConstant.SyntaxColors.SYMBOL.getColor());
        syntax.put("..", AlgoIDEConstant.SyntaxColors.SYMBOL.getColor());

        return syntax;
    }

    public boolean isLineHasBreakPoint(int line) {
        return breakpoints.contains(line);
    }

    public ALEditor() {
        super();
        breakpoints = new ArrayList<Integer>();
        speechs = new Speechs();
        linenumbers = new ALLineNumbersSidePane(this);
        linenumbers.setFont(AlgoIDEConstant.SOURCE_FONT);
        jSplitPane1.setLeftComponent(linenumbers);
        this.setKeywordColor(getALColor());

        this.setVerticalLineAtPos(80);

        setFont(AlgoIDEConstant.SOURCE_FONT);
        setTabSize(2);

        addFocusListener(this);

        getContainerWithLines().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                onMouseClicked(e);
            }
        });

        this.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                this_keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                this_keyReleased(e);
            }

        });

    }

    public void format() {
        StringBuilder sb = new StringBuilder(this.getText());

        // save current position
        sb.insert(getSelectionStart(), POSITION_CARET);

        int indent = 0;
        int pos = 0;
        boolean begin = true;

        // loop on each char
        while (pos < sb.length()) {
            char current = sb.charAt(pos);

            if (current == '\t' || (current == ' ' && begin)) {
                sb.deleteCharAt(pos);
                pos--;
            } else if (current == '{') {
                indent++;
            } else if (current == '}') {
                indent--;
                if (sb.charAt(pos - 1) == '\t') {
                    pos--;
                    sb.deleteCharAt(pos);
                }
            } else if (current == '\n' && pos + 1 < sb.length()) {
                begin = true;
                for (int i = 0; i < indent; i++) {
                    pos++;
                    sb.insert(pos, "\t");
                }
            } else {
                begin = false;
            }

            pos++;
        }

        // restore position and set text
        pos = sb.indexOf(POSITION_CARET);
        sb.deleteCharAt(pos);

        this.setText(sb.toString());

        requestFocus();
        select(pos, pos);
    }

    private void this_keyPressed(KeyEvent e) {
        try {

            if (e.getKeyCode() == KeyEvent.VK_TAB) {

                for (int i = getLineOfOffset(this.getSelectionStart()); i <= getLineOfOffset(this.getSelectionEnd()); i++) {

                    int startOffset = getLineStartOffset(i);

                    if (e.isShiftDown()) {
                        if ("\t".equals(this.getStyledDocument().getText(startOffset, 1))) {
                            this.getStyledDocument().remove(startOffset, 1);
                        }
                    } else {
                        this.getStyledDocument().insertString(startOffset, "\t", null);
                    }
                }

                e.consume();
            }
        } catch (BadLocationException ex) {
            // do nothing
        }
    }

    private void this_keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            try {

                int pos = this.getSelectionStart();
                int prevLine = getLineOfOffset(pos - 1);
                int tab = countTabAt(getLineStartOffset(prevLine));

                if ("{".equals(this.getStyledDocument().getText(pos - 2, 1))) {
                    tab++;
                }
                if ("}".equals(this.getStyledDocument().getText(pos, 1))) {
                    tab--;
                }

                for (int i = 0; i < tab; i++) {
                    this.getStyledDocument().insertString(pos, "\t", null);
                }

            } catch (BadLocationException ex) {
                Logger.getLogger(ALEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String getTextAs(EditorKit kit) {
        int begin = 0;
        int end = getDocument().getLength();
        if (getSelectionStart() != getSelectionEnd()) {
            begin = getSelectionStart();
            end = getSelectionEnd();
        }

        ByteArrayOutputStream str = new ByteArrayOutputStream();
        try {
            kit.write(str, getDocument(), begin, end);
        } catch (BadLocationException ex) {
            Logger.getLogger(ALEditor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ALEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        return str.toString();
    }

    public void copyAsRTF() {
        String text = getTextAs(new RTFEditorKit());
        text = text.replaceAll("Monospaced", "Courier");
        text = text.replaceAll("fs30", "fs22");

        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable t = new FlavorTransferable(new ByteArrayInputStream(text.getBytes()), new DataFlavor("text/rtf", "Rich Text Formatted"));
        cb.setContents(t, null);
    }

    public void copyAsHTML() {
        String text = getTextAs(new HTMLEditorKit());

        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable t = new FlavorTransferable(new ByteArrayInputStream(text.getBytes()), new DataFlavor("text/html", "Hyper Text Meta Language"));
        cb.setContents(t, null);
    }

    private void onMouseClicked(MouseEvent e) {
        int line = viewToLine(e.getY());
        if (!breakpoints.contains(line)) {
            breakpoints.add(line);
        } else {
            breakpoints.remove((Integer) line);
        }
        repaint();
        linenumbers.repaint();
    }

    @Override
    public void setText(String t) {
        super.setText(t);
        setCaretPosition(0);
    }

    public String getTextFromLineBegin() {
        try {
            int pos = getCaretPosition();
            String text = getDocument().getText(0, pos);
            int lineBegin = text.lastIndexOf("\n") + 1;
            return text.substring(lineBegin);
        } catch (BadLocationException ex) {
            Logger.getLogger(ALEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public void insertText(String t) {
        int pos = t.indexOf(POSITION_CARET);
        if (pos == -1) {
            pos = t.length();
        }
        t = t.replace(POSITION_CARET, "");

        int caret = getCaretPosition();
        try {
            getDocument().insertString(caret, t, null);
        } catch (BadLocationException ex) {
            Logger.getLogger(ALEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        setCaretPosition(caret + pos);
    }

    public int viewToLine(int y) {

        int modelY = y + jScrollPane1.getVerticalScrollBar().getValue();
        int viewLine = (modelY - 8) / getFontMetrics(getFont()).getHeight();
        return viewLine;
    }

    public int countTabAt(int offset) {
        try {

            int i = 0;
            while (offset + i < getDocument().getLength() && "\t".equals(getDocument().getText(offset + i, 1))) {
                i++;
            }

            return i;
        } catch (BadLocationException ex) {
            return 0;
        }
    }

    public int getLineOfOffset(int offset) throws BadLocationException {
        Document doc = getDocument();
        if (offset < 0) {
            throw new BadLocationException("Can't translate offset to line", -1);
        } else if (offset > doc.getLength()) {
            throw new BadLocationException("Can't translate offset to line", doc.getLength() + 1);
        } else {
            Element map = doc.getDefaultRootElement();
            return map.getElementIndex(offset);
        }
    }

    public int getLineStartOffset(int line) throws BadLocationException {
        Element map = getDocument().getDefaultRootElement();
        if (line < 0) {
            throw new BadLocationException("Negative line", -1);
        } else if (line >= map.getElementCount()) {
            throw new BadLocationException("No such line", getDocument().getLength() + 1);
        } else {
            Element lineElem = map.getElement(line);
            return lineElem.getStartOffset();
        }
    }

    @Override
    protected SyntaxColorizer buildSyntaxColorizer(HashMap<String, Color> keywords) {
        return new ALSyntaxColorizer(this, keywords);
    }
    int highlightFrom = -1, highlightTo = -1;
    int errorFrom = -1, errorTo = -1;

    private void paintTip(Graphics2D g, int line) {
        try {
            Rectangle position = modelToScrollView(getLineStartOffset(line));
            position.translate(0, 3);

            int cx = (g.getClipBounds().width - g.getClipBounds().x) / 2;

            g.fillRoundRect(cx - 8, position.y, 16, position.height, 10, 10);
            g.setColor(ColorTools.alphaColor(g.getColor(), 200));
            g.drawRoundRect(cx - 8, position.y, 16, position.height, 10, 10);

        } catch (BadLocationException ex) {
            Logger.getLogger(ALEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void paintLine(Graphics2D g, int line) {
        try {
            Rectangle position = modelToView(getLineStartOffset(line));
            g.fillRect(getVisibleRect().x, position.y, getVisibleRect().width, position.height);
        } catch (BadLocationException ex) {
            Logger.getLogger(ALEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void paintLineOffset(Graphics2D g, int pos) {
        try {
            Rectangle position = modelToView(pos);
            g.fillRect(getVisibleRect().x, position.y, getVisibleRect().width, position.height);
        } catch (BadLocationException ex) {
            Logger.getLogger(ALEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void paintWord(Graphics2D g, int from, int to) {
        try {
            Rectangle fromPos = modelToView(from);
            Rectangle toPos = modelToView(to);
            g.fillRect(fromPos.x, fromPos.y, toPos.x - fromPos.x, fromPos.height);
        } catch (BadLocationException ex) {
            Logger.getLogger(ALEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static final int MARGIN_SPEECH_W = 5;
    public static final int MARGIN_SPEECH_H = 0;

    private void paintSpeech(Graphics2D g, String message, int figX, int figY) {

        FontMetrics fm = g.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(message, g);

        int w = (int) (r.getWidth() + MARGIN_SPEECH_W * 2);
        int h = (int) (r.getHeight() + MARGIN_SPEECH_H * 2);
        int x = figX - w / 2 + 5;
        int y = figY - h;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(ColorTools.alphaColor(Color.GRAY, 128));
        g.fillRoundRect(x, y, w, h, 10, 10);
        g.setColor(ColorTools.alphaColor(Color.DARK_GRAY, 200));
        g.drawRoundRect(x, y, w, h, 10, 10);
        g.drawLine(x + w / 2, y + h, figX + 2, figY + 3);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setColor(Color.WHITE);
        int xt = (int) ((x + 1 + w / 2) - r.getCenterX());
        int yt = (int) figY + (h - fm.getHeight() - fm.getDescent()) / 2;
        g.drawString(message, xt, yt);
    }

    public void addSpeech(String message, int from, int to) {
        try {
            Rectangle fromPos = modelToView(from);
            Rectangle toPos = modelToView(to);

            int x = fromPos.x + ((toPos.x - fromPos.x) / 2);
            int y = fromPos.y;

            speechs.addSpeech(message, x, y, getLineOfOffset(from));

        } catch (BadLocationException ex) {
            // do nothing
        }
    }

    public void clearSpeechs() {
        speechs.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        final Graphics2D g2 = (Graphics2D) g;

        super.paintComponent(g);

        // breakpoints
        for (Integer breakpoint : breakpoints) {
            g.setColor(AlgoIDEConstant.EDITOR_BREAKPOINT_COLOR);
            paintLine(g2, breakpoint);
        }

        // highlight
        if (highlightFrom != -1 && highlightTo != -1) {
            g.setColor(AlgoIDEConstant.EDITOR_HIGHLIGHT_COLOR);
            paintWord(g2, highlightFrom, highlightTo);
        }

        speechs.each(new Speechs.EachFunction() {

            @Override
            public void invoke(String text, int x, int y) {
                paintSpeech(g2, text, x, y);
            }
        });

        // error
        if (errorFrom != -1 && errorTo != -1) {
            g.setColor(AlgoIDEConstant.EDITOR_ERROR_COLOR);
            paintWord(g2, errorFrom, errorTo);
        }

        g.setColor(AlgoIDEConstant.EDITOR_LINE_COLOR);

        // current line
        paintLineOffset(g2, getCaretPosition());
    }

    public void highlight(int from, int to) {
        highlightFrom = from;
        highlightTo = to;
    }

    public void highlightError(int from, int to) {
        errorFrom = from;
        errorTo = to;
    }

    public void clearHighlight() {
        highlightFrom = -1;
        highlightTo = -1;
        repaint();
    }

    public void clearHighlightError() {
        errorFrom = -1;
        errorTo = -1;
    }

    public void setSelection(int pos) {
        select(pos, pos);
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        super.repaint(tm, 0, y, getWidth(), height + 10);
    }

    private String getCurrentEditLine() {
        int readBackChars = 100;
        int caretPosition = getCaretPosition();

        if (caretPosition == 0) {
            return null;
        }

        StyledDocument doc = getStyledDocument();

        int offset = caretPosition <= readBackChars ? 0 : caretPosition
                - readBackChars;

        String text = null;
        try {
            text = doc.getText(offset, caretPosition);
        } catch (BadLocationException e) {
        }

        if (text != null) {
            int idx = text.lastIndexOf("\n");
            if (idx != -1) {
                return text.substring(idx);
            } else {
                return text;
            }
        }

        return null;
    }

    @Override
    public void focusGained(FocusEvent e) {
        clearHighlightError();
    }

    @Override
    public void focusLost(FocusEvent e) {
    }

    public class ALLineNumbersSidePane extends LineNumbersTextPane.LineNumbersSidePane {

        public ALLineNumbersSidePane(LineNumbersTextPane editor) {
            super(editor);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            Graphics2D g2 = (Graphics2D) g;

            // for antialising geometric shapes
            g2.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON));

            g2.setColor(AlgoIDEConstant.EDITOR_BREAKPOINT_COLOR);
            for (Integer breakPoint : breakpoints) {
                g.setColor(AlgoIDEConstant.EDITOR_BREAKPOINT_COLOR);
                paintTip(g2, breakPoint);
            }

        }
    }
}
