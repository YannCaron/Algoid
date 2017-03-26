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
package fr.cyann.algoide.runtime;

import fr.cyann.al.ast.Block;
import fr.cyann.al.ast.declaration.ObjectDeclaration;
import fr.cyann.al.data.FunctionInstance;
import fr.cyann.al.data.Identifiers;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.data.ObjectInstance;
import fr.cyann.al.exception.ALRuntimeException;
import fr.cyann.al.factory.DeclarationFactory;
import fr.cyann.al.factory.ExpressionFactory;
import fr.cyann.al.factory.FactoryUtils;
import fr.cyann.al.factory.TypeNameFunctionMap;
import fr.cyann.al.scope.Scope;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.al.visitor.RuntimeVisitor;
import static fr.cyann.al.visitor.RuntimeVisitor.callbackFunction;
import static fr.cyann.al.visitor.RuntimeVisitor.getCallback;
import static fr.cyann.al.visitor.RuntimeVisitor.returnValue;
import static fr.cyann.al.visitor.RuntimeVisitor.setOptionalParameter;
import fr.cyann.algoide.AlgoIDE;
import fr.cyann.algoide.algo.Algo;
import fr.cyann.algoide.algo.AlgoLogger;
import fr.cyann.algoide.algo.TextInvit;
import fr.cyann.algoide.lang.LangUI;
import fr.cyann.algoide.panels.AlgoIDEPanel;
import fr.cyann.algoide.plugin.ApplicationContext;
import fr.cyann.jasi.builder.ASTBuilder;
import fr.cyann.jasi.exception.MultilingMessage;
import fr.cyann.jasi.visitor.MethodVisitor;
import fr.cyann.tools.FileTools;
import java.awt.Color;
import java.awt.KeyEventDispatcher;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.Thread.State;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * The ALJavaVM class. Creation date: 23 mai 2013.
 * <p>
 * @author CyaNn
 * @version v0.1
 */
public class ALJavaVM extends RuntimeVisitor {

    public static final int MAX_SPEED_MS = 30;
    private final TaskManager taskManager;
    private final AlgoIDEPanel panel;
    private final Algo algoPanel;
    private final TextInvit invitPanel;
    private final AlgoLogger loggerPanel;

    public ALJavaVM(AlgoIDEPanel panel, Algo algoPanel, TextInvit invitPanel, AlgoLogger logger, TaskManager taskManager) {
        this.panel = panel;
        this.algoPanel = algoPanel;
        this.invitPanel = invitPanel;
        this.loggerPanel = logger;
        this.taskManager = taskManager;

        AlgoIDE.PLUGIN_LOADER.setAppContext(new ApplicationContext(taskManager, panel, algoPanel, invitPanel, loggerPanel));
    }

    private GeneralPath getPath(RuntimeContext context, Block<RuntimeContext> ast) {
        MutableVariant p1 = ast.function.decl.params.get(0).var.mv;
        MutableVariant close = ast.function.decl.params.get(1).var.mv;

        final GeneralPath path = new GeneralPath();
        int size = p1.size();
        if (size > 1) {
            path.moveTo(p1.getValue(0).getNumber(), p1.getValue(1).getNumber());
            for (int i = 2; i < size; i += 2) {
                path.lineTo(p1.getValue(i).getNumber(), p1.getValue(i + 1).getNumber());
            }
        }
        if (!close.isBool() || close.getBool()) {
            path.closePath();
        }

        return path;
    }

    private GeneralPath getCurve(RuntimeContext context, Block<RuntimeContext> ast) {
        MutableVariant p1 = ast.function.decl.params.get(0).var.mv;
        MutableVariant close = ast.function.decl.params.get(1).var.mv;

        final GeneralPath path = new GeneralPath();
        int size = p1.size();
        if (size > 1) {
            path.moveTo(p1.getValue(0).getNumber(), p1.getValue(1).getNumber());
            //path.quadTo(p1.getValue(size - 1).getNumber(), p1.getValue(size).getNumber(), p1.getValue(0).getNumber(), p1.getValue(1).getNumber());
            for (int i = 4; i < size; i += 2) {
                path.quadTo(p1.getValue(i - 2).getNumber(), p1.getValue(i - 1).getNumber(), p1.getValue(i).getNumber(), p1.getValue(i + 1).getNumber());
            }
        }
        if (!close.isBool() || close.getBool()) {
            //path.quadTo(p1.getValue(size - 1).getNumber(), p1.getValue(size).getNumber(), p1.getValue(0).getNumber(), p1.getValue(1).getNumber());
            path.closePath();
        }

        return path;
    }

