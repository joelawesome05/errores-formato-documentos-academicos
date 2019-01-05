package com.ucbcba.seminario.joel.erroresformatodocumentosacademicos.entities;

import java.util.ArrayList;
import java.util.List;

public class CoverMisstakes {



    public List<String> isThereAnyMisstakeOnLineOne(List<WordPositionSequence> wordUniversidad, List<WordPositionSequence> wordPablo){
        List<String> comments = new ArrayList<>();
        if (wordUniversidad.size() >= 1 && wordPablo.size() >= 1 ){
            if (!wordUniversidad.get(0).getFont().contains("Times") || !wordUniversidad.get(0).getFont().contains("New") || !wordUniversidad.get(0).getFont().contains("Roman") || !wordUniversidad.get(0).getFont().contains("Times") || !wordUniversidad.get(0).getFont().contains("New") || !wordUniversidad.get(0).getFont().contains("Roman")){
                comments.add("Fuente: Times New Roman");
            }
            if (wordUniversidad.get(0).getFontSize() != 18 || wordUniversidad.get(0).getFontSize() != 18){
                comments.add("Tamaño de la letra: 18 puntos");
            }
            if (!wordUniversidad.get(0).getFont().contains("Bold") || !wordUniversidad.get(0).getFont().contains("Bold")){
                comments.add("Negrilla");
            }
        }
        return comments;
    }

    public List<String> isThereAnyMisstakeOnLineTwo(List<WordPositionSequence> wordUnidad, List<WordPositionSequence> wordCbba, float pageWidth){
        List<String> comments = new ArrayList<>();
        if (wordUnidad.size() >= 1 && wordCbba.size() >= 1 ){
            if (!wordUnidad.get(0).getFont().contains("Times") || !wordUnidad.get(0).getFont().contains("New") || !wordUnidad.get(0).getFont().contains("Roman") || !wordCbba.get(0).getFont().contains("Times") || !wordCbba.get(0).getFont().contains("New") || !wordCbba.get(0).getFont().contains("Roman")){
                comments.add("Fuente: Times New Roman");
            }
            if (wordUnidad.get(0).getFontSize() != 16 || wordCbba.get(0).getFontSize() != 16){
                comments.add("Tamaño de la letra: 16 puntos");
            }
            if (!wordUnidad.get(0).getFont().contains("Bold") || !wordCbba.get(0).getFont().contains("Bold")){
                comments.add("Negrilla");
            }
            // Formula para ver si esta centrado
            if (Math.abs((pageWidth - wordCbba.get(0).getEndX()) - wordUnidad.get(0).getX()) >= 20){
                comments.add("Centrado");
            }
        }
        return comments;
    }

    public List<String> isThereAnyMisstakeOnLineTreeOrFour(List<WordPositionSequence> wordDepartamento, List<WordPositionSequence> lastWordOnDepartamentoRow, float pageWidth){
        List<String> comments = new ArrayList<>();
        if (wordDepartamento.size() >= 1 && lastWordOnDepartamentoRow.size() >= 1 ){
            int index = lastWordOnDepartamentoRow.size()-1;
            if (!wordDepartamento.get(0).getFont().contains("Times") || !wordDepartamento.get(0).getFont().contains("New") || !wordDepartamento.get(0).getFont().contains("Roman") || !lastWordOnDepartamentoRow.get(index).getFont().contains("Times") || !lastWordOnDepartamentoRow.get(index).getFont().contains("New") || !lastWordOnDepartamentoRow.get(index).getFont().contains("Roman")){
                comments.add("Fuente: Times New Roman");
            }
            if (wordDepartamento.get(0).getFontSize() != 14 || lastWordOnDepartamentoRow.get(index).getFontSize() != 14){
                comments.add("Tamaño de la letra: 14 puntos");
            }
            if (!wordDepartamento.get(0).getFont().contains("Bold") || !lastWordOnDepartamentoRow.get(index).getFont().contains("Bold") ){
                comments.add("Negrilla");
            }
            // Formula para ver si esta centrado
            if (Math.abs((pageWidth - lastWordOnDepartamentoRow.get(index).getEndX()) - wordDepartamento.get(0).getX()) >= 20){
                comments.add("Centrado");
            }
        }
        return comments;
    }

    public List<String> isThereAnyMisstakeOnTheTittle(List<WordPositionSequence> firstWord, List<WordPositionSequence> lastWord, float pageWidth) {
        List<String> comments = new ArrayList<>();
        if (firstWord.size() >= 1 && lastWord.size() >= 1 ){
            int index = lastWord.size()-1;
            if (!firstWord.get(0).getFont().contains("Times") || !firstWord.get(0).getFont().contains("New") || !firstWord.get(0).getFont().contains("Roman") || !lastWord.get(index).getFont().contains("Times") || !lastWord.get(index).getFont().contains("New") || !lastWord.get(index).getFont().contains("Roman")){
                comments.add("Fuente: Times New Roman");
            }
            if (firstWord.get(0).getFontSize() != 16 || lastWord.get(index).getFontSize() != 16){
                comments.add("Tamaño de la letra: 16 puntos");
            }
            if (!firstWord.get(0).getFont().contains("Bold") || !lastWord.get(index).getFont().contains("Bold")){
                comments.add("Negrilla");
            }
            // Formula para ver si esta centrado
            if (Math.abs((pageWidth - lastWord.get(index).getEndX()) - firstWord.get(0).getX()) >= 20){
                comments.add("Centrado");
            }
        }
        return comments;
    }

