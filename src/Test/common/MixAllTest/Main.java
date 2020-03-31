package Test.common.MixAllTest;

import java.lang.reflect.Method;

public class Main {
    public static void main(String[] args){
        Test test=new Test();
        Method[] methods = test.getClass().getMethods();
    }
}
