package br.inatel.dm112.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.inatel.dm112.model.Order;
import br.inatel.dm112.model.dao.OrderRepository;
import br.inatel.dm112.model.entities.OrderEntity;
import br.inatel.dm112.rest.support.OrderNotFoundException;

@Service
public class OrderService extends RestTemplate {

	@Autowired
	private OrderRepository repo;

	public OrderEntity getOrder(int orderNumber) {

		Optional<OrderEntity> obj = repo.findById(orderNumber);
		return obj.orElseThrow(() -> new OrderNotFoundException("Order " + orderNumber + " not found."));
	}

	public void updateOrder(Order order, Integer orderNumber) {

		OrderEntity entity = getOrder(orderNumber);
		updateOrderData(order, entity); // don't change PK
		repo.save(entity);
		System.out.println("OrderService updateOrder - atualizou o pedido: " + order.getNumber());
	}
	
	public void orderDeliveryStart(Integer orderNumber) {
		OrderEntity entity = getOrder(orderNumber);
		entity.setStatus(1);
		repo.save(entity);
		System.out.println("Iniciando entrega do pedido " + orderNumber + ".");
	}
	
	public void orderDeliveryEnd(Integer orderNumber) {
		OrderEntity entity = getOrder(orderNumber);
		entity.setStatus(2);
		repo.save(entity);
		System.out.println("Finalizando entrega do pedido " + orderNumber + ".");
	}
	
	public OrderEntity createOrder(Order order) {

		OrderEntity entity = convertToEntity(order);
		repo.save(entity);
		System.out.println("OrderService createOrder - pedido criado com número: " + entity.getNumber());
		return entity;
	}

	public List<Order> getAllOrders() {
		List<OrderEntity> entities = repo.findAll();
		List<Order> orders = new ArrayList<>();

		for (OrderEntity entity : entities) {
			Order order = convertToOrder(entity);
			orders.add(order);
		}
		return orders;
	}

	private void updateOrderData(Order order, OrderEntity entity) {
		entity.setCPF(order.getCpf());
		entity.setValue(order.getValue());
		entity.setStatus(order.getStatus());
		entity.setOrderDate(order.getOrderDate());
		entity.setIssueDate(order.getIssueDate());
		entity.setPaymentDate(order.getPaymentDate());
	}

	public static Order convertToOrder(OrderEntity entity) {
		Order order = new Order(entity.getNumber(), entity.getCPF(), entity.getValue(), entity.getStatus(),
				entity.getOrderDate(), entity.getIssueDate(), entity.getPaymentDate());
		return order;
	}

	public static OrderEntity convertToEntity(Order order) {
		OrderEntity entity = new OrderEntity();
		entity.setCPF(order.getCpf());
		entity.setValue(order.getValue());
		entity.setStatus(order.getStatus());
		entity.setOrderDate(order.getOrderDate());
		entity.setIssueDate(order.getIssueDate());
		entity.setPaymentDate(order.getPaymentDate());
		return entity;
	}

}