    public List<String> isThereAnyMisstakeOnTheTypeDocument(List<WordPositionSequence> firstWord, List<WordPositionSequence> lastWord, float pageWidth){
        List<String> comments = new ArrayList<>();
        if (firstWord.size() >= 1 && lastWord.size() >= 1 ){
            int index1 = 0;
            int index2 = lastWord.size()-1;
            if (!firstWord.get(index1).getFont().contains("Times") || !firstWord.get(index1).getFont().contains("New") || !firstWord.get(index1).getFont().contains("Roman") || !lastWord.get(index2).getFont().contains("Times") || !lastWord.get(index2).getFont().contains("New") || !lastWord.get(index2).getFont().contains("Roman")){
                comments.add("Fuente: Times New Roman");
            }
            if (firstWord.get(index1).getFontSize() != 12 || lastWord.get(index2).getFontSize() != 12){
                comments.add("Tamaño de la letra: 12 puntos");
            }
            if (!firstWord.get(index1).getFont().contains("Italic") || !lastWord.get(index2).getFont().contains("Italic") ){
                comments.add("Cursiva");
            }
            if (firstWord.get(index1).getFont().contains("Bold") || lastWord.get(index2).getFont().contains("Bold")){
                comments.add("No tenga negrilla");
            }
            // Formula para ver si esta centrado
            if (Math.abs((pageWidth - lastWord.get(index2).getEndX()) - firstWord.get(index1).getX()) <= 20 || lastWord.get(index2).getEndX() < 500){
                comments.add("Alineado al margen derecho");
            }
        }
        return comments;
    }

    public List<String> isThereAnyMisstakeOnTheAuthor(List<WordPositionSequence> firstWord, List<WordPositionSequence> lastWord, float pageWidth){
        List<String> comments = new ArrayList<>();
        if (firstWord.size() >= 1 && lastWord.size() >= 1 ){
            int index = lastWord.size()-1;
            if (!firstWord.get(0).getFont().contains("Times") || !firstWord.get(0).getFont().contains("New") || !firstWord.get(0).getFont().contains("Roman") || !lastWord.get(index).getFont().contains("Times") || !lastWord.get(index).getFont().contains("New") || !lastWord.get(index).getFont().contains("Roman")){
                comments.add("Fuente: Times New Roman");
            }
            if (firstWord.get(0).getFontSize() != 14 || lastWord.get(index).getFontSize() != 14){
                comments.add("Tamaño de la letra: 14 puntos");
            }
            if (!firstWord.get(0).getFont().contains("Bold") || !lastWord.get(index).getFont().contains("Bold") ){
                comments.add("Negrilla");
            }
            // Formula para ver si esta centrado
            if (Math.abs((pageWidth - lastWord.get(index).getEndX()) - firstWord.get(0).getX()) >= 20){
                comments.add("Centrado");
            }
        }
        return comments;
    }

    public List<String> isThereAnyMisstakeOnTheCityCountryOrYear(List<WordPositionSequence> firstWord, List<WordPositionSequence> lastWord, float pageWidth){
        List<String> comments = new ArrayList<>();
        if (firstWord.size() >= 1 && lastWord.size() >= 1 ){
            int index1 = firstWord.size()-1;
            int index2 = lastWord.size()-1;
            if (!firstWord.get(index1).getFont().contains("Times") || !firstWord.get(index1).getFont().contains("New") || !firstWord.get(index1).getFont().contains("Roman") || !lastWord.get(index2).getFont().contains("Times") || !lastWord.get(index2).getFont().contains("New") || !lastWord.get(index2).getFont().contains("Roman")){
                comments.add("Fuente: Times New Roman");
            }
            if (firstWord.get(index1).getFontSize() != 12 || lastWord.get(index2).getFontSize() != 12){
                comments.add("Tamaño de la letra: 12 puntos");
            }
            if (firstWord.get(index1).getFont().contains("Bold") || lastWord.get(index2).getFont().contains("Bold") ){
                comments.add("No tenga negrilla");
            }
            // Formula para ver si esta centrado
            if (Math.abs((pageWidth - lastWord.get(index2).getEndX()) - firstWord.get(index1).getX()) >= 20){
                comments.add("Centrado");
            }
        }
        return comments;
    }

}
