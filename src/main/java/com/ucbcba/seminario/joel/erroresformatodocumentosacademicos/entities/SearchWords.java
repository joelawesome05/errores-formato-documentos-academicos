package com.ucbcba.seminario.joel.erroresformatodocumentosacademicos.entities;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class SearchWords {
    private PDDocument pdfdocument;

    public SearchWords(PDDocument pdfdocument){
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

    public boolean isTheCoverInThisPage(int page) throws IOException {
        boolean bool1,bool2, bool3, bool4, bool5;
        bool1 = isTheWordInThePage(page,"“SAN PABLO”");
        bool2 = isTheWordInThePage(page,"REGIONAL");
        bool3 = isTheWordInThePage(page,"Departamento");
        bool4 = isTheWordInThePage(page,"Carrera");
        bool5 = isTheWordInThePage(page,"Bolivia");
        return getNumberOfTrues(bool1,bool2,bool3,bool4,bool5) >= 3;
    }

    public List<String> getWordsOfARow(int page, float yReference) throws IOException {
        final List<String> wordsFound = new ArrayList<String>();
        PDFTextStripper stripper = new PDFTextStripper() {
            @Override
            protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
                String wordSeparator = getWordSeparator();
                List<TextPosition> word = new ArrayList<>();
                for (TextPosition text : textPositions) {
                    String thisChar = text.getUnicode();
                    if (thisChar != null) {
                        if (thisChar.length() >= 1) {
                            if (!thisChar.equals(wordSeparator)) {
                                word.add(text);
                            } else if (!word.isEmpty()) {
                                if (word.get(0).getYDirAdj() == yReference) {
                                    StringBuilder builder = new StringBuilder();
                                    for (TextPosition textt : word) {
                                        builder.append(textt.getUnicode());
                                    }
                                    wordsFound.add(builder.toString());
                                }
                                word.clear();
                            }
                        }
                    }
                }
                if (!word.isEmpty()) {
                    if (word.get(0).getYDirAdj() == yReference) {
                        StringBuilder builder = new StringBuilder();
                        for (TextPosition textt : word) {
                            builder.append(textt.getUnicode());
                        }
                        wordsFound.add(builder.toString());
                    }
                    word.clear();
                }

            }
        };
        stripper.setSortByPosition(true);
        stripper.setStartPage(page);
        stripper.setEndPage(page);
        Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
        stripper.writeText(pdfdocument, dummy);
        return wordsFound;
    }

    public List<WordPositionSequence> filterByY(List<WordPositionSequence> words, float y){
        List<WordPositionSequence> resp = new ArrayList<WordPositionSequence>();
        for (WordPositionSequence word : words) {
            if (word.getY() == y){
                resp.add(word);
            }
        }
        return resp;
    }


}
