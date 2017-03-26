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

import fr.cyann.al.AL;
import fr.cyann.al.exception.ALRuntimeException;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.al.visitor.RuntimeVisitor;
import fr.cyann.algoide.algo.Algo;
import fr.cyann.algoide.algo.AlgoLogger;
import fr.cyann.algoide.algo.TextInvit;
import fr.cyann.algoide.panels.AlgoIDEPanel;
import fr.cyann.algoide.runtime.DebugExecution.Action;
import fr.cyann.jasi.exception.JASIException;
import fr.cyann.jasi.lexer.Token;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * The TaskManager class. Creation date: 23 mai 2013.
 *
 * @author CyaNn
 * @version v0.1
 */
public class TaskManager extends Thread {

    // constants
    public static String INFO_INTERRUPTED = "Interpreter thread interrupted !";
    public static String ERR_INDEX_OUT_OF_BOUNDS = "Index out of bounds !";
    public static String ERR_UNEXPECTED = "UNEXPECTED EXCEPTION !";
    public static String ERR_STACK_OVERFLOW = "Stack overflow !";
    // attributes
    private final Queue<Runnable> tasks;
    private final List<Thread> threads;
    private boolean debugMode = false, stepByStepMode = false;
    // runtime
    private final Syntax syntax;
    private final RuntimeVisitor base;
    private RuntimeVisitor exec;
    private final DebugExecution debugExec;
    private final AlgoIDEPanel panel;
    private final AlgoLogger logger;
    private final Algo algo;
    private boolean stopping = false;
    private boolean finished = false;

    // constructor
    public TaskManager(AlgoIDEPanel panel, Algo algo, TextInvit invit, AlgoLogger logger) {
        this.tasks = new LinkedBlockingQueue<Runnable>();
        this.threads = new LinkedList<Thread>();

        this.syntax = new Syntax();
        this.syntax.initalize();
        this.base = new ALJavaVM(panel, algo, invit, logger, this);
        this.debugExec = new DebugExecution(base, this, panel);

        this.algo = algo;

        loadRuntime();
        this.panel = panel;
        this.logger = logger;

        start();
    }

    // private
    public boolean isFree() {
        return tasks.isEmpty();
    }

    public boolean isStopping() {
        return stopping;
    }

    private void showError(Token token) {
        panel.editorHighlightError(token.getPos(), token.getPos() + token.getLength());
        panel.editorSetSelection(token.getPos());
    }

    // attributs
    private void loadRuntime() {
        this.exec = this.base;

        if (debugMode) {
            this.exec = this.debugExec;
        }

        if (stepByStepMode) {
            this.exec = new StepByStepExecution(this.exec, panel.getStepSpeed(), panel);
        }
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public void setStepByStepMode(boolean stepByStepMode) {
        this.stepByStepMode = stepByStepMode;
    }

    public Syntax getSyntax() {
        return syntax;
    }

    public RuntimeVisitor getExec() {
        return exec;
    }

    // methods
    @Override
    public synchronized void run() {
        Runnable currentTask;

        while (!finished) {
            try {

                // wait until start
                this.wait();

                stopping = false;
                if (!tasks.isEmpty()) {
                    while (!tasks.isEmpty()) {
                        currentTask = tasks.poll();
                        currentTask.run();
                    }
                }
            } catch (InterruptedException ex) {
                algo.clearEvents();
                tasks.clear();
                Logger.getLogger(TaskManager.class.getName()).log(Level.INFO, INFO_INTERRUPTED);
            } catch (RuntimeInterruptedExeption ex) {
                tasks.clear();
                Logger.getLogger(TaskManager.class.getName()).log(Level.INFO, INFO_INTERRUPTED);
            } catch (ALRuntimeException ex) {
                algo.clearEvents();
                tasks.clear();
                logger.err(ex.getMessage());
                ex.printStackTrace();
                showError(ex.getToken());
                if (!ex.isSilent()) {
                    panel.ExceptionPopup(ex.getMessage());
                }
            } catch (JASIException ex) {
                algo.clearEvents();
                tasks.clear();
                logger.err(ex.getMessage());
                ex.printStackTrace();
                panel.ExceptionPopup(ex.getMessage());
            } catch (StackOverflowError ex) {
                algo.clearEvents();
                tasks.clear();
                logger.err(ERR_STACK_OVERFLOW);
                Logger.getLogger(TaskManager.class.getName()).log(Level.WARNING, ERR_STACK_OVERFLOW);
                ex.printStackTrace();
                panel.ExceptionPopup(ERR_STACK_OVERFLOW);
            } catch (Exception ex) {
                algo.clearEvents();
                tasks.clear();
                logger.err(ERR_UNEXPECTED + " " + ex.getMessage());
                Logger.getLogger(TaskManager.class.getName()).log(Level.WARNING, ERR_UNEXPECTED);
                ex.printStackTrace();
                panel.ExceptionPopup(ERR_UNEXPECTED);
            }
        }

    }

    public synchronized void postWeakSource(final String task) {
        loadRuntime();

        postWeakTask(new Runnable() {
            @Override
            public void run() {
                RuntimeContext context = AL.execute(syntax, exec, task);
                panel.scopeViewerDisplayScope(context, context.getRoot());
                panel.hideDebugMenu();
                panel.editorClearHighlight();
                postDelayedTask(new Runnable() {

                    @Override
                    public void run() {
                        panel.editorClearSpeech();
                    }
                }, 1500);

            }
        });
    }

    public synchronized void postWeakTask(Runnable task) {
        if (isFree()) {
            postTask(task);
        }
    }

    public synchronized void postDelayedTask(final Runnable task, final long delay) {
        new Thread() {
            @Override
            public void run() {
                try {
                    TaskManager.this.threads.add(this);
                    Thread.sleep(delay);
                    postTask(task);
                } catch (InterruptedException ex) {
                    throw new RuntimeInterruptedExeption(ex);
                } finally {
                    TaskManager.this.threads.remove(this);
                }
            }
        }.start();

    }

    public synchronized void postTask(Runnable task) {
        tasks.add(task);
        executeTask();
    }

    public synchronized void executeTask() {
        this.notify();
    }

    public void clearTasks() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
        threads.clear();
        tasks.clear();
        stopping = true;
    }

    public void terminateTasks() {
        base.clearRessources();
        clearTasks();
        this.interrupt();
    }

    public void finish() {
        terminateTasks();
        finished = true;
    }

    /**
     * Control the debugger and goto the next token in source code.
     */
    public void gotoNextToken() {
        debugExec.continueRunning(Action.NEXT_TOKEN);
    }

    /**
     * Control the debugger and goto the next line in source code.
     */
    public void gotoNextLine() {
        debugExec.continueRunning(Action.NEXT_LINE);
    }

    /**
     * Control the debugger and continue execution.
     */
    public void continueDebug() {
        debugExec.continueRunning(Action.CONTINUE);
    }
}