    private synchronized void mouseEvent(final RuntimeContext context, final Block<RuntimeContext> ast, final MouseListener listener, final FunctionInstance func, final float eX, final float eY) {

        if (taskManager.getState() == State.WAITING) {
            taskManager.postWeakTask(new Runnable() {

                @Override
                public void run() {
                    setOptionalParameter(func, 0, eX);
                    setOptionalParameter(func, 1, eY);
                    boolean ret = callbackFunction(context, func, returnValue);
                    if (ret) {
                        algoPanel.removeMouseListener(listener);
                    }
                }
            });
        }

    }

    private synchronized void mouseEventMotion(final RuntimeContext context, final Block<RuntimeContext> ast, final MouseMotionListener listener, final FunctionInstance func, final float eX, final float eY) {

        if (taskManager.getState() == State.WAITING) {
            taskManager.postWeakTask(new Runnable() {

                @Override
                public void run() {
                    setOptionalParameter(func, 0, eX);
                    setOptionalParameter(func, 1, eY);
                    boolean ret = callbackFunction(context, func, returnValue);
                    if (ret) {
                        algoPanel.removeMouseMotionListener(listener);
                    }
                }
            });
        }

    }

    private synchronized void mouseEventWheel(final RuntimeContext context, final Block<RuntimeContext> ast, final MouseWheelListener listener, final FunctionInstance func, final int rotation) {

        if (taskManager.getState() == State.WAITING) {
            taskManager.postWeakTask(new Runnable() {

                @Override
                public void run() {
                    setOptionalParameter(func, 0, rotation);
                    boolean ret = callbackFunction(context, func, returnValue);
                    if (ret) {
                        algoPanel.removeMouseWheelListener(listener);
                    }
                }
            });
        }

    }

    private synchronized void keyEvent(final RuntimeContext context, final Block<RuntimeContext> ast, final KeyEventDispatcher listener, final FunctionInstance func, final char key) {

        algoPanel.setFocusable(true);
        algoPanel.requestFocus();

        if (taskManager.getState() == State.WAITING) {
            taskManager.postWeakTask(new Runnable() {

                @Override
                public void run() {
                    setOptionalParameter(func, 0, String.valueOf(key));
                    boolean ret = callbackFunction(context, func, returnValue);
                    if (ret) {
                        algoPanel.removeKeyListener(listener);
                    }
                }
            });
        }

    }

    private static Color[][] toColorArray(MutableVariant var) {

        if (var.size() > 0) {
            int w = var.getValue(0).size();

            Color[][] rows = new Color[var.size()][];
            for (int y = 0; y < var.size(); y++) {
                Color[] row = new Color[w];
                for (int x = 0; x < w; x++) {
                    row[x] = Colors.valueOf((int) var.getValue(y).getValue(x).getNumber()).getColor();
                }
                rows[y] = row;
            }
            return rows;
        }

        return new Color[0][];
    }

    private static Color[][] toColorArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Color[][] result = new Color[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {

                int argb = image.getRGB(col, row);
                result[row][col] = new Color(argb);
            }
        }

