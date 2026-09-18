package com.empresa.tiendaonline.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "factura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFactura;

    @Column(unique = true, nullable = false)
    private String numeroFactura;

    @OneToOne
    @JoinColumn(
            name = "id_pedido",
            nullable = false,
            unique = true
    )
    private Pedido pedido;

    @Builder.Default
    private LocalDateTime fechaEmision =
            LocalDateTime.now();

    private String tipoPago;

    private BigDecimal total;
}