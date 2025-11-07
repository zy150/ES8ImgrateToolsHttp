/**   
 * Simple to Introduction  
 * @Description:  base enum in mybatis for translate 
 * @Author:       wenyujun
 * @CreateDate:   2019-05-07
 * @UpdateUser:   
 * @UpdateDate:   
 * @UpdateRemark: 
 * @Version:      [v1.0] 
 *    
 */

package cuc.cdnews.domain;

public interface BaseEnum<E extends Enum<?>, T> {

	public T getValue();
	public String getDisplayName();
}
