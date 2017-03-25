/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.visitor;

import fr.cyann.al.ast.Assignation;
import fr.cyann.al.ast.While;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.data.Types;
import fr.cyann.al.factory.TypeNameFunctionMap;
import fr.cyann.jasi.ast.AST;
import fr.cyann.al.ast.BinaryOperator;
import fr.cyann.al.ast.Block;
import fr.cyann.al.ast.BooleanExpression;
import fr.cyann.al.ast.Break;
import fr.cyann.al.ast.Comment;
import fr.cyann.al.ast.EvalBlock;
import fr.cyann.al.ast.For;
import fr.cyann.al.ast.If;
import fr.cyann.al.ast.Loop;
import fr.cyann.al.ast.NumberExpression;
import fr.cyann.al.ast.Return;
import fr.cyann.al.ast.StringExpression;
import fr.cyann.al.ast.UnaryOperator;
import fr.cyann.al.ast.Until;
import fr.cyann.al.ast.declaration.FunctionDeclaration;
import fr.cyann.al.ast.declaration.ObjectDeclaration;
import fr.cyann.al.ast.declaration.VariableDeclaration;
import fr.cyann.al.ast.interfaces.Access;
import fr.cyann.al.ast.reference.Call;
import fr.cyann.al.ast.reference.Variable;
import fr.cyann.al.exception.VariableNotFoundException;
import fr.cyann.al.factory.DeclarationFactory;
import fr.cyann.al.factory.ExpressionFactory;
import fr.cyann.jasi.builder.ASTBuilder;
import fr.cyann.jasi.exception.InvalidTokenException;
import fr.cyann.al.scope.NestedScope;
import fr.cyann.al.scope.ParameterScope;
import fr.cyann.al.scope.Scope;
import fr.cyann.al.ast.Expression;
import fr.cyann.al.ast.NullExpression;
import fr.cyann.al.ast.Program;
import fr.cyann.al.ast.ValueExpression;
import fr.cyann.al.ast.declaration.ArrayDeclaration;
import fr.cyann.al.ast.reference.Index;
import fr.cyann.al.ast.interfaces.TailCache;
import fr.cyann.al.data.FunctionInstance;
import fr.cyann.al.data.Identifiers;
import fr.cyann.al.data.Nature;
import fr.cyann.al.data.ObjectInstance;
import fr.cyann.al.data.Wrapper;
import fr.cyann.al.exception.ALRuntimeException;
import fr.cyann.al.exception.InvalidExpressionException;
import fr.cyann.al.json.JSon;
import fr.cyann.al.syntax.ALTools;
import fr.cyann.jasi.ast.interfaces.TraversalFunctor;
import fr.cyann.jasi.exception.MultilingMessage;
import fr.cyann.jasi.scope.AbstractScope;
import fr.cyann.jasi.scope.ChainPredicate;
import fr.cyann.jasi.visitor.MethodVisitor;
import fr.cyann.jasi.visitor.MethodVisitorInjector;
import fr.cyann.jasi.visitor.NameMapVisitorInjector;
import fr.cyann.jasi.visitor.VisitorInjector;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * The Execution class. Creation date: 4 mars 2013.
 * <p>
 * @author CyaNn
 * @version v0.1
 */
public class RuntimeVisitor extends AbstractRuntime {

    private static final boolean VARIABLE_CACHE = true;

    final Set<MqttClient> mqttClients = new HashSet<MqttClient>();

    public static final ChainPredicate PREDICAT_UNTIL_FUNCTION = new ChainPredicate() {

        @Override
        public boolean eval(AbstractScope scope) {
            return (scope instanceof ParameterScope);
        }
    };

    public static final ChainPredicate PREDICAT_WHILE_IN_FUNCTION = new ChainPredicate() {

        @Override
        public boolean eval(AbstractScope scope) {
            boolean ret = !(scope instanceof ParameterScope || scope.equals(scope.getRoot()));
            return ret;
        }
    };

    public static final MutableVariant returnValue = new MutableVariant();
    protected ObjectDeclaration<RuntimeContext> math;

    public static void prepareCall(RuntimeContext context, FunctionInstance f, List<Expression<? extends Expression, RuntimeContext>> args) {

        // backup
        Scope backup = context.scope;

        // resolve parameters
        context.scope = context.call;
        ParameterScope functionScope = f.decl.scope;

        if (functionScope.used) {
            context.cache = false;
        }

        int size = f.decl.params.size();
        for (int i = 0; i < size; i++) {
            VariableDeclaration param = f.decl.params.get(i);
            MutableVariant mv = null;

            if (args.size() > i) {
                Expression arg = args.get(i);

                if (f.decl.lazyArgEvaluation) {
                    try {
                        arg.visite(context);
                    } catch (VariableNotFoundException ex) {
                        param.var.mv.invalid();
                    }
                } else {
                    arg.visite(context);
                }

                mv = arg.getLast().mv;

            } else if (param.expr != null) {
                param.expr.visite(context);
                mv = param.expr.getLast().mv;
            }

            if (mv != null) {
                if (functionScope.used) {
                    functionScope.variables.get(param.var.identifier).setValue(mv);
                } else {
                    param.var.mv.setValue(mv);
                }
            }

        }

        // restore
        context.scope = backup;
    }

    public static void prepareCall(FunctionInstance f, MutableVariant... args) {
        int size = args.length;

        for (int i = 0; i < size; i++) {
            f.decl.params.get(i).var.mv.setValue(args[i]);
        }
    }

    // callback
    public static final FunctionInstance getCallback(Block<RuntimeContext> ast, int index) {
        FunctionInstance func = ast.function.decl.params.get(index).var.mv.getFunction();
        func.enclosing = func.enclosing.cloneWhile(PREDICAT_WHILE_IN_FUNCTION);
        return func;
    }

    // optional parameter
    public static boolean getOptionalParameter(Block<RuntimeContext> ast, int index, boolean defaultValue) {
        MutableVariant parameter = ast.function.decl.params.get(index).var.mv;

        if (!parameter.isNull()) {
            return parameter.getBool();
        }

        return defaultValue;
    }

    public static float getOptionalParameter(Block<RuntimeContext> ast, int index, float defaultValue) {
        MutableVariant parameter = ast.function.decl.params.get(index).var.mv;

        if (!parameter.isNull()) {
            return parameter.getNumber();
        }

        return defaultValue;
    }

    public static int getOptionalParameter(Block<RuntimeContext> ast, int index, int defaultValue) {
        MutableVariant parameter = ast.function.decl.params.get(index).var.mv;

        if (!parameter.isNull()) {
            return (int) parameter.getNumber();
        }

        return defaultValue;
    }

    public static String getOptionalParameter(Block<RuntimeContext> ast, RuntimeContext context, int index, String defaultValue) {
        MutableVariant parameter = ast.function.decl.params.get(index).var.mv;

        if (!parameter.isNull()) {
            return parameter.getString(context);
        }

        return defaultValue;
    }

    public static FunctionInstance getOptionalFunction(Block<RuntimeContext> ast, int index) {
        MutableVariant parameter = ast.function.decl.params.get(index).var.mv;

        if (!parameter.isNull()) {
            return parameter.getFunction();
        }

        return null;
    }

    public static FunctionInstance getOptionalCallBack(Block<RuntimeContext> ast, int index) {
        MutableVariant parameter = ast.function.decl.params.get(index).var.mv;

        if (!parameter.isNull()) {
            FunctionInstance func = parameter.getFunction();
            func.enclosing = func.enclosing.cloneWhile(PREDICAT_WHILE_IN_FUNCTION);
            return func;
        }

        return null;
    }

    public static void setOptionalParameter(FunctionInstance f, int index) {
        if (index < f.decl.params.size()) {
            f.decl.params.get(index).var.mv.convertToVoid();
        }
    }

    public static void setOptionalParameter(FunctionInstance f, int index, boolean value) {
        if (index < f.decl.params.size()) {
            f.decl.params.get(index).var.mv.setValue(value);
        }
    }

    public static void setOptionalParameter(FunctionInstance f, int index, float value) {
        if (index < f.decl.params.size()) {
            //f.decl.scope.variables.get(f.decl.params.get(index).var.identifier).setValue(value);
            f.decl.params.get(index).var.mv.setValue(value);
        }
    }

    public static void setOptionalParameter(FunctionInstance f, int index, String value) {
        if (index < f.decl.params.size()) {
            f.decl.params.get(index).var.mv.setValue(value);
        }
    }

    public static void setOptionalParameter(FunctionInstance f, int index, List<MutableVariant> value) {
        if (index < f.decl.params.size()) {
            f.decl.params.get(index).var.mv.setValue(value);
        }
    }

    public static void setOptionalParameter(FunctionInstance f, int index, FunctionInstance value) {
        if (index < f.decl.params.size()) {
            f.decl.params.get(index).var.mv.setValue(value);
        }
    }

    public static void setOptionalParameter(FunctionInstance f, int index, ObjectInstance value) {
        if (index < f.decl.params.size()) {
            f.decl.params.get(index).var.mv.setValue(value);
        }
    }

    public static void setOptionalParameter(FunctionInstance f, int index, MutableVariant value) {
        if (index < f.decl.params.size()) {
            f.decl.params.get(index).var.mv.setValue(value);
        }
    }

    public static void setOptionalParameterExpression(VariableDeclaration<RuntimeContext> variable, MutableVariant value) {
        if (variable.expr == null || !(variable.expr instanceof ValueExpression)) {
            variable.expr = new ValueExpression<RuntimeContext>();
        }

        ((ValueExpression<RuntimeContext>) variable.expr).mv.setValue(value);
        variable.var.mv.setValue(value);
    }

    private static ObjectInstance merge(RuntimeContext context, ObjectInstance o1, ObjectInstance o2) {
        ObjectDeclaration<RuntimeContext> decl = new ObjectDeclaration<RuntimeContext>(o1.decl.getToken());
        decl.superClasses.addAll(o1.decl.superClasses);
        decl.superClasses.addAll(o2.decl.superClasses);

        decl.attributs.addAll(o1.decl.attributs);
        decl.attributs.addAll(o2.decl.attributs);

        ObjectInstance res = new ObjectInstance(decl, o1.scope.enclosing);
        res.build(context);

        return res;
    }

    private static boolean call(RuntimeContext context, FunctionInstance f, MutableVariant mv) {
        // execute body
        ParameterScope functionScope = f.decl.scope;
        functionScope.enclosing = f.enclosing;

        // backup
        Scope backup = context.scope;
        context.scope = f.decl.scope;

        f.decl.statement.function = f;
        f.decl.statement.visite(context);

        boolean returning = context.returning;

        // restore
        context.scope = backup;

        // manage return
        if (context.returning) {
            MutableVariant ret = context.returnValue;

            mv.setValue(ret);

            if (ret.isObject()) {
                context.scope = ret.getObject().scope;
                context.currentObject = ret.getObject();
            }
        } else {
            // as smalltalk, if don't return, return the enclosing object
            if (f.objectContainer != null) {
                mv.setValue(f.objectContainer);
            }
        }

        context.returning = false;
        context.returnValue.convertToVoid();

        return returning;
    }

    public static void trampoline(RuntimeContext context, MutableVariant mv) {

        Expression tail;

        while (context.trampolineCall != null) {
            tail = context.trampolineCall;
            context.trampolineCall = null;

            // backup
            Scope backup = context.scope;
            Scope backupCall = context.call;
            context.scope = context.trampolineScope;
            context.call = context.trampolineScopeCall;

            // trampoline call
            tail.visite(context);
            mv.setValue(tail.getLast().mv);

            // restore
            context.scope = backup;
            context.call = backupCall;
        }

    }

    public static boolean isTailRecursion(Expression<? extends Expression, RuntimeContext> expr) {

        // is it a call ?
        if (!(expr.next instanceof Call)) {
            return false;
        }

        Call call = ((Call) expr.getLast());

        // was forced by argument detection ?
        if (call.tailCache == TailCache.TAIL) {
            return true;
        } else if (call.tailCache == TailCache.NO_TAIL) {
            return false;
        }

        final Wrapper<Boolean> ret = new Wrapper<Boolean>(true);

        // traverse to find a call as argument
        List<Expression> args = ((Call) expr.getLast()).args;
        int size = args.size();
        for (int i = 0; i < size; i++) {
            args.get(i).depthFirstTraversal(new TraversalFunctor() {

                @Override
                public void traverse(AST ast) {
                    if (ast instanceof Expression && ((Expression) ast).getLast() instanceof Call) {
                        ret.setValue(false);
                    }
                }
            });
        }

        // tail cache
        if (ret.getValue()) {
            call.tailCache = TailCache.TAIL;
        } else {
            call.tailCache = TailCache.NO_TAIL;
        }

        return ret.getValue();
    }

    public static boolean callFunction(RuntimeContext context, FunctionInstance f, MutableVariant mv) {

        boolean ret = call(context, f, mv);
        trampoline(context, mv);
        return ret;

    }

    public static boolean callbackFunction(RuntimeContext context, FunctionInstance f, MutableVariant mv) {

        boolean saveCache = context.cache;
        context.cache = false;

        try {
            boolean ret = call(context, f, mv);
            trampoline(context, mv);
            return ret;
        } finally {
            context.cache = saveCache;
        }
    }

    public static void executeBlock(List<AST<? extends AST, RuntimeContext>> statements, RuntimeContext context) {

        // loop on statements children
        int size = statements.size();
        for (int i = 0; i < size; i++) {
            context.call = context.scope;

            statements.get(i).visite(context);

            if (context.returning || context.breaking) {
                break;
            }
        }
    }

    public static MutableVariant getExpressionOfLastStatement(List<AST<? extends AST, RuntimeContext>> statements) {
        if (statements.size() > 0) {
            AST last = statements.get(statements.size() - 1);
            if (last instanceof Expression) {
                return ((Expression) last).getLast().mv;
            }
        }
        return null;
    }

    private static FunctionInstance functionConcat(RuntimeContext context, FunctionInstance... fs) {
        FunctionDeclaration<RuntimeContext> frdecl = new FunctionDeclaration<RuntimeContext>(fs[0].decl.getToken());

        int sfs = fs.length;

        int use = 0;
        Block<RuntimeContext> block = new Block();

        for (int nfs = 0; nfs < sfs; nfs++) {

            FunctionDeclaration<RuntimeContext> decl = fs[nfs].decl;
            int size = decl.params.size();
            for (int i = 0; i < size; i++) {
                VariableDeclaration<RuntimeContext> var = decl.params.get(i).clone();
                if (!frdecl.isDeclarationExists(var.var)) {
                    frdecl.params.add(decl.params.get(i).clone());
                }
            }

            block.addAllStatements(decl.statement.statements);

            if (nfs == 0) {
                use = fs[nfs].use.count;
            } else {
                use = Math.max(use, fs[nfs].use.count);
            }

        }

        frdecl.setStatement(block);
        frdecl.injectVisitor(context.runtime);
        frdecl.visite(context);

        FunctionInstance fr = new FunctionInstance(frdecl, fs[0].enclosing);
        fr.use.count = use;

        return fr;
    }
    private static int recursivePosition;

