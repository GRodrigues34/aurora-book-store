package com.github.gr.aurora_bookstore.model.mapper;

import com.github.gr.aurora_bookstore.dto.orderDto.OrderCreateDTO;
import com.github.gr.aurora_bookstore.dto.orderDto.OrderItemCreateDTO;
import com.github.gr.aurora_bookstore.dto.orderDto.OrderItemReadDTO;
import com.github.gr.aurora_bookstore.dto.orderDto.OrderReadDTO;
import com.github.gr.aurora_bookstore.model.entity.Book;
import com.github.gr.aurora_bookstore.model.entity.OrderItem;
import com.github.gr.aurora_bookstore.model.entity.Orders;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderItemReadDTO toItemReadDto(OrderItem orderItem) {
        if (orderItem == null) return null;
        OrderItemReadDTO dto = new OrderItemReadDTO();
        dto.setBook(BookMapper.toReadDto(orderItem.getBook()));
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getItemPrice());
        dto.setTotalPrice(orderItem.getTotalPrice());
        return dto;
    }

    public static OrderReadDTO toReadDto(Orders order) {
        if (order == null) return null;
        OrderReadDTO dto = new OrderReadDTO();
        dto.setOrderId(order.getId());
        dto.setDate(order.getDate());
        if (order.getOrderItems() != null) {
            dto.setOrderItems(order.getOrderItems().stream()
                    .map(OrderMapper::toItemReadDto)
                    .collect(Collectors.toSet()));
        }
        dto.setTotalPrice(order.getTotalPrice());
        return dto;
    }

    public static OrderItem toItemEntity(OrderItemCreateDTO dto) {
        if (dto == null) return null;
        OrderItem item = new OrderItem();
        if (dto.getBookId() != null) {
            Book book = new Book();
            book.setId(dto.getBookId());
            item.setBook(book);
        }
        item.setQuantity(dto.getQuantity());
        return item;
    }

    public static Orders toEntity(OrderCreateDTO dto) {
        if (dto == null) return null;
        Orders order = new Orders();
        Set<OrderItem> items = new HashSet<>();
        if (dto.getOrderItems() != null) {
            for (OrderItemCreateDTO itemDto : dto.getOrderItems()) {
                OrderItem item = toItemEntity(itemDto);
                if (item != null) {
                    item.setOrder(order);
                    items.add(item);
                }
            }
        }
        order.setOrderItems(items);
        return order;
    }

}