        return result;
    }

    private static float toDecimal(float in) {
        System.out.println(((in % 256f) / 255f));
        return (in % 255.5f) / 255.0f;
    }

    private void showInput(final RuntimeContext context, String message, TextInvit.KeyValidator validator) {
        invitPanel.addInputText(message, new TextInvit.TextListener() {

            @Override
            public void Validated(String text) {
                context.returnValue(text);

                synchronized (ALJavaVM.this) {
                    ALJavaVM.this.notify();
                }
            }
        }, validator);

        synchronized (ALJavaVM.this) {
            try {
                ALJavaVM.this.wait();
            } catch (InterruptedException ex) {
                throw new RuntimeInterruptedExeption(ex);
            }
        }
    }

    @Override
    public void addDynamicMethods(RuntimeContext context, TypeNameFunctionMap dynamicMethods) {
        super.addDynamicMethods(context, dynamicMethods);

        // add plugins magic methods
        AlgoIDE.PLUGIN_LOADER.addDynamicMethods(context, dynamicMethods);
    }

    @Override
    public void addFrameworkObjects(ASTBuilder builder) {
        super.addFrameworkObjects(builder);

        // root
        // PRINT important, keep it
        builder.push(DeclarationFactory.factory("print", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final String message = ast.function.decl.params.get(0).var.mv.getString(context);
                panel.showTextPanel();
                invitPanel.addOutput(message);
            }
        }, "p1"));

        // util
        ObjectDeclaration<RuntimeContext> util = FactoryUtils.findObject(builder, "util");

        util.addDeclaration(DeclarationFactory.factory("wait", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                int timeout = (int) ast.function.decl.params.get(0).var.mv.getNumber();

                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    throw new RuntimeInterruptedExeption(e);
                }

            }
        }, "timeout"));

        util.addDeclaration(DeclarationFactory.factory("pulse", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, final RuntimeContext context) {
                final FunctionInstance function = getCallback(ast, 0);
                int timeout = (int) ast.function.decl.params.get(1).var.mv.getNumber();
                if (timeout < MAX_SPEED_MS) {
                    timeout = MAX_SPEED_MS;
                }
                final int to = timeout;

                // optional parameter c
                final int co = (int) getOptionalParameter(ast, 2, -1);
                final FunctionInstance f = getOptionalCallBack(ast, 3);

                taskManager.postTask(new Runnable() {

                    private int count = 0;

                    @Override
                    public void run() {
                        setOptionalParameter(function, 0, count);
                        boolean ret = callbackFunction(context, function, returnValue);

                        count++;
                        if (!ret && (co == -1 || count < co) && !taskManager.isStopping()) {
                            taskManager.postDelayedTask(this, to);
                        } else if (f != null) {
                            callbackFunction(context, f, returnValue);
                        }
                    }
                });
            }
        }, "function", "timeout", "count", "after"));

        util.addDeclaration(DeclarationFactory.factory("notice", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, final RuntimeContext context) {
                final FunctionInstance function = getCallback(ast, 0);
                int timeout = (int) ast.function.decl.params.get(1).var.mv.getNumber();
                if (timeout < MAX_SPEED_MS) {
                    timeout = MAX_SPEED_MS;
                }
                taskManager.postDelayedTask(new Runnable() {

                    @Override
                    public void run() {
                        callbackFunction(context, function, returnValue);
                    }
                }, timeout);
            }
        }, "function", "timeout"));

        util.addDeclaration(DeclarationFactory.factory("pullOn", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, final RuntimeContext context) {
                final MutableVariant functions = ast.function.decl.params.get(0).var.mv;
                int timeout = (int) ast.function.decl.params.get(1).var.mv.getNumber();
                if (timeout < MAX_SPEED_MS) {
                    timeout = MAX_SPEED_MS;
                }
                final int to = timeout;

                taskManager.postTask(new Runnable() {

                    private int index = 0;

                    @Override
                    public void run() {
                        FunctionInstance func = functions.getValue(index).getFunction();
                        func.enclosing = func.enclosing.cloneWhile(PREDICAT_WHILE_IN_FUNCTION);

                        boolean ret = callbackFunction(context, func, returnValue);

                        index++;
                        if (!ret && index < functions.size() && !taskManager.isStopping()) {
                            taskManager.postDelayedTask(this, to);
                        }
                    }
                });
            }
        }, "function", "timeout"));

        util.addDeclaration(DeclarationFactory.factory("clearTasks", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, final RuntimeContext context) {
                taskManager.clearTasks();
                algoPanel.clearEvents();
            }
        }));

        util.addDeclaration(DeclarationFactory.factory("log", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                String message = ast.function.decl.params.get(0).var.mv.getString(context);
                Logger.getLogger(ALJavaVM.class.getName()).log(Level.INFO, message);
                loggerPanel.info(message);
            }
        }, "message"));

        util.addDeclaration(DeclarationFactory.factory("warn", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                String message = ast.function.decl.params.get(0).var.mv.getString(context);
                Logger.getLogger(ALJavaVM.class.getName()).log(Level.WARNING, message);
                loggerPanel.warn(message);
            }
        }, "source"));

        util.addDeclaration(DeclarationFactory.factory("err", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                String message = ast.function.decl.params.get(0).var.mv.getString(context);
                Logger.getLogger(ALJavaVM.class.getName()).log(Level.SEVERE, message);
                loggerPanel.err(message);
            }
        }, "source"));

        util.addDeclaration(DeclarationFactory.factory("toClipboard", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                String text = ast.function.decl.params.get(0).var.mv.getString(context);

                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(text), null);
            }
        }, "source"));

        util.addDeclaration(DeclarationFactory.factory("fromClipboard", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                try {
                    String text = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);

                    context.returnValue(text);
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(ALJavaVM.class.getName()).log(Level.SEVERE, ex.getMessage());
                } catch (IOException ex) {
                    Logger.getLogger(ALJavaVM.class.getName()).log(Level.SEVERE, ex.getMessage());
                }
            }
        }));

        // ui
        ObjectDeclaration<RuntimeContext> ui = ExpressionFactory.object("ui");
        builder.push(DeclarationFactory.factory(ui));

        ui.addDeclaration(DeclarationFactory.factory("message", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final String message = ast.function.decl.params.get(0).var.mv.getString(context);
                JOptionPane.showMessageDialog(panel, message, "algo message", JOptionPane.INFORMATION_MESSAGE);
            }
        }, "message"));

        ui.addDeclaration(DeclarationFactory.factory("showMenu", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {
            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                String message = String.format(LangUI.getInstance().getString("msg_compatibility"), "showMenu");
                Logger.getLogger(ALJavaVM.class.getName()).log(Level.WARNING, message);
                loggerPanel.warn(message);
            }
        }));

        ui.addDeclaration(DeclarationFactory.factory("hideMenu", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {
            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                String message = String.format(LangUI.getInstance().getString("msg_compatibility"), "hideMenu");
                Logger.getLogger(ALJavaVM.class.getName()).log(Level.WARNING, message);
                loggerPanel.warn(message);
            }
        }));

        ui.addDeclaration(DeclarationFactory.factory("fullScreen", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                panel.fullScreen();
            }
        }));

        ui.addDeclaration(DeclarationFactory.factory("halfScreen", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                panel.halfScreen();
            }
        }));

        ui.addDeclaration(DeclarationFactory.factory("miniScreen", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                panel.miniScreen();
            }
        }));

        ui.addDeclaration(DeclarationFactory.factory("showText", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                panel.showTextPanel();
            }
        }));

        ui.addDeclaration(DeclarationFactory.factory("showAlgo", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                panel.showAlgoPanel();
            }
        }));

        ui.addDeclaration(DeclarationFactory.factory("showScope", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                panel.showScope();
            }
        }));

        ui.addDeclaration(DeclarationFactory.factory("showLog", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                panel.showLog();
            }
        }));

        ui.addDeclaration(DeclarationFactory.factory("clearLog", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                loggerPanel.clear();
            }
        }));

        // text
        ObjectDeclaration<RuntimeContext> text = ExpressionFactory.object("text");
        builder.push(DeclarationFactory.factory(text));

        text.addDeclaration(DeclarationFactory.factory("clear", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                invitPanel.clear();
            }
        }));

        text.addDeclaration(DeclarationFactory.factory("output", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final String message = ast.function.decl.params.get(0).var.mv.getString(context);
                panel.showTextPanel();
                invitPanel.addOutput(message);
            }
        }, "p1"));

        text.addDeclaration(DeclarationFactory.factory("inputText", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, final RuntimeContext context) {
                final String message = ast.function.decl.params.get(0).var.mv.getString(context);
                panel.showTextPanel();
                showInput(context, message, TextInvit.STRING_VALIDATOR);
            }
        }, "p1"));

        text.addDeclaration(DeclarationFactory.factory("inputNumber", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, final RuntimeContext context) {
                final String message = ast.function.decl.params.get(0).var.mv.getString(context);
                panel.showTextPanel();
                showInput(context, message, TextInvit.NUMBER_VALIDATOR);

                // cast to number
                context.returnValue(context.returnValue.getNumber());
            }
        }, "p1"));

        // algo
        ObjectDeclaration<RuntimeContext> algo = ExpressionFactory.object("algo");
        builder.push(DeclarationFactory.factory(algo));

        algo.addDeclaration(DeclarationFactory.factory("setBgColor", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                panel.showAlgoPanel();
                algoPanel.setBgColor(Colors.valueOf((int) p1).getColor());
            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("setBgRGB", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                final float p2 = ast.function.decl.params.get(1).var.mv.getNumber();
                final float p3 = ast.function.decl.params.get(2).var.mv.getNumber();
                panel.showAlgoPanel();
                algoPanel.setBgColor(new Color(toDecimal(p1), toDecimal(p2), toDecimal(p3)));
            }
        }, "p1", "p2", "p3"));

        algo.addDeclaration(DeclarationFactory.factory("setColor", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                algoPanel.setColor(Colors.valueOf((int) p1).getColor());
            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("setRGB", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                final float p2 = ast.function.decl.params.get(1).var.mv.getNumber();
                final float p3 = ast.function.decl.params.get(2).var.mv.getNumber();
                algoPanel.setColor(new Color(toDecimal(p1), toDecimal(p2), toDecimal(p3)));
            }
        }, "p1", "p2", "p3"));

        algo.addDeclaration(DeclarationFactory.factory("setAlpha", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                panel.showAlgoPanel();
                algoPanel.setAlpha(p1);
            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("setTextSize", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                panel.showAlgoPanel();
                algoPanel.setTextSize(p1);
            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("setStroke", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                panel.showAlgoPanel();
                algoPanel.setStroke(p1);
            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("setStack", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                panel.showAlgoPanel();
                algoPanel.setMaxShapes((int) p1);
            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("getTop", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                context.returnValue(-algoPanel.getHeight() / 2);
            }
        }));

        algo.addDeclaration(DeclarationFactory.factory("getBottom", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                context.returnValue(algoPanel.getHeight() / 2);
            }
        }));

        algo.addDeclaration(DeclarationFactory.factory("getLeft", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                context.returnValue(-(algoPanel.getWidth() * 1.4f) / 2);
            }
        }));

        algo.addDeclaration(DeclarationFactory.factory("getRight", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                context.returnValue((algoPanel.getWidth() * 1.4f) / 2);
            }
        }));

        algo.addDeclaration(DeclarationFactory.factory("getWidth", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                context.returnValue(algoPanel.getWidth() * 1.4f);
            }
        }));

        algo.addDeclaration(DeclarationFactory.factory("getHeight", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                context.returnValue(algoPanel.getHeight() * 1.4f);
            }
        }));

        algo.addDeclaration(DeclarationFactory.factory("getX", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                context.returnValue(algoPanel.getTurtleX());
            }
        }));

        algo.addDeclaration(DeclarationFactory.factory("getY", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                context.returnValue(algoPanel.getTurtleY());
            }
        }));

        algo.addDeclaration(DeclarationFactory.factory("getAngle", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                context.returnValue(algoPanel.getAngle());
            }
        }));

        algo.addDeclaration(DeclarationFactory.factory("show", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                panel.showAlgoPanel();
                algoPanel.showTurtle();
            }
        }));

        algo.addDeclaration(DeclarationFactory.factory("hide", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                panel.showAlgoPanel();
                algoPanel.hideTurtle();
            }
        }));

        algo.addDeclaration(DeclarationFactory.factory("clear", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                panel.showAlgoPanel();
                algoPanel.clear();
            }
        }));

        algo.addDeclaration(DeclarationFactory.factory("autoClear", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                panel.showAlgoPanel();
                algoPanel.autoClear();
            }
        }));

        algo.addDeclaration(DeclarationFactory.factory("removeFirst", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                panel.showAlgoPanel();
                algoPanel.removeFirst();
            }
        }));

        algo.addDeclaration(DeclarationFactory.factory("removeLast", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                panel.showAlgoPanel();
                algoPanel.removeLast();
            }
        }));

        algo.addDeclaration(DeclarationFactory.factory("goTo", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                final float p2 = ast.function.decl.params.get(1).var.mv.getNumber();
                panel.showAlgoPanel();
                algoPanel.goTo(p1, p2);
            }
        }, "p1", "p2"));

        algo.addDeclaration(DeclarationFactory.factory("lineTo", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                final float p2 = ast.function.decl.params.get(1).var.mv.getNumber();
                panel.showAlgoPanel();
                algoPanel.lineTo(p1, p2);
            }
        }, "p1", "p2"));

        algo.addDeclaration(DeclarationFactory.factory("go", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                panel.showAlgoPanel();
                algoPanel.go(p1);
            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("jump", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                panel.showAlgoPanel();
                algoPanel.jump(p1);
            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("turnLeft", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                panel.showAlgoPanel();
                algoPanel.rotate(algoPanel.getAngle() - p1);
            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("turnRight", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                panel.showAlgoPanel();
                algoPanel.rotate(algoPanel.getAngle() + p1);
            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("rotateTo", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                panel.showAlgoPanel();
                algoPanel.rotate(p1);
            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("circle", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                panel.showAlgoPanel();
                algoPanel.ellipse(p1 / 2, p1 / 2, false);
            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("disc", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                panel.showAlgoPanel();
                algoPanel.ellipse(p1 / 2, p1 / 2, true);
            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("square", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                panel.showAlgoPanel();
                algoPanel.rect(p1, p1, false);
            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("box", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                panel.showAlgoPanel();
                algoPanel.rect(p1, p1, true);
            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("rect", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                final float p2 = ast.function.decl.params.get(1).var.mv.getNumber();
                panel.showAlgoPanel();
                algoPanel.rect(p1, p2, false);
            }
        }, "p1", "p2"));

        algo.addDeclaration(DeclarationFactory.factory("plane", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                final float p2 = ast.function.decl.params.get(1).var.mv.getNumber();
                panel.showAlgoPanel();
                algoPanel.rect(p1, p2, true);
            }
        }, "p1", "p2"));

        algo.addDeclaration(DeclarationFactory.factory("oval", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                final float p2 = ast.function.decl.params.get(1).var.mv.getNumber();
                panel.showAlgoPanel();
                algoPanel.ellipse(p1 / 2, p2 / 2, false);
            }
        }, "p1", "p2"));

        algo.addDeclaration(DeclarationFactory.factory("platter", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                final float p2 = ast.function.decl.params.get(1).var.mv.getNumber();
                panel.showAlgoPanel();
                algoPanel.ellipse(p1 / 2, p2 / 2, true);
            }
        }, "p1", "p2"));

        algo.addDeclaration(DeclarationFactory.factory("path", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final GeneralPath path = getPath(context, ast);
                panel.showAlgoPanel();
                algoPanel.path(path, false);
            }
        }, "p1", "p2"));

        algo.addDeclaration(DeclarationFactory.factory("poly", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final GeneralPath path = getPath(context, ast);
                panel.showAlgoPanel();
                algoPanel.path(path, true);
            }
        }, "p1", "p2"));

        algo.addDeclaration(DeclarationFactory.factory("curve", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final GeneralPath path = getCurve(context, ast);
                panel.showAlgoPanel();
                algoPanel.path(path, false);
            }
        }, "p1", "p2"));

        algo.addDeclaration(DeclarationFactory.factory("curvedPoly", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final GeneralPath path = getCurve(context, ast);
                panel.showAlgoPanel();
                algoPanel.path(path, true);
            }
        }, "p1", "p2"));

        algo.addDeclaration(DeclarationFactory.factory("text", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                final String p1 = ast.function.decl.params.get(0).var.mv.getString(context);
                panel.showAlgoPanel();
                algoPanel.text(p1);
            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("onMove", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(final Block<RuntimeContext> ast, final RuntimeContext context) {
                algoPanel.addMouseMotionListener(new MouseAdapter() {

                    FunctionInstance func = getCallback(ast, 0);

                    @Override
                    public void mouseMoved(MouseEvent e) {
                        super.mouseMoved(e);
                        mouseEventMotion(context, ast, this, func, algoPanel.toRelativeX(e.getX()), algoPanel.toRelativeY(e.getY()));
                    }
                });
            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("onDrag", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(final Block<RuntimeContext> ast, final RuntimeContext context) {
                algoPanel.addMouseListener(new MouseAdapter() {

                    FunctionInstance func = ast.function.decl.params.get(0).var.mv.getFunction();

                    @Override
                    public void mousePressed(MouseEvent e) {
                        super.mousePressed(e);
                        mouseEvent(context, ast, this, func, algoPanel.toRelativeX(e.getX()), algoPanel.toRelativeY(e.getY()));
                    }
                });

                algoPanel.addMouseMotionListener(new MouseAdapter() {

                    FunctionInstance func = getCallback(ast, 0);

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        super.mouseDragged(e);
                        mouseEventMotion(context, ast, this, func, algoPanel.toRelativeX(e.getX()), algoPanel.toRelativeY(e.getY()));
                    }
                });
            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("onTouch", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(final Block<RuntimeContext> ast, final RuntimeContext context) {
                algoPanel.addMouseListener(new MouseAdapter() {

                    FunctionInstance func = ast.function.decl.params.get(0).var.mv.getFunction();

                    @Override
                    public void mousePressed(MouseEvent e) {
                        super.mousePressed(e);
                        mouseEvent(context, ast, this, func, algoPanel.toRelativeX(e.getX()), algoPanel.toRelativeY(e.getY()));
                    }
                });

                algoPanel.addMouseMotionListener(new MouseAdapter() {

                    FunctionInstance func = getCallback(ast, 0);

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        super.mouseDragged(e);
                        mouseEventMotion(context, ast, this, func, algoPanel.toRelativeX(e.getX()), algoPanel.toRelativeY(e.getY()));
                    }
                });
            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("onWheel", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(final Block<RuntimeContext> ast, final RuntimeContext context) {
                algoPanel.addMouseWheelListener(new MouseAdapter() {

                    FunctionInstance func = getCallback(ast, 0);

                    @Override
                    public void mouseWheelMoved(MouseWheelEvent e) {
                        mouseEventWheel(context, ast, this, func, e.getWheelRotation());
                    }
                });
            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("onClick", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(final Block<RuntimeContext> ast, final RuntimeContext context) {

                algoPanel.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mousePressed(MouseEvent e) {

                        FunctionInstance func = getCallback(ast, 0);

                        super.mousePressed(e);
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            mouseEvent(context, ast, this, func, algoPanel.toRelativeX(e.getX()), algoPanel.toRelativeY(e.getY()));
                        }
                    }
                });

            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("onTap", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(final Block<RuntimeContext> ast, final RuntimeContext context) {
                algoPanel.addMouseListener(new MouseAdapter() {
                    FunctionInstance func = getCallback(ast, 0);

                    @Override
                    public void mousePressed(MouseEvent e) {
                        super.mousePressed(e);
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            mouseEvent(context, ast, this, func, algoPanel.toRelativeX(e.getX()), algoPanel.toRelativeY(e.getY()));
                        }
                    }
                });

            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("onRelease", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(final Block<RuntimeContext> ast, final RuntimeContext context) {

                algoPanel.addMouseListener(new MouseAdapter() {

                    FunctionInstance func = getCallback(ast, 0);

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        super.mouseReleased(e);
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            mouseEvent(context, ast, this, func, algoPanel.toRelativeX(e.getX()), algoPanel.toRelativeY(e.getY()));
                        }
                    }
                });

            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("onUp", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(final Block<RuntimeContext> ast, final RuntimeContext context) {
                algoPanel.addMouseListener(new MouseAdapter() {

                    FunctionInstance func = getCallback(ast, 0);

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        super.mouseReleased(e);
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            mouseEvent(context, ast, this, func, algoPanel.toRelativeX(e.getX()), algoPanel.toRelativeY(e.getY()));
                        }
                    }
                });

            }
        }, "p1"));

        algo.addDeclaration(DeclarationFactory.factory("onKey", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(final Block<RuntimeContext> ast, final RuntimeContext context) {

                algoPanel.addKeyListener(new KeyEventDispatcher() {

                    FunctionInstance func = getCallback(ast, 0);

                    @Override
                    public boolean dispatchKeyEvent(KeyEvent e) {
                        if (e.getID() == KeyEvent.KEY_PRESSED) {
                            keyEvent(context, ast, this, func, e.getKeyChar());
                        }
                        return false;
                    }
                });
            }
        }, "p1"));

        ObjectDeclaration<RuntimeContext> algo_color = ExpressionFactory.object("color");
        algo.addDeclaration(DeclarationFactory.factory(algo_color));

        algo_color.addDeclaration(DeclarationFactory.factory("TRANSP", -1f));

        algo_color.addDeclaration(DeclarationFactory.factory("BLACK", 0f));

        algo_color.addDeclaration(DeclarationFactory.factory("DARK_BLUE", 1f));

        algo_color.addDeclaration(DeclarationFactory.factory("DARK_GREEN", 2f));

        algo_color.addDeclaration(DeclarationFactory.factory("DARK_CYAN", 3f));

        algo_color.addDeclaration(DeclarationFactory.factory("DARK_RED", 4f));

        algo_color.addDeclaration(DeclarationFactory.factory("DARK_MAGENTA", 5f));

        algo_color.addDeclaration(DeclarationFactory.factory("BROWN", 6f));

        algo_color.addDeclaration(DeclarationFactory.factory("LIGHT_GRAY", 7f));

        algo_color.addDeclaration(DeclarationFactory.factory("GRAY", 8f));

        algo_color.addDeclaration(DeclarationFactory.factory("BLUE", 9f));

        algo_color.addDeclaration(DeclarationFactory.factory("GREEN", 10f));

        algo_color.addDeclaration(DeclarationFactory.factory("CYAN", 11f));

        algo_color.addDeclaration(DeclarationFactory.factory("RED", 12f));

        algo_color.addDeclaration(DeclarationFactory.factory("MAGENTA", 13f));

        algo_color.addDeclaration(DeclarationFactory.factory("YELLOW", 14f));

        algo_color.addDeclaration(DeclarationFactory.factory("WHITE", 15f));

        ObjectDeclaration<RuntimeContext> algo_stamp = ExpressionFactory.object("stamp");
        algo.addDeclaration(DeclarationFactory.factory(algo_stamp));

        algo_stamp.addDeclaration(DeclarationFactory.factory("id", -1f));

        algo_stamp.addDeclaration(DeclarationFactory.factory("width", -1f));

        algo_stamp.addDeclaration(DeclarationFactory.factory("height", -1f));

        algo_stamp.addDeclaration(DeclarationFactory.factory("clone", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                ObjectInstance self = context.scope.resolve(Identifiers.getID("this")).getObject();
                MutableVariant a = ast.function.decl.params.get(0).var.mv;
                int size = (int) ast.function.decl.params.get(1).var.mv.getNumber();

                ObjectInstance clone = self.clone(context);
                MutableVariant id = clone.scope.resolve(Identifiers.getID("id"));
                MutableVariant width = clone.scope.resolve(Identifiers.getID("width"));
                MutableVariant height = clone.scope.resolve(Identifiers.getID("height"));

                // backup
                Scope backup = context.scope;
                context.scope = clone.scope;

                // create sprite
                final Color[][] colors = toColorArray(a);
                int h = a.size();
                if (h > 0) {
                    int w = colors[0].length;
                    id.setValue(algoPanel.createSprite(colors, size));
                    width.setValue(w * size);
                    height.setValue(h * size);
                }

                // restore
                context.scope = backup;

                context.returnValue(clone);
            }
        }, "p1", "p2"));

        algo_stamp.addDeclaration(DeclarationFactory.factory("load", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                ObjectInstance self = context.scope.resolve(Identifiers.getID("this")).getObject();
                String fileName = ast.function.decl.params.get(0).var.mv.getString(context);

                try {

                    BufferedImage img = FileTools.loadImgRessource(fileName);

                    ObjectInstance clone = self.clone(context);
                    MutableVariant id = clone.scope.resolve(Identifiers.getID("id"));
                    MutableVariant width = clone.scope.resolve(Identifiers.getID("width"));
                    MutableVariant height = clone.scope.resolve(Identifiers.getID("height"));

                    // backup
                    Scope backup = context.scope;
                    context.scope = clone.scope;

                    // create sprite
                    id.setValue(algoPanel.createSprite(img));
                    width.setValue(img.getWidth());
                    height.setValue(img.getHeight());

                    // restore
                    context.scope = backup;

                    context.returnValue(clone);
                } catch (IOException ex) {
                    throw new ALRuntimeException(new MultilingMessage("File [%s] not found !", "Fichier [%s] non trouvé").setArgs(fileName), ast.getToken());
                }
            }
        }, "p1"));

        algo_stamp.addDeclaration(DeclarationFactory.factory("delete", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                ObjectInstance self = context.scope.resolve(Identifiers.getID("this")).getObject();

                // backup
                Scope backup = context.scope;
                context.scope = self.scope;

                final MutableVariant id = context.scope.resolve(Identifiers.getID("id"));
                algoPanel.removeSprite((int) id.getNumber());
                id.setValue(-1f);

                // restore
                context.scope = backup;
            }
        }));

        algo_stamp.addDeclaration(DeclarationFactory.factory("draw", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                ObjectInstance self = context.scope.resolve(Identifiers.getID("this")).getObject();
                panel.showAlgoPanel();

                // backup
                Scope backup = context.scope;
                context.scope = self.scope;

                final int id = (int) context.scope.resolve(Identifiers.getID("id")).getNumber();
                algoPanel.sprite(id);

                // restore
                context.scope = backup;
            }
        }));

        // add plugins frameworkObjects
        AlgoIDE.PLUGIN_LOADER.addFrameworkObjects(builder);

    }
}
