/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.json;

import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.jasi.builder.ASTBuilder;

/**
 *
 * @author cyann
 */
public class JSon {

    private JSon() {
        throw new RuntimeException("Cannot instanciate static class !");
    }

    public static MutableVariant Parse(RuntimeContext runtimeContext, String json) {

        ASTBuilder builder = new ASTBuilder();
        JSonContext context = new JSonContext();
        context.runtimeContext = runtimeContext;

        JSonSyntax syntax = new JSonSyntax();
        syntax.parse(json, builder);
        JSonVisitor visitor = new JSonVisitor();

        // clear gc before execution
        System.gc();

        builder.injectVisitor(visitor);
        builder.visite(context);
        
        return context.currentmv;
    }

}
