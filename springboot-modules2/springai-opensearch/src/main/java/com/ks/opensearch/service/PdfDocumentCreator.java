package com.ks.opensearch.service;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;

public class PdfDocumentCreator {

    public static List<Document>fetchDocumentChunksFromPdf(String pdfFilePath) {
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(pdfFilePath);
        TokenTextSplitter splitter = new TokenTextSplitter();
        return splitter.transform(pdfReader.get());
    }
}
