package com.payloads.online;


import java.io.IOException;
import java.lang.instrument.Instrumentation;
public class DumperAgent {

    public static void premain(String agentArgs, Instrumentation inst) throws IOException {
        if (agentArgs != null){
            System.out.println(agentArgs);
            inst.addTransformer(new DefineTransformer(agentArgs));
        }
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("agentmain");
    }

    public static void main(String[] args){
        System.out.println("Please use the -javaagent option to load the jar package");
    }
}
