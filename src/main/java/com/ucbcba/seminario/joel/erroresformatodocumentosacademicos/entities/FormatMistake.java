package com.ucbcba.seminario.joel.erroresformatodocumentosacademicos.entities;


public class FormatMistake {

    private Content content;
    private Position position;
    private Comment comment;
    private final long id;

    public FormatMistake(Content content, Position position, Comment comment, long id) {
        this.content = content;
        this.position = position;
        this.comment = comment;
        this.id = id;
    }


    public long getId() {
        return id;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
