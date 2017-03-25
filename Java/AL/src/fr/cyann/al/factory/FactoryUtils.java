/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.factory;

import fr.cyann.al.ast.Block;
import fr.cyann.al.ast.declaration.ArrayDeclaration;
import fr.cyann.al.ast.declaration.ObjectDeclaration;
import fr.cyann.al.ast.declaration.VariableDeclaration;
import fr.cyann.al.data.Identifiers;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.data.ObjectInstance;
import fr.cyann.al.data.Types;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.builder.ASTBuilder;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.MethodVisitor;
import fr.cyann.jasi.visitor.VisitorInjector;

/**
 <p>
 @author caronyn
 */
public abstract class FactoryUtils {

	public static final MutableVariant DEFAULT_VALUE = new MutableVariant();

	/**
	 The class that contains function behaviour. When function is called the
	 visite method is called.
	 */
	public static abstract class Behaviour implements MethodVisitor<Block<RuntimeContext>, RuntimeContext> {
	};

	/**
	 Find existing AL object in the framework. It is possible to extends algo,
	 text or whatever functionality.
	 <p>
	 @param builder the AST builder. Add expression to it to create the
	                framework.
	 @param name    the object name
	 <p>
	 @return the object instance
	 */
	public static ObjectDeclaration<RuntimeContext> findObject(ASTBuilder builder, String name) {

		for (AST ast : builder.getProgram()) {
			if (ast.getName().equals(name)) {
				VariableDeclaration<RuntimeContext> var = (VariableDeclaration<RuntimeContext>) ast;
				return (ObjectDeclaration<RuntimeContext>) var.getExpr();
			}
		}
		return null;

	}

	/**
	 Create a new object in the framework.
	 <p>
	 @param builder the AST builder. Add expression to it to create the
	                framework.
	 @param name    the object name
	 <p>
	 @return the object instance
	 */
	public static ObjectDeclaration<RuntimeContext> addObject(ASTBuilder builder, String name) {

		ObjectDeclaration<RuntimeContext> object = ExpressionFactory.object(name);
		builder.push(DeclarationFactory.factory(object));
		return object;

	}

	/**
	Create a new object instance and build it. Useful for dynamic object creation inside a factory function.
	@param decl the object declaration
	@param injector the runtime
	@param context the context
	@return 
	*/
	public static ObjectInstance newObject(ObjectDeclaration<RuntimeContext> decl, VisitorInjector injector, RuntimeContext context) {
		ObjectInstance inst = new ObjectInstance(decl, context.root);
		inst.decl.injectVisitor(injector);
		inst.build(context);
		return inst;
	}

		/**
	Create a new object instance and build it. Useful for dynamic object creation inside a factory function.
	@param decl the object declaration
	@param injector the runtime
	@param context the context
	@param nativeObject the java native object, if instance is a java object wrapper
	@return 
	*/
	public static ObjectInstance newObject(ObjectDeclaration<RuntimeContext> decl, VisitorInjector injector, RuntimeContext context, Object nativeObject) {
		ObjectInstance inst = new ObjectInstance(decl, context.root, nativeObject);
		inst.decl.injectVisitor(injector);
		inst.build(context);
		return inst;
	}

	/**
	 Add attribute to the object. The attribute take value nil and type VOID.
	 <p>
	 @param obj  the object to which add attribute.
	 @param name the attribute name.
	 */
	public static void addAttribute(ObjectDeclaration<RuntimeContext> obj, String name) {
		obj.addDeclaration(DeclarationFactory.factory(name));
	}

	/**
	 Add attribute to the object. The attribute take value given and type
	 BOOLEAN.
	 <p>
	 @param obj  the object to which add attribute.
	 @param name the attribute name.
	 */
	public static void addAttribute(ObjectDeclaration<RuntimeContext> obj, String name, boolean b) {
		obj.addDeclaration(DeclarationFactory.factory(name, b));
	}

	/**
	 Add attribute to the object. The attribute take value given and type
	 NUMBER.
	 <p>
	 @param obj  the object to which add attribute.
	 @param name the attribute name.
	 */
	public static void addAttribute(ObjectDeclaration<RuntimeContext> obj, String name, float f) {
		obj.addDeclaration(DeclarationFactory.factory(name, f));
	}

	/**
	 Add attribute to the object. The attribute take value given and type
	 STRING.
	 <p>
	 @param obj  the object to which add attribute.
	 @param name the attribute name.
	 */
	public static void addAttribute(ObjectDeclaration<RuntimeContext> obj, String name, String s) {
		obj.addDeclaration(DeclarationFactory.factory(name, s));
	}

	/**
	 Add attribute to the object. The attribute take value given and type
	 MutableVariant.
	 <p>
	 @param obj  the object to which add attribute.
	 @param name the attribute name.
	 */
	public static void addAttribute(ObjectDeclaration<RuntimeContext> obj, String name, MutableVariant mv) {
		obj.addDeclaration(DeclarationFactory.factory(name, mv));
	}

