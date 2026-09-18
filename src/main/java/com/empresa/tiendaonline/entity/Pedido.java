package com.empresa.tiendaonline.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    
@Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado = EstadoPedido.CARRITO;

    @Builder.Default
    @OneToMany(
        mappedBy = "pedido",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<DetallePedido> detalles = new ArrayList<>();

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Envio envio;

@PrePersist
public void prePersist(){

    if(fecha == null){
        fecha = LocalDateTime.now();
    }

    if(total == null){
        total = BigDecimal.ZERO;
    }


    
    if(estado == null){
        estado = EstadoPedido.CARRITO;
    }


}
@Builder.Default
@Column(nullable = false)
private BigDecimal totalFinal = BigDecimal.ZERO;
@OneToOne(
        mappedBy = "pedido",
        cascade = CascadeType.ALL
)
private Factura factura;
}