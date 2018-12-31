package com.ucbcba.seminario.joel.erroresformatodocumentosacademicos.entities;

import com.ucbcba.seminario.joel.erroresformatodocumentosacademicos.entitiesHighlight.*;
import org.apache.pdfbox.pdmodel.PDDocument;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


public class MisstakesDetector {
    private PDDocument pdfdocument;
    private SearchWords searcher;
    private CoverMisstakes coverMisstakes;
    private final AtomicLong counter = new AtomicLong();
    private List<FormatMistake> formatMistakes = new ArrayList<>();

    public MisstakesDetector(PDDocument pdfdocument){
        this.pdfdocument = pdfdocument;
        coverMisstakes = new CoverMisstakes();
        searcher = new SearchWords(pdfdocument);
    }

    public PDDocument getPdfdocument() {
        return pdfdocument;
    }

    public void setPdfdocument(PDDocument pdfdocument) {
        this.pdfdocument = pdfdocument;
    }

    public SearchWords getSearcher() {
        return searcher;
    }

    public void setSearcher(SearchWords searcher) {
        this.searcher = searcher;
    }

    public CoverMisstakes getCoverMisstakes() {
        return coverMisstakes;
    }

    public void setCoverMisstakes(CoverMisstakes coverMisstakes) {
        this.coverMisstakes = coverMisstakes;
    }

    public List<FormatMistake> getFormatMistakes() {
        return formatMistakes;
    }

    public void setFormatMistakes(List<FormatMistake> formatMistakes) {
        this.formatMistakes = formatMistakes;
    }

    public FormatMistake hightlight(WordPositionSequence fistWord, WordPositionSequence lastWord, String contentText, String commentText, float pageWidth, float pageHeigh, int page, String id  ){
        Content content = new Content(contentText);
        BoundingRect boundingRect = new BoundingRect(fistWord.getX(), fistWord.getYUpper(), lastWord.getEndX(),lastWord.getY(),pageWidth,pageHeigh);
        List<BoundingRect> boundingRects = new ArrayList<>();
        boundingRects.add(boundingRect);
        Position position = new Position(boundingRect,boundingRects,page);
        Comment comment = new Comment(commentText,"");
        return new FormatMistake(content,position,comment,id);
    }


    // Funciones para la Cartula
    public void analyzeCoverPage() throws IOException {
        for (int page = 1; page <= pdfdocument.getNumberOfPages(); page++){
            boolean coverPage = searcher.isTheCoverInThisPage(page);
            if ( coverPage ){
                float pageWidth = pdfdocument.getPage(page-1).getMediaBox().getWidth();
                float pageHeigh = pdfdocument.getPage(page-1).getMediaBox().getHeight();

                List<WordPositionSequence> wordUniversidad = searcher.findWordsFromAPage(page, "UNIVERSIDAD");
                List<WordPositionSequence> wordPablo = searcher.findWordsFromAPage(page, "PABLO”");
                boolean b1 = coverMisstakes.isThereAnyMisstakeOnLineOne(wordUniversidad,wordPablo);
                if (b1){
                    String content = "UNIVERSIDAD CATÓLICA BOLIVIANA “SAN PABLO”";
                    String comment = "Por favor verifique: (Tamaño de la letra: 18 puntos - Fuente: Times New Roman, en mayúscula, negrilla y centrado)." ;
                    formatMistakes.add(hightlight(wordUniversidad.get(0), wordPablo.get(0), content,comment,pageWidth, pageHeigh,page,String.valueOf(counter.incrementAndGet())));
                }

                List<WordPositionSequence> wordUnidad = searcher.findWordsFromAPage(page, "UNIDAD");
                List<WordPositionSequence> wordCbba = searcher.findWordsFromAPage(page, "COCHABAMBA");
                boolean b2 = coverMisstakes.isThereAnyMisstakeOnLineTwo(wordUnidad,wordCbba,pageWidth);
                if (b2){
                    String content = "UNIDAD ACADÉMICA REGIONAL COCHABAMBA";
                    String comment = "Por favor verifique: (Tamaño de la letra: 16 puntos - Fuente: Times New Roman, en mayúscula, negrilla y centrado).";
                    formatMistakes.add(hightlight(wordUnidad.get(0), wordCbba.get(0), content,comment,pageWidth, pageHeigh,page,String.valueOf(counter.incrementAndGet())));
                }
                break;
            }
        }

    }
}