	/**
	 Add attribute to the object. The attribute take value given and type
	 ARRAY.<br>
	 example :<br>
	 <code>
	 MutableVariant mv = new MutableVariant();
	 mv.add(new MutableVariant(1));
	 mv.add(new MutableVariant(2));
	 mv.add(new MutableVariant(3));
	 FactoryUtils.addArray(obj, "myarray", mv);
	 </code>
	 <p>
	 @param obj  the object to which add attribute.
	 @param name the attribute name.
	 */
	public static void addArray(ObjectDeclaration<RuntimeContext> obj, String name, MutableVariant a) {
		ArrayDeclaration<RuntimeContext> array = ExpressionFactory.array(a);
		obj.addDeclaration(DeclarationFactory.factory(name, array));
	}

	/**
	 Add method to the object.
	 <p>
	 @param obj       the object to which add attribute.
	 @param name      the method name.
	 @param behaviour Behaviour.visite will be call each time the function is
	                  called in the script.<br>
	 On this visitor, it is possible to interract with everything in the IDE and
	 runtime panels (algo, text, log etc.).
	 @param params    The list of function parameter names.<br>
	 Parameters can be retreived with FactoryUtils.getParam methods.
	 */
	public static void addMethod(ObjectDeclaration<RuntimeContext> obj, String name, Behaviour behaviour, String... params) {
		obj.addDeclaration(DeclarationFactory.factory(name, behaviour, params));
	}

	/**
	 Add method to the object.
	 <p>
	 @param obj       the object to which add attribute.
	 @param name      the method name.
	 @param behaviour Behaviour.visite will be call each time the function is
	                  called in the script.<br>
	 On this visitor, it is possible to interract with everything in the IDE and
	 runtime panels (algo, text, log etc.).
	 @param params    The list of function parameter names.<br>
	 Parameters can be retreived with FactoryUtils.getParam methods.
	 */
	public static void addMagicMethod(RuntimeContext context, TypeNameFunctionMap dynamicMethods, Types type, String name, Behaviour behaviour, String... params) {
		dynamicMethods.put(type, ExpressionFactory.functionInstance(context, name, behaviour, params));
	}

	/**
	 Add nested object to the object.
	 <p>
	 @param obj  the object to which add nested one.
	 @param name the nested object name.
	 <p>
	 @return the nested object instance.
	 */
	public static ObjectDeclaration<RuntimeContext> addObject(ObjectDeclaration<RuntimeContext> obj, String name) {
		ObjectDeclaration<RuntimeContext> nestedObj = ExpressionFactory.object(name);
		obj.addDeclaration(DeclarationFactory.factory(nestedObj));
		return nestedObj;
	}

	/**
	 Get the function parameter by ID.
	 <p>
	 @param ast the block AST of the function
	 @param id  the range of the parameter.<br>
	 For performance considerations, the range of the parameter is used instead
	 of its name.<br>
	 0 is the first parameter, 1 the second etc...<br>
	 example :<br>
	 <code>
	 FactoryUtils.addMethod(object, "function", new FactoryUtils.Behaviour() {
	 <p>
	 @Override public void visite(Block<RuntimeContext> ast, RuntimeContext c) {
	 // retreive the first parameter called myString String myString =
	 FactoryUtils.getParam(ast, 0).getString(); // retreive the second parameter
	 called myNumber float myNumber = FactoryUtils.getParam(ast, 1).getNumber();
	 } }, "myString", "myNumber");
	 </code>
	 @return A mutable variant that is able ton contains all the AL types :
	         VOID, BOOL, NUMBER, STRING, ARRAY, FUNCTION, OBJECT<br>
	 Use the the getXXXX methods to have the java value.<br>
	 For example: mv.getString() will return the string contains in the
	 variant.<br>
	 If initial type is different from the wanted one, the internal value is
	 converted.<br>
	 Be carrefull, FUNCTION, OBJECT cannot been converted, convertion of them
	 will throws an exception.
	 */
	public static MutableVariant getParam(Block<RuntimeContext> ast, int id) {
		return ast.function.decl.params.get(id).var.mv;
	}

	/**
	 Like @see FactoryUtils#getParam(fr.cyann.al.ast.Block, int) but optionnaly
	 <p>
	 @param ast the block AST of the function
	 @param id  the range of the parameter.<br>
	 <p>
	 @return the value mutable variant.
	 */
	public static MutableVariant getOptionalParam(Block<RuntimeContext> ast, int id) {

		MutableVariant mv = ast.function.decl.params.get(id).var.mv;

		if (mv == null) {
			return DEFAULT_VALUE;
		} else {
			return mv;
		}

	}

	/**
	 Get the current object (this / self) parameter value by its name.
	 <p>
	 @param context The language runtime context.
	 @param name    the parameter name.
	 <p>
	 @return the value.
	 */
	public static MutableVariant getAttr(RuntimeContext context, String name) {
		return context.scope.resolve(Identifiers.getID("this")).getObject().scope.resolve(Identifiers.getID(name));
	}

	/**
	 Get the object that called the dynamic function.
	 <p>
	 @param ast the dynamic function block
	 <p>
	 @return the instance of the object
	 */
	public static MutableVariant getSelf(Block<RuntimeContext> ast) {
		return ast.function.self.mv;
	}
}
