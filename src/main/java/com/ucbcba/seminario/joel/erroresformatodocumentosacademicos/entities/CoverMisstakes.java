package com.ucbcba.seminario.joel.erroresformatodocumentosacademicos.entities;

import java.util.List;

public class CoverMisstakes {


    public boolean isThereAnyMisstakeOnLineOne(List<WordPositionSequence> wordUniversidad, List<WordPositionSequence> wordPablo){
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
            if (wordUniversidad.get(0).getFontSize() != 18){
                return true;
            }
            if (!wordUniversidad.get(0).getFont().contains("Bold") ){
                return true;
            }

        }
        return false;
    }

    public boolean isThereAnyMisstakeOnLineTwo(List<WordPositionSequence> wordUnidad, List<WordPositionSequence> wordCbba, float pageWidth){
        if (wordUnidad.size() >= 1 && wordCbba.size() >= 1 ){
            if (!wordUnidad.get(0).getFont().contains("Times") || !wordUnidad.get(0).getFont().contains("New") || !wordUnidad.get(0).getFont().contains("Roman")){
                return true;
            }
            if (wordUnidad.get(0).getFontSize() != 16){
                return true;
            }
            if (!wordUnidad.get(0).getFont().contains("Bold") ){
                return true;
            }
            if (!wordCbba.get(0).getFont().contains("Times") || !wordCbba.get(0).getFont().contains("New") || !wordCbba.get(0).getFont().contains("Roman")){
                return true;
            }
            if (wordCbba.get(0).getFontSize() != 16){
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

    public boolean isThereAnyMisstakeOnLineTreeOrFour(List<WordPositionSequence> wordDepartamento, List<WordPositionSequence> lastWordOnDepartamentoRow, float pageWidth){
        if (wordDepartamento.size() >= 1 && lastWordOnDepartamentoRow.size() >= 1 ){
            int index = lastWordOnDepartamentoRow.size()-1;
            if (!wordDepartamento.get(0).getFont().contains("Times") || !wordDepartamento.get(0).getFont().contains("New") || !wordDepartamento.get(0).getFont().contains("Roman")){
                return true;
            }
            if (wordDepartamento.get(0).getFontSize() != 14){
                return true;
            }
            if (!wordDepartamento.get(0).getFont().contains("Bold") ){
                return true;
            }
            if (!lastWordOnDepartamentoRow.get(index).getFont().contains("Times") || !lastWordOnDepartamentoRow.get(index).getFont().contains("New") || !lastWordOnDepartamentoRow.get(index).getFont().contains("Roman")){
                return true;
            }
            if (lastWordOnDepartamentoRow.get(index).getFontSize() != 14){
                return true;
            }
            if (!lastWordOnDepartamentoRow.get(index).getFont().contains("Bold") ){
                return true;
            }
            // Formula para ver si esta centrado
            if (Math.abs((pageWidth - lastWordOnDepartamentoRow.get(index).getEndX()) - wordDepartamento.get(0).getX()) >= 20){
                return true;
            }
        }
        return false;
    }
}
