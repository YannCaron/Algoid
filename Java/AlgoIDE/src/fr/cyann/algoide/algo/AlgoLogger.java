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
import fr.cyann.tools.swing.JAutoScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.SystemColor;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import org.jdesktop.swingx.VerticalLayout;

/**
 * <p>
 * @author caronyn
 */
public class AlgoLogger extends JPanel {

    // const
    public static final String LOG_SEPARATOR = "  ";
    public static final String LOG_FORMAT = "%s" + LOG_SEPARATOR + "%s" + LOG_SEPARATOR + "%s\n";
    public static final String LOG_SECONDE_FORMAT = "HH:mm:ss.SSS";
    // attributs
    private JScrollPane scroll = null;
    private JPanel logger = null;

    public AlgoLogger() throws HeadlessException {
        super(new BorderLayout());

        logger = new JPanel();

        VerticalLayout layout = new VerticalLayout();
        //layout.setGap(5);
        logger.setLayout(layout);
        logger.setBackground(UIManager.getColor("text"));
        
        //int sc = SystemColor.CONTROL_HIGHLIGHT;
        //logger.setBackground(.);
        logger.setBorder(new EmptyBorder(10, 10, 10, 10));

        scroll = new JAutoScrollPane();
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setViewportView(logger);

        add(scroll);
        //logger.setBackground(AlgoIDEConstant.RUN_BACKGROUND_COLOR);

    }

    @Override
    public void setBackground(Color bg) {
        if (logger != null) {
            logger.setBackground(bg);
        }
    }

    public void clear() {
        logger.removeAll();

        scroll.revalidate();
        scroll.repaint();
    }

    public void addLog(AlgoIDEConstant.Severity severity, String text) {
        SimpleDateFormat sf = new SimpleDateFormat(LOG_SECONDE_FORMAT);
        String date = sf.format(new Date());

        String sev = severity.name();
        while (sev.length() < 4) {
            sev = sev + " ";
        }
        String log = String.format(LOG_FORMAT, date, sev, text);

        JLabel label = new JLabel(log);
        label.setForeground(severity.getColor());
        label.setFont(AlgoIDEConstant.LOG_FONT);

        logger.add(label);

        scroll.revalidate();
        scroll.repaint();
    }

    public void info(String text) {
        addLog(AlgoIDEConstant.Severity.INFO, text);
    }

    public void warn(String text) {
        addLog(AlgoIDEConstant.Severity.WARN, text);
    }

    public void err(String text) {
        addLog(AlgoIDEConstant.Severity.ERR, text);
    }

}
