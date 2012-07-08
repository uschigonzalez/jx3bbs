package com.android.xiaow.jx3bbs.common;

import java.util.ArrayList;
import java.util.List;

import com.android.xiaow.jx3bbs.utils.HttpUtil;

public class SubBoard {
	//含有子版块的版块名称
	public static List<String> lists;
	
	public static void addSubBoard(String str){
		if(lists==null)
			lists=new ArrayList<String>();
		lists.add(str);
	}
	
	public static boolean validContact(String str){
		if(!HttpUtil.validStr(str))
			return false;
		if(lists.contains(str.trim()))
			return true;
		return false;
	}
}
