package com.bookStore.client.user.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.bookStore.commons.beans.Order;
import com.bookStore.commons.beans.OrderItem;
import com.bookStore.commons.beans.User;

public interface IUserDao {

	User selectEmail(String email);

	int insertUser(User user);

	int updateUserByActiveCode(String activeCode);

	User selectUserByUsernameAndPassword(User user);

	int updateUser(User user);

	List<Order> selectOrderByUser(Integer id);

	List<OrderItem> selectOrderItemById(String id);

	void deleteOrderById(String id);

	void deleteOrderItemById(String id);

	User selectUsername(String username);

	void addProductPnum(@Param("id")String id,@Param("buynum") int buynum);

}
