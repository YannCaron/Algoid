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
package fr.cyann.algoide;

import fr.cyann.al.AL;
import fr.cyann.al.scope.Scope;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.algoide.algo.Algo;
import fr.cyann.algoide.algo.AlgoLogger;
import fr.cyann.algoide.algo.ScopeViewer;
import fr.cyann.algoide.algo.TextInvit;
import fr.cyann.algoide.lang.LangUI;
import fr.cyann.algoide.panels.ALEditor;
import fr.cyann.algoide.panels.AlgoIDEPanel;
import fr.cyann.algoide.runtime.TaskManager;
import fr.cyann.al.autoCompletion.AutoCompletion;
import fr.cyann.tools.DesktopUtil;
import fr.cyann.tools.FileTools;
import fr.cyann.tools.GATracker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * The MainPanel main class. Creation date: 20 mai 2013.
 * <p>
 * @author CyaNn
 * @version v0.1
 */
public class MainPanel extends AlgoIDEPanel {

    // constants
    public static final float MAIN_SPLIT_DEFAULT = 0.5f;
    public static final float RUN_SPLIT_DEFAULT = 0.75f;
    // layout
    private JSplitPane runSplit = null;
    private JSplitPane mainSplit = null;
    private JTabbedPane runTab = null;
    private JTabbedPane infoTab = null;
    private JMenuItem save = null;
    private JCheckBox debug = null;
    private JCheckBox step = null;
    private JSpinner stepSpeed = null;
    private JPanel debugPanel = null;
    // controls
    private ALEditor editor = null;
    private Algo algo = null;
    private TextInvit invit = null;
    private AlgoLogger logger = null;
    private ScopeViewer scopeViewer = null;
    // task
    private TaskManager taskManager = null;
    // attributs
    private String currentFolder = "";
    private File currentFile = null;

    public MainPanel() {
        super(new BorderLayout());

        runTab = new JTabbedPane();
        infoTab = new JTabbedPane();

        editor = new ALEditor();
        algo = new Algo();
        invit = new TextInvit();
        logger = new AlgoLogger();
        scopeViewer = new ScopeViewer();

        taskManager = new TaskManager(this, algo, invit, logger);
        //editor.setKeywordHelp(taskManager.getSyntax(), taskManager.getExec());

        AutoCompletion.getInstance().initialize(new AutoCompletion.AutoCompletionInitializer() {

            @Override
            public void initializePrimitives(HashMap<String, String> map) {
                map.put("nil", "Null object.");
                map.put("true", "The true boolean value.");
                map.put("false", "The false boolean value.");
                map.put("nan", "Not an number (for example division by zero).");
                map.put("infinity ", "Infinity number.");

                map.put("set ", "Create a variable in the current scope.");
                map.put("if () {\n} else {\n}\n", "If execute the block if condition is true and else otherwise. Elseif are executed if the other condition is true.");
                map.put("loop () {\n}\n", "Loop repeats a block of code while limit is reached.");
                map.put("for ( ; ; ) {\n}\n", "For loop repeats a block of code while a control variable runs through an arithmetic progression.");
                map.put("do {\n} until ()\n", "Loop until condition become false. Condition is tester after execution.");
                map.put("while () {\n}\n", "Loop while condition return true.");
                map.put("array {\n}\n", "Define a linked list of elements or a dictionary indexable by any expression. When defining nested arrays, the keyword 'array' is only required for the first level of the array (the root). It then becomes optional.");
                map.put("function () {\n}\n", "Define a reusable and nested scoped part of code with parameters. The particularity of AL it that functions are considered as data. Function is an expression, so function declaring can be terminated by a semicolon.");
                map.put("object () {\n}\n", "Define a reusable scope composed of a set of attributes, methods and nested objects.");
            }

            @Override
            public RuntimeContext getBaseAPI() {
                return AL.createContext(taskManager.getSyntax(), taskManager.getExec(), null);
            }

            @Override
            public void applyMap(HashMap<String, String> map) {
                editor.setKeywordHelp(map);
            }

        });

        JPanel tools = new JPanel(new BorderLayout());
        addMainMenu(tools);
        addTools(tools);

        add(tools, BorderLayout.NORTH);

        runTab.addTab("algo", algo);
        runTab.addTab("text", invit);
        AlgoIDE.PLUGIN_LOADER.loadAdditionalPanels(runTab);

        runTab.setSelectedIndex(0);
        runTab.setTabPlacement(JTabbedPane.BOTTOM);

        infoTab.addTab("log", logger);
        infoTab.addTab("scope viewer", scopeViewer);
        infoTab.setSelectedIndex(0);
        //infoTab.setTabPlacement(JTabbedPane.BOTTOM);

        runSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, runTab, infoTab);
        runSplit.setOneTouchExpandable(true);
        runSplit.setResizeWeight(RUN_SPLIT_DEFAULT);

        mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editor.getContainerWithLines(), runSplit);
        mainSplit.setOneTouchExpandable(true);
        mainSplit.setResizeWeight(MAIN_SPLIT_DEFAULT);

        add(mainSplit, BorderLayout.CENTER);

        LangUI.getInstance().applyLocale(this);

        // save on exit
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                FileTools.saveToCurrentFile(editor.getText());
            }

        });

    }

    public static void setLaf() {
        try {

            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");

        } catch (Exception e1) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, e1);

            try {
                
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

                 //set Nimbus LAF primary colors
                 UIManager.put("control", new Color(114, 114, 114));
                 UIManager.put("nimbusBase", new Color(64, 64, 64));
                 UIManager.put("nimbusFocus", new Color(191, 191, 191));
                 //UIManager.put("nimbusLightBackground", new Color(176, 176, 176));
                 UIManager.put("nimbusLightBackground", new Color(230, 230, 230));
                 UIManager.put("nimbusSelectionBackground", new Color(90, 130, 195));
                 UIManager.put("text", new Color(0, 0, 0));

            } catch (Exception e2) {
                Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, e2);
            }
        }
    }

    private void saveVisibility() {
        if (currentFile == null) {
            save.setEnabled(false);
        } else {
            save.setEnabled(true);
        }
    }

    private void chooseFile(boolean newFile) {
        JFileChooser chooser = new JFileChooser(currentFolder);
        FileFilter ft = new FileNameExtensionFilter("AL file", "al");
        chooser.setFileFilter(ft);

        int ret = chooser.showOpenDialog(MainPanel.this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            currentFolder = chooser.getSelectedFile().getPath();
            currentFile = chooser.getSelectedFile();

            if (newFile) {
                currentFile = FileTools.ensureExtension(currentFile);
            }

            saveVisibility();
        }
    }

    private Icon getIcon(String path) {
        URL url = getClass().getResource(AlgoIDEConstant.IMAGE_FOLDER + path);
        return new ImageIcon(url);
    }

    // menu section --------
    private void addMainMenu(JPanel panel) {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");
        file.setName("mn_file");
        addFileMenu(file);
        menuBar.add(file);

        JMenu edit = new JMenu("Edit");
        edit.setName("mn_edit");
        addEditMenu(edit);
        menuBar.add(edit);

        JMenu examples = new JMenu("Examples");
        examples.setName("mn_examples");
        addExampleMenu(examples, getClass().getResource("/" + AlgoIDEConstant.EXAMPLE_FOLDER).getFile());
        menuBar.add(examples);

        JMenu help = new JMenu("Help");
        help.setName("mn_help");
        menuBar.add(help);

        addWebMenu(help, "mn_tutorials", "Tutorials", "tuto.png", KeyStroke.getKeyStroke(KeyEvent.VK_F1, Event.CTRL_MASK), LangUI.getInstance().getString("url_tutorial"));
        addWebMenu(help, "mn_helpweb", "Help", "help.png", KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), LangUI.getInstance().getString("url_help"));
        addWebMenu(help, "mn_forum", "Forum", "forum.png", KeyStroke.getKeyStroke(KeyEvent.VK_F1, Event.SHIFT_MASK), LangUI.getInstance().getString("url_forum"));
        addWebMenu(help, "mn_follow", "Follow", "google.png", KeyStroke.getKeyStroke(KeyEvent.VK_F1, Event.ALT_MASK), LangUI.getInstance().getString("url_google_plus"));

        addWebMenu(menuBar, "mn_donate", "Donate", "donate.png", KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.CTRL_MASK), LangUI.getInstance().getString("url_donate"));

        panel.add(menuBar, BorderLayout.NORTH);
    }

    private void addFileMenu(JMenu menu) {
        JMenuItem newFile = new JMenuItem("New", getIcon("new.png"));
        newFile.setName("mn_new");
        newFile.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GATracker.getInstance().trackEvent("ui.menu", "open.new");
                currentFile = null;
                MainPanel.this.editor.setText(AlgoIDEConstant.NEW_FILE_TEMPLATE);
                saveVisibility();
            }
        });
        newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
        menu.add(newFile);

        JMenuItem open = new JMenuItem("Open", getIcon("open.png"));
        open.setName("mn_open");
        open.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GATracker.getInstance().trackEvent("ui.menu", "open.own");
                chooseFile(false);
                MainPanel.this.loadFile(currentFile);
            }
        });
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
        menu.add(open);

        save = new JMenuItem("Save", getIcon("save.png"));
        save.setName("mn_save");
        save.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                MainPanel.this.saveFile(currentFile);
            }
        });
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
        menu.add(save);

        JMenuItem saveas = new JMenuItem("Save as", getIcon("saveas.png"));
        saveas.setName("mn_saveas");
        saveas.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFile(true);
                MainPanel.this.saveFile(currentFile);
            }
        });
        saveas.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK | Event.SHIFT_MASK));
        menu.add(saveas);

        saveVisibility();
    }

    private void addMenu(JMenu menu, String name, String text, String iconPath, KeyStroke keyStroke, ActionListener action) {
        JMenuItem subMenu = new JMenuItem(text, getIcon(iconPath));
        subMenu.setName(name);
        subMenu.setAccelerator(keyStroke);
        subMenu.addActionListener(action);
        menu.add(subMenu);
    }

    private void addExampleMenu(JMenu menu, String root) {
        JMenu current = null;
        for (final String path : AlgoIDEConstant.EXAMPLE_FILES) {
            final String fullPath = AlgoIDEConstant.EXAMPLE_FOLDER + path;

            if (path.endsWith("/")) {
                current = new JMenu(path.replace("/", ""));
                menu.add(current);
            } else {
                final JMenuItem fMenu = new JMenuItem(path.substring(path.lastIndexOf("/") + 1));
                fMenu.addActionListener(new AbstractAction() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        GATracker.getInstance().trackEvent("ui.menu", "open.example", path);
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                loadFile(fullPath);
                            }
                        });
                    }
                });
                if (current != null) {
                    current.add(fMenu);
                } else {
                    menu.add(fMenu);
                }
            }
        }
    }

    private void browseURL(String url) {

        try {

            java.net.URL uri = new java.net.URL(url);
            DesktopUtil.browse(uri);
        } catch (Exception e) {

            System.err.println(e.getMessage());
        }
    }

    private void addWebMenu(JComponent menu, String name, String text, String iconPath, KeyStroke keyStroke, final String url) {
        JMenuItem subMenu = new JMenuItem(text, getIcon(iconPath));
        subMenu.setName(name);
        subMenu.setAccelerator(keyStroke);
        subMenu.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GATracker.getInstance().trackEvent("ui.menu", "help", url);
                browseURL(url);
            }
        });
        menu.add(subMenu);
    }

    private void addEditMenu(JMenu menu) {
        addMenu(menu, "mn_undo", "Undo", "undo.png", KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK), new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                editor.getActionMap().get("undo").actionPerformed(e);
            }
        });

        addMenu(menu, "mn_redo", "Redo", "redo.png", KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK), new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                editor.getActionMap().get("redo").actionPerformed(e);
            }
        });

        menu.add(new JSeparator());

        addMenu(menu, "mn_cut", "Cut", "cut.png", KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK), new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                editor.requestFocus();
                editor.requestFocusInWindow();
                editor.cut();
            }
        });

        addMenu(menu, "mn_copy", "Copy", "copy.png", KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK), new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                editor.requestFocus();
                editor.requestFocusInWindow();
                editor.copy();
            }
        });

        addMenu(menu, "mn_copy_rtf", "Copy formated text", "copy.png", null, new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                editor.requestFocus();
                editor.requestFocusInWindow();
                editor.copyAsRTF();
            }
        });

        addMenu(menu, "mn_copy_html", "Copy formated text", "copy.png", null, new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                editor.requestFocus();
                editor.requestFocusInWindow();
                editor.copyAsHTML();
            }
        });

        addMenu(menu, "mn_paste", "Paste", "paste.png", KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK), new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                editor.requestFocus();
                editor.requestFocusInWindow();
                editor.paste();
            }
        });

        addMenu(menu, "mn_selectall", "Select all", "select.png", KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK), new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                editor.requestFocus();
                editor.requestFocusInWindow();
                editor.selectAll();
            }
        });

        menu.add(new JSeparator());

        addMenu(menu, "mn_format", "Format", "format.png", KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.ALT_MASK), new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                editor.format();
            }
        });

        menu.add(new JSeparator());

        addMenu(menu, "mn_search", "Search", "search.png", KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK), new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                editor.requestFocus();
                editor.requestFocusInWindow();
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        editor.getActionMap().get("find").actionPerformed(e);
                    }
                });
            }
        });
    }

    // tools section --------
    private void addTool(JPanel panel, String name, String iconPath, Action action) {
        JButton button = new JButton(name, getIcon(iconPath));
        button.addActionListener(action);
        panel.add(button);
    }

    private void addSpace(JPanel panel) {
        panel.add(new JToolBar.Separator(new Dimension(25, 0)));
    }

    private void addDebugTool(JPanel panel) {
        FlowLayout layout = new FlowLayout();
        layout.setVgap(0);
        debugPanel = new JPanel(layout);
        debugPanel.setBorder(null);
        debugPanel.add(new JSeparator(JSeparator.VERTICAL));

        addTool(debugPanel, "point", "next.png", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                taskManager.continueDebug();
            }
        });

        addTool(debugPanel, "line", "next.png", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                taskManager.gotoNextLine();
            }
        });

        addTool(debugPanel, "token", "next.png", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                taskManager.gotoNextToken();
            }
        });

        panel.add(debugPanel);
        hideDebugMenu();
    }

    public void stopRunning() {
        algo.clearEvents();
        AlgoIDE.PLUGIN_LOADER.clearEvents();
        taskManager.terminateTasks();
        algo.initialize();
        invit.clear();
        hideDebugMenu();
        editor.clearHighlight();
        editor.clearSpeechs();
    }

    private void addTools(JPanel panel) {
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        addTool(toolBar, "", "run.png", new AbstractAction("run") {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (taskManager.isFree()) {
                    GATracker.getInstance().trackEvent("ui.menu", "launch.run");
                    stopRunning();
                    FileTools.saveToCurrentFile(editor.getText());

                    if (isDebug() || isStep()) {
                        showScopePanel();
                    }

                    taskManager.postWeakSource(editor.getText());
                }
            }
        });

        addTool(toolBar, "", "stop.png", new AbstractAction("run") {

            @Override
            public void actionPerformed(ActionEvent e) {
                GATracker.getInstance().trackEvent("ui.menu", "launch.run");
                stopRunning();
            }
        });

        addSpace(toolBar);

        debug = new JCheckBox("Debug mode");
        debug.setName("cb_debug");
        debug.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox source = (JCheckBox) e.getSource();
                GATracker.getInstance().trackEvent("ui.menu", "config.debug", String.valueOf(source.isSelected()));
                taskManager.setDebugMode(source.isSelected());
            }
        });
        toolBar.add(debug);

        step = new JCheckBox("Step by step mode");
        step.setName("cb_step");
        step.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox source = (JCheckBox) e.getSource();
                GATracker.getInstance().trackEvent("ui.menu", "config.step", String.valueOf(source.isSelected()));
                taskManager.setStepByStepMode(source.isSelected());
            }
        });
        toolBar.add(step);

        stepSpeed = new JSpinner();
        stepSpeed.setModel(AlgoIDEConstant.DEFAULT_STEP_SPEED);
        toolBar.add(stepSpeed);

        addDebugTool(toolBar);

        addSpace(toolBar);

        addTool(toolBar, "", "format.png", new AbstractAction("format") {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (taskManager.isFree()) {
                    GATracker.getInstance().trackEvent("ui.menu", "format");
                    editor.format();
                }
            }
        });

        addSpace(toolBar);

        addTool(toolBar, "", "undo.png", new AbstractAction("undo") {

            @Override
            public void actionPerformed(ActionEvent e) {
                editor.getActionMap().get("undo").actionPerformed(e);
            }
        });

        addTool(toolBar, "", "redo.png", new AbstractAction("redo") {

            @Override
            public void actionPerformed(ActionEvent e) {
                editor.getActionMap().get("redo").actionPerformed(e);
            }
        });

        addSpace(toolBar);

        addTool(toolBar, "", "cut.png", new AbstractAction("cut") {

            @Override
            public void actionPerformed(ActionEvent e) {
                editor.requestFocus();
                editor.requestFocusInWindow();
                editor.cut();
            }
        });

        addTool(toolBar, "", "copy.png", new AbstractAction("copy") {

            @Override
            public void actionPerformed(ActionEvent e) {
                editor.requestFocus();
                editor.requestFocusInWindow();
                editor.copy();
            }
        });

        addTool(toolBar, "", "paste.png", new AbstractAction("paste") {

            @Override
            public void actionPerformed(ActionEvent e) {
                editor.requestFocus();
                editor.requestFocusInWindow();
                editor.paste();
            }
        });

        panel.add(toolBar, BorderLayout.SOUTH);
    }

    // attributs
    public Algo getAlgo() {
        return algo;
    }

    public TextInvit getInvit() {
        return invit;
    }

    public AlgoLogger getLogger() {
        return logger;
    }

    public boolean isDebug() {
        return debug.isSelected();
    }

    public boolean isStep() {
        System.out.println(step.isSelected());
        return step.isSelected();
    }

    @Override
    public long getStepSpeed() {
        return ((Integer) stepSpeed.getValue()).longValue();
    }

    // methods
    @Override
    public void ExceptionPopup(String message) {
        JOptionPane.showMessageDialog(this, message, "error", JOptionPane.ERROR_MESSAGE);
    }

    public void loadFile(File file) {
        String source = FileTools.readFile(file);
        editor.setText(source);
    }

    public void loadFile(String path) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (is != null) {
            String source = FileTools.readFile(is);
            editor.setText(source);
        }
    }

    public void saveFile(File file) {
        FileTools.saveFile(file, editor.getText());
        FileTools.saveToCurrentFile(editor.getText());
    }

    @Override
    public void fullScreen() {
        mainSplit.setDividerLocation(0.0f);
        runSplit.setDividerLocation(1.0f);
    }

    @Override
    public void halfScreen() {
        mainSplit.setDividerLocation(MAIN_SPLIT_DEFAULT);
        runSplit.setDividerLocation(RUN_SPLIT_DEFAULT);
    }

    @Override
    public void miniScreen() {
        mainSplit.setDividerLocation(1.0f);
        runSplit.setDividerLocation(RUN_SPLIT_DEFAULT);
    }

    @Override
    public void showAlgoPanel() {
        runTab.setSelectedIndex(0);
    }

    @Override
    public void showTextPanel() {
        runTab.setSelectedIndex(1);
    }

    public void showLogPanel() {
        infoTab.setSelectedIndex(0);
    }

    public void showScopePanel() {
        infoTab.setSelectedIndex(1);
    }

    @Override
    public void showPanel(String name) {
        for (int i = 0; i < runTab.getTabCount(); i++) {
            Component tab = runTab.getComponentAt(i);
            if (tab.getName() != null && tab.getName().equals(name)) {
                runTab.setSelectedIndex(i);
            }
        }
    }

    @Override
    public void showLog() {
    }

    @Override
    public void showScope() {
    }

    @Override
    public void showDebugMenu() {
        debugPanel.setVisible(true);
    }

    @Override
    public void hideDebugMenu() {
        debugPanel.setVisible(false);
    }

    @Override
    public void scopeViewerDisplayScope(RuntimeContext context, Scope scope) {
        scopeViewer.displayScope(context, scope);
    }

    @Override
    public void editorHighlight(int from, int to) {
        editor.highlight(from, to);
    }

    @Override
    public void editorHighlightError(int from, int to) {
        editor.highlightError(from, to);
        editorSetSelection(from);
    }

    @Override
    public void editorSetSelection(int pos) {
        editor.setSelection(pos);
        editor.requestFocusInWindow();
    }

    @Override
    public void editorClearHighlight() {
        editor.clearHighlight();
    }

    @Override
    public boolean editorIsLineHasBreakPoint(int line) {
        return editor.isLineHasBreakPoint(line);
    }

    @Override
    public void editorSpeech(String message, int from, int to) {
        editor.addSpeech(message, from, to);
    }

    @Override
    public void editorClearSpeech() {
        editor.clearSpeechs();
    }
}
