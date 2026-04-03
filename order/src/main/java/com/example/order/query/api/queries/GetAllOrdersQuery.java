package com.example.order.query.api.queries;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetAllOrdersQuery {

    private final String customerId;
}
