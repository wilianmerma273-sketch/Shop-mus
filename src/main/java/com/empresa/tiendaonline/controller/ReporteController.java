package com.empresa.tiendaonline.controller;


import com.empresa.tiendaonline.service.ReportePdfService;
import com.empresa.tiendaonline.service.ReporteExcelService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ReporteController {


    private final ReportePdfService reportePdfService;
    private final ReporteExcelService reporteExcelService;



    public ReporteController(
            ReportePdfService reportePdfService,
            ReporteExcelService reporteExcelService
    ){

        this.reportePdfService = reportePdfService;
        this.reporteExcelService = reporteExcelService;

    }



    // Vista de reportes

    @GetMapping("/reportes")
    public String vistaReportes(){

        return "reportes";

    }



    // Descargar PDF

    @GetMapping("/reportes/pdf")
    public ResponseEntity<byte[]> descargarPDF(){


        byte[] archivo =
                reportePdfService
                .generarReportePDF();



        return ResponseEntity.ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=reporte_tienda_online.pdf"
                )

                .contentType(
                        MediaType.APPLICATION_PDF
                )

                .body(archivo);

    }




    // Descargar Excel

    @GetMapping("/reportes/excel")
    public ResponseEntity<byte[]> descargarExcel(){


        byte[] archivo =
                reporteExcelService
                .generarExcel();



        return ResponseEntity.ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=reporte_tienda_online.xlsx"
                )

                .contentType(
                        MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )

                .body(archivo);

    }



}