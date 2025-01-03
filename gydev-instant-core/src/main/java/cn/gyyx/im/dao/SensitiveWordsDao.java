package cn.gyyx.im.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
  * 敏感词数据库操作
  * @author: zxw
  * @date: 2024/4/1/0001 14:31
  **/
@Repository
@Mapper
public interface SensitiveWordsDao {

	/**
	  * 获取所有敏感词数据
	  * @author: zxw
	  * @date: 2024/4/1/0001 14:30
	  * @return java.util.List<java.lang.String>
	  **/
	@Select("select word from sensitive_word_tb")
	List<String> getSensitiveWord();
}