    private static void recursiveEachDepthFirst(RuntimeContext context, MutableVariant self, FunctionInstance fc) {

        for (int i = 0; i < self.size(); i++) {
            MutableVariant value = self.getValue(i);

            if (value.isArray()) {
                recursiveEachDepthFirst(context, value, fc);
            } else {
                setOptionalParameter(fc, 0, value);
                setOptionalParameter(fc, 1, recursivePosition);
                setOptionalParameter(fc, 2, value.getKey());

                recursivePosition++;
                callFunction(context, fc, value);
            }

        }
    }

    public static class ComparatorAdapter implements Comparator<MutableVariant> {

        private final RuntimeContext context;
        private final FunctionInstance function;

        @Override
        public int compare(MutableVariant o1, MutableVariant o2) {
            returnValue.setValue(false);
            prepareCall(function, o1, o2);
            callFunction(context, function, returnValue);
            if (returnValue.getBool()) {
                return 1;
            }
            return -1;
        }

        public ComparatorAdapter(RuntimeContext context, FunctionInstance function) {
            this.context = context;
            this.function = function;
        }
    }

    private void clearMqttClients() {
        for (MqttClient client : mqttClients) {
            try {
                if (client.isConnected()) {
                    client.disconnect();
                }
                client.close();
            } catch (MqttException ex) {
                Logger.getLogger(RuntimeVisitor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        mqttClients.clear();
    }
    
    // visitor
    @Override
    public void addVisitors(Map<Class<? extends AST>, VisitorInjector> map) {

        // misc
        map.put(Comment.class, new MethodVisitorInjector<Comment<RuntimeContext>, RuntimeContext>() {

            /**
             * Comment visitor. Skip expression.
             */
            @Override
            public void visite(Comment<RuntimeContext> ast, RuntimeContext context) {
                // do nothing
            }
        });

        // structure description
        map.put(VariableDeclaration.class, new MethodVisitorInjector<VariableDeclaration<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(VariableDeclaration<RuntimeContext> ast, RuntimeContext context) {
                RuntimeContext.currentAST = ast;
                // callBackup
                Scope backup = context.scope;

                // visit the expression first (very important for object method re-assignation)
                if (ast.expr != null) {
                    ast.expr.visite(context);
                    ast.mv.setValue(ast.expr.getLast().mv);
                } else {
                    ast.mv.convertToVoid();
                }

                //get or create the variable
                Expression var = ast.var.getLast();
                if (var instanceof Variable) {
                    ((Variable) var).access = Access.CREATE;
                }
                ast.var.visite(context);

                // if function
                if (ast.mv.isFunction()) {
                    FunctionInstance f = ast.mv.getFunction();
                    f.use.increment();

                    if (f.use.count > 1) {
                        ast.mv.setValue(f.clone());
                    }
                }
                // assign
                ast.var.getLast().mv.setValue(ast.mv);

                if (ast.var.next != null) {
                    context.currentObject.addAttribute(RuntimeVisitor.this, context, ast);
                }

                // restore
                context.scope = backup;
            }
        });

        map.put(FunctionDeclaration.class, new MethodVisitorInjector<FunctionDeclaration<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(FunctionDeclaration<RuntimeContext> ast, RuntimeContext context) {
                RuntimeContext.currentAST = ast;

                // callBackup
                Scope backup = context.scope;

                if (!ast.mv.isFunction()) {
                    // create function
                    if (ast.scope == null) {
                        ast.scope = new ParameterScope(ast.getChainName(), context.scope);
                    }
                    context.scope = ast.scope;

                    // create parameters
                    int size = ast.params.size();
                    for (int i = 0; i < size; i++) {
                        VariableDeclaration<RuntimeContext> param = ast.params.get(i);
                        param.visite(context);
                    }
                }
                context.scope = ast.scope;

                FunctionInstance f = new FunctionInstance(ast, backup);
                f.enclosing = backup;
                ast.mv.setValue(f);

                if (ast.isCloned) {
                    f.use.count = 2;
                }

                // visite next
                if (ast.next != null) {
                    ast.next.visite(context);
                    //ast.mv.setValue(ast.next.mv);
                }
                // restore
                context.scope = backup;
            }
        });

        map.put(ObjectDeclaration.class, new MethodVisitorInjector<ObjectDeclaration<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(ObjectDeclaration<RuntimeContext> ast, RuntimeContext context) {
                RuntimeContext.currentAST = ast;
                // backup
                Scope backupcall = context.call;
                Scope backup = context.scope;

                context.call = context.scope;

                // create first instance
                ObjectInstance object = new ObjectInstance(ast, context.scope);
                object.build(context);
                ast.mv.setValue(object);

                context.scope = ast.mv.getObject().scope;

                // visite next
                if (ast.next != null) {
                    ast.next.visite(context);
                    //ast.mv.setValue(ast.next.mv);
                }

                // restore
                context.call = backupcall;
                context.scope = backup;
            }
        });

        map.put(ArrayDeclaration.class, new MethodVisitorInjector<ArrayDeclaration<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(ArrayDeclaration<RuntimeContext> ast, RuntimeContext context) {
                RuntimeContext.currentAST = ast;

                int size = ast.elements.size();

                ast.mv = new MutableVariant();
                ast.mv.convertToArray();
                ast.mv.toDimention(size - 1);

                // create array
                for (int i = 0; i < size; i++) {
                    Expression item = ast.elements.get(i);
                    item.visite(context);
                    ast.mv.getValue(i).setValue(item.getLast().mv);
                    //ast.mv.setCopyValue(i, item.getLast().mv);

                    if (i < ast.keys.size()) {
                        Expression key = ast.keys.get(i);
                        if (key != null) {
                            key.visite(context);
                            ast.mv.addKey(key.getLast().mv, i);
                            //} else {
                            //	ast.mv.addKey(nil, i);
                        }
                    }
                }

                // visite next
                if (ast.next != null) {
                    ast.next.visite(context);
                }
            }
        });

