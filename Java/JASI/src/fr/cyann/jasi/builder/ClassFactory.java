
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
package fr.cyann.jasi.builder;

import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.exception.FactoryException;
import fr.cyann.jasi.parser.StatementLeafToken;
import java.lang.reflect.Constructor;

/**
 * The ClassFactory class.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class ClassFactory implements FactoryStrategy {

    private Class<? extends AST> cls;

    /**
     * Default constructor. Set the AST class to instancing parameter.
     *
     * @param cls the AST class to instancing.
     */
    public ClassFactory(Class<? extends AST> cls) {
        this.cls = cls;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void buildLeaf(InterpreterBuilder builder, StatementLeafToken statement) {
        AST ast = null;
        try {
            Constructor<? extends AST> constructor = cls.getConstructor(statement.getToken().getClass());
            ast = constructor.newInstance(statement.getToken());
        } catch (Exception ex) {
            try {
                ast = cls.newInstance();
            } catch (FactoryException ex1) {
                throw ex1;
            } catch (Exception ex2) {
                throw new FactoryException(ex, cls);
            }
        }

        builder.push(ast);
    }
}
