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

    public void analyzeAllDocument() throws IOException {
        int coverPage = getCoverPage();
        int generalIndexPage = getGeneralIndex();
        for (int page = 1; page <= pdfdocument.getNumberOfPages(); page++) {
            if (page==coverPage){
                analyzeCoverPage(page);
                continue;
            }
            if (page==generalIndexPage){
                analyzeFooterNumeration(page);
                continue;
            }
            analyzeFooterNumeration(page);
        }

    }

    public int getCoverPage() throws IOException {
        int resp = 0;
        boolean coverPage;
        for (int page = 1; page <= pdfdocument.getNumberOfPages(); page++) {
            coverPage = searcher.isTheCoverInThisPage(page);
            if ( coverPage ){
                resp = page;
                break;
            }
        }
        return resp;
    }

    public int getGeneralIndex() throws IOException {
        int resp = 0;
        boolean generalIndexPage;
        for (int page = 1; page <= pdfdocument.getNumberOfPages(); page++) {
            generalIndexPage = searcher.isTheGeneralIndexInThisPage(page);
            if ( generalIndexPage ){
                resp = page;
                break;
            }
        }
        return resp;
    }


    // Funciones para la Cartula
    public void analyzeCoverPage(int page) throws IOException {

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

        List<WordPositionSequence> wordDepartamento = searcher.findWordsFromAPage(page, "Departamento");
        List<String> departamentoWords = searcher.getWordsOfARow(page,wordDepartamento.get(0).getY());
        List<WordPositionSequence> lastWordOnDepartamentoRow = searcher.findWordsFromAPage(page, departamentoWords.get(departamentoWords.size()-1));
        lastWordOnDepartamentoRow = searcher.filterByY(lastWordOnDepartamentoRow,wordDepartamento.get(0).getY());
        boolean b3 = coverMisstakes.isThereAnyMisstakeOnLineTreeOrFour(wordDepartamento,lastWordOnDepartamentoRow,pageWidth);
        if (b3){
            StringBuilder contentB = new StringBuilder();
            for (String word: departamentoWords){
                contentB.append(word).append(" ");
            }
            String content = contentB.toString();
            String comment = "Por favor verifique: (Tamaño de la letra: 14 puntos - Fuente: Times New Roman, en minúscula, negrilla y centrado).";
            formatMistakes.add(hightlight(wordDepartamento.get(0), lastWordOnDepartamentoRow.get(lastWordOnDepartamentoRow.size()-1), content, comment,pageWidth, pageHeigh,page,String.valueOf(counter.incrementAndGet())));
        }

        List<WordPositionSequence> wordCarrera = searcher.findWordsFromAPage(page, "Carrera");
        List<String> carreraWords = searcher.getWordsOfARow(page,wordCarrera.get(0).getY());
        List<WordPositionSequence> lastWordOnCarreraRow = searcher.findWordsFromAPage(page, carreraWords.get(carreraWords.size()-1));
        lastWordOnCarreraRow = searcher.filterByY(lastWordOnCarreraRow,wordCarrera.get(0).getY());
        boolean b4 = coverMisstakes.isThereAnyMisstakeOnLineTreeOrFour(wordCarrera,lastWordOnCarreraRow,pageWidth);
        if (b4){
            StringBuilder contentB = new StringBuilder();
            for (String word: carreraWords){
                contentB.append(word).append(" ");
            }
            String content = contentB.toString();
            String comment = "Por favor verifique: (Tamaño de la letra: 14 puntos - Fuente: Times New Roman, en minúscula, negrilla y centrado).";
            formatMistakes.add(hightlight(wordCarrera.get(0), lastWordOnCarreraRow.get(lastWordOnCarreraRow.size()-1), content,comment,pageWidth, pageHeigh,page,String.valueOf(counter.incrementAndGet())));
        }

        List<String> words = searcher.getFirstWordsOfTittleTypeAuthorDate(page);
        if (words.size() >= 4){
            List<WordPositionSequence> wordCochabamba = searcher.findWordsFromAPage(page, "Cochabamba");

            List<WordPositionSequence> firstWordTypeDocument = searcher.findWordsFromAPage(page, words.get(words.size() - 3));
            firstWordTypeDocument = searcher.filterByLessY(firstWordTypeDocument,wordCochabamba.get(wordCochabamba.size()-1).getY()); // Last one

            List<WordPositionSequence> firstWordTittle = searcher.findWordsFromAPage(page, words.get(0));
            firstWordTittle = searcher.filterByGreaterY(firstWordTittle,wordCarrera.get(0).getY());

            List<String> firstRowTittleWords = searcher.getWordsOfARow(page,firstWordTittle.get(0).getY());
            List<WordPositionSequence> lastWordFirstRowTittle = searcher.findWordsFromAPage(page,firstRowTittleWords.get(firstRowTittleWords.size()-1));
            lastWordFirstRowTittle = searcher.filterByY(lastWordFirstRowTittle,firstWordTittle.get(0).getY());

            List<WordPositionSequence> firstWordLastRowTittle = searcher.findWordsFromAPage(page, words.get(words.size() - 4));
            firstWordLastRowTittle = searcher.filterByLessY(firstWordLastRowTittle,firstWordTypeDocument.get(firstWordTypeDocument.size()-1).getY());
            boolean b5 = coverMisstakes.isThereAnyMisstakeOnTheTittle(firstWordTittle,lastWordFirstRowTittle,pageWidth);
            if (b5){
                StringBuilder contentB = new StringBuilder();
                for (String word: firstRowTittleWords){
                    contentB.append(word).append(" ");
                }
                String contentStr = contentB.toString();
                String commentStr = "Por favor verifique: (Tamaño de la letra: 16 puntos - Fuente: Times New Roman, en minúscula, negrilla y centrado).";
                Content content = new Content(contentStr);
                BoundingRect boundingRect = new BoundingRect(firstWordTittle.get(0).getX(), firstWordTittle.get(0).getYUpper(), lastWordFirstRowTittle.get(lastWordFirstRowTittle.size()-1).getEndX(),firstWordLastRowTittle.get(firstWordLastRowTittle.size()-1).getY(),pageWidth,pageHeigh);
                List<BoundingRect> boundingRects = new ArrayList<>();
                boundingRects.add(boundingRect);
                Position position = new Position(boundingRect,boundingRects,page);
                Comment comment = new Comment(commentStr,"");
                formatMistakes.add(new FormatMistake(content,position,comment,String.valueOf(counter.incrementAndGet())));
            }

            firstWordTypeDocument = searcher.filterByGreaterY(firstWordTypeDocument,firstWordLastRowTittle.get(firstWordLastRowTittle.size()-1).getY());
            List<String> typeDocumentWords = searcher.getWordsOfARow(page,firstWordTypeDocument.get(0).getY());
            List<WordPositionSequence> lastWordOnTypeDocument = searcher.findWordsFromAPage(page, typeDocumentWords.get(typeDocumentWords.size()-1));
            lastWordOnTypeDocument = searcher.filterByY(lastWordOnTypeDocument,firstWordTypeDocument.get(0).getY());
            boolean b6 = coverMisstakes.isThereAnyMisstakeOnTheTypeDocument(firstWordTypeDocument,lastWordOnTypeDocument,pageWidth);
            if (b6){
                StringBuilder contentB = new StringBuilder();
                for (String word: typeDocumentWords){
                    contentB.append(word).append(" ");
                }
                String content = contentB.toString();
                String comment = "Por favor verifique: (Tamaño de la letra: 12 puntos - Fuente: Times New Roman, en minúscula, cursiva y alineado al margen derecho).";
                formatMistakes.add(hightlight(firstWordTypeDocument.get(0), lastWordOnTypeDocument.get(lastWordOnTypeDocument.size()-1), content,comment,pageWidth, pageHeigh,page,String.valueOf(counter.incrementAndGet())));
            }

            List<WordPositionSequence> firstAuthorWord = searcher.findWordsFromAPage(page, words.get(words.size() - 2));
            firstAuthorWord = searcher.filterByGreaterY(firstAuthorWord,firstWordLastRowTittle.get(firstWordLastRowTittle.size()-1).getY());
            List<String> authorWords = searcher.getWordsOfARow(page,firstAuthorWord.get(0).getY());
            List<WordPositionSequence> lastAuthorWord = searcher.findWordsFromAPage(page, authorWords.get(authorWords.size()-1));
            lastAuthorWord = searcher.filterByY(lastAuthorWord,firstAuthorWord.get(0).getY());
            boolean b7 = coverMisstakes.isThereAnyMisstakeOnTheAuthor(firstAuthorWord,lastAuthorWord,pageWidth);
            if (b7){
                StringBuilder contentB = new StringBuilder();
                for (String word: authorWords){
                    contentB.append(word).append(" ");
                }
                String content = contentB.toString();
                String comment = "Por favor verifique: (Tamaño de la letra: 14 puntos - Fuente: Times New Roman, en minúscula, negrilla y centrado).";
                formatMistakes.add(hightlight(firstAuthorWord.get(0), lastAuthorWord.get(lastAuthorWord.size()-1), content,comment,pageWidth, pageHeigh,page,String.valueOf(counter.incrementAndGet())));
            }

        }

        List<WordPositionSequence> wordCochabamba = searcher.findWordsFromAPage(page, "Cochabamba");
        List<WordPositionSequence> wordBolivia = searcher.findWordsFromAPage(page, "Bolivia");
        boolean b8 = coverMisstakes.isThereAnyMisstakeOnTheCityCountryOrYear(wordCochabamba,wordBolivia,pageWidth);
        if (b8){
            String content = "Cochabamba - Bolivia";
            String comment = "Por favor verifique: (Tamaño de la letra: 12 puntos - Fuente: Times New Roman, en minúscula, centrado)." ;
            formatMistakes.add(hightlight(wordCochabamba.get(wordCochabamba.size()-1), wordBolivia.get(wordBolivia.size()-1), content,comment,pageWidth, pageHeigh,page,String.valueOf(counter.incrementAndGet())));
        }

        if (words.size() >= 4){
            List<WordPositionSequence> monthWord = searcher.findWordsFromAPage(page, words.get(words.size() - 1));
            List<String> monthAndYearWords = searcher.getWordsOfARow(page,monthWord.get(monthWord.size()-1).getY());
            List<WordPositionSequence> yearWord = searcher.findWordsFromAPage(page,monthAndYearWords.get(monthAndYearWords.size()-1));
            boolean b9 = coverMisstakes.isThereAnyMisstakeOnTheCityCountryOrYear(monthWord,yearWord,pageWidth);
            if (b9){
                StringBuilder contentB = new StringBuilder();
                for (String word: monthAndYearWords){
                    contentB.append(word).append(" ");
                }
                String content = contentB.toString();
                String comment = "Por favor verifique: (Tamaño de la letra: 12 puntos - Fuente: Times New Roman, en minúscula, centrado)." ;
                formatMistakes.add(hightlight(monthWord.get(monthWord.size()-1), yearWord.get(yearWord.size()-1), content,comment,pageWidth, pageHeigh,page,String.valueOf(counter.incrementAndGet())));
            }
        }
    }

    public void analyzeFooterNumeration(int page) throws IOException {
        List<String> words = searcher.getFirstWordsOfTittleTypeAuthorDate(page);
        List<WordPositionSequence> numberPage = searcher.findWordsFromAPage(page, words.get(words.size()-1));
        if (numberPage.get(numberPage.size()-1).getY() >720){
            List<String> comments = new ArrayList<>();
            if (numberPage.get(numberPage.size()-1).getX() < 500) {
                comments.add("alineado al margen derecho");
            }
            if (!words.get(words.size() - 1).equals(Integer.toString(page))){
                comments.add("número de página debería ser "+page);
            }
            if (comments.size()!=0){
                float pageWidth = pdfdocument.getPage(page-1).getMediaBox().getWidth();
                float pageHeigh = pdfdocument.getPage(page-1).getMediaBox().getHeight();
                StringBuilder commentStr = new StringBuilder("Por favor verifique: ");
                for (int i=0;i<comments.size();i++){
                    if (i!=0){
                        commentStr.append(" - ").append(comments.get(i));
                    }else{
                        commentStr.append(comments.get(i));
                    }
                }
                commentStr.append(".");
                String comment = commentStr.toString();
                String content = words.get(words.size()-1);
                formatMistakes.add(hightlight(numberPage.get(numberPage.size()-1), numberPage.get(numberPage.size()-1), content,comment,pageWidth, pageHeigh,page,String.valueOf(counter.incrementAndGet())));
            }
        }


    }

}
