/*
 YANN CARON CONFIDENTIAL
 __________________

 Yann Caron Copyright (c) 2011
 All Rights Reserved.
 __________________

 NOTICE:  All information contained herein is, and remains
 the property of Yann Caron and its suppliers, if any.
 The intellectual and technical concepts contained
 herein are proprietary to Yann Caron
 and its suppliers and may be covered by U.S. and Foreign Patents,
 patents in process, and are protected by trade secret or copyright law.
 Dissemination of this information or reproduction of this material
 is strictly forbidden unless prior written permission is obtained
 from Yann Caron.
 */
package fr.cyann.al;

import fr.cyann.al.ast.Program;
import fr.cyann.al.library.ALLib;
import fr.cyann.al.visitor.AbstractRuntime;
import fr.cyann.jasi.builder.ASTBuilder;
import fr.cyann.jasi.parser.PEG;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.jasi.builder.InterpreterBuilder;

/**
 * The AL class. Main project class.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class AL implements Constants {

    // hide constructor according sonar rule
    private AL() {
    }

    public static void setProgramToBlock(InterpreterBuilder builder) {
        Program<RuntimeContext> program = new Program<RuntimeContext>();

        while (!builder.isEmpty()) {
            program.addStatement(builder.poll());
        }

        builder.push(program);
    }

    /**
     * Create context with api
     *
     * @param syntax the parser used to parse the source code.
     * @param runtime the device implemented execution visitor.
     * @param libs
     * @return the created context
     */
    public static RuntimeContext createContext(PEG syntax, AbstractRuntime runtime, ALLib... libs) {
        // create context
        syntax.initalize();
        RuntimeContext context = new RuntimeContext(syntax, runtime);

        // init runtime resources
        ASTBuilder builderAPI = new ASTBuilder();
        runtime.initialize(builderAPI, context);

        // load additional libraries
        if (libs != null) {
            for (ALLib lib : libs) {
                lib.addFrameworkObjects(builderAPI);
                lib.addDynamicMethods(context, runtime.getDynamicMethods());
            }
        }

        // construct API
        setProgramToBlock(builderAPI);
        builderAPI.injectVisitor(runtime).visite(context);

        return context;
    }

    /**
     * Execute the source code from PEG parser.
     *
     * @param syntax the parser used to parse the source code.
     * @param runtime the device implemented execution visitor.
     * @param context the program context
     * @param source the source code to execute.
     * @return the root scope result.
     */
    public static RuntimeContext execute(PEG syntax, AbstractRuntime runtime, RuntimeContext context, String source) {

        ASTBuilder builder = new ASTBuilder();

        // parse
        syntax.parse(source, builder);
        setProgramToBlock(builder);

        // clear gc before execution
        System.gc();

        // execution phase
        builder.injectVisitor(runtime).visite(context);

        return context;
    }

    /**
     * Execute the source code from PEG parser.
     *
     * @param syntax the parser used to parse the source code.
     * @param runtime the device implemented execution visitor.
     * @param source the source code to execute.
     * @param libs the libraries to load
     * @return the root scope result.
     */
    public static RuntimeContext execute(PEG syntax, AbstractRuntime runtime, String source, ALLib... libs) {

        // create context
        RuntimeContext context = createContext(syntax, runtime, libs);

        execute(syntax, runtime, context, source);

        return context;
    }

}
