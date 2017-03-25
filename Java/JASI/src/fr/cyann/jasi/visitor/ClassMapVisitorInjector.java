
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
package fr.cyann.jasi.visitor;

import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.exception.JASIException;
import fr.cyann.jasi.exception.MultilingMessage;
import fr.cyann.jasi.exception.VisitorNotFoundException;
import java.util.Map;

/**
 * The ClassMapVisitorInjector class.
 *
 * @author Yann Caron
 * @version v0.1
 */
public abstract class ClassMapVisitorInjector implements VisitorInjector {

    private Map<Class<? extends AST>, VisitorInjector> map;

    /**
     * Template method inspired from [GoF].<br>
     * Configure here your matching between AST class and method to execute when
     * visited.<br>
     * Prefer the HashMap for more efficiency.
     *
     * @return the injector.
     */
    public abstract Map<Class<? extends AST>, VisitorInjector> getVisitorMap();

    /**
     * Override it to specify a special visitor to execute in case of no visitor
     * found for the class.<br>
     * Throw an exception by default.
     *
     * @param ast the ast to visite.
     * @return the injector.
     */
    protected VisitorInjector getDefault(AST ast) {
        throw new VisitorNotFoundException(ast.getClass());
    }

    /**
     * @inheritDoc
     */
    @Override
    public MethodVisitor getVisitor(AST ast) {
        // lazy initialization
        if (map == null) {
            map = getVisitorMap();
        }

        Class c = ast.getClass();
        if (map == null) {
            throw new JASIException(new MultilingMessage(
                    "Visitor injector not initialized !",
                    "Visitor injector non initialisé !"), ast.getToken().getPos());
        }

        VisitorInjector v = map.get(c);

        // get if visitor is not set on super class.
        while (v == null && !c.equals(Object.class)) {
            c = c.getSuperclass();
            v = map.get(c);
        }

        if (v == null) {
            v = getDefault(ast);
        }

        return v.getVisitor(ast);
    }
}
