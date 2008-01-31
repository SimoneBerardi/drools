package org.drools.clips;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.drools.Person;
import org.drools.clips.functions.BindFunction;
import org.drools.clips.functions.ModifyFunction;
import org.drools.clips.functions.PlusFunction;
import org.drools.clips.valuehandlers.FunctionCaller;
import org.drools.clips.valuehandlers.ListValueHandler;
import org.drools.clips.valuehandlers.LongValueHandler;
import org.drools.clips.valuehandlers.ObjectValueHandler;

public class BlockExecutionTest extends TestCase {
    
    XFunctionRegistry registry;
    
    public void setUp() {
        this.registry = new XFunctionRegistry( BuiltinFunctions.getInstance() );
    }    
    
    public void testAddWithModify() {
        BlockExecutionEngine engine = new BlockExecutionEngine();        
        BuildContext build = new ExecutionBuildContext(engine, this.registry );                
        
        FunctionCaller addCaller = new FunctionCaller( new PlusFunction() );
        addCaller.addParameter( new ObjectValueHandler( new BigDecimal( 20) ) );
        addCaller.addParameter( new LongValueHandler( "11" ) );
                
        FunctionCaller bindCaller = new FunctionCaller( new BindFunction() );
        bindCaller.addParameter( build.createLocalVariable( "?x" ) );
        bindCaller.addParameter( addCaller );
        
        engine.addFunction( bindCaller );
        
        
        FunctionCaller modifyCaller = new FunctionCaller( new ModifyFunction() );        
        build.createLocalVariable( "?p" );        
        modifyCaller.addParameter( build.getVariableValueHandler( "?p" ) );
        
        ListValueHandler list = new ListValueHandler();
        list.add( new ObjectValueHandler( "age") );
        list.add( build.getVariableValueHandler( "?x" ) );
        modifyCaller.addParameter( list );
        
        ExecutionContext context = new ExecutionContextImpl(null, null, 2);
        Person p = new Person("mark");
        context.setLocalVariable( 1, new ObjectValueHandler( p ) );
        
        engine.addFunction( modifyCaller );
        
        engine.execute( context );
        
        assertEquals( 31, p.getAge() );
        
        
    }
}