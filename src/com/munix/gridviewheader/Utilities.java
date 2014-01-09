package com.munix.gridviewheader;

import android.util.Log;

public class Utilities 
{
	public static void log(String message) 
	{
		String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();            
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
        int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();

        Log.v("minube " + className + "." + methodName + "():" + lineNumber, message);
	}
}