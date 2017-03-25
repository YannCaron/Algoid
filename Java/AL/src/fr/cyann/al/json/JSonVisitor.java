/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.json;

import fr.cyann.al.ast.declaration.ObjectDeclaration;
import fr.cyann.al.ast.declaration.VariableDeclaration;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.data.ObjectInstance;
import fr.cyann.al.factory.DeclarationFactory;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.visitor.ClassMapVisitorInjector;
import fr.cyann.jasi.visitor.MethodVisitorInjector;
import fr.cyann.jasi.visitor.VisitorInjector;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cyann
 */
public class JSonVisitor extends ClassMapVisitorInjector {

    @Override
    public Map<Class<? extends AST>, VisitorInjector> getVisitorMap() {
        Map<Class<? extends AST>, VisitorInjector> map = new HashMap<Class<? extends AST>, VisitorInjector>();

        map.put(Null.class, new MethodVisitorInjector<Null, JSonContext>() {

            @Override
            public void visite(Null ast, JSonContext context) {
                context.currentmv = new MutableVariant();
            }
        });

        map.put(Bool.class, new MethodVisitorInjector<Bool, JSonContext>() {

            @Override
            public void visite(Bool ast, JSonContext context) {
                String value = ast.getToken().getText();

                if ("true".equals(value)) {
                    context.currentmv = new MutableVariant(true);
                } else {
                    context.currentmv = new MutableVariant(false);
                }
            }
        });

        map.put(Number.class, new MethodVisitorInjector<Number, JSonContext>() {

            @Override
            public void visite(Number ast, JSonContext context) {
                String value = ast.getToken().getText();
                context.currentmv = new MutableVariant(Float.parseFloat(value));
            }
        });

        map.put(StringAST.class, new MethodVisitorInjector<StringAST, JSonContext>() {

            @Override
            public void visite(StringAST ast, JSonContext context) {
                String value = ast.getToken().getText();

                context.currentmv = new MutableVariant(value.substring(1, value.length() - 1));
            }
        });

        map.put(Association.class, new MethodVisitorInjector<Association, JSonContext>() {

            @Override
            public void visite(Association ast, JSonContext context) {
                MutableVariant array = context.currentmv;

                // key
                MutableVariant key = null;
                if (ast.getAssoName() != null) {
                    ast.getAssoName().visite(context);
                    key = context.currentmv;
                }

                // value
                ast.getValue().visite(context);
                MutableVariant value = context.currentmv;

                array.add(value);

                if (key != null) {
                    array.addKey(key, array.size() - 1);
                }

                context.currentmv = array;
            }
        });

        map.put(Array.class, new MethodVisitorInjector<Array, JSonContext>() {

            @Override
            public void visite(Array ast, JSonContext context) {
                context.currentmv = new MutableVariant();

                for (AST child : ast.getChildren()) {
                    child.visite(context);
                }
            }
        });

        map.put(ObjectAST.class, new MethodVisitorInjector<ObjectAST, JSonContext>() {

            @Override
            public void visite(ObjectAST ast, JSonContext context) {
                context.currentmv = new MutableVariant();

                for (AST child : ast.getChildren()) {
                    child.visite(context);
                }

                ObjectDeclaration object = new ObjectDeclaration(ast.getToken());
                for (MutableVariant mv : context.currentmv.getArray()) {
                    object.addDeclaration(DeclarationFactory.factory(mv.getKey().toString(), mv));
                }
                
                object.injectVisitor(context.runtimeContext.runtime);
                ObjectInstance instance = new ObjectInstance(object, context.runtimeContext.scope);
                instance.build(context.runtimeContext);
                
                context.currentmv = new MutableVariant(instance);
            }
        });

        return map;
    }

}
