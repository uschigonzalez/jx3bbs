/**   
 * @Title: DBKeyName.java 
 * @Package com.yhiker.playmate.core.db 
 * @Description: TODO
 * @author xiaowei   
 * @date 2012-8-7 ����10:12:53 
 * @version V1.0   
 */
package com.android.xiaow.core.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author ���� xiaowei
 * @����ʱ�� 2012-8-7 ����10:12:53 ��˵��
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DBPrimaryKeyName {
	/**
	 * @return the desired name of the field when it is serialized
	 */
	String value();
}
