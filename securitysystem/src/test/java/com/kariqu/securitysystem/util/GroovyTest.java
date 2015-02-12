package com.kariqu.securitysystem.util;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.junit.Test;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-8-30
 *        Time: 下午2:45
 */
public class GroovyTest {

    @Test
    public void testGroovyShell() {
        GroovyShell shell = new GroovyShell();
        String scriptText = "def mul(x, y) { x * y }\n println mul(5, 7)";
        Script script = shell.parse(scriptText);
        script.run();
        System.out.println(script.getClass());
    }

    @Test
    public void testScriptLoader() {
        String scriptText = "def mul(x, y) { x * y }\n println mul(5, 7)";
        GroovyClassLoader loader = new GroovyClassLoader();
        Class<?> newClazz = loader.parseClass(scriptText);
        try {
            Object obj = newClazz.newInstance();
            Script script = (Script) obj;
            script.run();
            System.out.println(newClazz);
            System.out.println(obj.getClass());
            System.out.println(script.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