        // structure using
        map.put(Variable.class, new MethodVisitorInjector<Variable<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Variable<RuntimeContext> ast, RuntimeContext context) {
                RuntimeContext.currentAST = ast;

                // callBackup
                Scope backup = context.scope;
                Scope backupcall = context.call;

                if (!VARIABLE_CACHE || (!context.cache && ast.pointer != null && ast.pointer.getNature() != Nature.LOCAL)) {;
                    //System.out.println("NO CACHE FOR " + ast.partName + " NATURE " + ast.pointer.getNature());
                    ast.mutable = true;
                }

                if (ast.pointer == null || ast.mutable) {
                    ast.mutable = false;
                    //System.out.println("LOAD VARIABLE " + ast.partName);

                    if (ast.access == Access.CREATE) {
                        // if it is a creation, create new
                        ast.access = null;
                        //ast.mutable = true;

                        if (context.scope.isAlreadyDefined(ast.identifier)) {
                            ast.pointer = (MutableVariant) context.scope.variables.get(ast.identifier);
                        } else {
                            ast.pointer = new MutableVariant();
                            context.scope.define(ast.identifier, ast.pointer);
                        }

                        ast.pointer.setSystem(ast.getToken().getPos() == 0);

                    } else {

                        if (ast.pointer != null) {
                            ast.mutable = true;
                        }
                        /*
                         if (ast.access == Access.WRITE && ast.next != null && ast.next instanceof Variable) {
                         ast.access =Access.READ;
                         ((Variable)ast.next).access = Access.WRITE;
                         }*/

                        if (ast.previous == null) {
                            ast.pointer = context.scope.resolve(ast.identifier, ast.access == Access.WRITE);
                        } else {
                            ast.pointer = context.scope.resolve(ast.identifier, true);
                        }

                        if (ast.pointer == null) { // variable not found
                            if (ast.previous == null) {
                                throw new VariableNotFoundException(ast, context.scope);
                            }

                            FunctionInstance f = dynamicMethods.get(ast.previous.mv.getType(), ast.identifier);

                            if (f == null) {
                                throw new VariableNotFoundException(ast, ast.previous.mv.getType(), context.scope);
                                //return;
                            } else {
                                FunctionInstance fi = f.clone();
                                fi.self = ast.previous;
                                ast.pointer = new MutableVariant(fi);
                                ast.mutable = false;
                            }
                        }
                    }

                }

                if (ast.pointer.getNature() == Nature.ATTRIBUTE) {
                    ast.mutable = true;
                }

                if (ast.pointer.isObject()) {
                    context.scope = ast.pointer.getObject().scope;
                    context.currentObject = ast.pointer.getObject();
                }

                ast.mv = ast.pointer;

                if (ast.next != null) {
                    ast.next.visite(context);
                }

                // restore
                context.scope = backup;
                context.call = backupcall;

            }
        });

        map.put(Call.class, new MethodVisitorInjector<Call<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Call<RuntimeContext> ast, RuntimeContext context) {
                RuntimeContext.currentAST = ast;

                ast.function = ast.previous.mv.getFunction();
                ast.function.call = ast;

                // backup
                boolean backupCache = context.cache;

                if (ast.function.use.count > 1) {
                    context.cache = false;
                }

                FunctionDeclaration<RuntimeContext> decl = ast.function.decl;
                ParameterScope backupParameterScope = decl.scope;
                ParameterScope functionScope;
                if (decl.scope.used) {
                    functionScope = decl.scope.clone();
                    functionScope.used = decl.scope.used;
                } else {
                    functionScope = decl.scope;
                }
                decl.scope = functionScope;

                // backup
                Scope backupCall = context.call;
                boolean usedBackup = functionScope.used;
                if (!ast.function.decl.system) {
                    functionScope.used = true;
                }

                context.callAST = ast;
                prepareCall(context, ast.function, ast.args);

                call(context, ast.function, ast.mv);

                // restore
                functionScope.used = usedBackup;
                context.cache = !usedBackup;

                // trampoline
                if (!ast.isTailCall) {
                    trampoline(context, ast.mv);
                }
                ast.isTailCall = false;

                context.call = backupCall;

                // next
                if (ast.next != null && !context.breakCallChain) {
                    ast.next.visite(context);
                }

                context.breakCallChain = false;

                // restore
                context.cache = backupCache;
                decl.scope = backupParameterScope;
            }
        });

        map.put(Index.class, new MethodVisitorInjector<Index<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Index ast, RuntimeContext context) {
                RuntimeContext.currentAST = ast;

                if (ast.previous == null) {
                    throw new InvalidExpressionException(ast);
                }

                // backup
                Scope backup = context.scope;
                Scope backupCall = context.call;
                context.scope = context.call;

                ast.expr.visite(context);
                ast.mv = ast.previous.mv.getValue(ast.expr.getLast().mv);

                context.call = backupCall;

                if (ast.mv == null) {
                    ast.mv = MutableVariant.NIL;
                }

                if (ast.mv.isObject()) {
                    context.scope = ast.mv.getObject().scope;
                    context.currentObject = ast.mv.getObject();
                }

                // next
                if (ast.next != null) {
                    ast.next.visite(context);
                }

                // restore
                context.scope = backup;

            }
        });

        // expression
        map.put(NullExpression.class, new MethodVisitorInjector<NullExpression<RuntimeContext>, RuntimeContext>() {

            /**
             * NumberExpression visitor.
             */
            @Override
            public void visite(NullExpression<RuntimeContext> ast, RuntimeContext context) {
                if (ast.next != null) {
                    ast.next.visite(context);
                    //ast.mv.setValue(ast.next.mv);
                }
            }
        });

        map.put(BooleanExpression.class, new MethodVisitorInjector<BooleanExpression<RuntimeContext>, RuntimeContext>() {

            /**
             * NumberExpression visitor.
             */
            @Override
            public void visite(BooleanExpression<RuntimeContext> ast, RuntimeContext context) {
                ast.mv.setValue(ast.value);

                if (ast.next != null) {
                    ast.next.visite(context);
                    //ast.mv.setValue(ast.next.mv);
                }
            }
        });

        map.put(NumberExpression.class, new MethodVisitorInjector<NumberExpression<RuntimeContext>, RuntimeContext>() {

            /**
             * NumberExpression visitor.
             */
            @Override
            public void visite(NumberExpression<RuntimeContext> ast, RuntimeContext context) {
                ast.mv.setValue(ast.value);

                if (ast.next != null) {
                    ast.next.visite(context);
                }
            }
        });

        map.put(StringExpression.class, new MethodVisitorInjector<StringExpression<RuntimeContext>, RuntimeContext>() {

            /**
             * NumberExpression visitor.
             */
            @Override
            public void visite(StringExpression<RuntimeContext> ast, RuntimeContext context) {
                ast.mv.setValue(ast.value);

                if (ast.next != null) {
                    ast.next.visite(context);
                    //ast.mv.setValue(ast.next.mv);
                }
            }
        });

        map.put(Assignation.class, new NameMapVisitorInjector() {

            /**
             * Assignation visitor. Mapped on ast.getName() value. Based on
             * Visitor / Composite design patterns.
             */
            @Override
            protected Map<String, VisitorInjector> getVisitorMap() {
                Map<String, VisitorInjector> map = new HashMap<String, VisitorInjector>();

                map.put("=", new MethodVisitorInjector<Assignation<RuntimeContext>, RuntimeContext>() {

                    /**
                     * Equals assignation.
                     */
                    @Override
                    public void visite(Assignation<RuntimeContext> ast, RuntimeContext context) {
                        // visit expression
                        ast.expr.visite(context);

                        // visite variable
                        if (ast.var.next == null) {
                            ast.var.access = Access.WRITE;
                        }
                        ast.var.visite(context);

                        // assign
                        ast.var.getLast().mv.setValue(ast.expr.getLast().mv);
                        ast.mv.setValue(ast.expr.getLast().mv);

                        // chain with next
                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put("+=", new MethodVisitorInjector<Assignation<RuntimeContext>, RuntimeContext>() {

                    @Override
                    public void visite(Assignation<RuntimeContext> ast, RuntimeContext context) {
                        // visite
                        ast.expr.visite(context);
                        float v1 = ast.expr.getLast().mv.getNumber();

                        if (ast.var.next == null) {
                            ast.var.access = Access.WRITE;
                        }
                        ast.var.visite(context);

                        // set result
                        float result = ast.var.getLast().mv.getNumber() + v1;
                        ast.var.getLast().mv.setValue(result);
                        ast.mv.setValue(result);

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put("-=", new MethodVisitorInjector<Assignation<RuntimeContext>, RuntimeContext>() {

                    @Override
                    public void visite(Assignation<RuntimeContext> ast, RuntimeContext context) {
                        // visite
                        ast.expr.visite(context);
                        float v1 = ast.expr.getLast().mv.getNumber();

                        if (ast.var.next == null) {
                            ast.var.access = Access.WRITE;
                        }
                        ast.var.visite(context);

                        // set result
                        float result = ast.var.getLast().mv.getNumber() - v1;
                        ast.var.getLast().mv.setValue(result);
                        ast.mv.setValue(result);

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put("*=", new MethodVisitorInjector<Assignation<RuntimeContext>, RuntimeContext>() {

                    @Override
                    public void visite(Assignation<RuntimeContext> ast, RuntimeContext context) {
                        // visite
                        ast.expr.visite(context);
                        float v1 = ast.expr.getLast().mv.getNumber();

                        if (ast.var.next == null) {
                            ast.var.access = Access.WRITE;
                        }
                        ast.var.visite(context);

                        // set result
                        float result = ast.var.getLast().mv.getNumber() * v1;
                        ast.var.getLast().mv.setValue(result);
                        ast.mv.setValue(result);

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put("/=", new MethodVisitorInjector<Assignation<RuntimeContext>, RuntimeContext>() {

                    @Override
                    public void visite(Assignation<RuntimeContext> ast, RuntimeContext context) {
                        // visite
                        ast.expr.visite(context);
                        float v1 = ast.expr.getLast().mv.getNumber();

                        if (ast.var.next == null) {
                            ast.var.access = Access.WRITE;
                        }
                        ast.var.visite(context);

                        // set result
                        float result = ast.var.getLast().mv.getNumber() / v1;
                        ast.var.getLast().mv.setValue(result);
                        ast.mv.setValue(result);

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put("%=", new MethodVisitorInjector<Assignation<RuntimeContext>, RuntimeContext>() {

                    @Override
                    public void visite(Assignation<RuntimeContext> ast, RuntimeContext context) {
                        // visite
                        ast.expr.visite(context);
                        float v1 = ast.expr.getLast().mv.getNumber();

                        if (ast.var.next == null) {
                            ast.var.access = Access.WRITE;
                        }
                        ast.var.visite(context);

                        // set result
                        float result = ast.var.getLast().mv.getNumber() % v1;
                        ast.var.getLast().mv.setValue(result);
                        ast.mv.setValue(result);

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put("..=", new MethodVisitorInjector<Assignation<RuntimeContext>, RuntimeContext>() {

                    @Override
                    public void visite(Assignation<RuntimeContext> ast, RuntimeContext context) {
                        // visite
                        ast.expr.visite(context);
                        String v1 = ast.expr.getLast().mv.getString(context);

                        if (ast.var.next == null) {
                            ast.var.access = Access.WRITE;
                        }
                        ast.var.visite(context);

                        // set result
                        String result = ast.var.getLast().mv.getString(context) + v1;
                        ast.var.getLast().mv.setValue(result);
                        ast.mv.setValue(result);

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put("->=", new MethodVisitorInjector<Assignation, RuntimeContext>() {

                    @Override
                    public void visite(Assignation ast, RuntimeContext context) {
                        // visite
                        ast.expr.visite(context);
                        FunctionInstance f2 = ast.expr.getLast().mv.getFunction();

                        if (ast.var.next == null) {
                            ast.var.access = Access.WRITE;
                        }
                        ast.var.visite(context);
                        FunctionInstance f1 = ast.var.getLast().mv.getFunction();

                        FunctionInstance result = functionConcat(context, f1, f2);

                        ast.var.getLast().mv.setValue(result);
                        ast.mv.setValue(result);

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                return map;
            }

            /**
             * Executed in the case of value not found. Throws exception.
             */
            @Override
            protected VisitorInjector getDefault(AST ast) {
                throw new InvalidTokenException(ast.getToken(), Assignation.class);
            }
        });

        map.put(UnaryOperator.class, new NameMapVisitorInjector() {

            /**
             * UnaryOperation visitor. Mapped on ast.getName() value. Based on
             * Visitor / Composite design patterns.
             */
            @Override
            protected Map<String, VisitorInjector> getVisitorMap() {
                Map<String, VisitorInjector> map = new HashMap<String, VisitorInjector>();

                map.put("!", new MethodVisitorInjector<UnaryOperator<RuntimeContext>, RuntimeContext>() {

                    /**
                     * Unary not operator. ! BooleanExpression;
                     */
                    @Override
                    public void visite(UnaryOperator<RuntimeContext> ast, RuntimeContext context) {
                        ast.right.visite(context);
                        ast.mv.setValue(!ast.right.getLast().mv.getBool());

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put("-", new MethodVisitorInjector<UnaryOperator<RuntimeContext>, RuntimeContext>() {

                    @Override
                    public void visite(UnaryOperator<RuntimeContext> ast, RuntimeContext context) {
                        ast.right.visite(context);
                        ast.mv.setValue(-ast.right.getLast().mv.getNumber());

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put("++", new MethodVisitorInjector<UnaryOperator<RuntimeContext>, RuntimeContext>() {

                    /**
                     * Increment unary operator. NumberExpression ++
                     */
                    @Override
                    public void visite(UnaryOperator<RuntimeContext> ast, RuntimeContext context) {
                        ast.right.visite(context);

                        // set result
                        float result = ast.right.getLast().mv.getNumber() + 1;
                        ast.right.getLast().mv.setValue(result);
                        ast.mv.setValue(result);

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put("--", new MethodVisitorInjector<UnaryOperator<RuntimeContext>, RuntimeContext>() {

                    /**
                     * DeDcrement unary operator. NumberExpression ++
                     */
                    @Override
                    public void visite(UnaryOperator<RuntimeContext> ast, RuntimeContext context) {
                        ast.right.visite(context);

                        // set result
                        float result = ast.right.getLast().mv.getNumber() - 1;
                        ast.right.getLast().mv.setValue(result);
                        ast.mv.setValue(result);

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);

                        }
                    }
                });

                map.put("new", new MethodVisitorInjector<UnaryOperator, RuntimeContext>() {

                    @Override
                    public void visite(UnaryOperator ast, RuntimeContext context) {
                        ast.right.visite(context);
                        ObjectInstance o = ast.right.getLast().mv.getObject();

                        ast.mv.setValue(o.clone(context));

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }

                    }
                });
                return map;
            }

            /**
             * Executed in the case of value not found. Throws exception.
             */
            @Override
            protected VisitorInjector getDefault(AST ast) {
                throw new InvalidTokenException(ast.getToken(), UnaryOperator.class);
            }
        });

        map.put(BinaryOperator.class, new NameMapVisitorInjector() {

            /**
             * BinaryOperator visitor. Mapped on ast.getName() value. Based on
             * Visitor / Composite design patterns.
             */
            @Override
            protected Map<String, VisitorInjector> getVisitorMap() {
                Map<String, VisitorInjector> map = new HashMap<String, VisitorInjector>();

                map.put("&&", new MethodVisitorInjector<BinaryOperator<RuntimeContext>, RuntimeContext>() {

                    @Override
                    public void visite(BinaryOperator<RuntimeContext> ast, RuntimeContext context) {
                        ast.left.visite(context);
                        ast.right.visite(context);

                        ast.mv.setValue(ast.left.getLast().mv.getBool() && ast.right.getLast().mv.getBool());

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put("||", new MethodVisitorInjector<BinaryOperator<RuntimeContext>, RuntimeContext>() {

                    @Override
                    public void visite(BinaryOperator<RuntimeContext> ast, RuntimeContext context) {
                        ast.left.visite(context);
                        ast.right.visite(context);

                        ast.mv.setValue(ast.left.getLast().mv.getBool() || ast.right.getLast().mv.getBool());

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put("==", new MethodVisitorInjector<BinaryOperator<RuntimeContext>, RuntimeContext>() {

                    @Override
                    public void visite(BinaryOperator<RuntimeContext> ast, RuntimeContext context) {
                        ast.left.visite(context);
                        ast.right.visite(context);

                        // set result
                        boolean result = ast.left.getLast().mv.compareTo(ast.right.getLast().mv) == 0;
                        ast.mv.setValue(result);

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put("!=", new MethodVisitorInjector<BinaryOperator<RuntimeContext>, RuntimeContext>() {

                    @Override
                    public void visite(BinaryOperator<RuntimeContext> ast, RuntimeContext context) {
                        ast.left.visite(context);
                        ast.right.visite(context);

                        // set result
                        boolean result = ast.left.getLast().mv.compareTo(ast.right.getLast().mv) != 0;
                        ast.mv.setValue(result);

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);

                        }
                    }
                });

                map.put("<", new MethodVisitorInjector<BinaryOperator<RuntimeContext>, RuntimeContext>() {

                    @Override
                    public void visite(BinaryOperator<RuntimeContext> ast, RuntimeContext context) {
                        ast.left.visite(context);
                        ast.right.visite(context);

                        // set result
                        boolean result = ast.left.getLast().mv.compareTo(ast.right.getLast().mv) < 0;
                        ast.mv.setValue(result);

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put(">", new MethodVisitorInjector<BinaryOperator<RuntimeContext>, RuntimeContext>() {

                    @Override
                    public void visite(BinaryOperator<RuntimeContext> ast, RuntimeContext context) {
                        ast.left.visite(context);
                        ast.right.visite(context);

                        // set result
                        boolean result = ast.left.getLast().mv.compareTo(ast.right.getLast().mv) > 0;
                        ast.mv.setValue(result);

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put("<=", new MethodVisitorInjector<BinaryOperator<RuntimeContext>, RuntimeContext>() {

                    @Override
                    public void visite(BinaryOperator<RuntimeContext> ast, RuntimeContext context) {
                        ast.left.visite(context);
                        ast.right.visite(context);

                        // set result
                        boolean result = ast.left.getLast().mv.compareTo(ast.right.getLast().mv) <= 0;
                        ast.mv.setValue(result);

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put(">=", new MethodVisitorInjector<BinaryOperator<RuntimeContext>, RuntimeContext>() {

                    @Override
                    public void visite(BinaryOperator<RuntimeContext> ast, RuntimeContext context) {
                        ast.left.visite(context);
                        ast.right.visite(context);

                        // set result
                        boolean result = ast.left.getLast().mv.compareTo(ast.right.getLast().mv) >= 0;
                        ast.mv.setValue(result);

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put("+", new MethodVisitorInjector<BinaryOperator<RuntimeContext>, RuntimeContext>() {

                    /**
                     * The plus number binary operator
                     */
                    @Override
                    public void visite(BinaryOperator<RuntimeContext> ast, RuntimeContext context) {
                        ast.left.visite(context);
                        float left = ast.left.getLast().mv.getNumber();
                        ast.right.visite(context);

                        // set result
                        float result = left + ast.right.getLast().mv.getNumber();
                        ast.mv.setValue(result);

                        if (ast.next != null) {
                            ast.next.visite(context);
//							//ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put("-", new MethodVisitorInjector<BinaryOperator<RuntimeContext>, RuntimeContext>() {

                    @Override
                    public void visite(BinaryOperator<RuntimeContext> ast, RuntimeContext context) {
                        ast.left.visite(context);
                        float left = ast.left.getLast().mv.getNumber();
                        ast.right.visite(context);

                        // set result
                        float result = left - ast.right.getLast().mv.getNumber();
                        ast.mv.setValue(result);

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put("*", new MethodVisitorInjector<BinaryOperator<RuntimeContext>, RuntimeContext>() {

                    @Override
                    public void visite(BinaryOperator<RuntimeContext> ast, RuntimeContext context) {
                        ast.left.visite(context);
                        float left = ast.left.getLast().mv.getNumber();
                        ast.right.visite(context);

                        // set result
                        float result = left * ast.right.getLast().mv.getNumber();
                        ast.mv.setValue(result);

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put("/", new MethodVisitorInjector<BinaryOperator<RuntimeContext>, RuntimeContext>() {

                    @Override
                    public void visite(BinaryOperator<RuntimeContext> ast, RuntimeContext context) {
                        ast.left.visite(context);
                        float left = ast.left.getLast().mv.getNumber();
                        ast.right.visite(context);

                        // set result
                        float result = left / ast.right.getLast().mv.getNumber();
                        ast.mv.setValue(result);

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put("%", new MethodVisitorInjector<BinaryOperator<RuntimeContext>, RuntimeContext>() {

                    @Override
                    public void visite(BinaryOperator<RuntimeContext> ast, RuntimeContext context) {
                        ast.left.visite(context);
                        float left = ast.left.getLast().mv.getNumber();
                        ast.right.visite(context);

                        // set result
                        float result = left % ast.right.getLast().mv.getNumber();
                        ast.mv.setValue(result);

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put("..", new MethodVisitorInjector<BinaryOperator<RuntimeContext>, RuntimeContext>() {

                    @Override
                    public void visite(BinaryOperator<RuntimeContext> ast, RuntimeContext context) {
                        ast.left.visite(context);
                        String left = ast.left.getLast().mv.getString(context);
                        ast.right.visite(context);

                        // set result
                        String result = left + ast.right.getLast().mv.getString(context);
                        ast.mv.setValue(result);

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                map.put("->", new MethodVisitorInjector<BinaryOperator<RuntimeContext>, RuntimeContext>() {

                    @Override
                    public void visite(BinaryOperator<RuntimeContext> ast, RuntimeContext context) {
                        ast.left.visite(context);
                        FunctionInstance f1 = ast.left.getLast().mv.getFunction();

                        ast.right.visite(context);
                        FunctionInstance f2 = ast.right.getLast().mv.getFunction();

                        ast.mv.setValue(functionConcat(context, f1, f2));

                        if (ast.next != null) {
                            ast.next.visite(context);
                            //ast.mv.setValue(ast.next.mv);
                        }
                    }
                });

                return map;
            }

            /**
             * Executed in the case of value not found. Throws exception.
             */
            @Override
            protected VisitorInjector getDefault(AST ast) {
                throw new InvalidTokenException(ast.getToken(), BinaryOperator.class);
            }
        });

        // scope
        map.put(Program.class, new MethodVisitorInjector<Program<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Program<RuntimeContext> ast, RuntimeContext context) {
                executeBlock(ast.statements, context);

                MutableVariant ret = new MutableVariant();
                trampoline(context, ret);

                if (!ret.isNull()) {
                    context.returnValue = ret;
                    context.returning = true;
                }
            }
        });

        map.put(Block.class, new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {

                if (ast.scope == null) {
                    ast.scope = new NestedScope("nested", context.scope);
                }
                // enclosing backup
                Scope enclBackup = ast.scope.enclosing;
                ast.scope.enclosing = context.scope;

                // backup
                Scope backup = context.scope;

                context.scope = ast.scope;
                executeBlock(ast.statements, context);

                // restore
                context.scope = backup;
                ast.scope.enclosing = enclBackup;

            }
        });

        map.put(EvalBlock.class, new MethodVisitorInjector<EvalBlock<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(EvalBlock<RuntimeContext> ast, RuntimeContext context) {
                executeBlock(ast.statements, context);
            }
        });

        // imperative statements
        map.put(If.class, new MethodVisitorInjector<If<RuntimeContext>, RuntimeContext>() {

            /**
             * If statement visitor.
             */
            @Override
            public void visite(If<RuntimeContext> ast, RuntimeContext context) {
                if (ast.trueStatement.scope == null) {
                    ast.trueStatement.scope = new NestedScope("if", context.scope);
                }
                // enclosing backup
                Scope enclBackup = ast.trueStatement.scope.enclosing;

                ast.trueStatement.scope.enclosing = context.scope;

                // callBackup
                Scope backup = context.scope;
                context.scope = ast.trueStatement.scope;

                ast.condition.visite(context);
                boolean executed = false;
                boolean cond = ast.condition.getLast().mv.getBool();

                // if
                if (cond) {
                    context.scope = ast.trueStatement.scope;
                    executeBlock(ast.trueStatement.statements, context);
                    ast.mv.setValue(getExpressionOfLastStatement(ast.trueStatement.statements));
                    executed = true;
                }

                // elseif
                if (!executed) {
                    int size = ast.elseIfs.size();
                    for (int i = 0; i < size; i++) {
                        If<RuntimeContext> elseif = ast.elseIfs.get(i);
                        elseif.trueStatement.scope = ast.trueStatement.scope;

                        elseif.condition.visite(context);
                        boolean elseCond = elseif.condition.getLast().mv.getBool();

                        if (elseCond) {
                            context.scope = ast.trueStatement.scope;
                            executeBlock(elseif.trueStatement.statements, context);
                            ast.mv.setValue(getExpressionOfLastStatement(elseif.trueStatement.statements));
                            executed = true;
                        }
                    }
                }

                // else
                if (!executed && ast.falseStatement != null) {
                    context.scope = ast.trueStatement.scope;
                    executeBlock(ast.falseStatement.statements, context);
                    ast.mv.setValue(getExpressionOfLastStatement(ast.falseStatement.statements));
                    executed = true;
                }

                // next
                if (ast.next != null) {
                    ast.next.visite(context);
                }

                // restore
                context.scope = backup;
                ast.trueStatement.scope.enclosing = enclBackup;
            }
        });

        map.put(Loop.class, new MethodVisitorInjector<Loop<RuntimeContext>, RuntimeContext>() {

            /**
             * For statement visitor.
             */
            @Override
            public void visite(Loop<RuntimeContext> ast, RuntimeContext context) {

                // scope
                Block<RuntimeContext> block = ast.statement;
                if (block.scope == null) {
                    block.scope = new NestedScope("loop", context.scope);
                }
                // enclosing backup
                Scope enclBackup = block.scope.enclosing;
                block.scope.enclosing = context.scope;

                // callBackup
                Scope backup = context.scope;

                // loop
                int limit = (int) context.getOptionalNumberOf(ast.limit, 0);
                for (int i = 0; i < limit; i++) {
                    context.scope = block.scope;
                    executeBlock(block.statements, context);
                    if (context.breaking || context.returning) {
                        break;
                    }
                }

                // restore
                context.breaking = false;
                context.scope = backup;
                block.scope.enclosing = enclBackup;

            }
        });

        map.put(For.class, new MethodVisitorInjector<For<RuntimeContext>, RuntimeContext>() {

            /**
             * For statement visitor.
             */
            @Override
            public void visite(For<RuntimeContext> ast, RuntimeContext context) {

                // scope
                Block block = ast.statement;
                if (block.scope == null) {
                    block.scope = new NestedScope("for", context.scope);
                }
                // enclosing backup
                Scope enclBackup = block.scope.enclosing;
                block.scope.enclosing = context.scope;

                // callBackup
                Scope backup = context.scope;
                context.scope = block.scope;

                int i = 0;

                // loop
                for (AST.visite(ast.decl, context);
                        context.getOptionalBoolOf(ast.condition, true);
                        AST.visite(ast.increment, context)) {
                    context.scope = block.scope;
                    executeBlock(block.statements, context);

                    if (context.breaking || context.returning) {
                        break;
                    }
                }

                // restore
                context.breaking = false;
                context.scope = backup;
                block.scope.enclosing = enclBackup;

            }
        });

        map.put(While.class, new MethodVisitorInjector<While<RuntimeContext>, RuntimeContext>() {

            /**
             * For statement visitor.
             */
            @Override
            public void visite(While<RuntimeContext> ast, RuntimeContext context) {

                // scope
                Block block = ast.statement;
                if (block.scope == null) {
                    block.scope = new NestedScope("while", context.scope);
                }
                // enclosing backup
                Scope enclBackup = block.scope.enclosing;
                block.scope.enclosing = context.scope;

                // callBackup
                Scope backup = context.scope;
                context.scope = block.scope;

                int i = 0;

                while (context.getOptionalBoolOf(ast.condition, true)) {
                    context.scope = block.scope;
                    executeBlock(block.statements, context);

                    if (context.breaking || context.returning) {
                        break;
                    }
                }

                // restore
                context.breaking = false;
                context.scope = backup;
                block.scope.enclosing = enclBackup;

            }
        });

        map.put(Until.class, new MethodVisitorInjector<Until<RuntimeContext>, RuntimeContext>() {

            /**
             * For statement visitor.
             */
            @Override
            public void visite(Until<RuntimeContext> ast, RuntimeContext context) {

                // scope
                Block block = ast.statement;
                if (block.scope == null) {
                    block.scope = new NestedScope("until", context.scope);
                }
                // enclosing backup
                Scope enclBackup = block.scope.enclosing;
                block.scope.enclosing = context.scope;

                // callBackup
                Scope backup = context.scope;
                context.scope = block.scope;

                int i = 0;

                do {
                    context.scope = block.scope;
                    executeBlock(block.statements, context);

                    if (context.breaking || context.returning) {
                        break;
                    }
                } while (!context.getOptionalBoolOf(ast.condition, true));

                // restore
                context.breaking = false;
                context.scope = backup;
                block.scope.enclosing = enclBackup;

            }
        });

        map.put(Break.class, new MethodVisitorInjector<Break<RuntimeContext>, RuntimeContext>() {

            /**
             * Break statement visitor. Throw break exception.
             */
            @Override
            public void visite(Break<RuntimeContext> ast, RuntimeContext context) {
                context.breaking = true;
            }
        });

        map.put(Return.class, new MethodVisitorInjector<Return<RuntimeContext>, RuntimeContext>() {

            /**
             * Return statement visitor. Throw return exception.
             */
            @Override
            public void visite(Return<RuntimeContext> ast, RuntimeContext context) {
                if (ast.expr != null) {
                    if (isTailRecursion(ast.expr)) {
                        // trampoline
                        ((Call) ast.expr.next).isTailCall = true;
                        context.trampolineCall = ast.expr;
                        context.trampolineScope = context.scope;
                        context.trampolineScopeCall = context.call;
                    } else {
                        ast.expr.visite(context);
                        context.returnValue.setValue(ast.expr.getLast().mv);

                        // Issue #3 : in case of returning function, clone the scope tree
                        if (context.returnValue.isFunction()) {
                            Scope clone = context.returnValue.getFunction().enclosing.cloneUntil(PREDICAT_UNTIL_FUNCTION);
                            context.returnValue.getFunction().enclosing = clone;
                        }
                    }

                }

                context.returning = true;
            }
        });

    }

    @Override
    public void addDynamicMethods(RuntimeContext context, TypeNameFunctionMap dynamicMethods) {

        FunctionInstance getType = ExpressionFactory.functionInstance(context, "getType", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;

                // return
                context.returnValue(self.getType().ordinal());
            }
        });

        FunctionInstance is = ExpressionFactory.functionInstance(context, "is", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // return
                context.returnValue(self.getType().ordinal() == (int) v1.getNumber());
            }
        }, "v1");

        FunctionInstance isNull = ExpressionFactory.functionInstance(context, "isNull", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;

                // return
                context.returnValue(self.isNull());
            }
        });

        FunctionInstance ifNull = ExpressionFactory.functionInstance(context, "ifNull", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // return
                if (self.isNull()) {
                    context.returnValue(v1);
                } else {
                    context.returnValue(self);
                }
            }
        }, "v1");

        FunctionInstance ifNotBreak = ExpressionFactory.functionInstance(context, "ifNotBreak", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // return
                if (!(self.getType().ordinal() == (int) v1.getNumber())) {
                    context.breakCallChain = true;
                } else {
                    context.returnValue(self);
                }
            }
        }, "v1");

        FunctionInstance ifNullBreak = ExpressionFactory.functionInstance(context, "ifNullBreak", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;

                // return
                if (self.isNull()) {
                    context.breakCallChain = true;
                } else {
                    context.returnValue(self);
                }
            }
        });

        FunctionInstance equals = ExpressionFactory.functionInstance(context, "equals", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // return
                context.returnValue(self.equals(v1));
            }
        }, "v1");

        FunctionInstance add = ExpressionFactory.functionInstance(context, "add", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant value = f.decl.params.get(0).var.mv;
                MutableVariant ident = f.decl.params.get(1).var.mv;

                if (!ident.isNull()) {
                    self.addCopy(ident, value);
                } else {
                    self.addCopy(value);
                }

                // return
                context.returnValue(self);
            }
        }, "value", "ident");

        FunctionInstance addAll = ExpressionFactory.functionInstance(context, "addAll", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant values = f.decl.params.get(0).var.mv;
                MutableVariant index = f.decl.params.get(1).var.mv;

                if (!index.isNull()) {
                    int i = (int) index.getNumber();
                    self.addAll(i, values.getArray());
                } else {
                    self.addAll(values.getArray());
                }

                // return
                context.returnValue(self);
            }
        }, "values", "index");

        FunctionInstance toString = ExpressionFactory.functionInstance(context, "toString", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;

                // return
                context.returnValue(self.toString());
            }
        });

        FunctionInstance toJson = ExpressionFactory.functionInstance(context, "toJson", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;

                // return
                context.returnValue(self.toJson(0));
            }
        });

        FunctionInstance onChanged = ExpressionFactory.functionInstance(context, "onChanged", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, final RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                final FunctionInstance func = ast.function.decl.params.get(0).var.mv.getFunction();

                self.setChangedListener(new MutableVariant.ChangedListener() {

                    @Override
                    public void changed(MutableVariant value) {
                        setOptionalParameter(func, 0, value);
                        callFunction(context, func, returnValue);
                    }
                });
                // return
                context.returnValue(self.toString());
            }
        }, "p1");

        // VOID
        // void.property
        dynamicMethods.put(Types.VOID, getType);

        dynamicMethods.put(Types.VOID, is);

        dynamicMethods.put(Types.VOID, isNull);

        dynamicMethods.put(Types.VOID, ifNull);

        dynamicMethods.put(Types.VOID, ifNotBreak);

        dynamicMethods.put(Types.VOID, ifNullBreak);

        dynamicMethods.put(Types.VOID, equals);

        // void.method
        dynamicMethods.put(Types.VOID, add);

        dynamicMethods.put(Types.VOID, addAll);

        dynamicMethods.put(Types.VOID, toString);

        dynamicMethods.put(Types.VOID, toJson);

        // void.event
        dynamicMethods.put(Types.VOID, onChanged);

        // BOOL
        // bool.property
        dynamicMethods.put(Types.BOOL, getType);

        dynamicMethods.put(Types.BOOL, is);

        dynamicMethods.put(Types.BOOL, isNull);

        dynamicMethods.put(Types.BOOL, ifNull);

        dynamicMethods.put(Types.BOOL, ifNotBreak);

        dynamicMethods.put(Types.BOOL, ifNullBreak);

        dynamicMethods.put(Types.BOOL, equals);

        dynamicMethods.put(Types.BOOL, toString);

        dynamicMethods.put(Types.BOOL, toJson);

        // bool.method
        dynamicMethods.put(Types.BOOL, add);

        dynamicMethods.put(Types.BOOL, addAll);

        dynamicMethods.put(Types.BOOL, ExpressionFactory.functionInstance(context, "not", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                MutableVariant self = ast.function.self.mv;
                context.returnValue(!self.getBool());
            }
        }));

        dynamicMethods.put(Types.BOOL, ExpressionFactory.functionInstance(context, "and", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {

                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // return
                context.returnValue(self.getBool() && v1.getBool());
            }
        }, "v1"));

        dynamicMethods.put(Types.BOOL, ExpressionFactory.functionInstance(context, "or", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // return
                context.returnValue(self.getBool() || v1.getBool());
            }
        }, "v1"));

        dynamicMethods.put(Types.BOOL, ExpressionFactory.functionInstance(context, "xor", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // return
                context.returnValue(self.getBool() != v1.getBool());
            }
        }, "v1"));

        // functional method
        dynamicMethods.put(Types.BOOL, ExpressionFactory.functionInstance(context, "ifTrue", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // backup
                Scope backup = context.scope;

                // execute
                if (self.getBool()) {
                    FunctionInstance fc = v1.getFunction();
                    prepareCall(fc);
                    callFunction(context, fc, ast.function.call.mv);
                }
                context.returnValue(ast.function.call.mv);

                // restore
                context.scope = backup;

            }
        }, "v1"));

        dynamicMethods.put(Types.BOOL, ExpressionFactory.functionInstance(context, "ifFalse", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // backup
                Scope backup = context.scope;

                // execute
                if (!self.getBool()) {
                    FunctionInstance fc = v1.getFunction();
                    prepareCall(fc);
                    callFunction(context, fc, ast.function.call.mv);
                }
                context.returnValue(ast.function.call.mv);

                // restore
                context.scope = backup;

            }
        }, "v1"));

        dynamicMethods.put(Types.BOOL, ExpressionFactory.functionInstance(context, "whileDo", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                Expression cond = ast.function.self;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // backup
                Scope backup = context.scope;

                Expression next = cond.next;
                cond.next = null;
                ast.function.call.mv.setValue(cond.mv);

                FunctionInstance fc = v1.getFunction();
                // backup
                prepareCall(fc);
                context.scope = fc.enclosing;

                // execute
                while (context.getOptionalBoolOf(cond, true)) {
                    fc.decl.statement.visite(context);
                }

                context.returnValue(cond.mv);

                cond.next = next;

                // restore
                context.scope = backup;
            }
        }, "v1"));

        dynamicMethods.put(Types.BOOL, ExpressionFactory.functionInstance(context, "untilDo", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                Expression cond = ast.function.self;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // backup
                Scope backup = context.scope;

                Expression next = cond.next;
                cond.next = null;
                ast.function.call.mv.setValue(cond.mv);

                FunctionInstance fc = v1.getFunction();

                prepareCall(fc);
                context.scope = fc.enclosing;

                // execute
                do {
                    fc.decl.statement.visite(context);
                } while (!context.getOptionalBoolOf(cond, true));

                context.returnValue(cond.mv);

                cond.next = next;

                // restore
                context.scope = backup;
            }
        }, "v1"));

        // bool.event
        dynamicMethods.put(Types.BOOL, onChanged);

        // NUMBER
        // number.property
        dynamicMethods.put(Types.NUMBER, getType);

        dynamicMethods.put(Types.NUMBER, is);

        dynamicMethods.put(Types.NUMBER, isNull);

        dynamicMethods.put(Types.NUMBER, ifNull);

        dynamicMethods.put(Types.NUMBER, ifNotBreak);

        dynamicMethods.put(Types.NUMBER, ifNullBreak);

        dynamicMethods.put(Types.NUMBER, ExpressionFactory.functionInstance(context, "isInteger", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                MutableVariant self = ast.function.self.mv;
                context.returnValue(self.isInteger());
            }
        }));

        dynamicMethods.put(Types.NUMBER, ExpressionFactory.functionInstance(context, "isReal", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                MutableVariant self = ast.function.self.mv;
                context.returnValue(self.isReal());
            }
        }));

        dynamicMethods.put(Types.NUMBER, ExpressionFactory.functionInstance(context, "isNan", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                MutableVariant self = ast.function.self.mv;
                context.returnValue(self.isNan());
            }
        }));

        dynamicMethods.put(Types.NUMBER, ExpressionFactory.functionInstance(context, "isInfinite", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                MutableVariant self = ast.function.self.mv;
                context.returnValue(self.isInfinite());
            }
        }));

        dynamicMethods.put(Types.NUMBER, equals);

        dynamicMethods.put(Types.NUMBER, toString);

        dynamicMethods.put(Types.NUMBER, toJson);

        // number.method
        dynamicMethods.put(Types.NUMBER, add);

        dynamicMethods.put(Types.NUMBER, addAll);

        dynamicMethods.put(Types.NUMBER, ExpressionFactory.functionInstance(context, "toInteger", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                MutableVariant self = ast.function.self.mv;
                context.returnValue((int) self.getNumber());
            }
        }));

        dynamicMethods.put(Types.NUMBER, ExpressionFactory.functionInstance(context, "decodePoint", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue value
                MutableVariant self = ast.function.self.mv;
                char chr = (char) self.getNumber();

                // return
                context.returnValue("" + chr);
            }
        }));

        // number.unary
        dynamicMethods.put(Types.NUMBER, ExpressionFactory.functionInstance(context, "minus", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                MutableVariant self = ast.function.self.mv;
                context.returnValue(-self.getNumber());
            }
        }));

        dynamicMethods.put(Types.NUMBER, ExpressionFactory.functionInstance(context, "increment", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                int inc = 1;
                if (!v1.isNull()) {
                    inc = (int) v1.getNumber();
                }

                float value = self.getNumber() + inc;

                // return
                self.setValue(value);
                context.returnValue(value);
            }
        }, "v1"));

        dynamicMethods.put(Types.NUMBER, ExpressionFactory.functionInstance(context, "decrement", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                int inc = 1;
                if (!v1.isNull()) {
                    inc = (int) v1.getNumber();
                }

                float value = self.getNumber() - inc;

                // return
                self.setValue(value);
                context.returnValue(value);
            }
        }, "v1"));

        // number.binary
        dynamicMethods.put(Types.NUMBER, ExpressionFactory.functionInstance(context, "addition", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // return
                context.returnValue(self.getNumber() + v1.getNumber());
            }
        }, "v1"));

        dynamicMethods.put(Types.NUMBER, ExpressionFactory.functionInstance(context, "substract", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // return
                context.returnValue(self.getNumber() - v1.getNumber());
            }
        }, "v1"));

        dynamicMethods.put(Types.NUMBER, ExpressionFactory.functionInstance(context, "multiply", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // return
                context.returnValue(self.getNumber() * v1.getNumber());
            }
        }, "v1"));

        dynamicMethods.put(Types.NUMBER, ExpressionFactory.functionInstance(context, "divide", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // return
                context.returnValue(self.getNumber() / v1.getNumber());
            }
        }, "v1"));

        dynamicMethods.put(Types.NUMBER, ExpressionFactory.functionInstance(context, "modulo", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // return
                context.returnValue(self.getNumber() % v1.getNumber());
            }
        }, "v1"));

        dynamicMethods.put(Types.NUMBER, ExpressionFactory.functionInstance(context, "limit", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                float v1 = f.decl.params.get(0).var.mv.getNumber();
                float v2 = f.decl.params.get(1).var.mv.getNumber();

                float min = Math.min(v1, v2);
                float max = Math.max(v1, v2);

                float v = self.getNumber();
                if (v < min) {
                    v = min;
                }
                if (v > max) {
                    v = max;
                }

                context.returnValue(v);
            }
        }, "v1", "v2"));

        // number.comparaison
        dynamicMethods.put(Types.NUMBER, ExpressionFactory.functionInstance(context, "greaterThan", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // return
                context.returnValue(self.compareTo(v1) > 0);
            }
        }, "v1"));

        dynamicMethods.put(Types.NUMBER, ExpressionFactory.functionInstance(context, "smallerThan", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // return
                context.returnValue(self.compareTo(v1) < 0);
            }
        }, "v1"));

        dynamicMethods.put(Types.NUMBER, ExpressionFactory.functionInstance(context, "greaterOrEquals", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // return
                context.returnValue(self.compareTo(v1) >= 0);
            }
        }, "v1"));

        dynamicMethods.put(Types.NUMBER, ExpressionFactory.functionInstance(context, "smallerOrEquals", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // return
                context.returnValue(self.compareTo(v1) <= 0);
            }
        }, "v1"));

        dynamicMethods.put(Types.NUMBER, ExpressionFactory.functionInstance(context, "between", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                float self = ast.function.self.mv.getNumber();
                FunctionInstance f = ast.function;
                float n1 = f.decl.params.get(0).var.mv.getNumber();
                float n2 = f.decl.params.get(1).var.mv.getNumber();

                // return
                context.returnValue(self >= Math.min(n1, n2)
                        && self <= Math.max(n1, n2));
            }
        }, "v1", "v2"));

        // number.functional
        dynamicMethods.put(Types.NUMBER, ExpressionFactory.functionInstance(context, "loopFor", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                FunctionInstance fc = f.decl.params.get(0).var.mv.getFunction();
                MutableVariant mvfrom = f.decl.params.get(1).var.mv;
                MutableVariant mvStep = f.decl.params.get(2).var.mv;

                int from = 0;
                if (!mvfrom.isNull()) {
                    from = (int) mvfrom.getNumber();
                }

                int step = 1;
                if (!mvStep.isNull()) {
                    step = (int) mvStep.getNumber();
                }

                // backup
                Scope backup = context.scope;

                // execute
                int to = (int) self.getNumber();
                prepareCall(fc);

                if (step > 0) {
                    int mf = Math.min(from, to);
                    int mt = Math.max(from, to);
                    for (int i = mf; i < mt; i = i + step) {
                        setOptionalParameter(fc, 0, i);
                        callFunction(context, fc, ast.function.call.mv);
                    }
                } else {
                    int mf = Math.max(from, to);
                    int mt = Math.min(from, to);
                    for (int i = mf; i > mt; i = i + step) {
                        setOptionalParameter(fc, 0, i);
                        callFunction(context, fc, ast.function.call.mv);
                    }
                }

                context.returnValue(ast.function.call.mv);
                // restore
                context.scope = backup;
            }
        }, "fc", "from", "step"));

        // number.event
        dynamicMethods.put(Types.NUMBER, onChanged);

        // STRING
        // string.property
        dynamicMethods.put(Types.STRING, getType);

        dynamicMethods.put(Types.STRING, is);

        dynamicMethods.put(Types.STRING, isNull);

        dynamicMethods.put(Types.STRING, ifNull);

        dynamicMethods.put(Types.STRING, ifNotBreak);

        dynamicMethods.put(Types.STRING, ifNullBreak);

        dynamicMethods.put(Types.STRING, equals);

        dynamicMethods.put(Types.STRING, toString);

        dynamicMethods.put(Types.STRING, toJson);

        dynamicMethods.put(Types.STRING, ExpressionFactory.functionInstance(context, "length", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                MutableVariant self = ast.function.self.mv;
                context.returnValue(self.getString(context).length());
            }
        }));

        dynamicMethods.put(Types.STRING, ExpressionFactory.functionInstance(context, "isEmpty", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                MutableVariant self = ast.function.self.mv;
                context.returnValue(self.getString(context).isEmpty());
            }
        }));

        dynamicMethods.put(Types.STRING, ExpressionFactory.functionInstance(context, "getChar", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // return
                context.returnValue(String.valueOf(self.getString(context).charAt((int) v1.getNumber())));
            }
        }, "pos"));

        dynamicMethods.put(Types.STRING, ExpressionFactory.functionInstance(context, "contains", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // return
                context.returnValue(self.getString(context).contains(v1.getString(context)));
            }
        }, "value"));

        dynamicMethods.put(Types.STRING, ExpressionFactory.functionInstance(context, "indexOf", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                String value = f.decl.params.get(0).var.mv.getString(context);
                MutableVariant start = f.decl.params.get(1).var.mv;

                String s = self.getString(context);
                int i = -1;
                if (start.isNull()) {
                    i = s.indexOf(value);
                } else {
                    i = s.indexOf(value, (int) start.getNumber());
                }

                // return
                if (i != -1) {
                    context.returnValue(i);
                } else {
                    context.returnValue();
                }

            }
        }, "value", "start"));

        // string.method
        dynamicMethods.put(Types.STRING, add);

        dynamicMethods.put(Types.STRING, addAll);

        dynamicMethods.put(Types.STRING, ExpressionFactory.functionInstance(context, "concat", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                // return
                context.returnValue(self.getString(context) + v1.getString(context));
            }
        }, "string"));

        dynamicMethods.put(Types.STRING, ExpressionFactory.functionInstance(context, "remove", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                int pos = (int) f.decl.params.get(0).var.mv.getNumber();
                MutableVariant length = f.decl.params.get(1).var.mv;

                String targ = self.getString(context);

                int end = 0;
                if (length.isNull()) {
                    end = pos + 1;
                } else {
                    end = pos + (int) length.getNumber();
                }

                if (end >= targ.length()) {
                    end = targ.length();
                }

                // return
                context.returnValue(targ.substring(0, pos) + targ.substring(end));
            }
        }, "pos", "length"));

        dynamicMethods.put(Types.STRING, ExpressionFactory.functionInstance(context, "upper", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                MutableVariant self = ast.function.self.mv;
                context.returnValue(self.getString(context).toUpperCase());
            }
        }));

        dynamicMethods.put(Types.STRING, ExpressionFactory.functionInstance(context, "lower", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                MutableVariant self = ast.function.self.mv;
                context.returnValue(self.getString(context).toLowerCase());
            }
        }));

        dynamicMethods.put(Types.STRING, ExpressionFactory.functionInstance(context, "append", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                String value = f.decl.params.get(0).var.mv.getString(context);
                MutableVariant v2 = f.decl.params.get(1).var.mv;
                int pos = (int) v2.getNumber();

                String res = self.getString(context);
                if (v2.isNull()) {
                    res += value;
                } else {
                    res = res.substring(0, pos) + value + res.substring(pos);
                }

                // return
                context.returnValue(res);
            }
        }, "value", "pos"));

        dynamicMethods.put(Types.STRING, ExpressionFactory.functionInstance(context, "appendSep", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                String value = f.decl.params.get(0).var.mv.getString(context);
                String sep = f.decl.params.get(1).var.mv.getString(context);

                String res = self.getString(context);
                if (res.length() > 0) {
                    res += sep;
                }
                res += value;

                // return
                context.returnValue(res);
            }
        }, "value", "sep"));

        dynamicMethods.put(Types.STRING, ExpressionFactory.functionInstance(context, "replace", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                String from = f.decl.params.get(0).var.mv.getString(context);
                String to = f.decl.params.get(1).var.mv.getString(context);

                // return
                context.returnValue(self.getString(context).replace(from, to));
            }
        }, "from", "to"));

        dynamicMethods.put(Types.STRING, ExpressionFactory.functionInstance(context, "replaceAt", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                String self = ast.function.self.mv.getString(context);
                FunctionInstance f = ast.function;
                String value = f.decl.params.get(0).var.mv.getString(context);
                int pos = (int) f.decl.params.get(1).var.mv.getNumber();
                int cut = pos + value.length();

                String res;
                res = self.substring(0, pos);
                if (pos <= self.length()) {
                    res += value;
                    if (cut <= self.length()) {
                        res += self.substring(cut);
                    }
                }

                // return
                context.returnValue(res);
            }
        }, "falue", "pos"));

        dynamicMethods.put(Types.STRING, ExpressionFactory.functionInstance(context, "subString", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                String self = ast.function.self.mv.getString(context);
                FunctionInstance f = ast.function;
                int from = (int) f.decl.params.get(0).var.mv.getNumber();
                MutableVariant to = f.decl.params.get(1).var.mv;

                // return
                if (to.isNull()) {
                    context.returnValue(self.substring(from));
                } else {
                    context.returnValue(self.substring(from, (int) to.getNumber()));
                }

            }
        }, "from", "to"));

        dynamicMethods.put(Types.STRING, ExpressionFactory.functionInstance(context, "subStringOf", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                String self = ast.function.self.mv.getString(context);
                FunctionInstance f = ast.function;
                String v1 = f.decl.params.get(0).var.mv.getString(context);
                MutableVariant v2 = f.decl.params.get(1).var.mv;

                int from = self.indexOf(v1);
                if (from != -1) {
                    from += v1.length();
                    int to = self.indexOf(v2.getString(context), from);

                    // return
                    if (v2.isNull() || to < 0) {
                        context.returnValue(self.substring(from));
                    } else {
                        context.returnValue(self.substring(from, to));
                    }
                } else {
                    context.returnValue(self);
                }

            }
        }, "v1", "v2"));

        dynamicMethods.put(Types.STRING, ExpressionFactory.functionInstance(context, "split", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                MutableVariant v1 = ast.function.decl.params.get(0).var.mv;

                returnValue.convertToVoid();
                String[] strings = self.getString(context).split(v1.getString(context));
                for (int i = 0; i < strings.length; i++) {
                    returnValue.add(new MutableVariant(strings[i]));
                }

                context.returnValue(returnValue);
            }
        }, "string"));

        dynamicMethods.put(Types.STRING, ExpressionFactory.functionInstance(context, "splitAt", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                List<MutableVariant> a = f.decl.params.get(0).var.mv.getArray();

                returnValue.convertToVoid();
                String s = self.getString(context);
                int start = 0;
                for (int i = 0; i < a.size(); i++) {
                    int end = (int) a.get(i).getNumber();
                    if (end < s.length()) {
                        returnValue.add(new MutableVariant(s.substring(start, end)));
                        start = end;
                    }
                }

                returnValue.add(new MutableVariant(s.substring(start, s.length())));

                context.returnValue(returnValue);
            }
        }, "string"));

        dynamicMethods.put(Types.STRING, ExpressionFactory.functionInstance(context, "create", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                FunctionInstance f = ast.function;
                String value = f.decl.params.get(0).var.mv.getString(context);
                int count = (int) f.decl.params.get(1).var.mv.getNumber();

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < count; i++) {
                    sb.append(value);
                }

                context.returnValue(sb.toString());

            }
        }, "value", "count"));

        dynamicMethods.put(Types.STRING, ExpressionFactory.functionInstance(context, "count", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                String self = ast.function.self.mv.getString(context);
                FunctionInstance f = ast.function;
                String value = f.decl.params.get(0).var.mv.getString(context);

                int count = 0, i = 0;
                int size = self.length();
                while (i < size) {
                    int begin = self.indexOf(value, i);
                    if (begin != -1) {
                        count++;
                        i += begin + value.length();
                    } else {
                        break;
                    }
                }

                // return
                context.returnValue(count);
            }
        }, "value"));

        dynamicMethods.put(Types.STRING, ExpressionFactory.functionInstance(context, "trim", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                MutableVariant self = ast.function.self.mv;
                context.returnValue(self.getString(context).trim());
            }
        }));

        dynamicMethods.put(Types.STRING, ExpressionFactory.functionInstance(context, "encodePoint", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                MutableVariant self = ast.function.self.mv;

                returnValue.convertToVoid();

                String str = self.getString(context);
                int size = str.length();
                for (int i = 0; i < size; i++) {
                    int ascii = (int) str.charAt(i);
                    if (i == 0) {
                        returnValue.setValue(ascii);
                    } else {
                        returnValue.add(new MutableVariant(ascii));
                    }
                }

                context.returnValue(returnValue);
            }
        }));

        // string.functional
        dynamicMethods.put(Types.STRING, ExpressionFactory.functionInstance(context, "each", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                String self = ast.function.self.mv.getString(context);
                FunctionInstance f = ast.function;
                FunctionInstance fc = f.decl.params.get(0).var.mv.getFunction();
                MutableVariant step = f.decl.params.get(1).var.mv;

                int st = 1;
                if (!step.isNull()) {
                    st = (int) step.getNumber();
                }

                // backup
                Scope backup = context.scope;
                StringBuilder sb = new StringBuilder();

                // execute
                int end;
                prepareCall(fc);
                String part;

                for (int i = 0; i < self.length(); i += st) {
                    end = Math.min(i + st, self.length());
                    part = String.valueOf(self.substring(i, end));
                    setOptionalParameter(fc, 0, part);
                    setOptionalParameter(fc, 1, i);

                    if (callFunction(context, fc, ast.function.call.mv)) {
                        sb.append(ast.function.call.mv.getString(context));
                    } else {
                        sb.append(part);
                    }
                }

                // restore
                context.scope = backup;

                context.returnValue(sb.toString());
            }
        }, "fc", "step"));

        // string.event
        dynamicMethods.put(Types.STRING, onChanged);

        // FUNCTION
        // function.property
        dynamicMethods.put(Types.FUNCTION, getType);

        dynamicMethods.put(Types.FUNCTION, is);

        dynamicMethods.put(Types.FUNCTION, isNull);

        dynamicMethods.put(Types.FUNCTION, ifNull);

        dynamicMethods.put(Types.FUNCTION, ifNotBreak);

        dynamicMethods.put(Types.FUNCTION, ifNullBreak);

        dynamicMethods.put(Types.FUNCTION, ExpressionFactory.functionInstance(context, "isFunction", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                MutableVariant self = ast.function.self.mv;
                context.returnValue(self.isFunction());
            }
        }));

        dynamicMethods.put(Types.FUNCTION, equals);

        dynamicMethods.put(Types.FUNCTION, toString);

        // function.prototype
        dynamicMethods.put(Types.FUNCTION, ExpressionFactory.functionInstance(context, "clone", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue value
                MutableVariant self = ast.function.self.mv;

                // return
                context.returnValue(self.getFunction().clone());
            }
        }));

        // function.method
        dynamicMethods.put(Types.FUNCTION, add);

        dynamicMethods.put(Types.FUNCTION, addAll);

        // function.functional
        dynamicMethods.put(Types.FUNCTION, ExpressionFactory.functionInstance(context, "decorate", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                FunctionInstance self = ast.function.self.mv.getFunction();
                FunctionInstance f = ast.function;
                FunctionInstance decorator = f.decl.params.get(0).var.mv.getFunction();

                f.use.increment();

                FunctionDeclaration<RuntimeContext> fd = decorator.decl.clone();
                fd.visite(context);

                fd.params.get(fd.params.size() - 1).var.mv.setValue(ast.function.self.mv);

                FunctionInstance result = new FunctionInstance(fd);
                result.enclosing = self.enclosing;
                result.use = f.use;

                context.returnValue(result);
            }
        }, "decorator"));

        dynamicMethods.put(Types.FUNCTION, ExpressionFactory.functionInstance(context, "concat", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                FunctionInstance self = ast.function.self.mv.getFunction();
                FunctionInstance f = ast.function;
                FunctionInstance fc = f.decl.params.get(0).var.mv.getFunction();

                context.returnValue(functionConcat(context, self, fc));
            }
        }, "fc"));

        dynamicMethods.put(Types.FUNCTION, ExpressionFactory.functionInstance(context, "setParameter", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                FunctionInstance self = ast.function.self.mv.getFunction();
                FunctionInstance f = ast.function;
                String name = f.decl.params.get(0).var.mv.getString(context);
                MutableVariant value = f.decl.params.get(1).var.mv;

                VariableDeclaration<RuntimeContext> param = self.decl.getDeclaration(name);
                if (param == null) {
                    param = DeclarationFactory.factoryVar(name);
                    param.injectVisitor(context.runtime);
                    self.decl.addDeclaration(param);

                    Scope backup = context.scope;
                    context.scope = self.decl.scope;
                    param.visite(context);
                    context.scope = backup;
                }

                setOptionalParameterExpression(self.decl.getDeclaration(name), value);

                context.returnValue(self);
            }
        }, "name", "value"));

        dynamicMethods.put(Types.FUNCTION, ExpressionFactory.functionInstance(context, "setParameters", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                FunctionInstance self = ast.function.self.mv.getFunction();
                FunctionInstance f = ast.function;
                List<MutableVariant> a = f.decl.params.get(0).var.mv.getArray();

                int size = a.size();
                for (int i = 0; i < size; i++) {
                    setOptionalParameterExpression(self.decl.params.get(i), a.get(i));
                }

                context.returnValue(self);
            }
        }, "a", "value"));

        dynamicMethods.put(Types.FUNCTION, ExpressionFactory.functionInstance(context, "getParametersNames", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                FunctionInstance self = ast.function.self.mv.getFunction();

                List<MutableVariant> result = new ArrayList<MutableVariant>();
                int size = self.decl.params.size();
                for (int i = 0; i < size; i++) {
                    result.add(new MutableVariant(self.decl.params.get(i).partName));
                }

                context.returnValue(result);
            }
        }));

        dynamicMethods.put(Types.FUNCTION, ExpressionFactory.functionInstance(context, "removeParameter", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                FunctionInstance self = ast.function.self.mv.getFunction();
                FunctionInstance f = ast.function;
                String name = f.decl.params.get(0).var.mv.getString(context);

                self.decl.removeDeclaration(name);

                context.returnValue(self);
            }
        }, "name"));

        dynamicMethods.put(Types.FUNCTION, ExpressionFactory.functionInstance(context, "parameterExists", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                FunctionInstance self = ast.function.self.mv.getFunction();
                FunctionInstance f = ast.function;
                String name = f.decl.params.get(0).var.mv.getString(context);

                context.returnValue(self.decl.isDeclarationExists(name));
            }
        }, "name"));

        // function.event
        dynamicMethods.put(Types.FUNCTION, onChanged);

        // OBJECT
        // object.property
        dynamicMethods.put(Types.OBJECT, getType);

        dynamicMethods.put(Types.OBJECT, is);

        dynamicMethods.put(Types.OBJECT, isNull);

        dynamicMethods.put(Types.OBJECT, ifNull);

        dynamicMethods.put(Types.OBJECT, ifNotBreak);

        dynamicMethods.put(Types.OBJECT, ifNullBreak);

        dynamicMethods.put(Types.OBJECT, ExpressionFactory.functionInstance(context, "isA", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue value
                ObjectInstance self = ast.function.self.mv.getObject();
                ObjectInstance o = ast.function.decl.params.get(0).var.mv.getObject();

                // return
                context.returnValue(self.decl.identity == o.decl.identity);
            }
        }, "o"));

        dynamicMethods.put(Types.OBJECT, equals);

        dynamicMethods.put(Types.OBJECT, toString);

        dynamicMethods.put(Types.OBJECT, toJson);

        // object.prototype
        dynamicMethods.put(Types.OBJECT, ExpressionFactory.functionInstance(context, "clone", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue value
                ObjectInstance self = ast.function.self.mv.getObject();
                ObjectInstance clone = self.clone(context);

                // enter scope
                Scope backup = context.scope;
                context.scope = context.call;

                int size = ast.function.call.args.size();
                int declSize = clone.decl.attributs.size();

                // loop on args and attributes
                int j = 0;
                for (int i = 0; i < size && j < declSize; i++) {

                    // get function argument value
                    ast.function.call.args.get(i).visite(context);
                    MutableVariant mv = ast.function.call.args.get(i).getLast().mv;

                    // get declaration
                    VariableDeclaration<RuntimeContext> var = clone.decl.attributs.get(j);

                    // find next value
                    while (j < declSize - 1 && (var.mv.isFunction())) {
                        j++;
                        var = clone.decl.attributs.get(j);
                    }
                    if (j < declSize && !(var.mv.isFunction())) {
                        clone.scope.define(Identifiers.getID(var.var.partName), mv);

                        j++;
                    }

                }

                // restore
                context.scope = backup;

                // return
                context.returnValue(clone);
            }
        }, "params"));

        dynamicMethods.put(Types.OBJECT, ExpressionFactory.functionInstance(context, "fromJSon", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue value
                ObjectInstance self = ast.function.self.mv.getObject();
                String json = ast.function.decl.params.get(0).var.mv.getString(context);
                ObjectInstance o = JSon.Parse(context, json).getObject();

                // return
                context.returnValue(merge(context, self, o));
            }
        }, "json"));

        // object.method
        dynamicMethods.put(Types.OBJECT, add);

        dynamicMethods.put(Types.OBJECT, addAll);

        dynamicMethods.put(Types.OBJECT, ExpressionFactory.functionInstance(context, "merge", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue value
                ObjectInstance self = ast.function.self.mv.getObject();
                ObjectInstance o = ast.function.decl.params.get(0).var.mv.getObject();

                // return
                context.returnValue(merge(context, self, o));
            }
        }, "o"));

        dynamicMethods.put(Types.OBJECT, ExpressionFactory.functionInstance(context, "setAttribute", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                ObjectInstance self = ast.function.self.mv.getObject();
                String name = ast.function.decl.params.get(0).var.mv.getString(context);
                MutableVariant value = ast.function.decl.params.get(1).var.mv;

                VariableDeclaration attr = DeclarationFactory.factory(name, value);
                attr.injectVisitor(context.runtime);

                // backup
                Scope backup = context.scope;
                context.scope = self.scope;

                attr.visite(context);

                // restore
                context.scope = backup;

                self.decl.attributs.add(attr);

                context.returnValue(self);
            }
        }, "name", "value"));

        dynamicMethods.put(Types.OBJECT, ExpressionFactory.functionInstance(context, "getAttributesNames", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                ObjectInstance self = ast.function.self.mv.getObject();

                MutableVariant ret = new MutableVariant();

                int size = self.decl.attributs.size();
                for (int i = 0; i < size; i++) {
                    ret.add(new MutableVariant(self.decl.attributs.get(i).var.partName));
                }

                context.returnValue(ret);
            }
        }));

        dynamicMethods.put(Types.OBJECT, ExpressionFactory.functionInstance(context, "removeAttribute", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                ObjectInstance self = ast.function.self.mv.getObject();
                String name = ast.function.decl.params.get(0).var.mv.getString(context);

                self.decl.removeDeclaration(name);
                self.scope.remove(Identifiers.getID(name));

                context.returnValue(self);
            }
        }, "name"));

        dynamicMethods.put(Types.OBJECT, ExpressionFactory.functionInstance(context, "attributeExists", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                ObjectInstance self = ast.function.self.mv.getObject();
                String name = ast.function.decl.params.get(0).var.mv.getString(context);

                context.returnValue(self.scope.variables.get(Identifiers.getID(name)) != null);
            }
        }, "name"));

        // object.event
        dynamicMethods.put(Types.OBJECT, onChanged);

        // ARRAY
        // array.property
        dynamicMethods.put(Types.ARRAY, getType);

        dynamicMethods.put(Types.ARRAY, is);

        dynamicMethods.put(Types.ARRAY, isNull);

        dynamicMethods.put(Types.ARRAY, ifNull);

        dynamicMethods.put(Types.ARRAY, ifNotBreak);

        dynamicMethods.put(Types.ARRAY, ifNullBreak);

        dynamicMethods.put(Types.ARRAY, equals);

        dynamicMethods.put(Types.ARRAY, toString);

        dynamicMethods.put(Types.ARRAY, toJson);

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "length", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue value
                MutableVariant self = ast.function.self.mv;

                // return
                context.returnValue(self.getArray().size());
            }
        }));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "isEmpty", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue value
                MutableVariant self = ast.function.self.mv;

                // return
                context.returnValue(self.isEmpty());
            }
        }));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "getItem", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue value
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant identifier = f.decl.params.get(0).var.mv;

                // return
                context.returnValue(self.getValue(identifier));
            }
        }, "identity"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "setItem", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue value
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant identifier = f.decl.params.get(0).var.mv;
                MutableVariant value = f.decl.params.get(1).var.mv;

                MutableVariant mv = self.getValue(identifier);
                mv.setValue(value);

                // return
                context.returnValue(self.getValue(identifier));
            }
        }, "identity", "value"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "getFirst", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue value
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;

                // return
                context.returnValue(self.getValue(0));
            }
        }));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "getLast", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue value
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;

                // return
                context.returnValue(self.getValue(self.size() - 1));
            }
        }, "identity"));

        // array.prototype
        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "clone", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue value
                MutableVariant self = ast.function.self.mv;

                // return
                context.returnValue(self.cloneArray());
            }
        }));

        // array.method
        dynamicMethods.put(Types.ARRAY, add);

        dynamicMethods.put(Types.ARRAY, addAll);

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "clear", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue value
                MutableVariant self = ast.function.self.mv;

                self.clearArray();

                // return
                context.returnValue(self);
            }
        }));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "contains", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue value
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant value = f.decl.params.get(0).var.mv;

                // return
                context.returnValue(self.containsValue(value));
            }
        }, "value"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "containsAll", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                List<MutableVariant> a = ast.function.decl.params.get(0).var.mv.getArray();

                MutableVariant ret = self.cloneArray();

                int size = a.size();
                for (int i = 0; i < size; i++) {
                    MutableVariant v = a.get(i);
                    if (!ret.containsValue(v)) {
                        context.returnValue(false);
                        return;
                    }
                }

                context.returnValue(true);
            }
        }, "array"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "containsAny", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                List<MutableVariant> a = ast.function.decl.params.get(0).var.mv.getArray();

                MutableVariant ret = self.cloneArray();

                int size = a.size();
                for (int i = 0; i < size; i++) {
                    MutableVariant v = a.get(i);
                    if (ret.containsValue(v)) {
                        context.returnValue(true);
                        return;
                    }
                }

                context.returnValue(false);
            }
        }, "array"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "remove", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public synchronized void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue value
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant identifier = f.decl.params.get(0).var.mv;
                MutableVariant value = self.getValue(identifier);

                if (self.isMap()) {
                    int index = self.indexOfKey(identifier);
                    self.removeKey(identifier);
                    self.removeValue(index);
                } else if (identifier.isNumber()) {
                    self.removeValue((int) identifier.getNumber());
                } else {
                    self.removeValue(self.indexOf(identifier));
                }

                // return
                context.returnValue(self);
            }
        }, "identifier"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "pop", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue value
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant identifier = f.decl.params.get(0).var.mv;

                int pos = self.size() - 1;
                if (!identifier.isNull()) {
                    pos = self.indexOfKey(identifier);
                }

                MutableVariant mv = self.getValue(pos);
                self.removeValue(pos);

                // return
                context.returnValue(mv);
            }
        }, "identifier"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "indexOf", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue value
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant value = f.decl.params.get(0).var.mv;

                // return
                context.returnValue(self.indexOf(value));
            }
        }, "value"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "count", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue value
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant value = f.decl.params.get(0).var.mv;

                int count = 0;
                int size = self.size();
                for (int i = 0; i < size; i++) {
                    if (value.equals(self.getValue(i))) {
                        count++;
                    }
                }
                // return
                context.returnValue(count);
            }
        }, "value"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "swap", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue value
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant id1 = f.decl.params.get(0).var.mv;
                MutableVariant id2 = f.decl.params.get(1).var.mv;

                int index1 = self.indexOfKey(id1);
                int index2 = self.indexOfKey(id2);
                self.swap(index1, index2);

                // return
                context.returnValue(self);
            }
        }, "id1", "id2"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "decodePoint", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue value
                MutableVariant self = ast.function.self.mv;
                StringBuilder sb = new StringBuilder();

                int size = self.size();
                for (int i = 0; i < size; i++) {
                    char chr = (char) self.getValue(i).getNumber();
                    sb.append(chr);
                }

                // return
                context.returnValue(sb.toString());
            }
        }));

        // array.functional
        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "create", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue value
                MutableVariant self = ast.function.self.mv;
                FunctionInstance f = ast.function;
                MutableVariant mvto = f.decl.params.get(0).var.mv;
                MutableVariant mfc = f.decl.params.get(1).var.mv;

                FunctionInstance fc = null;
                if (!mfc.isNull()) {
                    fc = mfc.getFunction();
                }

                int to = (int) mvto.getNumber();

                self.clearArray();
                for (int i = 0; i < to; i++) {
                    if (fc == null) {
                        self.add(new MutableVariant(i));
                    } else {
                        setOptionalParameter(fc, 0, i);
                        setOptionalParameter(fc, 1, new MutableVariant(i));

                        callFunction(context, fc, returnValue);

                        self.add(new MutableVariant(returnValue));
                    }
                }

                // return
                context.returnValue(self);
            }
        }, "to", "fc"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "find", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance fc = ast.function.decl.params.get(0).var.mv.getFunction();
                MutableVariant pos = ast.function.decl.params.get(1).var.mv;

                int size = self.size();
                prepareCall(fc);

                int start = 0;
                if (!pos.isInvalid()) {
                    start = (int) pos.getNumber();
                }

                for (int i = start; i < size && !context.returning; i++) {
                    MutableVariant value = self.getValue(i);
                    returnValue.setValue(false);
                    setOptionalParameter(fc, 0, value);
                    setOptionalParameter(fc, 1, i);
                    setOptionalParameter(fc, 2, value.getKey());

                    callFunction(context, fc, returnValue);

                    if (returnValue.getBool()) {
                        context.returnValue(self.getValue(i));
                    }
                }

            }
        }, "fc", "pos"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "each", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance fc = ast.function.decl.params.get(0).var.mv.getFunction();

                prepareCall(fc);

                // CAREFULL : keep self.size as condition to evaluate in case of removed into the functor
                for (int i = 0; i < self.size(); i++) {
                    MutableVariant value = self.getValue(i);
                    setOptionalParameter(fc, 0, value);
                    setOptionalParameter(fc, 1, i);
                    setOptionalParameter(fc, 2, value.getKey());

                    callFunction(context, fc, value);
                }

                context.returnValue(self);
            }
        }, "fc"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "eachOnRow", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                int rowNumber = (int) ast.function.decl.params.get(0).var.mv.getNumber();
                FunctionInstance fc = ast.function.decl.params.get(1).var.mv.getFunction();

                MutableVariant row = self.getValue(rowNumber);
                prepareCall(fc);

                for (int i = 0; i < row.size(); i++) {
                    MutableVariant value = row.getValue(i);
                    setOptionalParameter(fc, 0, value);
                    setOptionalParameter(fc, 1, i);
                    setOptionalParameter(fc, 2, value.getKey());

                    callFunction(context, fc, value);
                }

                context.returnValue(self);
            }
        }, "row", "fc"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "eachOnCol", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                int colNumber = (int) ast.function.decl.params.get(0).var.mv.getNumber();
                FunctionInstance fc = ast.function.decl.params.get(1).var.mv.getFunction();

                prepareCall(fc);

                //Iterator<MutableVariant> ki = row.getKeysIterator();
                for (int i = 0; i < self.size(); i++) {
                    MutableVariant value = self.getValue(i).getValue(colNumber);
                    setOptionalParameter(fc, 0, value);
                    setOptionalParameter(fc, 1, i);
                    setOptionalParameter(fc, 2, value.getKey());

                    callFunction(context, fc, value);
                }

                context.returnValue(self);
            }
        }, "col", "fc"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "eachItem", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance fc = ast.function.decl.params.get(0).var.mv.getFunction();

                recursiveEachDepthFirst(context, self, fc);

                context.returnValue(self);
            }
        }, "fc"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "filter", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                MutableVariant f = ast.function.decl.params.get(0).var.mv;

                if (!f.isFunction()) {
                    context.returnValue(self);
                    return;
                }

                FunctionInstance fc = f.getFunction();
                List<MutableVariant> list = new ArrayList<MutableVariant>();
                int size = self.size();
                for (int i = 0; i < size; i++) {
                    MutableVariant value = self.getValue(i);
                    setOptionalParameter(fc, 0, value);
                    setOptionalParameter(fc, 1, i);
                    setOptionalParameter(fc, 2, value.getKey());

                    returnValue.setValue(false);
                    callFunction(context, fc, returnValue);

                    if (returnValue.getBool()) {
                        list.add(self.getValue(i));
                    }
                }

                context.returnValue(list);

            }
        }, "fc"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "sort", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                MutableVariant f = ast.function.decl.params.get(0).var.mv;

                if (f.isNull()) {
                    self.sort();
                } else {
                    self.sort(new ComparatorAdapter(context, f.getFunction()));
                }

                context.returnValue(self);

            }
        }, "fc"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "min", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                MutableVariant f = ast.function.decl.params.get(0).var.mv;

                if (f.isNull()) {
                    context.returnValue(self.min());
                } else {
                    context.returnValue(self.min(new ComparatorAdapter(context, f.getFunction())));
                }

            }
        }, "fc"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "max", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                MutableVariant f = ast.function.decl.params.get(0).var.mv;

                if (f.isNull()) {
                    context.returnValue(self.max());
                } else {
                    context.returnValue(self.max(new ComparatorAdapter(context, f.getFunction())));
                }

            }
        }, "fc"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "join", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                FunctionInstance fc = ast.function.decl.params.get(0).var.mv.getFunction();

                int size = self.size();
                prepareCall(fc);

                MutableVariant result = null;
                if (self.size() > 0) {
                    result = self.getValue(0);

                    for (int i = 1; i < size; i++) {
                        MutableVariant value = self.getValue(i);
                        setOptionalParameter(fc, 0, result);
                        setOptionalParameter(fc, 1, value);
                        setOptionalParameter(fc, 2, i);
                        setOptionalParameter(fc, 3, value.getKey());

                        returnValue.convertToVoid();
                        callFunction(context, fc, returnValue);

                        result.setValue(returnValue);
                    }
                }

                context.returnValue(result);
            }
        }, "fc"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "merge", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                List<MutableVariant> a = ast.function.decl.params.get(0).var.mv.getArray();
                FunctionInstance fc = ast.function.decl.params.get(1).var.mv.getFunction();

                List<MutableVariant> ret = new ArrayList<MutableVariant>();
                int size = Math.max(self.size(), a.size());
                int is = 0, ia = 0;
                prepareCall(fc);

                for (int i = 0; i < size; i++) {
                    is = i % self.size();
                    setOptionalParameter(fc, 0, self.getValue(is));
                    ia = i % a.size();
                    setOptionalParameter(fc, 1, a.get(ia));
                    setOptionalParameter(fc, 2, i);
                    setOptionalParameter(fc, 3, self.getValue(is).getKey());

                    returnValue.convertToVoid();
                    callFunction(context, fc, returnValue);

                    ret.add(new MutableVariant(returnValue));
                }

                context.returnValue(ret);
            }
        }, "array", "fc"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "union", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                List<MutableVariant> a = ast.function.decl.params.get(0).var.mv.getArray();

                MutableVariant ret = self.cloneArray();

                int size = a.size();
                for (int i = 0; i < size; i++) {
                    MutableVariant v = a.get(i);
                    if (!ret.containsValue(v)) {
                        ret.add(new MutableVariant(v));
                    }
                }

                context.returnValue(ret);
            }
        }, "array"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "intersection", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                List<MutableVariant> a = ast.function.decl.params.get(0).var.mv.getArray();

                List<MutableVariant> ret = new ArrayList<MutableVariant>();

                int size = self.size();
                for (int i = 0; i < size; i++) {
                    MutableVariant v = self.getValue(i);
                    if (a.contains(v)) {
                        ret.add(new MutableVariant(v));
                    }
                }

                context.returnValue(ret);
            }
        }, "array"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "complement", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                List<MutableVariant> a = ast.function.decl.params.get(0).var.mv.getArray();

                List<MutableVariant> ret = new ArrayList<MutableVariant>();

                int size = self.size();
                for (int i = 0; i < size; i++) {
                    MutableVariant v = self.getValue(i);
                    if (!a.contains(v)) {
                        ret.add(new MutableVariant(v));
                    }
                }

                context.returnValue(ret);
            }
        }, "array"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "difference", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                List<MutableVariant> a = ast.function.decl.params.get(0).var.mv.getArray();

                List<MutableVariant> ret = new ArrayList<MutableVariant>();

                int size = self.size();
                for (int i = 0; i < size; i++) {
                    MutableVariant v = self.getValue(i);
                    if (!a.contains(v)) {
                        ret.add(new MutableVariant(v));
                    }
                }

                size = a.size();
                for (int i = 0; i < size; i++) {
                    MutableVariant v = a.get(i);
                    if (!self.containsValue(v)) {
                        ret.add(new MutableVariant(v));
                    }
                }

                context.returnValue(ret);
            }
        }, "array"));

        dynamicMethods.put(Types.ARRAY, ExpressionFactory.functionInstance(context, "product", new MethodVisitorInjector<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                // getValue values
                MutableVariant self = ast.function.self.mv;
                List<MutableVariant> a = ast.function.decl.params.get(0).var.mv.getArray();

                List<MutableVariant> ret = new ArrayList<MutableVariant>();
                int sizeS = self.size();

                for (int is = 0; is < sizeS; is++) {
                    int sizeA = a.size();
                    for (int ia = 0; ia < sizeA; ia++) {
                        MutableVariant mv = new MutableVariant();
                        mv.add(new MutableVariant(self.getValue(is)));
                        mv.add(new MutableVariant(a.get(ia)));
                        ret.add(mv);
                    }
                }

                context.returnValue(ret);
            }
        }, "array"));

        // array.event
        dynamicMethods.put(Types.ARRAY, onChanged);

    }

    @Override
    public void addFrameworkObjects(ASTBuilder builder) {

        MethodVisitor<Block<RuntimeContext>, RuntimeContext> eval = new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                String source = ast.function.decl.params.get(0).var.mv.getString(context);

                boolean eval = true;
                EvalBlock<RuntimeContext> eb;

                // backup
                Scope backup = context.scope;
                context.scope = context.call;

                // determin if eval is needed
                if (!ast.statements.isEmpty()) {

                    eb = (EvalBlock<RuntimeContext>) ast.statements.get(0);

                    if (source.equals(eb.getSource())) {
                        eval = false;
                    }
                    eb.setSource(source);
                } else {
                    eb = new EvalBlock<RuntimeContext>(ast.getToken(), source);
                    ast.statements.add(eb);
                }

                if (eval) {

                    // eval by creating fake new block of code
                    ASTBuilder builder = new ASTBuilder();
                    context.syntax.parse(source, builder);

                    eb.statements.clear();
                    int size = builder.getProgram().size();
                    for (int i = 0; i < size; i++) {
                        eb.addStatement(builder.getProgram().get(i));
                    }

                    eb.injectVisitor(context.runtime);
                    eb.visite(context);

                } else {
                    // visite already parsed block
                    eb.visite(context);
                }

                // restore
                context.scope = backup;
            }
        };

        // root
        builder.push(DeclarationFactory.factory("print", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                FunctionInstance f = ast.function;
                MutableVariant v1 = f.decl.params.get(0).var.mv;

                if (v1.isInvalid()) {
                    System.out.println("");
                } else {
                    System.out.println(v1.getString(context));
                }
            }
        }, "v"));

        builder.push(DeclarationFactory.factory("eval", eval, "source"));

        builder.push(DeclarationFactory.factory("exists", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                FunctionInstance f = ast.function;

                // get argument parameter
                String argumentName = f.call.args.get(0).getName();
                MutableVariant mv = context.scope.resolve(Identifiers.getID(argumentName));

                context.returnValue(mv != null);
            }
        }, true, "name"));

        builder.push(DeclarationFactory.factory("fromJSon", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                FunctionInstance f = ast.function;

                // get argument parameter
                String json = f.decl.params.get(0).var.mv.getString(context);//f.call.args.get(0).mv.getString(context);
                MutableVariant mv = JSon.Parse(context, json);

                context.returnValue(mv);
            }
        }, "json"));

        // al
        ObjectDeclaration al = ExpressionFactory.object("al");
        builder.push(DeclarationFactory.factory(al));

        al.addDeclaration(DeclarationFactory.factory("clock", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                context.returnValue((System.currentTimeMillis() % 86400000) / 1000f);
            }
        }));

        al.addDeclaration(DeclarationFactory.factory("allObjects", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {

                // get tree
                final Set<String> set = new TreeSet<String>();
                Scope scope = context.getRoot();
                ALTools.accALObjects(scope, set, "", true, ALTools.FILTER_ALL);

                // store it on a MutableVariant
                MutableVariant array = new MutableVariant();
                for (String obj : set) {
                    array.add(new MutableVariant(obj));
                }

                // return value
                context.returnValue(array);
            }
        }));

        al.addDeclaration(DeclarationFactory.factory("allLocalObjects", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {

                // get tree
                final Set<String> set = new TreeSet<String>();
                Scope scope = context.call;
                ALTools.accALObjects(scope, set, "", true, ALTools.FILTER_ALL);

                // store it on a MutableVariant
                MutableVariant array = new MutableVariant();
                array.convertToArray();
                for (String obj : set) {
                    array.add(new MutableVariant(obj));
                }

                // return value
                context.returnValue(array);
            }
        }));

        // al.order
        ObjectDeclaration al_order = ExpressionFactory.object("order");
        al.addDeclaration(DeclarationFactory.factory(al_order));

        al_order.addDeclaration(DeclarationFactory.factory("reverse", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                context.returnValue(true);
            }
        }, "item1", "item2"));

        al_order.addDeclaration(DeclarationFactory.factory("ascending", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                MutableVariant item1 = ast.function.decl.params.get(0).var.mv;
                MutableVariant item2 = ast.function.decl.params.get(1).var.mv;

                if (item1.compareTo(item2) == 1) {
                    context.returnValue(true);
                } else {
                    context.returnValue(false);
                }
            }
        }, "item1", "item2"));

        al_order.addDeclaration(DeclarationFactory.factory("descending", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                MutableVariant item1 = ast.function.decl.params.get(0).var.mv;
                MutableVariant item2 = ast.function.decl.params.get(1).var.mv;

                if (item1.compareTo(item2) == 1) {
                    context.returnValue(false);
                } else {
                    context.returnValue(true);
                }
            }
        }, "item1", "item2"));

        al_order.addDeclaration(DeclarationFactory.factory("random", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                if (Math.random() >= 0.5) {
                    context.returnValue(true);
                } else {
                    context.returnValue(false);
                }
            }
        }, "item1", "item2"));

        // al.join
        ObjectDeclaration al_combine = ExpressionFactory.object("combine");
        al.addDeclaration(DeclarationFactory.factory(al_combine));

        al_combine.addDeclaration(DeclarationFactory.factory("sum", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                float item1 = ast.function.decl.params.get(0).var.mv.getNumber();
                float item2 = ast.function.decl.params.get(1).var.mv.getNumber();

                context.returnValue(item1 + item2);
            }
        }, "item1", "item2"));

        al_combine.addDeclaration(DeclarationFactory.factory("product", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                float item1 = ast.function.decl.params.get(0).var.mv.getNumber();
                float item2 = ast.function.decl.params.get(1).var.mv.getNumber();

                context.returnValue(item1 * item2);
            }
        }, "item1", "item2"));

        al_combine.addDeclaration(DeclarationFactory.factory("concat", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                String item1 = ast.function.decl.params.get(0).var.mv.getString(context);
                String item2 = ast.function.decl.params.get(1).var.mv.getString(context);
                MutableVariant separator = ast.function.decl.params.get(4).var.mv;

                String sep = "";
                if (!separator.isNull()) {
                    sep = separator.getString(context);
                }

                context.returnValue(item1 + sep + item2);
            }
        }, "item1", "item2", "index1", "key", "separator"));

        // al.type
        ObjectDeclaration al_types = ExpressionFactory.object("types");
        al.addDeclaration(DeclarationFactory.factory(al_types));

        for (Types type : Types.values()) {
            al_types.addDeclaration(DeclarationFactory.factory(type.name(), type.ordinal()));
        }

        // util
        ObjectDeclaration<RuntimeContext> util = ExpressionFactory.object("util");
        builder.push(DeclarationFactory.factory(util));

        util.addDeclaration(DeclarationFactory.factory("eval", eval, "source"));

        // math
        math = ExpressionFactory.object("math");
        builder.push(DeclarationFactory.factory(math));

        math.addDeclaration(DeclarationFactory.factory("E", (float) Math.E));

        math.addDeclaration(DeclarationFactory.factory("PI", (float) Math.PI));

        math.addDeclaration(DeclarationFactory.factory("abs", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                float p1 = ast.function.decl.params.get(0).var.mv.getNumber();

                context.returnValue(Math.abs(p1));
            }
        }, "p1"));

        math.addDeclaration(DeclarationFactory.factory("acos", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                float p1 = ast.function.decl.params.get(0).var.mv.getNumber();

                context.returnValue((float) Math.toDegrees((float) Math.acos(p1)));
            }
        }, "p1"));

        math.addDeclaration(DeclarationFactory.factory("aim", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                float p2 = ast.function.decl.params.get(1).var.mv.getNumber();

                double angle = Math.toDegrees(Math.atan(p2 / p1));

                if (p1 > 0) {
                    angle += 90;
                } else {
                    angle -= 90;
                }

                context.returnValue((float) angle);
            }
        }, "p1", "p2"));

        math.addDeclaration(DeclarationFactory.factory("asin", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                float p1 = ast.function.decl.params.get(0).var.mv.getNumber();

                context.returnValue((float) Math.toRadians(Math.asin((p1))));
            }
        }, "p1"));

        math.addDeclaration(DeclarationFactory.factory("atan", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                float p1 = ast.function.decl.params.get(0).var.mv.getNumber();

                context.returnValue((float) Math.toDegrees(Math.atan(p1)));
            }
        }, "p1"));

        math.addDeclaration(DeclarationFactory.factory("ceil", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                double p1 = ast.function.decl.params.get(0).var.mv.getNumber();

                context.returnValue((float) Math.ceil(p1));
            }
        }, "p1"));

        math.addDeclaration(DeclarationFactory.factory("cos", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                double p1 = ast.function.decl.params.get(0).var.mv.getNumber();

                context.returnValue((float) Math.cos(Math.toRadians(p1)));
            }
        }, "p1"));

        math.addDeclaration(DeclarationFactory.factory("dbl", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                float p1 = ast.function.decl.params.get(0).var.mv.getNumber();

                context.returnValue((float) Math.pow(p1, 2));
            }
        }, "p1"));

        math.addDeclaration(DeclarationFactory.factory("diagonal", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                float p2 = ast.function.decl.params.get(1).var.mv.getNumber();

                float result = (float) Math.sqrt(Math.pow(p1, 2d) + Math.pow(p2, 2d));

                context.returnValue(result);
            }
        }, "p1", "p2"));

        math.addDeclaration(DeclarationFactory.factory("exp", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                float p1 = ast.function.decl.params.get(0).var.mv.getNumber();

                context.returnValue((float) Math.exp(p1));
            }
        }, "p1"));

        math.addDeclaration(DeclarationFactory.factory("floor", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                double p1 = ast.function.decl.params.get(0).var.mv.getNumber();

                context.returnValue((float) Math.floor(p1));
            }
        }, "p1"));

        math.addDeclaration(DeclarationFactory.factory("log", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                double p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                MutableVariant p2 = ast.function.decl.params.get(1).var.mv;

                double base = Math.E;
                if (!p2.isNull()) {
                    base = p2.getNumber();
                }

                context.returnValue((float) (Math.log(p1) / Math.log(base)));
            }
        }, "p1", "p2"));

        math.addDeclaration(DeclarationFactory.factory("max", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                float p2 = ast.function.decl.params.get(1).var.mv.getNumber();

                context.returnValue(Math.max(p1, p2));
            }
        }, "p1", "p2"));

        math.addDeclaration(DeclarationFactory.factory("min", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                float p2 = ast.function.decl.params.get(1).var.mv.getNumber();

                context.returnValue(Math.min(p1, p2));
            }
        }, "p1", "p2"));

        math.addDeclaration(DeclarationFactory.factory("random", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                float p1 = ast.function.decl.params.get(0).var.mv.getNumber();

                context.returnValue((int) (Math.random() * p1));
            }
        }, "p1"));

        math.addDeclaration(DeclarationFactory.factory("round", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                float p1 = ast.function.decl.params.get(0).var.mv.getNumber();

                context.returnValue(Math.round(p1));
            }
        }, "p1"));

        math.addDeclaration(DeclarationFactory.factory("sin", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                double p1 = ast.function.decl.params.get(0).var.mv.getNumber();

                context.returnValue((float) Math.sin(Math.toRadians(p1)));
            }
        }, "p1"));

        math.addDeclaration(DeclarationFactory.factory("sqrt", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                double p1 = ast.function.decl.params.get(0).var.mv.getNumber();

                context.returnValue((float) Math.sqrt(p1));
            }
        }, "p1"));

        math.addDeclaration(DeclarationFactory.factory("tan", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                float p1 = ast.function.decl.params.get(0).var.mv.getNumber();

                context.returnValue((float) Math.tan(Math.toRadians(p1)));
            }
        }, "p1"));

        math.addDeclaration(DeclarationFactory.factory("pow", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                float p1 = ast.function.decl.params.get(0).var.mv.getNumber();
                float p2 = ast.function.decl.params.get(1).var.mv.getNumber();

                context.returnValue((float) Math.pow(p1, p2));
            }
        }, "p1", "p2"));

        // network
        ObjectDeclaration network = ExpressionFactory.object("network");
        builder.push(DeclarationFactory.factory(network));

        ObjectDeclaration<RuntimeContext> network_mqtt = ExpressionFactory.object("mqtt");
        network.addDeclaration(DeclarationFactory.factory(network_mqtt));

        network_mqtt.addDeclaration(DeclarationFactory.factory("clone", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                try {
                    ObjectInstance self = context.scope.resolve(Identifiers.getID("this")).getObject();
                    String address = ast.function.decl.params.get(0).var.mv.getString(context);
                    String clientId = ast.function.decl.params.get(1).var.mv.getString(context);

                    ObjectInstance clone = self.clone(context);
                    MqttClient mqtt = new MqttClient(address, clientId, new MemoryPersistence());
                    mqttClients.add(mqtt);
                    ObjectInstance client = new ObjectInstance(new ObjectDeclaration<RuntimeContext>(ast.getToken()), clone.scope, mqtt);
                    clone.scope.define(Identifiers.getID("client"), new MutableVariant(client));
                    clone.scope.define(Identifiers.getID("subscribers"), new MutableVariant());
                    clone.scope.define(Identifiers.getID("address"), new MutableVariant(address));
                    clone.scope.define(Identifiers.getID("counter"), new MutableVariant(0));
                    clone.scope.define(Identifiers.getID("id"), new MutableVariant(clientId));

                    MqttConnectOptions connOpts = new MqttConnectOptions();
                    connOpts.setCleanSession(false);
                    mqtt.connect(connOpts);

                    context.returnValue(clone);
                } catch (MqttException ex) {
                    throw new ALRuntimeException(new MultilingMessage(ex.toString(), ex.toString()), context.callAST.getToken());
                }
            }
        }, "p1", "p2"));

        network_mqtt.addDeclaration(DeclarationFactory.factory("dispose", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                try {
                    ObjectInstance self = context.scope.resolve(Identifiers.getID("this")).getObject();
                    MqttClient client = (MqttClient) context.scope.resolve(Identifiers.getID("client")).getObject().nativeObject;

                    client.disconnect();
                    mqttClients.remove(client);

                    MutableVariant subscribers = context.scope.resolve(Identifiers.getID("subscribers"));
                    for (MutableVariant subscriber : subscribers.getArray()) {
                        MqttClient subClient = (MqttClient) subscriber.getObject().nativeObject;
                        subClient.disconnect();
                        mqttClients.remove(client);
                    }

                } catch (MqttException ex) {
                    Logger.getLogger(RuntimeVisitor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }));

        network_mqtt.addDeclaration(DeclarationFactory.factory("publish", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
                try {
                    ObjectInstance self = context.scope.resolve(Identifiers.getID("this")).getObject();
                    String topic = ast.function.decl.params.get(0).var.mv.getString(context);
                    String content = ast.function.decl.params.get(1).var.mv.getString(context);

                    MqttClient client = (MqttClient) context.scope.resolve(Identifiers.getID("client")).getObject().nativeObject;
                    MqttMessage message = new MqttMessage(content.getBytes());
                    message.setQos(2);
                    client.publish(topic, message);

                } catch (MqttException ex) {
                    throw new ALRuntimeException(new MultilingMessage(ex.toString(), ex.toString()), context.callAST.getToken());
                }
            }
        }, "p1", "p2"));

        network_mqtt.addDeclaration(DeclarationFactory.factory("subscribe", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(final Block<RuntimeContext> ast, final RuntimeContext context) {
                try {
                    ObjectInstance self = context.scope.resolve(Identifiers.getID("this")).getObject();
                    String topic = ast.function.decl.params.get(0).var.mv.getString(context);
                    final FunctionInstance function = ast.function.decl.params.get(1).var.mv.getFunction();

                    String address = context.scope.resolve(Identifiers.getID("address")).getString(context);
                    int counter = (int) context.scope.resolve(Identifiers.getID("counter")).getNumber();
                    context.scope.resolve(Identifiers.getID("counter")).setValue(counter + 1);

                    MqttClient subscriber = new MqttClient(address, "subscriber" + counter, new MemoryPersistence());
                    mqttClients.add(subscriber);
                    ObjectInstance subInstance = new ObjectInstance(new ObjectDeclaration<RuntimeContext>(ast.getToken()), self.scope, subscriber);
                    MutableVariant subscribers = self.scope.resolve(Identifiers.getID("subscribers"));
                    subscribers.add(new MutableVariant(subInstance));

                    subscriber.connect();
                    subscriber.setCallback(new MqttCallback() {

                        @Override
                        public void connectionLost(Throwable throwable) {
                            throw new ALRuntimeException(new MultilingMessage("MQTT connection lost !", "Connection MQTT perdue !"), context.callAST.getToken());
                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            setOptionalParameter(function, 0, topic);
                            setOptionalParameter(function, 1, message.toString());
                            callbackFunction(context, function, returnValue);
                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken deliveryToken) {
                        }
                    });
                    subscriber.subscribe(topic);

                } catch (MqttException ex) {
                    throw new ALRuntimeException(new MultilingMessage(ex.toString(), ex.toString()), context.callAST.getToken());
                }
            }
        }, "p1", "p2"));

        network_mqtt.addDeclaration(DeclarationFactory.factory("expect", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

            @Override
            public void visite(Block<RuntimeContext> ast, final RuntimeContext context) {
                try {
                    ObjectInstance self = context.scope.resolve(Identifiers.getID("this")).getObject();
                    String topic = ast.function.decl.params.get(0).var.mv.getString(context);
                    final MutableVariant predicat = ast.function.decl.params.get(1).var.mv;
                    MutableVariant timeoutVariant = ast.function.decl.params.get(2).var.mv;

                    String address = context.scope.resolve(Identifiers.getID("address")).getString(context);
                    int counter = (int) context.scope.resolve(Identifiers.getID("counter")).getNumber();
                    context.scope.resolve(Identifiers.getID("counter")).setValue(counter + 1);

                    final Object sync = new Object();

                    MqttClient subscriber = new MqttClient(address, "subscriber" + counter, new MemoryPersistence());
                    subscriber.connect();
                    subscriber.setCallback(new MqttCallback() {

                        @Override
                        public void connectionLost(Throwable throwable) {
                            throw new ALRuntimeException(new MultilingMessage("MQTT connection lost !", "Connection MQTT perdue !"), context.callAST.getToken());
                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            boolean terminate = false;
                            if (predicat.isFunction()) {
                                FunctionInstance func = predicat.getFunction();
                                setOptionalParameter(func, 0, topic);
                                setOptionalParameter(func, 1, message.toString());
                                callbackFunction(context, func, returnValue);
                                terminate = returnValue.getBool() || returnValue.isNull();
                            } else {
                                terminate = true;
                            }

                            if (terminate == true) {
                                synchronized (sync) {
                                    sync.notify();
                                }
                                subscriber.unsubscribe(topic);
                                context.returnValue(message.toString());
                            }
                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken deliveryToken) {
                        }
                    });
                    subscriber.subscribe(topic);

                    synchronized (sync) {
                        if (timeoutVariant.isBool()) {
                            sync.wait((long) timeoutVariant.getNumber());
                        } else {
                            sync.wait();
                        }
                    }

                } catch (MqttException ex) {
                    throw new ALRuntimeException(new MultilingMessage(ex.toString(), ex.toString()), context.callAST.getToken());
                } catch (InterruptedException ex) {
                    // do nothing
                }
            }
        }, "p1", "p2", "p3"));

    }

    @Override
    public void clearRessources() {
        clearMqttClients();
    }

}
