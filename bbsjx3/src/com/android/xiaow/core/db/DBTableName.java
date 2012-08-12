/**   
* @Title: SqliteDataBaseTableName.java 
* @Package com.hiker.onebyone.data.annotations 
* @Description: TODO
* @author xiaowei   
* @date 2012-7-19 ����3:45:41 
* @version V1.0   
*/
package com.android.xiaow.core.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 *@author ���� xiaowei
 *@����ʱ��  2012-7-19 ����3:45:41
 * ��˵��
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DBTableName {
	/**
	 * @return the desired name of the field when it is serialized
	 */
	String value();
}
