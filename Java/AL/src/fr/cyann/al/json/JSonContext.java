/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.json;

import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.jasi.visitor.Context;

/**
 *
 * @author cyann
 */
public class JSonContext implements Context {

    RuntimeContext runtimeContext;
    MutableVariant mv = new MutableVariant();
    MutableVariant currentmv = mv;
    
    public MutableVariant getValue() {
        return currentmv;
    }
    
}
