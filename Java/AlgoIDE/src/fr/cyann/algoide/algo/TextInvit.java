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
package fr.cyann.algoide.algo;

import fr.cyann.algoide.AlgoIDEConstant;
import fr.cyann.tools.ColorTools;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import org.jdesktop.swingx.VerticalLayout;

/**
 *
 * @author caronyn
 */
public class TextInvit extends JPanel {

    private Image background = null;
    private JScrollPane scroll = null;
    private JPanel invit = null;

    public TextInvit() throws HeadlessException {
        super(new BorderLayout());
        invit = new JPanel();

        VerticalLayout layout = new VerticalLayout();
        //layout.setGap(5);
        invit.setLayout(layout);
        invit.setBackground(AlgoIDEConstant.RUN_BACKGROUND_COLOR);
        invit.setBorder(new EmptyBorder(10, 10, 10, 10));

        scroll = new JScrollPane();
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setViewportView(invit);

        add(scroll);

        try {
            background = ImageIO.read(new java.net.URL(getClass().getResource(AlgoIDEConstant.RUN_BACKGROUND_IMAGE), AlgoIDEConstant.RUN_BACKGROUND_IMAGE_NAME));
        } catch (IOException ex) {
            Logger.getLogger(Algo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    /*
     @Override
     public void repaint(long tm, int x, int y, int width, int height) {
     // This forces repaints to repaint the entire TextPane.
     super.repaint(tm, 0, 0, getWidth(), getHeight());
     }
     */

    public void clear() {
        invit.removeAll();
        invit.repaint();
    }

    public void addOutput(String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(AlgoIDEConstant.RUN_BACKGROUND_COLOR);

        JLabel label = new JLabel(text);
        label.setForeground(AlgoIDEConstant.FORE_COLOR);
        label.setFont(AlgoIDEConstant.INVIT_FONT);

        panel.add(label);
        invit.add(panel);

        scroll.revalidate();
        scroll.repaint();
    }

    public interface TextListener {

        public void Validated(String text);
    }

    public interface KeyValidator {

        public boolean isValid(Character chr);
    }
    public static final KeyValidator STRING_VALIDATOR = new KeyValidator() {
        @Override
        public boolean isValid(Character chr) {
            return true;
        }
    };

    public static final KeyValidator NUMBER_VALIDATOR = new KeyValidator() {
        @Override
        public boolean isValid(Character chr) {
            if ((chr >= '0' && chr <= '9') || chr == '.') {
                return true;
            } else {
                return false;
            }
        }
    };

    public void addInputText(String text, final TextListener listener, final KeyValidator keyValidator) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(AlgoIDEConstant.TRANSPARENT);

        JLabel label = new JLabel(text);
        label.setForeground(AlgoIDEConstant.FORE_COLOR);
        label.setFont(AlgoIDEConstant.INVIT_FONT);

        final JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(200, 25));

        final JButton button = new JButton("ok");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                field.setEnabled(false);
                button.setEnabled(false);
                listener.Validated(field.getText());
            }
        });

        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    field.setEnabled(false);
                    button.setEnabled(false);
                    listener.Validated(field.getText());
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (keyValidator.isValid(e.getKeyChar())) {
                    super.keyTyped(e);
                } else {
                    e.consume();
                }
            }

        });

        panel.add(label);
        panel.add(field);
        panel.add(button);
        invit.add(panel);

        scroll.revalidate();
        scroll.repaint();

        field.requestFocus();
        field.requestFocusInWindow();

    }
}
