package es.codeurjc.nexusapp.utilities.dtos;

import lombok.Getter;

@Getter
public class JsonString {
    private String text;

    @Override
    public String toString(){
        return text;
    }
}
