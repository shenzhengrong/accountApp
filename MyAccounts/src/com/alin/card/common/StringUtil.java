package com.alin.card.common;

public class StringUtil
{

	public static boolean isEmpty(String source)
	{
		if(source == null || "".equals(source.trim()))
		{
			return true;
		}
		return false;
	}
	
}
