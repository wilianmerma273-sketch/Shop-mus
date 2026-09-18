package com.empresa.tiendaonline.service;


import com.empresa.tiendaonline.entity.*;
import com.empresa.tiendaonline.repository.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;



@Service
public class ReporteExcelService {


    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final EnvioRepository envioRepository;
    private final FacturaRepository facturaRepository;


    public ReporteExcelService(
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



public byte[] generarExcel(){


    Workbook workbook =
            new XSSFWorkbook();



    crearUsuarios(workbook);

    crearProductos(workbook);

    crearCategorias(workbook);

    crearPedidos(workbook);

    crearDetalles(workbook);

    crearEnvios(workbook);

    crearFacturas(workbook);

    crearEstados(workbook);

    crearRoles(workbook);



    try{

        ByteArrayOutputStream salida =
                new ByteArrayOutputStream();


        workbook.write(salida);

        workbook.close();


        return salida.toByteArray();


    }catch(Exception e){

        throw new RuntimeException(e);

    }


}



private void encabezado(
        Sheet hoja,
        String... columnas){


    Row fila =
            hoja.createRow(0);


    for(int i=0;i<columnas.length;i++){

        fila.createCell(i)
            .setCellValue(
                    columnas[i]
            );

    }

}



private void crearUsuarios(
        Workbook wb){


    Sheet hoja =
            wb.createSheet("Usuarios");


    encabezado(
            hoja,
            "ID",
            "Nombre",
            "Email",
            "Rol"
    );


    int fila=1;


    for(Usuario u:
            usuarioRepository.findAll()){


        Row row =
                hoja.createRow(fila++);


        row.createCell(0)
                .setCellValue(
                        u.getIdUsuario()
                );


        row.createCell(1)
                .setCellValue(
                        u.getNombre()
                );


        row.createCell(2)
                .setCellValue(
                        u.getEmail()
                );


        row.createCell(3)
                .setCellValue(
                        u.getRol().toString()
                );


    }

}




private void crearProductos(
        Workbook wb){


    Sheet hoja =
            wb.createSheet("Productos");


    encabezado(
            hoja,
            "ID",
            "Nombre",
            "Precio",
            "Stock",
            "Categoria"
    );


    int fila=1;


    for(Producto p:
            productoRepository.findAll()){


        Row row =
                hoja.createRow(fila++);


        row.createCell(0)
                .setCellValue(
                        p.getIdProducto()
                );


        row.createCell(1)
                .setCellValue(
                        p.getNombre()
                );


        row.createCell(2)
                .setCellValue(
                        p.getPrecio()
                        .doubleValue()
                );


        row.createCell(3)
                .setCellValue(
                        p.getStock()
                );


        row.createCell(4)
                .setCellValue(
                        p.getCategoria()
                        .getNombre()
                );

    }

}





private void crearCategorias(
        Workbook wb){


    Sheet hoja =
            wb.createSheet("Categorias");


    encabezado(
            hoja,
            "ID",
            "Nombre",
            "Descripcion"
    );


    int fila=1;


    for(Categoria c:
            categoriaRepository.findAll()){


        Row row =
                hoja.createRow(fila++);


        row.createCell(0)
                .setCellValue(
                        c.getIdCategoria()
                );


        row.createCell(1)
                .setCellValue(
                        c.getNombre()
                );


        row.createCell(2)
                .setCellValue(
                        c.getDescripcion()
                );


    }

}





private void crearPedidos(
        Workbook wb){


    Sheet hoja =
            wb.createSheet("Pedidos");


    encabezado(
            hoja,
            "ID",
            "Cliente",
            "Estado",
            "Total"
    );


    int fila=1;


    for(Pedido p:
            pedidoRepository.findAll()){


        Row row =
                hoja.createRow(fila++);


        row.createCell(0)
                .setCellValue(
                        p.getIdPedido()
                );


        row.createCell(1)
                .setCellValue(
                        p.getUsuario()
                        .getNombre()
                );


        row.createCell(2)
                .setCellValue(
                        p.getEstado()
                        .toString()
                );


        row.createCell(3)
                .setCellValue(
                        p.getTotal()
                        .doubleValue()
                );

    }

}





private void crearDetalles(
        Workbook wb){


    Sheet hoja =
            wb.createSheet("DetallePedido");


    encabezado(
            hoja,
            "Pedido",
            "Producto",
            "Cantidad",
            "Precio"
    );


    int fila=1;


    for(DetallePedido d:
            detallePedidoRepository.findAll()){


        Row row =
                hoja.createRow(fila++);


        row.createCell(0)
                .setCellValue(
                        d.getPedido()
                        .getIdPedido()
                );


        row.createCell(1)
                .setCellValue(
                        d.getProducto()
                        .getNombre()
                );


        row.createCell(2)
                .setCellValue(
                        d.getCantidad()
                );


        row.createCell(3)
                .setCellValue(
                        d.getPrecioUnitario()
                        .doubleValue()
                );

    }

}





private void crearEnvios(
        Workbook wb){


    Sheet hoja =
            wb.createSheet("Envios");


    encabezado(
            hoja,
            "Nombre",
            "Telefono",
            "Distrito",
            "Direccion"
    );


    int fila=1;


    for(Envio e:
            envioRepository.findAll()){


        Row row =
                hoja.createRow(fila++);


        row.createCell(0)
                .setCellValue(
                        e.getNombres()
                );


        row.createCell(1)
                .setCellValue(
                        e.getTelefono()
                );


        row.createCell(2)
                .setCellValue(
                        e.getDistrito()
                );


        row.createCell(3)
                .setCellValue(
                        e.getDireccion()
                );


    }

}




private void crearFacturas(
        Workbook wb){


    Sheet hoja =
            wb.createSheet("Facturas");


    encabezado(
            hoja,
            "Numero",
            "Pago",
            "Total"
    );


    int fila=1;


    for(Factura f:
            facturaRepository.findAll()){


        Row row =
                hoja.createRow(fila++);


        row.createCell(0)
                .setCellValue(
                        f.getNumeroFactura()
                );


        row.createCell(1)
                .setCellValue(
                        f.getTipoPago()
                );


        row.createCell(2)
                .setCellValue(
                        f.getTotal()
                        .doubleValue()
                );

    }

}




private void crearEstados(
        Workbook wb){


    Sheet hoja =
            wb.createSheet("Estados");


    encabezado(
            hoja,
            "Estado"
    );


    int fila=1;


    for(EstadoPedido e:
            EstadoPedido.values()){


        hoja.createRow(fila++)
                .createCell(0)
                .setCellValue(
                        e.toString()
                );

    }

}




private void crearRoles(
        Workbook wb){


    Sheet hoja =
            wb.createSheet("Roles");


    encabezado(
            hoja,
            "Rol"
    );


    int fila=1;


    for(Rol r:
            Rol.values()){


        hoja.createRow(fila++)
                .createCell(0)
                .setCellValue(
                        r.toString()
                );

    }

}



}