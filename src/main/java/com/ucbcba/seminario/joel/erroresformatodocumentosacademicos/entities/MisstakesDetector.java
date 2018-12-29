package com.ucbcba.seminario.joel.erroresformatodocumentosacademicos.entities;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MisstakesDetector {
    private PDDocument pdfdocument;

    public MisstakesDetector(PDDocument pdfdocument){
        this.pdfdocument = pdfdocument;
    }

    //Funciones basicas para encontrar Errores
    public List<WordPositionSequence> findWordsFromAPage(int page, String searchWord) throws IOException {
        final List<WordPositionSequence> listWordPositionSequences = new ArrayList<WordPositionSequence>();
        PDFTextStripper stripper = new PDFTextStripper() {
            @Override
            protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
                WordPositionSequence word = new WordPositionSequence(textPositions);
                String string = word.toString();
                int index = 0;
                int indexWordFound;
                while ((indexWordFound = string.indexOf(searchWord, index)) > -1) {
                    listWordPositionSequences.add(word.subSequence(indexWordFound, indexWordFound + searchWord.length()));
                    index = indexWordFound + 1;
                }
                super.writeString(text, textPositions);
            }
        };
        stripper.setSortByPosition(true);
        stripper.setStartPage(page);
        stripper.setEndPage(page);
        stripper.getText(pdfdocument);
        return listWordPositionSequences;
    }

    public boolean isTheWordInThePage(int page, String searchWord) throws IOException {
        final boolean[] resp = {false};
        PDFTextStripper stripper = new PDFTextStripper() {
            @Override
            protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
                WordPositionSequence word = new WordPositionSequence(textPositions);
                String string = word.toString();
                int index = 0;
                int indexWordFound;
                while (((indexWordFound = string.indexOf(searchWord, index)) > -1) && !resp[0]) {
                    resp[0] = true;
                    index = indexWordFound + 1;
                }
                super.writeString(text, textPositions);
            }
        };
        stripper.setSortByPosition(true);
        stripper.setStartPage(page);
        stripper.setEndPage(page);
        stripper.getText(pdfdocument);
        return resp[0];
    }


    public int getNumberOfTrues(boolean... vars) {
        int count = 0;
        for (boolean var : vars) {
            count += (var ? 1 : 0);
        }
        return count;
    }

    // Funciones para la Cartula
    public List<FormatMistake> getCoverPage() throws IOException {
        List<FormatMistake> formatMistakes = new ArrayList<>();
        boolean bool1,bool2, bool3, bool4, bool5;
        for (int page = 1; page <= pdfdocument.getNumberOfPages(); page++){
            bool1 = isTheWordInThePage(page,"“SAN PABLO”");
            bool2 = isTheWordInThePage(page,"REGIONAL");
            bool3 = isTheWordInThePage(page,"Departamento");
            bool4 = isTheWordInThePage(page,"Carrera");
            bool5 = isTheWordInThePage(page,"Bolivia");

            if ( getNumberOfTrues(bool1,bool2,bool3,bool4,bool5) >= 3 ){
                float pageWidth = pdfdocument.getPage(page).getMediaBox().getWidth();
                float pageHeigh = pdfdocument.getPage(page).getMediaBox().getHeight();
                List<WordPositionSequence> wordUniversidad = findWordsFromAPage(page, "UNIVERSIDAD");
                List<WordPositionSequence> wordPablo = findWordsFromAPage(page, "PABLO”");
                boolean b1 = isThereAnyMisstakeOnLine1Cover(wordUniversidad,wordPablo);
                if (b1){
                    Content content = new Content("UNIVERSIDAD CATÓLICA BOLIVIANA “SAN PABLO”");
                    BoundingRect boundingRect = new BoundingRect(wordUniversidad.get(0).getX(), wordUniversidad.get(0).getYUpper(), wordPablo.get(0).getEndX(),wordPablo.get(0).getY(),pageWidth,pageHeigh);
                    List<BoundingRect> boundingRects = new ArrayList<>();
                    boundingRects.add(boundingRect);
                    Position position = new Position(boundingRect,boundingRects,page);
                    Comment comment = new Comment("Por favor verifique: <br/>Tamaño de la letra: 18 puntos <br/>Fuente: Times New Roman, en mayúscula, negrilla y centrado.","");
                    formatMistakes.add(new FormatMistake(content,position,comment,1));
                }

                List<WordPositionSequence> wordUnidad = findWordsFromAPage(page, "UNIDAD");
                List<WordPositionSequence> wordCbba = findWordsFromAPage(page, "COCHABAMBA");
                boolean b2 = isThereAnyMisstakeOnLine2Cover(wordUnidad,wordCbba,pageWidth);
                if (b2){
                    Content content = new Content("UNIDAD ACADÉMICA REGIONAL COCHABAMBA");
                    BoundingRect boundingRect = new BoundingRect(wordUnidad.get(0).getX(), wordUnidad.get(0).getYUpper(), wordCbba.get(0).getEndX(),wordCbba.get(0).getY(),pageWidth,pageHeigh);
                    List<BoundingRect> boundingRects = new ArrayList<>();
                    boundingRects.add(boundingRect);
                    Position position = new Position(boundingRect,boundingRects,page);
                    Comment comment = new Comment("Por favor verifique: <br/>Tamaño de la letra: 16 puntos <br/>Fuente: Times New Roman, en mayúscula, negrilla y centrado.","");
                    formatMistakes.add(new FormatMistake(content,position,comment,2));
                }
                return formatMistakes;
            }
        }
        return formatMistakes;
    }

    public boolean isThereAnyMisstakeOnLine1Cover(List<WordPositionSequence> wordUniversidad, List<WordPositionSequence> wordPablo){
        if (wordUniversidad.size() >= 1 && wordPablo.size() >= 1 ){
            if (!wordUniversidad.get(0).getFont().contains("Times") || !wordUniversidad.get(0).getFont().contains("New") || !wordUniversidad.get(0).getFont().contains("Roman")){
                return true;
            }
            if (wordUniversidad.get(0).getFontSize() != 18){
                return true;
            }
            if (!wordUniversidad.get(0).getFont().contains("Bold") ){
                return true;
            }

            if (!wordUniversidad.get(0).getFont().contains("Times") || !wordUniversidad.get(0).getFont().contains("New") || !wordUniversidad.get(0).getFont().contains("Roman")){
                return true;
            }
            if (Math.round( wordUniversidad.get(0).getFontSize()) != 18){
                return true;
            }
            if (!wordUniversidad.get(0).getFont().contains("Bold") ){
                return true;
            }

        }
        return false;
    }

    public boolean isThereAnyMisstakeOnLine2Cover(List<WordPositionSequence> wordUnidad, List<WordPositionSequence> wordCbba, float pageWidth){
        if (wordUnidad.size() >= 1 && wordCbba.size() >= 1 ){
            if (!wordUnidad.get(0).getFont().contains("Times") || !wordUnidad.get(0).getFont().contains("New") || !wordUnidad.get(0).getFont().contains("Roman")){
                return true;
            }
            if (wordUnidad.get(0).getFontSize() != 15){
                return true;
            }
            if (!wordUnidad.get(0).getFont().contains("Bold") ){
                return true;
            }
            if (!wordCbba.get(0).getFont().contains("Times") || !wordCbba.get(0).getFont().contains("New") || !wordCbba.get(0).getFont().contains("Roman")){
                return true;
            }
            if (Math.round( wordCbba.get(0).getFontSize()) != 16){
                return true;
            }
            if (!wordCbba.get(0).getFont().contains("Bold") ){
                return true;
            }
            // Formula para ver si esta centrado
            if (Math.abs((pageWidth - wordCbba.get(0).getEndX()) - wordUnidad.get(0).getX()) >= 20){
                return true;
            }
        }
        return false;
    }
}
