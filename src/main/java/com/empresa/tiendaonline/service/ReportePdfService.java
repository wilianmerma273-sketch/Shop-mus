package com.empresa.tiendaonline.service;


import com.empresa.tiendaonline.entity.*;
import com.empresa.tiendaonline.repository.*;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class ReportePdfService {


    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final EnvioRepository envioRepository;
    private final FacturaRepository facturaRepository;


    public ReportePdfService(
            UsuarioRepository usuarioRepository,
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository,
            PedidoRepository pedidoRepository,
            DetallePedidoRepository detallePedidoRepository,
            EnvioRepository envioRepository,
            FacturaRepository facturaRepository
    ){

        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.envioRepository = envioRepository;
        this.facturaRepository = facturaRepository;

    }



    public byte[] generarReportePDF(){


        ByteArrayOutputStream salida =
                new ByteArrayOutputStream();


        Document documento =
                new Document();


        try {


            PdfWriter.getInstance(
                    documento,
                    salida
            );


            documento.open();



            Font titulo =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            18
                    );


            documento.add(
                    new Paragraph(
                            "REPORTE GENERAL TIENDA ONLINE",
                            titulo
                    )
            );


            documento.add(
                    new Paragraph(
                            " "
                    )
            );



            agregarUsuarios(documento);

            agregarProductos(documento);

            agregarCategorias(documento);

            agregarPedidos(documento);

            agregarDetalles(documento);

            agregarEnvios(documento);

            agregarFacturas(documento);

            agregarEstados(documento);

            agregarRoles(documento);



            documento.close();


        }catch(Exception e){

            e.printStackTrace();

        }


        return salida.toByteArray();

    }



private void agregarUsuarios(Document doc)
throws Exception{


    doc.add(new Paragraph("1. USUARIOS"));


    PdfPTable tabla =
            new PdfPTable(4);


    tabla.addCell("ID");
    tabla.addCell("Nombre");
    tabla.addCell("Email");
    tabla.addCell("Rol");



    for(Usuario u:
        usuarioRepository.findAll()){


        tabla.addCell(
                String.valueOf(
                        u.getIdUsuario()
                )
        );

        tabla.addCell(
                u.getNombre()
        );

        tabla.addCell(
                u.getEmail()
        );

        tabla.addCell(
                u.getRol().toString()
        );


    }


    doc.add(tabla);

    doc.add(new Paragraph(" "));


}



private void agregarProductos(Document doc)
throws Exception{


    doc.add(new Paragraph("2. PRODUCTOS"));


    PdfPTable tabla =
            new PdfPTable(5);


    tabla.addCell("ID");
    tabla.addCell("Nombre");
    tabla.addCell("Precio");
    tabla.addCell("Stock");
    tabla.addCell("Categoria");



    for(Producto p:
            productoRepository.findAll()){


        tabla.addCell(
                p.getIdProducto().toString()
        );


        tabla.addCell(
                p.getNombre()
        );


        tabla.addCell(
                p.getPrecio().toString()
        );


        tabla.addCell(
                p.getStock().toString()
        );


        tabla.addCell(
                p.getCategoria()
                .getNombre()
        );


    }


    doc.add(tabla);

}



private void agregarCategorias(Document doc)
throws Exception{


    doc.add(
            new Paragraph(
                    "3. CATEGORIAS"
            )
    );


    PdfPTable tabla =
            new PdfPTable(3);


    tabla.addCell("ID");
    tabla.addCell("Nombre");
    tabla.addCell("Descripcion");


    for(Categoria c:
            categoriaRepository.findAll()){


        tabla.addCell(
                c.getIdCategoria()
                .toString()
        );


        tabla.addCell(
                c.getNombre()
        );


        tabla.addCell(
                c.getDescripcion()
        );


    }


    doc.add(tabla);

}



private void agregarPedidos(Document doc)
throws Exception{


    doc.add(
            new Paragraph(
                    "4. PEDIDOS"
            )
    );


    PdfPTable tabla =
            new PdfPTable(5);


    tabla.addCell("ID");
    tabla.addCell("Cliente");
    tabla.addCell("Fecha");
    tabla.addCell("Estado");
    tabla.addCell("Total");



    DateTimeFormatter formato =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy"
            );


    for(Pedido p:
            pedidoRepository.findAll()){


        tabla.addCell(
                p.getIdPedido()
                .toString()
        );


        tabla.addCell(
                p.getUsuario()
                .getNombre()
        );


        tabla.addCell(
        p.getFecha() != null
        ? p.getFecha().format(formato)
        : "Sin fecha"
);


        tabla.addCell(
                p.getEstado()
                .toString()
        );


        tabla.addCell(
                p.getTotal()
                .toString()
        );


    }


    doc.add(tabla);

}



private void agregarDetalles(Document doc)
throws Exception{


    doc.add(
            new Paragraph(
                    "5. DETALLE PEDIDO"
            )
    );


    PdfPTable tabla =
            new PdfPTable(4);


    tabla.addCell("Pedido");
    tabla.addCell("Producto");
    tabla.addCell("Cantidad");
    tabla.addCell("Precio");



    for(DetallePedido d:
        detallePedidoRepository.findAll()){


        tabla.addCell(
                d.getPedido()
                .getIdPedido()
                .toString()
        );


        tabla.addCell(
                d.getProducto()
                .getNombre()
        );


        tabla.addCell(
                d.getCantidad()
                .toString()
        );


        tabla.addCell(
                d.getPrecioUnitario()
                .toString()
        );

    }


    doc.add(tabla);

}



private void agregarEnvios(Document doc)
throws Exception{


    doc.add(new Paragraph("6. ENVIOS"));


    PdfPTable tabla =
            new PdfPTable(4);


    tabla.addCell("Nombre");
    tabla.addCell("Telefono");
    tabla.addCell("Distrito");
    tabla.addCell("Direccion");


    for(Envio e:
        envioRepository.findAll()){


        tabla.addCell(
                e.getNombres()
        );


        tabla.addCell(
                e.getTelefono()
        );


        tabla.addCell(
                e.getDistrito()
        );


        tabla.addCell(
                e.getDireccion()
        );


    }


    doc.add(tabla);

}



private void agregarFacturas(Document doc)
throws Exception{


    doc.add(
            new Paragraph(
                    "7. FACTURAS"
            )
    );


    PdfPTable tabla =
            new PdfPTable(4);


    tabla.addCell("Numero");
    tabla.addCell("Pago");
    tabla.addCell("Total");
    tabla.addCell("Fecha");


    for(Factura f:
        facturaRepository.findAll()){


        tabla.addCell(
                f.getNumeroFactura()
        );


        tabla.addCell(
                f.getTipoPago()
        );


        tabla.addCell(
                f.getTotal()
                .toString()
        );


        tabla.addCell(
                f.getFechaEmision()
                .toString()
        );

    }


    doc.add(tabla);

}



private void agregarEstados(Document doc)
throws Exception{


    doc.add(
            new Paragraph(
                    "8. ESTADOS"
            )
    );


    for(EstadoPedido e:
            EstadoPedido.values()){


        doc.add(
                new Paragraph(
                        e.toString()
                )
        );

    }

}



private void agregarRoles(Document doc)
throws Exception{


    doc.add(
            new Paragraph(
                    "9. ROLES"
            )
    );


    for(Rol r:
            Rol.values()){


        doc.add(
                new Paragraph(
                        r.toString()
                )
        );

    }

}



}