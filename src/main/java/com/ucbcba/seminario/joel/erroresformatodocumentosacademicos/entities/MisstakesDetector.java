package com.ucbcba.seminario.joel.erroresformatodocumentosacademicos.entities;

import com.ucbcba.seminario.joel.erroresformatodocumentosacademicos.entitiesHighlight.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


public class MisstakesDetector {
    private PDDocument pdfdocument;
    private SearchWords searcher;
    private CoverMisstakes coverMisstakes;
    private final AtomicLong counter = new AtomicLong();
    private List<FormatMistake> formatMistakes = new ArrayList<>();
    private int imageNumber;
    private int tableNumber;


    public MisstakesDetector(PDDocument pdfdocument){
        this.pdfdocument = pdfdocument;
        coverMisstakes = new CoverMisstakes();
        searcher = new SearchWords(pdfdocument);
        imageNumber = 1;
        tableNumber = 1;
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
        int annexedPage=1;
        for (int page = 1; page <= pdfdocument.getNumberOfPages(); page++) {
            if (page==coverPage){
                analyzeCoverPage(page);
                continue;
            }
            if (page==generalIndexPage){
                analyzeGeneralIndexPage(page);
                continue;
            }
            boolean isIndexPage = searcher.isTheWordInThePage(page,".................");
            if (isIndexPage){
                continue;
            }

            List<String> ImagesOrTablesTittles = new ArrayList<String>();
            ImagesOrTablesTittles = searcher.getTittlesImagesOrTables(page);
            if (ImagesOrTablesTittles.size() % 2 == 0) {
                int index = 0;
                while (index < ImagesOrTablesTittles.size()) {
                    List<WordPositionSequence> ImageOrTable = searcher.findWordsFromAPage(page, ImagesOrTablesTittles.get(index));
                    List<WordPositionSequence> Tittle = searcher.findWordsFromAPage(page, ImagesOrTablesTittles.get(index + 1));
                    analyzeTableImageTittle(ImageOrTable, ImagesOrTablesTittles.get(index), page);
                    analyzeTableImage(Tittle, ImagesOrTablesTittles.get(index + 1), page);
                    index = index + 2;
                }
            }
            List<String> ImagesOrTablesSources = new ArrayList<String>();
            ImagesOrTablesSources = searcher.getSourcecsImagesOrTables(page);
            int index = 0;
            while (index < ImagesOrTablesSources.size()) {
                List<WordPositionSequence> Source = searcher.findWordsFromAPage(page, ImagesOrTablesSources.get(index));
                analyzeTableImageSource(Source, ImagesOrTablesSources.get(index), page);
                index = index + 1;
            }

            boolean isAnnexedPage1 = searcher.isTheWordInThePage(page,"Anexo");
            boolean isAnnexedPage2 = searcher.isTheWordInThePage(page,"ANEXO");
            if (isAnnexedPage1 || isAnnexedPage2){
                analyzeFooterNumeration(annexedPage);
                annexedPage++;
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
        List<String> comments1 = coverMisstakes.isThereAnyMisstakeOnLineOne(wordUniversidad,wordPablo);
        if (comments1.size() != 0){
            StringBuilder commentStr = new StringBuilder("Por favor verifique: ");
            for (int i = 0; i < comments1.size(); i++) {
                if (i != 0) {
                    commentStr.append(" - ").append(comments1.get(i));
                } else {
                    commentStr.append(comments1.get(i));
                }
            }
            commentStr.append(".");
            String comment = commentStr.toString();
            String content = "UNIVERSIDAD CATÓLICA BOLIVIANA “SAN PABLO”";
            formatMistakes.add(hightlight(wordUniversidad.get(0), wordPablo.get(0), content,comment,pageWidth, pageHeigh,page,String.valueOf(counter.incrementAndGet())));
        }

        List<WordPositionSequence> wordUnidad = searcher.findWordsFromAPage(page, "UNIDAD");
        List<WordPositionSequence> wordCbba = searcher.findWordsFromAPage(page, "COCHABAMBA");
        List<String> comments2 = coverMisstakes.isThereAnyMisstakeOnLineTwo(wordUnidad,wordCbba,pageWidth);
        if (comments2.size() != 0){
            StringBuilder commentStr = new StringBuilder("Por favor verifique: ");
            for (int i = 0; i < comments2.size(); i++) {
                if (i != 0) {
                    commentStr.append(" - ").append(comments2.get(i));
                } else {
                    commentStr.append(comments2.get(i));
                }
            }
            commentStr.append(".");
            String comment = commentStr.toString();
            String content = "UNIDAD ACADÉMICA REGIONAL COCHABAMBA";
            formatMistakes.add(hightlight(wordUnidad.get(0), wordCbba.get(0), content,comment,pageWidth, pageHeigh,page,String.valueOf(counter.incrementAndGet())));
        }

        List<WordPositionSequence> wordDepartamento = searcher.findWordsFromAPage(page, "Departamento");
        List<String> departamentoWords = searcher.getWordsOfARow(page,wordDepartamento.get(0).getY());
        List<WordPositionSequence> lastWordOnDepartamentoRow = searcher.findWordsFromAPage(page, departamentoWords.get(departamentoWords.size()-1));
        lastWordOnDepartamentoRow = searcher.filterByY(lastWordOnDepartamentoRow,wordDepartamento.get(0).getY());
        List<String> comments3 = coverMisstakes.isThereAnyMisstakeOnLineTreeOrFour(wordDepartamento,lastWordOnDepartamentoRow,pageWidth);
        if (comments3.size() != 0){
            StringBuilder contentB = new StringBuilder();
            for (String word: departamentoWords){
                contentB.append(word).append(" ");
            }
            StringBuilder commentStr = new StringBuilder("Por favor verifique: ");
            for (int i = 0; i < comments3.size(); i++) {
                if (i != 0) {
                    commentStr.append(" - ").append(comments3.get(i));
                } else {
                    commentStr.append(comments3.get(i));
                }
            }
            commentStr.append(".");
            String comment = commentStr.toString();
            String content = contentB.toString();
            formatMistakes.add(hightlight(wordDepartamento.get(0), lastWordOnDepartamentoRow.get(lastWordOnDepartamentoRow.size()-1), content, comment,pageWidth, pageHeigh,page,String.valueOf(counter.incrementAndGet())));
        }

        List<WordPositionSequence> wordCarrera = searcher.findWordsFromAPage(page, "Carrera");
        List<String> carreraWords = searcher.getWordsOfARow(page,wordCarrera.get(0).getY());
        List<WordPositionSequence> lastWordOnCarreraRow = searcher.findWordsFromAPage(page, carreraWords.get(carreraWords.size()-1));
        lastWordOnCarreraRow = searcher.filterByY(lastWordOnCarreraRow,wordCarrera.get(0).getY());
        List<String> comments4 = coverMisstakes.isThereAnyMisstakeOnLineTreeOrFour(wordCarrera,lastWordOnCarreraRow,pageWidth);
        if (comments4.size() != 0){
            StringBuilder contentB = new StringBuilder();
            for (String word: carreraWords){
                contentB.append(word).append(" ");
            }
            String content = contentB.toString();
            StringBuilder commentStr = new StringBuilder("Por favor verifique: ");
            for (int i = 0; i < comments4.size(); i++) {
                if (i != 0) {
                    commentStr.append(" - ").append(comments4.get(i));
                } else {
                    commentStr.append(comments4.get(i));
                }
            }
            commentStr.append(".");
            String comment = commentStr.toString();
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
            List<String> comments5 = coverMisstakes.isThereAnyMisstakeOnTheTittle(firstWordTittle,lastWordFirstRowTittle,pageWidth);
            if (comments5.size() != 0){
                StringBuilder contentB = new StringBuilder();
                for (String word: firstRowTittleWords){
                    contentB.append(word).append(" ");
                }
                String contentStr = contentB.toString();
                StringBuilder commentStr = new StringBuilder("Por favor verifique: ");
                for (int i = 0; i < comments5.size(); i++) {
                    if (i != 0) {
                        commentStr.append(" - ").append(comments5.get(i));
                    } else {
                        commentStr.append(comments5.get(i));
                    }
                }
                commentStr.append(".");
                String commentString = commentStr.toString();
                Content content = new Content(contentStr);
                BoundingRect boundingRect = new BoundingRect(firstWordTittle.get(0).getX(), firstWordTittle.get(0).getYUpper(), lastWordFirstRowTittle.get(lastWordFirstRowTittle.size()-1).getEndX(),firstWordLastRowTittle.get(firstWordLastRowTittle.size()-1).getY(),pageWidth,pageHeigh);
                List<BoundingRect> boundingRects = new ArrayList<>();
                boundingRects.add(boundingRect);
                Position position = new Position(boundingRect,boundingRects,page);
                Comment comment = new Comment(commentString,"");
                formatMistakes.add(new FormatMistake(content,position,comment,String.valueOf(counter.incrementAndGet())));
            }

            firstWordTypeDocument = searcher.filterByGreaterY(firstWordTypeDocument,firstWordLastRowTittle.get(firstWordLastRowTittle.size()-1).getY());
            List<String> typeDocumentWords = searcher.getWordsOfARow(page,firstWordTypeDocument.get(0).getY());
            List<WordPositionSequence> lastWordOnTypeDocument = searcher.findWordsFromAPage(page, typeDocumentWords.get(typeDocumentWords.size()-1));
            lastWordOnTypeDocument = searcher.filterByY(lastWordOnTypeDocument,firstWordTypeDocument.get(0).getY());
            List<String> comments6 = coverMisstakes.isThereAnyMisstakeOnTheTypeDocument(firstWordTypeDocument,lastWordOnTypeDocument,pageWidth);
            if (comments6.size() != 0){
                StringBuilder contentB = new StringBuilder();
                for (String word: typeDocumentWords){
                    contentB.append(word).append(" ");
                }
                String content = contentB.toString();
                StringBuilder commentStr = new StringBuilder("Por favor verifique: ");
                for (int i = 0; i < comments6.size(); i++) {
                    if (i != 0) {
                        commentStr.append(" - ").append(comments6.get(i));
                    } else {
                        commentStr.append(comments6.get(i));
                    }
                }
                commentStr.append(".");
                String comment = commentStr.toString();
                formatMistakes.add(hightlight(firstWordTypeDocument.get(0), lastWordOnTypeDocument.get(lastWordOnTypeDocument.size()-1), content,comment,pageWidth, pageHeigh,page,String.valueOf(counter.incrementAndGet())));
            }

            List<WordPositionSequence> firstAuthorWord = searcher.findWordsFromAPage(page, words.get(words.size() - 2));
            firstAuthorWord = searcher.filterByGreaterY(firstAuthorWord,firstWordLastRowTittle.get(firstWordLastRowTittle.size()-1).getY());
            List<String> authorWords = searcher.getWordsOfARow(page,firstAuthorWord.get(0).getY());
            List<WordPositionSequence> lastAuthorWord = searcher.findWordsFromAPage(page, authorWords.get(authorWords.size()-1));
            lastAuthorWord = searcher.filterByY(lastAuthorWord,firstAuthorWord.get(0).getY());
            List<String> comments7 = coverMisstakes.isThereAnyMisstakeOnTheAuthor(firstAuthorWord,lastAuthorWord,pageWidth);
            if (comments7.size() != 0){
                StringBuilder contentB = new StringBuilder();
                for (String word: authorWords){
                    contentB.append(word).append(" ");
                }
                String content = contentB.toString();
                StringBuilder commentStr = new StringBuilder("Por favor verifique: ");
                for (int i = 0; i < comments7.size(); i++) {
                    if (i != 0) {
                        commentStr.append(" - ").append(comments7.get(i));
                    } else {
                        commentStr.append(comments7.get(i));
                    }
                }
                commentStr.append(".");
                String comment = commentStr.toString();
                formatMistakes.add(hightlight(firstAuthorWord.get(0), lastAuthorWord.get(lastAuthorWord.size()-1), content,comment,pageWidth, pageHeigh,page,String.valueOf(counter.incrementAndGet())));
            }

        }

        List<WordPositionSequence> wordCochabamba = searcher.findWordsFromAPage(page, "Cochabamba");
        List<WordPositionSequence> wordBolivia = searcher.findWordsFromAPage(page, "Bolivia");
        List<String> comments8 = coverMisstakes.isThereAnyMisstakeOnTheCityCountryOrYear(wordCochabamba,wordBolivia,pageWidth);
        if (comments8.size() != 0){
            String content = "Cochabamba - Bolivia";
            StringBuilder commentStr = new StringBuilder("Por favor verifique: ");
            for (int i = 0; i < comments8.size(); i++) {
                if (i != 0) {
                    commentStr.append(" - ").append(comments8.get(i));
                } else {
                    commentStr.append(comments8.get(i));
                }
            }
            commentStr.append(".");
            String comment = commentStr.toString();
            formatMistakes.add(hightlight(wordCochabamba.get(wordCochabamba.size()-1), wordBolivia.get(wordBolivia.size()-1), content,comment,pageWidth, pageHeigh,page,String.valueOf(counter.incrementAndGet())));
        }

        if (words.size() >= 4){
            List<WordPositionSequence> monthWord = searcher.findWordsFromAPage(page, words.get(words.size() - 1));
            List<String> monthAndYearWords = searcher.getWordsOfARow(page,monthWord.get(monthWord.size()-1).getY());
            List<WordPositionSequence> yearWord = searcher.findWordsFromAPage(page,monthAndYearWords.get(monthAndYearWords.size()-1));
            List<String> comments9 = coverMisstakes.isThereAnyMisstakeOnTheCityCountryOrYear(monthWord,yearWord,pageWidth);
            if (comments9.size() != 0){
                StringBuilder contentB = new StringBuilder();
                for (String word: monthAndYearWords){
                    contentB.append(word).append(" ");
                }
                String content = contentB.toString();
                StringBuilder commentStr = new StringBuilder("Por favor verifique: ");
                for (int i = 0; i < comments9.size(); i++) {
                    if (i != 0) {
                        commentStr.append(" - ").append(comments9.get(i));
                    } else {
                        commentStr.append(comments9.get(i));
                    }
                }
                commentStr.append(".");
                String comment = commentStr.toString();
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

    public void analyzeTableImageTittle(List<WordPositionSequence> imageOrTable, String imageOrTableWord, int page) throws IOException {
        List<String> comments = new ArrayList<>();
        float pageWidth = pdfdocument.getPage(page - 1).getMediaBox().getWidth();
        float pageHeigh = pdfdocument.getPage(page - 1).getMediaBox().getHeight();
        if (imageOrTableWord.contains("Figura")){
            if (!imageOrTableWord.contains(Integer.toString(imageNumber))){
                comments.add("Numeración: debería ser Figura "+Integer.toString(imageNumber));
            }
            imageNumber++;
        }
        if (imageOrTableWord.contains("Tabla")){
            if (!imageOrTableWord.contains(Integer.toString(tableNumber))){
                comments.add("Numeración: debería ser Tabla "+Integer.toString(imageNumber));
            }
            tableNumber++;
        }
        if (!imageOrTable.get(0).getFont().contains("Times") || !imageOrTable.get(0).getFont().contains("New") || !imageOrTable.get(0).getFont().contains("Roman")){
            comments.add("Fuente: Times New Roman");
        }
        if (imageOrTable.get(0).getFontSize() != 12){
            comments.add("Tamaño de la letra: 12 puntos");
        }
        if (!imageOrTable.get(0).getFont().contains("Bold") ){
            comments.add("Negrilla");
        }
        // Formula para ver si esta centrado
        if (Math.abs((pageWidth - imageOrTable.get(0).getEndX()) - imageOrTable.get(0).getX()) >= 20){
            comments.add("Centrado");
        }
        if (comments.size() != 0) {
            StringBuilder commentStr = new StringBuilder("Por favor verifique: ( ");
            for (int i = 0; i < comments.size(); i++) {
                if (i != 0) {
                    commentStr.append(" - ").append(comments.get(i));
                } else {
                    commentStr.append(comments.get(i));
                }
            }
            commentStr.append(" ).");
            String comment = commentStr.toString();
            String content = imageOrTableWord;
            formatMistakes.add(hightlight(imageOrTable.get(0), imageOrTable.get(0), content, comment, pageWidth, pageHeigh, page, String.valueOf(counter.incrementAndGet())));
        }
    }

    public void analyzeTableImage(List<WordPositionSequence> imageOrTable, String imageOrTableWord, int page) throws IOException {
        List<String> comments = new ArrayList<>();
        float pageWidth = pdfdocument.getPage(page - 1).getMediaBox().getWidth();
        float pageHeigh = pdfdocument.getPage(page - 1).getMediaBox().getHeight();
        if (!imageOrTable.get(0).getFont().contains("Times") || !imageOrTable.get(0).getFont().contains("New") || !imageOrTable.get(0).getFont().contains("Roman")){
            comments.add("Fuente: Times New Roman");
        }
        if (imageOrTable.get(0).getFontSize() != 12){
            comments.add("Tamaño de la letra: 12 puntos");
        }
        if (!imageOrTable.get(0).getFont().contains("Bold") ){
            comments.add("Negrilla");
        }
        // Formula para ver si esta centrado
        if (Math.abs((pageWidth - imageOrTable.get(0).getEndX()) - imageOrTable.get(0).getX()) >= 20){
            comments.add("Centrado");
        }
        if (comments.size() != 0) {
            StringBuilder commentStr = new StringBuilder("Por favor verifique: ( ");
            for (int i = 0; i < comments.size(); i++) {
                if (i != 0) {
                    commentStr.append(" - ").append(comments.get(i));
                } else {
                    commentStr.append(comments.get(i));
                }
            }
            commentStr.append(" ).");
            String comment = commentStr.toString();
            String content = imageOrTableWord;
            formatMistakes.add(hightlight(imageOrTable.get(0), imageOrTable.get(0), content, comment, pageWidth, pageHeigh, page, String.valueOf(counter.incrementAndGet())));
        }
    }

    public void analyzeTableImageSource(List<WordPositionSequence> source, String sourceWord, int page) throws IOException {
        float pageWidth = pdfdocument.getPage(page - 1).getMediaBox().getWidth();
        float pageHeigh = pdfdocument.getPage(page - 1).getMediaBox().getHeight();
        for (int index=0;index<source.size();index++) {
            List<String> comments = new ArrayList<>();
            if (!source.get(index).getFont().contains("Times") || !source.get(index).getFont().contains("New") || !source.get(index).getFont().contains("Roman")) {
                comments.add("Fuente: Times New Roman");
            }
            if (source.get(index).getFontSize() != 12) {
                comments.add("Tamaño de la letra: 12 puntos");
            }
            if (source.get(index).getFont().contains("Bold")) {
                comments.add("No negrilla");
            }
            // Formula para ver si esta centrado
            if (Math.abs((pageWidth - source.get(index).getEndX()) - source.get(index).getX()) >= 20) {
                comments.add("Centrado");
            }
            if (comments.size() != 0) {
                StringBuilder commentStr = new StringBuilder("Por favor verifique: ( ");
                for (int i = 0; i < comments.size(); i++) {
                    if (i != 0) {
                        commentStr.append(" - ").append(comments.get(i));
                    } else {
                        commentStr.append(comments.get(i));
                    }
                }
                commentStr.append(" ).");
                String comment = commentStr.toString();
                String content = sourceWord;
                formatMistakes.add(hightlight(source.get(index), source.get(index), content, comment, pageWidth, pageHeigh, page, String.valueOf(counter.incrementAndGet())));
            }
        }
    }


    public int countChar(String str, char c)
    {
        int count = 0;

        for(int i=0; i < str.length(); i++)
        {    if(str.charAt(i) == c)
            count++;
        }

        return count;
    }

    public void analyzeGeneralIndexPage(int page) throws IOException {
        PDFTextStripper pdfStripper = new PDFTextStripper();
        pdfStripper.setStartPage(page);
        pdfStripper.setEndPage(page);
        pdfStripper.setParagraphStart("\n");
        pdfStripper.setSortByPosition(true);
        for (String line : pdfStripper.getText(pdfdocument).split(pdfStripper.getParagraphStart())) {
            String arr[] = line.split(" ", 2);
            if (!arr[0].equals("")) {
                if (line.trim().length() - line.trim().replaceAll(" ", "").length() >= 1) {
                    List<WordPositionSequence> words = searcher.findWordsFromAPage(page, line.trim());
                    if (words.size() == 0) {
                        String wordsToSearch = line.trim();
                        wordsToSearch = Normalizer.normalize(wordsToSearch, Normalizer.Form.NFD);
                        wordsToSearch = wordsToSearch.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
                        words = searcher.findWordsFromAPage(page, wordsToSearch);
                        if (words.size() == 0) {
                            continue;
                        }
                    }
                    if (line.trim().contains("ÍNDICE GENERAL") || line.trim().contains("Índice General") || line.trim().contains("Índice general")) {
                        analyzeGeneralIndex(words, line.trim(), page);
                        continue;
                    }
                    int numberOfPoints = countChar(arr[0], '.');
                    if (numberOfPoints == 0) {
                        analyzeChapter(words, line.trim(), page, arr[0]);
                        continue;
                    }
                    if (numberOfPoints == 1) {
                        analyzeOneDigitChapter(words, line.trim(), page, arr[1]);
                        continue;
                    }
                    if (numberOfPoints == 2) {
                        analyzeTwoDigitSubchapter(words, line.trim(), page, arr[1]);
                        continue;
                    }
                    if (numberOfPoints == 3) {
                        analyzeThreeDigitSection(words, line.trim(), page, arr[1]);
                        continue;
                    }
                    if (numberOfPoints == 4) {
                        analyzeFourDigitSubsection(words, line.trim(), page, arr[1]);
                        continue;
                    }
                }

            }
        }
    }

    public void analyzeGeneralIndex(List<WordPositionSequence> generalIndex, String word, int page){
        float pageWidth = pdfdocument.getPage(page - 1).getMediaBox().getWidth();
        float pageHeigh = pdfdocument.getPage(page - 1).getMediaBox().getHeight();
        List<String> comments = new ArrayList<>();
        if (!generalIndex.get(0).getFont().contains("Times") || !generalIndex.get(0).getFont().contains("New") || !generalIndex.get(0).getFont().contains("Roman")){
            comments.add("Fuente: Times New Roman");
        }
        if (generalIndex.get(0).getFontSize() != 12){
            comments.add("Tamaño de la letra: 12 puntos");
        }
        if (!word.equals("ÍNDICE GENERAL")){
            comments.add("Mayúscula");
        }
        if (!generalIndex.get(0).getFont().contains("Bold") ){
            comments.add("Negrilla");
        }
        // Formula para ver si esta centrado
        if (Math.abs((pageWidth - generalIndex.get(0).getEndX()) - generalIndex.get(0).getX()) >= 20) {
            comments.add("Centrado");
        }
        if (comments.size() != 0) {
            StringBuilder commentStr = new StringBuilder("Por favor verifique: ( ");
            for (int i = 0; i < comments.size(); i++) {
                if (i != 0) {
                    commentStr.append(" - ").append(comments.get(i));
                } else {
                    commentStr.append(comments.get(i));
                }
            }
            commentStr.append(" ).");
            String comment = commentStr.toString();
            String content = word;
            formatMistakes.add(hightlight(generalIndex.get(0), generalIndex.get(0), content, comment, pageWidth, pageHeigh, page, String.valueOf(counter.incrementAndGet())));
        }
    }

    public void analyzeChapter(List<WordPositionSequence> wordPositionSequences, String word, int page,String firstWord){
        if (!firstWord.equals("Anexo") && !firstWord.equals("ANEXO")) {
            float pageWidth = pdfdocument.getPage(page - 1).getMediaBox().getWidth();
            float pageHeigh = pdfdocument.getPage(page - 1).getMediaBox().getHeight();
            List<String> comments = new ArrayList<>();
            if (!wordPositionSequences.get(0).getFont().contains("Times") || !wordPositionSequences.get(0).getFont().contains("New") || !wordPositionSequences.get(0).getFont().contains("Roman")) {
                comments.add("Fuente: Times New Roman");
            }
            if (wordPositionSequences.get(0).getFontSize() != 12) {
                comments.add("Tamaño de la letra: 12 puntos");
            }
            if (!Character.isUpperCase(firstWord.charAt(1))) {
                comments.add("Todo en mayúscula");
            }
            if (!wordPositionSequences.get(0).getFont().contains("Bold")) {
                comments.add("Negrilla");
            }
            if (wordPositionSequences.get(0).getX() < 110) {
                comments.add("Alineado al margen izquierdo");
            }
            if (comments.size() != 0) {
                StringBuilder commentStr = new StringBuilder("Por favor verifique: ( ");
                for (int i = 0; i < comments.size(); i++) {
                    if (i != 0) {
                        commentStr.append(" - ").append(comments.get(i));
                    } else {
                        commentStr.append(comments.get(i));
                    }
                }
                commentStr.append(" ).");
                String comment = commentStr.toString();
                String content = word;
                formatMistakes.add(hightlight(wordPositionSequences.get(0), wordPositionSequences.get(0), content, comment, pageWidth, pageHeigh, page, String.valueOf(counter.incrementAndGet())));
            }
        }
    }

    public void analyzeOneDigitChapter(List<WordPositionSequence> wordPositionSequences, String word, int page, String chpaterWord){
        float pageWidth = pdfdocument.getPage(page - 1).getMediaBox().getWidth();
        float pageHeigh = pdfdocument.getPage(page - 1).getMediaBox().getHeight();
        List<String> comments = new ArrayList<>();
        if (word.contains("INTRODUCCIÓN") || word.contains("INTRODUCCIÓN") || word.contains("INTRODUCCION") || word.contains("Introducción") || word.contains("Introduccion") || word.contains("CONCLUSIONES") || word.contains("Conclusiones")|| word.contains("RECOMENDACIONES") || word.contains("Recomendaciones")|| word.contains("BIBLIOGRAFÍA") || word.contains("BIBLIOGRAFIA") || word.contains("BIBLIOGRAFÍA") || word.contains("Bibliografía") || word.contains("Bibliografia")|| word.contains("ANEXOS") || word.contains("Anexos")){
            comments.add("No tenga numeración");
        }
        if (!wordPositionSequences.get(0).getFont().contains("Times") || !wordPositionSequences.get(0).getFont().contains("New") || !wordPositionSequences.get(0).getFont().contains("Roman")){
            comments.add("Fuente: Times New Roman");
        }
        if (wordPositionSequences.get(0).getFontSize() != 12){
            comments.add("Tamaño de la letra: 12 puntos");
        }
        if (!Character.isUpperCase(chpaterWord.charAt(1))) {
            comments.add("Todo en mayúscula");
        }
        if (!wordPositionSequences.get(0).getFont().contains("Bold") ){
            comments.add("Negrilla");
        }
        if (comments.size() != 0) {
            StringBuilder commentStr = new StringBuilder("Por favor verifique: ( ");
            for (int i = 0; i < comments.size(); i++) {
                if (i != 0) {
                    commentStr.append(" - ").append(comments.get(i));
                } else {
                    commentStr.append(comments.get(i));
                }
            }
            commentStr.append(" ).");
            String comment = commentStr.toString();
            String content = word;
            formatMistakes.add(hightlight(wordPositionSequences.get(0), wordPositionSequences.get(0), content, comment, pageWidth, pageHeigh, page, String.valueOf(counter.incrementAndGet())));
        }
    }

    public void analyzeTwoDigitSubchapter(List<WordPositionSequence> wordPositionSequences, String word, int page, String subchpaterWord){
        float pageWidth = pdfdocument.getPage(page - 1).getMediaBox().getWidth();
        float pageHeigh = pdfdocument.getPage(page - 1).getMediaBox().getHeight();
        List<String> comments = new ArrayList<>();
        if (!wordPositionSequences.get(0).getFont().contains("Times") || !wordPositionSequences.get(0).getFont().contains("New") || !wordPositionSequences.get(0).getFont().contains("Roman")){
            comments.add("Fuente: Times New Roman");
        }
        if (wordPositionSequences.get(0).getFontSize() != 12){
            comments.add("Tamaño de la letra: 12 puntos");
        }
        if (Character.isUpperCase(subchpaterWord.charAt(1))) {
            comments.add("Minúscula");
        }
        if (!wordPositionSequences.get(0).getFont().contains("Bold") ){
            comments.add("Negrilla");
        }
        if (wordPositionSequences.get(0).getX() < 130) {
            comments.add("Tenga una sangría");
        }
        if (comments.size() != 0) {
            StringBuilder commentStr = new StringBuilder("Por favor verifique: ( ");
            for (int i = 0; i < comments.size(); i++) {
                if (i != 0) {
                    commentStr.append(" - ").append(comments.get(i));
                } else {
                    commentStr.append(comments.get(i));
                }
            }
            commentStr.append(" ).");
            String comment = commentStr.toString();
            String content = word;
            formatMistakes.add(hightlight(wordPositionSequences.get(0), wordPositionSequences.get(0), content, comment, pageWidth, pageHeigh, page, String.valueOf(counter.incrementAndGet())));
        }
    }

    public void analyzeThreeDigitSection(List<WordPositionSequence> wordPositionSequences, String word, int page, String sectionWord){
        float pageWidth = pdfdocument.getPage(page - 1).getMediaBox().getWidth();
        float pageHeigh = pdfdocument.getPage(page - 1).getMediaBox().getHeight();
        List<String> comments = new ArrayList<>();
        if (!wordPositionSequences.get(0).getFont().contains("Times") || !wordPositionSequences.get(0).getFont().contains("New") || !wordPositionSequences.get(0).getFont().contains("Roman")){
            comments.add("Fuente: Times New Roman");
        }
        if (wordPositionSequences.get(0).getFontSize() != 12){
            comments.add("Tamaño de la letra: 12 puntos");
        }
        if (Character.isUpperCase(sectionWord.charAt(1))) {
            comments.add("Minúscula");
        }
        if (!wordPositionSequences.get(0).getFont().contains("Bold") ){
            comments.add("Negrilla");
        }
        if (!wordPositionSequences.get(0).getFont().contains("Italic") ){
            comments.add("Cursiva");
        }
        if (wordPositionSequences.get(0).getX() < 165) {
            comments.add("Tenga dos sangrías");
        }
        if (comments.size() != 0) {
            StringBuilder commentStr = new StringBuilder("Por favor verifique: ( ");
            for (int i = 0; i < comments.size(); i++) {
                if (i != 0) {
                    commentStr.append(" - ").append(comments.get(i));
                } else {
                    commentStr.append(comments.get(i));
                }
            }
            commentStr.append(" ).");
            String comment = commentStr.toString();
            String content = word;
            formatMistakes.add(hightlight(wordPositionSequences.get(0), wordPositionSequences.get(0), content, comment, pageWidth, pageHeigh, page, String.valueOf(counter.incrementAndGet())));
        }
    }

    public void analyzeFourDigitSubsection(List<WordPositionSequence> wordPositionSequences, String word, int page, String subsectionWord){
        float pageWidth = pdfdocument.getPage(page - 1).getMediaBox().getWidth();
        float pageHeigh = pdfdocument.getPage(page - 1).getMediaBox().getHeight();
        List<String> comments = new ArrayList<>();
        if (!wordPositionSequences.get(0).getFont().contains("Times") || !wordPositionSequences.get(0).getFont().contains("New") || !wordPositionSequences.get(0).getFont().contains("Roman")){
            comments.add("Fuente: Times New Roman");
        }
        if (wordPositionSequences.get(0).getFontSize() != 12){
            comments.add("Tamaño de la letra: 12 puntos");
        }
        if (Character.isUpperCase(subsectionWord.charAt(1))) {
            comments.add("Minúscula");
        }
        if (wordPositionSequences.get(0).getFont().contains("Bold") ){
            comments.add("No tenga negrilla");
        }
        if (!wordPositionSequences.get(0).getFont().contains("Italic") ){
            comments.add("Cursiva");
        }
        if (wordPositionSequences.get(0).getX() < 200) {
            comments.add("Tenga tres sangrías");
        }
        if (comments.size() != 0) {
            StringBuilder commentStr = new StringBuilder("Por favor verifique: ( ");
            for (int i = 0; i < comments.size(); i++) {
                if (i != 0) {
                    commentStr.append(" - ").append(comments.get(i));
                } else {
                    commentStr.append(comments.get(i));
                }
            }
            commentStr.append(" ).");
            String comment = commentStr.toString();
            String content = word;
            formatMistakes.add(hightlight(wordPositionSequences.get(0), wordPositionSequences.get(0), content, comment, pageWidth, pageHeigh, page, String.valueOf(counter.incrementAndGet())));
        }
    }


}
