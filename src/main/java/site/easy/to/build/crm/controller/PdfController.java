package site.easy.to.build.crm.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.easy.to.build.crm.service.PdfService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/export/pdf")
public class PdfController {

    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/invoice")
    public void downloadPdf(HttpServletResponse response) {
        try {
            // Préparer les données pour le modèle
            Map<String, Object> data = new HashMap<>();
            data.put("date", "27/03/2025");
            data.put("items", List.of(
                    Map.of("name", "Produit A", "quantity", 2, "price", 50),
                    Map.of("name", "Produit B", "quantity", 1, "price", 30)
            ));

            // Générer le PDF
            byte[] pdfContent = pdfService.generatePdf("pdf/invoice", data);

            // Configurer la réponse HTTP
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"invoice.pdf\"");
            response.getOutputStream().write(pdfContent);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du téléchargement du PDF", e);
        }
    }
}

